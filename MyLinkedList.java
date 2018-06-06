package my.java.LinkedList;

import java.util.Iterator;
import java.util.Objects;

public class MyLinkedList<T> implements Iterable<T> {

    public MyLinkedList() {}

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        for(Entry<T> item = myHead; item != null; item = item.next) {
            builder.append(item.value);

            if(item != myTail) {
                builder.append(", ");
            }
        }

        return "[" + builder.toString() + "]";
    }

    private static class Entry<T> {
        T value = null;
        Entry<T> next = null;
        Entry<T> prev = null;

        Entry(T value, Entry<T> prev, Entry<T> next) {
            this.value = value;
            this.next = next;
            this.prev = prev;
        }
    }

    private int mySize = 0;
    private Entry<T> myHead, myTail;

    public int size() {
        return mySize;
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    private T cutOutEntry(Entry<T> unit) {
        final T element = unit.value;
        final Entry<T> next = unit.next;
        final Entry<T> prev = unit.prev;

        if (next == null) {
            myTail = prev;
        } else {
            next.prev = prev;
            unit.next = null;
        }

        if (prev == null) {
            myHead = next;
        } else {
            prev.next = next;
            unit.prev = null;
        }

        unit.value = null;
        mySize--;
        return element;
    }

    public boolean contains(Object o) {
        for (Entry<T> item = myHead; item != null; item = item.next) {
            if (Objects.equals(item.value, o)) {
                return true;
            }
        }
        return false;
    }

    public boolean add(T e) {
        Entry<T> newEntry = new Entry<>(e, myTail, null);
        mySize++;

        if (myHead == null) {
            myHead = newEntry;
            myTail = myHead;

            return true;
        }

        myTail.next = newEntry;
        myTail = newEntry;
        return true;
    }

    public T get(int index) {
        if (index < 0 || index >= mySize)
            throw new IndexOutOfBoundsException();
        Entry<T> item = myHead;

        for (int i = 0; i < index; i++) {
            item = item.next;
        }

        return item.value;
    }

    public boolean remove(Object o) {
        for (Entry<T> item = myHead; item != null; item = item.next) {
            if (Objects.equals(item.value, o)) {
                cutOutEntry(item);
                return true;
            }
        }
        return false;
    }

    public T remove(int index) {
        if (index < 0 || index >= mySize)
            throw new IndexOutOfBoundsException();

        Entry<T> item = myHead;
        if (index < (mySize >> 1)) {
            for (int i = 0; i < index; i++) {
                item = item.next;
            }
        } else {
            item = myTail;
            for (int i = mySize - 1; i > index; i--) {
                item = item.prev;
            }
        }
        return cutOutEntry(item);
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            Entry<T> current = null;

            @Override
            public boolean hasNext() {
                return mySize > 0 && current != myTail;
            }

            @Override
            public T next() {
                if (!hasNext())
                    throw new IllegalStateException();
                if (current == null) {
                    current = myHead;
                } else {
                    current = current.next;
                }
                return (T) current.value;
            }

            public void remove() {
                Entry<T> tmpEntry = current.prev;
                cutOutEntry(current);
                current = tmpEntry;
            }
        };
    }
}