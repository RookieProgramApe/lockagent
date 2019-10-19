package com.lxkj.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lxkj.entity.Transaction;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.Map;

/**
 * <p>
 * 支付 Mapper 接口
 * </p>
 *
 * @author 一个烧包
 * @since 2019-07-13
 */
public interface TransactionMapper extends BaseMapper<Transaction> {
  /**
   * 可提现金额
   * @param memberId
   * @return
   */
  @Select("select sum(amount) from (\n"
      + "   select ifnull(sum(amount), 0) as amount from `transaction` where status in (0, 1) and type in (80, 81) and member_id = #{memberId,jdbcType=VARCHAR}\n"
      + "    union all\n"
      + "   select ifnull(sum(amount+commission) * -1, 0) as amount from `transaction` where status in (0, 1) and type = 99 and member_id = #{memberId,jdbcType=VARCHAR}\n"
      + "  ) t")
  BigDecimal queryWithdrawable(@Param("memberId") String memberId);

  /**
   * 总收益
   * @param memberId
   * @return
   */
  @Select("select ifnull(sum(amount), 0) from `transaction` where status = 1 and type in (80,81) and member_id = #{memberId,jdbcType=VARCHAR}")
  BigDecimal queryAccruedIncome(@Param("memberId") String memberId);


  @Select(" select  m.avatar,m.nickname,t.* from transaction t " +
          "left join Member m on t.member_id=m.id " +
          "${ew.customSqlSegment}")
  IPage<Map> pagWithMessage(Page page, @Param(Constants.WRAPPER) Wrapper wrapper);
}
