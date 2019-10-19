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
 * 系统角色表
 * </p>
 *
 * @author Hu Xiao
 * @since 2018-12-02
 */
@Data
    @EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="SysRole对象", description="系统角色表")
public class SysRole extends Model<SysRole> {

private static final long serialVersionUID = 1L;

        @ApiModelProperty(value = "主键")
                    @TableId(value = "id", type = IdType.UUID)
                            private String id;


        @ApiModelProperty(value = "角色编码")
    @TableField("roleCode")
                    private String roleCode;


        @ApiModelProperty(value = "角色名称")
    @TableField("roleName")
                    private String roleName;


        @ApiModelProperty(value = "角色描术")
    @TableField("roleDesc")
                    private String roleDesc;


        @ApiModelProperty(value = "加入时间")
    @TableField("createTime")
                    private Date createTime;



@Override
protected Serializable pkVal() {
            return this.id;
        }

        }
