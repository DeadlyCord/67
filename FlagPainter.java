package dev.bastighg;

import net.minecraft.client.texture.NativeImage;

/**
 * Zeichnet Flaggen aus einer kleinen Beschreibungssprache auf ein 30x20-Pixelraster.
 * Ebenen werden mit "/" getrennt, Parameter mit ":".
 *   R:c           ganze Flaeche      H:c,c*w     horizontale Streifen (Gewichte)
 *   V:c,c*w       vertikale Streifen LV:c:w      Band am Mast    BD:c:h  Baender oben/unten
 *   CH:c:h CV:c:w Mittelstreifen     NC:c:t      Nordkreuz       PL:c:t  Kreuz mittig   PS:c  Schweizer Kreuz
 *   X:c:t         Diagonalkreuz      D:c:r:cx:cy Kreis           DI:c:r:cx:cy Raute
 *   S:c:r:cx:cy   Stern              T:c:w       Dreieck am Mast CT:c:w:h Gösch   CP:c:w:h Kreuz im Gösch
 *   IN:c:mx:my    Rahmen innen       Q:a,b,c,d   Quadranten      UJ:_:w:h Union Jack
 * Farben: R B G Y K W L N O M oder 6-stelliger Hex-Wert.
 */
public final class FlagPainter {
    public static final int W = 30, H = 20;
    public static final int TEX_W = 32, TEX_H = 40; // Flagge + Fahnenstange

    static int col(String t) {
        if (t.length() == 6) return 0xFF000000 | Integer.parseInt(t, 16);
        return switch (t) {
            case "R" -> 0xFFCE1126;
            case "B" -> 0xFF0033A0;
            case "G" -> 0xFF009A44;
            case "Y" -> 0xFFFCD116;
            case "K" -> 0xFF111111;
            case "W" -> 0xFFFFFFFF;
            case "L" -> 0xFF4AA3DF;
            case "N" -> 0xFF0A2A66;
            case "O" -> 0xFFFF8200;
            case "M" -> 0xFF8A1538;
            default -> 0xFFFF00FF;
        };
    }

    private static int[][] stripes(String s) {
        String[] t = s.split(",");
        int[][] r = new int[t.length][2];
        for (int i = 0; i < t.length; i++) {
            String[] p = t[i].split("\\*");
            r[i][0] = col(p[0]);
            r[i][1] = p.length > 1 ? Integer.parseInt(p[1]) : 1;
        }
        return r;
    }

    private static int pick(int[][] st, double f) {
        int tw = 0;
        for (int[] s : st) tw += s[1];
        double pos = f * tw, acc = 0;
        for (int[] s : st) {
            acc += s[1];
            if (pos < acc) return s[0];
        }
        return st[st.length - 1][0];
    }

    private static int p(String[] a, int i, int d) {
        return a.length > i ? Integer.parseInt(a[i]) : d;
    }

    private static void set(int[][] px, int x, int y, int c) {
        if (x >= 0 && x < W && y >= 0 && y < H) px[y][x] = c;
    }

    private static boolean inStar(double x, double y, double cx, double cy, double r) {
        double[] xs = new double[10], ys = new double[10];
        for (int i = 0; i < 10; i++) {
            double ang = Math.toRadians(-90 + 36 * i);
            double rr = i % 2 == 0 ? r : r * 0.4;
            xs[i] = cx + rr * Math.cos(ang);
            ys[i] = cy + rr * Math.sin(ang);
        }
        boolean in = false;
        for (int i = 0, j = 9; i < 10; j = i++) {
            if ((ys[i] > y) != (ys[j] > y)
                    && x < (xs[j] - xs[i]) * (y - ys[i]) / (ys[j] - ys[i]) + xs[i]) in = !in;
        }
        return in;
    }

