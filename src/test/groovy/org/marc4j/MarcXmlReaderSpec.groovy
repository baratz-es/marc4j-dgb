/*
 * Copyright (C) 2019 DIGIBÍS S.L.U.
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
package org.marc4j

import javax.xml.parsers.SAXParser
import javax.xml.parsers.SAXParserFactory
import javax.xml.transform.Result
import javax.xml.transform.Source
import javax.xml.transform.sax.SAXSource

import org.marc4j.marcxml.Converter
import org.marc4j.marcxml.MarcResult
import org.marc4j.marcxml.SaxErrorHandler
import org.marc4j.util.ResourcesUtil
import org.xml.sax.InputSource
import org.xml.sax.XMLReader

import spock.lang.Specification

/**
 * Unit test for MarcXmlReader
 *
 * TODO Try to read invalid files
 */
class MarcXmlReaderSpec extends Specification {

    def "Reading a valid MARC XML file"() {
        given:
        def handler = new SimpleRecordMarcHandler()
        SAXParserFactory factory = SAXParserFactory.newInstance()
        SAXParser saxParser = factory.newSAXParser()
        XMLReader xmlReader = saxParser.getXMLReader()
        xmlReader.setErrorHandler(new SaxErrorHandler())
        def inputSource = new InputSource(ResourcesUtil.getStream("/marcxml/quijote.xml"))
        Source source = new SAXSource(xmlReader, inputSource)
        Result result = new MarcResult(handler)

        when:
        Converter converter = new Converter()
        converter.convert(source, result)

        then:
        def records = handler.getRecords()
        records != null && records.size() == 1
        def record = records.get(0)
        record.getControlNumber() == "BABB20150005885"
        def dfield017 = record.getDataField("017")
        dfield017 != null
        def subfieldA = dfield017.getSubfield('a' as char)
        def subfieldB = dfield017.getSubfield('b' as char)
        subfieldA != null
        subfieldA.getData() == "M 23781-2014".toCharArray()
        subfieldB != null
        subfieldB.getData() == "Oficina Depósito Legal Madrid".toCharArray()
    }
}
