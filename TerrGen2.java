package com.svamp.spacetrash;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * Created by Simon on 2016-08-04.
 */
public class TerrGen2 {

    private static final double FEATURE_SIZE = 8;

    public float[][] generate(int width,int height) {
        double[] data = GenNoise.normalize(GenNoise.smoothNoise(width, height, FEATURE_SIZE));

        for (int i = 0; i < data.length; i++)
            data[i] = 255*data[i];

        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        img.getRaster().setPixels(0, 0, width, height, data);

        int w = img.getWidth();
        int h = img.getHeight();
        float[][] pixels = new float[w][h];

        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                //System.out.println("x,y: " + j + ", " + i);
                int pixel = img.getRGB(j, i);
                float pixelColor = getPixelARGB(pixel);
                pixels[i][j] = pixelColor;
                //System.out.println("");
            }
        }

        //System.out.println(Arrays.toString(pixels));

        /*
        try {
            // retrieve image
            File outputfile = new File("saved.png");
            outputfile.createNewFile();

            ImageIO.write(img, "png", outputfile);
        } catch (IOException e) {
            //o no!
        }
        */
        return pixels;
    }

    public float[][] generateSimplex(int mapsize, byte numRegions, long seed, int featureSize ,int regionX, int regionY) {

            OpenSimplexNoise noise = new OpenSimplexNoise(seed);
            //BufferedImage img = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB); //krashar på android..
            float[][] pixels = new float[512][512];

            for (int y = 0; y < mapsize; y++)
            {
                for (int x = 0; x < mapsize; x++)
                {
                    if ((int) x < (mapsize / numRegions) * (regionX + 1) && (int) x >= (mapsize / numRegions) * (regionX)) {
                        if ((int) y < (mapsize / numRegions) * (regionY + 1) && (int) y >= (mapsize / numRegions) * (regionY)) {
                            double value = noise.eval(x / featureSize, y / featureSize, 0.0);
                            int localX = ((int)x) - ((mapsize / numRegions) * (regionX));
                            int localY = ((int)y) - ((mapsize / numRegions) * (regionY));
                            int rgb = 0x010101 * (int) ((value + 1) * 127.5);
                            //img.setRGB(x, y, rgb); //krashar på android..
                            //System.out.println(getPixelARGB(rgb));
                            pixels[localX][localY] = getPixelARGB(rgb);
                        }
                    }
                }
            }

        return pixels;
    }

    public float getPixelARGB(int pixel) {
        //int alpha = (pixel >> 24) & 0xff;
        //int red = (pixel >> 16) & 0xff;
        //int green = (pixel >> 8) & 0xff;
        float color = (pixel) & 0xff;
        return color;
    }
}
