import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

interface Entry<K,V> {

    K getKey();

    V getValue();
}

interface Position<E> {

    E getElement() throws IllegalStateException;
}

interface BinaryTree<E> extends Tree<E> {

    Position<E> left(Position<E> p) throws IllegalArgumentException;

    Position<E> right(Position<E> p) throws IllegalArgumentException;

    Position<E> sibling(Position<E> p) throws IllegalArgumentException;
}

interface Queue<E> {

    int size();

    boolean isEmpty();

    void enqueue(E e);

    E first();

    E dequeue();
}

interface Tree<E> extends Iterable<E> {

    Position<E> root();

    Position<E> parent(Position<E> p) throws IllegalArgumentException;

    Iterable<Position<E>> children(Position<E> p)
        throws IllegalArgumentException;

    int numChildren(Position<E> p) throws IllegalArgumentException;

    boolean isInternal(Position<E> p) throws IllegalArgumentException;

    boolean isExternal(Position<E> p) throws IllegalArgumentException;

    boolean isRoot(Position<E> p) throws IllegalArgumentException;

    int size();

    boolean isEmpty();

    Iterator<E> iterator();

    Iterable<Position<E>> positions();
}

abstract class AbstractTree<E> implements Tree<E> {

    public boolean isInternal(Position<E> p) { return numChildren(p) > 0; }

    public boolean isExternal(Position<E> p) { return numChildren(p) == 0; }

    public boolean isRoot(Position<E> p) { return p == root(); }

    public boolean isEmpty() { return size() == 0; }

    public int depth(Position<E> p) throws IllegalArgumentException {
        if (isRoot(p))
            return 0;
        else
            return 1 + depth(parent(p));
    }

    private int heightBad() {
        int h = 0;
        for (Position<E> p : positions())
            if (isExternal(p))
                h = Math.max(h, depth(p));
        return h;
    }

    public int height(Position<E> p) throws IllegalArgumentException {
        int h = 0;
        for (Position<E> c : children(p))
            h = Math.max(h, 1 + height(c));
        return h;
    }

    private class ElementIterator implements Iterator<E> {
        Iterator<Position<E>> posIterator = positions().iterator();
        public boolean hasNext() { return posIterator.hasNext(); }
        public E next() { return posIterator.next().getElement(); }
        public void remove() { posIterator.remove(); }
    }

    public Iterator<E> iterator() { return new ElementIterator(); }

    public Iterable<Position<E>> positions() { return preorder(); }

    private void preorderSubtree(Position<E> p, List<Position<E>> snapshot) {
        snapshot.add(p);
        for (Position<E> c : children(p))
            preorderSubtree(c, snapshot);
    }

    public Iterable<Position<E>> preorder() {
        List<Position<E>> snapshot = new ArrayList<>();
        if (!isEmpty())
            preorderSubtree(root(), snapshot);
        return snapshot;
    }

    private void postorderSubtree(Position<E> p, List<Position<E>> snapshot) {
        for (Position<E> c : children(p))
            postorderSubtree(c, snapshot);
        snapshot.add(p);
    }

    public Iterable<Position<E>> postorder() {
        List<Position<E>> snapshot = new ArrayList<>();
        if (!isEmpty())
            postorderSubtree(root(), snapshot);
        return snapshot;
    }

    public Iterable<Position<E>> breadthfirst() {
        List<Position<E>> snapshot = new ArrayList<>();
        if (!isEmpty()) {
            Queue<Position<E>> fringe = new LinkedQueue<>();
            fringe.enqueue(root());
            while (!fringe.isEmpty()) {
                Position<E> p = fringe.dequeue();
                snapshot.add(p);
                for (Position<E> c : children(p))
                    fringe.enqueue(c);
            }
        }
        return snapshot;
    }
}

class LinkedQueue<E> implements Queue<E> {

    private SinglyLinkedList<E> list = new SinglyLinkedList<>();

    public LinkedQueue() { }

    public int size() { return list.size(); }

    public boolean isEmpty() { return list.isEmpty(); }

    public void enqueue(E element) { list.addLast(element); }

    public E first() { return list.first(); }

    public E dequeue() { return list.removeFirst(); }

    public String toString() {
        return list.toString();
    }
}

