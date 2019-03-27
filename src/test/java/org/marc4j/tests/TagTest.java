package org.marc4j.tests;

import static org.junit.Assert.fail;

import java.text.DecimalFormat;

import org.junit.Assert;
import org.junit.Test;
import org.marc4j.marc.IllegalTagException;
import org.marc4j.marc.Tag;

public class TagTest
{
    private DecimalFormat df = new DecimalFormat("000");

    @Test()
    public void testIsValid()
    {
        for (int i = 1; i <= 999; i++) {
            String tag = df.format(i).toString();
            Assert.assertTrue(Tag.isValid(tag));
        }
        try {
            Tag.isValid("1234");
            fail("Should raise an IllegalTagException");
        } catch (IllegalTagException success) {
        }
    }

    @Test()
    public void testIsControlField()
    {
        for (int i = 1; i < 10; i++) {
            String tag = df.format(i).toString();
            Assert.assertTrue(Tag.isControlField(tag));
        }

        for (int i = 10; i <= 999; i++) {
            String tag = df.format(i).toString();
            Assert.assertTrue(!Tag.isControlField(tag));
        }
    }

    @Test()
    public void testIsControlNumberField()
    {
        String tag = df.format(1).toString();
        Assert.assertTrue(Tag.isControlNumberField(tag));

        for (int i = 2; i <= 999; i++) {
            tag = df.format(i).toString();
            Assert.assertTrue(!Tag.isControlNumberField(tag));
        }
    }

    @Test()
    public void isDataField()
    {
        for (int i = 10; i <= 999; i++) {
            String tag = df.format(i).toString();
            Assert.assertTrue(Tag.isDataField(tag));
        }

        for (int i = 1; i < 10; i++) {
            String tag = df.format(i).toString();
            Assert.assertTrue(!Tag.isDataField(tag));
        }
    }

}
