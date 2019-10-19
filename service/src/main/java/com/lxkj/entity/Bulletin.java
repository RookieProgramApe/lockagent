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

/**
 * <p>
 * 公告
 * </p>
 *
 * @author 一个烧包
 * @since 2019-07-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="Bulletin对象", description="公告")
public class Bulletin extends Model<com.lxkj.entity.Bulletin> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.UUID)
    private String id;


    @ApiModelProperty(value = "公告内容")
    @TableField("content")
    private String content;


    @ApiModelProperty(value = "是否启用  0禁用 1启用")
    @TableField("enabled")
    private Integer enabled;


    @ApiModelProperty(value = "排序")
    @TableField("sort")
    private Integer sort;



    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
