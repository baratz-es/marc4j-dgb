package org.marc4j.marc;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

/**
 * Test ported from oficial marc4j
 */
class LeaderTest
{

    @Test
    void testConstructor()
    {
        Leader leader = new Leader();
        assertNotNull(leader, "leader is null");
    }

    @Test
    void testUnmarshal()
    {
        Leader leader = new Leader();
        leader.unmarshal("00714cam a2200205 a 4500");
        assertEquals("00714cam a2200205 a 4500", leader.marshal());
    }

    @Test
    void testUnmarshalSubfieldCodeLength()
    {
        Leader leader = new Leader();
        leader.unmarshal("00714cam a2100205 a 4500");
        assertEquals(1, leader.getSubfieldCodeLength());
    }

    @Test
    void testMarshal()
    {
        Leader leader = new Leader("00714cam a2200205 a 4500");
        assertEquals("00714cam a2200205 a 4500", leader.marshal());
    }

}
