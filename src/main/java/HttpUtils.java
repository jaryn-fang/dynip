
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;

/**
 * <pre>
 * HTTP请求工具类
 * </pre>
 *
 * @author jun.li
 * @time 2017年5月12日 下午3:37:20
 */
public class HttpUtils {

    private static final String REQUEST_METHOD_POST = "POST";
    private static final String REQUEST_METHOD_GET = "GET";

    /**
     * 连接超时
     */
    private static int CONNECT_TIME_OUT = 2000;

    /**
     * 读取数据超时
     */
    private static int READ_TIME_OUT = 2000;

    /**
     * 请求编码
     */
    private static String REQUEST_ENCODING = "UTF-8";

    /**
     * <pre>
     * 发送带参数的GET的HTTP请求
     * </pre>
     *
     */
    public static String doGet(String reqUrl, Map<String, Object> paramMap) {
        return doRequest(reqUrl, paramMap, REQUEST_METHOD_GET, REQUEST_ENCODING);
    }

    /**
     * <pre>
     * 发送带参数的POST的HTTP请求
     * </pre>
     *
     * @return HTTP响应的字符串
     */
    public static String doPost(String reqUrl, Map<String, Object> paramMap) {
        return doRequest(reqUrl, paramMap, REQUEST_METHOD_POST, REQUEST_ENCODING);
    }

    /**
     * <pre>
     * 发送带参数的GET的HTTP请求
     * ！返回JSON格式数据
     * </pre>
     *
     * @return HTTP响应的字符串
     */
    public static Object doGetBackJson(String reqUrl, Map<String, Object> paramMap) {
        String str = doRequest(reqUrl, paramMap, REQUEST_METHOD_GET, REQUEST_ENCODING);
        if (str == null) {
            return null;
        }
        return JsonUtils.parse(str);
    }

    /**
     * <pre>
     * 发送带参数的POST的HTTP请求
     * ！返回JSON格式数据
     * </pre>
     *
     * @return HTTP响应的字符串
     */
    public static Object doPostBackJson(String reqUrl, Map<String, Object> paramMap) {
        String str = doRequest(reqUrl, paramMap, REQUEST_METHOD_POST, REQUEST_ENCODING);
        if (str == null) {
            return null;
        }
        return JsonUtils.parse(str);
    }

    private static String doRequest(String reqUrl, Map<String, Object> paramMap, String reqMethod, String recvEncoding) {
        HttpURLConnection urlCon = null;
        String responseContent = null;
        try {
            StringBuilder params = new StringBuilder();
            URL url = null;
            if (paramMap != null) {
                if (reqMethod.equals(REQUEST_METHOD_GET)) {
                    params.append("?");
                }
                for (Entry<String, Object> element : paramMap.entrySet()) {
                    params.append(element.getKey());
                    params.append("=");
                    Object obj = element.getValue();
                    if (obj == null) {
                        obj = "0";
                    }
                    params.append(URLEncoder.encode(obj.toString(), REQUEST_ENCODING));
                    params.append("&");
                }

                if (params.length() > 0) {
                    params = params.deleteCharAt(params.length() - 1);
                }
            }
            if (reqMethod.equals(REQUEST_METHOD_GET)) {
                url = new URL(reqUrl + params.toString());
            } else {
                url = new URL(reqUrl);
            }
            urlCon = (HttpURLConnection) url.openConnection();
            urlCon.setRequestMethod(reqMethod);
            urlCon.setConnectTimeout(CONNECT_TIME_OUT);
            urlCon.setReadTimeout(READ_TIME_OUT);
            if (reqMethod.equals(REQUEST_METHOD_POST)) {
                urlCon.setDoOutput(true);
                byte[] b = params.toString().getBytes();
                urlCon.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                urlCon.setRequestProperty("Content-Length", String.valueOf(b.length));
                urlCon.getOutputStream().write(b, 0, b.length);
                urlCon.getOutputStream().flush();
                urlCon.getOutputStream().close();
            }

            InputStream in = urlCon.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(in, recvEncoding));
            String tempLine = rd.readLine();
            StringBuffer tempStr = new StringBuffer();
            while (tempLine != null) {
                tempStr.append(tempLine);
                tempLine = rd.readLine();
            }
            responseContent = tempStr.toString();
            rd.close();
            in.close();

            urlCon.getResponseMessage();
        } catch (IOException e) {
            Log.error("urlconnection error , url " + reqUrl);
        } finally {
            if (urlCon != null) {
                urlCon.disconnect();
            }
        }
        return responseContent;
    }

    public static void main(String[] args) {
    }

}
