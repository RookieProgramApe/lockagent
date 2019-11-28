package com.lxkj.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.lxkj.entity.Giftcard;
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

  @Select("select t.amount reward, r.`name` retailerName, t.create_time createTime from `transaction` t INNER JOIN retailer r on r.member_id=t.member_id where t.type=82 and t.member_id = #{memberId,jdbcType=VARCHAR}")
  IPage<Map> queryLbAward(IPage<Map> page, @Param("memberId") String memberId);

  @Select("select t.*,(select o.cargo_name from `order` o where o.id=t.order_id) as cargoName, (select g.serial from giftcard g where g.id=(select o.giftcard_id from `order` o where o.id=t.order_id)) as serial from `transaction` t where member_id=#{memberId,jdbcType=VARCHAR} and type=81 and status=1 order by create_time DESC")
  IPage<Map> queryTransaction(IPage<Map> page, @Param("memberId") String memberId);

  // 根据卡片类型、卡片使用情况分页查询
  List<Giftcard> queryCardPage(Map<String, Object> map);
  Long countCardPage(Map<String, Object> map);

  // 查询代理商卡片使用情况及分配情况信息信息
  Map<String, Object> queryRetailerCardDetail(@Param("memberId") String memberId);
}
