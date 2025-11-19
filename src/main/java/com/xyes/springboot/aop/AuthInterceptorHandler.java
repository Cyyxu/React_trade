package com.xyes.springboot.aop;

import com.alibaba.fastjson.JSONObject;
import com.xyes.springboot.common.JwtKit;
import com.xyes.springboot.common.JwtProperties;
import com.xyes.springboot.constant.CommonConstant;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * JWT认证拦截器
 * 在请求处理前进行JWT Token验证，确保请求的合法性
 * <p>
 * 功能说明：
 * <ul>
 *   <li>验证请求头中的JWT Token</li>
 *   <li>解析Token并验证其有效性</li>
 *   <li>对特定路径（如SSE、Swagger）进行放行</li>
 *   <li>处理OPTIONS预检请求</li>
 * </ul>
 *
 * @author xujun
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthInterceptorHandler implements HandlerInterceptor {

    private final JwtProperties jwtProperties;

    private final JwtKit jwtKit;

    /**
     * 前置拦截器
     * 在Controller方法执行前进行JWT Token验证
     *
     * @param request HTTP请求
     * @param response HTTP响应
     * @param handler 处理器对象
     * @return true表示继续执行，false表示拦截请求
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 如果不是HandlerMethod类型，直接放行（如静态资源等）
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        // 放行SSE路径（Server-Sent Events）
        if (request.getRequestURI().startsWith("/sse/")) {
            return true;
        }

        // 放行Swagger文档控制器
        String controllerClassName = handlerMethod.getBean().getClass().getName();
        if ("springfox.documentation.swagger.web.ApiResourceController".equals(controllerClassName)) {
            return true;
        }

        // 处理OPTIONS预检请求
        if (CommonConstant.OPTIONS.equals(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return true;
        }

        // 从请求头获取JWT Token
        String jwtToken = request.getHeader(jwtProperties.getTokenHeader());

        // 无敌令牌命中，直接放行（仅测试使用）
        if (CommonConstant.INVINCIBLE_TOKEN.equals(jwtToken)) {
            log.warn("检测到无敌令牌，直接放行请求: {}", request.getRequestURI());
            return true;
        }

        // 如果Token为空，返回未授权错误
        if (StringUtils.isBlank(jwtToken)) {
            return handleUnauthorized(response, "登录非法，无有效全局 Token");
        }

        // 截取Token的payload部分（去除"Bearer "前缀）
        String payloadToken = null;
        try {
            int tokenHeadLength = jwtProperties.getTokenHead().length();
            if (jwtToken.length() > tokenHeadLength + 1) {
                payloadToken = jwtToken.substring(tokenHeadLength + 1);
            }
        } catch (Exception e) {
            log.error("截取Token失败", e);
            return handleUnauthorized(response, "Token格式错误");
        }

        // 验证Token是否为空
        if (StringUtils.isBlank(payloadToken)) {
            return handleUnauthorized(response, "登录非法，无有效全局 Token");
        }

        // 解析并验证Token
        try {
            Claims claims = jwtKit.parseJwtToken(payloadToken);
            if (claims == null) {
                return handleUnauthorized(response, "Token解析失败");
            }
            return true;
        } catch (Exception e) {
            log.warn("Token解析失败: {}", e.getMessage());
            return handleUnauthorized(response, "登录过期，请重新登录");
        }
    }

    /**
     * 处理未授权请求
     * 返回401状态码和错误信息
     *
     * @param response HTTP响应
     * @param message 错误消息
     * @return false，表示拦截请求
     */
    private boolean handleUnauthorized(HttpServletResponse response, String message) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("status", 401);
        jsonObject.put("msg", message);
        renderJson(response, jsonObject.toJSONString());
        return false;
    }

    /**
     * 渲染JSON响应
     * 将JSON字符串写入HTTP响应
     *
     * @param response HTTP响应
     * @param json JSON字符串
     */
    private void renderJson(HttpServletResponse response, String json) {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        try (PrintWriter printWriter = response.getWriter()) {
            printWriter.print(json);
            printWriter.flush();
        } catch (Exception e) {
            log.error("写入响应失败", e);
        }
    }
}
