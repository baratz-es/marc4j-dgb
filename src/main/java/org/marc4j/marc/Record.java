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
 * <p>
 * <code>Record</code> defines behaviour for a record.
 * </p>
 *
 * <p>
 * The structure of a record according to the MARC standard is as
 * follows:
 * </p>
 *
 * <pre>
 * LEADER  DIRECTORY  FT  CONTROL_NUMBER_FIELD  FT
 *   CONTROL_FIELD_1  FT   ...   CONTROL_FIELD_n  FT
 *     DATA_FIELD_1  FT   ...   DATA_FIELD_n  FT  RT
 * </pre>
 * <p>
 * This structure is returned by the {@link #marshal()}
 * method.
 * </p>
 * <p>
 * <b>Note:</b> the control number field (tag 001) is an instance
 * of a {@link ControlField}. The method {@link #add(ControlField field)}
 * throws an {@link IllegalAddException} when more than one
 * control number field is supplied.
 * </p>
 *
 * @author <a href="mailto:mail@bpeters.com">Bas Peters</a>
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
    private List<ControlField> controlFieldList = new ArrayList<>();

    /** A collection of data fields. */
    private List<DataField> dataFieldList = new ArrayList<>();

    /**
     * <p>
     * Default constructor.
     * </p>
     */
    public Record()
    {
    }

    public static Record newRecordWithEmptyLeader()
    {
        return new Record(Leader.newEmptyLeader());
    }

    /**
     * <p>
     * Creates a new instance for a record and registers the
     * leader.
     * </p>
     *
     * @param leader the {@link Leader} object
     */
    public Record(Leader leader)
    {
        this.add(leader);
    }

    /**
     * <p>
     * Returns the leader.
     * </p>
     *
     * @return {@link Leader} - the leader
     */
    public Leader getLeader()
    {
        return this.leader;
    }

    /**
     * <p>
     * Registers the leader.
     * </p>
     *
     * @param leader the {@link Leader} object
     */
    public void add(Leader leader)
    {
        this.leader = leader;
    }

    /**
     * <p>
     * Adds a new {@link ControlField} instance to
     * the collection of variable fields.
     * </p>
     *
     * <p>
     * Checks if the variable field is a control number field (tag 001).
     * If the field is a control number field an
     * {@link IllegalAddException} is thrown when there is already
     * a control number field in the field collection.
     * </p>
     *
     * @param field the control field
     * @throws IllegalAddException when there is already a control
     *         number field on the field map
     */
    public void add(ControlField field)
    {
        String tag = field.getTag();
        if (Tag.isControlNumberField(tag)) {
            if (this.hasControlNumberField()) {
                throw new IllegalAddException(field.getClass().getName(), "control field number already exists");
            }
            this.controlFieldList.add(0, field);
        } else {
            this.controlFieldList.add(field);
        }
    }

    /**
     * <p>
     * Adds a new {@link DataField} instance to
     * the collection of variable fields.
     * </p>
     *
     * @param field the data field
     */
    public void add(DataField field)
    {
        this.dataFieldList.add(field);
    }

    /**
     * <p>
     * Returns the control number field (tag 001).
     * </p>
     *
     * @return {@link ControlField} - the control number field
     */
    public ControlField getControlNumberField()
    {
        ControlField cf = this.controlFieldList.get(0);
        if ("001".equals(cf.getTag())) {
            return cf;
        }
        return null;
    }

    /**
     * <p>
     * Returns the control number (contents for tag 001).
     * </p>
     *
     * @return String - the control number value
     */
    public String getControlNumber()
    {
        if (this.controlFieldList.isEmpty()) {
            return null;
        }

        ControlField cf = this.controlFieldList.get(0);
        if ("001".equals(cf.getTag())) {
            return new String(cf.getData());
        }
        return null;
    }

    /**
     * <p>
     * Returns the control field for the given tag.
     * </p>
     *
     * @param tag the tag name
     * @return ControlField - the control field object
     */
    public ControlField getControlField(String tag)
    {
        if (!Tag.isControlField(tag)) {
            return null;
        }

        for (ControlField controlField : this.controlFieldList) {
            ControlField cf = controlField;
            if (cf.getTag().equals(tag)) {
                return cf;
            }
        }
        return null;
    }

    /**
     * <p>
     * Returns true if there is a variable field with the given tag.
     * </p>
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
     * <p>
     * Returns the data field for the given tag.
     * </p>
     *
     * @param tag the tag name
     * @return DataField - the control number value
     */
    public DataField getDataField(String tag)
    {
        if (!Tag.isDataField(tag)) {
            return null;
        }

        for (DataField dataField : this.dataFieldList) {
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
        if (this.controlFieldList.isEmpty()) {
            return false;
        }
        ControlField cf = this.controlFieldList.get(0);
        return "001".equals(cf.getTag());
    }

    /**
     * <p>
     * Returns the collection of control fields.
     * </p>
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
        return this.controlFieldList;
    }

    /**
     * <p>
     * Sets the collection of control fields.
     * </p>
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
            this.controlFieldList = new ArrayList<>();
            return;
        }
        this.controlFieldList = new ArrayList<>();
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
     * <p>
     * Returns the collection of data fields.
     * </p>
     *
     * @return {@link List} - the data field collection
     * @see DataField
     */
    public List<DataField> getDataFieldList()
    {
        return this.dataFieldList;
    }

    /**
     * <p>
     * Sets the collection of data fields.
     * </p>
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
            this.dataFieldList = new ArrayList<>();
            return;
        }
        this.dataFieldList = new ArrayList<>();
        for (DataField dataField : newList) {
            this.add(dataField);
        }
    }

    /**
     * <p>
     * Returns the collection of variable fields.
     * </p>
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
        variableFields.addAll(this.controlFieldList);
        variableFields.addAll(this.dataFieldList);
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
            this.controlFieldList = new ArrayList<>();
            this.dataFieldList = new ArrayList<>();
            return;
        }
        this.controlFieldList = new ArrayList<>();
        this.dataFieldList = new ArrayList<>();
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
     * <p>
     * Returns a <code>String</code> representation for a record
     * following the structure of a MARC record (tape format).
     * </p>
     *
     * <p>
     * Variable fields are sorted by tag name.
     * </p>
     *
     * @return <code>String</code> - the MARC record
     * @throws MarcException if the record contains no leader or no
     *         control number field
     */
    public String marshal()
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

        // append control fields to directory and data
        for (ControlField controlField : this.controlFieldList) {
            ControlField cf = controlField;
            directory.add(cf.getTag(), cf.getLength());
            data.append(cf.marshal());
        }

        // append data fields to directory and data
        for (DataField dataField : this.dataFieldList) {
            DataField df = dataField;
            directory.add(df.getTag(), df.getLength());
            data.append(df.marshal());
        }

        // add base address of data and logical record length tp the leader
        int baseAddress = 24 + directory.getLength();
        int recordLength = baseAddress + data.length() + 1;
        this.leader.setRecordLength(recordLength);
        this.leader.setBaseAddressOfData(baseAddress);

        // return record in tape format
        return this.leader.marshal() + directory.marshal() + data + Record.RT;
    }

    /**
     * <p>
     * Returns a <code>String</code> representation for a record
     * following the structure of a MARC record (tape format).
     * </p>
     *
     * <p>
     * Variable fields are sorted by tag name.
     * </p>
     *
     * @param encoding charset enconding used to calculate the
     *        datafield length.
     * @return <code>String</code> - the MARC record
     * @throws MarcException if the record contains no leader or no
     *         control number field
     */
    /*
     * NOTA IMPORTANTE: Se sobrecarga el m�todo original marshal haciendo
     * una copia del original. En el m�todo original se podr�a utilizar
     * este pas�ndole un encoding null, pero no se hace para evitar que
     * pudiese haber posibles errores con una clase que se utiliza en
     * bastantes sitios.
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
            for (ControlField controlField : this.controlFieldList) {
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
            for (DataField dataField : this.dataFieldList) {
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
        if (this.controlFieldList != null) {
            ArrayList<ControlField> newList = new ArrayList<>();
            for (ControlField controlField : this.controlFieldList) {
                newList.add((ControlField)controlField.clone());
            }
            instance.setControlFieldList(newList);
        }
        if (this.dataFieldList != null) {
            ArrayList<DataField> newList = new ArrayList<>();
            for (DataField dataField : this.dataFieldList) {
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
            .append(Arrays.toString(this.controlFieldList.toArray()))
            .append(" ] ")
            .append("\n dataFieldList:[ ")
            .append(Arrays.toString(this.dataFieldList.toArray()))
            .append(" ] ")
            .toString();
    }
}
