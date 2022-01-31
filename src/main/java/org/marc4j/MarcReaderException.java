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
package org.marc4j;

/**
 * <p>
 * A <code>MarcReaderException</code> thrown when an error occurs
 * while parsing MARC records.
 * </p>
 *
 * @author <a href="mailto:mail@bpeters.com">Bas Peters</a>
 */
public class MarcReaderException
    extends Exception
{
    private static final long serialVersionUID = -8406057796165098547L;

    final int pos;
    final String controlNumber;
    final String fileName;

    /**
     * <p>
     * Creates an <code>Exception</code> indicating that an error
     * occured while parsing MARC records.
     * </p>
     *
     * @param message the reason why the exception is thrown
     * @param pos position in the character stream where the exception is thrown
     */
    public MarcReaderException(String message, int pos)
    {
        super(message);
        this.fileName = null;
        this.pos = pos;
        this.controlNumber = null;
    }

    /**
     * <p>
     * Creates a new <code>MarcReaderException</code> with the
     * specified message and an underlying root cause.
     * </p>
     *
     * @param message information about the cause of the exception
     * @param ex the nested exception that caused this exception
     */
    public MarcReaderException(String message, Throwable ex)
    {
        super(message, ex);
        this.fileName = null;
        this.pos = 0;
        this.controlNumber = null;
    }

    /**
     * <p>
     * Creates an <code>Exception</code> indicating that an error
     * occured while parsing MARC records.
     * </p>
     *
     * @param message the reason why the exception is thrown
     * @param pos position in the character stream where the exception is thrown
     * @param controlNumber the control number (tag 001)
     */
    public MarcReaderException(String message, int pos, String controlNumber)
    {
        super(message);
        this.fileName = null;
        this.pos = pos;
        this.controlNumber = controlNumber;
    }

    /**
     * <p>
     * Creates an <code>Exception</code> indicating that an error
     * occured while parsing MARC records.
     * </p>
     *
     * @param fileName the name of the input file
     * @param message the reason why the exception is thrown
     * @param pos position in the character stream where the exception is thrown
     * @param controlNumber the control number (tag 001)
     */
    public MarcReaderException(String message, String fileName, int pos, String controlNumber)
    {
        super(message);
        this.fileName = fileName;
        this.pos = pos;
        this.controlNumber = controlNumber;
    }

    /**
     * <p>
     * Returns the file name or null if there is no input file.
     * </p>
     * 
     * @return <code>String</code> - the file name
     */
    public String getFileName()
    {
        return this.fileName;
    }

    /**
     * <p>
     * Returns the position in the character stream where the exception is thrown.
     * </p>
     *
     * @return <code>int</code> - the position
     */
    public int getPosition()
    {
        return this.pos;
    }

    /**
     * <p>
     * Returns the control number (tag 001).
     * </p>
     *
     * @return <code>String</code> - the control number
     */
    public String getControlNumber()
    {
        return this.controlNumber;
    }
}
