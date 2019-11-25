package com.lxkj.api;

import com.lxkj.facade.WxService;
import com.lxkj.service.WXMessageService;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Zhanqian
 * @date 2019/11/21 16:10
 */
@RestController
@RequestMapping("/api/test")
public class TestController {

    @Value("${send_message.template_id}")
    private String templateId;

    @Value("${send_message.url}")
    private String[] url;

    @Autowired
    private WxService wxService;
    @Autowired
    private WXMessageService wxMessageService;

//    @RequestMapping("/test1")
//    public String test1() throws WxErrorException {
//        System.out.println(templateId);
//        System.out.println(url.length);
//        System.out.println(topcolor);
//        System.out.println(wxService.getAccessToken(true));
//        return "SUCCESS";
//    }

    @RequestMapping("/test1")
    public String test1(@RequestParam Integer type, @RequestParam String retailerId) throws WxErrorException {
//        System.out.println(templateId);
//        System.out.println(url.length);
//        System.out.println(topcolor);
//        System.out.println(wxService.getAccessToken(true));
        wxMessageService.sendRetailerMessage(retailerId);
        return "SUCCESS";
    }

}
