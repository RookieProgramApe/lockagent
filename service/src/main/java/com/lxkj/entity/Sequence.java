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
 * 序列号
 * </p>
 *
 * @author 一个烧包
 * @since 2019-08-02
 */
@Data
    @EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="Sequence对象", description="序列号")
public class Sequence extends Model<Sequence> {

private static final long serialVersionUID = 1L;

        @ApiModelProperty(value = "序列的名字")
                    @TableId(value = "name", type = IdType.UUID)
                            private String name;


        @ApiModelProperty(value = "序列的当前值")
    @TableField("current_value")
                    private Integer currentValue;


        @ApiModelProperty(value = "序列的自增值")
    @TableField("increment")
                    private Integer increment;



@Override
protected Serializable pkVal() {
            return this.name;
        }

        }
