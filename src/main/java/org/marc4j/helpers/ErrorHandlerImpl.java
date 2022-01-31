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
package org.marc4j.helpers;

import org.marc4j.ErrorHandler;
import org.marc4j.MarcReaderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Implements the <code>ErrorHandler</code> interface to report about
 * warnings and errors that occur during the parsing of a MARC record.
 * </p>
 *
 * @author <a href="mailto:mail@bpeters.com">Bas Peters</a>
 * @see ErrorHandler
 */
public class ErrorHandlerImpl
    implements ErrorHandler
{

    private static final Logger log = LoggerFactory.getLogger(ErrorHandlerImpl.class);

    @Override
    public void warning(MarcReaderException exception)
    {
        log.warn(printMarcException("Warning", exception), exception);
    }

    @Override
    public void error(MarcReaderException exception)
    {
        log.error(printMarcException("Error", exception), exception);
    }

    @Override
    public void fatalError(MarcReaderException exception)
    {
        log.error(printMarcException("FATAL", exception), exception);
    }

    public static String printMarcException(String label, MarcReaderException e)
    {
        StringBuffer buf = new StringBuffer();
        buf.append("** ");
        buf.append(label);
        buf.append(": ");
        buf.append(e.getMessage());
        buf.append('\n');
        if (e.getControlNumber() != null) {
            buf.append("   Record Number: ");
            buf.append(e.getControlNumber());
            buf.append('\n');
        }
        buf.append("   Character: ");
        buf.append(e.getPosition());
        buf.append('\n');
        return buf.toString();
    }

}
