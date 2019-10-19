package com.lxkj.entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 砍价过程配置
 * </p>
 *
 * @author 一个烧包
 * @since 2019-08-02
 */
@Data
    @EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="BargainStep对象", description="砍价过程配置")
public class BargainStep extends Model<BargainStep> {

private static final long serialVersionUID = 1L;

        @ApiModelProperty(value = "主键")
                    @TableId(value = "id", type = IdType.UUID)
                            private String id;


        @ApiModelProperty(value = "砍价活动ID")
    @TableField("bargain_id")
                    private String bargainId;


        @ApiModelProperty(value = "砍刀价格")
    @TableField("price")
                    private BigDecimal price;


        @ApiModelProperty(value = "创建时间")
    @TableField("create_time")
                    private Date createTime;


        @ApiModelProperty(value = "序号")
    @TableField("sort")
                    private Integer sort;



@Override
protected Serializable pkVal() {
            return this.id;
        }

        }
