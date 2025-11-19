package com.xyes.springboot.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.StopWatch;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xyes.springboot.exception.BusinessException;
import com.xyes.springboot.exception.ErrorCode;
import com.xyes.springboot.constant.CommonConstant;
import com.xyes.springboot.exception.ThrowUtils;
import com.xyes.springboot.manager.SparkAIManager;
import com.xyes.springboot.manager.SparkClient;
import com.xyes.springboot.manager.constant.SparkApiVersion;
import com.xyes.springboot.manager.exception.SparkException;
import com.xyes.springboot.manager.model.SparkMessage;
import com.xyes.springboot.manager.model.SparkSyncChatResponse;
import com.xyes.springboot.manager.model.request.SparkRequest;
import com.xyes.springboot.mapper.UserAiMessageMapper;
import com.xyes.springboot.model.dto.userAiMessage.*;
import com.xyes.springboot.model.entity.Commodity;
import com.xyes.springboot.model.entity.User;
import com.xyes.springboot.model.entity.UserAiMessage;
import com.xyes.springboot.model.vo.UserAiMessageVO;
import com.xyes.springboot.service.CommodityService;
import com.xyes.springboot.service.UserAiMessageService;
import com.xyes.springboot.service.UserService;
import com.xyes.springboot.util.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * 用户对话表服务实现
 *
 */
@Service
@Slf4j
public class UserAiMessageServiceImpl extends ServiceImpl<UserAiMessageMapper, UserAiMessage> implements UserAiMessageService {

    private final UserService userService;
    private final ThreadPoolTaskExecutor threadPoolExecutor;
    private final CommodityService commodityService;
    private final SparkAIManager sparkAIManager;
    private final com.xyes.springboot.manager.GeminiAIManager geminiAIManager;

    public UserAiMessageServiceImpl(UserService userService,
                                     @Qualifier("businessAsyncExecutor") ThreadPoolTaskExecutor threadPoolExecutor,
                                     CommodityService commodityService,
                                     SparkAIManager sparkAIManager,
                                     com.xyes.springboot.manager.GeminiAIManager geminiAIManager) {
        this.userService = userService;
        this.threadPoolExecutor = threadPoolExecutor;
        this.commodityService = commodityService;
        this.sparkAIManager = sparkAIManager;
        this.geminiAIManager = geminiAIManager;
    }

    @Value("${spark.appid}")
    private String appid;

    @Value("${spark.api-key}")
    private String apiKey;

    @Value("${spark.api-secret}")
    private String apiSecret;

    private SparkClient sparkClient;

    @PostConstruct
    public void initSparkClient() {
        sparkClient = new SparkClient();
        sparkClient.appid = appid;
        sparkClient.apiKey = apiKey;
        sparkClient.apiSecret = apiSecret;
    }

