package org.marc4j.marc

import spock.lang.Specification

/**
 * Unit test for DataField
 */

class DataFieldSpec extends Specification {
    final UNDEF_CHAR = '\u0000'

    def "Datafield empty constructor" () {
        when:
        def f = new DataField()

        then:
        f.id == DataField.EMPTY_ID
        f.tag == null
        f.ind1 == UNDEF_CHAR
        f.ind2 == UNDEF_CHAR
        f.list == []
    }
    def "Datafield constructor with valid tag" () {
        when:
        def f = new DataField("100")

        then:
        f.id == DataField.EMPTY_ID
        f.tag == "100"
        f.ind1 == UNDEF_CHAR
        f.ind2 == UNDEF_CHAR
        f.list == []
    }
    def "Datafield constructor with an invalid tag raises exception" () {
        when:
        def f = new DataField(a)

        then:
        thrown (IllegalTagException)

        where:
        a << ["000", "001", "008", "", "00", "11", "0000", "1111"]
    }
    def "Datafield constructor with tag and indicators" () {
        when:
        def f = new DataField("100", 'a' as char, 'b' as char)

        then:
        f.id == DataField.EMPTY_ID
        f.tag == "100"
        f.ind1 == 'a'
        f.ind2 == 'b'
        f.list == []
    }
    def "Datafield constructor with tag, indicators and id" () {
        when:
        def f = new DataField("100", 'a' as char, 'b' as char, 1)

        then:
        f.id == 1
        f.tag == "100"
        f.ind1 == 'a'
        f.ind2 == 'b'
        f.list == []
    }

    def "setTag raises an exception when passed an invaliddatafield tag" () {
        when:
        def f = new DataField()
        f.setTag (a)

        then:
        thrown (IllegalTagException)
        where:
        a << ["000", "001", "008", "", "00", "11", "0000", "1111"]
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

    def "adding a subfield and calling getSubfield, getSubfieldList returns the same subfield" () {
        setup:
        def subfield = new Subfield("a" as char, "valueA")

        when:
        def f = new DataField("100")
        f.add (subfield)

        then:
        f.getSubfieldList() == [subfield]
        f.getSubfield("a" as char) == subfield
    }

    def "adding twice the same subfield generates two subfields" () {
        when:
        def f = new DataField("100")
        f.add (new Subfield("a" as char, "value1"))
        f.add (new Subfield("a" as char, "value2"))

        then:
        f.getSubfieldList() == [new Subfield("a" as char, "value1"), new Subfield("a" as char, "value2")]
    }

    def "getSubfield returns only the first matching subfield" () {
        when:
        def f = new DataField("100")
        f.add (new Subfield("a" as char, "value1"))
        f.add (new Subfield("a" as char, "value2"))

        then:
        f.getSubfieldList() == [new Subfield("a" as char, "value1"), new Subfield("a" as char, "value2")]
        f.getSubfield("a" as char) == new Subfield("a" as char, "value1")
    }

    def "adding two different unordered subfield generates two subfields in the same order" () {
        when:
        def f = new DataField("100")
        f.add (new Subfield("b" as char, "valueB"))
        f.add (new Subfield("a" as char, "valueA"))

        then:
        f.getSubfieldList() == [new Subfield("b" as char, "valueB"), new Subfield("a" as char, "valueA")]
    }

    def "comparing the same datafield returns true" () {
        when:
        def f = new DataField("100", " " as char, "b" as char, 1)
        f.add (new Subfield ("a" as char, "valuea"))

        then:
        f.equals(f) == true
    }

    def "equals with an different datafield with same data returns true" () {
        when:
        def f1 = new DataField("100", " " as char, "b" as char, 1)
        f1.add (new Subfield ("a" as char, "valuea"))
        f1.add (new Subfield ("b" as char, "valueb"))
        def f2 = new DataField("100", " " as char, "b" as char, 1)
        f2.add (new Subfield ("a" as char, "valuea"))
        f2.add (new Subfield ("b" as char, "valueb"))

        then:
        f1.equals(f2) == true
    }

    def "equals with an similar datafield with different ordered subfields returns false" () {
        when:
        def f1 = new DataField("100", " " as char, "b" as char, 1)
        f1.add (new Subfield ("a" as char, "valuea"))
        f1.add (new Subfield ("b" as char, "valueb"))
        def f2 = new DataField("100", " " as char, "b" as char, 1)
        f2.add (new Subfield ("b" as char, "valueb"))
        f2.add (new Subfield ("a" as char, "valuea"))

        then:
        f1.equals(f2) == false
    }

    def "a clone has all fields equal except id" () {
        when:
        def f1 = new DataField("100", " " as char, "b" as char, 1)
        f1.add (new Subfield ("a" as char, "valuea"))
        def f2 = f1.clone()

        then:
        f1.getTag() == f2.getTag()
        f1.getIndicator1() == f2.getIndicator1()
        f1.getIndicator2() == f2.getIndicator2()
        f1.getSubfieldList() == f2.getSubfieldList()
        f1.getId() != f2.getId()
    }

    def "equals of a Datafield with id and its clone returns false" () {
        when:
        def f1 = new DataField("100", " " as char, "b" as char, 1)
        f1.add (new Subfield ("a" as char, "valuea"))
        f1.add (new Subfield ("b" as char, "valueb"))
        def f2 = f1.clone()

        then:
        f1.equals(f2) == false
    }

    def "equals with a clone with same id returns true" () {
        when:
        def f1 = new DataField("100", " " as char, "b" as char, 1)
        f1.add (new Subfield ("a" as char, "valuea"))
        f1.add (new Subfield ("b" as char, "valueb"))
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
        f1.add (new Subfield ("a" as char, "valuea"))
        def f2 = (DataField) f1.clone()
        f2.setId(f1.getId())
        f2.getSubfield("a" as char).getData()[0]="x"

        then:
        f2.getSubfield("a" as char).getData().toString()=="xaluea"
        f1.equals(f2) == false
    }
}
