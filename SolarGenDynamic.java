package com.svamp.spacetrash;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;

/**
 * Created by Simon on 2016-08-08.
 */
public class SolarGenDynamic {

    private TiledMap map;
    private Texture tiles;
    private short[][] planetSystemPos;
    private String[][] mergedInfo;
    private Solar solarSys;
    private long systemSeed;
    private int mapHeight;
    private int mapWidth;
    private int scale;
    public int regionX = 16, regionY = 16;

    public SolarGenDynamic(Texture sentTiles, int h, int w, int sentScale, long seed, boolean noStar) {

        systemSeed = seed;
        mapHeight = h;
        mapWidth = w;
        int tileValue;
        tiles = sentTiles;
        scale = sentScale;

        SystemLocation location = new SystemLocation();
        location.systemSeed = systemSeed;
        solarSys = new Solar(location, false,regionX,regionY);
        planetSystemPos = solarSys.returnPlanetPos();
        mergedInfo = solarSys.returnMergedInfo();

        {
            //tiles = new Texture(Gdx.files.internal(tilePath));
            TextureRegion[][] splitTiles = TextureRegion.split(tiles, scale, scale);
            map = new TiledMap();
            MapLayers layers = map.getLayers();
            for (byte l = 0; l < 5; l++) {
                TiledMapTileLayer layer = new TiledMapTileLayer(h, w, scale, scale);
                for (short x = 0; x < h; x++) {
                    for (short y = 0; y < w; y++) {
                        byte ty = 0; //number of lines in tileset
                        byte tx = 0; //number of rows
                        //int tileValue = (int) (pixels[x][y] / 10);

                        if (planetSystemPos[x][y] == 1){ //planet
                            tileValue = 3;
                        }
                        else if (planetSystemPos[x][y] == 2){ //moon
                            tileValue = 4;
                        }
                        else {
                            tileValue = 1;
                        }
                        if (x == mapWidth /2 && y == mapHeight /2){
                            //tileValue = 2;
                        }
                        String type = "None";
                        //tileMap[x][y] = tileValue;
                        //System.out.println(tx);

                        if (tileValue == 1) { //stars
                            tx = 1;
                            ty = 0;
                            type = "Stars";
                        }
                        else if (tileValue == 2) { //suns
                            tx = 0;
                            ty = 0;
                            type = "Suns";
                        }
                        else if (tileValue == 3) { //planets
                            tx = 0;
                            ty = 0;
                            type = "Planets";
                        }
                        else if (tileValue == 4) {//moons
                            tx = 0;
                            ty = 0;
                            type = "Moons";
                        }

                        //System.out.println(tx);

                        if (l == 0){ //stars
                            TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
                            cell.setTile(new StaticTiledMapTile(splitTiles[ty][tx]));
                            layer.setCell(x, y, cell);
                        }
                        else if (l == 1 && type == "Suns"){ //suns
                            TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
                            cell.setTile(new StaticTiledMapTile(splitTiles[ty][tx]));
                            layer.setCell(x, y, cell);
                        }
                        else if (l == 2 && type == "Planets"){ //planets
                            TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
                            cell.setTile(new StaticTiledMapTile(splitTiles[ty][tx]));
                            layer.setCell(x, y, cell);
                        }
                        else if (l == 3 && type == "Moons"){ //planets
                            TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
                            cell.setTile(new StaticTiledMapTile(splitTiles[ty][tx]));
                            layer.setCell(x, y, cell);
                        }
                    }
                }
                layers.add(layer);
            }
        }
    }

    public SolarGenDynamic(Texture sentTiles, int h, int w, int sentScale, long seed) {

        systemSeed = seed;
        mapHeight = h;
        mapWidth = w;
        int tileValue;
        tiles = sentTiles;
        scale = sentScale;

        SystemLocation location = new SystemLocation();
        location.systemSeed = systemSeed;
        solarSys = new Solar(location, true,regionX,regionY);
        planetSystemPos = solarSys.returnPlanetPos();
        mergedInfo = solarSys.returnMergedInfo();

        {
            //tiles = new Texture(Gdx.files.internal(tilePath));
            TextureRegion[][] splitTiles = TextureRegion.split(tiles, scale, scale);
            map = new TiledMap();
            MapLayers layers = map.getLayers();
            for (byte l = 0; l < 5; l++) {
                TiledMapTileLayer layer = new TiledMapTileLayer(h, w, scale, scale);
                for (short x = 0; x < h; x++) {
                    for (short y = 0; y < w; y++) {
                        byte ty = 0; //number of lines in tileset
                        byte tx = 0; //number of rows
                        //int tileValue = (int) (pixels[x][y] / 10);

                        if (planetSystemPos[x][y] == 1){ //planet
                            tileValue = 3;
                        }
                        else if (planetSystemPos[x][y] == 2){ //moon
                            tileValue = 4;
                        }
                        else {
                            tileValue = 1;
                        }
                        if (x == mapWidth /2 && y == mapHeight /2){
                            //tileValue = 2;
                        }
                        String type = "None";
                        //tileMap[x][y] = tileValue;
                        //System.out.println(tx);

                        if (tileValue == 1) { //stars
                            tx = 1;
                            ty = 0;
                            type = "Stars";
                        }
                        else if (tileValue == 2) { //suns
                            tx = 0;
                            ty = 0;
                            type = "Suns";
                        }
                        else if (tileValue == 3) { //planets
                            tx = 0;
                            ty = 0;
                            type = "Planets";
                        }
                        else if (tileValue == 4) {//moons
                            tx = 0;
                            ty = 0;
                            type = "Moons";
                        }

                        //System.out.println(tx);

                        if (l == 0){ //stars
                            TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
                            cell.setTile(new StaticTiledMapTile(splitTiles[ty][tx]));
                            layer.setCell(x, y, cell);
                        }
                        else if (l == 1 && type == "Suns"){ //suns
                            TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
                            cell.setTile(new StaticTiledMapTile(splitTiles[ty][tx]));
                            layer.setCell(x, y, cell);
                        }
                        else if (l == 2 && type == "Planets"){ //planets
                            TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
                            cell.setTile(new StaticTiledMapTile(splitTiles[ty][tx]));
                            layer.setCell(x, y, cell);
                        }
                        else if (l == 3 && type == "Moons"){ //planets
                            TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
                            cell.setTile(new StaticTiledMapTile(splitTiles[ty][tx]));
                            layer.setCell(x, y, cell);
                        }
                    }
                }
                layers.add(layer);
            }
        }
    }

