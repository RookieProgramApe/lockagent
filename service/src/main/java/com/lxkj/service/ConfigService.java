package com.lxkj.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lxkj.entity.Config;
import com.lxkj.mapper.ConfigMapper;
import java.math.BigDecimal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 配置 服务实现类
 * </p>
 *
 * @author 一个烧包
 * @since 2019-07-13
 */
@Slf4j
@Service
public class ConfigService extends ServiceImpl<ConfigMapper, Config> {

  public Integer queryForInt(String key) {
    String value = this.queryForString(key);
    if (value == null || value.isBlank()) {
      return null;
    }
    try {
      return Integer.parseInt(value);
    } catch (Exception e) {
      log.warn(value + " cannot be casted to Integer.");
      return null;
    }
  }

  public BigDecimal queryForDecimal(String key) {
    String value = this.queryForString(key);
    if (value == null || value.isBlank()) {
      return null;
    }
    try {
      return new BigDecimal(value);
    } catch (Exception e) {
      log.warn(value + " cannot be casted to BigDecimal.");
      return null;
    }
  }

  public String queryForString(String key) {
    Config config = this.getOne(Wrappers.<Config>query().eq("`key`", key));
    if (config == null) {
      return null;
    }
    return config.getValue();
  }

}