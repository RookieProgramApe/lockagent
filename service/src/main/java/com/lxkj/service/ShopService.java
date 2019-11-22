package com.lxkj.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lxkj.common.bean.JsonResults;
import com.lxkj.entity.Giftcard;
import com.lxkj.entity.RetailerGiftcard;
import com.lxkj.entity.Shop;
import com.lxkj.entity.ShopLottery;
import com.lxkj.mapper.ShopMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author Zhanqian
 * @date 2019/11/11 9:52
 */
@Service
public class ShopService extends ServiceImpl<ShopMapper, Shop> {

    @Autowired
    private CardOrderService cardOrderService;
    @Autowired
    private ShopLotteryService shopLotteryService;
    @Autowired
    private RetailerGiftcardService rgService;
    @Resource
    private JdbcTemplate jdbcTemplate;

    // 商家购买卡片
    public void buycard(String retailerId, Integer sum) {
        // 分配卡片
        cardOrderService.finshCardOrders2(retailerId, sum);
    }

    // 获取一张商家的卡片
    public Giftcard getOneGifcard(String lotteryId) {
        // 查询抽奖的基本信息
        ShopLottery lottery = shopLotteryService.getById(lotteryId);

        // 查询抽奖商家的信息
        Shop shop = this.getById(lottery.getShopId());

        Integer count = rgService.count(new QueryWrapper<RetailerGiftcard>().eq("member_id", shop.getMemberId()).eq("state", 0).eq("`type`", 1));

        if(count > 0) {
            if(count <= 10){
                // 补足卡片
                this.buycard(shop.getRetailerId(), 90);
            }
            // 查询一张轻奢卡片
            return this.jdbcTemplate.queryForObject("select * from giftcard where id=(select giftcard_id from retailer_giftcard where member_id=? and state=0 and `type`=1 LIMIT 1)",
                    new BeanPropertyRowMapper<Giftcard>(Giftcard.class), shop.getMemberId());
        }else {
            return null;
        }
    }
}
