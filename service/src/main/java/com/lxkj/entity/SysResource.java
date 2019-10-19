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
import java.util.List;

/**
 * <p>
 * 系统菜单表
 * </p>
 *
 * @author Hu Xiao
 * @since 2018-12-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "SysResource对象", description = "系统菜单表")
public class SysResource extends Model<SysResource> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.UUID)
    private String id;


    @ApiModelProperty(value = "父级ID")
    @TableField("pid")
    private String pid;


    @ApiModelProperty(value = "许可码")
    @TableField("resPermission")
    private String resPermission;


    @ApiModelProperty(value = "菜单名称")
    @TableField("resName")
    private String resName;


    @ApiModelProperty(value = "菜单URL")
    @TableField("resUrl")
    private String resUrl;


    @ApiModelProperty(value = "资源图标")
    @TableField("resIcon")
    private String resIcon;


    @ApiModelProperty(value = "层级")
    @TableField("levels")
    private Integer levels;


    @ApiModelProperty(value = "1.菜单 2.功能")
    @TableField("resType")
    private Integer resType;


    @ApiModelProperty(value = "状态 0:禁用 1:启用")
    @TableField("enableStatus")
    private Integer enableStatus;


    @TableField("orders")
    private Integer orders;

    @TableField(exist = false)
    private List<SysResource> childList;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
