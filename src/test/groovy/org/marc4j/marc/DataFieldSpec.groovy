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

import java.util.regex.Pattern

import spock.lang.Specification

/**
 * Unit test for DataField
 */
class DataFieldSpec extends Specification {
    final static UNDEF_CHAR = '\u0000'

    def "Datafield empty constructor" () {
        when:
        def f = new DataField()

        then:
        f.id == DataField.EMPTY_ID
        f.tag == null
        f.ind1 == UNDEF_CHAR
        f.ind2 == UNDEF_CHAR
        f.subfields == []
    }
    def "Datafield constructor with valid tag" () {
        when:
        def f = new DataField("100")

        then:
        f.id == DataField.EMPTY_ID
        f.tag == "100"
        f.ind1 == UNDEF_CHAR
        f.ind2 == UNDEF_CHAR
        f.subfields == []
    }
    def "Datafield constructor with an invalid tag raises exception" () {
        when:
        def f = new DataField(a)

        then:
        thrown (IllegalTagException)

        where:
        a << [
            "000",
            "001",
            "008",
            "",
            "00",
            "11",
            "0000",
            "1111"
        ]
    }
    def "Datafield constructor with tag and indicators" () {
        when:
        def f = new DataField("100", 'a' as char, 'b' as char)

        then:
        f.id == DataField.EMPTY_ID
        f.tag == "100"
        f.ind1 == 'a'
        f.ind2 == 'b'
        f.subfields == []
    }
    def "Datafield constructor with tag and invalid indicators raises exception" () {
        when:
        def f = new DataField("100", indicator1 as char, indicator2 as char)

        then:
        thrown (IllegalDataElementException)

        where:
        indicator1          | indicator2
        'a'                 | MarcConstants.US
        'b'                 | MarcConstants.FT
        'c'                 | MarcConstants.RT
        MarcConstants.US    | 'a'
        MarcConstants.FT    | 'b'
        MarcConstants.US    | MarcConstants.RT
    }

    def "Datafield constructor with tag, indicators and id" () {
        when:
        def f = new DataField("100", 'a' as char, 'b' as char, 1)

        then:
        f.id == 1
        f.tag == "100"
        f.ind1 == 'a'
        f.ind2 == 'b'
        f.subfields == []
    }

    def "setTag raises an exception when passed an invaliddatafield tag" () {
        when:
        def f = new DataField()
        f.setTag (a)

        then:
        thrown (IllegalTagException)

        where:
        a << [
            "000",
            "001",
            "008",
            "",
            "00",
            "11",
            "0000",
            "1111"
        ]
    }

    def "setTag modifies an existing tag" () {
        when:
        def f = new DataField("100")
        f.setTag ("101")
        then:
        f.getTag() == "101"
    }

    def "setIndicator1 modifies an existing indicator" () {
        when:
        def f = new DataField("100")
        f.setIndicator1 ("1" as char)

        then:
        f.getIndicator1() == "1"
    }

    def "setIndicator2 modifies an existing indicator" () {
        when:
        def f = new DataField("100")
        f.setIndicator2 ("#" as char)
        then:
        f.getIndicator2() == "#"
    }

    def "setIndicator1 raises an exception when passed an invalid indicator" () {
        when:
        def f = new DataField("100")
        f.setIndicator1 (indicator as char)

        then:
        thrown (IllegalDataElementException)

        where:
        indicator << [
            MarcConstants.US,
            MarcConstants.FT,
            MarcConstants.RT
        ]
    }

    def "setIndicator2 raises an exception when passed an invalid indicator" () {
        when:
        def f = new DataField("100")
        f.setIndicator2 (indicator as char)

        then:
        thrown (IllegalDataElementException)

        where:
        indicator << [
            MarcConstants.US,
            MarcConstants.FT,
            MarcConstants.RT
        ]
    }

    def "adding a subfield and calling getSubfield, getSubfieldList returns the same subfield" () {
        setup:
        def subfield = new Subfield("a" as char, "valueA")

        when:
        def f = new DataField("100")
        f.addSubfield (subfield)

        then:
        f.getSubfields() == [subfield]
        f.getSubfield("a" as char) == subfield
    }

    def "adding twice the same subfield generates two subfields" () {
        when:
        def f = new DataField("100")
        f.addSubfield (new Subfield("a" as char, "value1"))
        f.addSubfield (new Subfield("a" as char, "value2"))

        then:
        f.getSubfields() == [
            new Subfield("a" as char, "value1"),
            new Subfield("a" as char, "value2")
        ]
    }

    def "getFirstSubfield returns only the first matching subfield" () {
        given:
        def f = new DataField("100")
        f.addSubfield (new Subfield("a" as char, "value1"))
        f.addSubfield (new Subfield("a" as char, "value2"))

        expect:
        f.getSubfields() == [
            new Subfield("a" as char, "value1"),
            new Subfield("a" as char, "value2")
        ]

        when:
        def firstSubfield = f.getFirstSubfield("a" as char)

        then:
        firstSubfield.isPresent()
        firstSubfield.get() == new Subfield("a" as char, "value1")
    }

