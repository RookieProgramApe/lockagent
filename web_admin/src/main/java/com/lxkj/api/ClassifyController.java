package com.lxkj.api;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lxkj.common.bean.BaseController;
import com.lxkj.common.bean.JsonResults;
import com.lxkj.entity.Cargo;
import com.lxkj.entity.Classify;
import com.lxkj.service.CargoService;
import com.lxkj.service.ClassifyService;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Zhanqian
 * @date 2019/11/7 18:08
 */
@Api(tags = "商品分类接口")
@Slf4j
@RestController
@RequestMapping("/api/classify")
public class ClassifyController extends BaseController {

    @Autowired
    private ClassifyService classifyService;

    @Autowired
    private CargoService cargoService;

    @ApiOperation("商品分类分页列表(已上架)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "keyword", value = "模糊搜索[关键字]"),
            @ApiImplicitParam(paramType = "query", dataType = "Long", name = "page", value = " 页码", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "Long", name = "limit", value = "每页记录数", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "data:[{name:名称, img:图片, sort:排序}]"),
    })
    @PostMapping("/all")
    public JsonResults<List<Classify>> queryClassifyList(Long page, Long limit, String keyword) {
        IPage<Classify> data = this.classifyService.page(
                new Page<Classify>(page != null ? page : 1, limit != null ? limit : 10),
                new QueryWrapper<Classify>()
                        .eq("status", 1)
                        .eq("is_del", 0)
                        .like(StringUtils.isNotBlank(keyword), "name", keyword)
                        .orderByAsc("sort"));
        return BuildSuccessJson(data.getRecords(), data.getPages(), "查询成功");
    }

    @ApiOperation("获取指定分类下面的所有商品(已上架)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "keyword", value = "模糊搜索[关键字]"),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "classifyId", value = "分类id", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "data:[{name:名称, img:图片, sort:排序, cargoList:[]商品数组}]"),
    })
    @PostMapping("/getCargoListByClassify")
    public JsonResults<Classify> queryCargoList(Long page, Long limit, String keyword, String classifyId) {

        Classify classify = classifyService.getById(classifyId);
        List<Cargo> cargoList = cargoService.list(new QueryWrapper<Cargo>().eq("classify_id", classifyId));
        // 获取商品的封面和附件
        cargoList.stream().forEach(cargo -> cargoService.getData(cargo));
        classify.setCargoList(cargoList);

        return BuildSuccessJson(classify, "查询成功");
    }
}
