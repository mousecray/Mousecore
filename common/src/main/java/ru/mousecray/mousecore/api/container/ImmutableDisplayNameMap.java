/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.container;

import ru.mousecray.mousecore.api.DisplayName;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

//TODO: Replace array iteration by iterator like HashMap
@SuppressWarnings({ "ClassEscapesDefinedScope" })
@ParametersAreNonnullByDefault
public class ImmutableDisplayNameMap<V> implements Iterable<ImmutableDisplayNameMap.Entry<V>> {
    static final        int  DEFAULT_INITIAL_CAPACITY = 16;
    @Nullable protected Node head, tail;
    protected           Node[]                   table;
    protected           int                      size;
    protected           int                      emptyCount;
    @Nullable protected DisplayNameCollection<V> values;

    @SuppressWarnings("CopyConstructorMissesField")
    public ImmutableDisplayNameMap(ImmutableDisplayNameMap<V> values) {
        head = values.head;
        tail = values.tail;
        size = values.size;
        emptyCount = values.emptyCount;
        table = new Node[values.table.length];
        System.arraycopy(values.table, 0, table, 0, table.length);
    }

    public ImmutableDisplayNameMap(DisplayName name, V value) {
        this(1);
        put(name, value);
    }

    public ImmutableDisplayNameMap(int initialCapacity) { table = new Node[initialCapacity]; }
    public ImmutableDisplayNameMap()                    { this(DEFAULT_INITIAL_CAPACITY); }

    public boolean isEmpty()                            { return size <= 0; }
    protected boolean equalsInternalName(Node node, DisplayName name) {
        return node.key.getInternalName().equals(name.getInternalName());
    }
    protected boolean equalsDisplayName(Node node, DisplayName name) {
        return node.key.getDisplayName().equalsIgnoreCase(name.getDisplayName());
    }
    @Nullable @SuppressWarnings("unchecked") public V getFirst() { return head != null ? (V) head.value : null; }
    @Nullable @SuppressWarnings("unchecked") public V getLast()  { return tail != null ? (V) tail.value : null; }

    private static final class Node<NodeV> {
        private @Nullable Node prev, next;
        private @Nonnull DisplayName key;
        private          NodeV       value;

        public Node(DisplayName key, NodeV value) {
            this.key = key;
            this.value = value;
        }
    }

    public static final class Entry<EntryV> {
        private final @Nonnull DisplayName key;
        private final          EntryV      value;

        public Entry(DisplayName key, EntryV value) {
            this.key = key;
            this.value = value;
        }

        @Nonnull public DisplayName getKey() { return key; }
        public EntryV getValue()             { return value; }
    }

    @Nullable @SuppressWarnings({ "UnusedReturnValue" })
    public Entry<V> put(@Nonnull DisplayName key, V value) {
        throw new UnsupportedOperationException();
    }

    @Nullable @SuppressWarnings({ "UnusedReturnValue" })
    public Entry<V> remove(@Nonnull DisplayName key) {
        throw new UnsupportedOperationException();
    }

    @Nullable @SuppressWarnings({ "unchecked", "ForLoopReplaceableByForEach" })
    public V get(DisplayName name) {
        for (int i = 0, tableLength = table.length; i < tableLength; ++i) {
            Node<V> node = table[i];
            if (node != null && node.key.equals(name)) return node.value;
        }

        return null;
    }

    @Nullable @SuppressWarnings({ "unchecked", "ForLoopReplaceableByForEach" })
    public V getByInternalName(String name) {
        for (int i = 0, tableLength = table.length; i < tableLength; ++i) {
            Node<V> node = table[i];
            if (node != null && node.key.getInternalName().equals(name)) return node.value;
        }

        return null;
    }

    @Nullable @SuppressWarnings({ "unchecked", "ForLoopReplaceableByForEach" })
    public V getByDisplayName(String name) {
        for (int i = 0, tableLength = table.length; i < tableLength; ++i) {
            Node<V> node = table[i];
            if (node != null && node.key.getDisplayName().equalsIgnoreCase(name)) return node.value;
        }

        return null;
    }

    @SuppressWarnings({ "unchecked", "ForLoopReplaceableByForEach" })
    public boolean contains(DisplayName name) {
        for (int i = 0, tableLength = table.length; i < tableLength; ++i) {
            Node<V> node = table[i];
            if (node != null && node.key.equals(name)) return true;
        }

        return false;
    }

    @SuppressWarnings({ "unchecked", "ForLoopReplaceableByForEach" })
    public boolean containsByInternalName(String name) {
        for (int i = 0, tableLength = table.length; i < tableLength; ++i) {
            Node<V> node = table[i];
            if (node != null && node.key.getInternalName().equals(name)) return true;
        }

        return false;
    }

    @SuppressWarnings({ "unchecked", "ForLoopReplaceableByForEach" })
    public boolean containsByDisplayName(String name) {
        for (int i = 0, tableLength = table.length; i < tableLength; ++i) {
            Node<V> node = table[i];
            if (node != null && node.key.getDisplayName().equalsIgnoreCase(name)) return true;
        }

        return false;
    }

