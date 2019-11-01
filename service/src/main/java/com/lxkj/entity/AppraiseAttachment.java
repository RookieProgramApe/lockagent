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
 * @author Zhanqian
 * @date 2019/10/29 14:13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="AppraiseAttachment对象", description="评论附件")
public class AppraiseAttachment extends Model<AppraiseAttachment> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.UUID)
    private String id;


    @ApiModelProperty(value = "评论id")
    @TableField("appraise_id")
    private String appraiseId;


    @ApiModelProperty(value = "1 图片，2 视频")
    @TableField("`type`")
    private Integer type;


    @ApiModelProperty(value = "链接地址")
    @TableField("url")
    private String url;



    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
