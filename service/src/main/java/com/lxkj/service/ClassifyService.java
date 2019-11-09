package com.lxkj.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lxkj.entity.Classify;
import com.lxkj.mapper.ClassifyMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author Zhanqian
 * @date 2019/11/7 15:41
 */
@Service
public class ClassifyService extends ServiceImpl<ClassifyMapper, Classify> {

    @Resource
    private ClassifyMapper classifyMapper;
}
