package com.lxkj.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
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
    @TableField(value = "`remark`")
    private String remark;

    @ApiModelProperty(value = "匿名状态（0匿名 1非匿名）")
    @TableField(value = "status")
    private String status;

    @ApiModelProperty(value = "星级")
    @TableField(value = "star")
    private Integer star;

    @ApiModelProperty(value = "创建时间")
    @TableField(value = "create_time")
    private Date createTime;

    @ApiModelProperty(value = "是否删除（0未删除 1已删除）", hidden = true)
    @TableField(value = "is_del")
    private String isDel;

    @ApiModelProperty(value = "是否置顶（0不置顶 1置顶）")
    @TableField(value = "is_top")
    private String isTop;

    @ApiModelProperty(value = "会员id")
    @TableField(value = "member_id")
    private String memberId;

    @ApiModelProperty(value = "商品id")
    @TableField(value = "cargo_id")
    private String cargoId;

    @ApiModelProperty(value = "订单id")
    @TableField(value = "order_id")
    private String orderId;

    @ApiModelProperty(value = "评论人")
    @TableField(value = "member_name")
    private String memberName;

    @ApiModelProperty(value = "评论人头像")
    @TableField(value = "member_avatar")
    private String memberAvatar;

    @ApiModelProperty(value = "商品名称")
    @TableField(value = "cargo_name")
    private String cargoName;

    @ApiModelProperty(value = "商品规格")
    @TableField(value = "sku_name")
    private String skuName;

    @ApiModelProperty(value = "商品套餐")
    @TableField(value = "cate_name")
    private String cateName;

    @ApiModelProperty(value = "商品封面")
    @TableField(value = "cargo_img")
    private String cargoImg;

    @ApiModelProperty(value = "评论人信息")
    @TableField(exist = false)
    private Member member;

    @ApiModelProperty(value = "订单信息")
    @TableField(exist = false)
    private Order order;

    @ApiModelProperty(value = "商品信息")
    @TableField(exist = false)
    private Cargo cargo;

    @ApiModelProperty(value = "图片信息")
    @TableField(exist = false)
    private List<AppraiseAttachment> imgs = List.of();


    @ApiModelProperty(value = "评价类型 1普通商品 2积分商品")
    @TableField(value = "`type`")
    private Integer type;



    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}
