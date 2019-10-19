package com.lxkj.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lxkj.entity.CreditWarehousing;
import com.lxkj.entity.Warehousing;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 * 货物入库记录 Mapper 接口
 * </p>
 *
 * @author 一个烧包
 * @since 2019-07-13
 */
public interface CreditWarehousingMapper extends BaseMapper<CreditWarehousing> {
    @Select("select \n" +
            " (select ifnull(sum(inventory),0) from `creditwarehousing`where sku_id=#{id})\n" +
            "  -\n" +
            " (select count(1) from `creditorder` where status in (2,3,4) and sku_id=#{id})")
    Integer sumSku(@Param("id") String id);
}
