/*
 * Copyright (C) 2019 DIGIBÍS S.L.
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

import java.util.ArrayList;
import java.util.List;

import org.marc4j.marc.ControlField;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Leader;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;
import org.marc4j.util.CharacterConverter;

/**
 * Simple example of {@link MarcHandler} implementation to read MARC files and store on a list of {@link Record}
 */
public class SimpleRecordMarcHandler
    implements MarcHandler
{
    private List<Record> records;
    private Record record;
    private DataField datafield;
    private CharacterConverter characterConverter;

    public SimpleRecordMarcHandler()
    {
        this(null);
    }

    public SimpleRecordMarcHandler(CharacterConverter characterConverter)
    {
        this.characterConverter = characterConverter;
    }

    @Override
    public void startCollection()
    {
        this.records = new ArrayList<>();
    }

    @Override
    public void endCollection()
    {
    }

    @Override
    public void startRecord(Leader leader)
    {
        this.record = new Record();
        record.add(leader);
    }

    @Override
    public void endRecord()
    {
        this.records.add(this.record);
    }

    @Override
    public void controlField(String tag, char[] data, Long id)
    {
        this.record.add(new ControlField(tag, data));
    }

    @Override
    public void startDataField(String tag, char ind1, char ind2, Long id)
    {
        this.datafield = new DataField(tag, ind1, ind2);
    }

    @Override
    public void endDataField(String tag)
    {
        this.record.add(this.datafield);
    }

    @Override
    public void subfield(char code, char[] data, String linkCode)
    {
        if (this.characterConverter != null) {
            this.datafield.add(new Subfield(code, this.characterConverter.convert(data)));
        } else {
            this.datafield.add(new Subfield(code, data));
        }
    }

    public List<Record> getRecords()
    {
        return records;
    }

}
