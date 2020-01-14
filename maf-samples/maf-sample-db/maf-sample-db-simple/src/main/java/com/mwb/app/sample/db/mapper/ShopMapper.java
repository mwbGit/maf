package com.mwb.app.sample.db.mapper;

import com.mwb.app.sample.db.model.Shop;
import org.apache.ibatis.annotations.Select;

public interface ShopMapper {

    @Select("select * from shop where id=#{shopId}")
    Shop findById(long shopId);
}
