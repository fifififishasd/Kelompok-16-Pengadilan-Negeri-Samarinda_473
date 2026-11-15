package struktur;
public class AuditQueue<T> {
private static class Node<E> {
private E data;
private Node<E> next;

       private Node(E data) {
            this.data = data;
        }
    }

    private Node<T> head;
    private Node<T> tail;
    private int size;

    public AuditQueue() {
        head = null;
        tail = null;
        size = 0;
        validateInvariant();
    }

    
    public void enqueue(T value) {
        Node<T> node = new Node<>(value);

        if (isEmpty()) {
            head = node;
            tail = node;
        } else {
            tail.next = node;
            tail = node;
        }

        size++;
        validateInvariant();
    }

    public T dequeue() {
        if (isEmpty()) {
            return null;
        }

        T value = head.data;
        head = head.next;
        size--;

        if (head == null) {
            tail = null;
        }

        validateInvariant();
        return value;
    }

    public T peek() {
        return isEmpty() ? null : head.data;
    }

    public T get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException(
                "Index " + index + " tidak valid. Size: " + size
            );
        }

        Node<T> current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        return current.data;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void clear() {
        Node<T> current = head;
        while (current != null) {
            Node<T> next = current.next;
            current.next = null;
            current.data = null;
            current = next;
        }

        head = null;
        tail = null;
        size = 0;
        validateInvariant();
    }

    public boolean validateInvariant() {
        if (size < 0) {
            throw new IllegalStateException("Size AuditQueue tidak boleh negatif.");
        }

        if (size == 0) {
            if (head != null || tail != null) {
                throw new IllegalStateException(
                    "Antrean kosong harus memiliki head dan tail null."
                );
            }
            return true;
        }

        if (head == null || tail == null || tail.next != null) {
            throw new IllegalStateException("Pointer AuditQueue tidak valid.");
        }

        int counted = 0;
        Node<T> current = head;
        Node<T> last = null;

        while (current != null) {
            counted++;
            last = current;
            current = current.next;
        }

        if (counted != size || last != tail) {
            throw new IllegalStateException(
                "Jumlah node atau pointer tail AuditQueue tidak konsisten."
            );
        }

        return true;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        Node<T> current = head;
        int index = 1;

        while (current != null) {
            result.append(index++)
                  .append(". ")
                  .append(current.data);
            current = current.next;
            if (current != null) {
                result.append(System.lineSeparator());
            }
        }

        return result.toString();
    }
}
