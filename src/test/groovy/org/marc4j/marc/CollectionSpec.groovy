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
