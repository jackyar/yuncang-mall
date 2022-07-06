package com.yanyh.mall.service;

import com.yanyh.mall.entity.vo.UploadStockQueryVo;

/**
 * @Auther: yanyh
 * @Date: 2022/7/5
 * @Description:
 */
public interface SalePromotionService {
    void uploadStock(UploadStockQueryVo uploadStockVo);

    void salePromotionSeckill(String code, String userId);
}
