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

    private Throwable cause = null;

    int pos;
    String controlNumber;
    String fileName = null;

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
        this.setPosition(pos);
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
        super(message);
        this.initCause(ex);
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
        this.setPosition(pos);
        this.setControlNumber(controlNumber);
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
        this.setFileName(fileName);
        this.setPosition(pos);
        this.setControlNumber(controlNumber);
    }

    /**
     * <p>
     * Sets the root cause of this exception. This may
     * only be called once. Subsequent calls throw an
     * <code>IllegalStateException</code>.
     * </p>
     *
     * @param cause the root cause of this exception
     * @return the root cause of this exception
     * @throws IllegalStateException if this method is called twice.
     */
    @Override
    public Throwable initCause(Throwable cause)
    {
        if (this.cause == null) {
            this.cause = cause;
        } else {
            throw new IllegalStateException("Cannot reset the cause");
        }
        return cause;
    }

    private void setFileName(String fileName)
    {
        this.fileName = fileName;
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

    private void setPosition(int pos)
    {
        this.pos = pos;
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

    private void setControlNumber(String controlNumber)
    {
        this.controlNumber = controlNumber;
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
