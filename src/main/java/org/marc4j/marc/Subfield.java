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
package org.marc4j.marc;

import java.io.Serializable;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * <p>
 * <code>Subfield</code> defines behaviour for a subfield (a data
 * element within a data field).
 * </p>
 *
 * <p>
 * A subfield consists of a delimiter followed by a data element
 * identifier (together the subfield code) and a data element. The structure
 * of a data element according to the MARC standard is as follows:
 * </p>
 *
 * <pre>
 * DELIMITER DATA_ELEMENT_IDENTIFIER DATA_ELEMENT
 * </pre>
 * <p>
 * This structure is returned by the {@link #marshal()}
 * method.
 * </p>
 *
 * @author Bas Peters
 */
public class Subfield
    implements Serializable, Cloneable
{
    private static final long serialVersionUID = 7416630407651432911L;

    /** Empty value for the link code */
    public static final String EMPTY_LINK_CODE = null;

    private static final char US = MarcConstants.US;

    /** The code identifier. */
    private char code;

    /** The data element. */
    private char[] data;

    /** A code if the subfield has a link with another Record */
    private String linkCode = EMPTY_LINK_CODE;

    /** Default constructor */
    public Subfield()
    {
    }

    /**
     * <p>
     * Creates a new <code>Subfield</code> instance and registers the
     * data element identifier and the data element.
     * </p>
     *
     * @param code the data element identifier
     * @param data the data element
     */
    public Subfield(char code, char[] data)
    {
        this.setCode(code);
        this.setData(data);
    }

    /**
     * <p>
     * Creates a new <code>Subfield</code> instance and registers the
     * data element identifier and the data element.
     * </p>
     *
     * @param code the data element identifier
     * @param data the data element
     */
    public Subfield(char code, String data)
    {
        this.setCode(code);
        this.setData(data.toCharArray());
    }

    /**
     * <p>
     * Creates a new <code>Subfield</code> instance and registers the
     * data element identifier and the data element.
     * </p>
     *
     * @param code the data element identifier
     * @param data the data element
     * @param linkCode A code if the subfield has a link with another Record.
     */
    public Subfield(char code, char[] data, String linkCode)
    {
        this.setCode(code);
        this.setData(data);
        this.setLinkCode(linkCode);
    }

    /**
     * <p>
     * Creates a new <code>Subfield</code> instance and registers the
     * data element identifier and the data element.
     * </p>
     *
     * @param code the data element identifier
     * @param data the data element
     * @param linkCode A code if the subfield has a link with another Record.
     */
    public Subfield(char code, String data, String linkCode)
    {
        this.setCode(code);
        this.setData(data.toCharArray());
        this.setLinkCode(linkCode);
    }

    /**
     * Copy constructor
     *
     * @param other Another instance of Subfield
     */
    public Subfield(Subfield other)
    {
        this.code = other.code;
        this.linkCode = other.linkCode;
        this.data = String.copyValueOf(other.data).toCharArray();
    }


    /**
     * Returns <code>true</code> is the supplied regular expression pattern matches the {@link Subfield} data; else,
     * <code>false</code>.
     *
     * @param pattern An instance of a compiled Pattern to use as matcher
     */
    public boolean find(Pattern pattern)
    {
        if (this.data == null) {
            return false;
        }
        Matcher matcher = pattern.matcher(new String(this.getData()));

        return matcher.find();
    }

    /**
     * Sets the {@link Subfield} code.
     *
     * @param code The code identifier
     */
    public void setCode(char code)
    {
        this.code = code;
    }

    /**
     * Gets the {@link Subfield} code.
     *
     * @return <code>char</code> The code identifier
     */
    public char getCode()
    {
        return this.code;
    }

    /**
     * Sets the {@link Subfield} data.
     *
     * @param data The data element
     */
    public void setData(char[] data)
    {
        Verifier.checkDataElement(data);
        this.data = data;
    }

    /**
     * Sets the {@link Subfield} data.
     *
     * @param data The data element
     */
    public void setData(String data)
    {
        this.setData(data.toCharArray());
    }

    /**
     * Gets the {@link Subfield} data.
     *
     * @return <code>char[]</code> The data element
     */
    public char[] getData()
    {
        return this.data;
    }

    /**
     * Sets the {@link Subfield} link code.
     */
    public void setLinkCode(String linkCode)
    {
        this.linkCode = linkCode;
    }

    /**
     * Gets the {@link Subfield} link code.
     */
    public String getLinkCode()
    {
        return this.linkCode;
    }

    /**
     * Returns a <code>String</code> representation for a data element following the structure of a MARC data element.
     *
     * @return <code>String</code> The marshaled representation of this Subfield
     */
    public String marshal()
    {
        return new StringBuffer().append(US).append(this.code).append(this.data).toString();
    }

    /*
     * @deprecated Use {@link #Subfield(Subfield)}
     */
    @Deprecated
    @Override
    public Object clone()
    {
        return new Subfield(this);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != this.getClass()) {
            return false;
        }

        Subfield that = (Subfield)obj;
        return new EqualsBuilder()
            .append(this.code, that.code)
            .append(this.data, that.data)
            .append(this.linkCode, that.linkCode)
            .isEquals();
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder().append(this.code).append(this.data).append(this.linkCode).toHashCode();
    }

    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder();
        sb
            .append("\n            SUBFIELD - code:[ ")
            .append(this.code)
            .append(", Data:")
            .append(Arrays.toString(this.data))
            .append(this.getLinkCode() != null ? (", linkCode: ") + this.getLinkCode() : "")
            .append(" ] ");
        return sb.toString();
    }

}
