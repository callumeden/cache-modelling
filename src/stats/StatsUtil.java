package stats;

public class StatsUtil {

    private static final double CRTICAIL_VALUE = 1.96;
    private final int sampleSize;
    private final double[] dataPoints;
    private int dataPointCnt = 0;
    private double dataPointsSum;


    public StatsUtil(int sampleSize) {
        this.sampleSize = sampleSize;
        dataPoints = new double[sampleSize];
    }

    public void feedDataPoint(double dataPoint) {
        if (dataPointCnt >= sampleSize) {
            throw new IndexOutOfBoundsException("Cannot exceed sample size!");
        }
        dataPoints[dataPointCnt] = dataPoint;
        dataPointsSum += dataPoint;
        dataPointCnt++;
    }

    public double getMean() {
        return dataPointsSum / dataPointCnt;
    }

    public String getConfidenceInterval() {
        double sumOfDeviations = 0;
        double mean = getMean();
        for (int i = 0; i < dataPointCnt; i++) {
            sumOfDeviations += Math.pow(dataPoints[i] - mean, 2);
        }

        double standardDeviation = Math.sqrt(sumOfDeviations / dataPointCnt);
        double standardError = standardDeviation / Math.sqrt(dataPointCnt);
        double errMargin = standardError * CRTICAIL_VALUE;

        return String.format("[%.6f, %.6f]", (mean - errMargin), (mean + errMargin));
    }
}
