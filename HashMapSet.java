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

public class HashMapSet<E> implements Set<E> {
    private HashMap<Integer, E> container;

    public HashMapSet(){
        container = new HashMap<>();
    }

    public int size() {
        return container.size();
    }

    public boolean isEmpty() {
        return container.isEmpty();
    }

    public void add(E e) {
        container.put(size(), e);
    }

    public void remove(E e) {
        for(Map.Entry<Integer, E> cell: container.entrySet())
            if (cell.getValue() == e)
            {
                container.remove(cell.getKey());
                break;
            }
    }

    public boolean contains(E e) {
        for(Map.Entry<Integer, E> cell: container.entrySet())
            if (cell.getValue() == e)
                return true;

        return false;
    }

    public void addAll(Set<E> T) {
        for(E val: T)
            if (!contains(val))
                add(val);
    }

    public void retainAll(Set<E> T) {
        for(E val: T)
            if (!contains(val))
                remove(val);
    }

    public void removeAll(Set<E> T) {
        for(E val: T)
            remove(val);
    }

    public Iterator<E> iterator() {
        return new ElementsIterator<>();
    }

    private class ElementsIterator<E> implements Iterator<E>
    {
        int j = 0;

        public boolean hasNext() {
            return container.get(j+1) != null;
        }

        public E next() {
            return (E) container.get(j++);
        }
    }


    public static void main(String[] args) {

        HashMapSet<Integer> set1 = new HashMapSet<>();
        HashMapSet<Integer> set2 = new HashMapSet<>();
        HashMapSet<Integer> set3 = new HashMapSet<>();
        HashMapSet<Integer> set4 = new HashMapSet<>();

        for(int i=0; i<20; i++){
            set1.add(i);
            set2.add(i*2);
            set3.add(i*3);
            set4.add(i);
        }

        for(Integer i: set1) System.out.print(i+" ");
        System.out.println();

        for(Integer i: set2) System.out.print(i+" ");
        System.out.println();

        for(Integer i: set3) System.out.print(i+" ");
        System.out.println();

        set1.addAll(set4);

        for(Integer i: set1) System.out.print(i+" ");
        System.out.println();

        set1.retainAll(set2);

        for(Integer i: set1) System.out.print(i+" ");
        System.out.println();

        set1.addAll(set2);

        for(Integer i: set1) System.out.print(i+" ");
        System.out.println();

        set1.removeAll(set3);

        for(Integer i: set1)
            System.out.print(i+" ");
    }
}
