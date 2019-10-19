package com.lxkj.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 用户ID-角色ID
 * </p>
 *
 * @author Hu Xiao
 * @since 2018-12-02
 */
@Data
    @EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="SysUserRoleRef对象", description="用户ID-角色ID")
public class SysUserRoleRef extends Model<SysUserRoleRef> {

private static final long serialVersionUID = 1L;

                    @TableId(value = "id", type = IdType.UUID)
                            private String id;


    @TableField("userId")
                    private String userId;


    @TableField("roleId")
                    private String roleId;



@Override
protected Serializable pkVal() {
            return this.id;
        }

        }
