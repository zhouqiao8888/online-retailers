package com.mmall.common;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class TokenCache {
	
	private static Logger logger = LoggerFactory.getLogger(TokenCache.class);
	
	public static final String TOKENCACHE = "token_"; 
	
	private static LoadingCache<String, String> localCache = 
			CacheBuilder.newBuilder().initialCapacity(1000).maximumSize(10000).expireAfterAccess(12, TimeUnit.HOURS)
			.build(new CacheLoader<String, String>() {
				//当key没有命中时，默认返回"null"
				@Override
				public String load(String arg0) throws Exception {
//					return "null";
					return null;
				}
				
			});
	
	public static void setKey(String key, String value) {
		localCache.put(key, value);
	}
	
	public static String getValue(String key) {
		String value = null;
		try {
			value = localCache.get(key);
//			if(value.equals("null"))
//				return null;
		} catch (ExecutionException e) {
			logger.error("local cache get error", e);
		}
		return value;

	}
			

}
