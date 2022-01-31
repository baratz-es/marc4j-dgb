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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import org.apache.commons.lang3.StringUtils;
import org.marc4j.marc.ControlField;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Leader;
import org.marc4j.marc.MarcConstants;
import org.marc4j.marc.MarcException;
import org.marc4j.marc.Subfield;
import org.marc4j.marc.Tag;
import org.marc4j.marc.VariableField;

/**
 * <p>
 * Parses MARC records and reports events to the <code>MarcHandler</code>
 * and optionally the <code>ErrorHandler</code>.
 * </p>
 *
 * @author <a href="mailto:mail@bpeters.com">Bas Peters</a>
 * @see MarcHandler
 * @see ErrorHandler
 */
public class MarcReader
{

    /** The record terminator */
    private static final int RT = MarcConstants.RT;

    /** The field terminator */
    private static final int FT = MarcConstants.FT;

    /** The data element identifier */
    private static final int US = MarcConstants.US;

    /** The blank character */
    private static final int BLANK = MarcConstants.BLANK;

    int fileCounter = 0;
    int recordCounter = 0;
    String controlNumber = null;
    String tag = null;
    String fileName = null;

    /** The MarcHandler object. */
    private MarcHandler mh;

    /** The ErrorHandler object. */
    private ErrorHandler eh;

    /**
     * <p>
     * Registers the <code>MarcHandler</code> implementation.
     * </p>
     *
     * @param mh the {@link MarcHandler} implementation
     */
    public void setMarcHandler(MarcHandler mh)
    {
        this.mh = mh;
    }

    /**
     * <p>
     * Registers the <code>ErrorHandler</code> implementation.
     * </p>
     *
     * @param eh the {@link ErrorHandler} implementation
     */
    public void setErrorHandler(ErrorHandler eh)
    {
        this.eh = eh;
    }

    /**
     * <p>
     * Sends a file to the MARC parser.
     * </p>
     *
     * @param fileName the filename
     */
    public void parse(String fileName)
        throws IOException
    {
        this.setFileName(fileName);
        this.parse(new FileInputStream(fileName));
    }

    /**
     * <p>
     * Sends an input stream to the MARC parser.
     * </p>
     *
     * @param input the input stream
     */
    public void parse(InputStream input)
        throws IOException
    {
        this.parse(new BufferedReader(new InputStreamReader(input, "ISO8859_1")));
    }

    /**
     * <p>
     * Sends an input stream reader to the MARC parser.
     * </p>
     *
     * @param input the input stream reader
     */
    public void parse(InputStreamReader input)
        throws IOException
    {
        if (!"ISO8859_1".equals(input.getEncoding())) {
            throw new UnsupportedEncodingException("found " + input.getEncoding() + ", require ISO8859_1");
        }
        this.parse(new BufferedReader(input));
    }

