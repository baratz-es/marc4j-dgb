package org.marc4j;

import java.io.IOException;
import java.io.Writer;

import org.marc4j.marc.Leader;

/**
 * Example/test implementation of {@link MarcHandler}
 */
public class TaggedWriter
    implements MarcHandler
{

    /** The Writer object */
    private Writer out;

    /** Set the writer object */
    public void setWriter(Writer out)
    {
        this.out = out;
    }

    @Override
    public void startCollection()
    {
        if (this.out == null) {
            System.exit(0);
        }
    }

    @Override
    public void startRecord(Leader leader)
    {
        this.rawWrite("Leader ");
        this.rawWrite(leader.marshal());
        this.rawWrite('\n');
    }

    @Override
    public void controlField(String tag, char[] data, Long id)
    {
        this.rawWrite(tag);
        this.rawWrite(' ');
        this.rawWrite(new String(data));
        if (id != null) {
            this.rawWrite("id: " + id);
        }
        this.rawWrite('\n');
    }

    @Override
    public void startDataField(String tag, char ind1, char ind2, Long id)
    {
        this.rawWrite(tag);
        this.rawWrite(' ');
        this.rawWrite(ind1);
        this.rawWrite(ind2);
        if (id != null) {
            this.rawWrite("id: " + id);
        }
    }

    @Override
    public void subfield(char code, char[] data, String linkCode)
    {
        this.rawWrite('$');
        this.rawWrite(code);
        this.rawWrite(new String(data));
        if (linkCode != null) {
            this.rawWrite("linkCode: " + linkCode);
        }
    }

    @Override
    public void endDataField(String tag)
    {
        this.rawWrite('\n');
    }

    @Override
    public void endRecord()
    {
        this.rawWrite('\n');
    }

    @Override
    public void endCollection()
    {
        try {
            this.out.flush();
            this.out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void rawWrite(char c)
    {
        try {
            this.out.write(c);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void rawWrite(String s)
    {
        try {
            this.out.write(s);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
