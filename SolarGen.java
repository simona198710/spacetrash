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
public class SolarGen {

    private TiledMap map;
    private Texture tiles;
    private short[][] planetSystemPos;
   // private byte[][] planetIDs;
    private Solar solarSys;

    public SolarGen(Texture sentTiles, int h, int w, int scale, long seed) {

        long systemSeed = seed;
        int mapHeight = h;
        int mapWidth = w;
        int tileValue;
        tiles = sentTiles;

        SystemLocation location = new SystemLocation();
        location.systemSeed = systemSeed;
        solarSys = new Solar(location, true);
        planetSystemPos = solarSys.returnPlanetPos();
        //planetIDs = solarSys.returnPlanetIDs();
        //int[][] planetSystemPos = new int[h][w];


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

                        if (planetSystemPos[x][y] == 1){
                            tileValue = 3;
                        }
                        else {
                            tileValue = 1;
                        }
                        if (x == mapWidth /2 && y == mapHeight /2){
                            tileValue = 2;
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
                        else if (tileValue == 4) { //asteroids
                        }

                        //System.out.println(tx);
                        /*
                        if (l == 0){ //stars
                            TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
                            cell.setTile(new StaticTiledMapTile(splitTiles[ty][tx]));
                            layer.setCell(x, y, cell);
                        }
                        */
                        if (l == 1 && type == "Suns"){ //suns
                            TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
                            cell.setTile(new StaticTiledMapTile(splitTiles[ty][tx]));
                            layer.setCell(x, y, cell);
                        }
                        else if (l == 2 && type == "Planets"){ //planets
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

    public TiledMap returnMap() {

        return map;
    }

    public Solar returnGeneratedSystem() {

        return solarSys;
    }

    /*
    public byte[][] returnBodyIDs() {
        //newbodyIDs.toString()
        //System.out.println(bodyIDs[2087][2048]);
        return planetIDs;
    }
    */
}
