package com.svamp.spacetrash;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;

/**
 * Created by Simon on 2016-08-14.
 */
public class GalaxyPainter {

    private TiledMap map;
    private Texture tiles;
    private byte[][] starPos;
    public int mapHeight;
    public int mapWidth;
    private long galaxySeed;
    private int scale;
    public int regionX, regionY;
    private GalaxyGen galaxy;

    public GalaxyPainter(Texture sentTiles, int h, int w, int newScale, GalaxyGen sentGalaxy) {

        galaxySeed = sentGalaxy.getSeed();
        mapHeight = h;
        mapWidth = w;
        tiles = sentTiles;
        scale = newScale;
        galaxy = sentGalaxy;

        updateMap(galaxy.locationX,galaxy.locationY);
    }

    public void updateMap(int newRegionX, int newRegionY) {
        int tileValue;

        regionX = newRegionX;
        regionY = newRegionY;
        //galaxy = new GalaxyGen(galaxySeed,regionX,regionY);
        starPos = galaxy.getGalaxyData();

        {
            //tiles = new Texture(Gdx.files.internal(tilePath));
            TextureRegion[][] splitTiles = TextureRegion.split(tiles, scale, scale);
            map = new TiledMap();
            MapLayers layers = map.getLayers();
            for (byte l = 0; l < 2; l++) {
                TiledMapTileLayer layer = new TiledMapTileLayer(mapHeight, mapWidth, scale, scale);
                for (short x = 0; x < mapHeight; x++) {
                    for (short y = 0; y < mapWidth; y++) {
                        byte ty = 0; //number of lines in tileset
                        byte tx = 0; //number of rows
                        //int tileValue = (int) (pixels[x][y] / 10);

                        if (starPos[x][y] == 1){
                            tileValue = 1;
                        }
                        else {
                            tileValue = 0;
                        }
                        String type = "None";
                        //tileMap[x][y] = tileValue;
                        //System.out.println(tx);

                        if (tileValue == 1) { //stars
                            tx = 0;
                            ty = 0;
                            type = "Stars";
                        }

                        //System.out.println(tx);

                        if (l == 0 && type == "Stars"){ //stars
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

    public GalaxyGen returnGeneratedGalaxy() {

        return galaxy;
    }
}
