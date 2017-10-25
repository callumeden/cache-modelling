import cache.Cache;
import cache.FIFOCache;
import cache.RANDCache;
import javafx.util.Pair;
import org.math.plot.Plot2DPanel;

import javax.swing.*;
import java.awt.*;
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

        double T = 0;
        double hitCount = 0;
        double missCount = 0;
        scheduleInitialArrivals(N);

        Plot2DPanel plot = new Plot2DPanel();
        double x[] = new double[20000];
        double y[] = new double[20000];

        for (int i = 0; i < 20000; i++) {

            Pair<Double, Integer> arrivalPair = scheduledArrivals.poll();
            T = arrivalPair.getKey();
            int arrivalIndex = arrivalPair.getValue();
            if (cache.retrieve(arrivalIndex)) {
                hitCount++;
            } else {
                missCount++;
            }
            scheduleNext(T, arrivalIndex);

            //Writing stats for graph plot
            x[i] = T;
            double missRate = missCount / T;
            y[i] = missRate;

            if (T >= 1200) {
                //Safe point to discard existing stats due to start-up bias
                hitCount = 0;
                missCount = 0;
            }
        }


        //Plot miss rate to determine time after which start-up bias ends
        plot.addLinePlot("Miss rate plot", x, y);
        plot.addLabel("Time", Color.RED, 280.0, 580.0);
        plot.addLabel("Miss Rate", Color.RED, 10.0, 265.0);
        JFrame frame = new JFrame("a plot panel");
        frame.setSize(600, 600);
        frame.setContentPane(plot);
        frame.setVisible(true);

        System.out.println("Number of hits were : " + hitCount);
        System.out.println("Number of misses were : " + missCount);
    }

    private static void scheduleInitialArrivals(int N) {
        for (int i = 0; i < N; i++) {
            scheduleNext(0, i + 1);
        }
    }

    private static void scheduleNext(double T, int num) {
        double arrivalTime = getNextArrival(T, num);
        Pair<Double, Integer> p = new Pair<>(arrivalTime, num);
        scheduledArrivals.add(p);
    }

    private static double getNextArrival(double T, int num) {
        double rand = Math.random();
        double interval = -Math.log(1 - rand) * num; //lambda = 1/num
        return T + interval;
    }

}
