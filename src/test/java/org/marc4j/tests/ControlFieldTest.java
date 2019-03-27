package org.marc4j.tests;

import static org.junit.Assert.fail;

import org.junit.Assert;
import org.junit.Test;
import org.marc4j.marc.ControlField;
import org.marc4j.marc.IllegalDataElementException;
import org.marc4j.marc.IllegalTagException;

public class ControlFieldTest
{
    @Test()
    public void testConstructor()
    {
        char[] data = "test".toCharArray();
        ControlField cf2 = new ControlField("003", data);
        Assert.assertEquals(cf2.getTag(), "003");
        Assert.assertEquals(cf2.getData(), data);
    }

    @Test()
    public void testEquals()
    {
        ControlField cf1 = new ControlField("003", "test".toCharArray());
        ControlField cf2 = new ControlField("003", "test".toCharArray());
        ControlField cf3 = new ControlField("003", "uofiytyout".toCharArray());
        Assert.assertEquals(cf1, cf1);
        Assert.assertEquals(cf1.hashCode(), cf1.hashCode());
        Assert.assertFalse(cf1.equals(cf2)); // super.equals
        Assert.assertFalse(cf1.equals(cf3));

    }

    @Test()
    public void testSetter()
    {
        ControlField cf1 = new ControlField();
        try {
            cf1.setTag("010");
            fail("Should raise an IllegalTagException");
        } catch (IllegalTagException success) {
        }

        try {
            cf1.setData("test");
            fail("Should raise an IllegalDataElementException");
        } catch (IllegalDataElementException success) {
        }

    }

    @Test()
    public void testMarshal()
    {
        ControlField cf1 = new ControlField("003", "test".toCharArray());
        Assert.assertEquals(cf1.marshal(), "test");
    }
}
