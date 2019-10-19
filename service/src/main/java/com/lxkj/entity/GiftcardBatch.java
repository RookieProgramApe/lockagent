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
 * 卡片批量导入
 * </p>
 *
 * @author 一个烧包
 * @since 2019-07-15
 */
@Data
    @EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="GiftcardBatch对象", description="卡片批量导入")
public class GiftcardBatch extends Model<GiftcardBatch> {

private static final long serialVersionUID = 1L;

        @ApiModelProperty(value = "主键")
                    @TableId(value = "id", type = IdType.UUID)
                            private String id;


        @ApiModelProperty(value = "序列号")
    @TableField("sequence")
                    private String sequence;


        @ApiModelProperty(value = "卡号")
    @TableField("serial")
                    private String serial;


        @ApiModelProperty(value = "提货码")
    @TableField("passcode")
                    private String passcode;


        @ApiModelProperty(value = "-1 失败，1 成功")
    @TableField("status")
                    private Integer status;


        @ApiModelProperty(value = "原因备注")
    @TableField("reason")
                    private String reason;


        @ApiModelProperty(value = "导入人")
    @TableField("create_by")
                    private String createBy;


        @ApiModelProperty(value = "创建时间")
    @TableField("create_time")
                    private Date createTime;



@Override
protected Serializable pkVal() {
            return this.id;
        }

        }
