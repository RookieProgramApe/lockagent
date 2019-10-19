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
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 商品种类
 * </p>
 *
 * @author Zhanqian
 * @since 2019-10-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "CargoCategory对象", description = "商品种类")
public class CargoCategory extends Model<CargoCategory> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "商品种类ID")
    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "商品ID",hidden = true)
    @TableId(value = "cargo_id")
    private String cargoId;

    @ApiModelProperty(value = "种类名称")
    @TableId(value = "`name`")
    private String name;

    @ApiModelProperty(value = "价格")
    @TableId(value = "`price`")
    private BigDecimal price;

    @ApiModelProperty(value = "创建时间")
    @TableField("create_time")
    private Date createTime;

    @ApiModelProperty(value = "是否删除 0否 1是" ,hidden = true)
    @TableId(value = "is_del")
    private short isDel;

    @ApiModelProperty(value = "排序" ,hidden = true)
    @TableId(value = "sort")
    private Integer sort;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}
