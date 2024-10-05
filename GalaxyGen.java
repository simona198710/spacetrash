package com.svamp.spacetrash;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.BitSet;
import java.util.Random;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.StrictMath.sin;

/**
 * Created by Simon on 2016-08-08.
 */
public class GalaxyGen {

    static final float PI = 3.14f;

    int imageSize = 102400;
    int numRegions = 200;

    int galaxyArms = 4;
    int maxGalaxyArms = 6;
    float galaxySwirlyAngularSpread = 4;
    float galaxyArmThickness = .02f;
    float galaxyArmWeight = .6f;
    float galaxyHueStart = 3;
    float galaxyHueChangeAlongArm = 0f;
    float galaxyHueChangeTransverseArm = 0f;
    int galaxyNumStarsInThou = 10000000; //10m
    float galaxyMaxStarRad = 2f;
    float galaxyAvgStarRadStarRad = 1f;
    private long seed;
    public int sectorX, sectorY;
    public int locationX = 51586,locationY = 51454;
    private long locationSeed;

    private byte[][] galaxy;


    public GalaxyGen(long newSeed, int newSectorX, int newSectorY) {
        seed = newSeed;
        sectorX = newSectorX;
        sectorY = newSectorY;

        generate(sectorX,sectorY);
        updateCords(locationX,locationY);
    }

    public void generate(int newSectorX, int newSectorY){

        sectorX = newSectorX;
        sectorY = newSectorY;

        Random rand = new Random(seed);

        final int W = imageSize, H = imageSize;

        galaxy = new byte[512][512];

        //BufferedImage img = new BufferedImage(W,  H, BufferedImage.TYPE_INT_RGB);
        Color colour = Color.WHITE;

        //Graphics2D g2 = im.createGraphics();

        //g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
        //RenderingHints.VALUE_ANTIALIAS_ON);

        //g2.setColor(Color.black);
        //g2.fillRect(0, 0, W, H);

        int cx = W / 2, cy = H / 2;

        //The arm extends to the edge of the image.
        // Note: Since we are using a gaussian distribution, it is not
        // guaranteed that the edge will always reach, or stop at, edge of the
        // image. Armweight will also play a role.
        int armlen = Math.min(W, H) / 2;

        int arms = galaxyArms;
        if (arms == -1)
            arms = rand.nextInt(maxGalaxyArms - 2) + 2;

        double armdTheta = Math.PI * 2 / arms;

        float swirlyAngularSpread = galaxySwirlyAngularSpread;
        if (swirlyAngularSpread == Float.MIN_VALUE)
            swirlyAngularSpread = (rand.nextFloat() * 2 + 1) *
                    (rand.nextBoolean() ? -1 : 1);

        float armThickness = galaxyArmThickness;
        if (armThickness == Float.MIN_VALUE)
            armThickness = rand.nextFloat() * .1f + .05f;

        float armsWeight = galaxyArmWeight;
        if (armsWeight == Float.MIN_VALUE)
            armsWeight = rand.nextFloat() * .3f + .1f;

        float hueStart = galaxyHueStart;
        if (hueStart == Float.MIN_VALUE)
            hueStart = rand.nextFloat();

        float hueAlongArm = galaxyHueChangeAlongArm;
        if (hueAlongArm == Float.MIN_VALUE)
            hueAlongArm = (rand.nextFloat() * .5f + .5f) * (rand.nextBoolean() ? -1 : 1);

        float hueThickArm = galaxyHueChangeTransverseArm;
        if (hueThickArm == Float.MIN_VALUE)
            hueThickArm = (rand.nextFloat() * .1f + .1f) * (rand.nextBoolean() ? -1 : 1);

        // arms = 2;
//        swirlyAngularSpread = 0;
        //swirlyAngularSpread = 3;
        // armsWeight = .6f;
        //armThickness = .02f;
        // galaxyNumStarsInThou = 20;

        final float SPIRAL_END = -1f; //???? HOW DOES THIS WORK ??

        for (int i = 0; i < galaxyNumStarsInThou * 1; i++) {
            // Pick a point in a oval distribution. Use -swirly 0 to see how this works.
//            int x = rand.nextInt(armlen);
            double x = Math.abs(rand.nextGaussian()) * armlen * armsWeight;
//            int y = rand.nextInt(armlen/10);
            double y = rand.nextGaussian() * armlen * armThickness;

            double org_y = y;

            y *= (Math.pow(1.1, x * 15 / armlen) - 1 + .5) * 6;

            // Chose an arm
            int arm = rand.nextInt(arms);

            double r = Math.sqrt(x * x + y * y);

            double alph = r / (armlen * (1 - SPIRAL_END));
            alph = 1 - Math.pow((1 - alph), 10);

            if (alph > 1)
                alph = 1;

            // Rotate the star based on how far it is from the center, and also
            // which arm it is in.
            double dTh = swirlyAngularSpread * alph;

            // rotate by 90
            dTh += .5;

            dTh *= Math.PI;

            dTh += arm * armdTheta;

            double sin = Math.sin(dTh);
            double cos = Math.cos(dTh);
            double x1 = x * cos - y * sin;
            double y1 = y * cos + x * sin;

            x1 += cx;
            y1 += cy;

            float hue = (float) (hueStart + x * hueAlongArm / armlen + Math.abs(org_y) * hueThickArm / armlen / armThickness/*+ rand.nextFloat() * .3f*/);
            int rad = (int) (Math.abs(rand.nextGaussian()) * galaxyAvgStarRadStarRad) + 1;
            if (i > galaxyNumStarsInThou * 1000 * 999 / 1000)
                rad = (int) galaxyMaxStarRad;

            if (rad > galaxyMaxStarRad)
                rad = (int) galaxyMaxStarRad;
            float bright = .6f;
            if (rad > 2 * galaxyAvgStarRadStarRad)
                bright = .9f;

//            bright = rand.nextFloat();

            if (x1 < 0 || y1 < 0 || x1 > W || y1 > H)
                continue;

            //drawStar(g2, (int) x1, (int) y1, rad, hue, .9f, bright);

            //img.setRGB((int) x1, (int) y1, colour.getRGB());

            if ((int) x1 < (imageSize / numRegions) * (sectorX + 1) && (int) x1 >= (imageSize / numRegions) * (sectorX)) {
                if ((int) y1 < (imageSize / numRegions) * (sectorY + 1) && (int) y1 >= (imageSize / numRegions) * (sectorY)) {
                    //System.out.println("Real X: "+ ((int) x1) + " Local X: " + (((int)x1) - ((imageSize / numRegions) * (sectorX))));
                    //System.out.println("Real Y: "+ ((int) y1) + " Local Y: " + (((int)y1) - ((imageSize / numRegions) * (sectorY))));
                    galaxy[((int)x1) - ((imageSize / numRegions) * (sectorX))][((int)y1) - ((imageSize / numRegions) * (sectorY))]= 1;
                }
                //System.out.println("Value X betwen: " + (1024 * (regionX)) + " and " + (1024 * (regionX +1)));
                //System.out.println("Value Y betwen: " + (1024 * (regionY)) + " and " + (1024 * (regionY +1)));
            }
            //System.out.println("Value betwen: " + (800 * (sector)) + " and " + (800 * (sector +1)));
        }
        //galaxy[(int) x1][(int) y1].set(1);
        //System.out.println((int) x1);
        //System.out.println((int) y1);

//        CirclesBorderMaker bm = new CirclesBorderMaker(W, H, rand);
//        BufferedImage bim = bm.make();
//
//        g2.drawImage(bim, 0, 0, null);

    //makeBorder(g2, W, H, rand);

    //SID_DEBUG
    //saveImage(im, "out/back");
    //saveImage(img, "galaxeriminabraxer");
}

