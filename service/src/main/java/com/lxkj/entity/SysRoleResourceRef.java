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
 * 角色ID-资源ID
 * </p>
 *
 * @author Hu Xiao
 * @since 2018-12-02
 */
@Data
    @EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="SysRoleResourceRef对象", description="角色ID-资源ID")
public class SysRoleResourceRef extends Model<SysRoleResourceRef> {

private static final long serialVersionUID = 1L;

                    @TableId(value = "id", type = IdType.UUID)
                            private String id;


    @TableField("roleId")
                    private String roleId;


    @TableField("resourceId")
                    private String resourceId;



@Override
protected Serializable pkVal() {
            return this.id;
        }

        }
