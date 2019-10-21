package com.lxkj.mapper;

import com.lxkj.entity.Member;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * 会员 Mapper 接口
 * </p>
 *
 * @author 一个烧包
 * @since 2019-07-13
 */
public interface MemberMapper extends BaseMapper<Member> {

        int updateIntegral(Member member);

        }