    public void parse(Reader input)
        throws IOException
    {
        final int LDRLENGTH = 24;
        final int DIRENTRYLENGTH = 12;

        // Comienza colecci�n de registros
        if (this.mh != null) {
            this.mh.startCollection();
        }

        // Recorre registros
        while (true) {
            Leader leader = null;

            // Lee los bytes de la cabecera. Si no los lee todos, termina de leer
            char[] ldr = new char[LDRLENGTH];
            int charsRead = input.read(ldr);

            if (charsRead < LDRLENGTH) {
                break;
            }

            // if (charsRead == -1)
            // break;

            // while (charsRead != -1 && charsRead != ldr.length)
            // charsRead += input.read(ldr, charsRead, ldr.length - charsRead);

            // Compone la cabecera
            try {
                leader = new Leader(new String(ldr));
            } catch (MarcException e) {
                if (this.eh != null) {
                    this.reportFatalError("Unable to parse leader");
                }
                return;
            }

            if (leader.getBaseAddressOfData() == 0 || leader.getRecordLength() == 0) {
                if (this.eh != null) {
                    this.reportFatalError("Invalid MARC ISO 2709 file");
                }
                return;
            }

            this.recordCounter += LDRLENGTH;

            // Comienza registro
            if (this.mh != null) {
                this.mh.startRecord(leader);
            }

            // Obtiene datos del directorio
            int dirLength = leader.getBaseAddressOfData() - (LDRLENGTH + 1);
            int dirEntries = dirLength / DIRENTRYLENGTH;
            String[] tag = new String[dirEntries];
            int[] length = new int[dirEntries];

            if ((dirLength % DIRENTRYLENGTH) != 0) {
                if (this.eh != null) {
                    this.reportError("Invalid directory length");
                }
                return;
            }

            // Recorre el directorio
            for (int i = 0; i < dirEntries; i++) {

                // Lee c�digo de campo
                char[] d = new char[3];
                charsRead = input.read(d);
                while (charsRead != -1 && charsRead != d.length) {
                    charsRead += input.read(d, charsRead, d.length - charsRead);
                }

                // Lee longitud de campo
                char[] e = new char[4];
                charsRead = input.read(e);
                while (charsRead != -1 && charsRead != e.length) {
                    charsRead += input.read(e, charsRead, e.length - charsRead);
                }

                // Lee offset de campo
                char[] f = new char[5];
                charsRead = input.read(f);
                while (charsRead != -1 && charsRead != f.length) {
                    charsRead += input.read(f, charsRead, f.length - charsRead);
                }

                this.recordCounter += DIRENTRYLENGTH;
                tag[i] = new String(d);
                try {
                    length[i] = Integer.parseInt(new String(e));
                } catch (NumberFormatException nfe) {
                    if (this.eh != null) {
                        this.reportError("Invalid directory entry");
                    }
                }
            }

            // Comprueba que tras el directorio est� el car�cter FT
            if (input.read() != FT && this.eh != null) {
                this.reportError("Directory not terminated");
            }
            this.recordCounter++;

            // Recorre las entradas del directorio
            for (int i = 0; i < dirEntries; i++) {

                // Lee el campo actual
                char field[] = new char[length[i]];
                charsRead = input.read(field);
                while (charsRead != -1 && charsRead != field.length) {
                    charsRead += input.read(field, charsRead, field.length - charsRead);
                }

                // Busca un FT por el final (al combinar caracteres por el encoding, puede devolver caracteres de m�s)
                if (this.eh != null) {
                    String sField = new String(field);
                    int posFT = sField.lastIndexOf(FT);
                    if (posFT < 0) {
                        this.reportError("Field not terminated");
                    } else if (posFT < (sField.length() - 1)) {
                        String resto = sField.substring(posFT + 1);
                        resto = StringUtils.replaceChars(resto, "\0", "");
                        if (resto.length() > 0) {
                            this.reportError("Characters detected in field after FT");
                        }
                    }
                }

                // Parsea el campo de control o de datos actual
                this.recordCounter += length[i];
                if (Tag.isControlField(tag[i])) {
                    this.parseControlField(tag[i], field);
                } else {
                    this.parseDataField(tag[i], field);
                }
            }

            // Comprueba que el car�cter de fin de registro es RT
            if (input.read() != RT && this.eh != null) {
                this.reportError("Record not terminated");
            }
            this.recordCounter++;

            // Verifica que el tama�o del registro coincide con el reportado en la cabecera
            if (this.recordCounter != leader.getRecordLength() && this.eh != null) {
                this.reportError("Record length not equal to characters read");
            }

            this.fileCounter += this.recordCounter;
            this.recordCounter = 0;

            // Fin de registro
            if (this.mh != null) {
                this.mh.endRecord();
            }

        }
        input.close();

        // Fin de colecci�n
        if (this.mh != null) {
            this.mh.endCollection();
        }
    }

