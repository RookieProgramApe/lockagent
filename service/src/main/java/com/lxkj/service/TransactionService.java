package com.lxkj.service;

import com.lxkj.entity.Transaction;
import com.lxkj.mapper.TransactionMapper;
import com.lxkj.service.TransactionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 支付 服务实现类
 * </p>
 *
 * @author 一个烧包
 * @since 2019-07-13
 */
@Service
public class TransactionService extends ServiceImpl<TransactionMapper, Transaction> {

  public BigDecimal queryWithdrawable(String memberId) {
    return this.baseMapper.queryWithdrawable(memberId);
  }

  public BigDecimal queryAccruedIncome(String memberId) {
    return this.baseMapper.queryAccruedIncome(memberId);
  }
}