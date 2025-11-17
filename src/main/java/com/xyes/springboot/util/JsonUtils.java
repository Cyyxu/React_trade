package com.xyes.springboot.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * * @author xujun
 * @version 1.1
 * @since 2025/7/18
 */
public final class JsonUtils {
    
    private static final Logger log = LoggerFactory.getLogger(JsonUtils.class);

    /**
     * 默认ObjectMapper实例 - 线程安全
     */
    private static final ObjectMapper DEFAULT_MAPPER;

    /**
     * 包含null值的ObjectMapper实例
     */
    private static final ObjectMapper INCLUDE_NULL_MAPPER;

    /**
     * 美化格式的ObjectMapper实例
     */
    private static final ObjectMapper PRETTY_MAPPER;

    /**
     * 下划线命名的ObjectMapper实例
     */
    private static final ObjectMapper SNAKE_CASE_MAPPER;

    /**
     * 默认日期格式
     */
    private static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";

    static {
        // 初始化默认ObjectMapper
        DEFAULT_MAPPER = createDefaultObjectMapper();

        // 初始化包含null值的ObjectMapper
        INCLUDE_NULL_MAPPER = createDefaultObjectMapper();
        INCLUDE_NULL_MAPPER.setSerializationInclusion(JsonInclude.Include.ALWAYS);

        // 初始化美化格式的ObjectMapper
        PRETTY_MAPPER = createDefaultObjectMapper();
        PRETTY_MAPPER.enable(SerializationFeature.INDENT_OUTPUT);

        // 初始化下划线命名的ObjectMapper
        SNAKE_CASE_MAPPER = createDefaultObjectMapper();
        SNAKE_CASE_MAPPER.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
    }

