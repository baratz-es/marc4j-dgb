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
 * <code>ControlField</code> defines behavior for a control field (tag 001-009).
 * </p>
 *
 * <p>
 * Control fields are variable fields identified by tags beginning with two zero's. They are comprised of data and a
 * field terminator and do not contain indicators or subfield codes. The structure of a control field according to the
 * MARC standard is as follows:
 * </p>
 * 
 * <pre>
 * DATA_ELEMENT FIELD_TERMINATOR
 * </pre>
 * <p>
 * This structure is returned by the {@link #marshal()} method.
 * </p>
 *
 * @author <a href="mailto:mail@bpeters.com">Bas Peters</a>
 */
public class ControlField
    extends VariableField
    implements Serializable, Cloneable
{

    private static final long serialVersionUID = 1L;

    /** The MARC data element. */
    private char[] data;

    /**
     * <p>
     * Default constructor.
     * </p>
     */
    public ControlField()
    {
        super();
    }

    /**
     * <p>
     * Creates a new control field instance and registers the tag
     * and the control field data.
     * </p>
     *
     * @param tag the tag name
     * @param data the control field data
     */
    public ControlField(String tag, char[] data)
    {
        super(tag);
        this.setData(data);
    }

    /**
     * <p>
     * Creates a new control field instance and registers the tag
     * and the control field data.
     * </p>
     *
     * @param tag the tag name
     * @param data the control field data
     */
    public ControlField(String tag, String data)
    {
        super(tag);
        this.setData(data.toCharArray());
    }

    /**
     * <p>
     * Creates a new control field instance and registers the tag
     * and the control field data.
     * </p>
     *
     * @param tag the tag name
     * @param data the control field data
     * @param id the field id if exists.
     */
    public ControlField(String tag, char[] data, Long id)
    {
        this(tag, data);
        this.setId(id);
    }

    /**
     * <p>
     * Creates a new control field instance and registers the tag
     * and the control field data.
     * </p>
     *
     * @param tag the tag name
     * @param data the control field data
     * @param id the field id if exists.
     */
    public ControlField(String tag, String data, Long id)
    {
        this(tag, data.toCharArray(), id);
    }

    public ControlField(ControlField other)
    {
        super(other);
        this.data = Arrays.copyOf(other.data, other.data.length);
    }

    /**
     * <p>
     * Registers the tag.
     * </p>
     *
     * @param tag the tag name
     * @throws IllegalTagException when the tag is not a valid
     *         control field identifier
     */
    @Override
    public void setTag(String tag)
    {
        if (!Tag.isControlField(tag)) {
            // NOTE WTF this exception will be never throw, as Tag.isXXField(String) throws an exception when is
            // invalid!
            throw new IllegalTagException(tag, "not a control field identifier");
        }
        super.setTag(tag);
    }

    /**
     * <p>
     * Registers the control field data.
     * </p>
     *
     * @param data the control field data
     */
    public void setData(char[] data)
    {
        Verifier.checkDataElement(data);
        this.data = data;
    }

    /**
     * <p>
     * Registers the control field data.
     * </p>
     *
     * @param data the control field data
     */
    public void setData(String data)
    {
        this.setData(data.toCharArray());
    }

    /**
     * <p>
     * Returns the control field data.
     * </p>
     *
     * @return <code>char[]</code> - control field as a
     *             character array
     */
    public char[] getData()
    {
        return this.data;
    }

    /**
     * <p>
     * Returns a <code>String</code> representation for a control
     * field following the structure of a MARC control field.
     * </p>
     *
     * @return <code>String</code> - control field
     */
    public String marshal()
    {
        return new String(this.data) + (char)MarcConstants.FT;
    }

    /**
     * <p>
     * Returns the length of the serialized form of the control field.
     * </p>
     *
     * @return <code>int</code> - length of control field
     */
    public int getLength()
    {
        return this.marshal().length();
    }

    /*
     * @see java.lang.Object#clone()
     * @deprecated Use copy constructor  {@link #ControlField(ControlField)}
     */
    @Deprecated
    @Override
    public Object clone()
    {
        return new ControlField(this);
    }

    /*
     * @see java.lang.Object#equals()
     */
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

        ControlField that = (ControlField)obj;
        return new EqualsBuilder()
            .append(this.getTag(), that.getTag())
            .append(this.getId(), that.getId())
            .append(this.data, that.data)
            .isEquals();
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder().append(this.getTag()).append(this.getId()).append(this.data).toHashCode();
    }

    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder();
        sb
            .append("\n    CONTROLFIELD [  tag: ")
            .append(this.getTag())
            .append(", Data:")
            .append(Arrays.toString(this.data))
            .append(this.getId() != null ? (", id: ") + this.getId() : "")
            .append(" ] ");
        return sb.toString();
    }

}