    public byte updateMap(int newRegionX, int newRegionY) {
        if (newRegionX >= 0 && newRegionY >= 0 && newRegionX <= 32 && newRegionY <= 32) {

            int tileValue;

            regionX = newRegionX;
            regionY = newRegionY;

            SystemLocation location = new SystemLocation();
            location.systemSeed = systemSeed;
            solarSys = new Solar(location, true, regionX, regionY);
            planetSystemPos = solarSys.returnPlanetPos();
            //planetIDs = solarSys.returnPlanetIDs();
            //moonIDs = solarSys.returnMoonIDs();
            // planetToMoonIDs = solarSys.returnMoonIDtoPlanetID();
            mergedInfo = solarSys.returnMergedInfo();
            //int[][] planetSystemPos = new int[h][w];
            int h = mapHeight;
            int w = mapWidth;


            {
                //tiles = new Texture(Gdx.files.internal(tilePath));
                TextureRegion[][] splitTiles = TextureRegion.split(tiles, scale, scale);
                map = new TiledMap();
                MapLayers layers = map.getLayers();
                for (byte l = 0; l < 5; l++) {
                    TiledMapTileLayer layer = new TiledMapTileLayer(h, w, scale, scale);
                    for (short x = 0; x < h; x++) {
                        for (short y = 0; y < w; y++) {
                            byte ty = 0; //number of lines in tileset
                            byte tx = 0; //number of rows
                            //int tileValue = (int) (pixels[x][y] / 10);

                            if (planetSystemPos[x][y] == 1) {
                                tileValue = 3;
                            } else if (planetSystemPos[x][y] == 2) { //moon
                                tileValue = 4;
                            } else {
                                tileValue = 1;
                            }
                            if (x == mapWidth / 2 && y == mapHeight / 2) {
                                tileValue = 2;
                            }
                            String type = "None";
                            //tileMap[x][y] = tileValue;
                            //System.out.println(tx);

                            if (tileValue == 1) { //stars
                                tx = 1;
                                ty = 0;
                                type = "Stars";
                            } else if (tileValue == 2) { //suns
                                tx = 0;
                                ty = 0;
                                type = "Suns";
                            } else if (tileValue == 3) { //planets
                                tx = 0;
                                ty = 0;
                                type = "Planets";
                            } else if (tileValue == 4) {//moons
                                tx = 0;
                                ty = 0;
                                type = "Moons";
                            }

                            //System.out.println(tx);

                            if (l == 0) { //stars
                                TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
                                cell.setTile(new StaticTiledMapTile(splitTiles[ty][tx]));
                                layer.setCell(x, y, cell);
                            } else if (l == 1 && type == "Suns") { //suns
                                TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
                                cell.setTile(new StaticTiledMapTile(splitTiles[ty][tx]));
                                layer.setCell(x, y, cell);
                            } else if (l == 2 && type == "Planets") { //planets
                                TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
                                cell.setTile(new StaticTiledMapTile(splitTiles[ty][tx]));
                                layer.setCell(x, y, cell);
                            } else if (l == 3 && type == "Moons") { //moons
                                TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
                                cell.setTile(new StaticTiledMapTile(splitTiles[ty][tx]));
                                layer.setCell(x, y, cell);
                            }
                        }
                    }
                    layers.add(layer);
                }
            }
            return 0;
        }
        else {
            if (newRegionY > 32){
                System.out.println("OUTSIDE SOLARSYSTEM UP");
                return 1;
            }
            else if (newRegionY < 0){
                System.out.println("OUTSIDE SOLARSYSTEM DOWN");
                return -1;
            }
            else if (newRegionX > 32) {
                System.out.println("OUTSIDE SOLARSYSTEM RIGHT");
                return 2;
            }
            else if (newRegionX < 0) {
                System.out.println("OUTSIDE SOLARSYSTEM LEFT");
                return -2;
            }
            return 0;
        }
    }

    public TiledMap returnMap() {

        return map;
    }

    public Solar returnGeneratedSystem() {

        return solarSys;
    }
/*
    public byte[][] returnPlanetIDs() {
        //newbodyIDs.toString()
        //System.out.println(bodyIDs[2087][2048]);
        return planetIDs;
    }

    public byte[][][] returnMoonIDs() {
        //newbodyIDs.toString()
        //System.out.println(bodyIDs[2087][2048]);
        return moonIDs;
    }

    public byte[][] returnPlanetToMoonIDs() {
        //newbodyIDs.toString()
        //System.out.println(bodyIDs[2087][2048]);
        return planetToMoonIDs;
    }
    */
public String[][] returnMergedInfo() {
    //newbodyIDs.toString()
    //System.out.println(bodyIDs[2087][2048]);
    return mergedInfo;
}
}
