package com.svamp.spacetrash;

import com.badlogic.gdx.graphics.Color;

import java.lang.reflect.Field;
import java.util.Random;

/**
 * Created by Simon on 2016-08-06.
 */
public class Utils {

    public enum GrassColors {
        GREEN, ORANGE, FOREST, GOLD, TAN, TEAL, GOLDENROD, FIREBRICK, CHARTREUSE, RED, CORAL, BROWN, LIME, MAGENTA, MAROON, OLIVE, PURPLE, PINK, ROYAL, SCARLET, VIOLET
    }
    public enum TreeColors {
        GREEN, ORANGE, FOREST, GOLD, TAN, TEAL, GOLDENROD, FIREBRICK, CHARTREUSE, RED, CORAL, BROWN, LIME, MAGENTA, MAROON, OLIVE, PURPLE, PINK, ROYAL, SCARLET, VIOLET
    }
    public enum SandColors {
        YELLOW, SLATE, LIGHT_GRAY, GRAY, BROWN, DARK_GRAY
    }
    public enum MountainColors {
        SLATE, LIGHT_GRAY, GRAY, BROWN, DARK_GRAY, WHITE
    }
    public enum WaterColors {
        BLUE, NAVY, GREEN, ORANGE, FOREST, GOLD, TAN, TEAL, GOLDENROD, FIREBRICK, CHARTREUSE, RED, CORAL, BROWN, LIME, MAGENTA, MAROON, OLIVE, PURPLE, PINK, ROYAL, SCARLET, VIOLET
    }

    public static GrassColors getGrassColor(long seed){
        GrassColors[] colors = GrassColors.values();
        Random generator = new Random(seed+seed);
        return colors[generator.nextInt(colors.length)];
    }
    public static TreeColors getTreeColor(long seed){
        TreeColors[] colors = TreeColors.values();
        Random generator = new Random(seed);
        return colors[generator.nextInt(colors.length)];
    }
    public static SandColors getSandColor(long seed){
        SandColors[] colors = SandColors.values();
        Random generator = new Random(seed);
        return colors[generator.nextInt(colors.length)];
    }
    public static MountainColors getMountainColor(long seed){
        MountainColors[] colors = MountainColors.values();
        Random generator = new Random(seed);
        return colors[generator.nextInt(colors.length)];
    }
    public static WaterColors getWaterColor(long seed){
        WaterColors[] colors = WaterColors.values();
        Random generator = new Random(seed);
        return colors[generator.nextInt(colors.length)];
    }

    /**
     * Gets a random element out of a weighted list.
     *
     * @return index of chosen interval.
     */
    public static int oneOfWeightedList(Random rng, final int [] weights)
    {
        int sum = 0;

        for(int i=0; i<weights.length; i++) {
            sum += weights[i];
        }

        final int weight = (int)(sum*rng.nextDouble());

        sum = 0;

        for(int i=0; i<weights.length; i++) {
            final int high = sum + weights[i];
            if(sum <= weight && weight < high) {
                return i;
            }
            sum = high;
        }

        // Hajo: empty lists or crippled weights
        return -1;
    }

    private static final double sq2p = Math.sqrt(2 * Math.PI);

    public static double gauss(double x, double mu, double sigma)
    {
        final double t = ((x-mu)/sigma);
        return Math.exp(-0.5 * t * t) / (sigma * sq2p);
    }

    /**
     * Check if the point (x0, y0) can see point (x1, y1) by drawing a line
     * and testing for the "blocking" property at each new tile. Returns the
     * points on the line if it is, in fact, visible. Otherwise, returns an
     * empty list (rather than null - Efficient Java, item #43).
     *
     * http://en.wikipedia.org/wiki/Bresenham%27s_line_algorithm
     */
    public static boolean isVisibleUp(Terrgen map, int x0, int y0, int x1, int y1) {
        //x1 = x0 +1;
        //y1 = y0 +1;
        //List<Point> line = new LinkedList<Point>();
        //line.add(new Point(x0, y0));
        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);
        //int sx = (x0 < x1) ? x1 : -x1; //wtf är tile_x och tile_y
        //int sy = (y0 < y1) ? y1 : -y1;
        int sx = -1;
        int sy = 1;
        int err = dx - dy;
        int e2;

        while (!(x0 == x1 && y0 == y1)) {
            if (map.isBlocked(x0, y0)) {
                //line.clear();
                //String debugInfo = "X: " + Integer.toString(x0) + " Y: " + Integer.toString(y0);
                //System.out.println(debugInfo);
                return false;
            }

            e2 = 2 * err;

            if (e2 > -dy) {
                err -= dy;
                x0 += sx;
            }

            if (e2 < dx) {
                err += dx;
                y0 += sy;
            }

            //System.out.println(x0);
            //System.out.println(y0);
            //line.add(new Point(x0, y0));
        }

