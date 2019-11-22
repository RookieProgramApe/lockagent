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

/**
 * 物流公司信息
 * @author Zhanqian
 * @date 2019/11/20 14:43
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="Delivery对象", description="物流公司")
public class Delivery extends Model {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.UUID)
    private Integer id;

    @ApiModelProperty(value = "code编码")
    @TableField("code")
    private String code;

    @ApiModelProperty(value = "快递公司名称")
    @TableField("name")
    private String name;

    @ApiModelProperty(value = "是否删除")
    @TableField("is_del")
    private String isDel;


}
