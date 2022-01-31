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

import java.io.File;
import java.io.InputStream;
import java.io.Reader;

import javax.xml.transform.Source;

import org.marc4j.MarcReader;

/**
 * <p>
 * <code>MarcSource</code> is a MARC input source for
 * non-MARCXML conversions.
 * </p>
 *
 * @author <a href="mailto:mail@bpeters.com">Bas Peters</a>
 */
public class MarcSource
    implements Source
{

    public static final String FEATURE = "http://org.marc4j.marcxml.MarcSource/feature";

    private String publicId = null;
    private String systemId = null;
    private InputStream inputStream = null;
    private Reader reader = null;
    private MarcReader marcReader = null;

    /** Default constructor */
    public MarcSource()
    {
    }

    /**
     * <p>
     * Create a new instance.
     * </p>
     *
     * @param file the {@link File} object
     */
    public MarcSource(File file)
    {
        this.setSystemId(file);
    }

    /**
     * <p>
     * Create a new instance.
     * </p>
     *
     * @param marcReader the {@link MarcReader} object
     * @param file the {@link File} object
     */
    public MarcSource(MarcReader marcReader, File file)
    {
        this.setMarcReader(marcReader);
        this.setSystemId(file);
    }

    /**
     * <p>
     * Create a new instance.
     * </p>
     *
     * @param stream the {@link InputStream} object
     */
    public MarcSource(InputStream stream)
    {
        this.inputStream = stream;
    }

    /**
     * <p>
     * Create a new instance.
     * </p>
     *
     * @param marcReader the {@link MarcReader} object
     * @param stream the {@link InputStream} object
     */
    public MarcSource(MarcReader marcReader, InputStream stream)
    {
        this.inputStream = stream;
        this.setMarcReader(marcReader);
    }

    /**
     * <p>
     * Create a new instance.
     * </p>
     *
     * @param reader the {@link Reader} object
     */
    public MarcSource(Reader reader)
    {
        this.reader = reader;
    }

    /**
     * <p>
     * Create a new instance.
     * </p>
     *
     * @param marcReader the {@link MarcReader} object
     * @param reader the {@link Reader} object
     */
    public MarcSource(MarcReader marcReader, Reader reader)
    {
        this.reader = reader;
        this.setMarcReader(marcReader);
    }

    /**
     * <p>
     * Create a new instance.
     * </p>
     *
     * @param systemID the system identifier
     */
    public MarcSource(String systemID)
    {
        this.systemId = systemID;
    }

    /**
     * <p>
     * Create a new instance.
     * </p>
     *
     * @param marcReader the {@link MarcReader} object
     * @param systemID the system identifier
     */
    public MarcSource(MarcReader marcReader, String systemID)
    {
        this.systemId = systemID;
        this.setMarcReader(marcReader);
    }

    /**
     * <p>
     * Returns the InputStream object.
     * </p>
     *
     * @return InputStream - the InputStream object
     */
    public InputStream getInputStream()
    {
        return this.inputStream;
    }

    /**
     * <p>
     * Returns the public identifier.
     * </p>
     *
     * @return String - the public identifier
     */
    public String getPublicId()
    {
        return this.publicId;
    }

    /**
     * <p>
     * Returns the Reader object.
     * </p>
     *
     * @return Reader - the Reader object
     */
    public Reader getReader()
    {
        return this.reader;
    }

    /**
     * <p>
     * Returns the system identifier.
     * </p>
     *
     * @return String - the system identifier
     */
    @Override
    public String getSystemId()
    {
        return this.systemId;
    }

    /**
     * <p>
     * Returns the MarcReader object.
     * </p>
     *
     * @return MarcReader - the MarcReader object
     */
    public MarcReader getMarcReader()
    {
        return this.marcReader;
    }

    /**
     * <p>
     * Sets the MarcReader object.
     * </p>
     *
     * @param marcReader the {@link MarcReader} object
     */
    public void setMarcReader(MarcReader marcReader)
    {
        this.marcReader = marcReader;
    }

    /**
     * <p>
     * Sets the InputStream object.
     * </p>
     *
     * @param stream the {@link InputStream} object
     */
    public void setInputStream(InputStream stream)
    {
        this.inputStream = stream;
    }

    /**
     * <p>
     * Sets the public identifier.
     * </p>
     *
     * @param publicID the public identifer
     */
    public void setPublicId(String publicID)
    {
        this.publicId = publicID;
    }

    /**
     * <p>
     * Sets the Reader object.
     * </p>
     *
     * @param reader the {@link Reader} object
     */
    public void setReader(Reader reader)
    {
        this.reader = reader;
    }

    /**
     * <p>
     * Sets the File object.
     * </p>
     *
     * @param file the {@link File} object
     */
    public void setSystemId(File file)
    {
        this.systemId = file.getAbsolutePath();
    }

    /**
     * <p>
     * Sets the system identifier.
     * </p>
     *
     * @param systemID the system identifier
     */
    @Override
    public void setSystemId(String systemID)
    {
        this.systemId = systemID;
    }

}