        return true;
    }

    public static boolean isVisibleDunno(Terrgen map, int x0, int y0, int x1, int y1) {
        //x1 = x0 +1;
        //y1 = y0 +1;
        //List<Point> line = new LinkedList<Point>();
        //line.add(new Point(x0, y0));
        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);
        //int sx = (x0 < x1) ? x1 : -x1; //wtf är tile_x och tile_y
        //int sy = (y0 < y1) ? y1 : -y1;
        int sx = 1;
        int sy = -1;
        int err = dx - dy;
        int e2;

        while (!(x0 == x1 && y0 == y1)) {
            if (map.isBlocked(x0, y0)) {
                //line.clear();
                //String debugInfo = "X: " + Integer.toString(x0) + " Y: " + Integer.toString(y0);
                //System.out.println(debugInfo);
                return false;
            }

            e2 = 2 * err;

            if (e2 > -dy) {
                err -= dy;
                x0 += sx;
            }

            if (e2 < dx) {
                err += dx;
                y0 += sy;
            }

            //System.out.println(x0);
            //System.out.println(y0);
            //line.add(new Point(x0, y0));
        }

        return true;
    }

    public static boolean isVisibleDown(Terrgen map, int x0, int y0, int x1, int y1) {
        //x1 = x0 +1;
        //y1 = y0 +1;
        //List<Point> line = new LinkedList<Point>();
        //line.add(new Point(x0, y0));
        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);
        //int sx = (x0 < x1) ? x1 : -x1; //wtf är tile_x och tile_y
        //int sy = (y0 < y1) ? y1 : -y1;
        int sx = -1;
        int sy = -1;
        int err = dx - dy;
        int e2;

        while (!(x0 == x1 && y0 == y1)) {
            if (map.isBlocked(x0, y0)) {
                //line.clear();
                //String debugInfo = "X: " + Integer.toString(x0) + " Y: " + Integer.toString(y0);
                //System.out.println(debugInfo);
                return false;
            }

            e2 = 2 * err;

            if (e2 > -dy) {
                err -= dy;
                x0 += sx;
            }

            if (e2 < dx) {
                err += dx;
                y0 += sy;
            }

            //System.out.println(x0);
            //System.out.println(y0);
            //line.add(new Point(x0, y0));
        }

        return true;
    }

    public static boolean isVisibleRight(Terrgen map, int x0, int y0, int x1, int y1) {
        //x1 = x0 +1;
        //y1 = y0 +1;
        //List<Point> line = new LinkedList<Point>();
        //line.add(new Point(x0, y0));
        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);
        //int sx = (x0 < x1) ? x1 : -x1; //wtf är tile_x och tile_y
        //int sy = (y0 < y1) ? y1 : -y1;
        int sx = 1;
        int sy = 1;
        int err = dx - dy;
        int e2;

        while (!(x0 == x1 && y0 == y1)) {
            if (map.isBlocked(x0, y0)) {
                //line.clear();
                //String debugInfo = "X: " + Integer.toString(x0) + " Y: " + Integer.toString(y0);
                //System.out.println(debugInfo);
                return false;
            }

            e2 = 2 * err;

            if (e2 > -dy) {
                err -= dy;
                x0 += sx;
            }

            if (e2 < dx) {
                err += dx;
                y0 += sy;
            }

            //System.out.println(x0);
            //System.out.println(y0);
            //line.add(new Point(x0, y0));
        }

        return true;
    }

    public static boolean isVisibleLeft(Terrgen map, int x0, int y0, int x1, int y1) {
        //x1 = x0 +1;
        //y1 = y0 +1;
        //List<Point> line = new LinkedList<Point>();
        //line.add(new Point(x0, y0));
        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);
        //int sx = (x0 < x1) ? x1 : -x1; //wtf är tile_x och tile_y
        //int sy = (y0 < y1) ? y1 : -y1;
        int sx = -1;
        int sy = -1;
        int err = dx - dy;
        int e2;

        while (!(x0 == x1 && y0 == y1)) {
            if (map.isBlocked(x0, y0)) {
                //line.clear();
                //String debugInfo = "X: " + Integer.toString(x0) + " Y: " + Integer.toString(y0);
                //System.out.println(debugInfo);
                return false;
            }

            e2 = 2 * err;

            if (e2 > -dy) {
                err -= dy;
                x0 += sx;
            }

            if (e2 < dx) {
                err += dx;
                y0 += sy;
            }

            //System.out.println(x0);
            //System.out.println(y0);
            //line.add(new Point(x0, y0));
        }

        return true;
    }
}
