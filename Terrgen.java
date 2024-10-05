package com.svamp.spacetrash;

/**
 * Created by Simon on 2016-08-04.
 */

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import texturesynthesis.TextureSynthesizer;
import texturesynthesis.TextureSynthesizerException;
import texturesynthesis.synthesizer.WangTileSynthesizer;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.PixelGrabber;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class Terrgen {

    private TiledMap map;
    private Texture tiles;
    private TerrGen2 terrgen2;
    private int[][] tileMap;
    private int[][] treeMap;
    private int[][] wallMap;
    private int[][] blockedMap;
    public int mapHeight;
    public int mapWidth;
    private String planetType;
    private long planetSeed;
    private int scale;
    private int landRegionX, landRegionY;
    public int landLocalX, landLocalY;
    WangTileSynthesizer s = new WangTileSynthesizer();

    private TextureRegion[] groundTiles;
    private TextureRegion[] hillTiles;
    private TextureRegion[] mountainTiles;

    private TextureRegion[] stoneTiles;

    public int regionX = 0, regionY = 0;
    String tilePath;
    private Image generatedTexture;
    Pixmap pixmap = new Pixmap(32, 32, Pixmap.Format.RGBA8888);

    public Terrgen(java.lang.String newTilePath, int h, int w, int newScale, long seed, int sentLandX, int sentLandY) {

        planetSeed = seed;
        mapHeight = h;
        mapWidth = w;
        tilePath = newTilePath;
        scale = newScale;
        landRegionX = sentLandX;
        landRegionY = sentLandY;
        regionX = landRegionX;
        regionY = landRegionY;

        landLocalY = mapHeight /2;
        landLocalX = mapWidth /2;

        groundTiles = generateTiles("ground (10).bmp");
        hillTiles = generateTiles("stone (4).bmp");
        mountainTiles = generateTiles("12.png");

        String[] stoneTilesPaths = new String[]{"stone (1)Alpha Test.png","stone (1)Alpha Test Rotated.png","stone (2)Alpha Test.png","stone (2)Alpha Test Rotated.png","stone (3)Alpha Test.png","stone (3)Alpha Test Rotated.png","stone (4)Alpha Test.png","stone (4)Alpha Test Rotated.png"};
        stoneTiles = dontGenerateTiles(stoneTilesPaths);
        //stoneTiles1 = dontGenerateTiles("stone (1)Alpha Test.png");
        //stoneTiles1Rotated = dontGenerateTiles("stone (1)Alpha Test Rotated.png");
       // stoneTiles2 = dontGenerateTiles("stone (2)Alpha Test.png");
        //stoneTiles2Rotated = dontGenerateTiles("stone (2)Alpha Test Rotated.png");
        //stoneTiles3 = dontGenerateTiles("stone (3)Alpha Test.png");
        //stoneTiles3Rotated = dontGenerateTiles("stone (3)Alpha Test Rotated.png");
        //stoneTiles4 = dontGenerateTiles("stone (4)Alpha Test.png");
        //stoneTiles4Rotated = dontGenerateTiles("stone (4)Alpha Test Rotated.png");


        updateMap(regionX,regionY);
    }

    public void updateMap(int sentRegionX, int sentRegionY) {

        regionX = sentRegionX;
        regionY = sentRegionY;

        Random generator = new Random(planetSeed*planetSeed);
        int featureLow = 16;
        int featureHigh = 256;
        int featureSize = (int) (generator.nextDouble() * (featureLow - featureHigh + 8)) + featureLow;

        System.out.println("Seed: " + (planetSeed));

        terrgen2 = new TerrGen2();
        //float[][] pixels = terrgen2.generate(h,w);
        float[][] pixels = terrgen2.generateSimplex(32768,(byte) 64,planetSeed, featureSize,regionX,regionY);
        int[] planetdata = calcPlanetType(planetSeed);
        if (planetdata[0] == 1 && planetdata[1] == 1 && planetdata[2] == 1 && planetdata[3] == 1){
            planetType = "Earthlike";
        }
        else if (planetdata[0] == 1 && planetdata[1] == 1 && planetdata[2] == 1 && planetdata[3] == 0){
            planetType = "SimplePlants";
        }
        else if (planetdata[0] == 1 && planetdata[1] == 1 && planetdata[2] == 0 && planetdata[3] == 0){
            planetType = "Liveable";
        }
        else if (planetdata[0] == 1 && planetdata[1] == 0 && planetdata[2] == 0 && planetdata[3] == 0){
            planetType = "Atmo";
        }
        else if (planetdata[0] == 0 && planetdata[1] == 0 && planetdata[2] == 0 && planetdata[3] == 0){
            planetType = "NoAtmo";
        }

        //for (int i = 3317; i < 3917; i++) {
        //calcPlanetType(i);
        //}

        int oceanlevel = (int)(generator.nextDouble() * (7 - 1 + 1)) + 1;
        treeMap = new int[mapWidth][mapHeight];
        wallMap = new int[mapWidth][mapHeight];
        tileMap = new int[mapWidth][mapHeight];
        blockedMap = new int[mapWidth][mapHeight];

        {
            tiles = new Texture(Gdx.files.internal(tilePath));
            TextureRegion[][] splitTiles = TextureRegion.split(tiles, scale, scale);

            map = new TiledMap();
            MapLayers layers = map.getLayers();
            for (int l = 0; l < 14; l++) {
                TiledMapTileLayer layer = new TiledMapTileLayer(mapHeight, mapWidth, scale, scale);
                for (int x = 0; x < mapHeight; x++) {
                    for (int y = 0; y < mapWidth; y++) {
                        int ty = 0; //number of lines in tileset
                        int tx = 0; //number of rows
                        int tileValue = (int) (pixels[x][y] / 10);
                        String type = "None";
                        tileMap[x][y] = tileValue;
                        //System.out.println(tx);

                        tileValue = tileValue + 2; //orkar inte ändra mappningen igen...
                        tileValue = tileValue + oceanlevel;

                        if (x == landLocalX && y == landLocalY && regionX == landRegionX && regionY == landRegionY) {
                            tileValue = 999;
                        }

                        if (tileValue == 999) {
                            tx = 0;
                            ty = 12;
                            type = "None";
                        } else if (tileValue < 10) {
                            if (planetType != "NoAtmo" && planetType != "Atmo") {
                                tx = 0;
                                ty = 30;
                                type = "Water";
                            } else if (planetType == "Atmo") {
                                tx = 5;
                                ty = 7;
                                type = "None";
                            } else {
                                tx = 3;
                                ty = 30;
                                type = "None";
                            }
                        } else if (tileValue < 11) {
                            if (planetType != "NoAtmo" && planetType != "Atmo") {
                                tx = 0;
                                ty = 30;
                                type = "Water";
                            } else if (planetType == "Atmo") {
                                tx = 5;
                                ty = 7;
                                type = "None";
                            } else {
                                tx = 3;
                                ty = 30;
                                type = "None";
                            }
                        } else if (tileValue < 12) {
                            if (planetType != "NoAtmo" && planetType != "Atmo") {
                                tx = 6;
                                ty = 23;
                                type = "Water";
                            } else if (planetType == "Atmo") {
                                tx = 5;
                                ty = 7;
                                type = "None";
                            } else {
                                tx = 3;
                                ty = 30;
                                type = "None";
                            }
                        } else if (tileValue < 13) {
                            if (planetType != "NoAtmo" && planetType != "Atmo") {
                                tx = 12;
                                ty = 23;
                                type = "Water";
                            } else if (planetType == "Atmo") {
                                tx = 5;
                                ty = 7;
                                type = "None";
                            } else {
                                tx = 3;
                                ty = 30;
                                type = "None";
                            }
                        } else if (tileValue < 14) {
                            if (planetType != "NoAtmo" && planetType != "Atmo" && planetType != "Liveable") {
                                tx = 5;
                                ty = 7;
                                type = "Beach";
                            } else if (planetType == "Atmo") {
                                tx = 5;
                                ty = 7;
                                type = "None";
                            } else {
                                tx = 3;
                                ty = 30;
                                type = "None";
                            }
                        } else if (tileValue < 15) {
                            if (planetType != "NoAtmo" && planetType != "Atmo" && planetType != "Liveable") {
                                tx = 5;
                                ty = 11;
                                type = "None";
                            } else {
                                tx = 5;
                                ty = 7;
                                type = "None";
                            }
                        } else if (tileValue < 16) {
                            if (planetType != "NoAtmo" && planetType != "Atmo" && planetType != "Liveable") {
                                tx = 5;
                                ty = 11;
                                type = "None";
                            } else {
                                tx = 5;
                                ty = 7;
                                type = "None";
                            }
                        } else if (tileValue < 17) {
                            if (planetType != "NoAtmo" && planetType != "Atmo" && planetType != "Liveable") {
                                tx = 5;
                                ty = 11;
                                type = "None";
                            } else {
                                tx = 5;
                                ty = 7;
                                type = "None";
                            }
                        } else if (tileValue < 18) {
                            if (planetType != "NoAtmo" && planetType != "Atmo" && planetType != "Liveable") {
                                tx = 5;
                                ty = 11;
                                type = "None";
                            } else {
                                tx = 5;
                                ty = 7;
                                type = "None";
                            }
                        } else if (tileValue < 19) {
                            if (planetType != "NoAtmo" && planetType != "Atmo" && planetType != "Liveable") {
                                tx = 5;
                                ty = 11;
                                type = "None";
                            } else {
                                tx = 5;
                                ty = 7;
                                type = "None";
                            }
                        } else if (tileValue < 20) {
                            if (planetType != "NoAtmo" && planetType != "Atmo" && planetType != "Liveable") {
                                tx = 5;
                                ty = 11;
                                type = "None";
                            } else {
                                tx = 5;
                                ty = 7;
                                type = "None";
                            }
                        } else if (tileValue < 21) {
                            if (planetType != "NoAtmo" && planetType != "Atmo" && planetType != "Liveable") {
                                tx = 5;
                                ty = 11;
                                type = "None";
                            } else {
                                tx = 5;
                                ty = 7;
                                type = "None";
                            }
                        } else if (tileValue < 22) {
                            if (planetType != "NoAtmo" && planetType != "Atmo" && planetType != "Liveable") {
                                tx = 5;
                                ty = 11;
                                type = "None";
                            } else {
                                tx = 5;
                                ty = 7;
                                type = "None";
                            }
                        } else if (tileValue < 23) {
                            tx = 3;
                            ty = 30;
                            type = "Hills";
                        } else if (tileValue < 24) {
                            tx = 3;
                            ty = 30;
                            type = "Hills";
                        } else if (tileValue < 25) {
                            tx = 3;
                            ty = 28;
                            type = "Mountain";
                        } else if (tileValue < 26) {
                            tx = 7;
                            ty = 31;
                            type = "Mountain";
                        } else if (tileValue < 27) {
                            tx = 7;
                            ty = 31;
                            type = "Mountain";
                        } else if (tileValue < 28) {
                            tx = 7;
                            ty = 31;
                            type = "Mountain";
                        } else if (tileValue > 28) {
                            tx = 7;
                            ty = 31;
                            type = "Mountain";
                        }

                        //System.out.println(tx);
                        if (l == 0) {
                            if (type == "Water") { //water, swamp
                                Cell cell = new Cell();
                                cell.setTile(new StaticTiledMapTile(splitTiles[ty][tx]));
                                layer.setCell(x, y, cell);
                            }
                        }
                        if (l == 1) { //grass etc
                            if (type == "None") {

                                int low = 0;
                                int high = 15;
                                int rnd = (int) (generator.nextDouble() * (high - low + 1)) + low;

                                Cell cell = new Cell();
                                //cell.setTile(new StaticTiledMapTile(splitTiles[ty][tx]));
                                cell.setTile(new StaticTiledMapTile(groundTiles[rnd]));
                                layer.setCell(x, y, cell);
                            }
                        }
                        if (l == 2) { //hills
                            if (type == "Hills") {


                                int low = 0;
                                int high = 15;
                                int rnd = (int) (generator.nextDouble() * (high - low + 1)) + low;

                                Cell cell = new Cell();
                                cell.setTile(new StaticTiledMapTile(hillTiles[rnd]));
                                layer.setCell(x, y, cell);


                                //Cell cell = new Cell();
                                //cell.setTile(new StaticTiledMapTile(splitTiles[ty][tx]));
                                //layer.setCell(x, y, cell);
                            }
                        }
                        if (l == 3) { //mountains
                            if (type == "Mountain") {

                                int low = 0;
                                int high = 15;
                                int rnd = (int) (generator.nextDouble() * (high - low + 1)) + low;

                                Cell cell = new Cell();
                                cell.setTile(new StaticTiledMapTile(mountainTiles[rnd]));
                                layer.setCell(x, y, cell);

                                //Cell cell = new Cell();
                                //cell.setTile(new StaticTiledMapTile(splitTiles[ty][tx]));
                                //layer.setCell(x, y, cell);
                            }
                        }
                        if (l == 4) { //beach
                            if (type == "Beach") {
                                Cell cell = new Cell();
                                cell.setTile(new StaticTiledMapTile(splitTiles[ty][tx]));
                                layer.setCell(x, y, cell);
                            }
                        }
                        if (l == 5) { //trees
                            if (planetType == "Earthlike") {
                                int low = 1;
                                int high = 1000;
                                int rnd = (int) (generator.nextDouble() * (high - low + 1)) + low;

                                if ((tileValue >= 14) && (tileValue <= 21)) {
                                    if (rnd < 100) {
                                        Cell cell = new Cell();
                                        cell.setTile(new StaticTiledMapTile(splitTiles[31][1]));
                                        layer.setCell(x, y, cell);
                                        treeMap[x][y] = 1;
                                    } else if (rnd < 200) {
                                        Cell cell = new Cell();
                                        cell.setTile(new StaticTiledMapTile(splitTiles[30][29]));
                                        layer.setCell(x, y, cell);
                                        treeMap[x][y] = 1;
                                    }
                                }
                                if (tileValue == 22) {
                                    if (rnd < 25) {
                                        Cell cell = new Cell();
                                        cell.setTile(new StaticTiledMapTile(splitTiles[31][1]));
                                        layer.setCell(x, y, cell);
                                        treeMap[x][y] = 1;
                                    } else if (rnd < 50) {
                                        Cell cell = new Cell();
                                        cell.setTile(new StaticTiledMapTile(splitTiles[30][29]));
                                        layer.setCell(x, y, cell);
                                        treeMap[x][y] = 1;
                                    }
                                }
                            }
                            if (planetType == "SimplePlants") {
                                int low = 1;
                                int high = 1000;
                                int rnd = (int) (generator.nextDouble() * (high - low + 1)) + low;

                                if ((tileValue >= 14) && (tileValue <= 21)) {
                                    if (rnd < 10) {
                                        Cell cell = new Cell();
                                        cell.setTile(new StaticTiledMapTile(splitTiles[31][1]));
                                        layer.setCell(x, y, cell);
                                        treeMap[x][y] = 1;
                                    } else if (rnd < 20) {
                                        Cell cell = new Cell();
                                        cell.setTile(new StaticTiledMapTile(splitTiles[30][29]));
                                        layer.setCell(x, y, cell);
                                        treeMap[x][y] = 1;
                                    }
                                }
                                if (tileValue == 22) {
                                    if (rnd < 2) {
                                        Cell cell = new Cell();
                                        cell.setTile(new StaticTiledMapTile(splitTiles[31][1]));
                                        layer.setCell(x, y, cell);
                                        treeMap[x][y] = 1;
                                    } else if (rnd < 5) {
                                        Cell cell = new Cell();
                                        cell.setTile(new StaticTiledMapTile(splitTiles[30][29]));
                                        layer.setCell(x, y, cell);
                                        treeMap[x][y] = 1;
                                    }
                                }
                            }
                        }
                        if (l == 7) { //fog of war
                            Cell cell = new Cell();
                            cell.setTile(new StaticTiledMapTile(splitTiles[14][1]));
                            layer.setCell(x, y, cell);
                        }
                        if (l == 8) { //water background
                            if (type == "Water") {
                                Cell cell = new Cell();
                                cell.setTile(new StaticTiledMapTile(splitTiles[14][1]));
                                layer.setCell(x, y, cell);
                            }
                        }
                        if (l == 9) { //grass background
                            if (type == "None") {
                                Cell cell = new Cell();
                                cell.setTile(new StaticTiledMapTile(splitTiles[14][1]));
                                layer.setCell(x, y, cell);
                            }
                        }
                        if (l == 10) { //hills background
                            if (type == "Hills") {
                                Cell cell = new Cell();
                                cell.setTile(new StaticTiledMapTile(splitTiles[14][1]));
                                layer.setCell(x, y, cell);
                            }
                        }
                        if (l == 11) { //Mountain background
                            if (type == "Mountain") {
                                Cell cell = new Cell();
                                cell.setTile(new StaticTiledMapTile(splitTiles[14][1]));
                                layer.setCell(x, y, cell);
                            }
                        }
                        if (l == 12) { //Beach background
                            if (type == "Beach") {
                                Cell cell = new Cell();
                                cell.setTile(new StaticTiledMapTile(splitTiles[14][1]));
                                layer.setCell(x, y, cell);
                            }
                        }
                        if (l == 6) { //Stones
                            if ((tileValue >= 12) && (tileValue <= 21)) {

                                int low = 1;
                                int high = 10000;
                                if ((tileValue >= 14) && (tileValue <= 17)) {
                                    high = 7500;
                                }
                                if ((tileValue >= 15) && (tileValue <= 18)) {
                                    high = 5000;
                                }
                                if ((tileValue >= 17) && (tileValue <= 19)) {
                                    high = 1000;
                                }
                                if ((tileValue >= 20) && (tileValue <= 21)) {
                                    high = 500;
                                }
                                int rnd = (int) (generator.nextDouble() * (high - low + 1)) + low;

                                if (rnd < 10) {
                                    //int rnd2 = (int) (generator.nextDouble() * (15 - 0 + 1)) + 0;

                                    Cell cell = new Cell();
                                    cell.setTile(new StaticTiledMapTile(stoneTiles[0]));
                                    cell.getTile().getProperties().put("Stone",1);
                                    layer.setCell(x, y, cell);
                                    wallMap[x][y] = 1;
                                } else if (rnd < 20) {
                                    //int rnd2 = (int) (generator.nextDouble() * (15 - 0 + 1)) + 0;

                                    Cell cell = new Cell();
                                    cell.setTile(new StaticTiledMapTile(stoneTiles[2]));
                                    cell.getTile().getProperties().put("Stone",2);
                                    layer.setCell(x, y, cell);
                                    wallMap[x][y] = 1;
                                } else if (rnd < 30) {
                                    //int rnd2 = (int) (generator.nextDouble() * (15 - 0 + 1)) + 0;

                                    Cell cell = new Cell();
                                    cell.setTile(new StaticTiledMapTile(stoneTiles[4]));
                                    cell.getTile().getProperties().put("Stone",3);
                                    layer.setCell(x, y, cell);
                                    wallMap[x][y] = 1;
                                } else if (rnd < 40) {
                                    //int rnd2 = (int) (generator.nextDouble() * (15 - 0 + 1)) + 0;

                                    Cell cell = new Cell();
                                    cell.setTile(new StaticTiledMapTile(stoneTiles[6]));
                                    cell.getTile().getProperties().put("Stone",4);
                                    layer.setCell(x, y, cell);
                                    wallMap[x][y] = 1;
                                }
                            }
                        }
                        if (l == 13) { //Colission layer
                            if (type == "Water") {
                                Cell cell = new Cell();
                                cell.setTile(new StaticTiledMapTile(splitTiles[ty][tx]));
                                layer.setCell(x, y, cell);
                                blockedMap[x][y] = 1;
                            }
                            if (type == "Mountain") {
                                Cell cell = new Cell();
                                cell.setTile(new StaticTiledMapTile(splitTiles[ty][tx]));
                                layer.setCell(x, y, cell);
                                blockedMap[x][y] = 1;
                            }
                            if (treeMap[x][y] == 1) {
                                Cell cell = new Cell();
                                cell.setTile(new StaticTiledMapTile(splitTiles[0][0]));
                                layer.setCell(x, y, cell);
                                blockedMap[x][y] = 1;
                            }
                            if (wallMap[x][y] == 1) {
                                Cell cell = new Cell();
                                cell.setTile(new StaticTiledMapTile(splitTiles[0][0]));
                                layer.setCell(x, y, cell);
                                blockedMap[x][y] = 1;
                            }
                        }
                    }
                }
                layers.add(layer);
            }
        }
    }

    public TiledMap returnMap() {

        return map;
    }

    public int[][] returnTiles() {

        return tileMap;
    }

    public int[][] returnTrees() {

        return treeMap;
    }

    private int[] calcPlanetType(long seed) {

        int[] planetInfo = new int[4];

        int low = 1;
        int high = 100;
        Random atmosphereGenerator = new Random(seed*seed);
        Random waterGenerator = new Random(seed*seed*seed);
        Random plantsGenerator = new Random(seed*seed*seed*seed);
        Random grassGenerator = new Random(seed*seed*seed*seed*seed);

        int atmosphere = (int)(atmosphereGenerator.nextDouble() * (high - low + 1)) + low;
        int water = (int)(waterGenerator.nextDouble() * (high - low + 1)) + low;
        int plants = (int)(plantsGenerator.nextDouble() * (high - low + 1)) + low;
        int grass = (int)(grassGenerator.nextDouble() * (high - low + 1)) + low;

        if (atmosphere <= 25){
            planetInfo[0] = 1;
            if (water <= 25){
                planetInfo[1] = 1;
                if (grass <= 25) {
                    planetInfo[2] = 1;
                    if (plants <= 25) {
                        planetInfo[3] = 1;
                    } else {
                        planetInfo[3] = 0;
                    }
                }
                else {
                    planetInfo[2] = 0;
                }
            }
            else{
                planetInfo[1] = 0;
            }
        }
        else{
            planetInfo[0] = 0;
        }

        //String debug = "Atmosphere: " + Integer.toString(planetInfo[0]) + " Water: " + Integer.toString(planetInfo[1]) + " Grass: " + Integer.toString(planetInfo[2])  +  " Plants: " + Integer.toString(planetInfo[3]);
        //System.out.println(debug);

        if (planetInfo[3] == 1){
            String debug2 = "Earthlike! Seed: " + seed;
            System.out.println(debug2);
        }
        else if (planetInfo[2] == 1){
            String debug2 = "Simple Life! Seed: " + seed;
            System.out.println(debug2);
        }
        else if (planetInfo[1] == 1){
            String debug2 = "Water! Seed: " + seed;
            System.out.println(debug2);
        }


        return planetInfo;
    }

    public boolean isBlocked(int x,int y) {

        if (x < 0 || y < 0 || y > mapWidth -1 || x > mapHeight -1){
            return false;
        }

        if (treeMap[x][y] == 1){ //only checks for trees atm, use blockedMap for all that blocks walking
            //String debugInfo = "X: " + Integer.toString(x) + " Y: " + Integer.toString(y);
            //System.out.println(debugInfo);
            return true;
        }
        else{
            return false;
        }
    }

    public String returnPlanetType(){
        return planetType;
    }

    public long getSeed(){
        return planetSeed;
    }

    public String returnAtmosType(){

        String atmosType = "";

        Random generator = new Random(getSeed()*getSeed());

        if (planetType == "Earthlike"){
            int low = 1;
            int high = 100;
            int rnd = (int) (generator.nextDouble() * (high - low + 1)) + low;

            if (rnd >= 50){
                atmosType = "Type 1 (Fully Breathable)";
            }
            else if (rnd <= 25){
                atmosType = "Type 2 (Somewhat Breathable)";
            }
            else{
                atmosType = "Type 3 (Not Breathable)";
            }
        }else if (planetType == "SimplePlants"){
            int low = 1;
            int high = 100;
            int rnd = (int) (generator.nextDouble() * (high - low + 1)) + low;

            if (rnd <= 25){
                atmosType = "Type 1 (Fully Breathable)";
            }
            else if (rnd >= 50){
                atmosType = "Type 2 (Somewhat Breathable)";
            }
            else{
                atmosType = "Type 3 (Not Breathable)";
            }
        }
        else if (planetType == "Liveable" || planetType == "Atmo"){
            int low = 1;
            int high = 100;
            int rnd = (int) (generator.nextDouble() * (high - low + 1)) + low;

            if (rnd <= 25){
                atmosType = "Type 2 (Somewhat Breathable)";
            }
            else if (rnd >= 50){
                atmosType = "Type 3 (Not Breathable)";
            }
            else{
                atmosType = "Type 1 (Fully Breathable)";
            }
        }
        else{
            atmosType = "Type 4 (No Atmosphere)";
        }

        return atmosType;
    }

    public int returnTemp(){

        int temp = 0;

        Random generator = new Random(getSeed()*getSeed());

        if (planetType == "Earthlike"){
            int low = 0;
            int high = 40;
            temp = (int) (generator.nextDouble() * (high - low + 1)) + low;

        }else if (planetType == "SimplePlants"){
            int low = 0;
            int high = 60;
            temp = (int) (generator.nextDouble() * (high - low + 1)) + low;

        }
        else if (planetType == "Liveable"){
            int low = 0;
            int high = 99;
            temp = (int) (generator.nextDouble() * (high - low + 1)) + low;

        }
        else if (planetType == "Atmo"){
            int low = -100;
            int high = 160;
            temp = (int) (generator.nextDouble() * (high - low + 1)) + low;

        }
        else{
            int low = -200;
            int high = 200;
            temp = (int) (generator.nextDouble() * (high - low + 1)) + low;
        }

        return temp;
    }

    public static BufferedImage getImageFromArray(final int[] bitmap, final int width, final int height) {
        final BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        final WritableRaster raster = image.getRaster();
        final int[] pixels = ((DataBufferInt) raster.getDataBuffer()).getData();
        System.arraycopy(bitmap, 0, pixels, 0, bitmap.length);
        return image;
    }

    public Pixmap getPixmapFromArray(final int[] bitmap, final int width, final int height) {
        pixmap.setColor(0, 0, 0, 0);
        pixmap.fill();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int color = bitmap[i + (j * width)];
                color = (byte) color;
                pixmap.setColor(color);
                pixmap.drawPixel(i, j);
            }
        }
        return pixmap;
    }

    private TextureRegion[] generateTiles(String path) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(new File(path));
        } catch (IOException e) {
            System.out.println("KUNDE INTE LADDA BILDEN!");
        }

        TextureRegion[] tiles = new TextureRegion[16];

        for (int i = 0; i < 16; i++) {

            try {
                generatedTexture = s.generateTexture(image, 32, 32);
            } catch (TextureSynthesizerException e) {
                System.out.println("erwegrwgrg");
            }

            int[] imgPixels = new int[generatedTexture.getWidth(null) * generatedTexture.getHeight(null)];
            try {
                PixelGrabber pg = new PixelGrabber(generatedTexture, 0, 0, generatedTexture.getWidth(null), generatedTexture.getHeight(null), imgPixels, 0, generatedTexture.getWidth(null));
                pg.grabPixels();
            } catch (Exception e) {
                e.printStackTrace();
            }

            //int[] srcPixels = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();

            //int[] generatedTexturePixels = ((DataBufferInt) bufferedImage.getData().getDataBuffer()).getData();

            Texture t = new Texture(getPixmapFromArray(imgPixels, 32, 32));
            tiles[i] = new TextureRegion(t, 0, 0, 32, 32);
        }

        return tiles;
    }

    private TextureRegion[] dontGenerateTiles(String[] paths) {

        TextureRegion[] tiles = new TextureRegion[paths.length];

        int counter = 0;

        for (String path : paths) {

            BufferedImage image = null;
            try {
                image = ImageIO.read(new File(path));
            } catch (IOException e) {
                System.out.println("KUNDE INTE LADDA BILDEN!");
            }

            int[] imgPixels = new int[image.getWidth(null) * image.getHeight(null)];
            try {
                PixelGrabber pg = new PixelGrabber(image, 0, 0, image.getWidth(null), image.getHeight(null), imgPixels, 0, image.getWidth(null));
                pg.grabPixels();
            } catch (Exception e) {
                e.printStackTrace();
            }

            //int[] srcPixels = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();

            //int[] generatedTexturePixels = ((DataBufferInt) bufferedImage.getData().getDataBuffer()).getData();

            Texture t = new Texture(getPixmapFromArray(imgPixels, 32, 32));
            tiles[counter] = new TextureRegion(t, 0, 0, 32, 32);
            counter++;
    }

        return tiles;
    }

    public TextureRegion rotateTile(TextureRegion textureReg, int degree) {

        //rotera tilsen i den roterade texturregionen (helt broken)

        double u1 = textureReg.getU();
        double u2 = textureReg.getU2();
        double v1 = textureReg.getV();
        double v2 = textureReg.getV2();

        double newu1 = u1*Math.cos(Math.toRadians(degree))+ v1*Math.sin(Math.toRadians(degree));
        double newu2 = u2*Math.cos(Math.toRadians(degree))+ v2*Math.sin(Math.toRadians(degree));

        double newv1 = v1*Math.cos(Math.toRadians(degree))- u1*Math.sin(Math.toRadians(degree));
        double newv2 = v2*Math.cos(Math.toRadians(degree))- u2*Math.sin(Math.toRadians(degree));

        System.out.println("Old U " + u1 + "New " + newu1);
        System.out.println("Old U " + u2 + "New " + newu2);
        System.out.println("Old V " + v1 + "New " + newv1);
        System.out.println("Old U " + v2 + "New " + newv2);

        //textureReg.setU((float)newu1);
        textureReg.setU((float)newv2);
        //textureReg.setV((float)newv1);
        textureReg.setV((float)newu2);
        //splitTilesRotated[row][col].setU2();
        //splitTilesRotated[row][col].setV2();

        return textureReg;

    }

    public TiledMapTileLayer.Cell swapToRotatedTile(TiledMapTileLayer.Cell orginalCell,String tileType, Object tileID,boolean restore) {

        //System.out.println(tileID.toString());

        if (tileType == "Stone") {
            if (tileID.toString().equals("1")) {
                if (restore == false) {
                    //System.out.println("Actually changed cell " +tileID.toString());
                    TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
                    cell.setTile(new StaticTiledMapTile(stoneTiles[1]));
                    cell.getTile().getProperties().put(tileType, tileID.toString());
                    return cell;
                }
                if (restore == true) {
                    //System.out.println("Actually restored cell " +tileID.toString());
                    TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
                    cell.setTile(new StaticTiledMapTile(stoneTiles[0]));
                    cell.getTile().getProperties().put(tileType, tileID.toString());
                    return cell;
                }
            }
            else if (tileID.toString().equals("2")) {
                if (restore == false) {
                    //System.out.println("Actually changed cell " +tileID.toString());
                    TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
                    cell.setTile(new StaticTiledMapTile(stoneTiles[3]));
                    cell.getTile().getProperties().put(tileType, tileID.toString());
                    return cell;
                }
                if (restore == true) {
                    //System.out.println("Actually restored cell " +tileID.toString());
                    TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
                    cell.setTile(new StaticTiledMapTile(stoneTiles[2]));
                    cell.getTile().getProperties().put(tileType, tileID.toString());
                    return cell;
                }
            }
            else if (tileID.toString().equals("3")) {
                if (restore == false) {
                    //System.out.println("Actually changed cell " +tileID.toString());
                    TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
                    cell.setTile(new StaticTiledMapTile(stoneTiles[5]));
                    cell.getTile().getProperties().put(tileType, tileID.toString());
                    return cell;
                }
                if (restore == true) {
                    //System.out.println("Actually restored cell " +tileID.toString());
                    TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
                    cell.setTile(new StaticTiledMapTile(stoneTiles[4]));
                    cell.getTile().getProperties().put(tileType, tileID.toString());
                    return cell;
                }
            }
            else if (tileID.toString().equals("4")) {
                if (restore == false) {
                    //System.out.println("Actually changed cell " + tileID.toString());
                    TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
                    cell.setTile(new StaticTiledMapTile(stoneTiles[7]));
                    cell.getTile().getProperties().put(tileType, tileID.toString());
                    return cell;
                }
                if (restore == true) {
                    //System.out.println("Actually restored cell "+ tileID.toString());
                    TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
                    cell.setTile(new StaticTiledMapTile(stoneTiles[6]));
                    cell.getTile().getProperties().put(tileType, tileID.toString());
                    return cell;
                }
            }
        }

        return orginalCell;
    }

}
