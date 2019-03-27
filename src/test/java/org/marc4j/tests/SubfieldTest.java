package org.marc4j.tests;

import org.junit.Assert;
import org.junit.Test;
import org.marc4j.marc.Subfield;

public class SubfieldTest
{
    @Test()
    public void testConstructor()
    {
        char[] data = "test".toCharArray();

        Subfield sf1 = new Subfield('a', data);
        Assert.assertEquals(sf1.getCode(), 'a');
        Assert.assertEquals(sf1.getData(), data);
    }

    @Test()
    public void testEquals()
    {
        Subfield sf1 = new Subfield('a', "test".toCharArray());
        Subfield sf2 = new Subfield('a', "test".toCharArray());
        Subfield sf3 = new Subfield('a', "piyoitfou".toCharArray());
        Assert.assertEquals(sf1, sf1);
        Assert.assertEquals(sf1.hashCode(), sf1.hashCode());
        Assert.assertTrue(sf1.equals(sf2));
        Assert.assertFalse(sf1.equals(sf3));

    }

    @Test()
    public void testMarshal()
    {
        Subfield sf1 = new Subfield('a', "test".toCharArray());
        Assert.assertEquals(sf1.marshal(), "atest");
    }

}
