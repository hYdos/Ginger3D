package tk.valoeghese.sod;

import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.bytes.ByteList;

import java.util.Iterator;

public class ByteArrayDataSection implements BaseDataSection<Byte> {
    private final ByteList array;

    public ByteArrayDataSection() {
        this.array = new ByteArrayList();
    }

    public void writeByte(byte value) {
        this.array.add(value);
    }

    public int size() {
        return array.size();
    }

    /**
     * @deprecated Should only be used by the parser! Please use the type specific methods instead for writing data.
     */
    @Deprecated
    @Override
    public <T> void writeForParser(T data) throws UnsupportedOperationException {
        if (data instanceof Byte) {
        } else {
            throw new UnsupportedOperationException("Invalid data type parameter for this data section");
        }
    }

    public byte readByte(int index) {
        return this.array.getByte(index);
    }

    @Override
    public Iterator<Byte> iterator() {
        return this.array.iterator();
    }
}
