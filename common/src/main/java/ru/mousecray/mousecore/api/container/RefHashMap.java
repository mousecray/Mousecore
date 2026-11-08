/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.container;

import gnu.trove.THashMap;
import gnu.trove.TObjectHashingStrategy;
import ru.mousecray.mousecore.api.error.IncorrectOperationException;

import javax.annotation.Nonnull;
import java.lang.ref.ReferenceQueue;
import java.util.*;

@SuppressWarnings("unchecked")
abstract class RefHashMap<K, V> extends AbstractMap<K, V> implements Map<K, V> {
    private final          MyMap                      myMap;
    private final          ReferenceQueue<K>          myReferenceQueue  = new ReferenceQueue<>();
    private final          HardKey                    myHardKeyInstance = new HardKey(); // "singleton"
    @Nonnull private final HashingStrategy<? super K> myStrategy        = HashingStrategy.canonical();
    private                Set<Entry<K, V>>           entrySet;
    private                boolean                    processingQueue;

    RefHashMap(int initialCapacity) { myMap = new MyMap(initialCapacity); }

    static <K> boolean keyEqual(K k1, K k2, HashingStrategy<? super K> strategy) {
        return k1 == k2 || strategy.equals(k1, k2);
    }

    private final class MyMap extends THashMap<Key<K>, V> {
        private MyMap(int initialCapacity) {
            super(initialCapacity, 0.8f, new TObjectHashingStrategy<Key<K>>() {
                @Override public int computeHashCode(Key<K> key)      { return key.hashCode(); }
                @Override public boolean equals(Key<K> o1, Key<K> o2) { return o1 == o2 || keyEqual(o1.get(), o2.get(), myStrategy); }
            });
        }

        @Override public void compact()   { if (!processingQueue) super.compact(); }
        private void compactIfNecessary() { if (_deadkeys > _size && capacity() > 42) compact(); }

        @Override
        protected void rehash(int newCapacity) {
            int      oldCapacity = _set.length;
            Object[] oldKeys     = _set;
            V[]      oldVals     = _values;

            _set = new Object[newCapacity];
            _values = (V[]) new Object[newCapacity];

            for (int i = oldCapacity; i-- > 0; ) {
                Object o = oldKeys[i];
                if (o == null || o == REMOVED) continue;
                Key<K> k   = (Key<K>) o;
                K      key = k.get();
                if (key == null) continue;
                int index = insertionIndex(k);
                if (index < 0) {
                    throwObjectContractViolation(_set[-index - 1], o);
                    if (key == _set) throw new AssertionError();
                }
                _set[index] = o;
                _values[index] = oldVals[i];
            }
        }
    }

    @FunctionalInterface
    interface Key<T> {
        T get();
    }

    @Nonnull
    protected abstract <T> Key<T> createKey(@Nonnull T k, @Nonnull HashingStrategy<? super T> strategy, @Nonnull ReferenceQueue<? super T> q);

    private class HardKey implements Key<K> {
        private K   myObject;
        private int myHash;

        @Override public K get() { return myObject; }

        private void set(@Nonnull K object) {
            myObject = object;
            myHash = myStrategy.hashCode(object);
        }