    private static void makeBorder(Graphics2D g2, int w, int h, Random rand)
    {
        g2 = (Graphics2D) g2.create();
        int border = w/16;

        g2.setStroke(new BasicStroke(1.5f));
        g2.setColor(Color.lightGray);

        g2.drawLine(border - 20, border, border+w*2/3, border);
        g2.drawLine(border, border-20, border, border+h*2/3);

        g2.translate(w, h);
        g2.scale(-1, -1);

        g2.drawLine(border - 20, border, border+w*2/3, border);
        g2.drawLine(border, border-20, border, border+h*2/3);

        g2.dispose();
    }

    private static void flipBorder(int[] px, int[] py, int w, int h)
    {
        for(int i = 0; i < px.length; i++)
            px[i] = w - px[i];
        for(int i = 0; i < py.length; i++)
            py[i] = h - py[i];
    }

    public static void saveImage(BufferedImage im, String name)
    {
        int i = 0;
        for (;i < 1000; i++)
        {
            File f = new File(name+i+".png");
            if (f.exists())
                continue;

            try
            {
                ImageIO.write(im, "png", f);
            } catch (IOException e)
            {
                e.printStackTrace();
            }

            break;
        }

        if (i == 1000)
            System.err.println("Could not write image, already 1000 images with"
                    + " the same prefix exist.");
    }

    // WARNING: This needs to be thread local for the code to be multithreaded
    private static Color[] gCol = new Color[]{null, new Color(0, true)};
    private static final float[] zeroOne = new float[]{0, 1};

    public static void drawStar(Graphics2D g2, double x, double y, int rad, float hue,
                                float sat, float bright)
    {
        int rgb = Color.HSBtoRGB(hue, sat, bright);
        gCol[0] = new Color(rgb);
        RadialGradientPaint p = new RadialGradientPaint((float)x, (float)y, rad, zeroOne, gCol);

//        g2.setColor(new Color(rgb + 0x71000000, true));
        g2.setPaint(p);
        g2.fillOval(((int)x)-rad, ((int)y)-rad, 2*rad+1, 2*rad+1);
//        g2.fillRect(((int)x)-rad, ((int)y)-rad, 2*rad+1, 2*rad+1);
    }

    public byte[][] getGalaxyData(){
        return galaxy;
    }

    public long[] updateCords(int newX, int newY){

        locationX = newX;
        locationY = newY;
        int localX = ((int)locationX) - ((imageSize / numRegions) * (sectorX));
        int localY = ((int)locationY) - ((imageSize / numRegions) * (sectorY));

        long hasStar = 0;
        long localSeed = 0;

        if (galaxy[localX][localY] == 1){
            String seedText = Integer.toString(locationX)+Integer.toString(locationY)+Integer.toString(imageSize);
            localSeed = Long.parseLong(seedText);
            hasStar = 1;
        }
        long[] result = {hasStar,localSeed};
        locationSeed = localSeed;
        return result;

    }

    public int[] getLocalCords(){
        int localX = ((int)locationX) - ((imageSize / numRegions) * (sectorX));
        int localY = ((int)locationY) - ((imageSize / numRegions) * (sectorY));
        int[] result = {localX,localY};
        return result;
    }

    public long getSeed(){
        return seed;
    }

    public long getLocationSeed(){
        return locationSeed;
    }
}