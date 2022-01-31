/*
 * Copyright (C) 2022 DIGIBÍS S.L.U
 *
 * This file is part of MARC4J
 *
 * MARC4J is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * MARC4J is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with MARC4J; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.marc4j.marc

import spock.lang.Specification

/**
 * Unit test for ControlField
 */
class ControlFieldSpec extends Specification {
    
    def "Test constructor"() {
        given:
        def data = "test".toCharArray();
        def cf = new ControlField("003", data);
        
        expect:
        "003".equals(cf.getTag())
        data.equals(cf.getData())
    }
    
    def "Test equals"() {
        given:
        def cf1 = new ControlField("003", "test".toCharArray());
        def cf2 = new ControlField("003", "test".toCharArray());
        def cf3 = new ControlField("003", "uofiytyout".toCharArray());
        
        expect:
        cf1.equals(cf1);
        cf1.hashCode() == cf1.hashCode()
        cf1.equals(cf2)
        cf2.equals(cf1)
        !(cf1.equals(cf3))
        !(cf3.equals(cf1))
        !(cf2.equals(cf3))
    }
    
    def "Test setter"() {
        given:
        def cf1 = new ControlField();
        
        when:
        cf1.setTag("010");
        
        then:
        thrown(IllegalTagException)
        
        when:
        cf1.setData("test\u001E");
        
        then:
        thrown(IllegalDataElementException)
    }
    
    def "Marshaling"() {
        given:
        def cf = new ControlField("003", "test".toCharArray());
        
        expect:
        "test\u001E".equals(cf.marshal())
    }
}