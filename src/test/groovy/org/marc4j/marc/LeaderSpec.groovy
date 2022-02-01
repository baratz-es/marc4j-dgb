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
import spock.lang.Unroll

/**
 * Unit test for Leader
 *
 * Ported from oficial Marc4J
 */
class LeaderSpec extends Specification {

    def "Leader empty constructor"() {
        given:
        Leader leader = new Leader()

        expect:
        leader != null
    }

    @Unroll
    def "Leader unmarshaling #raw"() {
        given:
        Leader leader = new Leader()

        when:
        leader.unmarshal(raw)

        then:
        with(leader) {
            getRecordLength() == length
            getRecordStatus() == status as char
            getTypeOfRecord() == type as char
            getImplDefined1() == implDefined1.toCharArray()
            getCharCodingScheme() == coding as char
            getIndicatorCount() == indicator
            getSubfieldCodeLength() == subfield
            getBaseAddressOfData() == base
            getImplDefined2() == implDefined2.toCharArray()
            getEntryMap() == entryMap.toCharArray()
        }

        where:
        raw                         | length | status | type | implDefined1 | coding | indicator | subfield | base | implDefined2 | entryMap
        "00714cam a2200205 a 4500"  | 714    | 'c'    | 'a'  | "m "         | 'a'    | 2         | 2        | 205  | " a "        | "4500"
        "00000czm a2200000n  4500"  |   0    | 'c'    | 'z'  | "m "         | 'a'    | 2         | 2        |   0  | "n  "        | "4500"
        "00000caa a22000004  4500"  |   0    | 'c'    | 'a'  | "a "         | 'a'    | 2         | 2        |   0  | "4  "        | "4500"
        "00714cam a1100205 a 4500"  | 714    | 'c'    | 'a'  | "m "         | 'a'    | 1         | 1        | 205  | " a "        | "4500"
        "abcdeuam aop00abc a 1500"  |   0    | 'u'    | 'a'  | "m "         | 'a'    | 2         | 2        |   0  | " a "        | "1500"
    }

    def "Leader marshaling"() {
        given:
        Leader leader = new Leader("00714cam a2200205 a 4500")

        expect:
        "00714cam a2200205 a 4500" == leader.marshal()
    }
}
