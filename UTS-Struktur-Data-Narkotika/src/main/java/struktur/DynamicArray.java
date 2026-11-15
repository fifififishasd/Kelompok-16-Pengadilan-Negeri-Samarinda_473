package struktur;

import java.util.Objects;

public class DynamicArray<T> implements StrukturData<T> {
    private static final int DEFAULT_CAPACITY = 10;
    private Object[] elements;
    private int size;

    public DynamicArray() {
        this(DEFAULT_CAPACITY);
    }

    public DynamicArray(int initialCapacity) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("Kapasitas tidak boleh negatif.");
        }
        elements = new Object[Math.max(1, initialCapacity)];
        size = 0;
        validateInvariant();
    }

    @Override
    public void add(T element) {
        ensureCapacity(size + 1);
        elements[size++] = element;
        validateInvariant();
    }

    public void insert(int index, T element) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index insert: " + index);
        }
        ensureCapacity(size + 1);
        for (int i = size; i > index; i--) {
            elements[i] = elements[i - 1];
        }
        elements[index] = element;
        size++;
        validateInvariant();
    }

    @Override
    @SuppressWarnings("unchecked")
    public T get(int index) {
        checkIndex(index);
        return (T) elements[index];
    }

    @Override
    @SuppressWarnings("unchecked")
    public T remove(int index) {
        checkIndex(index);
        T removed = (T) elements[index];

        for (int i = index; i < size - 1; i++) {
            elements[i] = elements[i + 1];
        }
        elements[--size] = null;

        validateInvariant();
        return removed;
    }
        @SuppressWarnings("unchecked")
public T set(int index, T element) {
    checkIndex(index);
    T oldElement = (T) elements[index];
    elements[index] = element;
    validateInvariant();
    return oldElement;
}

    public int search(T target) {
        for (int i = 0; i < size; i++) {
            if (Objects.equals(elements[i], target)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public boolean contains(T target) {
        return search(target) != -1;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public void clear() {
        for (int i = 0; i < size; i++) {
            elements[i] = null;
        }
        size = 0;
        validateInvariant();
    }

    public int capacity() {
        return elements.length;
    }

    public boolean validateInvariant() {
        if (elements == null || size < 0 || size > elements.length) {
            throw new IllegalStateException("Invariant DynamicArray tidak valid.");
        }
        for (int i = size; i < elements.length; i++) {
            if (elements[i] != null) {
                throw new IllegalStateException("Slot di luar size harus null.");
            }
        }
        return true;
    }

    private void ensureCapacity(int minimumCapacity) {
        if (minimumCapacity <= elements.length) {
            return;
        }
        int newCapacity = Math.max(elements.length * 2, minimumCapacity);
        Object[] expanded = new Object[newCapacity];
        for (int i = 0; i < size; i++) {
            expanded[i] = elements[i];
        }
        elements = expanded;
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException(
                "Index " + index + " tidak valid. Size: " + size
            );
        }
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("[");
        for (int i = 0; i < size; i++) {
            if (i > 0) result.append(", ");
            result.append(elements[i]);
        }
        return result.append("]").toString();
    }

}
