package org.marc4j.marc

import spock.lang.Specification

/**
 * Unit test for Collection
 */

class CollectionSpec extends Specification {

    def "An empty Collection" () {
        when:
        def collection = new Collection()

        then:
        collection.getSize() == 0
    }

    def "A not empty Collection" () {
        when:
        def collection = new Collection()
        collection.add(this.generateRecord("BIB20170066252"))
        collection.add(this.generateRecord("BIB20180066252"))
        collection.add(this.generateRecord("BIB20190066252"))

        then:
        collection.getSize() == 3
        collection.getRecord(0).getControlNumberField().getData() == "BIB20170066252" as char[]
        collection.getRecord(1).getControlNumberField().getData() == "BIB20180066252" as char[]
        collection.getRecord(2).getControlNumberField().getData() == "BIB20190066252" as char[]

        when: "Marshal all records to a writer"
        def writer = new StringWriter()
        def outputString = ""
        try {
            collection.marshal(writer)
        } finally {
            outputString = writer.toString()
            writer.close()
        }

        then: "outputs without any exception launched"
        outputString != ""
    }

    def generateRecord(String controlCode) {
        def record = Record.newRecordWithEmptyLeader()
        def controlNumberField = new ControlField("001", controlCode)
        record.add(controlNumberField)
        def controlDateField = new ControlField("008", "180202s        esp                 spa  ")
        record.add(controlDateField)
        def dateField100 = new DataField("100")
        record.add(dateField100)
        return record
    }
}