    public DisplayNameCollection<V> values() {
        DisplayNameCollection<V> vs = values;
        if (vs == null) {
            vs = new ImmutableValues();
            values = vs;
        }
        return vs;
    }

    @Nonnull @Override @SuppressWarnings("ClassEscapesDefinedScope")
    public ImmutableEntryIterator iterator() { return new ImmutableEntryIterator(); }

    @Override
    public Spliterator<Entry<V>> spliterator() {
        return Spliterators.spliterator(iterator(), size, Spliterator.SIZED | Spliterator.ORDERED | Spliterator.DISTINCT);
    }

    @Override @SuppressWarnings({ "unchecked", "ConstantConditions" })
    public void forEach(@Nonnull Consumer<? super Entry<V>> action) {
        if (action == null) throw new NullPointerException();
        int mc = size;
        for (Node<V> e = head; e != null; e = e.next) action.accept(new Entry<>(e.key, e.value));
        if (size != mc) throw new ConcurrentModificationException();
    }

    @SuppressWarnings({ "unchecked", "ConstantConditions" })
    public void forEach(@Nonnull BiConsumer<DisplayName, ? super V> action) {
        if (action == null) throw new NullPointerException();
        int mc = size;
        for (Node<V> e = head; e != null; e = e.next) action.accept(e.key, e.value);
        if (size != mc) throw new ConcurrentModificationException();
    }

    public int getSize() { return size; }

    @SuppressWarnings("unchecked")
    abstract class ImmutableMapIterator {
        Node<V> next;
        @Nullable Node<V> current;
        int expectedSize;

        ImmutableMapIterator() {
            next = head;
            expectedSize = size;
            current = null;
        }

        public final boolean hasNext() { return next != null; }

