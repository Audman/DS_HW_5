import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;

interface Map<K,V> {

    int size();

    boolean isEmpty();

    V get(K key);

    V put(K key, V value);

    V remove(K key);

    Iterable<K> keySet();

    Iterable<V> values();

    Iterable<Entry<K,V>> entrySet();
}

interface SortedMap<K,V> extends Map<K,V>{

    Entry<K,V> firstEntry();

    Entry<K,V> lastEntry();

    Entry<K,V> ceilingEntry(K key) throws IllegalArgumentException;

    Entry<K,V> floorEntry(K key) throws IllegalArgumentException;

    Entry<K,V> lowerEntry(K key) throws IllegalArgumentException;

    Entry<K,V> higherEntry(K key) throws IllegalArgumentException;

    Iterable<Entry<K,V>> subMap(K fromKey, K toKey) throws IllegalArgumentException;
}

interface List<E> extends Iterable<E> {

    int size();

    boolean isEmpty();

    E get(int i) throws IndexOutOfBoundsException;

    E set(int i, E e) throws IndexOutOfBoundsException;

    void add(int i, E e) throws IndexOutOfBoundsException;

    E remove(int i) throws IndexOutOfBoundsException;

    Iterator<E> iterator();
}

abstract class AbstractMap<K,V> implements Map<K,V> {

    public boolean isEmpty() { return size() == 0; }

    protected static class MapEntry<K,V> implements Entry<K,V> {
        private K k;
        private V v;

        public MapEntry(K key, V value) {
            k = key;
            v = value;
        }

        public K getKey() { return k; }
        public V getValue() { return v; }

        protected void setKey(K key) { k = key; }
        public V setValue(V value) {
            V old = v;
            v = value;
            return old;
        }

        public String toString() { return "<" + k + ", " + v + ">"; }
    }

    private class KeyIterator implements Iterator<K> {
        private Iterator<Entry<K,V>> entries = entrySet().iterator();
        public boolean hasNext() { return entries.hasNext(); }
        public K next() { return entries.next().getKey(); }
        public void remove() { throw new UnsupportedOperationException("remove not supported"); }
    }

    private class KeyIterable implements Iterable<K> {
        public Iterator<K> iterator() { return new KeyIterator(); }
    }

    public Iterable<K> keySet() { return new KeyIterable(); }

    private class ValueIterator implements Iterator<V> {
        private Iterator<Entry<K,V>> entries = entrySet().iterator();
        public boolean hasNext() { return entries.hasNext(); }
        public V next() { return entries.next().getValue(); }
        public void remove() { throw new UnsupportedOperationException("remove not supported"); }
    }

    private class ValueIterable implements Iterable<V> {
        public Iterator<V> iterator() { return new ValueIterator(); }
    }

    public Iterable<V> values() { return new ValueIterable(); }
}

abstract class AbstractSortedMap<K,V>
    extends AbstractMap<K,V> implements SortedMap<K,V> {

    private Comparator<K> comp;

    protected AbstractSortedMap(Comparator<K> c) {
        comp = c;
    }

    protected AbstractSortedMap() {
        this(new DefaultComparator<K>());
    }

    protected int compare(Entry<K,V> a, Entry<K,V> b) {
        return comp.compare(a.getKey(), b.getKey());
    }

    protected int compare(K a, Entry<K,V> b) {
        return comp.compare(a, b.getKey());
    }

    protected int compare(Entry<K,V> a, K b) {
        return comp.compare(a.getKey(), b);
    }

    protected int compare(K a, K b) {
        return comp.compare(a, b);
    }

    protected boolean checkKey(K key) throws IllegalArgumentException {
        try {
            return (comp.compare(key,key)==0);
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("Incompatible key");
        }
    }
}

class DefaultComparator<E> implements Comparator<E> {
    @SuppressWarnings({"unchecked"})
    public int compare(E a, E b) throws ClassCastException {
        return ((Comparable<E>) a).compareTo(b);
    }
}

public class SortedMapList<E> implements List<E>
{
    private SortedMap<Integer,E> container;

    public SortedMapList()
    {
        container = new SortedTableMap<>();
    }

    // O(1), relies on SML.size() which is O(1)
    public int size() {
        return container.size();
    }

    // O(1), relies on size() which is O(1)
    public boolean isEmpty() {
        return size() == 0;
    }


    public E get(int i) throws IndexOutOfBoundsException {
        checkIndex(i);
        return container.get(i);
    }

    public E set(int i, E e) throws IndexOutOfBoundsException {
        checkIndex(i);
        return container.put(i, e);
    }

    public void add(int i, E e) throws IndexOutOfBoundsException {
        checkIndex(i);
        if (get(i) != null)
            for(Entry<Integer, E> entry: container.subMap(i, size()-1))
                ((AbstractMap.MapEntry)entry).setKey(entry.getKey()+1); // Doesn't work on lazy iterator

        container.put(i, e);
    }

    public E remove(int i) throws IndexOutOfBoundsException {
        checkIndex(i);
        E returnValue = container.remove(i);
        if (get(i) != null)
            for(Entry<Integer, E> entry: container.subMap(i, size()-1))
                ((AbstractMap.MapEntry)entry).setKey(entry.getKey()-1);
        return returnValue;
    }

    public Iterator<E> iterator() {
        return container.values().iterator();
    }

    public void checkIndex(int i) throws IndexOutOfBoundsException
    {
        if (i < 0 || i > size()) throw new IndexOutOfBoundsException();
    }

    public static void main(String[] args)
    {
        SortedMapList<Character> sml = new SortedMapList<>();

        sml.add(0,'a');
        sml.add(1,'b');
        sml.add(2,'c'); // removed later
        sml.add(3,'d');
        sml.add(1,'z'); // set e

        sml.remove(3);

        sml.set(1,'e');

        for(char c: sml)
            System.out.println(c);

        // It works! (It worked until it  didn't)
    }
}
