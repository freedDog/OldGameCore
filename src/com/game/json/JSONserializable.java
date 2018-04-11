package com.game.json;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.ezmorph.bean.MorphDynaBean;
import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import net.sf.json.JsonConfig;
import net.sf.json.util.NewBeanInstanceStrategy;
import net.sf.json.util.PropertyFilter;
import net.sf.json.util.PropertySetStrategy;

public class JSONserializable {
	private static Logger logger=LoggerFactory.getLogger(JSONserializable.class);
	private static HashMap<String,HashMap<String,Field>> fields=new HashMap<>();
	private static JsonConfig serizlizableConfig=new JsonConfig();
	private static JsonConfig unserizlizableConfig;
	
	static {
		serizlizableConfig.setIgnorePublicFields(true);
		serizlizableConfig.setJsonPropertyFilter(new PropertyFilter() {
			@Override
			public boolean apply(Object soure, String name, Object value) {
				try {
					Field field=JSONserializable.getDeclaredField(soure.getClass(), name);
					if(field!=null&&Modifier.isTransient(field.getModifiers())) {
						return true;
					}
				}catch(Exception e) {
					logger.error(e.getMessage());
				}
				return false;
			}
		});
		unserizlizableConfig=new JsonConfig();
		unserizlizableConfig.setNewBeanInstanceStrategy(new NewBeanInstanceStrategy() {
			@Override
			public Object newInstance(Class clazz, JSONObject obj) throws InstantiationException, IllegalAccessException,
					SecurityException, NoSuchMethodException, InvocationTargetException {
				if(Modifier.isAbstract(clazz.getModifiers())) {
					try {
						return Class.forName(obj.getString("clazz")).newInstance();
					}catch (Exception e) {
						logger.error(e.getMessage());
					}
				}
				return clazz.newInstance();
			}
		});
		unserizlizableConfig.setPropertySetStrategy(new PropertySetStrategy() {
			@Override
			public void setProperty(Object bean, String key, Object value)  {
				if(!(bean instanceof List)&&!(bean instanceof Map)
						&&!(bean instanceof Set)
						&&(null==JSONserializable.getDeclaredField(bean.getClass(),key))) {
					return;
				}
				if(value!=null) {
					if(MorphDynaBean.class.isAssignableFrom(value.getClass())) {
						MorphDynaBean _bean=(MorphDynaBean)value;
						try {
							_bean.get("clazz");	
						}catch(Exception e) {
							return;
						}
						try {
							Class clazz=null;
							try {
								clazz=Class.forName((String)_bean.get("clazz"));
							} catch (ClassNotFoundException e) {
								e.printStackTrace();
							}
							if(null==clazz) {
								return;
							}
							JsonConfig jsonConfig=unserizlizableConfig.copy();
							jsonConfig.setRootClass(clazz);
							value=JSONObject.toBean((JSONObject)JSONSerializer.toJSON(_bean,serizlizableConfig),jsonConfig);
						}catch (Exception e) {
							
						}
					}
				}
				if(value!=null&&(value instanceof List)) {
					List list=(List)value;
					if(list.size()==0) {
						return;
					}
					Object obj=list.get(0);
					if(MorphDynaBean.class.isAssignableFrom(obj.getClass())) {
						List temp=null;
						try {
							temp=list.getClass().newInstance();
						} catch (InstantiationException e) {
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						}
						if(null==temp) {
							return;
						}
						for(int i=0;i<list.size();i++) {
							MorphDynaBean _bean=(MorphDynaBean)list.get(i);
							_bean.get("clazz");
							Class clazz=null;
							try {
								clazz=Class.forName((String)_bean.get("clazz"));
							} catch (ClassNotFoundException e) {
								e.printStackTrace();
							}
							if(null==clazz) {
								return;
							}
							JsonConfig jsonConfig=unserizlizableConfig.copy();
							jsonConfig.setRootClass(clazz);
							temp.add(JSONObject.toBean((JSONObject)JSONSerializer.toJSON(_bean,serizlizableConfig),jsonConfig));
						}
						value=temp;
					}
					
				}
				if((bean instanceof Map)) {
					((Map)bean).put(key, value);
				}else {
					try {
						PropertyUtils.setSimpleProperty(bean, key, value);
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					} catch (NoSuchMethodException e) {
						e.printStackTrace();
					}
				}
			}
		});
	}
	public static String toString(Object obj) {
		try {
			Object object=JSONSerializer.toJSON(obj, serizlizableConfig);
			return object.toString();
		}catch(Exception e) {
			logger.error(e.getMessage(),JSONSerializer.toJSON(obj, serizlizableConfig));
		}
		return null;
	}
	public static Object toObject(String data,Class<?> clazz) {
		try {
			JsonConfig jsonConfig=unserizlizableConfig.copy();
			jsonConfig.setRootClass(clazz);
			JSONObject object=JSONObject.fromObject(data);
			return JSONObject.toBean(object, jsonConfig);
		}catch(Exception e) {
			logger.error(e.getMessage(), data);
		}
		return null;
	}
	public static Object toList(String data,Class<?> clazz) {
		try {
			JsonConfig jsonConfig=unserizlizableConfig.copy();
			jsonConfig.setRootClass(clazz);
			JSONArray object=JSONArray.fromObject(data);
			return JSONArray.toCollection(object, jsonConfig);
		}catch(Exception e) {
			logger.error(e.getMessage(),data);
		}
		return null;
	}
	private static Field getDeclaredField(Class<?> clazz,String name) {
		if(fields.containsKey(clazz.getName())) {
			return fields.get(clazz.getName()).get(name);
		}
		Class<?> _clazz=clazz;
		HashMap<String, Field> fieldMap=new HashMap<>();
		while(_clazz!=null) {
			Field[] _fields=_clazz.getDeclaredFields();
			for(int i=0;i<_fields.length;i++) {
				if(!fieldMap.containsKey(_fields[i].getName())) {
					fieldMap.put(_fields[i].getName(), _fields[i]);
				}
			}
			_clazz=_clazz.getSuperclass();
		}
		fields.put(clazz.getName(), fieldMap);
		return fields.get(clazz.getName()).get(name);
	}
}