        @Nonnull final Entry<V> nextNode() {
            Node<V> e = next;
            if (size != expectedSize) throw new ConcurrentModificationException();
            if (e == null) throw new NoSuchElementException();
            current = e;
            next = e.next;
            return new Entry<>(e.key, e.value);
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    class ImmutableEntryIterator extends ImmutableMapIterator implements Iterator<Entry<V>> {
        @Override @Nonnull public Entry<V> next() { return nextNode(); }
    }

    class ImmutableValueIterator extends ImmutableMapIterator implements Iterator<V> {
        @Override @Nonnull public V next() { return nextNode().getValue(); }
    }

    final class EntryIterator extends ImmutableEntryIterator {
        @Override
        public void remove() {
            Node<V> p = current;
            if (p == null) throw new IllegalStateException();
            if (size != expectedSize) throw new ConcurrentModificationException();
            current = null;
            DisplayName key = p.key;
            ImmutableDisplayNameMap.this.remove(key);
            expectedSize = size;
        }
    }

    final class ValueIterator extends ImmutableValueIterator {
        @Override
        public void remove() {
            Node<V> p = current;
            if (p == null) throw new IllegalStateException();
            if (size != expectedSize) throw new ConcurrentModificationException();
            current = null;
            DisplayName key = p.key;
            ImmutableDisplayNameMap.this.remove(key);
            expectedSize = size;
        }
    }

    public interface DisplayNameCollection<V1> extends Iterable<V1> {
        int size();

        @Nonnull @Override Iterator<V1> iterator();

        @Override
        default Spliterator<V1> spliterator() {
            return Spliterators.spliterator(iterator(), size(), Spliterator.SIZED | Spliterator.ORDERED);
        }

        <O> ArrayList<O> toList();

        default Stream<V1> stream() { return StreamSupport.stream(spliterator(), false); }

        @Nullable
        V1 findAny(@Nullable Predicate<? super V1> action);
    }

    class ImmutableValues implements DisplayNameCollection<V> {
        @Override public final int size()                      { return size; }
        @Override @Nonnull public final Iterator<V> iterator() { return new ImmutableValueIterator(); }

        @Override @SuppressWarnings({ "ConstantConditions", "unchecked" })
        public final void forEach(Consumer<? super V> action) {
            if (action == null) throw new NullPointerException();
            int mc = size;
            for (Node<V> e = head; e != null; e = e.next) action.accept(e.value);
            if (size != mc) throw new ConcurrentModificationException();
        }

        @Override @SuppressWarnings({ "ConstantConditions", "unchecked" }) @Nullable
        public final V findAny(@Nullable Predicate<? super V> action) {
            int mc = size;
            for (Node<V> e = head; e != null; e = e.next) {
                if (action == null || action.test(e.value)) return e.value;
            }
            if (size != mc) throw new ConcurrentModificationException();
            return null;
        }

        @Override @SuppressWarnings("unchecked")
        public <O> ArrayList<O> toList() {
            ArrayList<O> list = new ArrayList<>();
            for (V v : this) list.add((O) v);
            return list;
        }
    }

    class Values implements DisplayNameCollection<V> {
        @Override public final int size()                      { return size; }
        @Override @Nonnull public final Iterator<V> iterator() { return new ValueIterator(); }

        @Override @SuppressWarnings({ "ConstantConditions", "unchecked" })
        public final void forEach(Consumer<? super V> action) {
            if (action == null) throw new NullPointerException();
            int mc = size;
            for (Node<V> e = head; e != null; e = e.next) action.accept(e.value);
            if (size != mc) throw new ConcurrentModificationException();
        }

        @Override @SuppressWarnings({ "ConstantConditions", "unchecked" }) @Nullable
        public final V findAny(@Nullable Predicate<? super V> action) {
            int mc = size;
            for (Node<V> e = head; e != null; e = e.next) {
                if (action == null || action.test(e.value)) return e.value;
            }
            if (size != mc) throw new ConcurrentModificationException();
            return null;
        }

        @Override @SuppressWarnings("unchecked")
        public <O> ArrayList<O> toList() {
            ArrayList<O> list = new ArrayList<>();
            for (V v : this) list.add((O) v);
            return list;
        }
    }

    public static class Mutable<T> extends ImmutableDisplayNameMap<T> {
        public Mutable(ImmutableDisplayNameMap<T> values) { super(values); }
        public Mutable(DisplayName name, T value)         { super(name, value); }
        public Mutable(int initialCapacity)               { super(initialCapacity); }
        public Mutable()                                  { super(); }

        private void growTable() {
            Node<?>[] tempTable = table;
            table = new Node[size + DEFAULT_INITIAL_CAPACITY];
            System.arraycopy(tempTable, 0, table, 0, size);
        }

        @Override @Nullable @SuppressWarnings({ "unchecked", "UnusedReturnValue" })
        public Entry<T> put(@Nonnull DisplayName key, T value) {
            Objects.requireNonNull(key);
            if (size > 0) {
                int[]   empty      = new int[emptyCount];
                Node<T> targetNode = null;
                for (int i = 0, tableLength = table.length, emptyI = 0; i < tableLength; ++i) {
                    Node node = table[i];
                    if (node != null) {
                        if (node.key.equals(key) || equalsInternalName(node, key) || equalsDisplayName(node, key)) targetNode = node;
                    } else if (emptyI < empty.length) empty[emptyI++] = i;
                }

                if (targetNode != null) return new Entry<>(targetNode.key = key, targetNode.value = value);

                Node<T> node = new Node<>(key, value);
                node.prev = tail;
                assert tail != null;
                tail.next = node;
                tail = node;

                if (emptyCount > 0) {
                    table[empty[0]] = node;
                    --emptyCount;
                } else {
                    if (size >= table.length) growTable();
                    table[size] = node;
                }

                ++size;
            } else table[size++] = head = tail = new Node<>(key, value);

            return null;
        }

        @Override @Nullable @SuppressWarnings({ "unchecked", "UnusedReturnValue" })
        public Entry<T> remove(@Nonnull DisplayName key) {
            Objects.requireNonNull(key);
            if (size > 0) {
                int targetIndex = -1;
                for (int i = 0, tableLength = table.length; i < tableLength; ++i) {
                    Node node = table[i];
                    if (node.key.equals(key) || equalsInternalName(node, key) || equalsDisplayName(node, key)) {
                        targetIndex = i;
                        break;
                    }
                }
                if (targetIndex >= 0) {
                    Node<T> node = table[targetIndex];
                    table[targetIndex] = null;

                    Node prevNode = null;
                    for (int i = targetIndex - 1; i >= 0; --i) {
                        prevNode = table[i];
                        if (prevNode != null) break;
                    }

                    Node nextNode = null;
                    for (int i = targetIndex + 1; i < table.length; ++i) {
                        nextNode = table[i];
                        if (nextNode != null) break;
                    }

                    if (prevNode != null) prevNode.next = nextNode;
                    else head = nextNode;

                    if (nextNode != null) nextNode.prev = prevNode;
                    else tail = prevNode;

                    node.prev = node.next = null;

                    ++emptyCount;
                    --size;
                    trimIfLarge();
                    return new Entry<>(node.key, node.value);
                }
            }
            return null;
        }

        @SuppressWarnings("ForLoopReplaceableByForEach")
        private void trimIfLarge() {
            if (emptyCount > DEFAULT_INITIAL_CAPACITY) {
                int newSize = table.length - emptyCount + (emptyCount % DEFAULT_INITIAL_CAPACITY);
                if (newSize < DEFAULT_INITIAL_CAPACITY) newSize = DEFAULT_INITIAL_CAPACITY;

                Node[] table    = new Node[newSize];
                int    nonNullI = 0;

                for (int i = 0, tableLength = this.table.length; i < tableLength; i++) {
                    Node node = this.table[i];
                    if (node != null) table[nonNullI++] = node;
                }

                this.table = table;
                emptyCount = 0;
            }
        }

        @Override public DisplayNameCollection<T> values() {
            DisplayNameCollection<T> vs = values;
            if (vs == null) {
                vs = new Values();
                values = vs;
            }
            return vs;
        }

        @Nonnull @Override @SuppressWarnings("ClassEscapesDefinedScope")
        public ImmutableEntryIterator iterator() { return new EntryIterator(); }

        @Override
        public Spliterator<Entry<T>> spliterator() {
            return Spliterators.spliterator(iterator(), size, Spliterator.SIZED | Spliterator.ORDERED | Spliterator.DISTINCT);
        }
    }
}