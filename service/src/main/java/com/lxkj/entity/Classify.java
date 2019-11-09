package com.lxkj.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author Zhanqian
 * @date 2019/11/7 15:31
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "Classify对象", description = "商品分类")
public class Classify extends Model<Classify> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "分类id")
    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "分类名称")
    @TableField("name")
    private String name;

    @ApiModelProperty(value = "分类图片")
    @TableField("img")
    private String img;

    @ApiModelProperty(value = "分类排序")
    @TableField("sort")
    private Integer sort;

    @ApiModelProperty(value = "是否删除 0否 1是")
    @TableField("is_del")
    private Integer isDel;

    @ApiModelProperty(value = "状态 0下架 1上架")
    @TableField("`status`")
    private Integer status;

    @ApiModelProperty(value = "创建时间")
    @TableField("create_time")
    private Date createTime;

    @ApiModelProperty(value = "商品列表")
    @TableField(exist = false)
    private List<Cargo> cargoList = List.of();

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}