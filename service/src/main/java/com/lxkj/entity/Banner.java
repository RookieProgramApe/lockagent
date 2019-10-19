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
 * 轮播图
 * </p>
 *
 * @author 一个烧包
 * @since 2019-07-16
 */
@Data
    @EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="Banner对象", description="轮播图")
public class Banner extends Model<Banner> {

private static final long serialVersionUID = 1L;

        @ApiModelProperty(value = "主键")
                    @TableId(value = "id", type = IdType.UUID)
                            private String id;


        @ApiModelProperty(value = "图片")
    @TableField("image")
                    private String image;


        @ApiModelProperty(value = "排序")
    @TableField("sort")
                    private Integer sort;


        @ApiModelProperty(value = "创建人")
    @TableField("created_by")
                    private String createdBy;


        @ApiModelProperty(value = "创建时间")
    @TableField("created_date")
                    private Date createdDate;


        @ApiModelProperty(value = "是否启用：0 禁用，1 启用")
    @TableField("enabled")
                    private Integer enabled;



@Override
protected Serializable pkVal() {
            return this.id;
        }

        }
