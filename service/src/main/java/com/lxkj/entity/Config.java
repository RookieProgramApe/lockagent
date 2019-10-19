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
 * 配置
 * </p>
 *
 * @author 一个烧包
 * @since 2019-07-13
 */
@Data
    @EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="Config对象", description="配置")
public class Config extends Model<Config> {

private static final long serialVersionUID = 1L;

        @ApiModelProperty(value = "主键")
                    @TableId(value = "id", type = IdType.UUID)
                            private String id;


    @TableField("`key`")
                    private String key;


        @ApiModelProperty(value = "内容")
    @TableField("`value`")
                    private String value;


        @ApiModelProperty(value = "描述")
    @TableField("`description`")
                    private String description;



@Override
protected Serializable pkVal() {
            return this.id;
        }

        }
