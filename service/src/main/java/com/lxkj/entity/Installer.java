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
 * 申请安装
 * </p>
 *
 * @author 一个烧包
 * @since 2019-07-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "Installer对象", description = "申请安装")
public class Installer extends Model<Installer> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键",hidden = true)
    @TableId(value = "id", type = IdType.UUID)
    private String id;


    @ApiModelProperty(value = "名称(标题)")
    @TableField("name")
    private String name;


    @ApiModelProperty(value = "介绍")
    @TableField("duration")
    private String duration;

    @ApiModelProperty(value = "安装费用")
    @TableField("price")
    private BigDecimal price;


    @ApiModelProperty(value = "电话号码")
    @TableField("contact")
    private String contact;

    @ApiModelProperty(value = "是否启用  0禁用 1启用",hidden = true)
    @TableField("enabled")
    private Integer enabled;

    @ApiModelProperty(value = "创建时间",hidden = true)
    @TableField("create_time")
    private Date createTime;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
