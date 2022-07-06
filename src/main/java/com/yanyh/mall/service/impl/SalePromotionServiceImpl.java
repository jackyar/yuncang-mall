package com.yanyh.mall.service.impl;

import com.yanyh.mall.AbstractMybatisMapper;
import com.yanyh.mall.Constants;
import com.yanyh.mall.entity.ProductSku101;
import com.yanyh.mall.entity.vo.UploadStockQueryVo;
import com.yanyh.mall.service.SalePromotionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * @Auther: yanyh
 * @Date: 2022/7/5
 * @Description:
 */
@Service
@Slf4j
public class SalePromotionServiceImpl implements SalePromotionService {
    @Autowired
    private AbstractMybatisMapper mapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 上架商品：
     *
     * 1. 根据 <活动Id> 和 <skuId> 复合主键找到活动商品
     * 2. 把商品库存量写入Redis, 由于是后台管理员上架没有上锁，也可使用Redisson分布式锁以防万一。
     *
     * @param uploadStockVo
     */
    @Override
    public void uploadStock(UploadStockQueryVo uploadStockVo) {
        ProductSku101 productSku = mapper.selectProductSku(uploadStockVo.getPromotionId(), uploadStockVo.getSkuId());
        Integer stock = productSku.getStock();
        String key = Constants.STOCK_KEY_PREFIX + productSku.getCode();
        long expire  = uploadStockVo.getEndTime() - System.currentTimeMillis();

        Boolean result = redisTemplate.opsForValue().setIfAbsent(key, stock, expire, TimeUnit.MILLISECONDS);
        if (result == null || !result) {
            log.warn("该商品已上架，正在等待活动开始，请勿重复发布！");
        }

        log.info("SUCCESS: 商品预热成功");
    }

    /**
     * <用户抢购，扣减库存>
     *
     * 这里选择使用Lua脚本原子扣减操作，也可使用Redisson分布式锁实现（内部也是lua脚本实现的原子锁）
     *
     * @param code 商品编号
     */
    @Override
    public void salePromotionSeckill(String code, String userId) {
        Long result = redisTemplate.execute(new DefaultRedisScript<>(
                        "local stock = redis.call(\"get\", KEYS[1]);\n" +
                                "if stock and tonumber(stock) > 0 then\n" +
                                "\treturn redis.call(\"decr\", KEYS[1]);\n" +
                                "end\n" +
                                "return 0;", Long.class),
                Collections.singletonList(Constants.STOCK_KEY_PREFIX + code));

        if (result != null && result != 0) {
            // 生成订单号（这里是自己随便生成的，具体可按固定规则生成。）
            String tradeNo = Constants.TRADE_NO_PREFIX + System.currentTimeMillis();
            String value = tradeNo + "-" + code + "-" + userId;
            /**
             * todo: 由于题目限制，这里使用消息队列实现，效果和性能更加。
             *
             * 将秒杀成功的用户的 订单、商品号、用户ID 存入redis的Set结构。
             * 由其他定时任务或服务在活动开始后进行消费 完成<生成订单信息>、<数据库扣减库存>、<拉起支付> 等操作。
             *
             */
            redisTemplate.opsForSet().add("sku:seckill:users", value);
            log.info("SUCCESS: 抢购成功！=== 剩余库存 {}", result);

            // 抢购成功，将用户引导至 抢购成功、订单确认和支付倒计时等相关页面。

        } else {
            log.info("FAIL: 库存不足，活动结束");
            // todo: 商品抢光，结束活动，重置当前活动的状态和时间
        }

    }
}
