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
public class PlanetSystem {

    private TiledMap map;
    private Texture tiles;
    private int[][] planetPos;
    private byte[][] bodyIDs;
    private Solar planetSystem;

    public PlanetSystem(java.lang.String tilePath, int h, int w, int scale, Solar solarSystem, int planetID) {

        int mapHeight = h;
        int mapWidth = w;
        int tileValue;
        planetPos = new int[w][h];
        bodyIDs = new byte[w][h];

        planetSystem = solarSystem;

        //System.out.println(planetSystem.name);

        int counter = 1;
        for(Solar moon : planetSystem.children.get(planetID).children){
            int centerX = h / 2;
            int centerY = h / 2;

            double systemScale = 50;
            double moonxoff = moon.pos.x;
            double moonyoff = moon.pos.y;

            long moonx = centerX + (long) (moonxoff * systemScale);
            long moony = centerY + (long) (moonyoff * systemScale);

            long newMoonPosX = moonx;
            long newMoonPosY = moony;
            if (moon.orbitAngle < 0){
                //newMoonPosX = -newMoonPosX;
            }

            newMoonPosX = (newMoonPosX / 1000000) + centerX;
            System.out.println("Moon X: " + (newMoonPosX));
            System.out.println("Moon Y: " + (newMoonPosY));
            planetPos[(int) newMoonPosX][(int) newMoonPosY] = 1;
            bodyIDs[(int) newMoonPosX][(int) newMoonPosY] = (byte)counter;
            counter = counter +1;
        }

        {
            tiles = new Texture(Gdx.files.internal(tilePath));
            TextureRegion[][] splitTiles = TextureRegion.split(tiles, scale, scale);
            map = new TiledMap();
            MapLayers layers = map.getLayers();
            for (int l = 0; l < 5; l++) {
                TiledMapTileLayer layer = new TiledMapTileLayer(h, w, scale, scale);
                //System.out.println("Doing layer: " + (l));
                for (int x = 0; x < h; x++) {
                    for (int y = 0; y < w; y++) {
                        int ty = 0; //number of lines in tileset
                        int tx = 0; //number of rows
                        //int tileValue = (int) (pixels[x][y] / 10);

                        if (planetPos[x][y] == 1){
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
                        if (l == 0 && type == "Stars"){ //stars
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

    public byte[][] returnBodyIDs() {
        //newbodyIDs.toString()}
        return bodyIDs;
    }
}
