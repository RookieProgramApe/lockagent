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
 * 素材内容
 * </p>
 *
 * @author 一个烧包
 * @since 2019-10-06
 */
@Data
    @EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="MaterialInfo对象", description="素材内容")
public class MaterialInfo extends Model<MaterialInfo> {

private static final long serialVersionUID = 1L;

        @ApiModelProperty(value = "主键")
                    @TableId(value = "id", type = IdType.UUID)
                            private String id;


        @ApiModelProperty(value = "类别ID")
    @TableField("tid")
                    private String tid;


        @ApiModelProperty(value = "标题")
    @TableField("title")
                    private String title;


        @ApiModelProperty(value = "视频链接")
    @TableField("vod")
                    private String vod;


        @ApiModelProperty(value = "详情资料（富文本）")
    @TableField("description")
                    private String description;


        @ApiModelProperty(value = "创建时间")
    @TableField("create_time")
                    private Date createTime;



@Override
protected Serializable pkVal() {
            return this.id;
        }

        }
