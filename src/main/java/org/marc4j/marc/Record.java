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
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 * <code>Record</code> defines behaviour for a record.
 *
 * <p>
 * The structure of a record according to the MARC standard is as
 * follows:
 * </p>
 *
 * <pre>
 * LEADER  DIRECTORY  FT  CONTROL_NUMBER_FIELD  FT  CONTROL_FIELD_1  FT   ...   CONTROL_FIELD_n  FT  DATA_FIELD_1  FT   ...   DATA_FIELD_n  FT  RT
 * </pre>
 * <p>
 * This structure is returned by the {@link #marshal()} method.
 * </p>
 * <p>
 * <b>Note:</b> the control number field (tag 001) is an instance of a {@link ControlField}.
 * The method {@link #add(ControlField field)} throws an {@link IllegalAddException} when more than one control number
 * field is supplied.
 * </p>
 *
 * @author Bas Peters
 */
public class Record
    implements Serializable, Cloneable
{

    private static final long serialVersionUID = 1L;

    /** The record terminator. */
    private static final char RT = MarcConstants.RT;

    /** The leader (record label). */
    private Leader leader;

    /** A collection of control fields. */
    private List<ControlField> controlFields = new ArrayList<>();

    /** A collection of data fields. */
    private List<DataField> dataFields = new ArrayList<>();

    /**
     * Default constructor.
     */
    public Record()
    {
    }

    /**
     * Builds a instance of Record with an empty Leader
     */
    public static Record newRecordWithEmptyLeader()
    {
        return new Record(Leader.newEmptyLeader());
    }

    /**
     * Creates a new instance for a record and sets a leader.
     *
     * @param leader the {@link Leader} object
     */
    public Record(Leader leader)
    {
        this.add(leader);
    }

    /**
     * Returns the leader.
     *
     * @return {@link Leader} - the leader
     */
    public Leader getLeader()
    {
        return this.leader;
    }

    /**
     * Registers the leader.
     *
     * @param leader the {@link Leader} object
     */
    public void add(Leader leader)
    {
        this.leader = leader;
    }

    /**
     * Adds a new {@link ControlField} instance to the collection of variable fields.
     *
     * <p>
     * Checks if the variable field is a control number field (tag 001).
     * If the field is a control number field an
     * {@link IllegalAddException} is thrown when there is already
     * a control number field in the field collection.
     * </p>
     *
     * @param field the control field
     * @throws IllegalAddException when there is already a control number field on the field map
     */
    public void add(ControlField field)
    {
        String tag = field.getTag();
        if (Tag.isControlNumberField(tag)) {
            if (this.hasControlNumberField()) {
                throw new IllegalAddException(field.getClass().getName(), "control field number already exists");
            }
            this.controlFields.add(0, field);
        } else {
            this.controlFields.add(field);
        }
    }

    /**
     * Adds a new {@link DataField} instance to the collection of variable fields.
     *
     * @param field the data field
     */
    public void add(DataField field)
    {
        this.dataFields.add(field);
    }

    /**
     * Returns the control number field (tag 001).
     *
     * @return {@link ControlField} - the control number field
     */
    public ControlField getControlNumberField()
    {
        ControlField cf = this.controlFields.get(0);
        if ("001".equals(cf.getTag())) {
            return cf;
        }
        return null;
    }

    /**
     * Returns the control number (contents for tag 001).
     *
     * @return String - the control number value
     */
    public String getControlNumber()
    {
        if (this.controlFields.isEmpty()) {
            return null;
        }

        ControlField cf = this.controlFields.get(0);
        if ("001".equals(cf.getTag())) {
            return new String(cf.getData());
        }
        return null;
    }

    /**
     * Returns the control field for the given tag.
     *
     * @param tag the tag name
     * @return ControlField - the control field object
     */
    public ControlField getControlField(String tag)
    {
        if (!Tag.isControlField(tag)) {
            return null;
        }

        for (ControlField controlField : this.controlFields) {
            ControlField cf = controlField;
            if (cf.getTag().equals(tag)) {
                return cf;
            }
        }
        return null;
    }

    /**
     * Returns true if there is a variable field with the given tag.
     *
     * @param tag the tag name
     * @return true if the variable field exists, false if not
     */
    public boolean hasVariableField(String tag)
    {
        List<VariableField> list = this.getVariableFieldList();
        for (VariableField variableField : list) {
            VariableField vf = variableField;
            if (vf.getTag().equals(tag)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the first data field for the given tag.
     *
     * @param tag Tag name
     * @return DataField
     * @deprecated Use {@link #getFirstDataField(String)}
     */
    @Deprecated
    public DataField getDataField(String tag)
    {
        return this.getFirstDataField(tag);
    }

    /**
     * Returns the first data field for the given tag.
     *
     * @param tag Tag name
     * @return DataField
     */
    public DataField getFirstDataField(String tag)
    {
        if (!Tag.isDataField(tag)) {
            return null;
        }

        for (DataField dataField : this.dataFields) {
            DataField df = dataField;
            if (df.getTag().equals(tag)) {
                return df;
            }
        }
        return null;
    }

    /**
     * <p>
     * Returns true if the collection of variable fields contains a
     * control number field.
     * </p>
     *
     * @return <code>boolean</code> - true if there is a control number
     *             field, false if there is no control
     *             number field
     */
    public boolean hasControlNumberField()
    {
        if (this.controlFields.isEmpty()) {
            return false;
        }
        ControlField cf = this.controlFields.get(0);
        return "001".equals(cf.getTag());
    }

    /**
     * Returns the collection of control fields.
     *
     * <p>
     * The collection of control fields contains:
     * </p>
     * <ul>
     * <li>the control number field
     * <li>control fields
     * </ul>
     * <p>
     *
     * @return {@link List} - the control field collection
     * @see ControlField
     */
    public List<ControlField> getControlFieldList()
    {
        return this.controlFields;
    }

    /**
     * Sets the collection of control fields.
     *
     * <p>
     * A collection of control fields is a {@link List} object
     * with null or more {@link ControlField} objects.
     * </p>
     *
     * <p>
     * <b>Note:</b> this method replaces the current {@link List}
     * of control fields with the control fields in the new {@link List}.
     * </p>
     *
     * @param newList the new control field collection
     */
    public void setControlFieldList(List<ControlField> newList)
    {
        if (newList == null) {
            this.controlFields = new ArrayList<>();
            return;
        }
        this.controlFields = new ArrayList<>();
        for (ControlField controlField : newList) {
            Object obj = controlField;
            if (obj instanceof ControlField) {
                this.add((ControlField)obj);
            } else {
                throw new IllegalAddException(obj.getClass().getName(),
                    "a collection of control fields can only contain " + "ControlField objects.");
            }
        }
    }

    /**
     * Returns the collection of data fields.
     *
     * @return {@link List} - the data field collection
     * @see DataField
     */
    public List<DataField> getDataFieldList()
    {
        return this.dataFields;
    }

    /**
     * Sets the collection of data fields.
     *
     * <p>
     * A collection of data fields is a {@link List} object
     * with null or more {@link DataField} objects.
     * </p>
     *
     * <p>
     * <b>Note:</b> this method replaces the current {@link List}
     * of data fields with the data fields in the new {@link List}.
     * </p>
     *
     * @param newList the new data field collection
     */
    public void setDataFieldList(List<DataField> newList)
    {
        if (newList == null) {
            this.dataFields = new ArrayList<>();
            return;
        }
        this.dataFields = new ArrayList<>();
        for (DataField dataField : newList) {
            this.add(dataField);
        }
    }

    /**
     * Returns the collection of variable fields.
     *
     * <p>
     * The collection of variable fields contains:
     * </p>
     * <ul>
     * <li>the control number field
     * <li>control fields
     * <li>data fields
     * </ul>
     * <p>
     *
     * @return {@link List} - the variable field collection
     * @see ControlField
     * @see DataField
     */
    public List<VariableField> getVariableFieldList()
    {
        List<VariableField> variableFields = new ArrayList<>();
        variableFields.addAll(this.controlFields);
        variableFields.addAll(this.dataFields);
        return variableFields;
    }

    /**
     * <p>
     * Sets the collection of variable fields.
     * </p>
     *
     * <p>
     * A collection of variable fields is a {@link List} object
     * with null or more {@link ControlField} or {@link DataField}
     * objects.
     * </p>
     *
     * <p>
     * <b>Note:</b> this method replaces the current {@link List}
     * of variable fields with the variable fields in the new {@link List}.
     * </p>
     *
     * @param newList the new variable field collection
     */
    public void setVariableFieldList(List<VariableField> newList)
    {
        if (newList == null) {
            this.controlFields = new ArrayList<>();
            this.dataFields = new ArrayList<>();
            return;
        }
        this.controlFields = new ArrayList<>();
        this.dataFields = new ArrayList<>();
        for (VariableField variableField : newList) {
            Object obj = variableField;
            if (obj instanceof ControlField) {
                this.add((ControlField)obj);
            } else if (obj instanceof DataField) {
                this.add((DataField)obj);
            } else {
                throw new IllegalAddException(obj.getClass().getName(),
                    "a collection of variable fields can only contain " + "ControlField or DataField objects.");
            }
        }
    }

    /**
     * Returns a <code>String</code> representation for a record following the structure of a MARC record (tape format).
     *
     * <p>
     * Variable fields are sorted by tag name.
     * </p>
     *
     * @return <code>String</code> - the MARC record
     * @throws MarcException if the record contains no leader or no control number field
     */
    public String marshal()
    {
        return this.marshal(null);
    }

    /**
     * Returns a <code>String</code> representation for a record following the structure of a MARC record (tape format).
     *
     * <p>
     * Variable fields are sorted by tag name.
     * </p>
     *
     * @param encoding charset enconding used to calculate the fields length.
     * @return <code>String</code> - the MARC record
     * @throws MarcException if the record contains no leader or no control number field.
     * @throws MarcException if the encoding is invalid.
     */
    public String marshal(String encoding)
    {

        // throw exception if record contains no leader
        if (this.leader == null) {
            throw new MarcException("Record contains no leader");
        }

        // throw exception if record contains no control number field
        if (!this.hasControlNumberField()) {
            throw new MarcException("Record contains no control number field (tag 001)");
        }

        StringBuilder data = new StringBuilder();
        Directory directory = new Directory();

        try {
            // append control fields to directory and data
            for (ControlField controlField : this.controlFields) {
                ControlField cf = controlField;
                int fieldLength = 0;
                if (StringUtils.isNotBlank(encoding)) {
                    fieldLength = cf.marshal().getBytes(encoding).length;
                } else {
                    fieldLength = cf.getLength();
                }
                directory.add(cf.getTag(), fieldLength);
                data.append(cf.marshal());
            }

            // append data fields to directory and data
            for (DataField dataField : this.dataFields) {
                DataField df = dataField;
                int fieldLength = 0;
                if (StringUtils.isNotBlank(encoding)) {
                    fieldLength = df.marshal().getBytes(encoding).length;
                } else {
                    fieldLength = df.getLength();
                }
                directory.add(df.getTag(), fieldLength);
                data.append(df.marshal());
            }

            // add base address of data and logical record length tp the leader
            int baseAddress = 24 + directory.getLength();
            int recordLength = baseAddress + data.length() + 1;
            if (StringUtils.isNotBlank(encoding)) {
                recordLength = baseAddress + data.toString().getBytes(encoding).length + 1;
            }
            this.leader.setRecordLength(recordLength);
            this.leader.setBaseAddressOfData(baseAddress);

        } catch (UnsupportedEncodingException ex) {
            throw new MarcException("Error getting the bytes of a string with encoding " + encoding, ex);
        }

        // return record in tape format
        return this.leader.marshal() + directory.marshal() + data + Record.RT;
    }

    /*
     * @see java.lang.Object#clone()
     */
    @Override
    public Object clone()
    {
        Record instance;
        try {
            instance = (Record)super.clone();
        } catch (CloneNotSupportedException ex) {
            throw new MarcException("Unssoported clone method.", ex);
        }

        instance.leader = (Leader)this.leader.clone();
        if (this.controlFields != null) {
            ArrayList<ControlField> newList = new ArrayList<>();
            for (ControlField controlField : this.controlFields) {
                newList.add((ControlField)controlField.clone());
            }
            instance.setControlFieldList(newList);
        }
        if (this.dataFields != null) {
            ArrayList<DataField> newList = new ArrayList<>();
            for (DataField dataField : this.dataFields) {
                newList.add((DataField)dataField.clone());
            }
            instance.setDataFieldList(newList);
        }
        return instance;
    }

    @Override
    public String toString()
    {
        return new StringBuilder()
            .append("RECORD \n leader:[ ")
            .append(this.leader)
            .append(" ]")
            .append("\n controlFieldList:[ ")
            .append(Arrays.toString(this.controlFields.toArray()))
            .append(" ] ")
            .append("\n dataFieldList:[ ")
            .append(Arrays.toString(this.dataFields.toArray()))
            .append(" ] ")
            .toString();
    }
}
