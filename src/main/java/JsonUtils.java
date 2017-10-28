
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * <pre>
 * JSON工具类
 * </pre>
 *
 */
public class JsonUtils {

    // static {
    // JSON.DEFFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    // }

    /**
     * <pre>
     * 对象序列化为json字符串
     * </pre>
     *
     * @param t
     * @return
     */
    public final static <T> String stringify(T t) {
        String json = "{}";
        try {
            json = JSON.toJSONString(t, SerializerFeature.DisableCircularReferenceDetect, SerializerFeature.UseISO8601DateFormat);
        } catch (Exception e) {

        }
        return json;
    }

    /**
     * <pre>
     * 对象序列化为二进制json
     * </pre>
     *
     * @param <T>
     * @param t
     * @return
     */
    public final static <T> byte[] binaryify(T t) {
        byte[] json = null;
        try {
            json = JSON.toJSONBytes(t, SerializerFeature.DisableCircularReferenceDetect, SerializerFeature.UseISO8601DateFormat);
        } catch (Exception e) {

        }
        return json;
    }

    /**
     * <pre>
     * 字符串反序列化为对象
     * </pre>
     *
     * @param str
     * @param valueType
     * @return
     */
    public final static <T> T parse(String str, Class<T> valueType) {
        T obj = null;
        try {
            obj = JSON.parseObject(str, valueType, Feature.AllowISO8601DateFormat);
        } catch (Exception e) {
            System.out.println("对象反序列化错误,jsonStr:" + str);
        }
        return obj;
    }

    /**
     * <pre>
     * 二进制json反序列化为对象
     * </pre>
     *
     * @param bs
     * @param valueType
     * @return
     */
    public final static <T> T parse(byte[] bs, Class<T> valueType) {
        T obj = null;
        try {
            obj = JSON.parseObject(bs, valueType, Feature.AllowISO8601DateFormat);
        } catch (Exception e) {
            System.out.println("二进制反序列化错误,bs:" + Arrays.toString(bs));
        }
        return obj;
    }

    /**
     * <pre>
     * 字符串反序列化为对象
     * ！{}型json返回JsonObject相当于一个HashMap
     * ！[]型json返回JsonArray相当于一个ArrayList
     * </pre>
     *
     * @param str
     * @return
     */
    public final static Object parse(String str) {
        Object obj = null;
        try {
            obj = JSON.parse(str, Feature.AllowISO8601DateFormat);
        } catch (Exception e) {
            System.out.println("集合反序列化错误,jsonStr：" + str);
        }
        return obj;
    }

    /**
     * <pre>
     * 二进制反序列化为对象
     * ！{}型json返回JsonObject相当于一个HashMap
     * ！[]型json返回JsonArray相当于一个ArrayList
     * </pre>
     */
    public final static Object parse(byte[] bs) {
        Object obj = null;
        try {
            obj = JSON.parse(bs, Feature.AllowISO8601DateFormat);
        } catch (Exception e) {
            try {
                System.out.println("集合反序列化错误,jsonStr：" + new String(bs, "UTF-8"));
            } catch (UnsupportedEncodingException e1) {
                System.out.println("");
            }
        }
        return obj;
    }

    public static void main(String[] args) {
        Map<String, Object> obj = new HashMap<>();
        obj.put("key01", new Date());
        obj.put("key02", 1.00f);
        String str1 = JSON.toJSONStringWithDateFormat(obj, "yyyy-MM-dd HH:mm:ss");
        System.out.println(str1);// {"key01":"2017-07-07 17:49:02"}
        JSONObject obj1 = (JSONObject) JSON.parse(str1);
        System.out.println(obj1.get("key01").getClass().getSimpleName());// String
    }

    /**
     * @return
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "JsonUtil []";
    }

    static class TestJson {
        private int id;
        @JSONField(serialize = false)
        private String name;

        public TestJson(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public final int getId() {
            return id;
        }

        public final void setId(int id) {
            this.id = id;
        }

        public final String getName() {
            return name;
        }

        public final void setName(String name) {
            this.name = name;
        }

    }
}
