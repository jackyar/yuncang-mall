package com.yanyh.mall.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Auther: yanyh
 * @Date: 2022/7/5
 * @Description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductSku101 {
    /**
     * 商品的skuId
     */
    private String skuId;

    /**
     * 商品SKU名称
     */
    private String code;

    /**
     * 商品的库存
     */
    private Integer stock;
}
