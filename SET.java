/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.SortedSet;
import java.util.TreeSet;
/**
 *
 * @author asus
 */
public class SET<Key extends Comparable<Key>> implements Iterable<Key> {
    private TreeSet<Key> set;

    /**
     * Initializes an empty set.
     */
    public SET() {
        set = new TreeSet<Key>();
    }

    /**
     * Adds the key to the set if it is not already present.
     * @param key the key to add
     * @throws NullPointerException if <tt>key</tt> is <tt>null</tt>
     */
    public void add(Key key) {
        if (key == null) throw new NullPointerException("called add() with a null key");
        set.add(key);
    }


    /**
     * Does this symbol table contain the given key?
     * @param key the key
     * @return <tt>true</tt> if this symbol table contains <tt>key</tt> and
     *     <tt>false</tt> otherwise
     * @throws NullPointerException if <tt>key</tt> is <tt>null</tt>
     */
    public boolean contains(Key key) {
        if (key == null) throw new NullPointerException("called contains() with a null key");
        return set.contains(key);
    }

    /**
     * Removes the key from the set if the key is present.
     * @param key the key
     * @throws NullPointerException if <tt>key</tt> is <tt>null</tt>
     */
    public void delete(Key key) {
        if (key == null) throw new NullPointerException("called delete() with a null key");
        set.remove(key);
    }

    /**
     * Returns the number of key-value pairs in this symbol table.
     * @return the number of key-value pairs in this symbol table
     */
    public int size() {
        return set.size();
    }

    /**
     * Is this symbol table empty?
     * @return <tt>true</tt> if this symbol table is empty and <tt>false</tt> otherwise
     */
    public boolean isEmpty() {
        return size() == 0;
    }
 
    /**
     * Returns all of the keys in the symbol table as an iterator.
     * To iterate over all of the keys in a set named <tt>set</tt>, use the
     * foreach notation: <tt>for (Key key : set)</tt>.
     * @return an iterator to all of the keys in the set
     */
    public Iterator<Key> iterator() {
        return set.iterator();
    }

    /**
     * Returns the largest key in the set.
     * @return the largest key in the set
     * @throws NoSuchElementException if the set is empty
     */
    public Key max() {
        if (isEmpty()) throw new NoSuchElementException("called max() with empty set");
        return set.last();
    }

    /**
     * Returns the smallest key in the set.
     * @return the smallest key in the set
     * @throws NoSuchElementException if the set is empty
     */
    public Key min() {
        if (isEmpty()) throw new NoSuchElementException("called min() with empty set");
        return set.first();
    }


    /**
     * Returns the smallest key in the set greater than or equal to <tt>key</tt>.
     * @return the smallest key in the set greater than or equal to <tt>key</tt>
     * @param key the key
     * @throws NoSuchElementException if the set is empty
     * @throws NullPointerException if <tt>key</tt> is <tt>null</tt>
     */
    public Key ceil(Key key) {
        if (key == null) throw new NullPointerException("called ceil() with a null key");
        SortedSet<Key> tail = set.tailSet(key);
        if (tail.isEmpty()) throw new NoSuchElementException();
        return tail.first();
    }

    /**
     * Returns the largest key in the set less than or equal to <tt>key</tt>.
     * @return the largest key in the set table less than or equal to <tt>key</tt>
     * @param key the key
     * @throws NoSuchElementException if the set is empty
     * @throws NullPointerException if <tt>key</tt> is <tt>null</tt>
     */
    public Key floor(Key key) {
        if (key == null) throw new NullPointerException("called floor() with a null key");
        // headSet() does not include key if present (!)
        if (set.contains(key)) return key;
        SortedSet<Key> head = set.headSet(key);
        if (head.isEmpty()) throw new NoSuchElementException();
        return head.last();
    }

    /**
     * Returns the union of this set and that set.
     * @param that the other set
     * @return the union of this set and that set
     * @throws NullPointerException if <tt>that</tt> is <tt>null</tt>
     */
    public SET<Key> union(SET<Key> that) {
        if (that == null) throw new NullPointerException("called union() with a null argument");
        SET<Key> c = new SET<Key>();
        for (Key x : this)
            c.add(x);
        for (Key x : that)
            c.add(x);
        return c;
    }

    /**
     * Returns the intersection of this set and that set.
     * @param that the other set
     * @return the intersection of this set and that set
     * @throws NullPointerException if <tt>that</tt> is <tt>null</tt>
     */
    public SET<Key> intersects(SET<Key> that) {
        if (that == null) throw new NullPointerException("called intersects() with a null argument");
        SET<Key> c = new SET<Key>();
        if (this.size() < that.size()) {
            for (Key x : this) {
                if (that.contains(x)) c.add(x);
            }
        }
        else {
            for (Key x : that) {
                if (this.contains(x)) c.add(x);
            }
        }
        return c;
    }

    /**
     * Does this set equal <tt>y</tt>?
     * Note that this method declares two empty sets to be equal
     * even if they are parameterized by different generic types.
     * This is consistent with the behavior of <tt>equals()</tt> 
     * within Java's Collections framework.
     * @param y the other set
     * @return <tt>true</tt> if the two sets are equal; <tt>false</tt> otherwise
     */
    public boolean equals(Object y) {
        if (y == this) return true;
        if (y == null) return false;
        if (y.getClass() != this.getClass()) return false;
        SET<Key> that = (SET<Key>) y;
        if (this.size() != that.size()) return false;
        try {
            for (Key k : this)
                if (!that.contains(k)) return false;
        }
        catch (ClassCastException exception) {
            return false;
        }
        return true;
    }

    /**
     * This operation is not supported because sets are mutable.
     * @return does not return a value
     * @throws UnsupportedOperationException if called
     */
    public int hashCode() {
        throw new UnsupportedOperationException("hashCode() is not supported because sets are mutable");
    }

    /**
     * Returns a string representation of this set.
     * @return a string representation of this set, with the keys separated
     *   by single spaces
     */
    public String toString() {
        StringBuilder s = new StringBuilder();
        for (Key key : this)
            s.append(key + " ");
        return s.toString();
    }

}

