package com.lxkj.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.lxkj.entity.Retailer;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 * 代理商信息 Mapper 接口
 * </p>
 *
 * @author 一个烧包
 * @since 2019-07-13
 */
public interface RetailerMapper extends BaseMapper<Retailer> {

  @Select("select o.cargo_id as cargoId,\n"
      + "         o.`cargo_name` as cargoName,\n"
      + "         count(o.cargo_id) * o.count as saleCount,\n"
      + "         count(o.total_price) as totalPrice,\n"
      + "         (select ifnull(sum(t.amount),0)  "
      + "            from `transaction` t   " +
          "        where o.id = t.order_id  and t.type = 81\n"
      + "             and t.`status` = 1\n"
      + "             and t.member_id = #{memberId,jdbcType=VARCHAR}) as drpAward\n"
      + "    from `order` o\n"
      + "   inner join retailer_giftcard rg on o.giftcard_id = rg.giftcard_id\n"
      + "   where o.`status` !=11 "
      + "     and rg.member_id = #{memberId,jdbcType=VARCHAR}\n"
      + "   group by o.cargo_id")
  List<Map<String, Object>> queryCommission(@Param("memberId") String memberId);



  @Select("select u.phone,u.name, co.count as orderNum, co.total_price as orderPrice, co.create_time as orderCreateTime, t.amount\n"
          + "    from `transaction` t  "
          + "    inner join  card_order co on t.order_id = co.id\n"
          + "    inner join `retailer` u on u.id = co.retailer_id\n"
          + "   where t.`status` = 1"
          + "     and t.type = 80\n"
          + "     and t.member_id = #{memberId,jdbcType=VARCHAR}\n"
          + "   order by t.create_time desc")
  IPage<Map> queryCardAward(IPage<Map> page, @Param("memberId") String memberId);
}
