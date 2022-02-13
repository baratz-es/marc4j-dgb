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

import java.util.HashMap;
import java.util.Map;

import org.marc4j.MarcHandler;
import org.marc4j.marc.Leader;
import org.marc4j.marc.MarcException;
import org.marc4j.marc.Subfield;
import org.marc4j.marc.VariableField;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * <code>MarcXmlHandler</code> is a SAX2 <code>ContentHandler</code> that reports events to the <code>MarcHandler</code>
 * interface.
 *
 * @author Bas Peters
 * @see MarcHandler
 * @see DefaultHandler
 */
public class MarcXmlHandler
    extends DefaultHandler
{

    /** Constants representing each valid tag type */
    private static final int COLLECTION_ID = 1;
    private static final int LEADER_ID = 2;
    private static final int RECORD_ID = 3;
    private static final int CONTROLFIELD_ID = 4;
    private static final int DATAFIELD_ID = 5;
    private static final int SUBFIELD_ID = 6;

    /** The tag attribute name string */
    private static final String TAG_ATTR = "tag";

    /** The code attribute name string */
    private static final String CODE_ATTR = "code";

    /** The first indicator attribute name string */
    private static final String IND_1_ATTR = "ind1";

    /** The second indicator attribute name string */
    private static final String IND_2_ATTR = "ind2";

    /** Map for mapping of element strings to constants (Integer) */
    private static final Map<String, Integer> elementMap;

    /** Data element identifier */
    private String code;

    /** Tag name */
    private String tag;

    /** Leader object */
    private Leader leader;

    /** StringBuffer to store data */
    private StringBuffer data;

    /** MarcHandler object */
    private MarcHandler mh;

    /** Locator object */
    private Locator locator;

    static {
        elementMap = new HashMap<>();
        elementMap.put("collection", COLLECTION_ID);
        elementMap.put("leader", LEADER_ID);
        elementMap.put("record", RECORD_ID);
        elementMap.put("controlfield", CONTROLFIELD_ID);
        elementMap.put("datafield", DATAFIELD_ID);
        elementMap.put("subfield", SUBFIELD_ID);
    }

    /**
     * Construct a new default instance of the handler
     */
    public MarcXmlHandler()
    {
        this.data = new StringBuffer();
    }

    /**
     * <p>
     * Registers the <code>MarcHandler</code> object.
     * </p>
     *
     * @param mh the {@link MarcHandler} object
     */
    public void setMarcHandler(MarcHandler mh)
    {
        this.mh = mh;
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

        String realname = (name.length() == 0) ? qName : name;
        Integer elementType = elementMap.get(realname);

        // If the element isn't in the map, ignore it. Might be part of a
        // different namespace.
        if (elementType == null) {
            return;
        }

        switch (elementType) {
            case COLLECTION_ID:
                if (this.mh != null) {
                    this.mh.startCollection();
                }
                break;

            case LEADER_ID:
                this.data.delete(0, this.data.length());
                break;

            case CONTROLFIELD_ID:
                if (atts.getLength() < 1) {
                    throw new SAXParseException("Invalid controlfield", this.locator);
                }
                this.tag = atts.getValue(TAG_ATTR);

                this.data.delete(0, this.data.length());
                break;

            case DATAFIELD_ID:
                if (atts.getLength() < 3) {
                    throw new SAXParseException("Invalid datafield", this.locator);
                }
                this.tag = atts.getValue(TAG_ATTR);
                String ind1 = atts.getValue(IND_1_ATTR);
                String ind2 = atts.getValue(IND_2_ATTR);
                if (this.mh != null) {
                    this.mh.startDataField(this.tag, ind1.charAt(0), ind2.charAt(0), VariableField.EMPTY_ID);
                }

                this.data.delete(0, this.data.length());
                break;

            case SUBFIELD_ID:
                this.code = atts.getValue(CODE_ATTR);
                this.data.delete(0, this.data.length());
                break;

            default:

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

        String realname = (name.length() == 0) ? qName : name;
        Integer elementType = elementMap.get(realname);

        // If the element isn't in the map, ignore it. Might be part of a
        // different namespace.
        if (elementType == null) {
            return;
        }

        switch (elementType) {
            case COLLECTION_ID:
                if (this.mh != null) {
                    this.mh.endCollection();
                }

                break;

            case RECORD_ID:
                if (this.mh != null) {
                    this.mh.endRecord();
                }
                break;

            case LEADER_ID:
                try {
                    if (this.mh != null) {
                        this.mh.startRecord(new Leader(this.data.toString()));
                    }
                } catch (MarcException e) {
                    throw new SAXParseException("Unable to unmarshal leader", this.locator);
                }
                break;

            case CONTROLFIELD_ID:
                if (this.mh != null) {
                    this.mh.controlField(this.tag, this.data.toString().toCharArray(), VariableField.EMPTY_ID);
                }
                break;

            case DATAFIELD_ID:
                if (this.mh != null) {
                    this.mh.endDataField(this.tag);
                }
                this.tag = null;
                break;

            case SUBFIELD_ID:
                char[] ch = this.data.toString().toCharArray();
                if (this.mh != null) {
                    this.mh.subfield(this.code.charAt(0), ch, Subfield.EMPTY_LINK_CODE);
                }
                this.code = null;
                break;

            default:
        }
    }
}
