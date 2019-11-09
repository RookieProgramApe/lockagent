package com.lxkj.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.lxkj.common.util.collection.Lists;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 商品
 * </p>
 *
 * @author 一个烧包
 * @since 2019-07-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "Cargo对象", description = "商品")
public class Cargo extends Model<Cargo> {

  private static final long serialVersionUID = 1L;

  @ApiModelProperty(value = "商品ID")
  @TableId(value = "id", type = IdType.UUID)
  private String id;

  @ApiModelProperty(value = "代理商商品提货奖励设置ID")
  @TableField(exist=false)
  private String retailerRewardId;

  @ApiModelProperty(value = "商品名称")
  @TableField("name")
  private String name;

  @ApiModelProperty(value = "客服电话")
  @TableField("tellphone")
  private String tellphone;

  @ApiModelProperty(value = "原价")
  @TableField("original_price")
  private BigDecimal originalPrice;


  @ApiModelProperty(value = "现价")
  @TableField("sale_price")
  private BigDecimal salePrice;


  @ApiModelProperty(value = "虚拟销量",hidden = true)
  @TableField("base_sale_count")
  private Integer baseSaleCount;


  @ApiModelProperty(value = "商品详情资料（富文本）")
  @TableField("description")
  private String description;


  @ApiModelProperty(value = "0 下架，1 上架",hidden = true)
  @TableField("status")
  private Integer status;


  @ApiModelProperty(value = "创建时间")
  @TableField("create_time")
  private Date createTime;

  @ApiModelProperty(value = "序号",hidden = true)
  @TableField("sort")
  private Integer sort;

  @ApiModelProperty(value = "开启卡片验证 0关闭 1开启")
  @TableField("iscard")
  private Integer iscard;

  @ApiModelProperty(value = "种类(规格)")
  @TableField(exist = false)
  private List<CargoSku> sku = Lists.of();

  @ApiModelProperty(value = "套餐")
  @TableField(exist = false)
  private List<CargoCategory> categoryList = Lists.of();

  @ApiModelProperty(value = "附件列表",hidden = true)
  @TableField(exist = false)
  private List<CargoAttachment> attachment = Lists.of();

  @ApiModelProperty(value = "封面地址")
  @TableField(exist = false)
  private String picture;

  @ApiModelProperty(value = "视频地址")
  @TableField(exist = false)
  private String mov;


  @ApiModelProperty(value = "销量")
  @TableField(exist = false)
  private Integer saleNum;

  @ApiModelProperty(value = "奖励金额")
  @TableField(exist = false)
  private BigDecimal figure;

  @ApiModelProperty(value = "奖励金额")
  @TableField(exist = false)
  private BigDecimal figureb;

  @ApiModelProperty(value = "奖励金额")
  @TableField(exist = false)
  private BigDecimal figurec;

  @ApiModelProperty(value = "0 下架，1 上架",hidden = true)
  @TableField("isdel")
  private Integer isdel;

  @ApiModelProperty(value = "1普通商品，2积分商品")
  @TableField("`type`")
  private Integer type;

  @ApiModelProperty(value = "积分")
  @TableField("point")
  private BigDecimal point;

  @ApiModelProperty(value = "是否热销 0 不热销，1 热销")
  @TableField("is_hot")
  private Integer isHot;

  @ApiModelProperty(value = "是否上新 0 不上新，1 上新")
  @TableField("is_new")
  private Integer isNew;

  @ApiModelProperty(value = "是否预售 （0不预售 1预售）")
  @TableField("is_adv")
  private Integer isAdv;

  @ApiModelProperty(value = "卡片类型 （0全部 1轻奢 2贵族 3至尊）")
  @TableField("card_type")
  private Integer cardType;

  @ApiModelProperty(value = "商品分类")
  @TableField("classify_id")
  private String classifyId;

  @ApiModelProperty(value = "商品分类名称")
  @TableField(exist = false)
  private String classifyName;

  @Override
  protected Serializable pkVal() {
    return this.id;
  }

}
