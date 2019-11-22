package com.lxkj.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lxkj.entity.Giftcard;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 卡片 Mapper 接口
 * </p>
 *
 * @author 一个烧包
 * @since 2019-07-13
 */
public interface GiftcardMapper extends BaseMapper<Giftcard> {


        // 优化后的分页查询卡片
        List<Giftcard> queryGiftcardPageByOrderId(Map<String, Object> map);

        Long countGiftcardPageByOrderId(Map<String, Object> map);

        // 优化后的分页查询卡片
        List<Giftcard> queryGiftcardPageByMemberId(Map<String, Object> map);

        Long countGiftcardPageByMemberId(Map<String, Object> map);

        // 优化后的分页查询卡片
        List<Giftcard> queryGiftcardPage(Map<String, Object> map);

        Long countGiftcardPage(Map<String, Object> map);

}
