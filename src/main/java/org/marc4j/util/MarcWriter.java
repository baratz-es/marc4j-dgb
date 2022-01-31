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

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.marc4j.MarcHandler;
import org.marc4j.marc.ControlField;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Leader;
import org.marc4j.marc.MarcException;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Implements the <code>MarcHandler</code> interface
 * to write record objects to tape format (ISO 2709).
 * </p>
 *
 * @author <a href="mailto:mail@bpeters.com">Bas Peters</a>
 * @see MarcHandler
 */
public class MarcWriter
    implements MarcHandler
{

    private static final Logger log = LoggerFactory.getLogger(MarcHandler.class);
    /** Record object */
    private Record record;

    /** Data field object */
    private DataField datafield;

    /** The Writer object */
    private Writer out;

    /** The character conversion option */
    private CharacterConverter charconv = null;

    /**
     * <p>
     * Default constructor.
     * </p>
     *
     */
    public MarcWriter()
        throws IOException
    {
        this(System.out);
    }

    /**
     * <p>
     * Creates a new instance.
     * </p>
     *
     * @param out the {@link OutputStream} object
     *
     */
    public MarcWriter(OutputStream out)
        throws IOException
    {
        this(new OutputStreamWriter(out));
    }

    /**
     * <p>
     * Creates a new instance.
     * </p>
     *
     * @param out the {@link OutputStream} object
     * @param encoding the encoding
     *
     */
    public MarcWriter(OutputStream out, String encoding)
        throws IOException
    {
        this(new OutputStreamWriter(out, encoding));
    }

    /**
     * <p>
     * Creates a new instance and registers the Writer object.
     * </p>
     *
     * @param out the {@link Writer} object
     */
    public MarcWriter(Writer out)
    {
        this.setWriter(out);
    }

    /**
     * <p>
     * Registers the Writer object.
     * </p>
     *
     * @param out the {@link Writer} object
     */
    public void setWriter(Writer out)
    {
        this.out = out;
    }

    /**
     * @deprecated As of MARC4J beta 7 replaced by {@link #setCharacterConverter(CharacterConverter charconv)}
     */
    @Deprecated
    public void setUnicodeToAnsel(boolean convert)
    {
        if (convert) this.charconv = new UnicodeToAnsel();
    }

    /**
     * <p>
     * Sets the character conversion table.
     * </p>
     *
     * <p>
     * A character converter is an instance of {@link CharacterConverter}.
     * </p>
     *
     * @param charconv the character converter
     */
    public void setCharacterConverter(CharacterConverter charconv)
    {
        this.charconv = charconv;
    }

    /**
     * <p>
     * Registers the Writer object.
     * </p>
     *
     * <p>
     * If the encoding is ANSEL the input
     * stream will be converted.
     * </p>
     *
     * @param out the {@link Writer} object
     * @param convert the conversion option
     */
    public void setWriter(Writer out, boolean convert)
    {
        this.out = out;
        this.setUnicodeToAnsel(convert);
    }

    /**
     * <p>
     * System exits when the Writer object is null.
     * </p>
     *
     */
    @Override
    public void startCollection()
    {
        if (this.out == null) System.exit(0);
    }

    @Override
    public void startRecord(Leader leader)
    {
        this.record = new Record();
        this.record.add(leader);
    }

    @Override
    public void controlField(String tag, char[] data, Long id)
    {
        this.record.add(new ControlField(tag, data, id));
    }

    @Override
    public void startDataField(String tag, char ind1, char ind2, Long id)
    {
        this.datafield = new DataField(tag, ind1, ind2, id);
    }

    @Override
    public void subfield(char code, char[] data, String linkCode)
    {
        if (this.charconv != null)
            this.datafield.add(new Subfield(code, this.charconv.convert(data), linkCode));
        else
            this.datafield.add(new Subfield(code, data, linkCode));
    }

    @Override
    public void endDataField(String tag)
    {
        this.record.add(this.datafield);
    }

    @Override
    public void endRecord()
    {
        try {
            this.rawWrite(this.record.marshal());
        } catch (IOException e) {
            log.error("Se ha producido un error al escribir en la salida", e);
        } catch (MarcException e) {
            log.error("Se ha producido un error al procesar el registro", e);
        }
    }

    @Override
    public void endCollection()
    {
        try {
            this.out.flush();
            this.out.close();
        } catch (IOException e) {
            log.error("Se ha producido un error al finalizar la colección", e);
        }
    }

    private void rawWrite(String s)
        throws IOException
    {
        this.out.write(s);
    }

}
