/**
 * Copyright (C) 2002 Bas Peters (mail@bpeters.com)
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

/**
 * <p>
 * A <code>CharacterConverterLoaderException</code> is thrown
 * when an error occurs while loading a character conversion class.
 * </p>
 *
 * @author <a href="mailto:mail@bpeters.com">Bas Peters</a>
 */
public class CharacterConverterLoaderException
    extends RuntimeException
{

    private static final long serialVersionUID = 4771889265835047836L;

    /**
     * <p>
     * Creates a new <code>CharacterConverterLoaderException</code>.
     * </p>
     *
     */
    public CharacterConverterLoaderException()
    {
    }

    /**
     * <p>
     * Creates a new <code>CharacterConverterLoaderException</code>
     * with the specified message.
     * </p>
     *
     * @param message information about the cause of the exception
     */
    public CharacterConverterLoaderException(String message)
    {
        super(message);
    }

    /**
     * <p>
     * Creates a new <code>CharacterConverterLoaderException</code> with the
     * specified message and an underlying root cause.
     * </p>
     *
     * @param message information about the cause of the exception
     * @param ex the nested exception that caused this exception
     */
    public CharacterConverterLoaderException(String message, Throwable ex)
    {
        super(message, ex);
    }
}
