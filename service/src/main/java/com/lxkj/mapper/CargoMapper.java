package com.lxkj.mapper;

import com.lxkj.entity.Cargo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 商品 Mapper 接口
 * </p>
 *
 * @author 一个烧包
 * @since 2019-07-13
 */
public interface CargoMapper extends BaseMapper<Cargo> {

    List<Cargo> getCargoList(Map<String, Object> map);

    Long countCargoList(Map<String, Object> ma);
 }
