package com.lxkj.service;

import com.lxkj.entity.Member;
import com.lxkj.entity.Retailer;
import com.lxkj.facade.WxService;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 通过微信公众号向用户推送信息
 * @author Zhanqian
 * @date 2019/11/21 16:42
 */
@Service
public class WXMessageService {

    @Value("${send_message.template_id}")
    private String[] templateId;

    @Value("${send_message.url}")
    private String[] url;

    @Autowired
    private WxService wxService;
    @Autowired
    private RetailerService retailerService;
    @Autowired
    private MemberService memberService;

    /**
     * 发送代理商信息
     * @param retailerId 代理商id
     */
    public void sendRetailerMessage(String retailerId) {
        if (StringUtils.isNotBlank(retailerId)) {
            // 获取代理商信息
            Retailer retailer = retailerService.getById(retailerId);
            Member member = memberService.getById(retailer.getMemberId());
            Integer type = retailer.getType();
            // 判断代理商是否审核已通过
            if(retailer.getStatus().equals(1)){
                WxMpTemplateMessage message = new WxMpTemplateMessage();
                List<WxMpTemplateData> data = new ArrayList<>();
                WxMpTemplateData d = new WxMpTemplateData();
                d.setName("first");
                d.setValue("您好" + retailer.getName() + "，恭喜您成为" + (type==1?"安纹事业合伙人":type==2?"安纹合伙人":type==3?"安纹微股东":"") + "。");
                d.setColor("#173177");
                data.add(d);
                d = new WxMpTemplateData();
                d.setName("keyword1");
                d.setValue(new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss").format(retailer.getCreateTime()));
                d.setColor("#173177");
                data.add(d);
                d = new WxMpTemplateData();
                d.setName("keyword2");
                d.setValue("审核通过");
                d.setColor("#67C23A");
                data.add(d);
                d = new WxMpTemplateData();
                d.setName("remark");
                d.setValue("点击完善个人信息");
                d.setColor("#173177");
                data.add(d);
                message.setData(data);
                message.setToUser(member.getOpenId());
                if (type == 1){
                    message.setUrl(url[type-1]);
                    message.setTemplateId(templateId[type-1]);
                }else if (type == 2){
                    message.setUrl(url[type-1]);
                    message.setTemplateId(templateId[type-1]);
                }else if (type == 3){
                    message.setUrl(url[type-1]);
                    message.setTemplateId(templateId[type-1]);
                }else {
                    message.setUrl("https://aw.wisehuitong.com/");
                }

                // 抛出可能的异常
                try {
                    wxService.getTemplateMsgService().sendTemplateMsg(message);
                }catch (WxErrorException e){
                    e.printStackTrace();
                }

            }
        }
    }

    /**
     * 发送提货奖励提醒
     * @param serial 卡号
     * @param cargoName 商品名称
     * @param money 金额
     * @param time 时间
     * @param type 代理商类型
     */
    public void thSendMessage(String serial, String cargoName, String money, Date time, Integer type, String openId, String storeName) {
        WxMpTemplateMessage message = new WxMpTemplateMessage();
        List<WxMpTemplateData> data = new ArrayList<>();
        WxMpTemplateData d = new WxMpTemplateData();
        d.setName("first");
        if (type == 1){
            d.setValue("您好，尊敬的安纹事业合伙人，您的微股东「" + storeName + "」名下的卡片「" + serial + "」已经完成提货。");
        }else if (type == 2){
            d.setValue("您好，尊敬的安纹合伙人，您的微股东「" + storeName + "」名下的卡片「" + serial + "」已经完成提货。");
        }else if (type == 3){
            d.setValue("您好，尊敬的安纹微股东，您名下的卡片「" + serial + "」已经完成提货。");
        }else {
            d.setValue("您好，您名下的卡片「" + serial + "」已经完成提货。");
        }
        d.setColor("#173177");
        data.add(d);
        d = new WxMpTemplateData();
        d.setName("keyword1");
        d.setValue(money + "元");
        d.setColor("#F56C6C");
        data.add(d);
        d = new WxMpTemplateData();
        d.setName("keyword2");
        d.setValue(new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss").format(time));
        d.setColor("#173177");
        data.add(d);
        d = new WxMpTemplateData();
        d.setName("remark");
        d.setValue("提货佣金已经到账，点击查看提货佣金明细。");
        d.setColor("#173177");
        data.add(d);
        message.setTemplateId(templateId[3]);
        message.setUrl(url[3] + "&identity=" + type);

        message.setData(data);
        message.setToUser(openId);

        // 抛出可能的异常
        try {
            wxService.getTemplateMsgService().sendTemplateMsg(message);
        }catch (WxErrorException e){
            e.printStackTrace();
        }
    }

    /**
     * 合伙人裂变收益提醒
     * @param param
     */
    public void lbSendMessage(Map<String, Object> param) {
        WxMpTemplateMessage message = new WxMpTemplateMessage();
        List<WxMpTemplateData> data = new ArrayList<>();
        WxMpTemplateData d = new WxMpTemplateData();
        d.setName("first");
        if (param.get("type").equals(1)){
            d.setValue("您好，尊敬的安纹事业合伙人，您邀请的用户「" + param.get("retailerName") + "」已成功加入安纹事业合伙人。");
        }else if (param.get("type").equals(2)){
            d.setValue("您好，尊敬的安纹合伙人，您邀请的用户「" + param.get("retailerName") + "」已成功加入安纹合伙人。");
        }else if (param.get("type").equals(3)){
            d.setValue("您好，尊敬的安纹微股东，您邀请的用户「" + param.get("retailerName") + "」已成功加入安纹微股东。");
        }else {
            d.setValue("您好，尊敬的用户，您邀请的用户「" + param.get("retailerName") + "」已成功加入安纹。");
        }
        d.setColor("#173177");
        data.add(d);
        d = new WxMpTemplateData();
        d.setName("keyword1");
        d.setValue("裂变收益");
        d.setColor("#173177");
        data.add(d);
        d = new WxMpTemplateData();
        d.setName("keyword2");
        d.setValue(param.get("money").toString() + "元");
        d.setColor("#F56C6C");
        data.add(d);
        d = new WxMpTemplateData();
        d.setName("keyword3");
        d.setValue(new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss").format(param.get("time")));
        d.setColor("#173177");
        data.add(d);
        d = new WxMpTemplateData();
        d.setName("keyword4");
        d.setValue(param.get("banlance").toString() + "元");
        d.setColor("#173177");
        data.add(d);
        d = new WxMpTemplateData();
        d.setName("remark");
        d.setValue("裂变收益已经到账，点击查看收益明细。");
        d.setColor("#173177");
        data.add(d);
        message.setTemplateId(templateId[4]);
        message.setUrl(url[4] + "&identity=" + param.get("type"));

        message.setData(data);
        message.setToUser(param.get("openId").toString());
        // 抛出可能的异常
        try {
            wxService.getTemplateMsgService().sendTemplateMsg(message);
        }catch (WxErrorException e){
            e.printStackTrace();
        }
    }
}
