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
 * 商品规格类型
 * </p>
 *
 * @author 李红
 * @since 2019-10-14
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "CargoSkuType对象", description = "商品规格类型")
public class CargoSkuType  extends Model<CargoSkuType> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "商品规格ID")
    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "商品规格类型")
    @TableField("type")
    private String type;



    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}

