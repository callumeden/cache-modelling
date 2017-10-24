package cache;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RANDCache extends Cache {
    private List<Integer> store;
    private Victimiser victimiser;

    public RANDCache(int size) {
        super(size);
        this.victimiser = new Victimiser(size);

    }

    private int evict() {
        int victimPosition = victimiser.next();
        knownVals.remove(store.get(victimPosition));
        store.remove(victimPosition);
        return victimPosition;
    }

    @Override
    protected void replace(int val) {
        int evictPos = evict();
        store.add(evictPos, val);
    }

    @Override
    protected void addToStore(int val) {
        if (store == null) {
            store = new ArrayList<>();
        }
        store.add(val);
    }


    private static class Victimiser {
        private int size;
        private Random rand;

        Victimiser(int size) {
            this.size = size;
            rand = new Random();
        }

        int next() {
            return rand.nextInt(size);
        }
    }
}
