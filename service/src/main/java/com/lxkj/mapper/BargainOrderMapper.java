package com.lxkj.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lxkj.entity.BargainOrder;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

/**
 * <p>
 * 砍价订单 Mapper 接口
 * </p>
 *
 * @author 一个烧包
 * @since 2019-08-02
 */
public interface BargainOrderMapper extends BaseMapper<BargainOrder> {

        @Select(" select bo.id,bo.cargo_id,bo.count,bo.floor_price,bo.status,bo.create_time,m.avatar,m.nickname " +
                " ,(select name from cargo where id=bo.cargo_id limit 1) as cargoName  " +
                " from  bargain_order bo inner join member m on bo.member_id=m.id  "+
                  "${ew.customSqlSegment}")
        IPage<Map> pageListBargainOrder(Page page, @Param(Constants.WRAPPER) Wrapper wrapper);
        }
