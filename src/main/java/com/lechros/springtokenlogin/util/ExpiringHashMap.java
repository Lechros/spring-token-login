package com.lechros.springtokenlogin.util;

import lombok.Getter;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public class ExpiringHashMap<K, V> implements Map<K, V> {

    private final Map<K, V> map;

    private final Map<K, ExpiringKey> expiringKeys;

    private final DelayQueue<ExpiringKey> delayQueue;

    private final long timeoutMillis;

    private final boolean renewOnAccess;

    public ExpiringHashMap(long timeoutMillis) {
        this(timeoutMillis, false);
    }

    public ExpiringHashMap(long timeoutMillis, boolean renewOnAccess) {
        map = new ConcurrentHashMap<>();
        expiringKeys = new ConcurrentHashMap<>();
        delayQueue = new DelayQueue<>();
        this.timeoutMillis = timeoutMillis;
        this.renewOnAccess = renewOnAccess;
    }

    @Override
    public int size() {
        flush();
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        flush();
        return map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        flush();
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        flush();
        return map.containsValue(value);
    }

    @Override
    public V get(Object key) {
        flush();
        if (renewOnAccess) {
            renewKey((K) key);
        }
        return map.get(key);
    }

    @Override
    public V put(K key, V value) {
        flush();
        renewKey(key);
        return map.put(key, value);
    }

    @Override
    public V remove(Object key) {
        V removed = map.remove(key);
        expireKey(expiringKeys.remove(key));
        return removed;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        map.clear();
        expiringKeys.clear();
        delayQueue.clear();
    }

    @Override
    public Set<K> keySet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<V> values() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        throw new UnsupportedOperationException();
    }

    private void flush() {
        ExpiringKey key;
        while ((key = delayQueue.poll()) != null) {
            if (key.isExpired()) continue;
            map.remove(key.getKey());
            expiringKeys.remove(key.getKey());
        }
    }

    private void expireKey(ExpiringKey key) {
        if (key != null) {
            key.expire();
            flush();
        }
    }

    private void renewKey(K key) {
        ExpiringKey newKey = new ExpiringKey(key, now());
        ExpiringKey oldKey = expiringKeys.put(key, newKey);
        expireKey(oldKey);
        delayQueue.offer(newKey);
    }

    private long now() {
        return System.currentTimeMillis();
    }

    private class ExpiringKey implements Delayed {

        @Getter
        private final K key;

        private final long createdMillis;

        @Getter
        private boolean expired;

        public ExpiringKey(K key, long currentMillis) {
            this.key = key;
            this.createdMillis = currentMillis;
            this.expired = false;
        }

        @Override
        public long getDelay(TimeUnit unit) {
            return unit.convert(getDelayMillis(), TimeUnit.MILLISECONDS);
        }

        private long getDelayMillis() {
            if (expired) return 0;
            return createdMillis + timeoutMillis - System.currentTimeMillis();
        }

        public void expire() {
            expired = true;
        }

        @Override
        public int compareTo(Delayed o) {
            ExpiringKey other = (ExpiringKey) o;
            return Long.compare(this.getDelayMillis(), other.getDelayMillis());
        }
    }
}
