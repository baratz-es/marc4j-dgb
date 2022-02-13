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
package org.marc4j.marcxml;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import org.marc4j.MarcHandler;
import org.marc4j.MarcReader;
import org.marc4j.marc.Leader;
import org.marc4j.util.CharacterConverter;
import org.marc4j.util.CharacterConverterLoader;
import org.marc4j.util.CharacterConverterLoaderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.DefaultHandler;

/**
 * <p>
 * <code>MarcXmlReader</code> is an <code>XMLReader</code> that consumes <code>MarcHandler</code> events and reports
 * events to a SAX2 <code>ContentHandler</code>.
 * </p>
 *
 * @author Bas Peters
 * @see MarcHandler
 * @see ContentHandler
 */
public class MarcXmlReader
    implements XMLReader, MarcHandler
{

    private static final Logger log = LoggerFactory.getLogger(MarcXmlReader.class);

    /** Enables pretty printing */
    private boolean prettyPrinting = true;

    /** Empty attributes */
    private static final Attributes EMPTY_ATTS = new AttributesImpl();

    /** The lexical handler property */
    private static final String LEXICAL_HANDLER = "http://xml.org/sax/properties/lexical-handler";

    /** MARC4J error handler property */
    private static final String ERROR_HANDLER = "http://marc4j.org/properties/error-handler";

    /** MARC4J ansel to unicode conversion */
    private static final String ANSEL_TO_UNICODE = "http://marc4j.org/features/ansel-to-unicode";

    /** MARC4J character conversion */
    private static final String CHARACTER_CONVERTER = "http://marc4j.org/properties/character-conversion";

    /** MARC4J pretty printing */
    private static final String PRETTY_PRINTING = "http://marc4j.org/features/pretty-printing";

    /** MARC4J document type declaration property */
    private static final String DOC_TYPE_DECL = "http://marc4j.org/properties/document-type-declaration";

    /** MARC4J schema location property */
    private static final String SCHEMA_LOC = "http://marc4j.org/properties/schema-location";

    /** Namespace for MARCXML */
    private static final String NS_URI = "http://www.loc.gov/MARC21/slim";

    /** Namespace for W3C XML Schema instance */
    private static final String NS_XSI = "http://www.w3.org/2001/XMLSchema-instance";

    /** Schema location */
    private String schemaLocation = null;

    /** System identifier */
    private String systemId = null;

    /** {@link DocType} object */
    private DoctypeDecl doctype = null;

    /** the lexical handler object */
    public LexicalHandler lh;

    /** {@link ContentHandler} object */
    private ContentHandler ch;

    /** {@link ErrorHandler} object */
    private org.marc4j.ErrorHandler eh;

    private CharacterConverter charconv = null;

    /**
     * <p>
     * Sets the content handler.
     * </p>
     *
     * @param ch
     */
    @Override
    public void setContentHandler(ContentHandler ch)
    {
        this.ch = ch;
    }

    /**
     * <p>
     * Returns the content handler.
     * </p>
     *
     * @return ch
     */
    @Override
    public ContentHandler getContentHandler()
    {
        return this.ch;
    }

    /**
     * <p>
     * Not supported.
     * </p>
     *
     * @param er
     */
    @Override
    public void setEntityResolver(EntityResolver er)
    {
    }

    @Override
    public EntityResolver getEntityResolver()
    {
        return null;
    }

    /**
     * <p>
     * Not supported.
     * </p>
     *
     * @param dh
     */
    @Override
    public void setDTDHandler(DTDHandler dh)
    {
    }

    @Override
    public DTDHandler getDTDHandler()
    {
        return null;
    }

    /**
     * <p>
     * Not supported.
     * </p>
     *
     * @param seh
     */
    @Override
    public void setErrorHandler(org.xml.sax.ErrorHandler seh)
    {
    }

    @Override
    public org.xml.sax.ErrorHandler getErrorHandler()
    {
        return null;
    }

    /**
     * <p>
     * Sets the object for the given property.
     * </p>
     *
     * @param name the property name
     * @param obj the property object
     */
    @Override
    public void setProperty(String name, Object obj)
        throws SAXNotRecognizedException, SAXNotSupportedException
    {
        if (DOC_TYPE_DECL.equals(name)) {
            this.doctype = (DoctypeDecl)obj;
        } else if (ERROR_HANDLER.equals(name)) {
            this.eh = (org.marc4j.ErrorHandler)obj;
        } else if (SCHEMA_LOC.equals(name)) {
            this.schemaLocation = (String)obj;
        } else if (CHARACTER_CONVERTER.equals(name)) {
            this.charconv = (CharacterConverter)obj;
        } else if (LEXICAL_HANDLER.equals(name)) {
            this.lh = (LexicalHandler)obj;
        } else {
            throw new SAXNotRecognizedException("Unrecongnized property: " + name);
        }
    }

    /**
     * <p>
     * Returns the object for the given property.
     * </p>
     *
     * @param name the property name
     */
    @Override
    public Object getProperty(String name)
        throws SAXNotRecognizedException, SAXNotSupportedException
    {
        if (DOC_TYPE_DECL.equals(name)) {
            return this.doctype;
        }
        if (ERROR_HANDLER.equals(name)) {
            return this.eh;
        }
        if (SCHEMA_LOC.equals(name)) {
            return this.schemaLocation;
        }
        if (CHARACTER_CONVERTER.equals(name)) {
            return this.charconv;
        }
        throw new SAXNotRecognizedException("Unrecongnized property: " + name);
    }

    /**
     * <p>
     * Sets the boolean for the feature with the given name.
     * </p>
     *
     * @param name the name of the feature
     * @param value the boolean value
     */
    @Override
    public void setFeature(String name, boolean value)
        throws SAXNotRecognizedException, SAXNotSupportedException
    {
        if ("http://xml.org/sax/features/namespaces".equals(name) && value) {
        } else if ("http://xml.org/sax/features/namespace-prefixes".equals(name) && !value) {
        } else if (ANSEL_TO_UNICODE.equals(name)) {
            this.setCharacterConverter(true);
        } else if (PRETTY_PRINTING.equals(name)) {
            this.prettyPrinting = value;
        } else {
            throw new SAXNotRecognizedException("Unrecongnized feature: " + name);
        }
    }

    /**
     * <p>
     * Returns the boolean for the feature with the given name.
     * </p>
     *
     * @param name the name of the feature
     */
    @Override
    public boolean getFeature(String name)
        throws SAXNotRecognizedException
    {
        if ("http://xml.org/sax/features/namespaces".equals(name)) {
            return true;
        }
        if ("http://xml.org/sax/features/namespace-prefixes".equals(name)) {
            return false;
        }
        if (ANSEL_TO_UNICODE.equals(name) && (this.charconv != null)) {
            return true;
        }
        if (PRETTY_PRINTING.equals(name)) {
            return this.prettyPrinting;
        }
        throw new SAXNotRecognizedException("Unrecongnized feature: " + name);
    }

    /**
     * <p>
     * Parse input from a system identifier (URI).
     * </p>
     *
     * @param systemId the system identifier (URI)
     */
    @Override
    public void parse(String systemId)
        throws SAXException, IOException
    {
        this.systemId = systemId;
        this.parse(new InputSource(systemId));
    }

    /**
     * <p>
     * Sends the input source to the <code>MarcReader</code>.
     * </p>
     *
     * @param input the {@link InputSource}
     */
    @Override
    public void parse(InputSource input)
    {
        if (this.ch != null) {
            this.ch = this.getContentHandler();
        } else {
            this.ch = new DefaultHandler();
        }

        try (BufferedReader br = this.getBufferedReaderFromInput(input, "ISO8859_1")) {

            // Create a new MarcReader object.
            MarcReader marcReader = new MarcReader();

            // Register the MarcHandler implementation.
            marcReader.setMarcHandler(this);

            // Register the ErrorHandler implementation.
            if (this.eh != null) {
                marcReader.setErrorHandler(this.eh);
            }

            // Send the file to the parse method.
            marcReader.parse(br);

        } catch (IOException | SAXException ex) {
            log.error("An error happened when trying to parse a XML document.", ex);
        }

    }

    // Convert the InputSource into a BufferedReader.
    private BufferedReader getBufferedReaderFromInput(InputSource input, String charsetName)
        throws IOException, SAXException
    {
        BufferedReader bufferedReader = null;

        if (input.getCharacterStream() != null) {
            bufferedReader = new BufferedReader(input.getCharacterStream());
        } else if (input.getByteStream() != null) {
            bufferedReader = new BufferedReader(new InputStreamReader(input.getByteStream(), charsetName));
        } else if (input.getSystemId() != null) {
            java.net.URL url = new URL(input.getSystemId());
            bufferedReader = new BufferedReader(new InputStreamReader(url.openStream(), charsetName));
        } else {
            throw new SAXException("Invalid InputSource object");
        }
        return bufferedReader;
    }

    /**
     * <p>
     * Returns the document handler being used, starts the document
     * and reports the root element.
     * </p>
     *
     */
    @Override
    public void startCollection()
    {
        try {
            AttributesImpl atts = new AttributesImpl();

            // Report start of XML document.
            this.ch.startDocument();

            // Report document type declaration
            if (this.lh != null && this.doctype != null && this.schemaLocation == null) {
                this.lh.startDTD(this.doctype.getName(), this.doctype.getPublicId(), this.doctype.getSystemId());
                this.lh.endDTD();
            }

            // Outputting namespace declarations through the attribute object,
            // since the startPrefixMapping refuses to output namespace declarations.
            if (this.schemaLocation != null) {
                atts.addAttribute("", "xsi", "xmlns:xsi", "CDATA", NS_XSI);
                atts.addAttribute(NS_XSI, "schemaLocation", "xsi:schemaLocation", "CDATA", this.schemaLocation);
            }

            // Do not output the namespace declaration for MARCXML
            // together with a document type declaration
            if (this.doctype == null) {
                atts.addAttribute("", "", "xmlns", "CDATA", NS_URI);
            }

            // Report start of prefix mapping for MARCXML
            // OK together with Document Type Delcaration?
            this.ch.startPrefixMapping("", NS_URI);

            // Report root element
            this.ch.startElement(NS_URI, "collection", "collection", atts);

        } catch (SAXException ex) {
            log.error("An error happended, when traying to add the initial elements of the document.", ex);
        }
    }

    /**
     * <p>
     * Reports the starting element for a record and the leader node.
     * </p>
     *
     * @param leader the leader
     */
    @Override
    public void startRecord(Leader leader)
    {
        try {
            if (this.prettyPrinting) {
                this.ch.ignorableWhitespace("\n  ".toCharArray(), 0, 3);
            }
            this.ch.startElement(NS_URI, "record", "record", EMPTY_ATTS);
            if (this.prettyPrinting) {
                this.ch.ignorableWhitespace("\n    ".toCharArray(), 0, 5);
            }

            Leader safeLeader = this.sanitizeLeader(leader);

            this.writeElement(NS_URI, "leader", "leader", EMPTY_ATTS, safeLeader.marshal());
        } catch (SAXException se) {
            log.error("An error happended, when traying to add the initial elements of a record.", se);
        }
    }

    /**
     * <p>
     * Reports a control field node (001-009).
     * </p>
     *
     * @param tag the tag name
     * @param data the data element
     * @param id the field id if exists.
     */
    @Override
    public void controlField(String tag, char[] data, Long id)
    {
        try {
            AttributesImpl atts = new AttributesImpl();
            atts.addAttribute("", "tag", "tag", "CDATA", tag);
            if (this.prettyPrinting) {
                this.ch.ignorableWhitespace("\n    ".toCharArray(), 0, 5);
            }
            this.writeElement(NS_URI, "controlfield", "controlfield", atts, data);
        } catch (SAXException se) {
            log.error("An error happended when was trying to add the Control field", se);
        }
    }

    /**
     * <p>
     * Reports the starting element for a data field (010-999).
     * </p>
     *
     * @param tag the tag name
     * @param ind1 the first indicator value
     * @param ind2 the second indicator value
     * @param id the field id if exists.
     */
    @Override
    public void startDataField(String tag, char ind1, char ind2, Long id)
    {
        try {
            ind1 = this.sanitizeIndicatorChar(ind1);
            ind2 = this.sanitizeIndicatorChar(ind2);

            AttributesImpl atts = new AttributesImpl();
            atts.addAttribute("", "tag", "tag", "CDATA", tag);
            atts.addAttribute("", "ind1", "ind1", "CDATA", String.valueOf(ind1));
            atts.addAttribute("", "ind2", "ind2", "CDATA", String.valueOf(ind2));
            if (this.prettyPrinting) {
                this.ch.ignorableWhitespace("\n    ".toCharArray(), 0, 5);
            }
            this.ch.startElement(NS_URI, "datafield", "datafield", atts);
        } catch (SAXException se) {
            log.error(
                "And error happended when trying to add the initial elementos of a Data field. Tag {}, {} {}, id {}",
                tag, ind1, ind2, id, se);
        }
    }

    /**
     * <p>
     * Reports a subfield node.
     * </p>
     *
     * @param code the data element identifier
     * @param data the data element
     * @param linkCode a code if the subfield has a link with another Record
     */
    @Override
    public void subfield(char code, char[] data, String linkCode)
    {
        try {
            AttributesImpl atts = new AttributesImpl();
            atts.addAttribute("", "code", "code", "CDATA", String.valueOf(code));
            if (this.prettyPrinting) {
                this.ch.ignorableWhitespace("\n      ".toCharArray(), 0, 7);
            }
            this.ch.startElement(NS_URI, "subfield", "subfield", atts);
            if (this.charconv != null) {
                char[] unicodeData = this.charconv.convert(data);
                this.ch.characters(unicodeData, 0, unicodeData.length);
            } else {
                this.ch.characters(data, 0, data.length);
            }
            this.ch.endElement(NS_URI, "subfield", "subfield");
        } catch (SAXException se) {
            log.error("An error happended trying to create a subfield with code {}", code, se);
        }
    }

    /**
     * <p>
     * Reports the closing element for a data field.
     * </p>
     *
     * @param tag the tag name
     */
    @Override
    public void endDataField(String tag)
    {
        try {
            this.ch.ignorableWhitespace("\n    ".toCharArray(), 0, 5);
            this.ch.endElement(NS_URI, "datafield", "datafield");
        } catch (SAXException se) {
            log.error("An error happended trying to close a Data Field with tag {}", tag, se);
        }
    }

    /**
     * <p>
     * Reports the closing element for a record.
     * </p>
     *
     */
    @Override
    public void endRecord()
    {
        try {
            if (this.prettyPrinting) {
                this.ch.ignorableWhitespace("\n  ".toCharArray(), 0, 3);
            }
            this.ch.endElement(NS_URI, "record", "record");
        } catch (SAXException se) {
            log.error("An error happended trying to close a record", se);
        }
    }

    /**
     * <p>
     * Reports the closing element for the root, reports the end
     * of the prefix mapping and the end a document.
     * </p>
     *
     */
    @Override
    public void endCollection()
    {
        try {
            if (this.prettyPrinting) {
                this.ch.ignorableWhitespace("\n".toCharArray(), 0, 1);
            }
            this.ch.endElement(NS_URI, "collection", "collection");
            this.ch.endPrefixMapping("");
            this.ch.endDocument();
        } catch (SAXException e) {
            log.error("An error happened trying to add the elements to close a collection", e);
        }
    }

    private void writeElement(String uri, String localName, String qName, Attributes atts, String content)
        throws SAXException
    {
        this.writeElement(uri, localName, qName, atts, content.toCharArray());
    }

    private void writeElement(String uri, String localName, String qName, Attributes atts, char[] content)
        throws SAXException
    {
        this.ch.startElement(uri, localName, qName, atts);
        this.ch.characters(content, 0, content.length);
        this.ch.endElement(uri, localName, qName);
    }

    private void setCharacterConverter(boolean convert)
    {
        if (convert) {
            try {
                this.charconv =
                    (CharacterConverter)CharacterConverterLoader.createCharacterConverter("org.marc4j.charconv",
                        "org.marc4j.util.AnselToUnicode");
            } catch (CharacterConverterLoaderException e) {
                log.error("An error happended when trying to get the character converter.", e);
            }
        }
    }

    /**
     * Sanitizes the leader type of record and bibliograhic level
     */
    private Leader sanitizeLeader(Leader leader)
    {
        Leader safeLeader = leader;
        if (this.verifyLeaderTypeOfRecord(leader)) {
            if (!this.verifyBibliographicLevel(leader)) {
                safeLeader = new Leader(leader);
                this.enforceBibliograchiLevelAsMonographic(safeLeader);
            }
        } else {
            safeLeader = new Leader(leader);
            this.enforceTypeOfRecordLanguageMaterial(safeLeader);
            if (!this.verifyBibliographicLevel(safeLeader)) {
                this.enforceBibliograchiLevelAsMonographic(safeLeader);
            }
        }
        return safeLeader;
    }

    /**
     * Checks if the record type of the leader (position 06), it's correct
     * 
     * @return True if the type of the record, it's valid
     */
    private boolean verifyLeaderTypeOfRecord(Leader leader)
    {
        char typeOfRecord = leader.getTypeOfRecord();
        switch (typeOfRecord) {
            case 'a':
            case 'c':
            case 'd':
            case 'e':
            case 'f':
            case 'g':
            case 'i':
            case 'j':
            case 'k':
            case 'm':
            case 'o':
            case 'p':
            case 'r':
            case 't':
            case 'z':
            case 'u':
            case 'v':
            case 'x':
            case 'y':
            case 'w':
            case 'q':
                return true;

            default:
                return false;
        }
    }

    /**
     * Enforces the type of record to 'a' -> Language material
     */
    private Leader enforceTypeOfRecordLanguageMaterial(Leader leader)
    {
        leader.setTypeOfRecord('a');
        return leader;
    }

    /**
     * Checks if the bibliographic level of the leader (position 07), it's correct
     */
    private boolean verifyBibliographicLevel(Leader leader)
    {
        char bibliographicLevel = leader.getImplDefined1()[0];
        switch (bibliographicLevel) {
            case 'a':
            case 'b':
            case 'c':
            case 'd':
            case 'i':
            case 'm':
            case 's':
            case 'n':
            case 'o':
            case 'p':
            case 'q':
            case 'z':
                return true;

            default:
                return false;
        }
    }

    /**
     * Enforces the bibliographic level as a Monographic component part
     */
    private Leader enforceBibliograchiLevelAsMonographic(Leader leader)
    {
        char[] implDefined = leader.getImplDefined1();

        // If position 07 it's '#', then it's hcanged to ' '
        if (implDefined[0] == '#') {
            implDefined[0] = ' ';
        } else {
            implDefined[0] = 'm';
        }
        leader.setImplDefined1(implDefined);
        return leader;
    }

    /**
     * Sanitize a indicator character. If a invalid indicator it's found, returns a ' '
     * 
     * Valid indicator characters are a to z, ' ', and 0 to 9
     */
    private char sanitizeIndicatorChar(char indicator)
    {
        indicator = Character.toLowerCase(indicator);
        if (!Character.isLetterOrDigit(indicator)) {
            indicator = ' ';
        }
        return indicator;
    }
}
