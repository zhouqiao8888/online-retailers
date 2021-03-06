package com.mmall.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alipay.api.AlipayResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.GoodsDetail;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
import com.alipay.demo.trade.utils.ZxingUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.dao.OrderItemMapper;
import com.mmall.dao.OrderMapper;
import com.mmall.dao.PayInfoMapper;
import com.mmall.pojo.Order;
import com.mmall.pojo.OrderItem;
import com.mmall.pojo.PayInfo;
import com.mmall.service.IPaymentService;
import com.mmall.util.BigDecimalUtil;
import com.mmall.util.DateTimeUtil;
import com.mmall.util.FTPUtil;
import com.mmall.util.PropertiesUtil;

@Service("iPaymentService")
public class PaymentServiceImpl implements IPaymentService {

	private static final Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class); 
	
	@Autowired
	private OrderMapper orderMapper;
	
	@Autowired
	private OrderItemMapper orderItemMapper;
	
	@Autowired
	private PayInfoMapper payInfoMapper;
	
	public ServerResponse<Map<String, String>> payOrder(Long orderNo, Integer userId, String path) {
		Map<String, String> resMap = Maps.newHashMap();
		resMap.put("orderNo", orderNo.toString());

		Order order = orderMapper.selectByUserIdAndOrderNo(userId, orderNo);
		if(order == null) {
			return ServerResponse.createByErrorMsg("未找到当前用户的订单");
		}		
		
		if(order.getStatus() != Const.OrderStatusEnum.NO_PAY.getCode()) {
			return ServerResponse.createByErrorMsg("订单已支付或者已取消");
		}
		
		 // (必填) 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线，
        // 需保证商户系统端不能重复，建议通过数据库sequence生成，
        String outTradeNo = order.getOrderNo().toString();

        // (必填) 订单标题，粗略描述用户的支付目的。如“xxx品牌xxx门店当面付扫码消费”
        String subject = new StringBuilder().append("happymmall扫码支付，订单号：").append(outTradeNo).toString();


        // (必填) 订单总金额，单位为元，不能超过1亿元
        // 如果同时传入了【打折金额】,【不可打折金额】,【订单总金额】三者,则必须满足如下条件:【订单总金额】=【打折金额】+【不可打折金额】
        String totalAmount = order.getPayment().toString();

        // (可选) 订单不可打折金额，可以配合商家平台配置折扣活动，如果酒水不参与打折，则将对应金额填写至此字段
        // 如果该值未传入,但传入了【订单总金额】,【打折金额】,则该值默认为【订单总金额】-【打折金额】
        String undiscountableAmount = "0";

        // 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)
        // 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
        String sellerId = "";

        // 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品2件共15.00元"
        String body = new StringBuilder().append("商品订单号为").append(outTradeNo).append("，共").append(totalAmount).append("元").toString();

        // 商户操作员编号，添加此参数可以为商户操作员做销售统计
        String operatorId = "test_operator_id";

        // (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
        String storeId = "test_store_id";

        // 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
        ExtendParams extendParams = new ExtendParams();
        extendParams.setSysServiceProviderId("2088100200300400500");

        // 支付超时，定义为120分钟
        String timeoutExpress = "120m";

        // 商品明细列表，需填写购买商品详细信息，
        List<GoodsDetail> goodsDetailList = new ArrayList<GoodsDetail>();
        List<OrderItem> orderItemList = orderItemMapper.selectOrderItemsByUserIdAndOrderNo(userId, orderNo);
        for(OrderItem orderItem : orderItemList) {
        	GoodsDetail goods1 = GoodsDetail.newInstance(orderItem.getProductId().toString(), orderItem.getProductName(), 
        			BigDecimalUtil.multiply(orderItem.getCurrentUnitPrice().doubleValue(), Double.valueOf(100)).longValue(), orderItem.getQuantity());
        	goodsDetailList.add(goods1);
        }
        

        // 创建扫码支付请求builder，设置请求参数
        AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder()
            .setSubject(subject).setTotalAmount(totalAmount).setOutTradeNo(outTradeNo)
            .setUndiscountableAmount(undiscountableAmount).setSellerId(sellerId).setBody(body)
            .setOperatorId(operatorId).setStoreId(storeId).setExtendParams(extendParams)
            .setTimeoutExpress(timeoutExpress)
            	.setNotifyUrl(PropertiesUtil.getProperty("alipay.callback.url"))//支付宝服务器主动通知商户服务器里指定的页面http路径,根据需要设置
            .setGoodsDetailList(goodsDetailList);

        /** 一定要在创建AlipayTradeService之前调用Configs.init()设置默认参数
         *  Configs会读取classpath下的zfbinfo.properties文件配置信息，如果找不到该文件则确认该文件是否在classpath目录
         */
        Configs.init("zfbinfo.properties");

        /** 使用Configs提供的默认参数
         *  AlipayTradeService可以使用单例或者为静态成员对象，不需要反复new
         */
        AlipayTradeService tradeService = new AlipayTradeServiceImpl.ClientBuilder().build();
        
        AlipayF2FPrecreateResult result = tradeService.tradePrecreate(builder);
        switch (result.getTradeStatus()) {
            case SUCCESS:
                logger.info("支付宝预下单成功: )");

                AlipayTradePrecreateResponse response = result.getResponse();
                dumpResponse(response);
                
                //判断文件目录是否存在
                File fileDir = new File(path);
                if(!fileDir.exists()) {
                	fileDir.setWritable(true);
                	fileDir.mkdirs();
                }

                // 将二维码图片保存到本地路径
                String qrFilePath = String.format(path + "/qr-%s.png", response.getOutTradeNo());
                File qrFile = ZxingUtils.getQRCodeImge(response.getQrCode(), 256, qrFilePath);
                logger.info("filePath:" + qrFilePath);
                
                //将二维码图片上传到ftp服务器
//                File targetFile = new File(path, qrFileName);
				try {
					boolean uploadFlag = FTPUtil.uploadFile(Lists.newArrayList(qrFile));
					if(!uploadFlag) {
						return ServerResponse.createByErrorMsg("文件上传到ftp服务器失败");
					}
					qrFile.delete();
				} catch (IOException e) {
					logger.error("二维码图片上传失败", e.getMessage());
				}
				
                String qrFileName = String.format("qr-%s.png", response.getOutTradeNo());
				resMap.put("url", PropertiesUtil.getProperty("ftp.server.http.prefix") + qrFileName);
                return ServerResponse.createBySuccessMsgAndData("订单预下单成功", resMap);		
				
            case FAILED:
                logger.error("支付宝预下单失败!!!");
                return ServerResponse.createByErrorMsg("支付宝预下单失败!!!");

            case UNKNOWN:
                logger.error("系统异常，预下单状态未知!!!");
                return ServerResponse.createByErrorMsg("系统异常，预下单状态未知!!!");

            default:
                logger.error("不支持的交易状态，交易返回异常!!!");
                return ServerResponse.createByErrorMsg("不支持的交易状态，交易返回异常!!!");
        }
	}
	
	public ServerResponse<String> aliPayCallback(Map<String, String> params) {
		String tradeNo = params.get("trade_no");
		String tradeStatus = params.get("trade_status");
		String payment = params.get("gmt_payment");
		Long orderNo = Long.parseLong(params.get("out_trade_no"));
		
		Order order = orderMapper.selectByOrderNo(orderNo);
		if(order == null) {
			logger.info("不是本商城的订单", orderNo);
			return ServerResponse.createByErrorMsg("不是本商城的订单");
		}
		
		//判断订单的状态码,判断订单是否已完成
		if(order.getStatus() >= Const.OrderStatusEnum.PAY.getCode()) {
			logger.info("重复的支付宝回调", order.getStatus());
			return ServerResponse.createByErrorMsg("重复的支付宝回调");
		}
		
		//若订单未完成，且回调状态显示已完成，更新订单状态
		if(Const.AlipayCallbackStatus.TRADE_SUCCESS.equals(tradeStatus)) {
			order.setStatus(Const.OrderStatusEnum.PAY.getCode());
			order.setPaymentTime(DateTimeUtil.strToDate(payment));
			int resCount = orderMapper.updateByPrimaryKeySelective(order);
			if(resCount == 0) {
				logger.info("订单更新失败");
				return ServerResponse.createByErrorMsg("订单更新失败");
			}
		}
		
		//插入支付信息
		PayInfo payInfo = new PayInfo();
		payInfo.setOrderNo(orderNo);
		payInfo.setUserId(order.getUserId());
		payInfo.setPayPlatform(Const.PayPlatformEnum.ALIPAY.getCode());
		payInfo.setPlatformNumber(tradeNo);
		payInfo.setPlatformStatus(tradeStatus);
		
		int resCount = payInfoMapper.insert(payInfo);
		if(resCount == 0) {
			logger.info("支付信息持久化失败");
			return ServerResponse.createByErrorMsg("支付信息持久化失败");
		}
		
		logger.info("支付宝回调成功");
		return ServerResponse.createBySuccessMsg("支付宝回调成功");
	}
	
	public ServerResponse<Boolean> queryOrderStatus(Long orderNo, Integer userId) {
		Order order = orderMapper.selectByUserIdAndOrderNo(userId, orderNo);
		if(order == null || order.getStatus() < Const.OrderStatusEnum.PAY.getCode()) {
			return ServerResponse.createBySuccessData(false);
		}
		return ServerResponse.createBySuccessData(true);
	}
	
	 // 简单打印应答
    private void dumpResponse(AlipayResponse response) {
        if (response != null) {
            logger.info(String.format("code:%s, msg:%s", response.getCode(), response.getMsg()));
            if (StringUtils.isNotEmpty(response.getSubCode())) {
                logger.info(String.format("subCode:%s, subMsg:%s", response.getSubCode(),
                    response.getSubMsg()));
            }
            logger.info("body:" + response.getBody());
        }
    }
}
