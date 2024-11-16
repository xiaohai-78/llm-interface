package com.xiaohai.llminterface.utils;

import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.ConnectionPool;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.SecureRandom;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * http工具类，依赖okhttp3
 * 废弃util中其他的httpUtil，统一到这个类来
 * @date 2022-8-4
 */
@Slf4j
public class OkHttpUtil {

    private static final Logger LOG = LoggerFactory.getLogger(OkHttpUtil.class);

//    private static final int CONNECT_TIMEOUT = 5 * 1000 * 100;
    private static final int CONNECT_TIMEOUT = 30 * 1000 * 100;

//    private static final int READ_TIMEOUT = 10 * 1000 * 100;
    private static final int READ_TIMEOUT = 60 * 1000 * 100;

//    private static final int WRITE_TIMEOUT = 10 * 1000 * 100;
    private static final int WRITE_TIMEOUT = 60 * 1000 * 100;

    /**
     * okhttp连接池中整体的空闲连接的最大数量
     */
    private static final int MAX_IDL_CONNECTIONS = 10;
    /**
     * 最大连接空闲时时间,秒
     */
    private static final long KEEP_ALIVE_DURATION = 60L;

    public static final String APPLICATION_JSON_UTF8_VALUE = "application/json;charset=UTF-8";

    public static final String APPLICATION_FORM_URLENCODED_VALUE = "application/x-www-form-urlencoded";

    public static final String APPLICATION_OCTET_STREAM_VALUE = "application/octet-stream";
    /**
     * 单服务共享一个client实例
     */
    private static final OkHttpClient CLIENT;

    static {
        CLIENT = okHttpConfigClient();
    }

