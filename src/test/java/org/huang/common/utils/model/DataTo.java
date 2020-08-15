package org.huang.common.utils.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
public class DataTo extends DataBase {
    private String int2Str;
    private Integer int2Integer;
    private Number int2Number;
    private BigDecimal double2BigDecimal;
    private int integer2int;
    private int str2int;
    private Integer str2Integer;
    private Date str2Date1;
    private Date str2Date2;

    private String data2Only;
}
