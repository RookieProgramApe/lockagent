package com.lxkj.api;

import com.lxkj.common.bean.BaseController;
import com.lxkj.common.bean.JsonResults;
import com.lxkj.service.AppraiseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Zhanqian
 * @date 2019/10/24 9:56
 */
@Api(tags = "评价接口")
@Slf4j
@RestController
@RequestMapping("/api/appraise")
public class AppraiseController extends BaseController {

    @Autowired
    private AppraiseService appraiseService;

    @ApiOperation("商品详情")
    @PostMapping("/save")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "star", dataType = "String", value = "星级", required = true),
            @ApiImplicitParam(name = "describe", dataType = "String", value = "描述", required = true)
    })
    public JsonResults queryCargoDetail(@RequestParam String star, @RequestParam String describe) {
//        Cargo data = this.cargoService.getById(id);
//        cargoService.getData(data);
        return BuildSuccessJson("查询成功");
    }

}
