package tk.valoeghese.sod;

import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.floats.FloatList;

import java.util.Iterator;

public class FloatArrayDataSection implements BaseDataSection<Float> {
    private final FloatList array;

    public FloatArrayDataSection() {
        this.array = new FloatArrayList();
    }

    public void writeFloat(float value) {
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
        if (data instanceof Float) {
        } else {
            throw new UnsupportedOperationException("Invalid data type parameter for this data section");
        }
    }

    public float readFloat(int index) {
        return this.array.getFloat(index);
    }

    @Override
    public Iterator<Float> iterator() {
        return this.array.iterator();
    }
}
