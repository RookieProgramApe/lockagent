package com.lxkj.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 用户ID-机构ID
 * </p>
 *
 * @author 一个烧包
 * @since 2019-05-17
 */
@Data
    @EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="SysUserOrgRef对象", description="用户ID-机构ID")
public class SysUserOrgRef extends Model<SysUserOrgRef> {

private static final long serialVersionUID = 1L;

                    @TableId(value = "id", type = IdType.UUID)
                            private String id;


    @TableField("userId")
                    private String userId;


    @TableField("orgId")
                    private String orgId;



@Override
protected Serializable pkVal() {
            return this.id;
        }

        }
