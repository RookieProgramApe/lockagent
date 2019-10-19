package com.lxkj.service;

import com.lxkj.entity.Counter;
import com.lxkj.mapper.CounterMapper;
import com.lxkj.service.CounterService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 计数器 服务实现类
 * </p>
 *
 * @author 一个烧包
 * @since 2019-09-30
 */
@Service
public class CounterService extends ServiceImpl<CounterMapper, Counter> {

 }