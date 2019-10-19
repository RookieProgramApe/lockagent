package com.lxkj.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lxkj.entity.Config;
import com.lxkj.entity.Member;
import com.lxkj.mapper.MemberMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * <p>
 * 会员 服务实现类
 * </p>
 *
 * @author 一个烧包
 * @since 2019-07-13
 */
@Service
public class MemberService extends ServiceImpl<MemberMapper, Member> {

    @Autowired
    private ConfigService configService;




    public BigDecimal integralTransformYuan(String memberId) {
        var member = this.getById(memberId);
        if (member.getIntegral().compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        } else {
            BigDecimal credit_discount_rate = this.configService.queryForDecimal("credit_discount_rate");
            BigDecimal discount = member.getIntegral().divide(credit_discount_rate, 2, RoundingMode.HALF_DOWN);
            return discount;
        }
    }



}