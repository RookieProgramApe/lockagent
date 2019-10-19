package com.lxkj.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableField;

import java.io.Serializable;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 卡片订单审批流程
 * </p>
 *
 * @author 一个烧包
 * @since 2019-07-31
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "CardOrderFlow对象", description = "卡片订单审批流程")
public class CardOrderFlow extends Model<CardOrderFlow> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.UUID)
    private String id;


    @ApiModelProperty(value = "卡片订单id")
    @TableField("card_order_id")
    private String cardOrderId;


    @ApiModelProperty(value = "审核状态说明")
    @TableField("state")
    private String state;


    @ApiModelProperty(value = "备注")
    @TableField("remark")
    private String remark;


    @ApiModelProperty(value = "后台用户ID")
    @TableField("create_by")
    private String createBy;


    @ApiModelProperty(value = "创建时间")
    @TableField("create_time")
    private Date createTime;

    @ApiModelProperty(value = "1卡片订单 2合伙人注册")
    @TableField("lb")
    private Integer lb;

    @TableField(exist = false)
    private List<CardOrderFlowFile> fileList;
    @TableField(exist = false)
    private String realName;
    @TableField(exist = false)
    private String mobile;
    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
