import cache.Cache;
import cache.FIFOCache;
import cache.RANDCache;
import javafx.util.Pair;
import stats.StatsUtil;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Scanner;

public class Main {

    private static final int SAMPLE_SIZE = 50;
    private static final int STARTUP_BIAS_END_TIME = 1200;
    private static final int NUM_EVENTS = 20000;

    private static Comparator<Pair<Double, Integer>> leastTimeFirstOrdering = ((o1, o2) -> {
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
        System.out.println("Please type values for n (population size) and m (cache capacity): \n");
        int N = sc.nextInt();
        int m = sc.nextInt();
        System.out.println("Please enter a strategy: RAND or FIFO\n");
        String strategy = sc.next();

        Cache cache = createCache(strategy, m);
        if (cache == null) {
            return;
        }

        StatsUtil hitRatioStats = new StatsUtil(SAMPLE_SIZE);
        StatsUtil missRateStats = new StatsUtil(SAMPLE_SIZE);

        for (int k = 0; k < SAMPLE_SIZE; k++) {
            //Returns a pair <Hit Ratio, Miss Rate>
            TrialResult trialResult = performTrial(N, cache);
            hitRatioStats.feedDataPoint(trialResult.getHitRatio());
            missRateStats.feedDataPoint(trialResult.getMissRate());
        }

        writeStatsToConsole(hitRatioStats, missRateStats);
    }

    private static Cache createCache(String strategy, int cacheSize) {

        switch (strategy) {
            case "RAND":
                return new RANDCache(cacheSize);
            case "FIFO":
                return new FIFOCache(cacheSize);
            default:
                System.err.println("Unknown strategy!");
                return null;
        }
    }

    private static TrialResult performTrial(int populationSize, Cache cache) {
        Queue<Pair<Double, Integer>> scheduledArrivals = new PriorityQueue<>(leastTimeFirstOrdering);
        scheduleInitialArrivals(scheduledArrivals, populationSize);

        double T = 0;
        double hitCount = 0;
        double missCount = 0;

        //Experiment k
        for (int i = 0; i < NUM_EVENTS; i++) {

            //Get next arrival & schedule next
            Pair<Double, Integer> arrivalPair = scheduledArrivals.poll();
            T = arrivalPair.getKey();
            int arrivalIndex = arrivalPair.getValue();

            //Only recording stats for T>1200 due to start-up bias
            if (cache.retrieve(arrivalIndex) && T >= STARTUP_BIAS_END_TIME) {
                hitCount++;
            } else {
                missCount++;
            }
            scheduleNext(scheduledArrivals, T, arrivalIndex);
        }

        double hitRatio = hitCount / (hitCount + missCount);
        double missRate = missCount / T;

        return new TrialResult(hitRatio, missRate);
    }

    private static void scheduleInitialArrivals(Queue<Pair<Double, Integer>> scheduledArrivals, int N) {
        for (int i = 0; i < N; i++) {
            scheduleNext(scheduledArrivals, 0, i + 1);
        }
    }

    private static void scheduleNext(Queue<Pair<Double, Integer>> scheduledArrivals, double T, int num) {
        double arrivalTime = getNextArrival(T, num);
        Pair<Double, Integer> p = new Pair<>(arrivalTime, num);
        scheduledArrivals.add(p);
    }

    private static double getNextArrival(double T, int num) {
        double rand = Math.random();
        double interval = -Math.log(1 - rand) * num; //lambda = 1/num
        return T + interval;
    }

    private static void writeStatsToConsole(StatsUtil hitRatioStats, StatsUtil missRateStats) {
        System.out.println("Hit ratio mean: " + hitRatioStats.getMean());
        System.out.println("Hit ratio confidence interval (95%): " + hitRatioStats.getConfidenceInterval());
        System.out.println("Miss rate mean: " + missRateStats.getMean());
        System.out.println("Miss rate confidence interval (95%): " + missRateStats.getConfidenceInterval());
    }

    private static class TrialResult {
        private double hitRatio;
        private double missRate;

        public TrialResult(double hitRatio, double missRate) {
            this.hitRatio = hitRatio;
            this.missRate = missRate;
        }

        public double getHitRatio() {
            return hitRatio;
        }

        public double getMissRate() {
            return missRate;
        }
    }

}
