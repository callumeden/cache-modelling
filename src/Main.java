import cache.Cache;
import cache.FIFOCache;
import cache.RANDCache;
import javafx.util.Pair;

import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Scanner;

public class Main {

    //Stores scheduled events in the form of (time, itemNo) where time = event time & 1 <= itemNo <= N
    //Ordered by time ascending
    private static Queue<Pair<Double, Integer>> scheduledArrivals = new PriorityQueue<>((o1, o2) -> {
        if (o1.getKey().equals(o2.getKey())) {
            return 0;
        }

        if (o1.getKey() > o2.getKey()) {
            return 1;
        }

        return -1;
    });

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Please type values for n and m: \n");
        int N = sc.nextInt();
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

        int T = 0;
        scheduleInitialArrivals(N);

//        while (true) {
//            Pair<Integer, Integer> arrivalPair = scheduledArrivals.poll();
//            T = arrivalPair.getKey();
//            int arrivalIndex = arrivalPair.getValue();
//            scheduleNext(T, arrivalIndex);
//            System.out.println("Time of " + T);
//        }

//        System.out.println(cache);

    }

    private static void scheduleInitialArrivals(int N) {
        for (int i = 0; i < N; i++) {
            scheduleNext(0, i + 1);
        }
    }

    private static void scheduleNext(int T, int num) {
        double arrivalTime = getNextArrival(T, num);
        Pair p = new Pair<>(arrivalTime, num);
        scheduledArrivals.add(p);

        double arrivalSum = 0;
        for (int i = 0; i < 500; i ++) {
            arrivalSum += getNextArrival(0, 9);
        }
        System.out.println("AVERAGE TIME IS :" + arrivalSum/500.0);
    }

    private static double getNextArrival(int T, int num) {
        double rand = Math.random();
        double interval = -Math.log(1 - rand) * num; //lambda = 1/num
        return T + interval;

    }

}
