package tk.valoeghese.sod;

import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;

import java.util.Iterator;

public class LongArrayDataSection implements BaseDataSection<Long> {
    private final LongList array;

    public LongArrayDataSection() {
        this.array = new LongArrayList();
    }

    public void writeLong(long value) {
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
        if (data instanceof Long) {
        } else {
            throw new UnsupportedOperationException("Invalid data type parameter for this data section");
        }
    }

    public long readLong(int index) {
        return this.array.getLong(index);
    }

    @Override
    public Iterator<Long> iterator() {
        return this.array.iterator();
    }
}