    def "adding two different unordered subfield generates two subfields in the same order" () {
        when:
        def f = new DataField("100")
        f.addSubfield (new Subfield("b" as char, "valueB"))
        f.addSubfield (new Subfield("a" as char, "valueA"))

        then:
        f.getSubfields() == [
            new Subfield("b" as char, "valueB"),
            new Subfield("a" as char, "valueA")
        ]
    }

    def "comparing the same datafield returns true" () {
        when:
        def f = new DataField("100", " " as char, "b" as char, 1)
        f.addSubfield (new Subfield ("a" as char, "valuea"))

        then:
        f.equals(f) == true
    }

    def "equals with an different datafield with same data returns true" () {
        when:
        def f1 = new DataField("100", " " as char, "b" as char, 1)
        f1.addSubfield (new Subfield ("a" as char, "valuea"))
        f1.addSubfield (new Subfield ("b" as char, "valueb"))
        def f2 = new DataField("100", " " as char, "b" as char, 1)
        f2.addSubfield (new Subfield ("a" as char, "valuea"))
        f2.addSubfield (new Subfield ("b" as char, "valueb"))

        then:
        f1.equals(f2) == true
    }

    def "equals with an similar datafield with different ordered subfields returns false" () {
        when:
        def f1 = new DataField("100", " " as char, "b" as char, 1)
        f1.addSubfield (new Subfield ("a" as char, "valuea"))
        f1.addSubfield (new Subfield ("b" as char, "valueb"))
        def f2 = new DataField("100", " " as char, "b" as char, 1)
        f2.addSubfield (new Subfield ("b" as char, "valueb"))
        f2.addSubfield (new Subfield ("a" as char, "valuea"))

        then:
        f1.equals(f2) == false
    }

    def "a clone has all fields equal except id" () {
        when:
        def f1 = new DataField("100", " " as char, "b" as char, 1)
        f1.addSubfield (new Subfield ("a" as char, "valuea"))
        def f2 = f1.clone()

        then:
        f1.getTag() == f2.getTag()
        f1.getIndicator1() == f2.getIndicator1()
        f1.getIndicator2() == f2.getIndicator2()
        f1.getSubfields() == f2.getSubfields()
        f1.getId() != f2.getId()
    }

    def "equals of a Datafield with id and its clone returns false" () {
        when:
        def f1 = new DataField("100", " " as char, "b" as char, 1)
        f1.addSubfield (new Subfield ("a" as char, "valuea"))
        f1.addSubfield (new Subfield ("b" as char, "valueb"))
        def f2 = f1.clone()

        then:
        f1.equals(f2) == false
    }

    def "equals with a clone with same id returns true" () {
        when:
        def f1 = new DataField("100", " " as char, "b" as char, 1)
        f1.addSubfield (new Subfield ("a" as char, "valuea"))
        f1.addSubfield (new Subfield ("b" as char, "valueb"))
        def f2 = f1.clone()
        f2.setId(f1.getId())
        then:
        f1.equals(f2) == true
    }

    def "equals with a modified clone with different tag returns false" () {
        when:
        def f1 = new DataField("100", " " as char, "b" as char, 1)
        def f2 = f1.clone()
        f2.setId(f1.getId())
        f2.setTag("101")

        then:
        f1.equals(f2) == false
    }

    def "equals with a modified clone with diffent list returns false" () {
        when:
        def f1 = new DataField("100", " " as char, "b" as char, 1)
        f1.addSubfield (new Subfield ("a" as char, "valuea"))
        def f2 = (DataField) f1.clone()
        f2.setId(f1.getId())
        f2.getFirstSubfield("a" as char).get().getData()[0]="x"

        then:
        f2.getFirstSubfield("a" as char).get().getData().toString()=="xaluea"
        f1.equals(f2) == false
    }

    def "test HasSubfield"() {
        when:
        def df = new DataField("245", "1" as char, "2" as char)
        df.addSubfield(new Subfield('a' as char, "test"))

        then:
        df.hasSubfield('a' as char) == true
        df.hasSubfield('x' as char) == false
    }

    def "Verify correct marshal output"() {
        when:
        def df = new DataField("245", "1" as char, "2" as char)
        df.addSubfield(new Subfield('a' as char, "test"))

        then:
        df.marshal() == "12\u001Fatest\u001E"
        df.getLength() == 9
    }

    def "Finding if a subfield value matches a regex pattern"() {
        given: "A DataField with a few subfields"
        def dataField = new DataField("245", "1" as char, "2" as char)
        dataField.addSubfield(new Subfield('a' as char, "Calderón de la Barca"))
        dataField.addSubfield (new Subfield ("a" as char, "לדרון דה לה ברקה, פדרו"))
        dataField.addSubfield (new Subfield ("b" as char, "Diccionario Biográfico Electrónico"))

        expect:
        dataField.find(Pattern.compile(regex)) == found

        where:
        regex                 || found
        "pablito"             || false
        ".*Becquer.*"         || false
        ".*Calderón.*"        || true
        ".*Biográfico.*"      || true
        "\\p{IsAlphabetic}"   || true
        ".*ר.*"               || true
    }
}