    private void parseControlField(String tag, char[] field)
    {
        // Si el tama�o del campo no es el correcto, reporta un mensaje de advertencia
        if (field.length < 2) {
            if (this.eh != null) {
                this.reportWarning("Control Field contains no data elements for tag " + tag);
            }
            return;
        }

        // Si el c�digo es de control, lo anota
        if (Tag.isControlNumberField(tag)) {
            this.setControlNumber(this.trimFT(field));
        }

        // Parsea el campo de control
        try {
            if (this.mh != null) {
                this.mh.controlField(tag, this.trimFT(field), VariableField.EMPTY_ID);
            }
        } catch (Exception e) {
            if (this.eh != null) {
                this.reportWarning("Control Field is not valid: " + tag + " - " + new String(field));
            }
        }
    }

    private void parseDataField(String tag, char[] field)
        throws IOException
    {
        // indicators defaulting to a blank value
        char ind1 = BLANK;
        char ind2 = BLANK;
        char code = BLANK;

        StringBuffer data = null;

        // Si el tama�o del campo es demasiado peque�o, reporta advertencia y sale
        if (field.length < 4) {
            if (this.eh != null) {
                this.reportWarning("Data field contains no data elements for tag " + tag);
            }

            // En otro caso parsea el campo de datos
        } else {
            ind1 = field[0];
            ind2 = field[1];
            if (this.mh != null) {
                this.mh.startDataField(tag, ind1, ind2, VariableField.EMPTY_ID);
            }

            // Si la longitud del campo es correcta pero tras los indicadores no est� US, advertencia
            if ((field.length > 3 && field[2] != US) && (this.eh != null)) {
                this.reportWarning("Expected a data element identifier");
            }

            // Recorre los caracteres del campo de datos
            for (int i = 2; i < field.length; i++) {
                char c = field[i];
                switch (c) {
                    case US:
                        if (data != null) {
                            this.reportSubfield(code, data);
                        }
                        code = field[i + 1];
                        i++;
                        data = new StringBuffer();
                        break;

                    case FT:
                        if (data != null) {
                            this.reportSubfield(code, data);
                        }
                        break;

                    default:
                        if (data != null) {
                            data.append(c);
                        }
                }
            }
            if (this.mh != null) {
                this.mh.endDataField(tag);
            }
        }
    }

    private void reportSubfield(char code, StringBuffer data)
    {
        if (this.mh != null) {
            this.mh.subfield(code, new String(data).toCharArray(), Subfield.EMPTY_LINK_CODE);
        }
    }

    private void reportWarning(String message)
    {
        if (this.eh != null) {
            this.eh.warning(
                new MarcReaderException(message, this.getFileName(), this.getPosition(), this.getControlNumber()));
        }
    }

    private void reportError(String message)
    {
        if (this.eh != null) {
            this.eh.error(
                new MarcReaderException(message, this.getFileName(), this.getPosition(), this.getControlNumber()));
        }
    }

    private void reportFatalError(String message)
    {
        if (this.eh != null) {
            this.eh.fatalError(
                new MarcReaderException(message, this.getFileName(), this.getPosition(), this.getControlNumber()));
        }
    }

    // private void setControlNumber(String controlNumber) {
    // this.controlNumber = controlNumber;
    // }

    private void setControlNumber(char[] controlNumber)
    {
        this.controlNumber = new String(controlNumber);
    }

    private void setFileName(String fileName)
    {
        this.fileName = fileName;
    }

    private String getControlNumber()
    {
        return this.controlNumber;
    }

    private int getPosition()
    {
        return this.fileCounter + this.recordCounter;
    }

    private String getFileName()
    {
        return this.fileName;
    }

    private char[] trimFT(char[] field)
    {
        StringBuffer sb = new StringBuffer();
        for (char c : field) {
            switch (c) {
                case FT:
                    break;

                default:
                    sb.append(c);
            }
        }
        return sb.toString().toCharArray();
    }

}
