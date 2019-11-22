package com.lxkj.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.lxkj.entity.Giftcard;
import com.lxkj.mapper.GiftcardMapper;
import com.lxkj.service.GiftcardService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 卡片 服务实现类
 * </p>
 *
 * @author 一个烧包
 * @since 2019-07-13
 */
@Service
public class GiftcardService extends ServiceImpl<GiftcardMapper, Giftcard> {

       @Resource
       private  GiftcardMapper giftcardMapper;

       // 根据订单id查询分配的卡片信息
       public List<Giftcard> queryGiftcardPageByOrderId(Map<String, Object> map) {

              return giftcardMapper.queryGiftcardPageByOrderId(map);

       }

       // 根据订单id查询分配的卡片信息
       public Long countGiftcardPageByOrderId(Map<String, Object> map) {

        return giftcardMapper.countGiftcardPageByOrderId(map);

       }

       // 根据memberid查询分配的卡片信息
       public List<Giftcard> queryGiftcardPageByMemberId(Map<String, Object> map) {

              return giftcardMapper.queryGiftcardPageByMemberId(map);

       }

       // 根据memberid查询分配的卡片信息
       public Long countGiftcardPageByMemberId(Map<String, Object> map) {

              return giftcardMapper.countGiftcardPageByMemberId(map);

       }

       // 根据memberid查询分配的卡片信息
       public List<Giftcard> queryGiftcardPage(Map<String, Object> map) {

              return giftcardMapper.queryGiftcardPage(map);

       }

       // 根据memberid查询分配的卡片信息
       public Long countGiftcardPage(Map<String, Object> map) {

              return giftcardMapper.countGiftcardPage(map);

       }
 }