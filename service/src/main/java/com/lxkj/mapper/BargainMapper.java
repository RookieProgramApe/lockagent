package com.lxkj.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.lxkj.entity.Bargain;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lxkj.service.BargainOrderFlowService;
import com.lxkj.service.BargainOrderService;
import com.lxkj.service.BargainStepService;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 砍价活动 Mapper 接口
 * </p>
 *
 * @author 一个烧包
 * @since 2019-08-02
 */
public interface BargainMapper extends BaseMapper<Bargain> {


        @Select("SELECT " +
                    "a.*," +
                "   b.name as cargoName,"+
                "   b.sale_price as cargoSalePrice,"+
                "   b.base_sale_count as cargoBaseSaleCount,"+
                "   b.description as cargoDescription,"+
                " FROM bargain a " +
                " inner join cargo b on a.cargo_id=b.id " +
                "${ew.customSqlSegment}")
        IPage<Bargain> queryPage(IPage<Bargain> page, @Param(Constants.WRAPPER) Wrapper wrapper);




        @Select("select ifnull(sum(price),0) from bargain_order_flow ${ew.customSqlSegment}  ")
        BigDecimal getAmount(@Param(Constants.WRAPPER) Wrapper wrapper);


        @Select("SELECT " +
                        "b.avatar," +
                        "b.nickname," +
                        "a.price "+
                " FROM bargain_order_flow a " +
                "  inner join member b on a.end_member_id=b.id " +
                " where a.status=1  and a.bargain_order_id=#{orderId}  order by a.end_time desc")
        List<Map> BargainingList(@Param("orderId") String orderId);

}
