import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;

class SortedTableMap<K,V> extends AbstractSortedMap<K,V> {

    private ArrayList<MapEntry<K,V>> table = new ArrayList<>();

    public SortedTableMap() { super(); }

    public SortedTableMap(Comparator<K> comp) { super(comp); }

    private int findIndex(K key, int low, int high) {
        if (high < low) return high + 1;
        int mid = (low + high) / 2;
        int comp = compare(key, table.get(mid));
        if (comp == 0)
            return mid;
        else if (comp < 0)
            return findIndex(key, low, mid - 1);
        else
            return findIndex(key, mid + 1, high);
    }

    private int findIndex(K key) { return findIndex(key, 0, table.size() - 1); }

    public int size() { return table.size(); }

    public V get(K key) throws IllegalArgumentException {
        checkKey(key);
        int j = findIndex(key);
        if (j == size() || compare(key, table.get(j)) != 0) return null;
        return table.get(j).getValue();
    }

    public V put(K key, V value) throws IllegalArgumentException {
        checkKey(key);
        int j = findIndex(key);
        if (j < size() && compare(key, table.get(j)) == 0)
            return table.get(j).setValue(value);
        table.add(j, new MapEntry<K,V>(key,value));
        return null;
    }

    public V remove(K key) throws IllegalArgumentException {
        checkKey(key);
        int j = findIndex(key);
        if (j == size() || compare(key, table.get(j)) != 0) return null;
        return table.remove(j).getValue();
    }

    private Entry<K,V> safeEntry(int j) {
        if (j < 0 || j >= table.size()) return null;
        return table.get(j);
    }

    public Entry<K,V> firstEntry() { return safeEntry(0); }

    public Entry<K,V> lastEntry() { return safeEntry(table.size()-1); }

    public Entry<K,V> ceilingEntry(K key) throws IllegalArgumentException {
        return safeEntry(findIndex(key));
    }

    public Entry<K,V> floorEntry(K key) throws IllegalArgumentException {
        int j = findIndex(key);
        if (j == size() || ! key.equals(table.get(j).getKey()))
            j--;
        return safeEntry(j);
    }

    public Entry<K,V> lowerEntry(K key) throws IllegalArgumentException {
        return safeEntry(findIndex(key) - 1);
    }

    public Entry<K,V> higherEntry(K key) throws IllegalArgumentException {
        int j = findIndex(key);
        if (j < size() && key.equals(table.get(j).getKey()))
            j++;
        return safeEntry(j);
    }

    private Iterable<Entry<K,V>> snapshot(int startIndex, K stop) {
        ArrayList<Entry<K,V>> buffer = new ArrayList<>();
        int j = startIndex;
        while (j < table.size() && (stop == null || compare(stop, table.get(j)) > 0))
            buffer.add(table.get(j++));
        return buffer;
    }

    public Iterable<Entry<K,V>> entrySet() { return new EntryIterable(); }
    public Iterable<Entry<K,V>> subMap(K fromKey, K toKey) throws IllegalArgumentException {
        return snapshot(findIndex(fromKey), toKey);
    }

    private class EntryIterable implements Iterable<Entry<K,V>>
    {
        K from;
        K to;

        public EntryIterable()
        {
            from = table.getFirst().getKey();
            to = null;
        }

        public EntryIterable(K _from, K _to)
        {
            for(Entry<K,V> entry : table)
            {
                K key = entry.getKey();
                if (key.equals(_from))
                    from = key;
                if (key.equals(_to)){
                    to = key;
                    break;
                }
            }
        }

        public Iterator<Entry<K,V>> iterator() {
            return new EntryIterator<>(from, to);
        }
    }

    private class EntryIterator<E> implements Iterator<E>
    {
        K from;
        K to;

        public EntryIterator(K _from, K _to)
        {
            from = _from;
            to = _to;
        }

        public boolean hasNext() {
            return higherEntry(from) != to;
        }

        public E next() {
            K temp = from;
            from = higherEntry(from).getKey();
            return (E) ceilingEntry(temp);
        }
    }
}
