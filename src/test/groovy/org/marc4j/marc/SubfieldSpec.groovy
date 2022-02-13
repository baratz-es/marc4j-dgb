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
 * Unit test for Subfield
 */
class SubfieldSpec extends Specification {

    def "Test constructor"() {
        given:
        def data = "test".toCharArray()
        def sf = new Subfield('a' as char, data)

        expect:
        ('a' as char).equals(sf.getCode())
        data.equals(sf.getData())
    }

    def "Test equals"() {
        given:
        def sf1 = new Subfield('a' as char, "test".toCharArray())
        def sf2 = new Subfield('a' as char, "test".toCharArray())
        def sf3 = new Subfield('a' as char, "piyoitfou".toCharArray())

        expect:
        sf1.equals(sf1)
        sf1.hashCode().equals(sf1.hashCode())
        sf1.equals(sf2)
        sf2.equals(sf1)
        !(sf1.equals(sf3))
        !(sf3.equals(sf1))
        !(sf2.equals(sf3))
    }

    def "Copy constructor"() {
        given: "A valid Subfield"
        def subfield = new Subfield('a' as char, "Calderón de la Barca", "FILA20180015793")

        when: "We create a copy"
        def copy = new Subfield(subfield)

        then: "Must have the same exact values"
        copy.getCode().equals('a' as char)
        (copy.getData() as String).equals("Calderón de la Barca")
        copy.getLinkCode().equals("FILA20180015793")
        subfield.equals(copy)
        copy.equals(subfield)

        when: "We edit the original Subfield"
        subfield.setCode('b' as char)
        subfield.setData("Calderón de la Barca, Pedro".toCharArray())
        subfield.setLinkCode("AUT20180015793")

        then: "The copy isn't affected"
        copy.getCode().equals('a' as char)
        (copy.getData() as String).equals("Calderón de la Barca")
        copy.getLinkCode().equals("FILA20180015793")
        !subfield.equals(copy)
    }

    def "Test setter"() {
        given: "An empty Subfield"
        def sf = new Subfield()

        when: "we set data to a invalid valued"
        sf.setData("test\u001E")

        then: "we get an exception"
        thrown(IllegalDataElementException)
    }

    def "Marshaling"() {
        given:
        def sf = new Subfield('a' as char, "test".toCharArray())

        expect:
        "\u001Fatest".equals(sf.marshal())
    }

    def "Finding if a subfield value matches a regex pattern"() {
        given:
        def subfield = new Subfield('a' as char, value.toCharArray())

        expect:
        subfield.find(regex) == found

        where:
        value                                   | regex                 || found
        "Calderón de la Barca"                  | ".*Calderón.*"        || true
        "קלדרון דה לה ברקה, פדרו"               | "\\p{IsAlphabetic}"   || true
        "Diccionario Biográfico Electrónico"    | ".*Becquer.*"         || false
    }
}