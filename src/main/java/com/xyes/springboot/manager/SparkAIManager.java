package com.xyes.springboot.manager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
public class SparkAIManager {
    // 讯飞星火API地址
    public static final String HOST_URL = "https://spark-api.xf-yun.com/v1/x1";
    public static final String DOMAIN = "x1";
    
    @Value("${spark.appid}")
    private String appid;
    
    @Value("${spark.api-key}")
    private String apiKey;
    
    @Value("${spark.api-secret}")
    private String apiSecret;

    private final Gson gson = new Gson();
    
    /**
     * 发送消息并获取AI响应
     * @param message 用户消息
     * @param timeout 超时时间（秒）
     * @return AI响应内容
     */
    public String sendMessageAndGetResponse(String message, int timeout) throws Exception {
        // 构建鉴权url
        System.out.println("开始构建WebSocket连接，API Key: " + (apiKey != null ? apiKey.substring(0, Math.min(10, apiKey.length())) + "..." : "null"));
        String authUrl = getAuthUrl(HOST_URL, apiKey, apiSecret);
        System.out.println("鉴权URL构建完成: " + authUrl.substring(0, Math.min(100, authUrl.length())) + "...");
        
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(timeout + 10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build();
        String url = authUrl.replace("http://", "ws://").replace("https://", "wss://");
        System.out.println("WebSocket URL: " + url.substring(0, Math.min(100, url.length())) + "...");
        Request request = new Request.Builder().url(url).build();
        
        CompletableFuture<String> responseFuture = new CompletableFuture<>();
        SparkAIWebSocketListener listener = new SparkAIWebSocketListener(responseFuture, message);
        System.out.println("正在建立WebSocket连接...");
        WebSocket webSocket = client.newWebSocket(request, listener);
        System.out.println("WebSocket对象已创建");
        
        try {
            // 等待响应或超时
            System.out.println("等待AI响应，超时时间: " + timeout + "秒");
            String response = responseFuture.get(timeout, TimeUnit.SECONDS);
            if (response == null || response.trim().isEmpty()) {
                throw new RuntimeException("AI服务返回空响应");
            }
            System.out.println("AI响应接收成功，长度: " + response.length());
            return response;
        } catch (java.util.concurrent.TimeoutException e) {
            System.err.println("WebSocket响应超时，关闭连接");
            webSocket.close(1000, "Timeout");
            throw new RuntimeException("AI服务调用超时（" + timeout + "秒）", e);
        } catch (Exception e) {
            System.err.println("WebSocket调用异常: " + e.getMessage());
            webSocket.close(1000, "Error");
            throw new RuntimeException("AI服务调用异常: " + e.getMessage(), e);
        }
    }
    
    /**
     * 异步发送消息并获取AI响应
     * @param message 用户消息
     * @return CompletableFuture包装的AI响应
     */
    public CompletableFuture<String> sendMessageAsync(String message) {
        CompletableFuture<String> future = new CompletableFuture<>();
        try {
            // 构建鉴权url
            String authUrl = getAuthUrl(HOST_URL, apiKey, apiSecret);
            OkHttpClient client = new OkHttpClient.Builder().build();
            String url = authUrl.replace("http://", "ws://").replace("https://", "wss://");
            Request request = new Request.Builder().url(url).build();
            
            WebSocket webSocket = client.newWebSocket(request, new SparkAIWebSocketListener(future, message));
        } catch (Exception e) {
            future.completeExceptionally(e);
        }
        return future;
    }

    // 鉴权方法
    public static String getAuthUrl(String hostUrl, String apiKey, String apiSecret) throws Exception {
        URL url = new URL(hostUrl);
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
        HttpUrl httpUrl = Objects.requireNonNull(HttpUrl.parse("https://" + url.getHost() + url.getPath())).newBuilder()
                .addQueryParameter("authorization", Base64.getEncoder().encodeToString(authorization.getBytes(StandardCharsets.UTF_8)))
                .addQueryParameter("date", date)
                .addQueryParameter("host", url.getHost())
                .build();

        return httpUrl.toString();
    }
    
    /**
     * WebSocket监听器内部类
     */
    private class SparkAIWebSocketListener extends WebSocketListener {
        private final CompletableFuture<String> responseFuture;
        private final String userMessage;
        private final StringBuilder totalAnswer = new StringBuilder();

        public SparkAIWebSocketListener(CompletableFuture<String> responseFuture, String userMessage) {
            this.responseFuture = responseFuture;
            this.userMessage = userMessage;
        }

        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            super.onOpen(webSocket, response);
            System.out.println("WebSocket连接已建立，状态码: " + (response != null ? response.code() : "null"));
            // 发送请求数据
            JSONObject requestJson = buildRequestJson(userMessage);
            System.out.println("发送WebSocket请求，消息长度: " + userMessage.length());
            webSocket.send(requestJson.toString());
        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
            try {
                JsonParse myJsonParse = gson.fromJson(text, JsonParse.class);
                if (myJsonParse == null || myJsonParse.header == null) {
                    responseFuture.completeExceptionally(
                        new RuntimeException("AI服务返回格式错误，无法解析响应"));
                    webSocket.close(1000, "");
                    return;
                }
                
                if (myJsonParse.header.code != 0) {
                    responseFuture.completeExceptionally(
                        new RuntimeException("AI服务错误，错误码：" + myJsonParse.header.code));
                    webSocket.close(1000, "");
                    return;
                }
                
                if (myJsonParse.payload != null && 
                    myJsonParse.payload.choices != null && 
                    myJsonParse.payload.choices.text != null) {
                    List<Text> textList = myJsonParse.payload.choices.text;
                    for (Text temp : textList) {
                        if (temp != null && temp.content != null) {
                            totalAnswer.append(temp.content);
                        }
                    }
                }
                
                if (myJsonParse.header.status == 2) {
                    // 最后一条结果，完成响应
                    String finalAnswer = totalAnswer.toString();
                    if (finalAnswer.isEmpty()) {
                        responseFuture.completeExceptionally(
                            new RuntimeException("AI服务返回空内容"));
                    } else {
                        responseFuture.complete(finalAnswer);
                    }
                    webSocket.close(1000, "");
                }
            } catch (Exception e) {
                responseFuture.completeExceptionally(
                    new RuntimeException("解析AI响应失败: " + e.getMessage(), e));
                webSocket.close(1000, "");
            }
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
            super.onFailure(webSocket, t, response);
            String errorMsg = "WebSocket连接失败";
            if (response != null) {
                errorMsg += "，状态码：" + response.code();
                try {
                    if (response.body() != null) {
                        String body = response.body().string();
                        errorMsg += "，错误信息：" + body;
                    }
                } catch (IOException e) {
                    errorMsg += "，读取错误信息失败：" + e.getMessage();
                }
            }
            if (t != null) {
                errorMsg += "，异常：" + t.getMessage();
            }
            RuntimeException exception = new RuntimeException(errorMsg, t);
            responseFuture.completeExceptionally(exception);
        }
        
        /**
         * 构建请求JSON
         */
        private JSONObject buildRequestJson(String message) {
            JSONObject requestJson = new JSONObject();

            JSONObject header = new JSONObject();
            header.put("app_id", appid);
            header.put("uid", UUID.randomUUID().toString().substring(0, 10));

            JSONObject parameter = new JSONObject();
            JSONObject chat = new JSONObject();
            chat.put("domain", DOMAIN);
            chat.put("temperature", 0.5);
            chat.put("max_tokens", 4096);
            parameter.put("chat", chat);

            JSONObject payload = new JSONObject();
            JSONObject messageObj = new JSONObject();
            JSONArray text = new JSONArray();

            // 用户消息
            RoleContent roleContent = new RoleContent();
            roleContent.role = "user";
            roleContent.content = message;
            text.add(JSON.toJSON(roleContent));

            messageObj.put("text", text);
            payload.put("message", messageObj);

            requestJson.put("header", header);
            requestJson.put("parameter", parameter);
            requestJson.put("payload", payload);
            
            return requestJson;
        }
    }

    // 返回的json结果拆解
    private static class JsonParse {
        Header header;
        Payload payload;
    }

    private static class Header {
        int code;
        int status;
        String sid;
    }

    private static class Payload {
        Choices choices;
    }

    private static class Choices {
        List<Text> text;
    }

    private static class Text {
        String role;
        String content;
    }
    
    private static class RoleContent {
        String role;
        String content;

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}
