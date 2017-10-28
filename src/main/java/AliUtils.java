import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;

public class AliUtils {

    private static String Access_Key_ID =  "LTAIC7V3dXJ7mLp4";
    private static String Access_Key_Secret =  "MPNCO5tlMCYnprmuYtEl3n2hYFKtJy";
    private static final String ISO8601_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    final static  String HTTP_METHOD = "GET";

    private static final String ENCODING = "UTF-8";

    static String ALI = "http://alidns.aliyuncs.com/?";

    public static String percentEncode(String value)
            throws UnsupportedEncodingException {
        return value != null ?
                URLEncoder.encode(value, ENCODING).replace("+", "%20")
                        .replace("*", "%2A").replace("%7E", "~")
                : null;
    }


    public static void main(String[] args) {
        //update("10.10.10.1", "1111");
        get();
        //System.out.println(HttpUtils.doGetBackJson(get(), null));
        System.out.println(HttpUtils.doGetBackJson(update("113.116.156.9", "3604589338286080"), null));
       // getAliIp();
    }

    public static String getAliIp() {
        JSONObject obj = (JSONObject)HttpUtils.doGetBackJson(get(), null);
        if(obj == null) {
            return "";
        }
        JSONArray array = obj.getJSONObject("DomainRecords").getJSONArray("Record");
        if(array.size() == 0 ){
            return "";
        }
        JSONObject result = (JSONObject) array.get(0);
        if(result == null) {
            return "";
        }
        return result.get("Value").toString();
    }

    public final static String getSign(String key, String stringToSign) {
        // 以下是一段计算签名的示例代码
        final String ALGORITHM = "HmacSHA1";
        final String ENCODING = "UTF-8";
        Mac mac = null;
        try {
            mac = Mac.getInstance(ALGORITHM);
            mac.init(new SecretKeySpec( key.getBytes(ENCODING), ALGORITHM));
            byte[] signData = mac.doFinal(stringToSign.getBytes(ENCODING));
            String signature = new String(Base64.getEncoder().encode(signData));
            return signature;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public final static String get() {
        Map<String, String> parameters = new HashMap<String, String>();
        // 加入请求参数
        parameters.put("Version", "2015-01-09");
        parameters.put("AccessKeyId", Access_Key_ID);
        parameters.put("Timestamp", formatIso8601Date(new Date()));
        parameters.put("SignatureMethod", "HMAC-SHA1");
        parameters.put("SignatureVersion", "1.0");
        parameters.put("SignatureNonce", UUID.randomUUID().toString());
        parameters.put("Format", "JSON");

        parameters.put("DomainName", "jhcoder.top");
        parameters.put("Action", "DescribeDomainRecords");

        // 对参数进行排序，注意严格区分大小写
        String[] sortedKeys = parameters.keySet().toArray(new String[]{});
        Arrays.sort(sortedKeys);
        final String SEPARATOR = "&";
        // 生成stringToSign字符串
        StringBuilder stringToSign = new StringBuilder();
        stringToSign.append(HTTP_METHOD).append(SEPARATOR);
        try {
            stringToSign.append(percentEncode("/")).append(SEPARATOR);


            StringBuilder canonicalizedQueryString = new StringBuilder();
            for(String key : sortedKeys) {
                // 这里注意对key和value进行编码
                canonicalizedQueryString.append("&")
                        .append(percentEncode(key)).append("=")
                        .append(percentEncode(parameters.get(key)));
            }
            // 这里注意对canonicalizedQueryString进行编码
            stringToSign.append(percentEncode(canonicalizedQueryString.toString().substring(1)));

            String sign = getSign(Access_Key_Secret + "&", stringToSign.toString());



            String url = ALI + canonicalizedQueryString.toString().substring(1) + "&Signature=" + percentEncode(sign);

            System.out.println(url);
            return url;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }


    public final static String update(String ip, String recordId) {
        Map<String, String> parameters = new HashMap<String, String>();
        // 加入请求参数
        parameters.put("Version", "2015-01-09");
        parameters.put("AccessKeyId", Access_Key_ID);
        parameters.put("Timestamp", formatIso8601Date(new Date()));
        parameters.put("SignatureMethod", "HMAC-SHA1");
        parameters.put("SignatureVersion", "1.0");
        parameters.put("SignatureNonce", UUID.randomUUID().toString());
        parameters.put("Format", "JSON");

        parameters.put("Action", "UpdateDomainRecord");
        parameters.put("RecordId", recordId);
        parameters.put("RR", "@");
        parameters.put("Type", "A");
        parameters.put("Value", ip);
        parameters.put("Type", "A");

        // 对参数进行排序，注意严格区分大小写
        String[] sortedKeys = parameters.keySet().toArray(new String[]{});
        Arrays.sort(sortedKeys);
        final String SEPARATOR = "&";
        // 生成stringToSign字符串
        StringBuilder stringToSign = new StringBuilder();
        stringToSign.append(HTTP_METHOD).append(SEPARATOR);
        try {
            stringToSign.append(percentEncode("/")).append(SEPARATOR);


            StringBuilder canonicalizedQueryString = new StringBuilder();
            for(String key : sortedKeys) {
                // 这里注意对key和value进行编码
                canonicalizedQueryString.append("&")
                        .append(percentEncode(key)).append("=")
                        .append(percentEncode(parameters.get(key)));
            }
            // 这里注意对canonicalizedQueryString进行编码
            stringToSign.append(percentEncode(canonicalizedQueryString.toString().substring(1)));

            String sign = getSign(Access_Key_Secret + "&", stringToSign.toString());


            String url = ALI + canonicalizedQueryString.toString().substring(1) + "&Signature=" + percentEncode(sign);

            System.out.println(url);
            return url;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    return "";
    }


    private static String formatIso8601Date(Date date) {
        SimpleDateFormat df = new SimpleDateFormat(ISO8601_DATE_FORMAT);
        df.setTimeZone(new SimpleTimeZone(0, "GMT"));
        return df.format(date);
    }

}
