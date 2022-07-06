package com.yanyh.mall.controller;

import com.yanyh.mall.Constants;
import com.yanyh.mall.EnableAccessLimit;
import com.yanyh.mall.entity.vo.UploadStockQueryVo;
import com.yanyh.mall.service.SalePromotionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * @Auther: yanyh
 * @Date: 2022/7/5
 * @Description:
 *
 * 促销活动 controller
 */
@RestController
@RequestMapping("/sale/sku")
@Slf4j
public class SalePromotionController {

    @Autowired
    private SalePromotionService salePromotionService;

    /**
     * 优惠活动秒杀入口
     *
     * @param code sku code
     * @return
     */
    @GetMapping("/sku/secKill")
    @EnableAccessLimit(accessPerSecond = 100)
    public ResponseEntity<String> salePromotionSeckill(String code) {
        String userId = servletRequestGetUserId();
        salePromotionService.salePromotionSeckill(code, userId);
        return ResponseEntity.ok(Constants.OK);
    }

    /**
     * 管理员上架商品
     *
     * @param uploadStockVo
     * @return
     */
    @PostMapping("/admin/upload/stock")
    public ResponseEntity<Object> uploadStock(@RequestBody UploadStockQueryVo uploadStockVo) {
        salePromotionService.uploadStock(uploadStockVo);
        return ResponseEntity.ok(Constants.OK);
    }

    private String servletRequestGetUserId() {
        // todo: 具体可封装成工具类，通过request中的token拿到userId
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder
                .getRequestAttributes())).getRequest();

        return "request.token.userId";
    }
}
