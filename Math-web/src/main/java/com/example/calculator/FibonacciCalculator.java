package com.example.calculator;

import java.util.ArrayList;
import java.util.List;

public class FibonacciCalculator {

    // Class to store Arc details
    public static class QuadrantArc {
        public long radius;
        public List<Double> X = new ArrayList<>();
        public List<Double> Y = new ArrayList<>();

        public QuadrantArc(long radius) {
            this.radius = radius;
        }
    }

    public static List<QuadrantArc> calculateFibonacci(int n) {
        // ensure non-negative
        if (n < 0) n = 0;

        List<QuadrantArc> allArcs = new ArrayList<>();
        if (n == 0) return allArcs;

        // Fibonacci Sequence (Using Long to support larger n)
        List<Long> fib = new ArrayList<>();
        fib.add(1L);
        if (n > 1) fib.add(1L);

        for (int i = 2; i < n; i++) {
            long nextFib = fib.get(i - 1) + fib.get(i - 2);
            fib.add(nextFib);
        }

        double currentX = 0.0;
        double currentY = 0.0;

        // sampling density for each quarter-arc (higher = smoother)
        final double STEP_DEG = 3.0; // 3° steps yields smooth curves with reasonable performance

        // Arcs Calculation
        for (int i = 0; i < n; i++) {
            long r = fib.get(i);
            QuadrantArc arc = new QuadrantArc(r);

            // start angle rotates by 90° each arc: 0,90,180,270,...
            double startAngleDeg = (i % 4) * 90.0;
            double endAngleDeg = startAngleDeg + 90.0;

            // Center calculation so arc continues from current point:
            // start point S = current point; center C = S - r * u(startAngle)
            double startRad = Math.toRadians(startAngleDeg);
            double cx = currentX - (r * Math.cos(startRad));
            double cy = currentY - (r * Math.sin(startRad));

            // Generate sampled points from start to end (inclusive)
            for (double a = startAngleDeg; a <= endAngleDeg + 1e-9; a += STEP_DEG) {
                double rad = Math.toRadians(a);
                double px = cx + r * Math.cos(rad);
                double py = cy + r * Math.sin(rad);

                arc.X.add(px);
                arc.Y.add(py);
            }

            // update current point to arc end (last sampled)
            currentX = arc.X.get(arc.X.size() - 1);
            currentY = arc.Y.get(arc.Y.size() - 1);

            allArcs.add(arc);
        }

        return allArcs;
    }
}
