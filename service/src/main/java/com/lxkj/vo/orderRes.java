package com.lxkj.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class orderRes {

    private String oid_partner;
    private String dt_order;
    private String no_order;
    private String oid_paybill;
    private String money_order;
    private String result_pay;
    private String settle_date;
    private String info_order;
    private String pay_type;
    private String bank_code;
    private String sign_type;
    private String sign;


}
