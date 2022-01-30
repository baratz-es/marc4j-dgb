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

import static org.junit.Assert.fail

import java.text.DecimalFormat

import org.junit.Assert
import org.junit.Test

import spock.lang.Specification
import spock.lang.Unroll

/**
 * Unit test for Tag
 */
class TagSpec extends Specification {
    private DecimalFormat df = new DecimalFormat("000");
    
    def "Test Tag.isValid(#i)"() {
        given:
        def tag = df.format(i).toString();
        
        expect:
        Tag.isValid(tag)
        
        where:
        i << (1 .. 999)
    }
    
    def "Test invalid tag with Tag.isValid()"() {
        when:
        Tag.isValid("1234")
        
        then:
        thrown(IllegalTagException)
    }
    
    def "Test Tag.isControlField(#i)"() {
        given:
        def tag = df.format(i).toString();
        
        expect:
        Tag.isControlField(tag) == i < 10
        
        where:
        i << (1 .. 999)
    }
    
    def "Test Tag.isControlNumberField(#i)"() {
        given:
        def tag = df.format(i).toString();
        
        expect:
        Tag.isControlNumberField(tag) == (i == 1) 
        
        where:
        i << (1 .. 999)
    }
    
    def "Test Tag.isDataField(#i)"() {
        given:
        def tag = df.format(i).toString();
        
        expect:
        Tag.isDataField(tag) == (i >= 10)
        
        where:
        i << (1 .. 999)
    }
}
    