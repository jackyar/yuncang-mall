package com.yanyh.mall;

import java.lang.annotation.*;

/**
 * @Auther: yanyh
 * @Date: 2022/7/5
 * @Description:
 *
 * 自定义限流的方法注解！
 * 为标记 @EnableAccessLimit 注解的接口做限流策略。
 *
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface EnableAccessLimit {
    /**
     * 限制单个接口每秒的请求响应数量。
     * @return
     */
    double accessPerSecond() default 1000;

}