        private void clear() { myObject = null; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Key)) return false;
            return keyEqual(myObject, ((Key<K>) o).get(), myStrategy);
        }

        @Override public int hashCode() { return myHash; }
    }

    void processQueue() {
        try {
            processingQueue = true;
            Key<K> wk;
            while ((wk = (Key<K>) myReferenceQueue.poll()) != null) removeKey(wk);
        } finally { processingQueue = false; }
        myMap.compactIfNecessary();
    }

    void removeKey(@Nonnull Key<K> key)        { myMap.remove(key); }
    @Nonnull Key<K> createKey(@Nonnull K key)  { return createKey(key, myStrategy, myReferenceQueue); }
    V putKey(@Nonnull Key<K> weakKey, V value) { return myMap.put(weakKey, value); }
    @Override public int size()                { return entrySet().size(); }
    @Override public boolean isEmpty()         { return myMap.isEmpty() || entrySet().isEmpty(); }

    @Override
    public boolean containsKey(Object key) {
        if (key == null) return false;
        myHardKeyInstance.set((K) key);
        try { return myMap.containsKey(myHardKeyInstance); } finally { myHardKeyInstance.clear(); }
    }

    @Override
    public boolean containsValue(Object value) {
        throw new IncorrectOperationException("getValue() makes no sense for weak/soft map because GC can clear the key any moment now");
    }

    @Override
    public V get(Object key) {
        if (key == null) return null;
        myHardKeyInstance.set((K) key);
        try { return myMap.get(myHardKeyInstance); } finally { myHardKeyInstance.clear(); }
    }

    @Override
    public V put(@Nonnull K key, V value) {
        processQueue();
        return putKey(createKey(key), value);
    }

    @Override
    public V remove(@Nonnull Object key) {
        processQueue();
        myHardKeyInstance.set((K) key);
        try { return myMap.remove(myHardKeyInstance); } finally { myHardKeyInstance.clear(); }
    }

    @Override
    public void clear() {
        processQueue();
        myMap.clear();
    }

    private static final class MyEntry<K, V> implements Entry<K, V> {
        private final          Entry<?, V>                ent;
        private final          K                          key;
        private final          int                        myKeyHashCode;
        @Nonnull private final HashingStrategy<? super K> myStrategy;

        private MyEntry(@Nonnull Entry<?, V> ent, @Nonnull K key, int keyHashCode, @Nonnull HashingStrategy<? super K> strategy) {
            this.ent = ent;
            this.key = key;
            myKeyHashCode = keyHashCode;
            myStrategy = strategy;
        }

        @Override public K getKey()          { return key; }
        @Override public V getValue()        { return ent.getValue(); }
        @Override public V setValue(V value) { return ent.setValue(value); }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Entry)) return false;
            Entry<K, V> e = (Entry<K, V>) o;
            return keyEqual(key, e.getKey(), myStrategy) && Objects.equals(getValue(), e.getValue());
        }

        @Override
        public int hashCode() {
            V v;
            return myKeyHashCode ^ ((v = getValue()) == null ? 0 : v.hashCode());
        }
    }

    private class EntrySet extends AbstractSet<Entry<K, V>> {
        private final Set<Entry<Key<K>, V>> hashEntrySet = myMap.entrySet();

        @Nonnull
        @Override
        public Iterator<Entry<K, V>> iterator() {
            return new Iterator<Entry<K, V>>() {
                private final Iterator<Entry<Key<K>, V>> hashIterator = hashEntrySet.iterator();
                private       MyEntry<K, V>              next;

                @Override
                public boolean hasNext() {
                    while (hashIterator.hasNext()) {
                        Entry<Key<K>, V> ent = hashIterator.next();
                        Key<K>           wk  = ent.getKey();
                        K                k;
                        if ((k = wk.get()) == null) continue;
                        next = new MyEntry<>(ent, k, wk.hashCode(), myStrategy);
                        return true;
                    }
                    return false;
                }

                @Override
                public Entry<K, V> next() {
                    if (next == null && !hasNext()) throw new NoSuchElementException();
                    Entry<K, V> e = next;
                    next = null;
                    return e;
                }

                @Override public void remove() { hashIterator.remove(); }
            };
        }

        @Override public boolean isEmpty() { return !iterator().hasNext(); }

        @Override
        public int size() {
            int j = 0;
            for (Iterator<Entry<K, V>> i = iterator(); i.hasNext(); i.next()) j++;
            return j;
        }

        @Override
        public boolean remove(Object o) {
            processQueue();
            if (!(o instanceof Entry)) return false;
            Entry<K, V> e   = (Entry<K, V>) o;
            V           ev  = e.getValue();
            HardKey     key = myHardKeyInstance;
            boolean     toRemove;
            try {
                key.set(e.getKey());
                V hv = myMap.get(key);
                toRemove = hv == null ? ev == null && myMap.containsKey(key) : hv.equals(ev);
                if (toRemove) myMap.remove(key);
            } finally { key.clear(); }
            return toRemove;
        }

        @Override
        public int hashCode() {
            int h = 0;
            for (Entry<Key<K>, V> entry : hashEntrySet) {
                Key<K> wk = entry.getKey();
                if (wk == null) continue;
                Object v;
                h += wk.hashCode() ^ ((v = entry.getValue()) == null ? 0 : v.hashCode());
            }
            return h;
        }
    }


    @Nonnull
    @Override
    public Set<Entry<K, V>> entrySet() {
        Set<Entry<K, V>> es = entrySet;
        if (es == null) entrySet = es = new EntrySet();
        return es;
    }
}