/*
 * Copyright (C) 2019 DIGIBÍS S.L.U
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
 * Unit test for RecordField
 */
class RecordSpec extends Specification {
    final UNDEF_CHAR = '\u0000'
    final EMPTY_LEADER_MARSHAL = "00000" + UNDEF_CHAR + UNDEF_CHAR + UNDEF_CHAR + UNDEF_CHAR + UNDEF_CHAR +
    "0000000" + UNDEF_CHAR + UNDEF_CHAR + UNDEF_CHAR + UNDEF_CHAR + UNDEF_CHAR + UNDEF_CHAR + UNDEF_CHAR

    def "Record empty constructor" () {
        when:
        def record = new Record()

        then:
        record.getLeader() == null
        record.getControlNumber() == null
        record.hasControlNumberField() == false
        record.getControlFieldList().isEmpty()
        record.getDataFieldList().isEmpty()

        when: "if we try to marshal"
        record.marshal()

        then: "throws a exception"
        thrown (MarcException)

        when: "if we try to marshal with encoding"
        record.marshal("UTF-8")

        then: "throws a exception"
        thrown (MarcException)
    }

    def "Record constructor with empty Leader" () {
        when:
        def record = Record.newRecordWithEmptyLeader()

        then:
        def leader = record.getLeader()
        leader.marshal() == EMPTY_LEADER_MARSHAL
        record.getControlNumber() == null
        record.hasControlNumberField() == false
        record.getControlFieldList().isEmpty()
        record.getDataFieldList().isEmpty()

        when: "if we try to marshal"
        record.marshal()

        then: "throws a exception"
        thrown (MarcException)

        when: "if we try to marshal with encoding"
        record.marshal("UTF-8")

        then: "throws a exception"
        thrown (MarcException)
    }

    def "Record with empty Leader and with fields" () {
        when:
        def record = Record.newRecordWithEmptyLeader()
        def controlNumberField = new ControlField("001", "BIB20180066252")
        record.add(controlNumberField)
        def controlDateField = new ControlField("008", "180202s        esp                 spa  ")
        record.add(controlDateField)
        def dateField100 = new DataField("100")
        record.add(dateField100)

        then:
        record.hasControlNumberField()
        record.getControlNumberField().equals(controlNumberField)
        record.getControlField("008").equals(controlDateField)
        record.getControlField("009") == null
        record.getDataField("100").equals(dateField100)
        record.getDataField("200") == null
        record.getDataFieldList().size() == 1
        record.getVariableFieldList().size() == 3

        and: "Verify that we can generate the marshal string"
        record.marshal() != ""

        when: "We can clone it"
        def cloneRecord = record.clone()

        then: "and are identical"
        record.getControlNumberField().equals(controlNumberField)
        record.getControlField("008").equals(controlDateField)
        record.getDataField("100").equals(dateField100)
        cloneRecord.marshal() == record.marshal()
    }

    def "Navigate Summerland record controlfields and datafields"() {
        when: "Given a well know MARC record"
        def record = this.makeSummerlandRecord()

        then: "Verify access to the control number field"
        record.getControlNumber() == "12883376"
        def controlField = record.getControlNumberField()
        controlField.getTag() == "001"
        controlField.getData() == "12883376".toCharArray()

        and: "Verify that we have the expected number of fields"
        def variableFields = record.getVariableFieldList()
        variableFields.size() == 15
        def controlFields = record.getControlFieldList()
        controlFields.size() == 3
        def dataFields = record.getDataFieldList()
        dataFields.size() == 12

        and: "Verify retriving a specifig controlfield by tag"
        def cfield = record.getControlField("005")
        cfield.getTag() == "005"

        and: "Verify retriving a specifig datafield by tag"
        def dfield = record.getDataField("245")
        dfield.getTag() == "245"

        // Not Implemented yet
        //def fields = record.getVariableFields("650")
        //fields.size() == 3

        //def fieldTags = ["245", "260", "300"];
        //fields = record.getVariableFields(fields);
        //fields.size() == 3
    }

    private Record makeSummerlandRecord() {
        def leader = new Leader("00714cam a2200205 a 4500")
        def record = new Record(leader)

        def cField = new ControlField("001", "12883376")
        record.add(cField)
        cField = new ControlField("005", "20030616111422.0")
        record.add(cField)
        cField = new ControlField("008", "020805s2002    nyu    j      000 1 eng  ")
        record.add(cField)

        def dField = new DataField("020", ' ' as char, ' ' as char)
        record.add(dField)
        def subField = new Subfield("a" as char, "0786808772")
        dField.add(subField)

        dField = new DataField("020", ' ' as char, ' ' as char)
        record.add(dField)
        subField = new Subfield("a" as char, "0786816155 (pbk.)")
        dField.add(subField)

        dField = new DataField("040", ' ' as char, ' ' as char)
        record.add(dField)
        subField = new Subfield("a" as char, "DLC")
        dField.add(subField)
        subField = new Subfield("c" as char, "DLC")
        dField.add(subField)
        subField = new Subfield("d" as char, "DLC")
        dField.add(subField)

        dField = new DataField("100", '1' as char, ' ' as char)
        record.add(dField)
        subField = new Subfield("a" as char, "Chabon, Michael.")
        dField.add(subField)

        dField = new DataField("245", '1' as char, '0' as char)
        record.add(dField)
        subField = new Subfield("a" as char, "Summerland /")
        dField.add(subField)
        subField = new Subfield("c" as char, "Michael Chabon.")

        dField = new DataField("250", '1' as char, '0' as char)
        record.add(dField)
        subField = new Subfield("a" as char, "1st ed.")

        dField = new DataField("260", ' ' as char, ' ' as char)
        record.add(dField)
        subField = new Subfield("a" as char, "New York :")
        dField.add(subField)
        subField = new Subfield("c" as char, "Miramax Books/Hyperion Books for Children,")
        dField.add(subField)
        subField = new Subfield("d" as char, "c2002.")
        dField.add(subField)

        dField = new DataField("300", ' ' as char, ' ' as char)
        record.add(dField)
        subField = new Subfield("a" as char, "500 p. ;")
        dField.add(subField)
        subField = new Subfield("c" as char, "22 cm.")
        dField.add(subField)

        dField = new DataField("520", ' ' as char, ' ' as char)
        record.add(dField)
        subField = new Subfield("a" as char, "Ethan Feld, the worst baseball player in the history of the game, finds himself recruited by a 100-year-old scout to help a band of fairies triumph over an ancient enemy.")
        dField.add(subField)

        dField = new DataField("650", ' ' as char, '1' as char)
        record.add(dField)
        subField = new Subfield("a" as char, "Fantasy.")
        dField.add(subField)

        dField = new DataField("650", ' ' as char, '1' as char)
        record.add(dField)
        subField = new Subfield("a" as char, "Baseball")
        dField.add(subField)
        subField = new Subfield("v" as char, "Fiction.")
        dField.add(subField)

        dField = new DataField("650", ' ' as char, '1' as char)
        record.add(dField)
        subField = new Subfield("a" as char, "Magic")
        dField.add(subField)
        subField = new Subfield("v" as char, "Fiction.")
        dField.add(subField)

        return record
    }
}
