package com.xyes.springboot.manager;


import com.xyes.springboot.manager.constant.SparkApiVersion;
import com.xyes.springboot.manager.exception.SparkException;
import com.xyes.springboot.manager.listener.SparkBaseListener;
import com.xyes.springboot.manager.listener.SparkSyncChatListener;
import com.xyes.springboot.manager.model.SparkSyncChatResponse;
import com.xyes.springboot.manager.model.request.SparkRequest;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * XfSparkClient
 */
public class SparkClient {

    /**
     * 讯飞星火应用ID
     * 需要在讯飞开放平台创建应用并获取
     */
    public String appid;

    /**
     * 讯飞星火API Key
     * 需要在讯飞开放平台创建应用并获取
     */
    public String apiKey;

    /**
     * 讯飞星火API Secret
     * 需要在讯飞开放平台创建应用并获取
     */
    public String apiSecret;

    private final OkHttpClient client = new OkHttpClient.Builder().build();
    public void chatStream(SparkRequest sparkRequest, SparkBaseListener listener) {
        sparkRequest.getHeader().setAppId(appid);
        listener.setSparkRequest(sparkRequest);

        SparkApiVersion apiVersion = sparkRequest.getApiVersion();
        String apiUrl = apiVersion.getUrl();

        // 构建鉴权url
        String authWsUrl = null;
        try {
            authWsUrl = getAuthUrl(apiUrl).replace("http://", "ws://").replace("https://", "wss://");
        } catch (Exception e) {
            throw new SparkException(500, "构建鉴权url失败", e);
        }
        // 创建请求
        Request request = new Request.Builder().url(authWsUrl).build();
        // 发送请求
        client.newWebSocket(request, listener);
    }

    public SparkSyncChatResponse chatSync(SparkRequest sparkRequest) {
        SparkSyncChatResponse chatResponse = new SparkSyncChatResponse();
        CountDownLatch latch = new CountDownLatch(1);
        chatResponse.setLatch(latch);

        SparkSyncChatListener syncChatListener = new SparkSyncChatListener(chatResponse);
        this.chatStream(sparkRequest, syncChatListener);

        try {
            latch.await(3, TimeUnit.SECONDS); // 最多等30秒
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new SparkException(500, "等待响应中断", e);
        }

        Throwable exception = chatResponse.getException();
        if (exception != null) {
            throw new SparkException(500, exception.getMessage(), exception);
        }
        return chatResponse;
    }

    /**
     * 获取认证之后的URL
     */
    public String getAuthUrl(String apiUrl) throws Exception {
        URL url = new URL(apiUrl);
        // 时间
        SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        String date = format.format(new Date());
        // 拼接
        String preStr = "host: " + url.getHost() + "\n" +
                "date: " + date + "\n" +
                "GET " + url.getPath() + " HTTP/1.1";
        // SHA256加密
        Mac mac = Mac.getInstance("hmacsha256");
        SecretKeySpec spec = new SecretKeySpec(apiSecret.getBytes(StandardCharsets.UTF_8), "hmacsha256");
        mac.init(spec);

        byte[] hexDigits = mac.doFinal(preStr.getBytes(StandardCharsets.UTF_8));
        // Base64加密
        String sha = Base64.getEncoder().encodeToString(hexDigits);

        // 拼接
        String authorization = String.format("api_key=\"%s\", algorithm=\"%s\", headers=\"%s\", signature=\"%s\"", apiKey, "hmac-sha256", "host date request-line", sha);
        // 拼接地址
        HttpUrl httpUrl = Objects.requireNonNull(HttpUrl.parse("https://" + url.getHost() + url.getPath())).newBuilder().//
                addQueryParameter("authorization", Base64.getEncoder().encodeToString(authorization.getBytes(StandardCharsets.UTF_8))).//
                addQueryParameter("date", date).//
                addQueryParameter("host", url.getHost()).//
                build();

        return httpUrl.toString();
    }
}