package com.lxkj.entity;

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
 * 卡片订单流程图片
 * </p>
 *
 * @author 一个烧包
 * @since 2019-07-31
 */
@Data
    @EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="CardOrderFlowFile对象", description="卡片订单流程图片")
public class CardOrderFlowFile extends Model<CardOrderFlowFile> {

private static final long serialVersionUID = 1L;

        @ApiModelProperty(value = "主键")
                    @TableId(value = "id", type = IdType.UUID)
                            private String id;


        @ApiModelProperty(value = "流程id")
    @TableField("card_order_flow_id")
                    private String cardOrderFlowId;


        @ApiModelProperty(value = "图片地址")
    @TableField("imageurl")
                    private String imageurl;


        @ApiModelProperty(value = "创建时间")
    @TableField("create_time")
                    private Date createTime;



@Override
protected Serializable pkVal() {
            return this.id;
        }

        }
