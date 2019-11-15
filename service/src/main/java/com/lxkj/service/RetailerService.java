package com.lxkj.service;

import com.lxkj.entity.Giftcard;
import com.lxkj.entity.Retailer;
import com.lxkj.mapper.RetailerMapper;
import com.lxkj.service.RetailerService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 代理商信息 服务实现类
 * </p>
 *
 * @author 一个烧包
 * @since 2019-07-13
 */
@Service
public class RetailerService extends ServiceImpl<RetailerMapper, Retailer> {

  /**
   * @return cargoId 商品ID
   * @return cargoName 商品名称
   * @return saleCount 销量
   * @return totalPrice 销售额
   * @return drpAward 提货佣金
   */
  public List<Map<String, Object>> queryCommission(String memberId) {
    return this.baseMapper.queryCommission(memberId);
  }

  // 根据卡片类型、卡片使用情况分页查询
  public  List<Giftcard> queryCardPage(Map<String, Object> map) {
    return this.baseMapper.queryCardPage(map);
  }

  public  Long countCardPage(Map<String, Object> map) {

    return this.baseMapper.countCardPage(map);
  }

  public Map<String, Object> queryRetailerCardDetail(String memberId) {
    return this.baseMapper.queryRetailerCardDetail(memberId);
  }


}