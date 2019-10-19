package com.lxkj.entity;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableField;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 砍价订单
 * </p>
 *
 * @author 一个烧包
 * @since 2019-08-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BargainOrder对象", description = "砍价订单")
public class BargainOrder extends Model<BargainOrder> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "砍价订单ID")
    @TableId(value = "id", type = IdType.UUID)
    private String id;


    @ApiModelProperty(value = "会员ID",hidden = true)
    @TableField("member_id")
    private String memberId;


    @ApiModelProperty(value = "商品ID",hidden = true)
    @TableField("cargo_id")
    private String cargoId;


    @ApiModelProperty(value = "商品购买数量",hidden = true)
    @TableField("count")
    private Integer count;


    @ApiModelProperty(value = "砍价活动ID",hidden = true)
    @TableField("bargain_id")
    private String bargainId;


    @ApiModelProperty(value = "商品最低价")
    @TableField("floor_price")
    private BigDecimal floorPrice;


    @ApiModelProperty(value = "1砍价未完成(显示喊好友砍一刀按钮) 2砍价已完成(显示立即购买) 3已购买(不展示任何按钮)")
    @TableField("status")
    private Integer status;


    @ApiModelProperty(value = "创建时间")
    @TableField("create_time")
    private Date createTime;


    /***************商品信息********************/
    @ApiModelProperty(value = "商品信息")
    @TableField(exist = false)
    private Cargo cargo;

    @ApiModelProperty(value = "商品销量")
    @TableField(exist = false)
    private Integer saleNum;

    @ApiModelProperty(value = "商品封面地址")
    @TableField(exist = false)
    private String picture;

    @ApiModelProperty(value = "商品原售价")
    @TableField(exist = false)
    private BigDecimal originalPrice;

    @ApiModelProperty(value = "商品详情资料（富文本）")
    @TableField(exist = false)
    private String description;
    /*********************砍价活动信息***********************/
    @ApiModelProperty(value = "微信分享内容")
    @TableField(exist = false)
    private String shareContent;

    @ApiModelProperty(value = "微信分享图片")
    @TableField(exist = false)
    private String sharePic;

    @ApiModelProperty(value = "砍价说明（富文本）")
    @TableField(exist = false)
    private String cnts;

    @ApiModelProperty(value = "当前成交价格合计")
    @TableField(exist = false)
    private BigDecimal endPrice;

    @ApiModelProperty(value = "已砍金额合计")
    @TableField(exist = false)
    private BigDecimal ykPrice;

    @ApiModelProperty(value = "已砍次数合计")
    @TableField(exist = false)
    private Integer ykCount;

    @ApiModelProperty(value = "剩余次数合计（还需要完成次数）")
    @TableField(exist = false)
    private Integer wkCount;
    /*********************用户砍价结果***********************/

    @ApiModelProperty(value = "帮TA砍了多少金额(当前用户)")
    @TableField(exist = false)
    private BigDecimal heplePrice;

    @ApiModelProperty(value = "是否可以帮Ta砍(true=已砍过，false=未砍过)")
    @TableField(exist = false)
    private Boolean isHelp;



    @ApiModelProperty(value = "发起人昵称")
    @TableField(exist = false)
    private String memberName;
    @ApiModelProperty(value = "发起人头像")
    @TableField(exist = false)
    private String memberAvatar;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
