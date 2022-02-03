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
package org.marc4j.marcxml

import org.marc4j.MarcHandler
import org.marc4j.marc.VariableField
import org.xml.sax.SAXParseException
import org.xml.sax.helpers.AttributesImpl

import spock.lang.Specification

/**
 * Unit test for MarcXmlHandler
 */
class MarcXmlHandlerSpec extends Specification {

    final leaderCharArray = "00714cam a2200205 a 4500".toCharArray()

    def "Opening and Closing a collection"() {
        given:
        def atts = new AttributesImpl()
        def marcXmlHandler = new MarcXmlHandler()
        def marcHandler = Mock(MarcHandler)
        marcXmlHandler.setMarcHandler(marcHandler)

        when:
        marcXmlHandler.startElement("", "collection", "collection", atts)

        then:
        1 * marcHandler.startCollection()
        0 * marcHandler._

        when:
        marcXmlHandler.endElement("", "collection", "collection")

        then:
        1 * marcHandler.endCollection()
        0 * marcHandler._
    }

    def "Reading a Leader node"() {
        given:
        def atts = new AttributesImpl()
        def marcXmlHandler = new MarcXmlHandler()
        def marcHandler = Mock(MarcHandler)
        marcXmlHandler.setMarcHandler(marcHandler)

        when:
        marcXmlHandler.startElement("", "leader", "leader", atts)
        then:
        0 * marcHandler._

        when:
        marcXmlHandler.characters(leaderCharArray, 0, leaderCharArray.length)
        marcXmlHandler.endElement("", "leader", "leader")

        then:
        1 * marcHandler.startRecord(!null)
        0 * marcHandler._
    }


    def "Reading a Control node"() {
        given:
        def atts = new AttributesImpl()
        atts.addAttribute("", "tag", "tag", "string", "010")
        def marcXmlHandler = new MarcXmlHandler()
        def marcHandler = Mock(MarcHandler)
        marcXmlHandler.setMarcHandler(marcHandler)

        when:
        marcXmlHandler.startElement("", "controlfield", "controlfield", atts)
        then:
        0 * marcHandler._
        notThrown(SAXParseException)

        when:
        marcXmlHandler.characters("".toCharArray(), 0, 0)
        marcXmlHandler.endElement("", "controlfield", "controlfield")

        then:
        1 * marcHandler.controlField("010", _, VariableField.EMPTY_ID)
        0 * marcHandler._
    }
}
