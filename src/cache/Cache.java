package cache;

import java.util.HashSet;
import java.util.Set;

public abstract class Cache {
    Set<Integer> knownVals = new HashSet<>();

    public Cache(int size) {
        populateCache(size);
    }

    private void populateCache(int size) {
        for (int i = 1; i <= size; i++) {

            addToStore(i);
            knownVals.add(i);
        }

    }

    public boolean retrieve(int val) {
        if (knownVals.contains(val)) {
            return true;
        }
        replace(val);
        knownVals.add(val);
        return false;
    }

    protected abstract void replace(int val);

    protected abstract void addToStore(int val);
}
