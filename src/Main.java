import java.util.*;

public class Main {

    private static int front = -1;

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
                cache = new FIFOCache();
                break;
            default:
                System.err.println("Unknown strategy!");
        }

    }

    private static abstract class Cache {
        Set<Integer> knownVals = new HashSet<>();

        public boolean retrieve(int val) {
            if (knownVals.contains(val)) {
                return true;
            }
            evict();
            addToStore(val);
            knownVals.add(val);
            return false;
        }

        abstract void evict();
        abstract void addToStore(int val);
    }

    private static class FIFOCache extends Cache {
        private Queue<Integer> store = new ArrayDeque<>();


        @Override
        void evict() {
            int removed = store.poll();
            knownVals.remove(removed);
        }

        @Override
        void addToStore(int val) {
            store.add(val);
        }
    }

    private static class RANDCache extends Cache {
        private List<Integer> store = new ArrayList<>();
        private int size;

        public RANDCache(int size) {
            this.size = size;
        }


        @Override
        void evict() {

        }

        @Override
        void addToStore(int val) {
            store.add(val);
        }
    }
}
