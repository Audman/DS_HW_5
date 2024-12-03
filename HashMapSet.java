import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

interface Set<E> extends Iterable<E> {

    // Returns the number of elements in the set.
    int size();

    // Returns if the set is empty.
    boolean isEmpty();

    //Adds the element e to S (if not already present).
    void add(E e);

    //Removes the element e from S (if it is present).
    void remove(E e);

    //Returns whether e is an element of S
    boolean contains(E e);

    /**
     * Updates S to also include all elements of set T,
     * effectively replacing S by S∪T (S union T)
     */
    void addAll(Set<E> T);

    /**
     * Updates S so that it only keeps those elements that are
     * also elements of set T, effectively replacing S by S∩T (S intersection T).
     */
    void retainAll(Set<E> T);

    /**
     * Updates S by removing any of its elements that also occur
     * in set T, effectively replacing S by S−T (S subtract T).
     */
    void removeAll(Set<E> T);

    /**
     * Returns an iterator over the elements of the set in a sorted order.
     * Should be lazy. Should support methods next, hasNext and remove.
     */
    Iterator<E> iterator();
}

public class HashMapSet<E extends Comparable<E>> implements Set<E> {
    private HashMap<E, E> container;

    public HashMapSet(){
        container = new HashMap<>();
    }

    // O(1) - returns an int
    public int size() {
        return container.size();
    }

    // O(1) - returns a boolean
    public boolean isEmpty() {
        return container.isEmpty();
    }

    // Expected O(1), worst case O(n), because it's HashTable.put
    public void add(E e) {
        container.put(e, null);
    }

    // Expected O(1), worst case O(n), because it's HashTable.remove
    public void remove(E e) {
        container.remove(e);
    }

    // O(n) - traverses through all the keys and return true if a match occurs
    public boolean contains(E e) {
        for(Map.Entry<E, E> cell : container.entrySet())
            if (cell.getKey().equals(e))
                return true;

        return false;

        // If the value of a key was the key itself, this method could work in O(1)
        //return container.get(e).equals(e);
    }

    // O(n^2) - traverses through T and adds the value if it's not in the S
    // n(T.length) times n(contains), add() is O(1)
    public void addAll(Set<E> T) {
        for(E val: T)
            if (!contains(val))
                add(val);
    }

    // Same as  addAll, except that remove() is O(1)
    public void retainAll(Set<E> T) {
        for(E val: T)
            if (!contains(val))
                remove(val);
    }

    // O(n), doesn't check if a value is in the container, directly removes it
    public void removeAll(Set<E> T) {
        for(E val: T)
            remove(val);
    }

    public Iterator<E> iterator() {
        return new ElementsIterator();
    }

    private class ElementsIterator implements Iterator<E>
    {
        E min = null; // In human language "from"
        E max = null; // To

        // Lazy iterator, knows only "from" and "to" values
        // O(n) - explained inside the method
        public ElementsIterator()
        {
            // Assign the first element to min and max, to not keep them null
            // O(1) coded like this for simplicity
            for(E key : container.keySet())
            {
                min = key;
                max = key;
                break;
            }

            // Assign actual maximal and minimal values O(n)
            for(E key : container.keySet())
            {
                if(key.compareTo(min) < 0)
                    min = key;

                if(key.compareTo(max) > 0)
                    max = key;
            }
        }

        // O(1), just several comparisons
        public boolean hasNext() {
            if(max == null) return false;
            // One time min can be same as max
            if(min == max) max = null;
            return true;
        }

        // O(n) -traverses through all the container
        public E next()
        {
            // If max is nulled, we are looking for the last element
            if(max == null) return min;

            // Find the lowest value between min and max (max included)
            E secondMin = max;
            for(E walk: container.keySet())
                if (walk.compareTo(secondMin) <= 0 && walk.compareTo(min) > 0)
                    secondMin = walk;

            E returnValue = min;
            min = secondMin;
            return returnValue;
        }
    }

    public static void main(String[] args)
    {
        HashMapSet<Integer> set1 = new HashMapSet<>();
        HashMapSet<Integer> set2 = new HashMapSet<>();
        HashMapSet<Integer> set3 = new HashMapSet<>();
        HashMapSet<Integer> set4 = new HashMapSet<>();

        for(int i = 0; i < 20; i++)
        {
            set1.add(i);
            set2.add(i*2);
            set3.add(i*3);
            set4.add(i);
        }

        for(Integer i: set1)
            System.out.print(i+" ");
        System.out.println();

        for(Integer i: set2)
            System.out.print(i+" ");
        System.out.println();

        for(Integer i: set3)
            System.out.print(i+" ");
        System.out.println();

        set1.addAll(set4);

        for(Integer i: set1)
            System.out.print(i+" ");
        System.out.println();

        set1.retainAll(set2);

        for(Integer i: set1)
            System.out.print(i+" ");
        System.out.println();

        set1.addAll(set2);

        for(Integer i: set1)
            System.out.print(i+" ");
        System.out.println();

        set1.removeAll(set3);

        for(Integer i: set1)
            System.out.print(i+" ");
    }
}
