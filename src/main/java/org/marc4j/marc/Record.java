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
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Stream;

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
     * Copy constructor
     * Copy id attributes of contained ControlField and DataField instances, so if you don't want has the same, use
     * {@link Record#copy(Record)}.
     *
     * @param other Another instance of {@link Record}, where to copy all the values
     */
    public Record(Record other)
    {
        this.leader = new Leader(other.leader);

        other.controlFields.forEach((ControlField controlField) -> {
            this.controlFields.add(new ControlField(controlField));
        });
        other.dataFields.forEach((DataField dataField) -> {
            this.dataFields.add(new DataField(dataField));
        });
    }

    /**
     * Creates a copy of the original instance without copy the id attributes of its ControlField and DataFields
     * instances.
     *
     * @param original Instance to copy.
     * @return new copy of original instance.
     */
    public static Record copy(Record original)
    {
        if (original == null) {
            return null;
        }
        Record copy = new Record(original.leader);
        original.controlFields.forEach((ControlField controlField) -> {
            copy.controlFields.add(ControlField.copy(controlField));
        });
        original.dataFields.forEach((DataField dataField) -> {
            copy.dataFields.add(DataField.copy(dataField));
        });
        return copy;
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
        List<VariableField> list = this.getVariableFields();
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
     *     field, false if there is no control
     *     number field
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
     * @deprecated Use {@link #getControlFields()}
     */
    @Deprecated
    public List<ControlField> getControlFieldList()
    {
        return this.getControlFields();
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
    public List<ControlField> getControlFields()
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
     * @deprecated Use {@link #setControlFields(List)}
     */
    @Deprecated
    public void setControlFieldList(List<ControlField> newList)
    {
        this.setControlFields(newList);
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
     * @param newControlFields the new control field collection
     * @throws IllegalAddException if newList contains an object that isn't an instance of ControlField
     */
    public void setControlFields(java.util.Collection<ControlField> newControlFields)
    {
        if (newControlFields == null || newControlFields.isEmpty()) {
            this.controlFields = new ArrayList<>();
            return;
        }

        this.controlFields = new ArrayList<>();
        for (ControlField controlField : newControlFields) {
            if (controlField instanceof ControlField) {
                this.add(controlField);
            } else {
                throw new IllegalAddException(controlField.getClass().getName(),
                    "a collection of control fields can only contain " + "ControlField objects.");
            }
        }
    }

    /**
     * Returns the collection of data fields.
     *
     * @return {@link List} - the data field collection
     * @see DataField
     * @deprecated Use {@link #getDataFields()}
     */
    @Deprecated
    public List<DataField> getDataFieldList()
    {
        return this.getDataFields();
    }

    /**
     * Returns the collection of data fields.
     *
     * @return {@link List} - the data field collection
     * @see DataField
     */
    public List<DataField> getDataFields()
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
     * @deprecated Use {@link #setDataFields(List)}
     */
    @Deprecated
    public void setDataFieldList(List<DataField> newList)
    {
        this.setDataFields(newList);
    }

    /**
     * Sets the collection of data fields.
     *
     * <p>
     * A collection of data fields is a {@link List} object with null or more {@link DataField} objects.
     * </p>
     *
     * <p>
     * <b>Note:</b> this method replaces the current {@link List} of data fields with the data fields in the new
     * {@link List}.
     * </p>
     *
     * @param newDataFields the new data field collection
     */
    public void setDataFields(java.util.Collection<DataField> newDataFields)
    {
        if (newDataFields == null || newDataFields.isEmpty()) {
            this.dataFields = new ArrayList<>();
            return;
        }
        this.dataFields = new ArrayList<>(newDataFields);
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
     * @deprecated Use {@link #getVariableFields()}
     */
    @Deprecated
    public List<VariableField> getVariableFieldList()
    {
        return this.getVariableFields();
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
    public List<VariableField> getVariableFields()
    {
        List<VariableField> variableFields = new ArrayList<>();
        variableFields.addAll(this.controlFields);
        variableFields.addAll(this.dataFields);
        return variableFields;
    }

    /**
     * Sets the collection of variable fields.
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
     * @deprecated Use {@link #setVariableFields(List)
     */
    @Deprecated
    public void setVariableFieldList(List<VariableField> newList)
    {
        this.setVariableFields(newList);
    }

    /**
     * Sets the collection of variable fields.
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
     * @param newVariableFields the new variable field collection
     */
    public void setVariableFields(java.util.Collection<VariableField> newVariableFields)
    {
        if (newVariableFields == null || newVariableFields.isEmpty()) {
            this.controlFields = new ArrayList<>();
            this.dataFields = new ArrayList<>();
            return;
        }

        this.controlFields = new ArrayList<>();
        this.dataFields = new ArrayList<>();
        for (VariableField variableField : newVariableFields) {
            if (variableField instanceof ControlField) {
                this.add((ControlField)variableField);
            } else if (variableField instanceof DataField) {
                this.add((DataField)variableField);
            } else {
                throw new IllegalAddException(variableField.getClass().getName(),
                    "a collection of variable fields can only contain " + "ControlField or DataField objects.");
            }
        }
    }

    /**
     * Returns a stream of the variable fields of this record
     */
    public Stream<VariableField> getVariableFieldsStream()
    {
        Stream<VariableField> controlFieldsStream = this.controlFields.stream().map(VariableField.class::cast);
        Stream<VariableField> dataFieldsStream = this.dataFields.stream().map(VariableField.class::cast);
        return Stream.concat(controlFieldsStream, dataFieldsStream);
    }

    /**
     * Returns a stream of the variable fields of this record, that have the indicated tag or are prefixed by a tag
     * value
     *
     * @param tagPrefix Complete Fieldtag (ie, 010, 200, 536, etc) or the fieldtag prefix (1 -> returns the datafields
     * 1XX)
     * @return A stream of VariableFields that matchs the tag. If the tag is empty, then returns an empty stream.
     */
    public Stream<? extends VariableField> getVariableFieldsStreamPrefixedBy(final String tagPrefix)
    {
        Stream<? extends VariableField> fields;

        if (StringUtils.isEmpty(tagPrefix)) {
            return Stream.empty();
        }

        if ((tagPrefix.length() == 3) && (Tag.isControlField(tagPrefix))) {
            fields = this.controlFields.stream();
        } else {
            fields = this.dataFields.stream();
        }

        return fields
            .filter(Objects::nonNull)
            .filter(field -> StringUtils.equals(field.getTag(), tagPrefix) || field.getTag().startsWith(tagPrefix));
    }

    /**
     * Returns a stream of the variable fields of this record, that have the indicated tag
     *
     * @param tag Complete Fieldtag (ie, 010, 200, 536, etc)
     * @return A stream of VariableFields that matchs the tag. If the tag is empty, then returns an empty stream.
     */
    public Stream<? extends VariableField> getVariableFieldsStream(final String tag)
    {
        Stream<? extends VariableField> fields;

        if (StringUtils.isEmpty(tag)) {
            return Stream.empty();
        }

        if ((tag.length() == 3) && (Tag.isControlField(tag))) {
            fields = this.controlFields.stream();
        } else {
            fields = this.dataFields.stream();
        }

        return fields
            .filter(Objects::nonNull)
            .filter(field -> StringUtils.equals(field.getTag(), tag));
    }

    /**
     * Returns the first Variable Field for the given tag.
     *
     * @param tag Tag name
     * @return VariableField or null
     */
    public VariableField getFirstVariableField(final String tag)
    {
        return this.getVariableFieldsStream(tag).findFirst().orElse(null);
    }

    /**
     * Returns a stream of the control fields of this record
     */
    public Stream<ControlField> getControlFieldsStream()
    {
        return this.controlFields.stream();
    }

    /**
     * Returns a stream of the control fields of this record, that have the indicated tag or are prefixed by a tag
     * value
     *
     * @param tagPrefix Complete Fieldtag (ie, 010, 001, etc) or the fieldtag prefix (01 -> returns the controlfields
     * 01X)
     * @return A stream of ControlField that matchs the tag. If the tag is empty, then returns all the control fields
     */
    public Stream<ControlField> getControlFieldsStreamPrefixedBy(final String tagPrefix)
    {
        if (StringUtils.isEmpty(tagPrefix)) {
            return this.getControlFieldsStream();
        }

        return this.controlFields.stream()
            .filter(Objects::nonNull)
            .filter(field -> StringUtils.equals(field.getTag(), tagPrefix) || field.getTag().startsWith(tagPrefix));
    }

    /**
     * Returns a stream of the control fields of this record, that have the indicated tag
     *
     * @param tag Complete Fieldtag (ie, 010, 001, etc)
     * @return A stream of ControlField that matchs the tag. If the tag is empty, then returns all the control fields
     */
    public Stream<ControlField> getControlFieldsStream(final String tag)
    {
        if (StringUtils.isEmpty(tag)) {
            return this.getControlFieldsStream();
        }

        return this.controlFields.stream()
            .filter(Objects::nonNull)
            .filter(field -> StringUtils.equals(field.getTag(), tag));
    }

    /**
     * Returns a stream of the data fields of this record
     */
    public Stream<DataField> getDataFieldsStream()
    {
        return this.dataFields.stream();
    }

    /**
     * Returns a stream of the data fields of this record, that have the indicated tag or are prefixed by a tag
     * value
     *
     * @param tagPrefix Complete Fieldtag (ie, 100, 200, 345, etc) or the fieldtag prefix (1 -> returns the datafields
     * 1XX)
     * @return A stream of ControlField that matchs the tag. If the tag is empty, then returns all data fields
     */
    public Stream<DataField> getDataFieldsStreamPrefixedBy(final String tagPrefix)
    {
        if (StringUtils.isEmpty(tagPrefix)) {
            return this.getDataFieldsStream();
        }

        return this.dataFields.stream()
            .filter(Objects::nonNull)
            .filter(field -> StringUtils.equals(field.getTag(), tagPrefix) || field.getTag().startsWith(tagPrefix));
    }

    /**
     * Returns a stream of the data fields of this record, that have the indicated tag
     *
     * @param tag Complete Fieldtag (ie, 100, 200, 345, etc)
     * @return A stream of ControlField that matchs the tag. If the tag is empty, then returns all data fields
     */
    public Stream<DataField> getDataFieldsStream(final String tag)
    {
        if (StringUtils.isEmpty(tag)) {
            return this.getDataFieldsStream();
        }

        return this.dataFields.stream()
            .filter(Objects::nonNull)
            .filter(field -> StringUtils.equals(field.getTag(), tag));
    }

    /**
     * Returns a List of VariableField objects that have a data element that matches the given regular expression.
     * <p>
     * See {@link java.util.regex.Pattern} for more information about Java regular expressions.
     * </p>
     *
     * @param pattern An instance of a compiled Pattern to use as matcher
     * @return A stream of VariableFields that matches the pattern
     */
    public Stream<VariableField> find(Pattern pattern)
    {
        return this.getVariableFieldsStream()
            .filter(variablefield -> variablefield.find(pattern));
    }

    /**
     * Returns a List of VariableField objects with the given tag that have a data element that matches the given
     * regular expression.
     * <p>
     * See {@link java.util.regex.Pattern} for more information about Java regular expressions.
     * </p>
     *
     * @param tag A field tag value
     * @param pattern An instance of a compiled Pattern to use as matcher
     * @return A stream of VariableFields that matches the given tags, and the pattern
     */
    public Stream<VariableField> find(String tag, Pattern pattern)
    {
        return this.find(Collections.singletonList(tag), pattern);
    }

    /**
     * Returns a List of VariableField objects with the given tags that have a data element that matches the given
     * regular expression.
     * <p>
     * See {@link java.util.regex.Pattern} for more information about Java regular expressions.
     * </p>
     *
     * @param tags A collection of tag values
     * @param pattern An instance of a compiled Pattern to use as matcher
     * @return A stream of VariableFields that matches any tag of the given tags, and the pattern
     */
    public Stream<VariableField> find(java.util.Collection<String> tags, Pattern pattern)
    {
        return this.getVariableFieldsStream()
            .filter(variableField -> tags.contains(variableField.getTag()))
            .filter(variableField -> variableField.find(pattern));
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
     * @param encoding charset encoding used to calculate the fields length.
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

    /**
     * @deprecated Use copy constructor {@link Record(Record)}
     * @see java.lang.Object#clone()
     */
    @Deprecated
    @Override
    public Object clone()
    {
        Record instance;
        try {
            instance = (Record)super.clone();
        } catch (CloneNotSupportedException ex) {
            throw new MarcException("Unsupported clone method.", ex);
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
