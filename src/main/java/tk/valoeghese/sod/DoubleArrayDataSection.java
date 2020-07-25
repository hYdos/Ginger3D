package tk.valoeghese.sod;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;

import java.util.Iterator;

public class DoubleArrayDataSection implements BaseDataSection<Double> {
    private final DoubleList array;

    public DoubleArrayDataSection() {
        this.array = new DoubleArrayList();
    }

    public void writeDouble(double value) {
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
        if (data instanceof Double) {
        } else {
            throw new UnsupportedOperationException("Invalid data type parameter for this data section");
        }
    }

    public double readDouble(int index) {
        return this.array.getDouble(index);
    }

    @Override
    public Iterator<Double> iterator() {
        return this.array.iterator();
    }
}
