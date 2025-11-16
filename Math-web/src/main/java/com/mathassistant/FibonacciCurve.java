package com.mathassistant;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * FibonacciCurve
 *
 * Produces SVG output for a Fibonacci quadrant spiral with:
 *  - light grid background
 *  - rainbow per-arc strokes
 *  - sampled quarter-circle arcs (smooth paths)
 *  - number bubbles (radius labels)
 *  - dashed axes and center dot
 *  - title
 *
 * Public API:
 *  - String generateSVG(int n, boolean unitsMode, boolean generatorMode)
 *  - String generateSvg(int n)  // convenience wrapper for backward-compatibility
 */
public class FibonacciCurve {

    /* Green color for all curves */
    private static final String[] COLORS = {
        "#00AA00"
    };

    // -----------------------
    // Public wrapper (old name)
    // -----------------------
    public String generateSvg(int n) {
        return generateSVG(n, false, false);
    }

    /**
     * Main SVG generator (mirrors the style you provided earlier).
     * @param n number of Fibonacci radii (1..40 recommended)
     */
    public String generateSVG(int n, boolean unitsMode, boolean generatorMode) {

        final int SIZE = 700;           // canvas 700×700
        final double M  = 60;           // margin
        final double US = SIZE - 2*M;   // usable square

        StringBuilder svg = new StringBuilder();
        svg.append("<?xml version='1.0' encoding='UTF-8'?>\n");
        svg.append("<svg xmlns='http://www.w3.org/2000/svg' ")
           .append("width='").append(SIZE).append("' height='").append(SIZE)
           .append("' viewBox='0 0 ").append(SIZE).append(" ").append(SIZE).append("'>\n");

        // defs and light grid background
        svg.append("  <defs>\n");
        svg.append("    <pattern id='grid' width='10' height='10' patternUnits='userSpaceOnUse'>\n");
        svg.append("      <path d='M 10 0 L 0 0 0 10' fill='none' stroke='#e8e8e8' stroke-width='0.7'/>\n");
        svg.append("    </pattern>\n");
        svg.append("    <clipPath id='clipPad'><rect x='0' y='0' width='").append(SIZE).append("' height='").append(SIZE).append("'/></clipPath>\n");
        svg.append("  </defs>\n");
        svg.append("  <rect width='100%' height='100%' fill='url(#grid)'/>\n");
        svg.append("  <rect x='0.5' y='0.5' width='").append(SIZE-1)
           .append("' height='").append(SIZE-1)
           .append("' fill='none' stroke='#ddd' stroke-width='1'/>\n");

        // Validate n
        if (n < 1 || n > 40) {
            svg.append("  <text x='").append(SIZE/2).append("' y='").append(SIZE/2)
               .append("' font-family='Arial' font-size='20' font-weight='bold' text-anchor='middle' fill='red'>")
               .append("Enter N between 1 and 40</text>\n</svg>");
            return svg.toString();
        }

        // compute arcs (sampled points for each quarter-circle)
        List<QuadrantArc> arcs = calculateQuadrantArcs(n);

        // compute bounds
        double minX=Double.POSITIVE_INFINITY, minY=Double.POSITIVE_INFINITY, maxX=Double.NEGATIVE_INFINITY, maxY=Double.NEGATIVE_INFINITY;
        for (QuadrantArc a : arcs) {
            for (double v : a.X) { if (v < minX) minX = v; if (v > maxX) maxX = v; }
            for (double v : a.Y) { if (v < minY) minY = v; if (v > maxY) maxY = v; }
        }
        // fallback if degenerate
        if (minX == Double.POSITIVE_INFINITY) { minX = -1; maxX = 1; minY = -1; maxY = 1; }

        double worldW = Math.max(1e-6, maxX - minX);
        double worldH = Math.max(1e-6, maxY - minY);

        // center the world box inside the usable square (US)
        double scale = US / Math.max(worldW, worldH);
        if (scale <= 0) scale = 1.0;
        double extraX = (US - worldW * scale) / 2.0;
        double extraY = (US - worldH * scale) / 2.0;
        final double baseX = M + extraX; // pixel x of world.minX
        final double baseY = M + extraY; // pixel y of world.maxY (top)
        double ox = baseX + (0 - minX) * scale;            // origin in px
        double oy = baseY + (maxY - 0) * scale;

        // draw solid dark axes (locked, centered)
        svg.append("  <g stroke='#222' stroke-width='2'>\n");
        svg.append("    <line x1='0' y1='").append(fmt(oy)).append("' x2='").append(SIZE)
           .append("' y2='").append(fmt(oy)).append("'/>\n"); // X axis
        svg.append("    <line x1='").append(fmt(ox)).append("' y1='0' x2='")
           .append(fmt(ox)).append("' y2='").append(SIZE).append("'/>\n"); // Y axis
        svg.append("  </g>\n");

        // center dot
        svg.append("  <circle cx='").append(fmt(ox)).append("' cy='").append(fmt(oy))
           .append("' r='3' fill='#222'/>\n");

        // group transform: scale & shift world coordinates to canvas pixels
        svg.append("  <g clip-path='url(#clipPad)'>\n");

        // map arcs using baseX/baseY so everything is centered and locked
        int colIdx = 0;
        for (QuadrantArc a : arcs) {
            String col = COLORS[colIdx++ % COLORS.length];

            // Build path from sampled points
            svg.append("    <path d='");
            boolean first = true;
            for (int i = 0; i < a.X.size(); i++) {
                double px = baseX + (a.X.get(i) - minX) * scale;
                double py = baseY + (maxY - a.Y.get(i)) * scale;
                svg.append(first ? "M " : " L ").append(fmt(px)).append(" ").append(fmt(py));
                first = false;
            }
            svg.append("' stroke='").append(col)
               .append("' stroke-width='3' fill='none' stroke-linecap='round' stroke-linejoin='round'/>\n");

            // number bubble at mid-point
            int mid = a.X.size() / 2;
            double lx = baseX + (a.X.get(mid) - minX) * scale;
            double ly = baseY + (maxY - a.Y.get(mid)) * scale;
            svg.append("    <circle cx='").append(fmt(lx)).append("' cy='").append(fmt(ly))
               .append("' r='10' fill='white' stroke='").append(col).append("' stroke-width='1.3'/>\n");
            svg.append("    <text x='").append(fmt(lx)).append("' y='").append(fmt(ly+3))
               .append("' font-family='Arial' font-size='9' font-weight='bold' text-anchor='middle' fill='#333'>")
               .append(a.radius).append("</text>\n");
        }

        svg.append("  </g>\n");

        svg.append("</svg>");
        return svg.toString();
    }

