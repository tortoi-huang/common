package org.huang.common.utils;

import junit.framework.TestCase;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.time.FastDateFormat;
import org.junit.Test;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class BeanUtilsTest extends TestCase {

    @Test
    public void testCopy() {
        try {
            FastDateFormat yMd = FastDateFormat.getInstance("yyyy-MM-dd");
            FastDateFormat yMdhms = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");

            Data1 data1 = new Data1();
            data1.setInt2Str(1);
            data1.setInt2Integer(2);
            data1.setInt2Number(3);
            data1.setDouble2BigDecimal(4.0);
            data1.setInteger2int(5);
            data1.setStr2int("6");
            data1.setStr2Integer("7");
            data1.setStr2Date1("2020-01-02");
            data1.setStr2Date2("2020-01-03 23:09:59");
            data1.setData1Only("8");
            data1.setData0Only("9");

            Data2 data2 = new Data2();
            BeanUtils.copy(data1, data2);

            assertEquals("1",data2.getInt2Str());
            assertEquals(2,data2.getInt2Integer().intValue());
            assertEquals(3,data2.getInt2Number().intValue());
            assertNull(data2.getDouble2BigDecimal());
            assertEquals(5,data2.getInteger2int());
            assertEquals(6,data2.getStr2int());
            assertEquals(yMd.parse("2020-01-02"),data2.getStr2Date1());
            assertEquals(yMdhms.parse("2020-01-03 23:09:59"),data2.getStr2Date2());
            assertEquals("9",data2.getData0Only());

            Data1 data12 = new Data1();
            data2.setDouble2BigDecimal(BigDecimal.valueOf(4));
            BeanUtils.copy(data2, data12);

            assertEquals(1,data12.getInt2Str());
            assertEquals(2,data12.getInt2Integer());
            assertEquals(3,data12.getInt2Number());
            assertEquals(0,(int)data12.getDouble2BigDecimal());
            assertEquals(5,data12.getInteger2int().intValue());
            assertEquals("6",data12.getStr2int());
            assertEquals("2020-01-02 00:00:00",data12.getStr2Date1());
            assertEquals("2020-01-03 23:09:59",data12.getStr2Date2());
            assertEquals("9",data12.getData0Only());

            for (int i = 0; i < 1000; i++) {
                BeanUtils.copy(data2, new Data1());
            }


            Data1 data31 = new Data1();
            data31.setStr1("str1");
            data31.setData1Only("d1oly");
            Data2 data32 = new Data2();
            data32.setStr2("str2");
            Data1 data33 = new Data1();
            BeanUtils.copy(data31,data33);
            BeanUtils.copy(data32,data33);

            assertEquals(null,data33.getStr1());
            assertEquals("str2",data33.getStr2());
            assertEquals("d1oly",data33.getData1Only());

            Map<String,Object> map1 = new HashMap<>();
            BeanUtils.copy(data1,map1);

            assertEquals(data1.getInt2Str(),map1.get("int2Str"));
            assertEquals(data1.getInt2Integer(),map1.get("int2Integer"));
            assertEquals(data1.getInt2Number(),map1.get("int2Number"));
            assertEquals(data1.getDouble2BigDecimal(),map1.get("double2BigDecimal"));
            assertEquals(data1.getInteger2int(),map1.get("integer2int"));
            assertEquals(data1.getStr2int(),map1.get("str2int"));
            assertEquals(data1.getStr2Date1(),map1.get("str2Date1"));
            assertEquals(data1.getStr2Date2(),map1.get("str2Date2"));
            assertEquals(data1.getData0Only(),map1.get("data0Only"));
            assertEquals(data1.getData1Only(),map1.get("data1Only"));

            Data1 data13 = new Data1();
            BeanUtils.copy(map1,data13);

            assertEquals(data1.getInt2Str(),data13.getInt2Str());
            assertEquals(data1.getInt2Integer(),data13.getInt2Integer());
            assertEquals(data1.getInt2Number(),data13.getInt2Number());
            assertEquals(data1.getDouble2BigDecimal(),data13.getDouble2BigDecimal());
            assertEquals(data1.getInteger2int(),data13.getInteger2int());
            assertEquals(data1.getStr2int(),data13.getStr2int());
            assertEquals(data1.getStr2Date1(),data13.getStr2Date1());
            assertEquals(data1.getStr2Date2(),data13.getStr2Date2());
            assertEquals(data1.getData0Only(),data13.getData0Only());
            assertEquals(data1.getData1Only(),data13.getData1Only());

        } catch (ParseException e) {
            e.printStackTrace();
            fail();
        }
    }


    @Getter
    @Setter
    private static class Data0 {
        private String str1;
        private String str2;
        private String str3;
        private String data0Only;
    }

    @Getter
    @Setter
    private static class Data1 extends Data0 {
        private int int2Str;
        private int int2Integer;
        private int int2Number;
        private double double2BigDecimal;
        private Integer integer2int;
        private String str2int;
        private String str2Integer;
        private String str2Date1;
        private String str2Date2;

        private String data1Only;
    }

    @Getter
    @Setter
    private static class Data2 extends Data0 {
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
}