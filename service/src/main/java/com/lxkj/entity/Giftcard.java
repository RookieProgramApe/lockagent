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
import java.util.Date;

/**
 * <p>
 * 卡片
 * </p>
 *
 * @author 一个烧包
 * @since 2019-07-13
 */
@Data
    @EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="Giftcard对象", description="卡片")
public class Giftcard extends Model<Giftcard> {

private static final long serialVersionUID = 1L;

        @ApiModelProperty(value = "主键")
                    @TableId(value = "id", type = IdType.UUID)
                            private String id;


        @ApiModelProperty(value = "卡号")
    @TableField("serial")
                    private String serial;


        @ApiModelProperty(value = "提货码")
    @TableField("passcode")
                    private String passcode;


        @ApiModelProperty(value = "1 未分配，2 已分配给代理商，3 已使用")
    @TableField("`status`")
                    private Integer status;


        @ApiModelProperty(value = "创建时间")
    @TableField("create_time")
                    private Date createTime;


        @ApiModelProperty(value = "更新时间")
    @TableField("update_time")
                    private Date updateTime;

    @ApiModelProperty(value = "0:未使用 1:已使用")
    @TableField(exist=false)
    private Integer state;

    @ApiModelProperty(value = "0未兑换 1已兑换")
    @TableField("`use`")
    private Integer use;


    @Override
protected Serializable pkVal() {
            return this.id;
        }

        }
