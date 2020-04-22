package org.huang.common.utils;

import lombok.experimental.UtilityClass;
import net.sf.cglib.beans.BeanCopier;
import net.sf.cglib.beans.BeanMap;
import net.sf.cglib.core.Converter;
import net.sf.cglib.reflect.FastClass;
import org.apache.commons.lang3.time.FastDateFormat;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@UtilityClass
public class BeanUtils {
    private final Map<String, BeanCopier> noConvertCoppers = new ConcurrentHashMap<>();
    private final Map<String, BeanCopier> coppers = new ConcurrentHashMap<>();
    private final Map<Class, FastClass> fastClassMap = new ConcurrentHashMap<>();

    //private final Map<Class, BeanMap> beanMapMap = new ConcurrentHashMap<>();
    private final FieldConverter converter = new FieldConverter();

    /**
     * 将 src 的属性复制到 dest
     * 同名数据类型不同时不复制
     *
     * @param src  源对象
     * @param dest 目标对象
     */
    public void copyProperties(Object src, Object dest) {
        String key = src.getClass().getName() + dest.getClass().getName();
        BeanCopier beanCopier = noConvertCoppers.computeIfAbsent(key, k -> BeanCopier.create(src.getClass(), dest.getClass(), false));
        beanCopier.copy(src, dest, null);
    }

    public void copyProperties(Object src, Map dest) {
        //BeanMap beanMap = beanMapMap.computeIfAbsent(src.getClass(), c -> BeanMap.create(src));
        //TODO 这里每次都要创建类属性访问器， 要调研一下有没有办法缓存类属性的访问器，然后传入对象来获取就好了
        BeanMap beanMap = BeanMap.create(src);
        dest.putAll(beanMap);
    }

    public void copyProperties(Map src, Object dest) {
        //BeanMap beanMap = beanMapMap.computeIfAbsent(dest.getClass(), c -> BeanMap.create(dest));
        BeanMap beanMap = BeanMap.create(dest);
        beanMap.putAll(src);
    }

    /**
     * 这个重载方法是为了防止前面两个方法，遇到两个map参数的情况，
     * 使用重载可以节省copyProperties(Object src, Map dest) 和 copyProperties(Map src, Object dest)判断 两个map参数的逻辑
     * @param src
     * @param dest
     */
    public void copyProperties(Map src, Map dest) {
        dest.putAll(src);
    }

    public void copy(Object src, Map dest) {
        copyProperties(src,dest);
    }

    public void copy(Map src, Object dest) {
        copyProperties(src,dest);
    }

    /**
     * 这个重载方法是为了防止前面两个方法，遇到两个map参数的情况，
     * 使用重载可以节省copyProperties(Object src, Map dest) 和 copyProperties(Map src, Object dest)判断 两个map参数的逻辑
     * @param src
     * @param dest
     */
    public void copy(Map src, Map dest) {
        copyProperties(src,dest);
    }

    /**
     * 将 src 的属性复制到 dest<br/>
     * 同名数据类型不同时尽最大努力转换，转换不了设为null值
     *
     * @param src  源对象
     * @param dest 目标对象
     */
    public void copy(Object src, Object dest) {
        String key = src.getClass().getName() + dest.getClass().getName();
        BeanCopier beanCopier = coppers.computeIfAbsent(key, k -> BeanCopier.create(src.getClass(), dest.getClass(), true));
        beanCopier.copy(src, dest, converter);
    }

    public <T> T map(Object src, Class<T> type) {
        try {
            FastClass fastClass = fastClassMap.computeIfAbsent(type, FastClass::create);
            T t = (T)fastClass.newInstance();
            copy(src, t);
            return t;
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> List<T> map2list(Collection src, Class<T> type) {
        List<T> list = new ArrayList<>((int)(src.size() / 0.75));
        for (Object o : src) {
            list.add(map(o,type));
        }
        return list;
    }

    private class FieldConverter implements Converter {
        private static final Map<String, Function> convert = new ConcurrentHashMap<>();
        private static final FastDateFormat YMD = FastDateFormat.getInstance("yyyy-MM-dd");
        private static final FastDateFormat YMDHMS = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");

        @Override
        public Object convert(Object o, Class aClass, Object o1) {
            if (o == null) return null;
            if (o.getClass() == aClass || aClass.isAssignableFrom(o.getClass())) return o;
            String key = o.getClass().getName() + aClass.getName();
            Function function = convert.get(key);
            if (function != null)
                return function.apply(o);
            if (aClass == String.class)
                return String.valueOf(o);
            return null;
        }

        private static Date ps(String s, FastDateFormat f) {
            try {
                return f.parse(s);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }

        static {
            convert.put(String.class.getName() + "byte", o -> Byte.parseByte(o.toString()));
            convert.put(String.class.getName() + "int", o -> Integer.parseInt(o.toString()));
            convert.put(String.class.getName() + "long", o -> Long.parseLong(o.toString()));
            convert.put(String.class.getName() + "float", o -> Float.parseFloat(o.toString()));
            convert.put(String.class.getName() + "double", o -> Double.parseDouble(o.toString()));

            convert.put(Byte.class.getName() + "byte", o -> ((Number) o).byteValue());
            convert.put(Integer.class.getName() + "int", o -> ((Number) o).intValue());
            convert.put(Long.class.getName() + "long", o -> ((Number) o).longValue());
            convert.put(Float.class.getName() + "float", o -> ((Number) o).floatValue());
            convert.put(Double.class.getName() + "double", o -> ((Number) o).doubleValue());

            convert.put(String.class.getName() + Integer.class.getName(), o -> Integer.parseInt(o.toString()));
            convert.put(String.class.getName() + Long.class.getName(), o -> Long.parseLong(o.toString()));
            convert.put(String.class.getName() + Double.class.getName(), o -> Double.parseDouble(o.toString()));
            convert.put(String.class.getName() + Float.class.getName(), o -> Float.parseFloat(o.toString()));
            convert.put(String.class.getName() + BigDecimal.class.getName(), o -> new BigDecimal(o.toString()));
            convert.put(String.class.getName() + Date.class.getName(), o -> {
                String str = o.toString();
                if (str.matches("^\\d{4}-\\d{1,2}-\\d{1,2}$")) {
                    return ps(str, YMD);
                }
                return ps(str, YMDHMS);
            });
            convert.put(Date.class.getName() + String.class.getName(), o -> YMDHMS.format((Date) o));
        }
    }
}
