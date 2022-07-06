package com.yanyh.mall.handler;

import com.google.common.util.concurrent.RateLimiter;
import com.yanyh.mall.EnableAccessLimit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

/**
 * @Auther: yanyh
 * @Date: 2022/7/5
 * @Description:
 *
 * 根据题意：
 * 为保障Java服务端正常运行不崩溃，需要对正常访问用户进行限流处理，大约每秒响应1000个请求。
 *
 * 限流：方法注解 + 拦截器 为优惠秒杀活动接口限流兜底。
 * 常见的限流算法：
 *      - 固定窗口计数器算法
 *      - 滑动窗口算法
 *      - 漏斗限流算法
 *      - 令牌桶限流算法
 *
 * 由于题目限制 （服务端框架采用spring boot+mybatis+redis）
 * 我这里使用的是 <Guava> 工具包中 RateLimiter 类实现的 <令牌桶限流算法>
 *
 * 如果项目是微服务架构的话，也可使用 SpringCloud Alibaba 中 Sentinel组件也可实现
 * 且有更好的性能和可扩展的个性化配置。
 *
 */
@Slf4j
public class AccessLimitInterceptor implements HandlerInterceptor {

    private static RateLimiter rateLimiter;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        /**
         * request对象获取token,登录验证/
         */
        userLoginVerify(request);

        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            EnableAccessLimit limit = handlerMethod.getMethodAnnotation(EnableAccessLimit.class);

            // 放行没有打限流注解的请求
            if (Objects.isNull(limit)) {
                return true;
            }

            // 接口限流，根据参数创建限流策略
            if (Objects.isNull(rateLimiter)) {
                rateLimiter = RateLimiter.create(limit.accessPerSecond());
            }

            // 没有获得限流令牌
            if (! rateLimiter.tryAcquire()) {
                log.warn("=== 限流，response返回前端<弹框>或<重定向>页面降级处理 ===");
                return false;

            }
        }

        return true;
    }

    private void userLoginVerify(HttpServletRequest request) {
        // todo: 登录验证
    }
}
