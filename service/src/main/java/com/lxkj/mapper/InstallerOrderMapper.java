package com.lxkj.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.lxkj.entity.InstallerOrder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

/**
 * <p>
 * 安装订单 Mapper 接口
 * </p>
 *
 * @author 一个烧包
 * @since 2019-08-27
 */
public interface InstallerOrderMapper extends BaseMapper<InstallerOrder> {
        @Select("SELECT " +
                        "a.*," +
                        "b.avatar as avatar,"+
                        "b.nickname," +
                        "o.count,"+
                        "o.recipient,"+
                        "o.total_price as orderPrice,"+
                        "o.mobile,"+
                        "o.province," +
                        "o.city,"+
                        "o.county,"+
                        "o.address,"+
                         "o.ordernum,"+
                        "o.cargo_name,"+
                        "o.sku_name  " +
                " FROM installer_order a " +
                " inner join member b on a.member_id=b.id" +
               " inner join `order` o on a.order_id=o.id "   +
                "${ew.customSqlSegment}")
        IPage<Map> queryPage(IPage<Map> page, @Param(Constants.WRAPPER) Wrapper wrapper);

}
