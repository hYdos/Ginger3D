package tk.valoeghese.sod;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;

import java.util.Iterator;

public class IntArrayDataSection implements BaseDataSection<Integer> {
    private final IntList array;

    public IntArrayDataSection() {
        this.array = new IntArrayList();
    }

    public void writeInt(int value) {
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
        if (data instanceof Integer) {
        } else {
            throw new UnsupportedOperationException("Invalid data type parameter for this data section");
        }
    }

    public int readInt(int index) {
        return this.array.getInt(index);
    }

    @Override
    public Iterator<Integer> iterator() {
        return this.array.iterator();
    }
}