    public static int[][] render(String spec) {
        int[][] px = new int[H][W];
        for (int[] row : px) java.util.Arrays.fill(row, 0xFFFFFFFF);

        for (String layer : spec.split("/")) {
            String[] a = layer.split(":");
            String n = a[0];
            String c = a.length > 1 ? a[1] : "W";
            int k = col(c.split(",")[0]);

            for (int y = 0; y < H; y++) {
                for (int x = 0; x < W; x++) {
                    double fx = x + 0.5, fy = y + 0.5;
                    boolean hit = false;
                    int color = k;
                    switch (n) {
                        case "R" -> hit = true;
                        case "H" -> { hit = true; color = pick(stripes(c), fy / H); }
                        case "V" -> { hit = true; color = pick(stripes(c), fx / W); }
                        case "LV" -> hit = x < p(a, 2, 10);
                        case "BD" -> hit = y < p(a, 2, 3) || y >= H - p(a, 2, 3);
                        case "CH" -> { int h = p(a, 2, 4); hit = y >= (H - h) / 2 && y < (H - h) / 2 + h; }
                        case "CV" -> { int w = p(a, 2, 6); hit = x >= (W - w) / 2 && x < (W - w) / 2 + w; }
                        case "NC" -> {
                            int t = p(a, 2, 4);
                            hit = (x >= 10 - t / 2 && x < 10 - t / 2 + t) || (y >= 10 - t / 2 && y < 10 - t / 2 + t);
                        }
                        case "PL" -> {
                            int t = p(a, 2, 4);
                            hit = (x >= 15 - t / 2 && x < 15 - t / 2 + t) || (y >= 10 - t / 2 && y < 10 - t / 2 + t);
                        }
                        case "PS" -> hit = (x >= 13 && x <= 16 && y >= 3 && y <= 16) || (y >= 8 && y <= 11 && x >= 8 && x <= 21);
                        case "X" -> {
                            double t = p(a, 2, 3) / 2.0;
                            double yy = x * (H - 1.0) / (W - 1.0);
                            hit = Math.abs(y - yy) < t || Math.abs(y - (H - 1 - yy)) < t;
                        }
                        case "D" -> {
                            double r = p(a, 2, 5), cx = p(a, 3, 15), cy = p(a, 4, 10);
                            hit = (fx - cx) * (fx - cx) + (fy - cy) * (fy - cy) <= r * r;
                        }
                        case "DI" -> {
                            double r = p(a, 2, 6), cx = p(a, 3, 15), cy = p(a, 4, 10);
                            hit = Math.abs(fx - cx) + Math.abs(fy - cy) <= r;
                        }
                        case "S" -> hit = inStar(fx, fy, p(a, 3, 15), p(a, 4, 10), p(a, 2, 5));
                        case "T" -> {
                            double half = Math.abs(fy - 10) / 10.0;
                            hit = fx < p(a, 2, 14) * (1 - half);
                        }
                        case "CT" -> hit = x < p(a, 2, 14) && y < p(a, 3, 10);
                        case "CP" -> {
                            int w = p(a, 2, 14), h = p(a, 3, 10);
                            int t = Math.max(2, h / 5);
                            boolean inCanton = x < w && y < h;
                            hit = inCanton && (Math.abs(fx - w / 2.0) < t / 2.0 || Math.abs(fy - h / 2.0) < t / 2.0);
                        }
                        case "IN" -> hit = x >= p(a, 2, 4) && x < W - p(a, 2, 4) && y >= p(a, 3, 3) && y < H - p(a, 3, 3);
                        case "Q" -> {
                            String[] qs = c.split(",");
                            hit = true;
                            color = col(qs[(y < H / 2 ? 0 : 2) + (x < W / 2 ? 0 : 1)]);
                        }
                        case "UJ" -> {
                            int w = p(a, 2, 15), h = p(a, 3, 10);
                            if (x < w && y < h) {
                                hit = true;
                                color = col("N");
                                double yy = x * (h - 1.0) / (w - 1.0);
                                if (Math.abs(y - yy) < 1.3 || Math.abs(y - (h - 1 - yy)) < 1.3) color = col("W");
                                double cx = w / 2.0, cy = h / 2.0;
                                double t = Math.max(2, h / 3.0);
                                if (Math.abs(fx - cx) < t / 2 || Math.abs(fy - cy) < t / 2) color = col("W");
                                double t2 = Math.max(1, h / 6.0);
                                if (Math.abs(fx - cx) < t2 / 2 + 0.2 || Math.abs(fy - cy) < t2 / 2 + 0.2) color = col("R");
                            }
                        }
                        default -> { }
                    }
                    if (hit) px[y][x] = color;
                }
            }
        }
        return px;
    }

    /** Malt Flagge + Fahnenstange in das Texturbild (32x40, transparent drumherum). */
    public static void paint(NativeImage img, String spec) {
        for (int y = 0; y < TEX_H; y++)
            for (int x = 0; x < TEX_W; x++) img.setColorArgb(x, y, 0);
        for (int y = 0; y < TEX_H; y++) {
            img.setColorArgb(0, y, 0xFF8B5A2B);
            img.setColorArgb(1, y, 0xFF6B4423);
        }
        int[][] px = render(spec);
        for (int y = 0; y < H; y++)
            for (int x = 0; x < W; x++) img.setColorArgb(x + 2, y, px[y][x]);
    }
}
