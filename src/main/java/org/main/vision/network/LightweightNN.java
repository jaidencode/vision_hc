package org.main.vision.network;

/**
 * Very small neural net using logistic regression to classify
 * movement deltas as valid or invalid. This is intentionally
 * lightweight so it can run every tick without overhead.
 */
public class LightweightNN {
    private final double[] w = new double[4];
    private static final double RATE = 0.05;

    /** Raw probability that the given deltas are valid. */
    public double evaluate(double dx, double dy, double dz) {
        double z = w[0] * dx + w[1] * dy + w[2] * dz + w[3];
        return sigmoid(z);
    }

    /** Evaluate whether the given deltas are likely valid. */
    public boolean allow(double dx, double dy, double dz) {
        return evaluate(dx, dy, dz) >= 0.5;
    }

    /** Train the network with the given outcome. */
    public void train(double dx, double dy, double dz, boolean valid) {
        double z = w[0] * dx + w[1] * dy + w[2] * dz + w[3];
        double out = sigmoid(z);
        double error = (valid ? 1.0 : 0.0) - out;
        w[0] += RATE * error * dx;
        w[1] += RATE * error * dy;
        w[2] += RATE * error * dz;
        w[3] += RATE * error;
    }

    private double sigmoid(double v) {
        return 1.0 / (1.0 + Math.exp(-v));
    }
}