abstract class AbstractBinaryTree<E> extends AbstractTree<E>
    implements BinaryTree<E> {

    public Position<E> sibling(Position<E> p) {
        Position<E> parent = parent(p);
        if (parent == null) return null;
        if (p == left(parent))
            return right(parent);
        else
            return left(parent);
    }

    public int numChildren(Position<E> p) {
        int count=0;
        if (left(p) != null)
            count++;
        if (right(p) != null)
            count++;
        return count;
    }

    public Iterable<Position<E>> children(Position<E> p) {
        List<Position<E>> snapshot = new ArrayList<>(2);
        if (left(p) != null)
            snapshot.add(left(p));
        if (right(p) != null)
            snapshot.add(right(p));
        return snapshot;
    }

    private void inorderSubtree(Position<E> p, List<Position<E>> snapshot) {
        if (left(p) != null)
            inorderSubtree(left(p), snapshot);
        snapshot.add(p);
        if (right(p) != null)
            inorderSubtree(right(p), snapshot);
    }

    public Iterable<Position<E>> inorder() {
        List<Position<E>> snapshot = new ArrayList<>();
        if (!isEmpty())
            inorderSubtree(root(), snapshot);
        return snapshot;
    }

    public Iterable<Position<E>> positions() {
        return inorder();
    }
}

class SinglyLinkedList<E> implements Cloneable {

    private static class Node<E> {

        private E element;

        private Node<E> next;

        public Node(E e, Node<E> n) {
            element = e;
            next = n;
        }

        public E getElement() { return element; }

        public Node<E> getNext() { return next; }

        public void setNext(Node<E> n) { next = n; }
    }

    private Node<E> head = null;

    private Node<E> tail = null;

    private int size = 0;

    public SinglyLinkedList() { }

    public int size() { return size; }

    public boolean isEmpty() { return size == 0; }

    public E first() {
        if (isEmpty()) return null;
        return head.getElement();
    }

    public E last() {
        if (isEmpty()) return null;
        return tail.getElement();
    }

    public void addFirst(E e) {
        head = new Node<>(e, head);
        if (size == 0)
            tail = head;
        size++;
    }

    public void addLast(E e) {
        Node<E> newest = new Node<>(e, null);
        if (isEmpty())
            head = newest;
        else
            tail.setNext(newest);
        tail = newest;
        size++;
    }

    public E removeFirst() {
        if (isEmpty()) return null;
        E answer = head.getElement();
        head = head.getNext();
        size--;
        if (size == 0)
            tail = null;
        return answer;
    }

    @SuppressWarnings({"unchecked"})
    public boolean equals(Object o) {
        if (o == null) return false;
        if (getClass() != o.getClass()) return false;
        SinglyLinkedList other = (SinglyLinkedList) o;
        if (size != other.size) return false;
        Node walkA = head;
        Node walkB = other.head;
        while (walkA != null) {
            if (!walkA.getElement().equals(walkB.getElement())) return false;
            walkA = walkA.getNext();
            walkB = walkB.getNext();
        }
        return true;
    }

    @SuppressWarnings({"unchecked"})
    public SinglyLinkedList<E> clone() throws CloneNotSupportedException {

        SinglyLinkedList<E> other = (SinglyLinkedList<E>) super.clone();
        if (size > 0) {
            other.head = new Node<>(head.getElement(), null);
            Node<E> walk = head.getNext();
            Node<E> otherTail = other.head;
            while (walk != null) {
                Node<E> newest = new Node<>(walk.getElement(), null);
                otherTail.setNext(newest);
                otherTail = newest;
                walk = walk.getNext();
            }
        }
        return other;
    }

    public int hashCode() {
        int h = 0;
        for (Node walk=head; walk != null; walk = walk.getNext()) {
            h ^= walk.getElement().hashCode();
            h = (h << 5) | (h >>> 27);
        }
        return h;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("(");
        Node<E> walk = head;
        while (walk != null) {
            sb.append(walk.getElement());
            if (walk != tail)
                sb.append(", ");
            walk = walk.getNext();
        }
        sb.append(")");
        return sb.toString();
    }
}

class LinkedBinaryTree<E> extends AbstractBinaryTree<E> {