    /**
     * 校验数据
     * @param userAiMessage
     * @param add           对创建的数据进行校验
     */
    @Override
    public void validUserAiMessage(UserAiMessage userAiMessage, boolean add) {
        ThrowUtils.throwIf(userAiMessage == null, ErrorCode.PARAMS_ERROR);

        String userInputText = userAiMessage.getUserInputText();
        String aiGenerateText = userAiMessage.getAiGenerateText();
        Long userId = userAiMessage.getUserId();
        // 创建数据时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(StringUtils.isBlank(userInputText), ErrorCode.PARAMS_ERROR);
            ThrowUtils.throwIf(userId < 0, ErrorCode.PARAMS_ERROR);
        }else{
            // 修改数据时，有参数则校验
            ThrowUtils.throwIf(userId == null || userId < 0, ErrorCode.PARAMS_ERROR, "用户 ID 错误");
            ThrowUtils.throwIf(StringUtils.isBlank(aiGenerateText), ErrorCode.PARAMS_ERROR);
        }

    }

    /**
     * 获取查询条件
     *
     * @param userAiMessageQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<UserAiMessage> getQueryWrapper(UserAiMessageQueryRequest userAiMessageQueryRequest) {
        QueryWrapper<UserAiMessage> queryWrapper = new QueryWrapper<>();
        if (userAiMessageQueryRequest == null) {
            return queryWrapper;
        }
        Long id = userAiMessageQueryRequest.getId();
        String userInputText = userAiMessageQueryRequest.getUserInputText();
        String aiGenerateText = userAiMessageQueryRequest.getAiGenerateText();
        Long userId = userAiMessageQueryRequest.getUserId();
        String sortField = userAiMessageQueryRequest.getSortField();
        String sortOrder = userAiMessageQueryRequest.getSortOrder();
        // 模糊查询
        queryWrapper.like(StringUtils.isNotBlank(userInputText), "userInputText", userInputText);
        queryWrapper.like(StringUtils.isNotBlank(aiGenerateText), "aiGenerateText", aiGenerateText);
        // 精确查询
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        // 排序规则
        queryWrapper.orderBy(SqlUtils.validSortField(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    /**
     * 获取用户对话表封装
     *
     * @param userAiMessage
     * @param request
     * @return
     */
    @Override
    public UserAiMessageVO getUserAiMessageVO(UserAiMessage userAiMessage, HttpServletRequest request) {
        return UserAiMessageVO.objToVo(userAiMessage);
    }

    /**
     * 分页获取用户对话表封装
     *
     * @param userAiMessagePage
     * @param request
     * @return
     */
    @Override
    public Page<UserAiMessageVO> getUserAiMessageVOPage(Page<UserAiMessage> userAiMessagePage, HttpServletRequest request) {
        List<UserAiMessage> userAiMessageList = userAiMessagePage.getRecords();
        Page<UserAiMessageVO> userAiMessageVOPage = new Page<>(userAiMessagePage.getCurrent(), userAiMessagePage.getSize(), userAiMessagePage.getTotal());
        if (CollUtil.isEmpty(userAiMessageList)) {
            return userAiMessageVOPage;
        }
        // 对象列表 => 封装对象列表
        List<UserAiMessageVO> userAiMessageVOList = userAiMessageList.stream().map(UserAiMessageVO::objToVo).collect(Collectors.toList());
        userAiMessageVOPage.setRecords(userAiMessageVOList);
        return userAiMessageVOPage;
    }

    /**
     * 创建用户对话（包含AI服务调用）
     *
     * @param userAiMessageAddRequest
     * @param request
     * @return
     */
    @Override
    public UserAiMessage addUserAiMessage(UserAiMessageAddRequest userAiMessageAddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(userAiMessageAddRequest == null, ErrorCode.PARAMS_ERROR);
        String userInputText = userAiMessageAddRequest.getUserInputText();
        ThrowUtils.throwIf(StringUtils.isBlank(userInputText), ErrorCode.PARAMS_ERROR, "用户输入不能为空");
        
        User loginUser = userService.getLoginUser(request);
        log.info("用户 {} 开始调用AI服务，输入内容: {}", loginUser.getId(), userInputText);
        
        UserAiMessage userAiMessage = new UserAiMessage();
        userAiMessage.setUserId(loginUser.getId());
        userAiMessage.setUserInputText(userInputText);
        
        try {
            // 构建AI提示词
            String prompt = buildAIPrompt(userInputText);
            // 调用AI服务
            String response = callAIService(prompt);
            // 处理AI响应
            String processedResponse = processAIResponse(response);
            // 设置AI生成内容
            userAiMessage.setAiGenerateText(processedResponse);
            // 校验数据
            validUserAiMessage(userAiMessage, true);
            // 插入数据库
            boolean result = this.save(userAiMessage);
            ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
            return this.getById(userAiMessage.getId());
        } catch (BusinessException e) {
            log.error("业务异常: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("AI服务调用异常", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "AI服务调用失败: " + e.getMessage());
        }
    }

    /**
     * 构建AI提示词
     * @param userInputText 用户输入
     * @return 完整的提示词
     */
    private String buildAIPrompt(String userInputText) {
        String presetInformation = "你是一个二手商品交易推荐官，你需要根据数据库的商品名称、价格、新旧程度、库存、用户的偏好的多方面进行适配性推荐，并给出相关的理由。\n";
        String userText = "用户偏好信息：" + userInputText + "\n";
        
        // 优化：使用数据库查询，直接在SQL层面过滤和排序，减少数据传输量
        // 减少商品数量从50到25，降低提示词长度，提升AI处理速度
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Commodity> queryWrapper = 
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        queryWrapper.eq("isListed", 1) // 只查询已上架的商品
                .orderByDesc("id") // 按ID倒序，获取最新商品
                .last("LIMIT 25"); // 限制25个商品，平衡推荐质量和响应速度
        List<Commodity> commodities = commodityService.list(queryWrapper);
        
        // 优化：简化商品信息格式，减少提示词长度
        // 格式：商品名|新旧程度|库存|价格（整数）
        String commodityList = commodities.stream()
                .map(commodity -> String.format("%s|%s|%d|%.0f",
                        commodity.getCommodityName(),
                        commodity.getDegree(),
                        commodity.getCommodityInventory(),
                        commodity.getPrice()))
                .collect(Collectors.joining("\n"));
        
        String commodityInfo = "数据库商品信息（格式：商品名|新旧程度|库存|价格）：\n" + commodityList + "\n";
        
        return presetInformation + userText + commodityInfo;
    }

    /**
     * 调用AI服务
     * @param prompt 提示词
     * @return AI响应
     * @throws Exception 调用异常
     */
    private String callAIService(String prompt) throws Exception {

        // 优先级：1. Gemini -> 2. SparkAIManager -> 3. SparkClient
        
        // 首先尝试使用Gemini（如果已配置）
        if (geminiAIManager.isAvailable()) {
            try {
                log.info("优先使用Gemini调用AI服务，提示词长度: {}", prompt.length());
                // Gemini响应很快，超时时间设置为30秒即可
                String response = geminiAIManager.sendMessageAndGetResponse(prompt, 30);
                if (response != null && !response.trim().isEmpty()) {
                    log.info("Gemini调用成功，响应长度: {}", response.length());
                    return response;
                }
                log.warn("Gemini返回空响应，尝试使用SparkAIManager");
            } catch (Exception e) {
                log.warn("Gemini调用失败，错误信息: {}，尝试使用SparkAIManager", e.getMessage());
            }
        } else {
            log.info("Gemini未配置，跳过Gemini调用，使用SparkAIManager");
        }
        
        // 回退到SparkAIManager
        return trySparkAIManagerOrSparkClient(prompt);
    }
    
    /**
     * 尝试使用SparkAIManager或SparkClient作为备选方案
     * @param prompt 提示词
     * @return AI响应
     * @throws Exception 调用异常
     */
    private String trySparkAIManagerOrSparkClient(String prompt) throws Exception {
        // 尝试使用SparkAIManager
        try {
            log.info("尝试使用SparkAIManager调用AI服务，提示词长度: {}", prompt.length());
            String response = sparkAIManager.sendMessageAndGetResponse(prompt, 120);
            if (response != null && !response.trim().isEmpty()) {
                log.info("SparkAIManager调用成功，响应长度: {}", response.length());
                return response;
            }
            log.warn("SparkAIManager返回空响应，回退到SparkClient");
        } catch (Exception e) {
            log.error("SparkAIManager调用失败，错误信息: {}，回退到SparkClient", e.getMessage());
        }
        
        // 最后回退到SparkClient
        try {
            log.info("回退到SparkClient调用AI服务，提示词长度: {}", prompt.length());
            String fallbackResponse = callAIServiceWithSparkClient(prompt);
            log.info("SparkClient回退调用成功，响应长度: {}", fallbackResponse != null ? fallbackResponse.length() : 0);
            return fallbackResponse;
        } catch (Exception fallbackException) {
            log.error("SparkClient回退调用也失败，错误信息: {}", fallbackException.getMessage(), fallbackException);
            throw new RuntimeException("所有AI服务调用均失败，最后错误: " + fallbackException.getMessage(), fallbackException);
        }
    }

    /**
     * 使用SparkClient调用AI服务（回退方案）
     * @param prompt 提示词
     * @return AI响应
     * @throws Exception 调用异常
     */
    private String callAIServiceWithSparkClient(String prompt) throws Exception {
        log.info("开始使用SparkClient调用AI服务");
        List<SparkMessage> messages = new ArrayList<>();
        messages.add(SparkMessage.userContent(prompt));
        
        // 构造请求
        SparkRequest sparkRequest = SparkRequest.builder()
                .maxTokens(2048)
                .messages(messages)
                .temperature(0.2)
                .apiVersion(SparkApiVersion.X1)
                .build();
                
        Future<String> future = threadPoolExecutor.submit(() -> {
            try {
                log.info("SparkClient开始同步调用");
                // 同步调用
                StopWatch stopWatch = new StopWatch();
                stopWatch.start();
                SparkSyncChatResponse chatResponse = sparkClient.chatSync(sparkRequest);
                stopWatch.stop();
                log.info("SparkClient调用完成，耗时: {}ms", stopWatch.getTotalTimeMillis());
                String content = chatResponse.getContent();
                if (content == null || content.trim().isEmpty()) {
                    log.warn("SparkClient返回空内容");
                    throw new RuntimeException("SparkClient返回空内容");
                }
                return content;
            } catch (SparkException e) {
                log.error("SparkClient调用异常，错误码: {}, 错误信息: {}", e.getCode(), e.getMessage());
                if (e.getCode() != null && (e.getCode() == 11200 || e.getCode() == 11201 || 
                                          e.getCode() == 11202 || e.getCode() == 11203)) {
                    throw new RuntimeException("AI服务当前不可用，可能是由于授权问题或使用量超限。请稍后再试或联系管理员。");
                }
                throw new RuntimeException("AI服务调用失败: " + e.getMessage(), e);
            } catch (Exception exception) {
                log.error("SparkClient调用遇到异常: {}", exception.getMessage(), exception);
                throw new RuntimeException("遇到异常: " + exception.getMessage(), exception);
            }
        });

        try {
            // 增加超时时间到120秒
            log.info("等待SparkClient响应，超时时间: 120秒");
            String result = future.get(120, TimeUnit.SECONDS);
            log.info("SparkClient响应成功，内容长度: {}", result != null ? result.length() : 0);
            return result;
        } catch (TimeoutException e) {
            log.error("SparkClient调用超时（120秒）");
            future.cancel(true);
            throw new RuntimeException("AI服务调用超时（120秒），请稍后再试", e);
        } catch (ExecutionException e) {
            log.error("SparkClient执行异常: {}", e.getMessage(), e);
            future.cancel(true);
            if (e.getCause() instanceof SparkException sparkException) {
                log.error("SparkClient SparkException，错误码: {}", sparkException.getCode());
                if (sparkException.getCode() != null &&
                    (sparkException.getCode() == 11200 || 
                     sparkException.getCode() == 11201 || 
                     sparkException.getCode() == 11202 || 
                     sparkException.getCode() == 11203)) {
                    throw new RuntimeException("AI服务授权错误，请联系管理员", sparkException);
                }
            }
            Throwable cause = e.getCause();
            throw new RuntimeException("AI服务调用失败: " + (cause != null ? cause.getMessage() : e.getMessage()), cause != null ? cause : e);
        } catch (Exception e) {
            log.error("SparkClient调用异常: {}", e.getMessage(), e);
            future.cancel(true);
            throw new RuntimeException("AI服务调用异常: " + e.getMessage(), e);
        }
    }

    /**
     * 处理AI响应内容
     * @param response AI响应
     * @return 处理后的内容
     */
    private String processAIResponse(String response) {
        // 清理AI生成文本中的null字符串
        if (response != null) {
            response = response.replaceAll("null", "");
            // 如果清理后为空，则设置默认值
            if (response.trim().isEmpty()) {
                response = "AI生成内容出现异常，请重新尝试";
            }
        } else {
            response = "AI生成内容为空，请重新尝试";
        }
        return response;
    }

    /**
     * 删除用户对话（包含权限校验）
     *
     * @param id
     * @param request
     * @return
     */
    @Override
    public Boolean deleteUserAiMessageById(Long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR);
        
        // 获取当前登录用户
        User user = userService.getLoginUser(request);
        
        // 判断对话是否存在
        UserAiMessage oldUserAiMessage = this.getById(id);
        ThrowUtils.throwIf(oldUserAiMessage == null, ErrorCode.NOT_FOUND_ERROR);
        
        // 仅本人或管理员可删除
        if (!oldUserAiMessage.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        
        // 删除数据库记录
        boolean result = this.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        
        return true;
    }

    /**
     * 更新用户对话（仅管理员）
     *
     * @param userAiMessageUpdateRequest
     * @return
     */
    @Override
    public Boolean updateUserAiMessageById(UserAiMessageUpdateRequest userAiMessageUpdateRequest) {
        ThrowUtils.throwIf(userAiMessageUpdateRequest == null || userAiMessageUpdateRequest.getId() <= 0, ErrorCode.PARAMS_ERROR);
        
        // DTO转实体
        UserAiMessage userAiMessage = new UserAiMessage();
        BeanUtils.copyProperties(userAiMessageUpdateRequest, userAiMessage);
        
        // 数据校验
        validUserAiMessage(userAiMessage, false);
        
        // 判断是否存在
        long id = userAiMessageUpdateRequest.getId();
        UserAiMessage oldUserAiMessage = this.getById(id);
        ThrowUtils.throwIf(oldUserAiMessage == null, ErrorCode.NOT_FOUND_ERROR);
        
        // 更新数据库
        boolean result = this.updateById(userAiMessage);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        
        return true;
    }

    /**
     * 编辑用户对话（用户自己可用）
     *
     * @param userAiMessageEditRequest
     * @param request
     * @return
     */
    @Override
    public Boolean editUserAiMessageById(UserAiMessageEditRequest userAiMessageEditRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(userAiMessageEditRequest == null || userAiMessageEditRequest.getId() <= 0, ErrorCode.PARAMS_ERROR);
        
        // DTO转实体
        UserAiMessage userAiMessage = new UserAiMessage();
        BeanUtils.copyProperties(userAiMessageEditRequest, userAiMessage);
        
        // 数据校验
        validUserAiMessage(userAiMessage, false);
        
        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        
        // 判断是否存在
        long id = userAiMessageEditRequest.getId();
        UserAiMessage oldUserAiMessage = this.getById(id);
        ThrowUtils.throwIf(oldUserAiMessage == null, ErrorCode.NOT_FOUND_ERROR);
        
        // 仅本人或管理员可编辑
        if (!oldUserAiMessage.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        
        // 更新数据库
        boolean result = this.updateById(userAiMessage);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        
        return true;
    }

    /**
     * 根据 id 获取用户对话（封装类）
     *
     * @param id
     * @param request
     * @return
     */
    @Override
    public UserAiMessageVO getUserAiMessageVOById(Long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        UserAiMessage userAiMessage = this.getById(id);
        ThrowUtils.throwIf(userAiMessage == null, ErrorCode.NOT_FOUND_ERROR);
        // 获取封装类
        return this.getUserAiMessageVO(userAiMessage, request);
    }

    /**
     * 分页获取用户对话列表（实体类）
     *
     * @param userAiMessageQueryRequest
     * @return
     */
    @Override
    public Page<UserAiMessage> listUserAiMessageByPage(UserAiMessageQueryRequest userAiMessageQueryRequest) {
        long current = userAiMessageQueryRequest.getCurrent();
        long size = userAiMessageQueryRequest.getPageSize();
        return this.page(new Page<>(current, size),
                this.getQueryWrapper(userAiMessageQueryRequest));
    }

    /**
     * 分页获取用户对话列表（封装类）
     *
     * @param userAiMessageQueryRequest
     * @param request
     * @return
     */
    @Override
    public Page<UserAiMessageVO> listUserAiMessageVOByPage(UserAiMessageQueryRequest userAiMessageQueryRequest, HttpServletRequest request) {
        long current = userAiMessageQueryRequest.getCurrent();
        long size = userAiMessageQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 2000, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<UserAiMessage> userAiMessagePage = this.page(new Page<>(current, size),
                this.getQueryWrapper(userAiMessageQueryRequest));
        // 获取封装类
        return this.getUserAiMessageVOPage(userAiMessagePage, request);
    }

    /**
     * 分页获取当前用户的对话列表
     *
     * @param userAiMessageQueryRequest
     * @param request
     * @return
     */
    @Override
    public Page<UserAiMessageVO> getMyUserAiMessageVOPage(UserAiMessageQueryRequest userAiMessageQueryRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(userAiMessageQueryRequest == null, ErrorCode.PARAMS_ERROR);
        
        // 获取当前登录用户并设置查询条件
        User loginUser = userService.getLoginUser(request);
        userAiMessageQueryRequest.setUserId(loginUser.getId());
        
        long current = userAiMessageQueryRequest.getCurrent();
        long size = userAiMessageQueryRequest.getPageSize();
        
        // 限制爬虫
        ThrowUtils.throwIf(size > 2000, ErrorCode.PARAMS_ERROR);
        
        // 查询数据库
        Page<UserAiMessage> userAiMessagePage = this.page(
                new Page<>(current, size),
                this.getQueryWrapper(userAiMessageQueryRequest)
        );
        
        // 获取封装类
        return this.getUserAiMessageVOPage(userAiMessagePage, request);
    }

}
