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
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 订单
 * </p>
 *
 * @author 一个烧包
 * @since 2019-07-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("`order`")
@ApiModel(value = "Order对象", description = "订单")
public class Order extends Model<Order> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "订单ID")
    @TableId(value = "id", type = IdType.UUID)
    private String id;


    @ApiModelProperty(value = "物流编号")
    @TableField("sequence")
    private String sequence;


    @ApiModelProperty(value = "会员ID")
    @TableField("member_id")
    private String memberId;


    @ApiModelProperty(value = "1 临售商品，2 砍价商品")
    @TableField("type")
    private Integer type;


    @ApiModelProperty(value = "购买商品时使用的卡片ID",hidden = true)
    @TableField("giftcard_id")
    private String giftcardId;

    @ApiModelProperty(value = "砍价订单ID",hidden = true)
    @TableField("bargain_order_id")
    private String bargainOrderId;

    @ApiModelProperty(value = "单价")
    @TableField("unit_price")
    private BigDecimal unitPrice;


    @ApiModelProperty(value = "购买数量")
    @TableField("count")
    private Integer count;


    @ApiModelProperty(value = "积分抵扣",hidden = true)
    @TableField("credit")
    private BigDecimal credit;


    @ApiModelProperty(value = "积分抵扣金额",hidden = true)
    @TableField("credit_discount")
    private BigDecimal creditDiscount;


    @ApiModelProperty(value = "合计金额")
    @TableField("total_price")
    private BigDecimal totalPrice;

    @ApiModelProperty(value = "使用积分")
    @TableField("point")
    private BigDecimal point;


    @ApiModelProperty(value = "2 已支付，3 已发货，4 已完成")
    @TableField("status")
    private Integer status;


    @ApiModelProperty(value = "快递公司")
    @TableField("delivery_provider")
    private String deliveryProvider;


    @ApiModelProperty(value = "物流运单号")
    @TableField("delivery_track")
    private String deliveryTrack;


    @ApiModelProperty(value = "收件人",hidden = true)
    @TableField("recipient")
    private String recipient;


    @ApiModelProperty(value = "收件人号码",hidden = true)
    @TableField("mobile")
    private String mobile;


    @ApiModelProperty(value = "省",hidden = true)
    @TableField("province")
    private String province;


    @ApiModelProperty(value = "市",hidden = true)
    @TableField("city")
    private String city;


    @ApiModelProperty(value = "区县",hidden = true)
    @TableField("county")
    private String county;


    @ApiModelProperty(value = "详细地址",hidden = true)
    @TableField("address")
    private String address;


    @ApiModelProperty(value = "备注")
    @TableField("remark")
    private String remark;


    @ApiModelProperty(value = "商品ID")
    @TableField("cargo_id")
    private String cargoId;


    @ApiModelProperty(value = "商品名称")
    @TableField("cargo_name")
    private String cargoName;


    @ApiModelProperty(value = "商品图片")
    @TableField("cargo_image")
    private String cargoImage;

    @ApiModelProperty(value = "ip地址")
    @TableField("ipaddr")
    private String ipaddr;

    @ApiModelProperty(value = "SKU ID",hidden = true)
    @TableField("sku_id")
    private String skuId;


    @ApiModelProperty(value = "SKU名称")
    @TableField("sku_name")
    private String skuName;

    @ApiModelProperty(value = "套餐id",hidden = true)
    @TableField("cate_id")
    private String cateId;


    @ApiModelProperty(value = "套餐名称")
    @TableField("cate_name")
    private String cateName;


    @ApiModelProperty(value = "创建时间")
    @TableField("create_time")
    private Date createTime;

    @ApiModelProperty(value = "运单导入时间")
    @TableField("import_time")
    private Date importTime;

    @ApiModelProperty(value = "支付参数",hidden = true)
    @TableField("paycnts")
    private String paycnts;

    @ApiModelProperty(value = "唯一订单号",hidden = true)
    @TableField("ordernum")
    private String ordernum;

    @ApiModelProperty(value = "发件人姓名",hidden = true)
    @TableField(exist=false)
    private String addresserName;

    @ApiModelProperty(value = "发件人邮编",hidden = true)
    @TableField(exist=false)
    private String addresserCode;

    @ApiModelProperty(value = "发件人电话",hidden = true)
    @TableField(exist=false)
    private String addresserMobile;

    @ApiModelProperty(value = "发件人省",hidden = true)
    @TableField(exist=false)
    private String addresserProvince;

    @ApiModelProperty(value = "发件人市",hidden = true)
    @TableField(exist=false)
    private String addresserCity;


    @ApiModelProperty(value = "发件人区",hidden = true)
    @TableField(exist=false)
    private String addresserCounty;


    @ApiModelProperty(value = "发件人详细地址")
    @TableField(exist=false)
    private String addresserAddress;


    @ApiModelProperty(value = "提货卡号")
    @TableField(exist=false)
    private String giftcardNum;

    @ApiModelProperty(value = "客服电话")
    @TableField(exist=false)
    private String phone;

    @ApiModelProperty(value = "是否已评价（0未评价 1已评价）")
    @TableField("appraise_id")
    private String appraiseId;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
