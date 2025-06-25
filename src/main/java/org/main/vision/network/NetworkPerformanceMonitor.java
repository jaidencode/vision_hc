package org.main.vision.network;

/**
 * Tracks basic network quality statistics using a lightweight
 * neural network. Incoming position corrections are fed into
 * the network and the resulting score is used as an estimate
 * of connection stability.
 */
public class NetworkPerformanceMonitor {
    private static final NetworkPerformanceMonitor INSTANCE = new NetworkPerformanceMonitor();

    private final LightweightNN net = new LightweightNN();
    private double quality = 1.0;

    private NetworkPerformanceMonitor() {}

    public static NetworkPerformanceMonitor getInstance() {
        return INSTANCE;
    }

    /**
     * Update the monitor with a server correction delta.
     */
    public void recordCorrection(double dx, double dy, double dz, boolean valid) {
        if (!org.main.vision.VisionClient.getActiveNetHack().isEnabled()) return;
        net.train(dx, dy, dz, valid);
        double score = net.evaluate(dx, dy, dz);
        // Exponential moving average of the latest score
        quality = quality * 0.95 + score * 0.05;
        if (quality < 0.0) quality = 0.0;
        if (quality > 1.0) quality = 1.0;
    }

    /**
     * @return connection quality between 0 (poor) and 1 (excellent).
     */
    public double getQuality() {
        return quality;
    }
}
