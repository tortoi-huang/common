package org.huang.common.utils;

import org.apache.commons.lang3.time.FastDateFormat;
import org.huang.common.utils.model.DataFrom;
import org.huang.common.utils.model.DataTo;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

public class BeanUtilsTest {

    @Test
    public void testCopy() {
        try {
            FastDateFormat yMd = FastDateFormat.getInstance("yyyy-MM-dd");
            FastDateFormat yMdhms = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");

            DataFrom dataFrom = new DataFrom();
            dataFrom.setInt2Str(1);
            dataFrom.setInt2Integer(2);
            dataFrom.setInt2Number(3);
            dataFrom.setDouble2BigDecimal(4.0);
            dataFrom.setInteger2int(5);
            dataFrom.setStr2int("6");
            dataFrom.setStr2Integer("7");
            dataFrom.setStr2Date1("2020-01-02");
            dataFrom.setStr2Date2("2020-01-03 23:09:59");
            dataFrom.setData1Only("8");
            dataFrom.setData0Only("9");

            DataTo dataTo = new DataTo();
            BeanUtils.copy(dataFrom, dataTo);

            Assert.assertEquals("1", dataTo.getInt2Str());
            Assert.assertEquals(2, dataTo.getInt2Integer().intValue());
            Assert.assertEquals(3, dataTo.getInt2Number().intValue());
            Assert.assertNull(dataTo.getDouble2BigDecimal());
            Assert.assertEquals(5, dataTo.getInteger2int());
            Assert.assertEquals(6, dataTo.getStr2int());
            Assert.assertEquals(yMd.parse("2020-01-02"), dataTo.getStr2Date1());
            Assert.assertEquals(yMdhms.parse("2020-01-03 23:09:59"), dataTo.getStr2Date2());
            Assert.assertEquals("9", dataTo.getData0Only());

            DataFrom dataFrom2 = new DataFrom();
            dataTo.setDouble2BigDecimal(BigDecimal.valueOf(4));
            BeanUtils.copy(dataTo, dataFrom2);

            Assert.assertEquals(1, dataFrom2.getInt2Str());
            Assert.assertEquals(2, dataFrom2.getInt2Integer());
            Assert.assertEquals(3, dataFrom2.getInt2Number());
            Assert.assertEquals(0, (int) dataFrom2.getDouble2BigDecimal());
            Assert.assertEquals(5, dataFrom2.getInteger2int().intValue());
            Assert.assertEquals("6", dataFrom2.getStr2int());
            Assert.assertEquals("2020-01-02 00:00:00", dataFrom2.getStr2Date1());
            Assert.assertEquals("2020-01-03 23:09:59", dataFrom2.getStr2Date2());
            Assert.assertEquals("9", dataFrom2.getData0Only());

            DataFrom data31 = new DataFrom();
            data31.setStr1("str1");
            data31.setData1Only("d1oly");
            DataTo data32 = new DataTo();
            data32.setStr2("str2");
            DataFrom data33 = new DataFrom();
            BeanUtils.copy(data31,data33);
            BeanUtils.copy(data32,data33);

            Assert.assertEquals(null, data33.getStr1());
            Assert.assertEquals("str2", data33.getStr2());
            Assert.assertEquals("d1oly", data33.getData1Only());

            Map<String,Object> map1 = new HashMap<>();
            BeanUtils.copy(dataFrom,map1);

            Assert.assertEquals(dataFrom.getInt2Str(), map1.get("int2Str"));
            Assert.assertEquals(dataFrom.getInt2Integer(), map1.get("int2Integer"));
            Assert.assertEquals(dataFrom.getInt2Number(), map1.get("int2Number"));
            Assert.assertEquals(dataFrom.getDouble2BigDecimal(), map1.get("double2BigDecimal"));
            Assert.assertEquals(dataFrom.getInteger2int(), map1.get("integer2int"));
            Assert.assertEquals(dataFrom.getStr2int(), map1.get("str2int"));
            Assert.assertEquals(dataFrom.getStr2Date1(), map1.get("str2Date1"));
            Assert.assertEquals(dataFrom.getStr2Date2(), map1.get("str2Date2"));
            Assert.assertEquals(dataFrom.getData0Only(), map1.get("data0Only"));
            Assert.assertEquals(dataFrom.getData1Only(), map1.get("data1Only"));

            map1.put(" error key",100);
            map1.put("noKey",101);
            DataFrom data13 = new DataFrom();
            BeanUtils.copy(map1,data13);

            Assert.assertEquals(dataFrom.getInt2Str(), data13.getInt2Str());
            Assert.assertEquals(dataFrom.getInt2Integer(), data13.getInt2Integer());
            Assert.assertEquals(dataFrom.getInt2Number(), data13.getInt2Number());
            Assert.assertEquals((int)(dataFrom.getDouble2BigDecimal()*100), (int)(data13.getDouble2BigDecimal()* 100));
            Assert.assertEquals(dataFrom.getInteger2int(), data13.getInteger2int());
            Assert.assertEquals(dataFrom.getStr2int(), data13.getStr2int());
            Assert.assertEquals(dataFrom.getStr2Date1(), data13.getStr2Date1());
            Assert.assertEquals(dataFrom.getStr2Date2(), data13.getStr2Date2());
            Assert.assertEquals(dataFrom.getData0Only(), data13.getData0Only());
            Assert.assertEquals(dataFrom.getData1Only(), data13.getData1Only());

        } catch (ParseException e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testMapPerformance() {
        DataFrom dataFrom = new DataFrom();
        dataFrom.setInt2Str(1);
        dataFrom.setInt2Integer(2);
        dataFrom.setInt2Number(3);
        dataFrom.setDouble2BigDecimal(4.0);
        dataFrom.setInteger2int(5);
        dataFrom.setStr2int("6");
        dataFrom.setStr2Integer("7");
        dataFrom.setStr2Date1("2020-01-02");
        dataFrom.setStr2Date2("2020-01-03 23:09:59");
        dataFrom.setData1Only("8");
        dataFrom.setData0Only("9");

        long l = System.currentTimeMillis();
        for(int i = 0;i < 1000000;i++)
            BeanUtils.map(dataFrom,DataTo.class);
        System.out.println(System.currentTimeMillis() - l);

        long l2 = System.currentTimeMillis();
        for(int i = 0;i < 1000000;i++)
            BeanUtils.copy(dataFrom,new DataTo());
        System.out.println(System.currentTimeMillis() - l2);

        long l3 = System.currentTimeMillis();
        for(int i = 0;i < 1000000;i++)
            BeanUtils.map(dataFrom,DataTo.class);
        System.out.println(System.currentTimeMillis() - l3);

        long l4 = System.currentTimeMillis();
        for(int i = 0;i < 1000000;i++)
            BeanUtils.copy(dataFrom,new DataTo());
        System.out.println(System.currentTimeMillis() - l4);
    }
}