    private static OkHttpClient okHttpConfigClient() {
        return new OkHttpClient().newBuilder()
                .connectionPool(pool())
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
                .readTimeout(READ_TIMEOUT, TimeUnit.MILLISECONDS)
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.MILLISECONDS)
                //支持HTTPS请求，跳过证书验证
                .sslSocketFactory(createSSLSocketFactory(), (X509TrustManager) getTrustManagers()[0])
                .hostnameVerifier((hostname, session) -> true)
                .build();
    }

    private static ConnectionPool pool() {
        return new ConnectionPool(MAX_IDL_CONNECTIONS, KEEP_ALIVE_DURATION, TimeUnit.SECONDS);
    }

    /**
     * 生成安全套接字工厂，用于https请求的证书跳过
     *
     * @return 返回安全套接字工厂
     */
    private static SSLSocketFactory createSSLSocketFactory() {
        SSLSocketFactory ssfFactory = null;
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, getTrustManagers(), new SecureRandom());
            ssfFactory = sc.getSocketFactory();
        } catch (Exception e) {
            LOG.error("createSSLSocketFactory error", e);
        }
        return ssfFactory;
    }

    private static TrustManager[] getTrustManagers() {
        return new TrustManager[] {
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                    }

                    @Override
                    public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                    }

                    @Override
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return new java.security.cert.X509Certificate[]{};
                    }
                }
        };
    }


    /**
     * get请求（同步）
     * @param url 请求地址,不能为空
     * @return 返回get请求response，请求失败则返回null
     */
    public static String get(String url) {
        return get(url, null);
    }

    /**
     * get请求（同步）
     * @param url 请求地址,不能为空
     * @param params 请求参数，置于url后，可以为空
     * @return 返回get请求response，请求失败则返回null
     */
    public static String get(String url, Map<String, Object> params) {
        return get(url, params, null);
    }

    /**
     * get请求（同步）
     * @param url 请求地址,不能为空
     * @param params 请求参数，置于url后，可以为空
     * @return 返回get请求response，请求失败则返回null
     */
    public static String get(String url, Map<String, Object> params, Map<String, String> header) {
        Request request = constructGetRequest(url, params, header);
        // 请求发送
        return doSyncRequest(request);
    }

    /**
     * post请求（同步）
     * MediaType 为 application/json
     * @param url 请求地址，不能为空
     * @param params 请求参数，置于request body，不能为空
     * @param contentType 不可为空
     * @param headers 请求头，可以为空
     * @return 返回post请求response，请求失败则返回null
     */
    public static String postCommon(String url, Map<String, Object> params, String contentType, Map<String, String> headers) {
        if (StringUtil.isEmpty(url)) {
            throw new IllegalArgumentException("url不能为空");
        }
        if (params == null) {
            throw new IllegalArgumentException("params不能为空");
        }
        JSONObject paramJson = new JSONObject();
        Set<String> keySet = params.keySet();
        for (String key : keySet) {
            paramJson.put(key, params.get(key).toString());
        }
        System.out.println(paramJson);
        return post(url, paramJson.toJSONString(), contentType, headers);
    }

    /**
     * post请求（同步）
     * MediaType 为 contentType
     * @param url 请求地址，不能为空
     * @param strData 请求体，放置于request body, 不能为空
     * @param contentType content-type 默认 application/json;charset=UTF-8
     * @param headers 请求头，可以为空
     * @return 返回post请求response，请求失败则返回null
     */
    public static String post(String url, String strData, String contentType, Map<String, String> headers) {
        log.info("requestBody: {}", strData);
        Request request = constructPostRequest(url, strData, contentType, headers);
        // 请求发送
        String doSyncRequest = doSyncRequest(request);
        log.info("response: {}", doSyncRequest);
        return doSyncRequest;
    }

    /**
     * 构建get请求Request实体
     * @param url 请求地址,不能为空
     * @param params 请求参数，置于url后，可以为空
     * @param headersMap 请求头，可以为空
     * @return 返回构建好的get Request实体
     */
    private static Request constructGetRequest(String url, Map<String, Object> params, Map<String, String> headersMap) {
        if (StringUtil.isEmpty(url)) {
            throw new IllegalArgumentException("url不能为空");
        }
        return buildGetRequest(url, params, headersMap);
    }

    /**
     * 通用构建post Request方法
     * @param url 请求地址，不能为空
     * @param params 请参数，可以为空
     * @param headersMap 请求头，可以为空
     * @return 返回构建好的post Request实体
     */
    private static Request buildGetRequest(String url, Map<String, Object> params, Map<String, String> headersMap) {
        // 请求构建
        if (params != null && !params.isEmpty()) {
            url = url + CommentUtil.mapToUrl(params);
        }
        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .get();
        if (headersMap!= null && !headersMap.isEmpty()) {
            Headers headers = Headers.of(headersMap);
            requestBuilder.headers(headers);
        }
        return requestBuilder.build();
    }

    /**
     * 构建post请求Request实体
     * MediaType 为 contentType
     * @param url 请求地址，不能为空
     * @param strData 请求体，放置于request body, 不能为空
     * @param contentType content-type 默认 application/json;charset=UTF-8
     * @param headers 请求头，可以为空
     * @return 返回构建好的post Request实体
     */
    private static Request constructPostRequest(String url, String strData, String contentType, Map<String, String> headers) {
        if (StringUtil.isEmpty(url)) {
            throw new IllegalArgumentException("url不能为空");
        }
        // 请求体
        RequestBody requestBody = constructRequestBody(strData, contentType);
        // 请求构建
        return buildPostRequest(url, requestBody, headers);
    }

    /**
     * 通用构建post Request方法
     * @param url 请求地址，不能为空
     * @param requestBody 请求体，放置于request body, 不能为空
     * @param headers 请求头，可以为空
     * @return 返回构建好的post Request实体
     */
    private static Request buildPostRequest(String url, RequestBody requestBody, Map<String, String> headers) {
        // 请求构建
        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .post(requestBody);
        // 请求头
        if (headers!= null && !headers.isEmpty()) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                requestBuilder.addHeader(entry.getKey(), entry.getValue());
            }
        }
        return requestBuilder.build();
    }

    /**
     * 构建请求体，String输入
     * content-type 为 contentType
     * @param strData 请求体内容
     * @param contentType content-type 默认 application/json;charset=UTF-8
     * @return 返回构建的RequestBody
     */
    private static RequestBody constructRequestBody(String strData, String contentType) {
        if (strData == null) {
            throw new IllegalArgumentException("strData不能为空");
        }
        if (StringUtil.isEmpty(contentType)) {
            contentType = APPLICATION_JSON_UTF8_VALUE;
        }
        return RequestBody.create(MediaType.parse(contentType), strData);
    }

    /**
     * 进行同步请求，并返回结果(String)
     * @param request 同步请求体
     * @return 返回请求response，请求失败则返回null
     */
    private static String doSyncRequest(Request request) {
        // 请求发送
        try (Response response = CLIENT.newCall(request).execute()){
            if (response.body() != null) {
                return response.body().string();
            }
        } catch (Exception e) {
            LOG.error("OkHttpUtil post error", e);
        }
        return null;
    }

    /**
     * 文件上传formPart,用于文件上传场景
     * 使用方式为new FileFormPart(name, filename, content).init()
     * 然后传入到具体的post方法使用
     * requestBody的默认mediaType为 {@value APPLICATION_OCTET_STREAM_VALUE}
     * @author kaka
     */
    @Getter
    public static class FileFormPart {

        private final String name;

        private final String filename;

        private final byte[] content;

        private MediaType mediaType;

        private RequestBody requestBody;

        public FileFormPart(String name, String filename, byte[] content, MediaType mediaType) {
            this(name, filename, content);
            this.mediaType = mediaType;
        }

        public FileFormPart(String name, String filename, byte[] content) {
            this.name = name;
            this.filename = filename;
            this.content = content;
        }

        public FileFormPart init() {
            if (mediaType == null) {
                mediaType = MediaType.parse(APPLICATION_OCTET_STREAM_VALUE);
            }
            requestBody = RequestBody.create(mediaType, content);
            return this;
        }
    }

}

