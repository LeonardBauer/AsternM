public class PriorityDictionary {
    private int[] keys;
    private int[][] values;
    private int size;

    public PriorityDictionary(int capacity) {
        keys = new int[capacity];
        values = new int[capacity][7];
        size = 0;
    }

    public void put(int key, int[] value) {
        if (size == keys.length) {
            // Handle resizing if necessary
            int newCapacity = keys.length * 2;
            int[] newKeys = new int[newCapacity];
            int[][] newValues = new int[newCapacity][7];
            System.arraycopy(keys, 0, newKeys, 0, size);
            System.arraycopy(values, 0, newValues, 0, size);
            keys = newKeys;
            values = newValues;
        }

        // Find the position to insert the new key-value pair
        int i = size - 1;
        while (i >= 0 && keys[i] > key) {
            keys[i + 1] = keys[i];
            values[i + 1] = values[i];
            i--;
        }

        keys[i + 1] = key;
        values[i + 1] = value;
        size++;
    }

    public int[][] getValues() {
        return values;
    }

    public int[] get(int key) {
        for (int i = 0; i < size; i++) {
            if (keys[i] == key) {
                return values[i];
            }
        }
        return null;
    }

    public int[] pop() {
        if (size == 0) {
            return null;
        }
        int[] poppedValue = values[0];
        // Shift elements to remove the first entry
        for (int i = 1; i < size; i++) {
            keys[i - 1] = keys[i];
            values[i - 1] = values[i];
        }
        size--;
        return poppedValue;
    }

    public int size() {
        return size;
    }


}
