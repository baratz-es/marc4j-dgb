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

import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.XMLFilterImpl;

/**
 * <p>
 * <code>ExtendedFilter</code> extends <code>XMLFilterImpl</code>
 * with an implementation of the <code>LexicalHandler</code> interface.
 * </p>
 *
 * @author Bas Peters
 */
public class ExtendedFilter
    extends XMLFilterImpl
    implements LexicalHandler
{

    /** The lexical handler property */
    private static final String LEXICAL_HANDLER = "http://xml.org/sax/properties/lexical-handler";

    /** the lexical handler object */
    public LexicalHandler lh;

    /**
     * <p>
     * Sets the object for the given property.
     * </p>
     *
     * @param uri the property name
     * @param obj the property object
     */
    @Override
    public void setProperty(String uri, Object obj)
        throws SAXNotRecognizedException, SAXNotSupportedException
    {
        if (LEXICAL_HANDLER.equals(uri)) {
            this.lh = (LexicalHandler)obj;
        } else {
            super.setProperty(uri, obj);
        }
    }

    @Override
    public void startEntity(String name)
        throws SAXException
    {
        if (this.lh != null) {
            this.lh.startEntity(name);
        }
    }

    @Override
    public void endEntity(String name)
        throws SAXException
    {
        if (this.lh != null) {
            this.lh.endEntity(name);
        }
    }

    @Override
    public void startCDATA()
        throws SAXException
    {
        if (this.lh != null) {
            this.lh.startCDATA();
        }
    }

    @Override
    public void endCDATA()
        throws SAXException
    {
        if (this.lh != null) {
            this.lh.endCDATA();
        }
    }

    @Override
    public void startDTD(String name, String publicId, String systemId)
        throws SAXException
    {
        if (this.lh != null) {
            this.lh.startDTD(name, publicId, systemId);
        }
    }

    @Override
    public void endDTD()
        throws SAXException
    {
        if (this.lh != null) {
            this.lh.endDTD();
        }
    }

    @Override
    public void comment(char ch[], int start, int length)
        throws SAXException
    {
        if (this.lh != null) {
            this.lh.comment(ch, start, length);
        }
    }
}
