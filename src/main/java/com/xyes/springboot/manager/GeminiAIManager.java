package com.xyes.springboot.manager;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Google Gemini AI 管理器
 * 使用 Gemini API 进行对话
 */
@Service
@Slf4j
public class GeminiAIManager {
    
    @Value("${gemini.api-key:}")
    private String apiKey;
    
    @Value("${gemini.model:gemini-2.0-flash}")
    private String model;
    
    @Value("${gemini.api-url:https://generativelanguage.googleapis.com/v1beta/models}")
    private String apiUrl;
    
    private final Gson gson = new Gson();
    private final OkHttpClient httpClient;
    
    public GeminiAIManager() {
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)  // 连接超时5秒
                .readTimeout(30, TimeUnit.SECONDS)    // 读取超时30秒（Gemini响应很快）
                .writeTimeout(5, TimeUnit.SECONDS)    // 写入超时5秒
                .build();
    }
    
    /**
     * 发送消息并获取AI响应
     * @param message 用户消息
     * @param timeout 超时时间（秒）
     * @return AI响应内容
     * @throws Exception 调用异常
     */
    public String sendMessageAndGetResponse(String message, int timeout) throws Exception {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new RuntimeException("Gemini API Key 未配置，请在 application.yml 中配置 gemini.api-key");
        }
        
        // 验证 API Key 格式（Google API Key 通常以 AIza 开头）
        String trimmedKey = apiKey.trim();
        if (!trimmedKey.startsWith("AIza") && !trimmedKey.startsWith("Alza")) {
            log.warn("Gemini API Key 格式可能不正确，通常应以 'AIza' 开头，当前: {}", 
                    trimmedKey.length() > 10 ? trimmedKey.substring(0, 10) + "..." : trimmedKey);
        }
        
        log.info("使用 Gemini API 调用AI服务，模型: {}, 提示词长度: {}, API Key 前缀: {}", 
                model, message.length(), 
                trimmedKey.length() > 10 ? trimmedKey.substring(0, 10) + "..." : trimmedKey);
        
        // 构建请求URL（不使用 key 参数，改用请求头）
        String url = String.format("%s/%s:generateContent", apiUrl, model);
        
        // 构建请求体
        GeminiRequest request = new GeminiRequest();
        GeminiContent content = new GeminiContent();
        GeminiPart part = new GeminiPart();
        part.text = message;
        content.parts = new GeminiPart[]{part};
        request.contents = new GeminiContent[]{content};
        
        // 设置生成参数
        request.generationConfig = new GenerationConfig();
        request.generationConfig.temperature = 0.7;
        request.generationConfig.maxOutputTokens = 2048;
        request.generationConfig.topP = 0.8;
        request.generationConfig.topK = 40;
        
        String requestBody = gson.toJson(request);
        log.debug("Gemini 请求URL: {}", url);
        log.debug("Gemini 请求体: {}", requestBody);
        
        // 创建HTTP请求（使用请求头传递 API Key，更安全）
        Request httpRequest = new Request.Builder()
                .url(url)
                .header("Content-Type", "application/json")
                .header("X-goog-api-key", apiKey)  // 使用请求头传递 API Key
                .post(RequestBody.create(requestBody, MediaType.parse("application/json; charset=utf-8")))
                .build();
        
        try (Response response = httpClient.newCall(httpRequest).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "未知错误";
                log.error("Gemini API 调用失败，状态码: {}, 错误信息: {}", response.code(), errorBody);
                
                // 解析错误信息，提供更友好的提示
                String errorMessage = "Gemini API 调用失败";
                if (errorBody.contains("API key not valid") || errorBody.contains("API_KEY_INVALID")) {
                    errorMessage = "Gemini API Key 无效，请检查配置是否正确。错误详情: " + response.code();
                } else if (errorBody.contains("API key not found")) {
                    errorMessage = "Gemini API Key 未找到，请检查配置";
                } else if (errorBody.contains("PERMISSION_DENIED")) {
                    errorMessage = "Gemini API 权限被拒绝，请检查 API Key 权限";
                } else {
                    errorMessage = "Gemini API 调用失败: " + response.code() + " - " + errorBody;
                }
                
                throw new RuntimeException(errorMessage);
            }
            
            String responseBody = response.body().string();
            log.debug("Gemini 响应体: {}", responseBody);
            
            GeminiResponse geminiResponse = gson.fromJson(responseBody, GeminiResponse.class);
            
            if (geminiResponse.candidates == null || geminiResponse.candidates.length == 0) {
                log.warn("Gemini API 返回空响应");
                throw new RuntimeException("Gemini API 返回空响应");
            }
            
            GeminiCandidate candidate = geminiResponse.candidates[0];
            if (candidate.content == null || candidate.content.parts == null || candidate.content.parts.length == 0) {
                log.warn("Gemini API 返回内容为空");
                throw new RuntimeException("Gemini API 返回内容为空");
            }
            
            String result = candidate.content.parts[0].text;
            if (result == null || result.trim().isEmpty()) {
                throw new RuntimeException("Gemini API 返回文本为空");
            }
            
            log.info("Gemini API 调用成功，响应长度: {}", result.length());
            return result;
            
        } catch (IOException e) {
            log.error("Gemini API 网络请求异常: {}", e.getMessage(), e);
            throw new RuntimeException("Gemini API 网络请求异常: " + e.getMessage(), e);
        }
    }
    
    /**
     * 检查 Gemini API 是否可用
     * @return 是否可用
     */
    public boolean isAvailable() {
        return apiKey != null && !apiKey.trim().isEmpty();
    }
    
    // Gemini API 请求和响应模型
    private static class GeminiRequest {
        GeminiContent[] contents;
        GenerationConfig generationConfig;
    }
    
    private static class GeminiContent {
        GeminiPart[] parts;
    }
    
    private static class GeminiPart {
        String text;
    }
    
    private static class GenerationConfig {
        double temperature;
        int maxOutputTokens;
        double topP;
        int topK;
    }
    
    private static class GeminiResponse {
        GeminiCandidate[] candidates;
        PromptFeedback promptFeedback;
    }
    
    private static class GeminiCandidate {
        GeminiContent content;
        String finishReason;
        int index;
    }
    
    private static class PromptFeedback {
        String blockReason;
    }
}

