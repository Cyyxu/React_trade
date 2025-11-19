package com.xyex.shared.utils;

import cn.hutool.core.util.ObjectUtil;
import com.xyex.infrastructure.exception.BusinessException;
import com.xyex.infrastructure.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;


@Slf4j
public class ExternalHttpUtil {

    private static final WebClient WEB_CLIENT = SpringUtils.getBean(WebClient.class);
    private static final String SSO_SERVICE_URl = SpringUtils.getProperty("sso.server");
    private static final String VESSEL_SERVER = SpringUtils.getProperty("vessel.server");

    /**
     * 请求外部服务
     *
     * @param <T>          响应类型
     * @param path         请求路径
     * @param method       请求方法
     * @param query        请求参数
     * @param headers      请求头
     * @param body         请求体
     * @param responseType 响应类型
     * @return 响应结果
     */
    private static <T> Mono<T> request(String path,
                                       HttpMethod method,
                                       Map<String, Object> query,
                                       HttpHeaders headers,
                                       Object body,
                                       ParameterizedTypeReference<T> responseType) {
        //请求头设置
        if (ObjectUtil.isNull(headers)) {
            headers = new HttpHeaders();
        }
        if (!headers.containsKey(HttpHeaders.CONTENT_TYPE)) {
            headers.setContentType(MediaType.APPLICATION_JSON);
        }
        //请求参数
        if (ObjectUtil.isNull(query)) {
            query = new HashMap<>();
        }

        // 构建带查询参数的URI
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(path);
        for (Map.Entry<String, Object> entry : query.entrySet()) {
            uriBuilder.queryParam(entry.getKey(), entry.getValue());
        }
        URI uri = uriBuilder.build().toUri();

        HttpHeaders finalHeaders = headers;
        WebClient.RequestBodySpec requestSpec = WEB_CLIENT.method(method)
                .uri(uri)
                .headers(httpHeaders -> httpHeaders.addAll(finalHeaders));

        // 如果body不为空才设置bodyValue
        if (ObjectUtil.isNotNull(body)) {
            requestSpec.bodyValue(body);
        }

        return requestSpec.retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse -> clientResponse.bodyToMono(String.class)
                        .flatMap(errorBody -> Mono.error(new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, errorBody))))
                .bodyToMono(responseType);
    }

    public static <T> Mono<T> ssoGet(String path,
                                     Map<String, Object> query,
                                     HttpHeaders headers,
                                     ParameterizedTypeReference<T> responseType) {
        return request(ExternalHttpUtil.SSO_SERVICE_URl.concat(path), HttpMethod.GET, query, headers, null, responseType);
    }

    public static <T> Mono<T> ssoPost(String path,
                                      Map<String, Object> query,
                                      HttpHeaders headers,
                                      Object body,
                                      ParameterizedTypeReference<T> responseType) {
        return request(ExternalHttpUtil.SSO_SERVICE_URl.concat(path), HttpMethod.POST, query, headers, body, responseType);
    }

    public static <T> Mono<T> forecastGet(String path,
                                          Map<String, Object> query,
                                          HttpHeaders headers,
                                          Object body,
                                          ParameterizedTypeReference<T> responseType) {
        return request(ExternalHttpUtil.VESSEL_SERVER.concat(path), HttpMethod.GET, query, headers, body, responseType);

    }

}