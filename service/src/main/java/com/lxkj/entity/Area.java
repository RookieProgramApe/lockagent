package com.lxkj.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 国标码
 * </p>
 *
 * @author 一个烧包
 * @since 2019-10-06
 */
@Data
    @EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="Area对象", description="国标码")
public class Area extends Model<Area> {

private static final long serialVersionUID = 1L;

        @ApiModelProperty(value = "运输区域")
    @TableField("area")
                    private String area;


        @ApiModelProperty(value = "运输区域描述")
    @TableField("name")
                    private String name;


        @ApiModelProperty(value = "国标码")
    @TableField("code")
                    private String code;


        @ApiModelProperty(value = "国标码描述")
    @TableField("desc")
                    private String desc;



@Override
protected Serializable pkVal() {
            return null;
        }

        }