    // Add backward-compatible method expected by other parts of the project
    public byte[] generateCurve(int n, int size) {
        // produce SVG string (existing generator method)
        String svg = generateSvg(n);
        // return UTF-8 bytes so callers compile and receive content
        return svg.getBytes(StandardCharsets.UTF_8);
    }

    // -----------------------
    // Helper: QuadrantArc class
    // -----------------------
    private static class QuadrantArc {
        List<Double> X = new ArrayList<>();
        List<Double> Y = new ArrayList<>();
        int radius;
    }

    /**
     * Build a sampled list of quarter-circle arcs using the same math approach
     * as your original code: centers offset by radius according to theta,
     * and each arc spans 90 degrees. Each arc is sampled with 'steps' points.
     */
    private List<QuadrantArc> calculateQuadrantArcs(int n) {
        if (n < 1) n = 1;
        if (n > 40) n = 40;

        // Fibonacci numbers (starting with 1,1,...)
        List<Double> fibs = new ArrayList<>();
        if (n >= 1) fibs.add(1.0);
        if (n >= 2) fibs.add(1.0);
        for (int i = 2; i < n; i++) {
            fibs.add(fibs.get(i - 1) + fibs.get(i - 2));
        }

        List<QuadrantArc> arcs = new ArrayList<>();
        double currX = 0, currY = 0, theta_deg = 0;

        // sample density for each quarter-arc (higher = smoother)
        final int STEPS = 28;

        for (double r : fibs) {

            double sin_th = Math.sin(Math.toRadians(theta_deg));
            double cos_th = Math.cos(Math.toRadians(theta_deg));

            // px,py as outward unit vector perpendicular to direction theta
            double px = -sin_th;
            double py = cos_th;

            double cenX = currX + r * px;
            double cenY = currY + r * py;

            // vector from center to current point
            double vx = currX - cenX;
            double vy = currY - cenY;
            double start_ang = Math.toDegrees(Math.atan2(vy, vx));
            double end_ang = start_ang + 90.0;

            QuadrantArc qa = new QuadrantArc();
            qa.radius = (int)Math.round(r);

            // sample points from start_ang -> end_ang inclusive
            for (int s = 0; s <= STEPS; s++) {
                double t = start_ang + (end_ang - start_ang) * (s / (double)STEPS);
                double pxs = cenX + r * Math.cos(Math.toRadians(t));
                double pys = cenY + r * Math.sin(Math.toRadians(t));
                qa.X.add(pxs);
                qa.Y.add(pys);
            }

            // update current point to the arc's end (last sampled)
            currX = qa.X.get(qa.X.size()-1);
            currY = qa.Y.get(qa.Y.size()-1);

            // rotate theta by 90 degrees for next quadrant
            theta_deg = (theta_deg + 90.0) % 360.0;

            arcs.add(qa);
        }

        return arcs;
    }

    // -----------------------
    // Formatting helpers
    // -----------------------
    private static String fmt(double v) { return String.format("%.3f", v); }
}