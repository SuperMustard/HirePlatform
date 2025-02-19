package com.hanxin.filter;

import com.google.gson.Gson;
import com.hanxin.base.BaseInfoProperties;
import com.hanxin.exceptions.ExceptionWrapper;
import com.hanxin.result.CustomJSONResult;
import com.hanxin.result.ResponseStatusEnum;
import com.hanxin.utils.JwtUtils;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
@Slf4j
public class SecurityFilterJWT extends BaseInfoProperties implements GlobalFilter, Ordered {

    public static final String HEADER_USER_TOKEN = "headerUserToken";

    @Autowired
    private ExcludeUrlsProperties excludeUrlsProperties;

    @Autowired
    private JwtUtils jwtUtils;

    private AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        //get current request path
        String url = exchange.getRequest().getURI().getPath();

        //get all exclude path
        List<String> excludeList = excludeUrlsProperties.getUrls();

        if (excludeList != null && !excludeList.isEmpty()) {
            for (String excludeUrl : excludeList) {
                if (antPathMatcher.matchStart(excludeUrl, url)) {
                    log.error("excluded");
                    //pass the filter
                    return chain.filter(exchange);
                }
            }
        }

        String fileStart = excludeUrlsProperties.getFileStart();
        if (!fileStart.isEmpty()) {
            if (antPathMatcher.matchStart(fileStart, url)) {
                //pass the filter
                return chain.filter(exchange);
            }
        }

        // 判断header中是否有token，对用户请求进行判断拦截
        HttpHeaders headers = exchange.getRequest().getHeaders();
        String userToken = headers.getFirst(HEADER_USER_TOKEN);

        // 判空header中的令牌
        if (StringUtils.isNotBlank(userToken)) {
            String[] tokenArr = userToken.split(JwtUtils.at);
            if (tokenArr.length < 2) {
                return renderErrorMsg(exchange, ResponseStatusEnum.UN_LOGIN);
            }

            // 获得jwt的令牌与前缀
            String prefix = tokenArr[0];
            String jwt = tokenArr[1];

            // 判断并且处理用户信息
            if (prefix.equalsIgnoreCase(TOKEN_USER_PREFIX)) {
                return dealJWT(jwt, exchange, chain, APP_USER_JSON);
            } else if (prefix.equalsIgnoreCase(TOKEN_SAAS_PREFIX)) {
                return dealJWT(jwt, exchange, chain, SAAS_USER_JSON);
            } else if (prefix.equalsIgnoreCase(TOKEN_ADMIN_PREFIX)) {
                return dealJWT(jwt, exchange, chain, ADMIN_USER_JSON);
            }

//            return dealJWT(jwt, exchange, chain, APP_USER_JSON);
        }

        return renderErrorMsg(exchange, ResponseStatusEnum.UN_LOGIN);
    }

    public Mono<Void> dealJWT(String jwt, ServerWebExchange exchange, GatewayFilterChain chain, String key) {
        try {
            String userJson = jwtUtils.checkJWT(jwt);
            ServerWebExchange serverWebExchange = setNewHeader(exchange, key, userJson);
            return chain.filter(serverWebExchange);
        } catch (ExpiredJwtException e) {
            e.printStackTrace();
            return renderErrorMsg(exchange, ResponseStatusEnum.JWT_EXPIRE_ERROR);
        } catch (Exception e) {
            e.printStackTrace();
            return renderErrorMsg(exchange, ResponseStatusEnum.JWT_SIGNATURE_ERROR);
        }
    }

    public ServerWebExchange setNewHeader(ServerWebExchange exchange,
                                          String headerKey,
                                          String headerValue) {
        // 重新构建新的request
        ServerHttpRequest newRequest = exchange.getRequest()
                .mutate()
                .header(headerKey, headerValue)
                .build();
        // 替换原来的request
        ServerWebExchange newExchange = exchange.mutate().request(newRequest).build();
        return newExchange;
    }

    //Build Custom Error Response which will be sent to client
    public Mono<Void> renderErrorMsg(ServerWebExchange exchange, ResponseStatusEnum statusEnum) {
        //get response
        ServerHttpResponse response = exchange.getResponse();

        //build jsonResult
        CustomJSONResult jsonResult = CustomJSONResult.exception(statusEnum);

        //change response code
        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);

        //set header type
        if (response.getHeaders().containsKey("Content-Type"))
            response.getHeaders().add("Content-Type", MimeTypeUtils.APPLICATION_JSON_VALUE);

        String resultJson = new Gson().toJson(jsonResult);
        DataBuffer dataBuffer = response
                .bufferFactory()
                .wrap(resultJson.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(dataBuffer));
    }

    @Override
    public int getOrder() {
        return 0; //smaller number means higher priority
    }
}
