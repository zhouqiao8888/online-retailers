package com.mmall.service;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.collect.Lists;
import com.mmall.util.FTPUtil;

@Service("iFileService")
public class FileServiceImpl implements IFileService {
	
	private static final Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);
	
	/**
	 * 用户上传文件->tomcat upload文件夹下->ftp服务器
	 */
	public String upload(MultipartFile file, String path) {
		//为了避免多个用户上传相同的文件名造成文件覆盖，重新构造上传的文件名
		String fileName = file.getOriginalFilename();
		String fileExtension = fileName.substring(fileName.lastIndexOf("."));
		String fileUploadName = UUID.randomUUID().toString() + fileExtension;
		logger.info("开始上传文件,上传的文件名为{},上传的路径为{},上传的新文件名为{}", fileName, path, fileUploadName);
		
		//构造路径所在的目录
		File fileDir = new File(path);
		if(!fileDir.exists()) {
			fileDir.setWritable(true);
			fileDir.mkdirs();
		}
		
		//构造上传文件
		File fileUpload = new File(path, fileUploadName);
		try {
			//将源文件内容拷贝到上传文件	
			file.transferTo(fileUpload);
			
			//将文件上传到ftp服务器
			boolean ftpUploadFlag = FTPUtil.uploadFile(Lists.newArrayList(fileUpload));
			if(!ftpUploadFlag) {
				return null;
			}
			
			//文件上传到ftp服务器后，删除upload文件夹下的文件
			fileUpload.delete();
			
		} catch (IllegalStateException | IOException e) {
			logger.error("文件上传错误", e);
			return null;
		}		
		return fileUploadName;
	}
	
//	public static void main(String[] args) {
//		String fileName = "abc.jpg";
//		System.out.println(fileName.substring(fileName.lastIndexOf(".") + 1));
//		logger.info("开始上传文件,上传的文件名为{},上传的路径为{},上传的新文件名为{}", 1, 2, 3);
//	}
}
