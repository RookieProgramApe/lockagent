package com.lxkj.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.lxkj.common.exception.BusinessException;
import com.lxkj.entity.Bargain;
import com.lxkj.entity.BargainOrder;
import com.lxkj.entity.BargainOrderFlow;
import com.lxkj.entity.BargainStep;
import com.lxkj.mapper.BargainMapper;
import com.lxkj.service.BargainService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.Synchronized;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 砍价活动 服务实现类
 * </p>
 *
 * @author 一个烧包
 * @since 2019-08-02
 */
@Service
public class BargainService extends ServiceImpl<BargainMapper, Bargain> {

    @Resource
    private BargainMapper brgainMapper;
    @Autowired
    private BargainStepService bargainStepService;
    @Autowired
    private BargainOrderService bargainOrderService;
    @Autowired
    private BargainOrderFlowService brgainOrderFlowService;

    /**
     * 发起砍价剩余机会(剩余库存)
     *
     * @param bargainId
     * @return
     */
    public Integer querySurplus(String bargainId) {
        var data = this.getById(bargainId);
        int sum = data.getStock() - bargainOrderService.count(new QueryWrapper<BargainOrder>().eq("bargain_id", data.getId()));
        return sum;
    }

    /**
     * 已砍次数
     *
     * @param bargain_order_id
     * @return
     */
    public Integer get_YK(String bargain_order_id) {
        var i = brgainOrderFlowService.count(new QueryWrapper<BargainOrderFlow>()
                .eq("bargain_order_id", bargain_order_id)
                .eq("status", 1));
        return i;
    }

    /**
     * 剩余未砍次数
     *
     * @param bargain_order_id
     * @return
     */
    public Integer get_WK(String bargain_order_id) {
        var i = brgainOrderFlowService.count(new QueryWrapper<BargainOrderFlow>()
                .eq("bargain_order_id", bargain_order_id)
                .eq("status", 0));
        return i;
    }

    /**
     * 总共需要砍
     *
     * @param bargain_order_id
     * @return
     */
    public Integer get_SUM(String bargain_order_id) {
        var i = brgainOrderFlowService.count(new QueryWrapper<BargainOrderFlow>()
                .eq("bargain_order_id", bargain_order_id));
        return i;
    }

    /**
     * 已砍金额
     *
     * @param bargain_order_id
     * @return
     */
    public BigDecimal Yk_Amount(String bargain_order_id) {
        return brgainMapper.getAmount(new QueryWrapper<>().eq("bargain_order_id", bargain_order_id).eq("status", 1));
    }

    /**
     * 用户为这个订单砍了多少钱(帮TA砍了多少金额)
     * @param bargain_order_id
     * @return
     */
    public BigDecimal member_Yk_Amount(String bargain_order_id,String memberId) {
        return brgainMapper.getAmount(new QueryWrapper<>()
                .eq("bargain_order_id", bargain_order_id)
                .eq("end_member_id",memberId).eq("status", 1));
    }

    /**
     * 砍价配置总金额
     *
     * @param bargain_order_id
     * @return
     */
    public BigDecimal Sum_Amount(String bargain_order_id) {
        return brgainMapper.getAmount(new QueryWrapper<>().eq("bargain_order_id", bargain_order_id));
    }

    /**
     * 砍价榜
     * @param bargain_order_id
     * @return  {avatar:'头像',nickname:'昵称',price:'金额'}
     */
    public  List<Map> BargainingList(String bargain_order_id) {
        return brgainMapper.BargainingList(bargain_order_id);
    }

    /**
     * 帮TA砍价
     */
    @Transactional(rollbackFor = Exception.class)
    public synchronized BigDecimal Help_Bargain(String bargain_order_id, String memberId) {
        //判断是否砍价过了
        int i = brgainOrderFlowService.count(new QueryWrapper<BargainOrderFlow>().eq("end_member_id", memberId).eq("bargain_order_id", bargain_order_id));
        if (i > 0) {
           throw new BusinessException("您已经帮TA砍过价了");
        }
        //未砍的列表
        var list=brgainOrderFlowService.list(new QueryWrapper<BargainOrderFlow>().eq("bargain_order_id",bargain_order_id).eq("status",0).orderByAsc("sort"));
        if(!list.isEmpty()){
            //砍价成功
            BargainOrderFlow obj=list.get(0);
            obj.setStatus(1);
            obj.setEndTime(new Date());
            obj.setEndMemberId(memberId);
            obj.updateById();
            //剩余一条了表示砍价已完成
            if(list.size()<=1){
                this.bargainOrderService.update(new UpdateWrapper<BargainOrder>().set("status",2).eq("id",bargain_order_id));
            }
            return obj.getPrice();
        }else{
            throw new BusinessException("该订单砍价已完成");
        }
    }



    /**
     * 发起砍价
     */
    @Transactional(rollbackFor = Exception.class)
    public  String add_Bargain(String bargain_id, String memberId) {
        var data=this.getById(bargain_id);
        var i=querySurplus(data.getId());
        if(i<=0){
            throw new BusinessException("很遗憾，发起砍价的机会已用完");
        }
        var a=bargainOrderService.count(new QueryWrapper<BargainOrder>().eq("bargain_id", data.getId()).eq("member_id",memberId));
        if(a>0){
            throw new BusinessException("您已经发起过一次，请勿重复操作");
        }

        var list= bargainStepService.list(new QueryWrapper<BargainStep>().eq("bargain_id",data.getId()).orderByAsc("sort"));
        //订单
        var order =new BargainOrder();
        order.setMemberId(memberId);
        order.setCargoId(data.getCargoId());
        order.setCount(1);
        order.setBargainId(bargain_id);
        order.setFloorPrice(data.getFloorPrice());
        order.setStatus(1);
        order.insert();

        //步骤
        list.stream().forEach(p->{
            BargainOrderFlow addBean=new BargainOrderFlow();
            addBean.setCargoId(data.getCargoId());
            addBean.setBargainOrderId(order.getId());
            addBean.setBargainId(data.getId());
            addBean.setPrice(p.getPrice());
            addBean.setSort(p.getSort());
            addBean.setStatus(0);
            addBean.insert();
        });
        return order.getId();

    }
}