    /**
     * 创建默认配置的ObjectMapper
     *
     * @return 配置好的ObjectMapper实例
     */
    private static ObjectMapper createDefaultObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        // 序列化配置
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL); // 忽略null值
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // 日期不序列化为时间戳
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS); // 忽略空Bean序列化错误

        // 反序列化配置
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES); // 忽略未知属性
        mapper.disable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES); // 允许基本类型为null
        mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT); // 空字符串作为null

        // 解析器配置
        mapper.enable(JsonParser.Feature.ALLOW_COMMENTS); // 允许注释
        mapper.enable(JsonParser.Feature.ALLOW_SINGLE_QUOTES); // 允许单引号
        mapper.enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES); // 允许无引号字段名

        // 注册Java8时间模块
        mapper.registerModule(new JavaTimeModule());

        // 设置默认时区和日期格式
        mapper.setTimeZone(TimeZone.getDefault());
        mapper.setDateFormat(new SimpleDateFormat(DEFAULT_DATE_PATTERN));

        return mapper;
    }

    // ==================== 基础序列化/反序列化方法 ====================

    /**
     * 将对象序列化为JSON字符串
     *
     * <p>这是最常用的序列化方法，适用于大部分场景。
     * 会忽略null值，使用默认的日期格式。</p>
     *
     * <p>业务场景示例：</p>
     * <pre>
     * // API响应序列化
     * ApiResponse response = new ApiResponse(200, "success", userData);
     * String json = JsonUtils.toJson(response);
     *
     * // 日志记录
     * log.info("用户操作记录: {}", JsonUtils.toJson(userAction));
     * </pre>
     *
     * @param obj 要序列化的对象
     * @return JSON字符串，序列化失败时返回null
     */
    public static String toJson(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return DEFAULT_MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("对象序列化为JSON失败: {}", obj.getClass().getSimpleName(), e);
            return null;
        }
    }

    /**
     * 将对象序列化为美化格式的JSON字符串
     *
     * <p>输出格式化的JSON，便于阅读和调试</p>
     *
     * @param obj 要序列化的对象
     * @return 美化格式的JSON字符串
     */
    public static String toPrettyJson(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return PRETTY_MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("对象序列化为美化JSON失败: {}", obj.getClass().getSimpleName(), e);
            return null;
        }
    }

    /**
     * 将对象序列化为包含null值的JSON字符串
     *
     * <p>包含所有字段，即使值为null</p>
     *
     * @param obj 要序列化的对象
     * @return 包含null值的JSON字符串
     */
    public static String toJsonWithNull(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return INCLUDE_NULL_MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("对象序列化为包含null的JSON失败: {}", obj.getClass().getSimpleName(), e);
            return null;
        }
    }

    /**
     * 将对象序列化为下划线命名的JSON字符串
     *
     * <p>Java驼峰命名转换为下划线命名，适配某些API规范</p>
     *
     * @param obj 要序列化的对象
     * @return 下划线命名的JSON字符串
     */
    public static String toSnakeCaseJson(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return SNAKE_CASE_MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("对象序列化为下划线JSON失败: {}", obj.getClass().getSimpleName(), e);
            return null;
        }
    }

    /**
     * 将JSON字符串反序列化为指定类型的对象
     *
     * <p>最常用的反序列化方法，支持各种Java对象类型</p>
     *
     * <p>业务场景示例：</p>
     * <pre>
     * // API请求解析
     * String requestJson = request.getBody();
     * UserRequest userReq = JsonUtils.fromJson(requestJson, UserRequest.class);
     *
     * // 配置文件解析
     * String configJson = Files.readString(configPath);
     * AppConfig config = JsonUtils.fromJson(configJson, AppConfig.class);
     *
     * </pre>
     *
     * @param json  JSON字符串
     * @param clazz 目标类型
     * @param <T>   泛型类型
     * @return 反序列化后的对象，失败时返回null
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        if (json != null && !json.trim().isEmpty()) {
            Objects.requireNonNull(clazz, "目标类型不能为空");
            try {
                return DEFAULT_MAPPER.readValue(json, clazz);
            } catch (JsonProcessingException e) {
                log.error("JSON反序列化失败 - 目标类型: {}, JSON: {}", clazz.getSimpleName(), json.length() > 200 ? json.substring(0, 200) + "..." : json, e);
            }
        }
        try {
            Constructor<T> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true); // 处理私有构造器
            return constructor.newInstance();
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException |
                 InstantiationException e) {
            return null;
        }
    }

    /**
     * 将JSON字符串反序列化为指定的泛型类型
     *
     * <p>处理复杂的泛型类型，如List&lt;User&gt;、Map&lt;String, List&lt;User&gt;&gt;等</p>
     *
     * <p>使用示例：</p>
     * <pre>
     * // 复杂泛型类型
     * TypeReference&lt;List&lt;User&gt;&gt; typeRef = new TypeReference&lt;List&lt;User&gt;&gt;() {};
     * List&lt;User&gt; users = JsonUtils.fromJson(json, typeRef);
     *
     * // 嵌套泛型类型
     * TypeReference&lt;Map&lt;String, List&lt;String&gt;&gt;&gt; typeRef =
     *     new TypeReference&lt;Map&lt;String, List&lt;String&gt;&gt;&gt;() {};
     * Map&lt;String, List&lt;String&gt;&gt; result = JsonUtils.fromJson(json, typeRef);
     * </pre>
     *
     * @param json          JSON字符串
     * @param typeReference 类型引用
     * @param <T>           泛型类型
     * @return 反序列化后的对象
     */
    public static <T> T fromJson(String json, TypeReference<T> typeReference, T defaultValue) {
        if (json == null || json.trim().isEmpty()) {
            log.debug("JSON字符串为空，无法反序列化");
            return defaultValue;
        }
        Objects.requireNonNull(typeReference, "类型引用不能为空");

        try {
            return DEFAULT_MAPPER.readValue(json, typeReference);
        } catch (JsonProcessingException e) {
            log.error("JSON反序列化失败 - 类型引用: {}, JSON长度: {}", typeReference.getType(), json.length(), e);
            return defaultValue;
        }
    }

    // ==================== 便捷方法 ====================

    /**
     * 将JSON字符串反序列化为List
     *
     * <p>专门处理List类型的反序列化，使用更简单</p>
     *
     * @param json         JSON字符串
     * @param elementClass List元素类型
     * @param <T>          元素泛型类型
     * @return List对象
     */
    public static <T> List<T> fromJsonToList(String json, Class<T> elementClass) {
        if (json == null || json.trim().isEmpty()) {
            return new ArrayList<>();
        }

        try {
            JavaType listType = SNAKE_CASE_MAPPER.getTypeFactory().constructCollectionType(List.class, elementClass);
            return SNAKE_CASE_MAPPER.readValue(json, listType);
        } catch (JsonProcessingException e) {
            log.error("JSON反序列化为List失败 - 元素类型: {}", elementClass.getSimpleName(), e);
            return new ArrayList<>();
        }
    }

    /**
     * 将JSON字符串反序列化为Map
     *
     * <p>返回Map&lt;String, Object&gt;类型，适用于动态JSON处理</p>
     *
     * @param json JSON字符串
     * @return Map对象
     */
    public static Map<String, Object> fromJsonToMap(String json) {
        if (json == null || json.trim().isEmpty()) {
            return new HashMap<>();
        }

        try {
            TypeReference<Map<String, Object>> typeRef = new TypeReference<>() {
            };
            return DEFAULT_MAPPER.readValue(json, typeRef);
        } catch (JsonProcessingException e) {
            log.error("JSON反序列化为Map失败", e);
            return new HashMap<>();
        }
    }

    /**
     * 将JSON字符串反序列化为指定类型的Map
     *
     * @param json       JSON字符串
     * @param valueClass Map值的类型
     * @param <T>        值的泛型类型
     * @return Map对象
     */
    public static <T> Map<String, T> fromJsonToMap(String json, Class<T> valueClass) {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }

        try {
            JavaType mapType = DEFAULT_MAPPER.getTypeFactory().constructMapType(Map.class, String.class, valueClass);
            return DEFAULT_MAPPER.readValue(json, mapType);
        } catch (JsonProcessingException e) {
            log.error("JSON反序列化为Map失败 - 值类型: {}", valueClass.getSimpleName(), e);
            return null;
        }
    }

    // ==================== 文件和流处理 ====================

    /**
     * 从InputStream反序列化对象
     *
     * <p>适用于文件读取、网络流等场景</p>
     *
     * @param inputStream 输入流
     * @param clazz       目标类型
     * @param <T>         泛型类型
     * @return 反序列化后的对象
     */
    public static <T> T fromJson(InputStream inputStream, Class<T> clazz) {
        Objects.requireNonNull(inputStream, "输入流不能为空");
        Objects.requireNonNull(clazz, "目标类型不能为空");

        try {
            return DEFAULT_MAPPER.readValue(inputStream, clazz);
        } catch (IOException e) {
            log.error("从输入流反序列化失败 - 目标类型: {}", clazz.getSimpleName(), e);
            return null;
        }
    }

    /**
     * 从InputStream反序列化为指定泛型类型
     *
     * @param inputStream   输入流
     * @param typeReference 类型引用
     * @param <T>           泛型类型
     * @return 反序列化后的对象
     */
    public static <T> T fromJson(InputStream inputStream, TypeReference<T> typeReference) {
        Objects.requireNonNull(inputStream, "输入流不能为空");
        Objects.requireNonNull(typeReference, "类型引用不能为空");

        try {
            return DEFAULT_MAPPER.readValue(inputStream, typeReference);
        } catch (IOException e) {
            log.error("从输入流反序列化失败 - 类型引用: {}", typeReference.getType(), e);
            return null;
        }
    }

    // ==================== JSON节点操作 ====================

    /**
     * 解析JSON字符串为JsonNode
     *
     * <p>用于动态JSON处理，无需预定义Java类</p>
     *
     * @param json JSON字符串
     * @return JsonNode对象
     */
    public static JsonNode parseToNode(String json) {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }

        try {
            return DEFAULT_MAPPER.readTree(json);
        } catch (JsonProcessingException e) {
            log.error("解析JSON为节点失败", e);
            return null;
        }
    }

    /**
     * 创建空的ObjectNode
     *
     * @return 新的ObjectNode实例
     */
    public static ObjectNode createObjectNode() {
        return DEFAULT_MAPPER.createObjectNode();
    }

    /**
     * 创建空的ArrayNode
     *
     * @return 新的ArrayNode实例
     */
    public static ArrayNode createArrayNode() {
        return DEFAULT_MAPPER.createArrayNode();
    }

    /**
     * 将JsonNode转换为指定类型的对象
     *
     * @param node  JSON节点
     * @param clazz 目标类型
     * @param <T>   泛型类型
     * @return 转换后的对象
     */
    public static <T> T nodeToObject(JsonNode node, Class<T> clazz) {
        if (node == null) {
            return null;
        }

        Objects.requireNonNull(clazz, "目标类型不能为空");

        try {
            return DEFAULT_MAPPER.treeToValue(node, clazz);
        } catch (JsonProcessingException e) {
            log.error("JsonNode转换为对象失败 - 目标类型: {}", clazz.getSimpleName(), e);
            return null;
        }
    }

    /**
     * 将对象转换为JsonNode
     *
     * @param obj 源对象
     * @return JsonNode对象
     */
    public static JsonNode objectToNode(Object obj) {
        if (obj == null) {
            return null;
        }

        return DEFAULT_MAPPER.valueToTree(obj);
    }

    // ==================== 工具方法 ====================

    /**
     * 验证字符串是否为有效的JSON格式
     *
     * @param json 待验证的字符串
     * @return 是否为有效JSON
     */
    public static boolean isValidJson(String json) {
        if (json == null || json.trim().isEmpty()) {
            return false;
        }

        try {
            DEFAULT_MAPPER.readTree(json);
            return true;
        } catch (JsonProcessingException e) {
            return false;
        }
    }

    /**
     * 深度复制对象（通过JSON序列化/反序列化）
     *
     * <p>注意：这种方式会丢失对象的类型信息和瞬态字段</p>
     *
     * @param obj   源对象
     * @param clazz 目标类型
     * @param <T>   泛型类型
     * @return 复制后的对象
     */
    public static <T> T deepCopy(Object obj, Class<T> clazz) {
        if (obj == null) {
            return null;
        }

        try {
            String json = DEFAULT_MAPPER.writeValueAsString(obj);
            return DEFAULT_MAPPER.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            log.error("深度复制对象失败 - 源类型: {}, 目标类型: {}", obj.getClass().getSimpleName(), clazz.getSimpleName(), e);
            return null;
        }
    }

    /**
     * 合并两个JSON对象
     *
     * <p>将source的字段合并到target中，source中的字段会覆盖target中的同名字段</p>
     *
     * @param target 目标JSON字符串
     * @param source 源JSON字符串
     * @return 合并后的JSON字符串
     */
    public static String mergeJson(String target, String source) {
        if (target == null || target.trim().isEmpty()) {
            return source;
        }
        if (source == null || source.trim().isEmpty()) {
            return target;
        }

        try {
            JsonNode targetNode = DEFAULT_MAPPER.readTree(target);
            JsonNode sourceNode = DEFAULT_MAPPER.readTree(source);

            if (targetNode.isObject() && sourceNode.isObject()) {
                ObjectNode result = (ObjectNode) targetNode;
                sourceNode.fields().forEachRemaining(entry -> result.set(entry.getKey(), entry.getValue()));
                return DEFAULT_MAPPER.writeValueAsString(result);
            }

            return source; // 如果不是对象类型，直接返回source

        } catch (JsonProcessingException e) {
            log.error("合并JSON失败", e);
            return target;
        }
    }

    /**
     * json格式化
     * @param json 需要被格式化的json
     * @return 格式化之后的json
     */
    public static String formatJson(String json) {
        String result = "";
        try {
            Object obj = DEFAULT_MAPPER.readValue(json, Object.class);
            result = PRETTY_MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("Json 格式化异常: {}", json, e);
            return result;
        }
        return result;
    }

    /**
     * 私有构造函数，防止实例化
     */
    private JsonUtils() {
        throw new UnsupportedOperationException("JsonUtils是工具类，不能被实例化");
    }
}