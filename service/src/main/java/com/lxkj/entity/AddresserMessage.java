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
 * 发件人信息
 * </p>
 *
 * @author 一个烧包
 * @since 2019-07-26
 */
@Data
    @EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="AddresserMessage对象", description="发件人信息")
public class AddresserMessage extends Model<AddresserMessage> {

private static final long serialVersionUID = 1L;

        @ApiModelProperty(value = "主键")
                    @TableId(value = "id", type = IdType.UUID)
                            private String id;


        @ApiModelProperty(value = "快递公司")
    @TableField("delivery_provider")
                    private String deliveryProvider;


        @ApiModelProperty(value = "发件人")
    @TableField("addresser")
                    private String addresser;


        @ApiModelProperty(value = "发件人号码")
    @TableField("mobile")
                    private String mobile;


        @ApiModelProperty(value = "省")
    @TableField("province")
                    private String province;


        @ApiModelProperty(value = "地区(省市区)")
    @TableField("city")
                    private String city;


        @ApiModelProperty(value = "区县")
    @TableField("county")
                    private String county;


        @ApiModelProperty(value = "详细地址")
    @TableField("address")
                    private String address;


        @ApiModelProperty(value = "备注")
    @TableField("remark")
                    private String remark;


        @ApiModelProperty(value = "创建时间")
    @TableField("create_time")
                    private Date createTime;



@Override
protected Serializable pkVal() {
            return this.id;
        }

        }
