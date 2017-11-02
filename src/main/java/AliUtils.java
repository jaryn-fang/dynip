import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

public class AliUtils {

    final static String Access_Key_ID = "LTAIC7V3dXJ7mLp4";
    final static String Access_Key_Secret = "MPNCO5tlMCYnprmuYtEl3n2hYFKtJy";

    final static String ALI = "http://alidns.aliyuncs.com/?";

    final static String HTTP_METHOD = "GET";
    final static String ENCODING = "UTF-8";
    final static String ALGORITHM = "HmacSHA1";
    final static String ISO8601_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";


    final static String DOMAINNAME = "jhcoder.top";

    public static void main(String[] args) {
        //update("10.10.10.1", "1111");
        //get();
        //System.out.println(HttpUtils.doGetBackJson(get(), null));
        System.out.println(HttpUtils.doGetBackJson(update(Iputils.getV4IP(), "3604589338286080"), null));
        // getAliIp();
    }

    /**
     * 获取Ali域名的IP
     *
     * @return
     */
    public static String getAliIp(String domainName) {
        return getByKey(domainName,"Value");
    }

    /**
     * 获取Ali域名的RecordId
     *
     * @return
     */
    public static String getAliRecordId(String domainName) {
        return getByKey(domainName,"RecordId");
    }

    public static String getByKey(String domainName, String key) {
        JSONObject obj = (JSONObject) HttpUtils.doGetBackJson(get(), null);
        if (obj == null) {
            return "";
        }
        JSONArray array = obj.getJSONObject("DomainRecords").getJSONArray("Record");
        if (array.size() == 0) {
            return "";
        }
        for(int i=0; i< array.size(); i++) {
            JSONObject result = (JSONObject) array.get(i);
            if (result.get("DomainName").equals(domainName)) {
                return result.get(key).toString();
            }
        }
        return "";
    }


    /**
     * 获取域名列表
     * {
     * "PageNumber": 1,
     * "TotalCount": 1,
     * "PageSize": 20,
     * "RequestId": "77324A79-72C3-41A8-AF2F-E6B355978D81",
     * "DomainRecords": {
     * "Record": [
     * {
     * "RR": "www",
     * "Status": "ENABLE",
     * "Value": "113.116.156.7",
     * "Weight": 1,
     * "RecordId": "3604589338286080",
     * "Type": "A",
     * "DomainName": "jhcoder.top",
     * "Locked": false,
     * "Line": "default",
     * "TTL": 600
     * }
     * ]
     * }
     * }
     *
     * @param domainName 域名
     * @return
     */
    public final static String get() {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters = fillParameters(parameters);

        parameters.put("Action", "DescribeDomainRecords");
        parameters.put("DomainName", DOMAINNAME);

        String url = getUrl(parameters);
        System.out.println(url);
        return url;
    }


    /**
     * 更新解析
     *
     * @param ip
     * @param recordId
     * @return
     */
    public final static String update(String ip, String recordId) {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters = fillParameters(parameters);

        parameters.put("Action", "UpdateDomainRecord");
        parameters.put("RecordId", recordId);
        parameters.put("RR", "www");
        parameters.put("Type", "A");
        parameters.put("Value", ip);
        parameters.put("Type", "A");

        String url = getUrl(parameters);
        System.out.println(url);
        return url;
    }

    /**
     * 执行解析
     *
     * @param ip
     * @param recordId
     * @return
     */
    public final static String enable(String ip, String recordId) {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters = fillParameters(parameters);

        parameters.put("Action", "SetDomainRecordStatus");
        parameters.put("RecordId", recordId);
        parameters.put("Status", "Enable");

        String url = getUrl(parameters);
        System.out.println(url);
        return url;
    }

    /**
     * 获取URL的地址
     *
     * @param parameters
     * @return
     */
    public static String getUrl(Map<String, String> parameters) {
        // 对参数进行排序，注意严格区分大小写
        String[] sortedKeys = parameters.keySet().toArray(new String[]{});
        Arrays.sort(sortedKeys);
        final String SEPARATOR = "&";
        // 生成stringToSign字符串
        StringBuilder stringToSign = new StringBuilder();
        stringToSign.append(HTTP_METHOD).append(SEPARATOR);
        StringBuilder canonicalizedQueryString = new StringBuilder();
        try {
            stringToSign.append(percentEncode("/")).append(SEPARATOR);


            for (String key : sortedKeys) {
                // 这里注意对key和value进行编码
                canonicalizedQueryString.append("&")
                        .append(percentEncode(key)).append("=")
                        .append(percentEncode(parameters.get(key)));
            }
            // 这里注意对canonicalizedQueryString进行编码
            stringToSign.append(percentEncode(canonicalizedQueryString.toString().substring(1)));

            String sign = getSign(Access_Key_Secret + "&", stringToSign.toString());

            String url = ALI + canonicalizedQueryString.toString().substring(1) + "&Signature=" + percentEncode(sign);

            return url;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 填充公共参数
     *
     * @param parameters
     * @return
     */
    public static Map<String, String> fillParameters(Map<String, String> parameters) {
        // 加入请求参数
        parameters.put("Version", "2015-01-09");
        parameters.put("AccessKeyId", Access_Key_ID);
        parameters.put("Timestamp", formatIso8601Date(new Date()));
        parameters.put("SignatureMethod", "HMAC-SHA1");
        parameters.put("SignatureVersion", "1.0");
        parameters.put("SignatureNonce", UUID.randomUUID().toString());
        parameters.put("Format", "JSON");
        return parameters;
    }

    /**
     * 获取一段计算签名的示例代码
     *
     * @param key
     * @param stringToSign
     * @return
     */
    public final static String getSign(String key, String stringToSign) {


        Mac mac = null;
        try {
            mac = Mac.getInstance(ALGORITHM);
            mac.init(new SecretKeySpec(key.getBytes(ENCODING), ALGORITHM));
            byte[] signData = mac.doFinal(stringToSign.getBytes(ENCODING));
            String signature = new String(Base64.getEncoder().encode(signData));
            return signature;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    private static String formatIso8601Date(Date date) {
        SimpleDateFormat df = new SimpleDateFormat(ISO8601_DATE_FORMAT);
        df.setTimeZone(new SimpleTimeZone(0, "GMT"));
        return df.format(date);
    }


    public static String percentEncode(String value) throws UnsupportedEncodingException {
        return value != null ? URLEncoder.encode(value, ENCODING)
                .replace("+", "%20")
                .replace("*", "%2A").replace("%7E", "~") : null;
    }


}