    protected static class Node<E> implements Position<E> {
        private E element;
        private Node<E> parent;
        private Node<E> left;
        private Node<E> right;

        public Node(E e, Node<E> above, Node<E> leftChild, Node<E> rightChild) {
            element = e;
            parent = above;
            left = leftChild;
            right = rightChild;
        }

        public E getElement() { return element; }
        public Node<E> getParent() { return parent; }
        public Node<E> getLeft() { return left; }
        public Node<E> getRight() { return right; }

        public void setElement(E e) { element = e; }
        public void setParent(Node<E> parentNode) { parent = parentNode; }
        public void setLeft(Node<E> leftChild) { left = leftChild; }
        public void setRight(Node<E> rightChild) { right = rightChild; }
    }

    protected Node<E> createNode(E e, Node<E> parent,
                                 Node<E> left, Node<E> right) {
        return new Node<E>(e, parent, left, right);
    }

    protected Node<E> root = null;

    private int size = 0;

    public LinkedBinaryTree() { }

    protected Node<E> validate(Position<E> p) throws IllegalArgumentException {
        if (!(p instanceof Node))
            throw new IllegalArgumentException("Not valid position type");
        Node<E> node = (Node<E>) p;
        if (node.getParent() == node)
            throw new IllegalArgumentException("p is no longer in the tree");
        return node;
    }

    public int size() {
        return size;
    }

    public Position<E> root() {
        return root;
    }

    public Position<E> parent(Position<E> p) throws IllegalArgumentException {
        Node<E> node = validate(p);
        return node.getParent();
    }

    public Position<E> left(Position<E> p) throws IllegalArgumentException {
        Node<E> node = validate(p);
        return node.getLeft();
    }

    public Position<E> right(Position<E> p) throws IllegalArgumentException {
        Node<E> node = validate(p);
        return node.getRight();
    }

    public Position<E> addRoot(E e) throws IllegalStateException {
        if (!isEmpty()) throw new IllegalStateException("Tree is not empty");
        root = createNode(e, null, null, null);
        size = 1;
        return root;
    }

    public Position<E> addLeft(Position<E> p, E e)
        throws IllegalArgumentException {
        Node<E> parent = validate(p);
        if (parent.getLeft() != null)
            throw new IllegalArgumentException("p already has a left child");
        Node<E> child = createNode(e, parent, null, null);
        parent.setLeft(child);
        size++;
        return child;
    }

    public Position<E> addRight(Position<E> p, E e)
        throws IllegalArgumentException {
        Node<E> parent = validate(p);
        if (parent.getRight() != null)
            throw new IllegalArgumentException("p already has a right child");
        Node<E> child = createNode(e, parent, null, null);
        parent.setRight(child);
        size++;
        return child;
    }

    public E set(Position<E> p, E e) throws IllegalArgumentException {
        Node<E> node = validate(p);
        E temp = node.getElement();
        node.setElement(e);
        return temp;
    }

    public void attach(Position<E> p, LinkedBinaryTree<E> t1,
                       LinkedBinaryTree<E> t2) throws IllegalArgumentException {
        Node<E> node = validate(p);
        if (isInternal(p)) throw new IllegalArgumentException("p must be a leaf");
        size += t1.size() + t2.size();
        if (!t1.isEmpty()) {
            t1.root.setParent(node);
            node.setLeft(t1.root);
            t1.root = null;
            t1.size = 0;
        }
        if (!t2.isEmpty()) {
            t2.root.setParent(node);
            node.setRight(t2.root);
            t2.root = null;
            t2.size = 0;
        }
    }

    public E remove(Position<E> p) throws IllegalArgumentException {
        Node<E> node = validate(p);
        if (numChildren(p) == 2)
            throw new IllegalArgumentException("p has two children");
        Node<E> child = (node.getLeft() != null ? node.getLeft() : node.getRight() );
        if (child != null)
            child.setParent(node.getParent());
        if (node == root)
            root = child;
        else {
            Node<E> parent = node.getParent();
            if (node == parent.getLeft())
                parent.setLeft(child);
            else
                parent.setRight(child);
        }
        size--;
        E temp = node.getElement();
        node.setElement(null);
        node.setLeft(null);
        node.setRight(null);
        node.setParent(node);
        return temp;
    }
}

