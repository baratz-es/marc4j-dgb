/*
 * Copyright (C) 2019 DIGIBÍS S.L.
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

import org.marc4j.helpers.ErrorHandlerImpl
import org.marc4j.util.ResourcesUtil

import spock.lang.Specification

/**
 * Unit test for MarcReader
 * 
 * TODO Try to read invalid files
 * TODO Add MARC8 files
 */
class MarcReaderSpec extends Specification {

    def "Reading a valid ISO 2709 file encodded with ISO8859-1"() {
        given:
        def marcReader = new MarcReader()
        def handler = new TaggedWriter()
        InputStream inputStream = ResourcesUtil.getStream("/iso2709/quijote-iso8859_1.txt")
        def bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "ISO8859_1"))

        when:
        inputStream != null
        Writer out = new StringWriter()
        try {
            handler.setWriter(out)
            marcReader.setMarcHandler(handler)
            marcReader.setErrorHandler(new ErrorHandlerImpl())
            marcReader.parse(bufferedReader)
        } catch (IOException e) {
            e.printStackTrace()
        }

        then:
        out.toString() == ISO2709_TAGGED_OUTPUT
        out.close()
    }

    def "Reading Records from a valid ISO 2709 file encodded with ISO8859-1"() {
        given:
        def marcReader = new MarcReader()
        def handler = new SimpleRecordMarcHandler()
        InputStream inputStream = ResourcesUtil.getStream("/iso2709/quijote-iso8859_1.txt")
        def bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "ISO8859_1"))

        when:
        inputStream != null
        marcReader.setMarcHandler(handler)
        marcReader.setErrorHandler(new ErrorHandlerImpl())
        marcReader.parse(bufferedReader)

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
        subfieldB.getData() == "Oficina DepÃ³sito Legal Madrid".toCharArray()
    }

    final ISO2709_TAGGED_OUTPUT =
    '''Leader 00972nam a2200265 c 4500
001 BABB20150005885
003 BABB
005 20150123093710.0
008 141222s2014    sp a   | |||| 000 1 spa  
015   $aMON1412
016   $aa5591328
017   $aM 23781-2014$bOficina DepÃ³sito Legal Madrid
020   $a978-84-680-2538-4
035   $a(OCoLC)900058857
040   $aSpMaBN$bspa$cSpMaBN$erdc
080   $a087.5:82
100 1 $0BAA20110040635$aPÃ©rez-Reverte, Arturo$d1951$4aut
245 10$aDon Quijote de la Mancha$cMiguel de Cervantes Saavedra ; adaptaciÃ³n por Arturo PÃ©rez-Reverte
250   $a1Âª ed.
260   $aMadrid$bReal Academia EspaÃ±ola$bSantillana$c2014
300   $a586 p.$bil.$c22 cm
336   $aTexto (visual)$2isbdcontent
337   $asin mediaciÃ³n$2isbdmedia
594   $aAdaptaciÃ³n de: Don Quijote de la Mancha / Miguel de Cervantes Saavedra
700 1 $0BABA20150003713$aCervantes Saavedra, Miguel de$d1547-1616$tDon Quijote de la Mancha

'''
}
