package cache;

import java.util.ArrayDeque;
import java.util.Queue;

public class FIFOCache extends Cache {
    private Queue<Integer> store;

    public FIFOCache(int size) {
        super(size);
    }

    private void evict() {
        int removed = store.poll();
        knownVals.remove(removed);
    }

    @Override
    protected void replace(int val) {
        evict();
        store.add(val);
    }

    @Override
    protected void addToStore(int val) {
        if (store == null) {
            store = new ArrayDeque<>();
        }
        store.add(val);
    }

}