class AVLTree<K,V> extends LinkedBinaryTree<Entry<K,V>> {

    protected static class AVLNode<E> extends Node<E> {
        int height=0;
        AVLNode(E e, Node<E> parent, Node<E> leftChild, Node<E> rightChild) {
            super(e, parent, leftChild, rightChild);
        }
        public int getHeight( ) { return height; }
        public void setHeight(int value) { height = value; }
    }

    public int getHeight(Position<Entry<K,V>> p) {
        return ((AVLNode<Entry<K,V>>) p).getHeight( );
    }
    public void setHeight(Position<Entry<K,V>> p, int value) {
        ((AVLNode<Entry<K,V>>) p).setHeight(value);
    }

    protected Node<Entry<K,V>> createNode(Entry<K,V> e,
                                          Node<Entry<K,V>> parent,
                                          Node<Entry<K,V>> left,
                                          Node<Entry<K,V>> right) {
        return new AVLNode<>(e, parent, left, right);
    }

    private void relink(Node<Entry<K,V>> parent, Node<Entry<K,V>> child,
                        boolean makeLeftChild) {
        child.setParent(parent);
        if (makeLeftChild)
            parent.setLeft(child);
        else
            parent.setRight(child);
    }

    public void rotate(Position<Entry<K,V>> p) {
        Node<Entry<K,V>> x = validate(p);
        Node<Entry<K,V>> y = x.getParent( );
        Node<Entry<K,V>> z = y.getParent( );
        if (z == null) {
            root = x;
            x.setParent(null);
        } else
            relink(z, x, y == z.getLeft( ));

        if (x == y.getLeft( )) {
            relink(y, x.getRight( ), true);
            relink(x, y, false);
        } else {
            relink(y, x.getLeft( ), false);
            relink(x, y, true);
        }
    }

    public Position<Entry<K,V>> restructure(Position<Entry<K,V>> x) {
        Position<Entry<K,V>> y = parent(x);
        Position<Entry<K,V>> z = parent(y);
        if ((x == right(y)) == (y == right(z))) {
            rotate(y);
            return y;
        } else {
            rotate(x);
            rotate(x);
            return x;
        }
    }
}

/*
public class ArrayToBST<K,V>
{
    // Given an Integer array with strictly increasing numbers
    // Constructs a LBT such that the resulting bla-bla-bla
    // Has the smallest possible height.
    // No restrictions about AVL

    private static AVLTree avl;

    public ArrayToBST()
    {
        avl = new AVLTree<>();
    }

    public static <E> void constructTree (LinkedBinaryTree<E> tree , E [ ] data )
    {
        for (E i : data)
            avl.addRight(i);
    }

    private static <E> void constructTree (
        LinkedBinaryTree<E> tree,
        Position<E> parent ,
//        ??? ,
//        ??? ,
        E [ ] data )
    {

    }
}
*/

public class ArrayToBST
{
    // O(n) - these methods will be called once per element, plus once for every leaves
    public static <E> void constructTree (LinkedBinaryTree<E> tree , E [ ] data )
    {
        if (data.length == 0) return;
        int mid = (data.length-1)/2;

        Position<E> root = tree.addRoot(data[mid]);
        constructTree(tree, root, 0, mid-1, true, data);
        constructTree(tree, root, mid+1, data.length-1, false, data);
    }

    private static <E> void constructTree (LinkedBinaryTree<E> tree, Position<E> parent , int start, int end , boolean left, E [ ] data )
    {
        if(start > end) return;

        int mid = start + (end - start) / 2;

        Position<E> root;
        if(left)
            root = tree.addLeft(parent, data[mid]);
        else
            root = tree.addRight(parent, data[mid]);
        constructTree(tree, root, start, mid-1, true, data);
        constructTree(tree, root, mid+1, end, false, data);
    }

    public static void main(String[] args) {
        Integer [] array = new Integer [] {0,3,5,8, 10,50,60,75};
        LinkedBinaryTree<Integer> lbt = new LinkedBinaryTree<>();
        ArrayToBST.constructTree(lbt,array);
        for (Position<Integer> pos : lbt.inorder())
            System.out.print(pos.getElement() + " ");
    }
}
