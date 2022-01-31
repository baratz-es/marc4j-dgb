/**
 * Copyright (C) 2002 Bas Peters
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
package org.marc4j.util;

import java.io.File;
import java.io.FileInputStream;
import java.util.Hashtable;
import java.util.Vector;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 * <p>
 * <code>CodeTableHandler</code> is a SAX2 <code>ContentHandler</code>
 * that builds a data structure to facilitate AnselToUnicode character conversion.
 *
 * @author <a href="mailto:ckeith@loc.gov">Corey Keith</a>
 * @see DefaultHandler
 */
public class CodeTableHandler
    extends DefaultHandler
{

    private Hashtable sets;
    private Hashtable charset;
    private Hashtable combiningchars;

    /** Data element identifier */
    private Integer isocode;
    private Integer marc;
    private Character ucs;
    private boolean iscombining;
    private Vector combining;

    /** Tag name */
    private String tag;

    /** StringBuffer to store data */
    private StringBuffer data;

    /** Locator object */
    private Locator locator;

    public Hashtable getCharSets()
    {
        return this.sets;
    }

    public Hashtable getCombiningChars()
    {
        return this.combiningchars;
    }

    /**
     * <p>
     * Registers the SAX2 <code>Locator</code> object.
     * </p>
     *
     * @param locator the {@link Locator} object
     */
    @Override
    public void setDocumentLocator(Locator locator)
    {
        this.locator = locator;
    }

    @Override
    public void startElement(String uri, String name, String qName, Attributes atts)
        throws SAXParseException
    {
        if ("characterSet".equals(name)) {
            this.charset = new Hashtable();
            this.isocode = Integer.valueOf(atts.getValue("ISOcode"), 16);
            this.combining = new Vector();
        } else if ("marc".equals(name)) {
            this.data = new StringBuffer();
        } else if ("codeTables".equals(name)) {
            this.sets = new Hashtable();
            this.combiningchars = new Hashtable();
        } else if ("ucs".equals(name)) {
            this.data = new StringBuffer();
        } else if ("isCombining".equals(name)) {
            this.data = new StringBuffer();
        } else if ("code".equals(name)) {
            this.iscombining = false;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length)
    {
        if (this.data != null) {
            this.data.append(ch, start, length);
        }
    }

    @Override
    public void endElement(String uri, String name, String qName)
        throws SAXParseException
    {
        if ("characterSet".equals(name)) {
            this.sets.put(this.isocode, this.charset);
            this.combiningchars.put(this.isocode, this.combining);
            this.combining = null;
            this.charset = null;
        } else if ("marc".equals(name)) {
            this.marc = Integer.valueOf(this.data.toString(), 16);
        } else if ("ucs".equals(name)) {
            this.ucs = new Character((char)Integer.parseInt(this.data.toString(), 16));
        } else if ("code".equals(name)) {
            if (this.iscombining) {
                this.combining.add(this.marc);
            }
            this.charset.put(this.marc, this.ucs);
        } else if ("isCombining".equals(name) && "true".equals(this.data.toString())) {
            this.iscombining = true;
        }

        this.data = null;
    }

    public static void main(String[] args)
    {
        Hashtable charsets = null;

        try {

            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setNamespaceAware(true);
            factory.setValidating(false);
            SAXParser saxParser = factory.newSAXParser();
            XMLReader rdr = saxParser.getXMLReader();

            File file = new File("C:\\Documents and Settings\\ckeith\\Desktop\\Projects\\Code Tables\\codetables.xml");
            InputSource src = new InputSource(new FileInputStream(file));

            CodeTableHandler saxUms = new CodeTableHandler();

            rdr.setContentHandler(saxUms);
            rdr.parse(src);

            charsets = saxUms.getCharSets();

            // System.out.println( charsets.toString() );
            System.out.println(saxUms.getCombiningChars());

        } catch (Exception exc) {
            exc.printStackTrace(System.out);
            // System.err.println( "Exception: " + exc );
        }
    }
}
