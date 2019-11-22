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
import java.util.List;

/**
 * @author Zhanqian
 * @date 2019/11/21 16:42
 */
@Service
public class WXMessageService {

    @Value("${send_message.template_id}")
    private String templateId;

    @Value("${send_message.url}")
    private String[] url;

    @Value("${send_message.topcolor}")
    private String topcolor;

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
    public void sendRetailerMessage(String retailerId) throws WxErrorException {
        if (StringUtils.isNotBlank(retailerId)) {
            // 获取代理商信息
            Retailer retailer = retailerService.getById(retailerId);
            Member member = memberService.getById(retailer.getMemberId());
            Integer type = retailer.getType();
            // 判断代理商是否审核已通过
            if(retailer.getStatus().equals(1)){
                WxMpTemplateMessage message = new WxMpTemplateMessage();
                message.setTemplateId(templateId);
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
                }else if (type == 2){
                    message.setUrl(url[type-1]);
                }else if (type == 3){
                    message.setUrl(url[type-1]);
                }else {
                    message.setUrl("https://aw.wisehuitong.com/");
                }
                wxService.getTemplateMsgService().sendTemplateMsg(message);
            }
        }
    }
}
