package com.lxkj.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Zhanqian
 * @date 2019/10/23 17:08
 * 评价的实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "Appraise评价实体类", description = "评价")
public class Appraise extends Model<Appraise> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "评价id")
    @TableId(value = "`id`", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "描述")
    @TableId(value = "`remark`")
    private String remark;

    @ApiModelProperty(value = "匿名状态（0匿名 1非匿名）")
    @TableId(value = "status")
    private String status;

    @ApiModelProperty(value = "星级")
    @TableId(value = "star")
    private Integer star;

    @ApiModelProperty(value = "创建时间")
    @TableId(value = "create_time")
    private Date createTime;

    @ApiModelProperty(value = "是否删除（0未删除 1已删除）", hidden = true)
    @TableId(value = "is_del")
    private String isDel;

    @ApiModelProperty(value = "是否置顶（0不置顶 1置顶）")
    @TableId(value = "is_top")
    private String isTop;

    @ApiModelProperty(value = "会员id")
    @TableId(value = "member_id")
    private String memberId;

    @ApiModelProperty(value = "商品id")
    @TableId(value = "cargo_id")
    private String cargoId;

    @ApiModelProperty(value = "订单id")
    @TableId(value = "order_id")
    private String orderId;

    @ApiModelProperty(value = "评论人")
    @TableId(value = "member_name")
    private String memberName;

    @ApiModelProperty(value = "评论人头像")
    @TableId(value = "member_avatar")
    private String memberAvatar;

    @ApiModelProperty(value = "商品名称")
    @TableId(value = "cargo_name")
    private String cargoName;

    @ApiModelProperty(value = "商品规格")
    @TableId(value = "sku_name")
    private String skuName;

    @ApiModelProperty(value = "商品套餐")
    @TableId(value = "cate_name")
    private String cateName;

    @ApiModelProperty(value = "商品封面")
    @TableId(value = "cargo_img")
    private String cargoImg;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}
