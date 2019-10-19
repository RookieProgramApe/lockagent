package com.lxkj.entity;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableField;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.models.auth.In;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 砍价活动
 * </p>
 *
 * @author 一个烧包
 * @since 2019-08-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "Bargain对象", description = "砍价活动")
public class Bargain extends Model<Bargain> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "砍价活动ID")
    @TableId(value = "id", type = IdType.UUID)
    private String id;


    @ApiModelProperty(value = "商品ID",hidden = true)
    @TableField("cargo_id")
    private String cargoId;


    @ApiModelProperty(value = "开放库存（剩余机会）",hidden = true)
    @TableField("stock")
    private Integer stock;


    @ApiModelProperty(value = "最低价")
    @TableField("floor_price")
    private BigDecimal floorPrice;


    @ApiModelProperty(value = "虚拟参与人数",hidden = true)
    @TableField("base_count")
    private Integer baseCount;


    @ApiModelProperty(value = "砍价说明（富文本）")
    @TableField("cnts")
    private String cnts;


    @ApiModelProperty(value = "0 下架，1 上架",hidden = true)
    @TableField("status")
    private Integer status;


    @ApiModelProperty(value = "创建时间")
    @TableField("create_time")
    private Date createTime;


    @ApiModelProperty(value = "0未删除 1已删除",hidden = true)
    @TableField("isdel")
    private Integer isdel;


    @ApiModelProperty(value = "序号",hidden = true)
    @TableField("sort")
    private Integer sort;


    @ApiModelProperty(value = "微信分享内容")
    @TableField("share_content")
    private String shareContent;



    @ApiModelProperty(value = "微信分享图片")
    @TableField("share_pic")
    private String sharePic;

    @ApiModelProperty(value = "商品信息")
    @TableField(exist = false)
    private Cargo cargo;


    @ApiModelProperty(value = "剩余库存(剩余机会)")
    @TableField(exist = false)
    private Integer kz;

    @ApiModelProperty(value = "原售价(划线)")
    @TableField(exist = false)
    private BigDecimal originalPrice;

    @ApiModelProperty(value = "正在参与人数")
    @TableField(exist = false)
    private Integer doIng;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
