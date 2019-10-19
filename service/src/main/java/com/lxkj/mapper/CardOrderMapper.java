package com.lxkj.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lxkj.entity.CardOrder;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

/**
 * <p>
 * 卡片订单 Mapper 接口
 * </p>
 *
 * @author 一个烧包
 * @since 2019-07-18
 */
public interface CardOrderMapper extends BaseMapper<CardOrder> {
       @Select(" select co.*,m.avatar,m.nickname,r.`name`,r.phone from card_order co \n " +
               " inner join member m on co.member_id=m.id \n " +
               " inner join retailer r on co.retailer_id=r.id " +
               " ${ew.customSqlSegment} ")
       IPage<Map> cardOrderPage(Page page, @Param(Constants.WRAPPER) Wrapper wrapper);
        }
