package com.game.json;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.sf.ezmorph.bean.MorphDynaBean;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import net.sf.json.JsonConfig;
import net.sf.json.util.NewBeanInstanceStrategy;
import net.sf.json.util.PropertyFilter;
import net.sf.json.util.PropertySetStrategy;
import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.LoggerFactory;

public class JSONserializable
{
  private static org.slf4j.Logger log = LoggerFactory.getLogger(JSONserializable.class);

  private static HashMap<String, HashMap<String, Field>> fields = new HashMap();

  private static JsonConfig serializableConfig = new JsonConfig();
  private static JsonConfig unserializableConfig;

  static
  {
    serializableConfig.setIgnorePublicFields(true);

    serializableConfig.setJsonPropertyFilter(new PropertyFilter() {
      public boolean apply(Object source, String name, Object value) {
        try {
          Field field = JSONserializable.getDeclaredField(source.getClass(), name);

          if ((field != null) && 
            (Modifier.isTransient(field.getModifiers())))
            return true;
        }
        catch (Exception e) {
          e.printStackTrace();
        }
        return false;
      }
    });
    unserializableConfig = new JsonConfig();

    unserializableConfig
      .setNewBeanInstanceStrategy(new NewBeanInstanceStrategy()
    {
      public Object newInstance(Class c, JSONObject jo)
        throws InstantiationException, IllegalAccessException
      {
        if (Modifier.isAbstract(c.getModifiers())) {
          try
          {
            return Class.forName(jo.getString("clazz"))
              .newInstance();
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
        return c.newInstance();
      }
    });
    unserializableConfig.setPropertySetStrategy(new PropertySetStrategy()
    {
      public void setProperty(Object bean, String key, Object value)
        throws JSONException
      {
        if ((!(bean instanceof List)) && (!(bean instanceof Map)) && 
          (!(bean instanceof Set)) && 
          (JSONserializable.getDeclaredField(bean.getClass(), key) == null))
        {
          return;
        }

        if (value != null)
        {
          if (MorphDynaBean.class.isAssignableFrom(value
            .getClass())) {
            MorphDynaBean _bean = (MorphDynaBean)value;
            try {
              _bean.get("clazz");
            } catch (Exception e) {
              return;
            }

            try
            {
              Class clazz = null;
              try {
                clazz = Class.forName((String)_bean.get("clazz"));
              } catch (Exception localException1) {
              }
              if (clazz == null) {
                return;
              }

              JsonConfig jsonConfig = JSONserializable.unserializableConfig.copy();

              jsonConfig.setRootClass(clazz);

              value = JSONObject.toBean((JSONObject)
                JSONSerializer.toJSON(_bean, JSONserializable.serializableConfig), jsonConfig);
            } catch (Exception e) {
              throw new JSONException(e);
            }
          }
        }
        if ((value != null) && ((value instanceof List))) {
          List list = (List)value;
          if (list.size() == 0)
            return;
          Object obj = list.get(0);
          if (MorphDynaBean.class.isAssignableFrom(obj.getClass())) {
            List temp = null;
            try {
              temp = (List)list.getClass().newInstance();
            } catch (Exception e) {
              throw new JSONException(e);
            }

            if (temp == null) {
              return;
            }
            for (int i = 0; i < list.size(); i++) {
              MorphDynaBean _bean = (MorphDynaBean)list.get(i);
              try {
                _bean.get("clazz");
              } catch (Exception e) {
                continue;
              }
              try
              {
                Class clazz = null;
                try {
                  clazz = Class.forName((String)_bean
                    .get("clazz"));
                } catch (Exception localException2) {
                }
                if (clazz == null) {
                  return;
                }

                JsonConfig jsonConfig = JSONserializable.unserializableConfig
                  .copy();

                jsonConfig.setRootClass(clazz);

                temp.add(JSONObject.toBean(
                  (JSONObject)JSONSerializer.toJSON(
                  _bean, JSONserializable.serializableConfig), 
                  jsonConfig));
              } catch (Exception e) {
                throw new JSONException(e);
              }
            }

            value = temp;
          }
        }

        if ((bean instanceof Map))
          ((Map)bean).put(key, value);
        else
          try {
            PropertyUtils.setSimpleProperty(bean, key, value);
          } catch (NoSuchMethodException e) {
            throw new JSONException(e);
          } catch (Exception e) {
            throw new JSONException(e);
          }
      }
    });
  }

  public static String toString(Object obj)
  {
    try
    {
      Object object = JSONSerializer.toJSON(obj, serializableConfig);

      return object.toString();
    } catch (Exception e) {
      log.error(e, e);
      log.error(JSONSerializer.toJSON(obj, serializableConfig));
    }
    return null;
  }

  public static Object toObject(String data, Class<?> clazz)
  {
    try
    {
      JsonConfig jsonConfig = unserializableConfig.copy();

      jsonConfig.setRootClass(clazz);

      JSONObject object = JSONObject.fromObject(data);

      return JSONObject.toBean(object, jsonConfig);
    } catch (Exception e) {
      log.error(e, e);
      log.error(data);
    }
    return null;
  }

  public static Object toList(String data, Class<?> clazz)
  {
    try
    {
      JsonConfig jsonConfig = unserializableConfig.copy();

      jsonConfig.setRootClass(clazz);

      JSONArray object = JSONArray.fromObject(data);

      return JSONArray.toCollection(object, jsonConfig);
    } catch (Exception e) {
      log.error(e, e);
      log.error(data);
    }
    return null;
  }

  private static Field getDeclaredField(Class<?> c, String name)
  {
    if (fields.containsKey(c.getName()))
    {
      return (Field)((HashMap)fields.get(c.getName())).get(name);
    }
    Class _c = c;

    HashMap fieldMap = new HashMap();

    while (_c != null)
    {
      Field[] _fields = _c.getDeclaredFields();
      for (int i = 0; i < _fields.length; i++)
      {
        if (!fieldMap.containsKey(_fields[i].getName()))
        {
          fieldMap.put(_fields[i].getName(), _fields[i]);
        }
      }

      _c = _c.getSuperclass();
    }

    fields.put(c.getName(), fieldMap);

    return (Field)((HashMap)fields.get(c.getName())).get(name);
  }
}