package com.hanxin.filter;

import com.google.gson.Gson;
import com.hanxin.base.BaseInfoProperties;
import com.hanxin.result.CustomJSONResult;
import com.hanxin.result.ResponseStatusEnum;
import com.hanxin.utils.IPUtil;
import com.hanxin.utils.JwtUtils;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
@Slf4j
public class IPLimitFilterJWT extends BaseInfoProperties implements GlobalFilter, Ordered {

    @Autowired
    private ExcludeUrlsProperties excludeUrlsProperties;

    private AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Value("${blackIP.continueCounts}")
    private Integer continueCounts;
    @Value("${blackIP.timeInterval}")
    private Integer timeInterval;
    @Value("${blackIP.limitTimes}")
    private Integer limitTimes;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        log.info("filter number 2");
// 1. 获取当前的请求路径
        String url = exchange.getRequest().getURI().getPath();

// 2. 获得所有的需要进行ip限流校验的url list
        List<String> ipLimitUrlList = excludeUrlsProperties.getIpLimitUrls();

// 3. 校验
        if (ipLimitUrlList != null && !ipLimitUrlList.isEmpty()) {
            for (String limitUrl : ipLimitUrlList) {
                if (antPathMatcher.matchStart(limitUrl, url)) {
                    // 如果匹配到，则表明需要校验
                    log.info("IPLimitFilter - 被拦截到：url = " + url);
                    return this.doLimit(exchange, chain);
                }
            }
        }

// 4. 默认放行
        return chain.filter(exchange);
    }

    /**
     * 限制IP频繁访问接口，对其进行限制
     * @param exchange
     * @param chain
     * @return
     */
    private Mono<Void> doLimit(ServerWebExchange exchange,
                               GatewayFilterChain chain) {

        // 根据request获得ip
        ServerHttpRequest request = exchange.getRequest();
        String ip = IPUtil.getIP(request);

        /**
         * 需求：
         * 判断ip在10秒内请求的次数是否超过3次，
         * 如果超过，则限制访问15秒，15秒过后再放行
         */
        // 正常的IP
        final String ipRedisKey = "gateway-ip:" + ip;
        // 被拦截的黑名单，如果存在，表示目前被关小黑屋
        final String ipRedisLimitKey = "gateway-ip-limit:" + ip;

        // 获得剩余的限制时间
        long limitLeftTime = redis.ttl(ipRedisLimitKey);
        // 如果剩余时间还存在，说明这个ip不能访问，继续等待
        if (limitLeftTime > 0) {
            // 终止请求，返回错误
            return renderErrorMsg(exchange, ResponseStatusEnum.SYSTEM_ERROR_BLACK_IP);
        }

        // 在redis中累加ip的请求访问次数
        long requestCounts = redis.increment(ipRedisKey, 1);
        // 从0开始计算请求次数，初期访问为1，则设置过期时间，也就是连续请求的间隔时间
        if (requestCounts == 1) { //如果是第一次，则设定过期时间
            redis.expire(ipRedisKey, timeInterval);
        }

        // 如果还能取得到请求次数，说明用户连续请求的次数落在10秒内
        // 一旦请求次数超过了连续访问的次数，则需要限制这个ip了
        if (requestCounts > continueCounts) {
            // 限制ip访问一段时间
            redis.set(ipRedisLimitKey, ipRedisLimitKey, limitTimes);

            // 终止请求，返回错误
            return renderErrorMsg(exchange, ResponseStatusEnum.SYSTEM_ERROR_BLACK_IP);
        }

        return chain.filter(exchange);
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
        return 1; //smaller number means higher priority
    }
}
