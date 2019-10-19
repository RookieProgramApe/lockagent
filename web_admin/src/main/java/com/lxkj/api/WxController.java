package com.lxkj.api;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lxkj.annotation.LoginRequired;
import com.lxkj.common.bean.BaseController;
import com.lxkj.common.bean.FileMappingProperties;
import com.lxkj.common.bean.JsonResults;
import com.lxkj.common.util.ID;
import com.lxkj.common.util.Strings;
import com.lxkj.common.util.collection.Lists;
import com.lxkj.entity.Member;
import com.lxkj.facade.WxService;
import com.lxkj.service.MemberService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.io.BufferedInputStream;
import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.WxJsapiSignature;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpQrcodeService;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Api(tags = "微信相关接口", description = "微信授权登陆、获取界面签名")
@Slf4j
@RestController
@RequestMapping("/api")
public class WxController extends BaseController {

  @Autowired
  private MemberService memberService;

  @Autowired
  private WxService wxService;

  @Autowired
  private FileMappingProperties fileMapping;

  @ApiOperation(value = "微信授权登陆")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "code", value = "微信CODE", required = true),
  })
  @ApiResponses({
      @ApiResponse(code = 200, message = "{data:用户token值}")
  })
  @PostMapping("/wx/login")
  @Transactional
  public JsonResults<String> wxLogin(@RequestParam String code) {
    if (Strings.isEmpty(code)) {
      log.warn("微信CODE不能为空");
      return this.BuildFailJson("操作失败");
    }
    try {
      WxMpOAuth2AccessToken accessToken = this.wxService.oauth2getAccessToken(code);
      WxMpUser user = this.wxService.oauth2getUserInfo(accessToken, "zh_CN");
      Member member = memberService.getOne(new QueryWrapper<Member>().eq("open_id", user.getOpenId()));
      if (member == null) {
        member = new Member();
        member.setOpenId(user.getOpenId());
        member.setUnionId(user.getUnionId());
      }
      member.setAvatar(user.getHeadImgUrl());
      member.setNickname(user.getNickname());
      member.setGender(user.getSexDesc());
      this.memberService.saveOrUpdate(member);
      return this.BuildSuccessJson(member.getId(), "登陆成功");
    } catch (Exception e) {
      if (e instanceof WxErrorException) {
        log.warn(e.getLocalizedMessage());
      } else {
        log.error(e.getMessage(), e);
      }
      return this.BuildFailJson("登陆失败");
    }
  }

  @ApiOperation(value = "获取页面签名（仅公众号）")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "url", value = "页面地址", required = true)
  })
  @RequestMapping(value = "/wx/sign", method = {RequestMethod.POST, RequestMethod.GET})
  public JsonResults<WxJsapiSignature> wxSign(@RequestParam String url) {
    try {
      WxJsapiSignature data = this.wxService.createJsapiSignature(url);
      return this.BuildSuccessJson(data, "操作成功");
    } catch (WxErrorException e) {
      log.error(e.getLocalizedMessage(), e);
      return this.BuildFailJson("操作失败");
    }
  }

  /**
   * 获取短链接
   */
  @ApiOperation(value = "获取短链接")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "url", value = "链接地址（仅公众号）", required = true)
  })
  @RequestMapping(value = "/wx/shortUrl", method = {RequestMethod.GET})
  @LoginRequired
  public JsonResults<String> wxShortUrl(@RequestParam String url) {
    try {
      String data = this.wxService.shortUrl(url);
      return this.BuildSuccessJson(data, "操作成功");
    } catch (WxErrorException e) {
      log.error(e.getMessage(), e);
      return this.BuildFailJson("操作失败");
    }
  }

  /**
   * 根据微信文件ID保存本地文件
   */
  @ApiOperation(value = "文件上传（仅公众号）")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "file", value = "微信文件ID数组", required = true),
      @ApiImplicitParam(name = "model", value = "目录", defaultValue = "sand")
  })
  @PostMapping("/wx/file/upload")
  public JsonResults<List<String>> uploadFile(@RequestParam("file") String[] files) {
    String time = LocalDateTime.now().format(DateTimeFormatter.BASIC_ISO_DATE);
    String model = "lockagent";
    //文件存放根路径
    String docPath = fileMapping.getPath() + File.separator + model + File.separator + time;
    List<String> urls = Lists.of();
    for (String file : files) {
      if (file.isEmpty()) {
        continue;
      }
      try {
        int retry = 0;
        boolean succeed = false;
        byte[] content;
        do {
          log.info("Retry for the {}th time.", (retry + 1));
          String accessToken = this.wxService.getAccessToken();
          if (retry != 0) {
            accessToken = this.wxService.getAccessToken(true);
          }
          String requestUrl = "http://file.api.weixin.qq.com/cgi-bin/media/get?access_token=" + accessToken + "&media_id=" + file;
          URL query = new URL(requestUrl);
          HttpURLConnection conn = (HttpURLConnection) query.openConnection();
          conn.setDoInput(true);
          conn.setRequestMethod("GET");
          BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
          content = IOUtils.toByteArray(bis);
          String string = new String(content, StandardCharsets.UTF_8);
          if (string.contains("errmsg")) {
            log.error(string);
            Thread.sleep(1000L);
          } else {
            succeed = true;
          }
          retry++;
        } while (!succeed && retry < 5);
        //文件名
        String fileName = ID.nextSnowflakeId() + ".jpg";
        //文件全路径
        File fileAllPath = new File(docPath + File.separator + fileName);
        if (!fileAllPath.getParentFile().exists()) {
          fileAllPath.getParentFile().mkdirs();
        }
        Files.write(fileAllPath.toPath(), content);
        String url = fileMapping.getMapping() + "/" + model + "/" + time + "/" + fileName;
        urls.add(url);
      } catch (Exception e) {
        log.error(e.getMessage(), e);
        return this.BuildFailJson("上传文件失败");
      }
    }
    return this.BuildSuccessJson(urls, "上传文件成功");
  }

  @ApiOperation(value = "文件上传")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "file", value = "文件", required = true)
  })
  @PostMapping("/file/upload")
  public JsonResults<List<String>> uploadFile(@RequestParam("file") MultipartFile[] files) {
    String time = LocalDateTime.now().format(DateTimeFormatter.BASIC_ISO_DATE);
    String model = "lockagent";
    //文件存放根路径
    String docPath = fileMapping.getPath() + File.separator + model + File.separator + time;
    List<String> urls = Lists.of();
    for (MultipartFile file : files) {
      if (file.isEmpty()) {
        continue;
      }
      //文件名
      String fileName = ID.nextGUID() + file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
      //文件全路径
      File fileAllPath = new File(docPath + File.separator + fileName);
      if (!fileAllPath.getParentFile().exists()) {
        fileAllPath.getParentFile().mkdirs();
      }
      try {
        file.transferTo(fileAllPath);
        String url = fileMapping.getMapping() + "/" + model + "/" + time + "/" + fileName;
        urls.add(url);
      } catch (Exception e) {
        log.error(e.getMessage(), e);
        return this.BuildFailJson("上传文件失败");
      }
    }
    return this.BuildSuccessJson(urls, "上传文件成功");
  }

  /**
   * 获取微信二维码（公众号）
   */
  @ApiOperation(value = "获取微信二维码（公众号）")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "scene", value = "场景", required = true)
  })
  @PostMapping("/wx/qrCode")
  @LoginRequired
  public JsonResults<WxMpQrCodeTicket> wxOaQrCode(String scene) {
    try {
      WxMpQrcodeService qrcodeService = this.wxService.getQrcodeService();
      WxMpQrCodeTicket ticket = qrcodeService.qrCodeCreateTmpTicket(scene, 2592000);
      return this.BuildSuccessJson(ticket, "操作成功");
    } catch (WxErrorException e) {
      log.error(e.getMessage(), e);
      return this.BuildFailJson("操作失败");
    }
  }

}
