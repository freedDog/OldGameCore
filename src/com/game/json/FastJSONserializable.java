package com.game.json;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;


public class FastJSONserializable {
	private static Logger logger=LoggerFactory.getLogger(FastJSONserializable.class);
	public static String toString(Object obj) {
		try {
			if((obj instanceof List) || (obj instanceof Map)) {
				return JSON.toJSONString(obj, new SerializerFeature[] {SerializerFeature.WriteClassName});
			}else if(obj instanceof Collection) {
				throw new Exception("It is not allowed to serialize a Collection other than the Map");
			}
			return JSON.toJSONString(obj);
		}catch(Exception e) {
			logger.error(e.getMessage());
		}
		return null;
	}
	public static String toStringWithClassName(Object obj) {
		try {
			if((obj instanceof List)||(obj instanceof Map)) {
				return JSON.toJSONString(obj, new SerializerFeature[] {SerializerFeature.WriteClassName});
			}else if(obj instanceof Collection) {
				throw new Exception("It is not allowed to serialize a Collection other than the Map");
			}
			return JSON.toJSONString(obj, new SerializerFeature[] {SerializerFeature.WriteClassName});
		}catch(Exception e) {
			logger.error(e.getMessage());
		}
		return null;
	}
	public static <T> T toObject(String data,Class<T> clazz) {
		try {
			return JSON.parseObject(data, clazz);
		}catch(Exception e) {
			logger.error(e.getMessage()+data);
		}
		return null;
	}
	public static <T> List<T> toList(String data,Class<T> clazz){
		try {
			return JSON.parseArray(data, clazz);
		}catch(Exception e) {
			logger.error(e.getMessage()+data);
		}
		return null;
	}
}
