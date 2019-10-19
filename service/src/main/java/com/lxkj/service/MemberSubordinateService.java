package com.lxkj.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lxkj.entity.MemberSubordinate;
import com.lxkj.mapper.MemberSubordinateMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 会员团队 服务实现类
 * </p>
 *
 * @author 一个烧包
 * @since 2019-07-13
 */
@Service
public class MemberSubordinateService extends ServiceImpl<MemberSubordinateMapper, MemberSubordinate> {

  public List<Map<String, Object>> querySubordinateByLevel(String memberId) {
    return this.baseMapper.querySubordinateByLevel(memberId);
  }



}