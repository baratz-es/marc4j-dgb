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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 * <p>
 * <code>ReverseCodeTableHandler</code> is a SAX2 <code>ContentHandler</code>
 * that builds a data structure to facilitate <code>UnicodeToAnsel</code> character conversion.
 *
 * @author <a href="mailto:ckeith@loc.gov">Corey Keith</a>
 * @see DefaultHandler
 */
public class ReverseCodeTableHandler
    extends DefaultHandler
{

    private static Logger log = LoggerFactory.getLogger(ReverseCodeTableHandler.class);

    private Hashtable charset;
    private Vector combiningchars;

    /** Data element identifier */
    private Integer isocode;
    private char[] marc;
    private Character ucs;
    private boolean combining;

    /** Tag name */
    private String tag;

    /** StringBuffer to store data */
    private StringBuffer data;

    /** Locator object */
    private Locator locator;

    public Hashtable getCharSets()
    {
        return this.charset;
    }

    public Vector getCombiningChars()
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
        if (name.equals("characterSet"))
            this.isocode = Integer.valueOf(atts.getValue("ISOcode"), 16);
        else if (name.equals("marc"))
            this.data = new StringBuffer();
        else if (name.equals("codeTables")) {
            this.charset = new Hashtable();
            this.combiningchars = new Vector();
        } else if (name.equals("ucs"))
            this.data = new StringBuffer();
        else if (name.equals("code"))
            this.combining = false;
        else if (name.equals("isCombining")) this.data = new StringBuffer();

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
        if (name.equals("marc")) {
            String marcstr = this.data.toString();
            if (marcstr.length() == 6) {
                this.marc = new char[3];
                this.marc[0] = (char)Integer.parseInt(marcstr.substring(0, 2), 16);
                this.marc[1] = (char)Integer.parseInt(marcstr.substring(2, 4), 16);
                this.marc[2] = (char)Integer.parseInt(marcstr.substring(4, 6), 16);
            } else {
                this.marc = new char[1];
                this.marc[0] = (char)Integer.parseInt(marcstr, 16);
            }
        } else if (name.equals("ucs")) {
            this.ucs = new Character((char)Integer.parseInt(this.data.toString(), 16));
        } else if (name.equals("code")) {
            if (this.combining) {
                this.combiningchars.add(this.ucs);
            }

            if (this.charset.get(this.ucs) == null) {
                Hashtable h = new Hashtable(1);
                h.put(this.isocode, this.marc);
                this.charset.put(this.ucs, h);
            } else {
                Hashtable h = (Hashtable)this.charset.get(this.ucs);
                h.put(this.isocode, this.marc);
            }
        } else if (name.equals("isCombining")) {
            if (this.data.toString().equals("true")) this.combining = true;
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

            ReverseCodeTableHandler saxUms = new ReverseCodeTableHandler();

            rdr.setContentHandler(saxUms);
            rdr.parse(src);
        } catch (Exception exc) {
            log.error("Exception: " + exc, exc);
        }
    }
}
