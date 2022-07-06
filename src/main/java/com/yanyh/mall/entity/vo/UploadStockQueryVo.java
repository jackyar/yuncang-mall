package com.yanyh.mall.entity.vo;

import lombok.Data;

/**
 * @Auther: yanyh
 * @Date: 2022/7/5
 * @Description:
 *
 * 商品上架请求VO
 */
@Data
public class UploadStockQueryVo {
    /**
     * 活动Id
     */
    private String promotionId;

    /**
     * 商品的skuId
     */
    private String skuId;

    /**
     * 活动开始时间
     */
    private Long startTime;

    /**
     * 活动结束时间
     */
    private Long endTime;

}
