package com.lxkj.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lxkj.entity.MemberSubordinate;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 会员团队 Mapper 接口
 * </p>
 *
 * @author 一个烧包
 * @since 2019-07-13
 */
public interface MemberSubordinateMapper extends BaseMapper<MemberSubordinate> {

  @Select("select ms.name, ms.phone,ms.member_id as memberId,"+
          "         (select count(1) from retailer b where b.parent_member_id = ms.member_id) as teamcount,"
          + "        (select count(1) from retailer_giftcard rg where rg.member_id = ms.member_id) as bought, ms.create_time\n"
          + "    from retailer ms\n"
          + "   where ms.parent_member_id = #{memberId,jdbcType=VARCHAR}")
  IPage<Map> querySubordinate(IPage<Map> page, @Param("memberId") String memberId);

    @Select(" select m.avatar, m.nickname, ms.name,ms.phone," +
            "   (select count(1) from retailer b where b.parent_member_id = ms.member_id) as teamcount,"
            + "  (select count(1) from `card_order` co where co.status=2 and co.member_id = ms.member_id) as bought "
            + " ,(select ifnull(sum(count),0) from `card_order` co where co.status=2 and co.member_id = ms.member_id) as sumCount "
            + " ,(select ifnull(sum(total_price),0) from `card_order` co where co.status=2 and co.member_id = ms.member_id) as sumTotal "
            + " , ms.create_time\n"
            + "  from retailer ms\n"
            + "  inner join member m on m.id = ms.member_id\n"
            + "  where ms.parent_member_id = #{memberId,jdbcType=VARCHAR} order by ms.create_time desc ")
    List<Map<String, Object>> querySubordinateByLevel(@Param("memberId") String memberId);
}
