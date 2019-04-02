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
}
