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
 * @author <a href="mailto:mail@bpeters.com">Bas Peters</a>
 */
public class Subfield
    implements Serializable, Cloneable
{
    private static final long serialVersionUID = 7416630407651432911L;

    /** Empty value for the link code */
    public static final String EMPTY_LINK_CODE = null;

    private static final char US = MarcConstants.US;

    /** The data element identifier. */
    private char code;

    /** The data element. */
    private char[] data;

    /** A code if the subfield has a link with another Record */
    private String linkCode;

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
     * <p>
     * Registers the data element identifier.
     * </p>
     *
     * @param code the data element identifier
     * @throws IllegalIdentifierException when the data element identifier
     *         is not a valid data element
     *         identifier
     */
    public void setCode(char code)
    {
        this.code = code;
    }

    /**
     * <p>
     * Registers the data element.
     * </p>
     *
     * @param data the data element
     */
    public void setData(char[] data)
    {
        Verifier.checkDataElement(data);
        this.data = data;
    }

    /**
     * <p>
     * Registers the data element.
     * </p>
     *
     * @param data the data element
     */
    public void setData(String data)
    {
        this.setData(data.toCharArray());
    }

    /**
     * <p>
     * Returns the data element identifier.
     * </p>
     *
     * @return <code>char</code> - the data element identifier
     */
    public char getCode()
    {
        return this.code;
    }

    /**
     * <p>
     * Returns the data element.
     * </p>
     *
     * @return <code>char[]</code> - the data element
     */
    public char[] getData()
    {
        return this.data;
    }

    /**
     * @return Devuelve el valor de linkCode.
     */
    public String getLinkCode()
    {
        return this.linkCode;
    }

    /**
     * @param linkCode Nuevo valor para linkCode.
     */
    public void setLinkCode(String linkCode)
    {
        this.linkCode = linkCode;
    }

    /**
     * <p>
     * Returns a <code>String</code> representation for a data element
     * following the structure of a MARC data element.
     * </p>
     *
     * @return <code>String</code> - the data element
     */
    public String marshal()
    {
        return new StringBuffer().append(US).append(this.code).append(this.data).toString();
    }

    /*
     * @see java.lang.Object#clone()
     */
    @Override
    public Object clone()
    {
        // Creamos nueva instancia
        return new Subfield(this.code, String.copyValueOf(this.data), this.linkCode);
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
