import javafx.util.Pair;

import java.util.*;

public class Main {

    private static Queue<Pair<Integer, Integer>> retrievalQueue = new ArrayDeque<>();

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Please type values for n and m: \n");
        int n = sc.nextInt();
        int m = sc.nextInt();
        System.out.println("Please enter a strategy: RAND or FIFO\n");
        String strategy = sc.next();

        Cache cache;

        switch (strategy) {
            case "RAND":
                cache = new RANDCache(m);
                break;
            case "FIFO":
                cache = new FIFOCache(m);
                break;
            default:
                System.err.println("Unknown strategy!");
                return;
        }

        System.out.println(cache);

    }

    private static abstract class Cache {
        Set<Integer> knownVals = new HashSet<>();

        public Cache(int size) {
            populateCache(size);
        }

        private void populateCache(int size) {
            for (int i = 1; i <= size; i ++) {

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

    private static class FIFOCache extends Cache {
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

    private static class RANDCache extends Cache {
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

    }

    private static class Victimiser {
        private int size;
        private Random rand;

        public Victimiser(int size) {
            this.size = size;
            rand = new Random();
        }

        public int next() {
            return rand.nextInt(size);
        }
    }

}
