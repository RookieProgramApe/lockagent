package com.lxkj.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lxkj.entity.Appraise;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AppraiseMapper extends BaseMapper<Appraise> {

    List<Appraise> getAppraiseByCargoId(@Param("cargoId") String cargoId);
}
