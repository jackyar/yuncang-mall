package com.yanyh.mall;

import com.yanyh.mall.entity.ProductSku101;
import org.springframework.stereotype.Repository;

/**
 * @Auther: yanyh
 * @Date: 2022/7/5
 * @Description:
 *
 * 充当Mock, 抽象模拟数据库的相关查询操作
 */
@Repository
public class AbstractMybatisMapper {
    public ProductSku101 selectProductSku(String promotionId, String skuId){
        return new ProductSku101("uuid-101", "sku101", 200);
    }


}
