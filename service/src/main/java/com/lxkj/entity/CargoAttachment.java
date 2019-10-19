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
 * 商品附件
 * </p>
 *
 * @author 一个烧包
 * @since 2019-07-13
 */
@Data
    @EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="CargoAttachment对象", description="商品附件")
public class CargoAttachment extends Model<CargoAttachment> {

private static final long serialVersionUID = 1L;

        @ApiModelProperty(value = "主键")
                    @TableId(value = "id", type = IdType.UUID)
                            private String id;


        @ApiModelProperty(value = "商品ID")
    @TableField("cargo_id")
                    private String cargoId;


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
