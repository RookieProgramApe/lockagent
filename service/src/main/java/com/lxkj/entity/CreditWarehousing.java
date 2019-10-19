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

/**
 * <p>
 * 货物入库记录
 * </p>
 *
 * @author 一个烧包
 * @since 2019-07-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("creditwarehousing")
@ApiModel(value = "Warehousing对象", description = "货物入库记录")
public class CreditWarehousing extends Model<CreditWarehousing> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.UUID)
    private String id;


    @ApiModelProperty(value = "商品ID")
    @TableField("cargo_id")
    private String cargoId;


    @ApiModelProperty(value = "SKU ID")
    @TableField("sku_id")
    private String skuId;


    @ApiModelProperty(value = "入库量")
    @TableField("inventory")
    private Integer inventory;

    @ApiModelProperty(value = "备注")
    @TableField("remk")
    private String remk;

    @ApiModelProperty(value = "入库时间")
    @TableField("create_time")
    private Date createTime;


    @ApiModelProperty(value = "入库人")
    @TableField("create_by")
    private String createBy;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
