package com.svamp.spacetrash;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;
import java.util.Locale;

import static com.badlogic.gdx.graphics.Color.*;
import static com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell.*;

public class PlanetScreen implements Screen {

    private TiledMap map;

    private OrthogonalTiledMapRenderer renderer1;
    private OrthogonalTiledMapRenderer renderer1b;

    private OrthogonalTiledMapRenderer renderer2;
    private OrthogonalTiledMapRenderer renderer2b;

    private OrthogonalTiledMapRenderer renderer3;
    private OrthogonalTiledMapRenderer renderer3b;

    private OrthogonalTiledMapRenderer renderer4;
    private OrthogonalTiledMapRenderer renderer4b;

    private OrthogonalTiledMapRenderer renderer5;
    private OrthogonalTiledMapRenderer renderer5b;

    private OrthogonalTiledMapRenderer renderer6;
    private OrthogonalTiledMapRenderer renderer7;
    private OrthogonalTiledMapRenderer fogOfWar;

    public OrthographicCamera camera;
    private TextureAtlas playerAtlas;
    public Terrgen terrgenerator;
    private SolarSystemScreen solarScreen;

    private SpriteBatch cordinatesBatch;
    private BitmapFont bfont = new BitmapFont();

    private player player;
    private long seed;

    private int playerTileX;
    private int playerTileY;
    private String playerFacing;

    private Utils.GrassColors grassColor;
    private Utils.TreeColors treeColor;
    private Utils.MountainColors mountainColor;
    private Utils.SandColors sandColor;
    private Utils.WaterColors waterColor;

    public int tilesetSize = 32;
    public String tileset = "SvampPackTransparent.png";
    private String direction = "0";

    private Texture tiles;

    private long startTime = 0;

    private Game g;

    public PlanetScreen(Game game, long customSeed, SolarSystemScreen sentSolarScreen)
    {
        seed = customSeed;
        tiles = new Texture(Gdx.files.internal(tileset));
        solarScreen = sentSolarScreen;
        g = game;
    }

    //@Override
    public void show () {
        startTime = TimeUtils.nanoTime();

        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        cordinatesBatch = new SpriteBatch();

        grassColor = Utils.getGrassColor(seed);
        treeColor = Utils.getTreeColor(seed);
        sandColor = Utils.getSandColor(seed);
        mountainColor = Utils.getMountainColor(seed);
        waterColor = Utils.getWaterColor(seed);

        //map = loader.load("untitled.tmx");
        terrgenerator = new Terrgen(tileset,512,512,tilesetSize,seed,0,0);
        updateMap();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, (w / h) * 480, 720);
        camera.update();


        //player movement animation stuff
        playerAtlas = new TextureAtlas("playerAnim.pack");
        Animation up, left, right, down;
        up = new Animation(1 / 6f, playerAtlas.findRegions("u"));
        left = new Animation(1 / 6f, playerAtlas.findRegions("l"));
        right = new Animation(1 / 6f, playerAtlas.findRegions("r"));
        down = new Animation(1 / 6f, playerAtlas.findRegions("d"));

        //create player
        player = new player(up, left, right, down, (TiledMapTileLayer) map.getLayers().get(13),this);
        player.setPosition((terrgenerator.landLocalX) * 32, (terrgenerator.landLocalY) * 32);

        Gdx.input.setInputProcessor(player);

    }

    private void updateMap(){
        map = terrgenerator.returnMap();

        updateRenderers();
    }

    private void updateRenderers(){

        renderer1 = new OrthogonalTiledMapRenderer(map);
        renderer1b = new OrthogonalTiledMapRenderer(map);

        renderer2 = new OrthogonalTiledMapRenderer(map);
        renderer2b = new OrthogonalTiledMapRenderer(map);

        renderer3 = new OrthogonalTiledMapRenderer(map);
        renderer3b = new OrthogonalTiledMapRenderer(map);

        renderer4 = new OrthogonalTiledMapRenderer(map);
        renderer4b = new OrthogonalTiledMapRenderer(map);

        renderer5 = new OrthogonalTiledMapRenderer(map);
        renderer5b = new OrthogonalTiledMapRenderer(map);

        renderer6 = new OrthogonalTiledMapRenderer(map);
        renderer7 = new OrthogonalTiledMapRenderer(map);
        fogOfWar = new OrthogonalTiledMapRenderer(map);
    }

    private void updatePlayer(){
        if (direction == "0"){
            player.setPosition((player.getX() / 32) * 32, (512 - 512) * 32);
        }
        else if (direction == "180"){
            player.setPosition((player.getX() / 32) * 32, (512 - 0) * 32);
        }
        else if (direction == "90"){
            player.setPosition(0 * 32, (512 - (player.getY() / 32)) * 32);
        }
        else if (direction == "270"){
            player.setPosition(512 * 32, (512 - (player.getY() / 32)) * 32);
        }
    }

    @Override
    public void render (float delta) {

        double oxygenRate = 0.001;

        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        /*
        if (player.getFacing() == "0") {
            camera.position.set(player.getX() + player.getWidth() / 2, player.getY() + player.getHeight() / 2 + (32 * 9), 0);
        }else if (player.getFacing() == "180") {
            camera.position.set(player.getX() + player.getWidth() / 2, player.getY() + player.getHeight() / 2 - (32 * 9), 0);
        }
        else if (player.getFacing() == "90") {
            camera.position.set(player.getX() + player.getWidth() / 2  + (32 * 9), player.getY() + player.getHeight() / 2, 0);
        }
        else if (player.getFacing() == "270") {
            camera.position.set(player.getX() + player.getWidth() / 2 - (32 * 9), player.getY() + player.getHeight() / 2, 0);
        }
        else if (player.getFacing() == "45") { //fungerar
            camera.position.set(player.getX() - player.getWidth() / 2  + (32 * 9) - (player.getWidth() *2) + 12, player.getY() - player.getHeight() / 2 + (32 * 9) - (player.getHeight() *2) + 12, 0);
        }
        else if (player.getFacing() == "135") { //fungerar
            camera.position.set(player.getX() + player.getWidth() / 2 + (32 * 6) - 4 + (player.getWidth() / 2), player.getY() + player.getHeight() / 2 - (32 * 6) + 4 - (player.getHeight() / 2), 0);
        }
        else if (player.getFacing() == "225") { //fungerar
            camera.position.set(player.getX() + player.getWidth() / 2 + (32 / 6) - (32 * 6) - 1 - (player.getWidth() / 2), player.getY() - player.getHeight() / 2 - (32 / 6) - (32 * 6) + 9 + (player.getHeight() / 2), 0);
        }
        else if (player.getFacing() == "315") { //fungerar
            camera.position.set(player.getX() + player.getWidth() / 2 + (32 / 9) - (32 * 6) + 1 - (player.getWidth() / 2),  player.getY() + player.getHeight() / 2 + (32 / 9) + (32 * 6) - 7 + (player.getHeight() / 2), 0);
        }
        */
        camera.position.set(player.getX() + player.getWidth() / 2, player.getY() + player.getHeight() / 2, 0);
        camera.update();

        renderer1.setView(camera);
        renderer1b.setView(camera);

        renderer2.setView(camera);
        renderer2b.setView(camera);

        renderer3.setView(camera);
        renderer3b.setView(camera);

        renderer4.setView(camera);
        renderer4b.setView(camera);

        renderer5.setView(camera);
        renderer5b.setView(camera);

        renderer6.setView(camera);
        renderer7.setView(camera);
        fogOfWar.setView(camera);

        int[] layersToRender1 = new int[1];
        int[] layersToRender1b = new int[1];

        int[] layersToRender2 = new int[1];
        int[] layersToRender2b = new int[1];

        int[] layersToRender3 = new int[1];
        int[] layersToRender3b = new int[1];

        int[] layersToRender4 = new int[1];
        int[] layersToRender4b = new int[1];

        int[] layersToRender5 = new int[1];
        int[] layersToRender5b = new int[1];

        int[] layersToRender6 = new int[1];
        int[] layersToRender7 = new int[1];
        int[] fogOfWarLayersToRender = new int[1];

        layersToRender1[0] = 0;
        layersToRender1b[0] = 8;

        layersToRender2[0] = 1;
        layersToRender2b[0] = 9;

        layersToRender3[0] = 2;
        layersToRender3b[0] = 10;

        layersToRender4[0] = 3;
        layersToRender4b[0] = 11;

        layersToRender5[0] = 4;
        layersToRender5b[0] = 12;

        layersToRender6[0] = 5;
        layersToRender7[0] = 6;
        fogOfWarLayersToRender[0] = 7;


        //update FoW if player has moved or turned
        if (playerTileX != (int)player.getX() / tilesetSize || playerTileY != (int)player.getY() / tilesetSize || player.getFacing() != playerFacing) {
            playerTileX = (int)player.getX() / tilesetSize;
            playerTileY = (int)player.getY() / tilesetSize;

            playerFacing = player.getFacing();

            //System.out.flush();

            updateFoWPositiveRight(19);
            updateFoWPositiveLeft(19);
            updateFoWNegativeLeft(19);
            updateFoWNegativeRight(19);

            if (playerFacing == "0"){
                calcFowUpRight(19);
                calcFowUpLeft(19);
                calcFowDownRight(19);
                calcFowDownLeft(19);
            }
            if (playerFacing == "45"){
                calcFowUpRight(19);
                calcFowUpLeft(19);
                calcFowDownRight(19);
                calcFowDownLeft(19);
            }
            else if(playerFacing == "90"){
                calcFowUpRight(19);
                calcFowUpLeft(19);
                calcFowDownRight(19);
                calcFowDownLeft(19);
            }
            else if(playerFacing == "135"){
                calcFowUpRight(19);
                calcFowUpLeft(19);
                calcFowDownRight(19);
                calcFowDownLeft(19);
            }
            else if(playerFacing == "180"){
                calcFowUpRight(19);
                calcFowUpLeft(19);
                calcFowDownRight(19);
                calcFowDownLeft(19);
            }
            else if(playerFacing == "225"){
                calcFowUpRight(19);
                calcFowUpLeft(19);
                calcFowDownRight(19);
                calcFowDownLeft(19);
            }
            else if(playerFacing == "270"){
                calcFowUpRight(19);
                calcFowUpLeft(19);
                calcFowDownRight(19);
                calcFowDownLeft(19);
            }
            else if(playerFacing == "315"){
                calcFowUpRight(19);
                calcFowUpLeft(19);
                calcFowDownRight(19);
                calcFowDownLeft(19);
            }

            oxygenRate = 0.01;
        }

        if (terrgenerator.returnAtmosType() != "Type 1 (Fully Breathable)"){
            if (TimeUtils.timeSinceNanos(startTime) > 1000000000) {
                player.setOxygen(player.getOxygen() - oxygenRate);
                startTime = TimeUtils.nanoTime();
            }
        }

        renderer1.getBatch().setColor(Colors.get(waterColor.toString())); //water
        renderer1b.getBatch().setColor(Colors.get(waterColor.toString())); //water

        renderer2.getBatch().setColor(Colors.get(grassColor.toString())); //grass
        renderer2b.getBatch().setColor(Colors.get(grassColor.toString())); //grass

        if (terrgenerator.returnPlanetType() == "Earthlike" ||  terrgenerator.returnPlanetType() == "SimplePlants" ||  terrgenerator.returnPlanetType() == "NoAtmo") {
            renderer3.getBatch().setColor(Colors.get(mountainColor.toString())); //hills
            renderer3b.getBatch().setColor(Colors.get(mountainColor.toString())); //hills

            renderer4.getBatch().setColor(Colors.get(mountainColor.toString())); //mountains
            renderer4b.getBatch().setColor(Colors.get(mountainColor.toString())); //mountains
        }
        else{
            renderer3.getBatch().setColor(Colors.get(grassColor.toString())); //hills
            renderer3b.getBatch().setColor(Colors.get(grassColor.toString())); //hills

            renderer4.getBatch().setColor(Colors.get(grassColor.toString())); //mountains
            renderer4b.getBatch().setColor(Colors.get(grassColor.toString())); //mountains
        }
        renderer5.getBatch().setColor(Colors.get(sandColor.toString())); //beach
        renderer5b.getBatch().setColor(Colors.get(sandColor.toString())); //beach

        renderer6.getBatch().setColor(Colors.get(treeColor.toString())); //trees
        renderer7.getBatch().setColor(Colors.get(grassColor.toString())); //stones
        fogOfWar.getBatch().setColor(BLACK); //FoW

        renderer1b.render(layersToRender1b);
        renderer1.render(layersToRender1);

        renderer2b.render(layersToRender2b);
        renderer2.render(layersToRender2);

        renderer3b.render(layersToRender3b);
        renderer3.render(layersToRender3);

        renderer4b.render(layersToRender4b);
        renderer4.render(layersToRender4);

        renderer5b.render(layersToRender5b);
        renderer5.render(layersToRender5);

        renderer6.render(layersToRender6);
        renderer7.render(layersToRender7);
        fogOfWar.render(fogOfWarLayersToRender);

        String debugText = "X: " + playerTileX + " Y: " + playerTileY + " FPS: " + Gdx.graphics.getFramesPerSecond() ;

        double ox = player.getOxygen();
        String oxygenFormated = String.format(Locale.US,"%.02f", ox);
        String infoBar = player.getFacing() + "HP: " + player.getHP() + "    Oxygen: " + oxygenFormated + "     Atmosphere: " + terrgenerator.returnAtmosType() + "  Temp: " + terrgenerator.returnTemp() + "c";

        cordinatesBatch.begin();
        bfont.draw(cordinatesBatch, infoBar, Gdx.graphics.getWidth() / 2 - 300, Gdx.graphics.getHeight() / 2 + 230);
        cordinatesBatch.end();

        player.draw(renderer1.getBatch(),renderer2.getBatch(),renderer3.getBatch(),renderer4.getBatch(),renderer5.getBatch(),renderer6.getBatch(),renderer7.getBatch());
    }

    public void hide(){

    }

    public void pause(){

    }

    public void resume(){

    }

    public void resize(int width, int height){

    }

    public void dispose(){
        map.dispose();
        cordinatesBatch.dispose();
        playerAtlas.dispose();
        renderer1.dispose();
        renderer2.dispose();
        renderer3.dispose();
        renderer4.dispose();
        renderer5.dispose();
        renderer6.dispose();
    }

    private void calcFowUp(int y){

        for (int i = playerTileY; i < y + playerTileY; i++) {
            for (int j = playerTileX; j < 1 + playerTileX; j++) {

                if (j > 0 || i > 0 || i < terrgenerator.mapWidth - 1 || j < terrgenerator.mapHeight - 1) {
                    TiledMapTileLayer fogOfWarLayer = (TiledMapTileLayer) map.getLayers().get(7);
                    TextureRegion[][] splitTiles = TextureRegion.split(tiles, tilesetSize, tilesetSize);

                    if (Utils.isVisibleUp(terrgenerator, (int) player.getX() / tilesetSize, (int) player.getY() / tilesetSize, j, i)) {
                        if (fogOfWarLayer.getCell(j, i) != null) {
                            fogOfWarLayer.getCell(j, i).setTile(null);
                        }
                    } else {
                        if (fogOfWarLayer.getCell(j, i) != null) {
                            fogOfWarLayer.getCell(j, i).setTile(new StaticTiledMapTile(splitTiles[14][1]));
                        }
                    }
                }
            }
        }

    }

    private void calcFowDown(int y){

        for (int i = playerTileY; i > playerTileY - y; i--) {
            for (int j = playerTileX; j < 1 + playerTileX; j++) {

                if (j > 0 || i > 0 || i < terrgenerator.mapWidth - 1 || j < terrgenerator.mapHeight - 1) {
                    TiledMapTileLayer fogOfWarLayer = (TiledMapTileLayer) map.getLayers().get(7);
                    TextureRegion[][] splitTiles = TextureRegion.split(tiles, tilesetSize, tilesetSize);

                    if (Utils.isVisibleDown(terrgenerator, (int) player.getX() / tilesetSize, (int) player.getY() / tilesetSize, j, i)) {
                        if (fogOfWarLayer.getCell(j, i) != null) {
                            fogOfWarLayer.getCell(j, i).setTile(null);
                        }
                    } else {
                        if (fogOfWarLayer.getCell(j, i) != null) {
                            fogOfWarLayer.getCell(j, i).setTile(new StaticTiledMapTile(splitTiles[14][1]));
                        }
                    }
                }
            }
        }
    }

    private void calcFowRight(int x){

        for (int i = playerTileY; i > playerTileY - 1; i--) {
            for (int j = playerTileX; j < playerTileX + x; j++) {

                if (j > 0 || i > 0 || i < terrgenerator.mapWidth - 1 || j < terrgenerator.mapHeight - 1) {
                    TiledMapTileLayer fogOfWarLayer = (TiledMapTileLayer) map.getLayers().get(7);
                    TextureRegion[][] splitTiles = TextureRegion.split(tiles, tilesetSize, tilesetSize);

                    if (Utils.isVisibleRight(terrgenerator, (int) player.getX() / tilesetSize, (int) player.getY() / tilesetSize, j, i)) {
                        if (fogOfWarLayer.getCell(j, i) != null) {
                            fogOfWarLayer.getCell(j, i).setTile(null);
                        }
                    } else {
                        if (fogOfWarLayer.getCell(j, i) != null) {
                            fogOfWarLayer.getCell(j, i).setTile(new StaticTiledMapTile(splitTiles[14][1]));
                        }
                    }
                }
            }
        }
    }

    private void calcFowLeft(int x){

        for (int i = playerTileY; i > playerTileY - 1; i--) {
            for (int j = playerTileX; j > playerTileX - x; j--) {

                if (j > 0 || i > 0 || i < terrgenerator.mapWidth - 1 || j < terrgenerator.mapHeight - 1) {
                    TiledMapTileLayer fogOfWarLayer = (TiledMapTileLayer) map.getLayers().get(7);
                    TextureRegion[][] splitTiles = TextureRegion.split(tiles, tilesetSize, tilesetSize);

                    //String debugInfo = "Checking X: " + Integer.toString(j) + " Y: " + Integer.toString(i);
                    //System.out.println(debugInfo);

                    if (Utils.isVisibleLeft(terrgenerator, (int) player.getX() / tilesetSize, (int) player.getY() / tilesetSize, j, i)) {
                        if (fogOfWarLayer.getCell(j, i) != null) {
                            fogOfWarLayer.getCell(j, i).setTile(null);
                        }
                    } else {
                        if (fogOfWarLayer.getCell(j, i) != null) {
                            fogOfWarLayer.getCell(j, i).setTile(new StaticTiledMapTile(splitTiles[14][1]));
                        }
                    }
                }
            }
        }
    }

    private void calcFowDownLeft(int x){

        for (int i = playerTileY; i > playerTileY - x; i--) {
            for (int j = playerTileX; j > playerTileX - x; j--) {

                if (j > 0 || i > 0 || i < terrgenerator.mapWidth - 1 || j < terrgenerator.mapHeight - 1) {
                    TiledMapTileLayer fogOfWarLayer = (TiledMapTileLayer) map.getLayers().get(7);
                    TextureRegion[][] splitTiles = TextureRegion.split(tiles, tilesetSize, tilesetSize);

                    //String debugInfo = "Checking X: " + Integer.toString(j) + " Y: " + Integer.toString(i);
                   // System.out.println(debugInfo);

                    if (Utils.isVisibleLeft(terrgenerator, (int) player.getX() / tilesetSize, (int) player.getY() / tilesetSize, j, i)) {
                        if (fogOfWarLayer.getCell(j, i) != null) {
                            fogOfWarLayer.getCell(j, i).setTile(null);
                        }
                    } else {
                        if (fogOfWarLayer.getCell(j, i) != null) {
                            fogOfWarLayer.getCell(j, i).setTile(new StaticTiledMapTile(splitTiles[14][1]));
                        }
                    }
                }
            }
        }
    }

    private void calcFowDownRight(int x){

        for (int i = playerTileY; i > playerTileY - x; i--) {
            for (int j = playerTileX; j < playerTileX + x; j++) {

                if (j > 0 || i > 0 || i < terrgenerator.mapWidth - 1 || j < terrgenerator.mapHeight - 1) {
                    TiledMapTileLayer fogOfWarLayer = (TiledMapTileLayer) map.getLayers().get(7);
                    TextureRegion[][] splitTiles = TextureRegion.split(tiles, tilesetSize, tilesetSize);

                    //String debugInfo = "Checking X: " + Integer.toString(j) + " Y: " + Integer.toString(i);
                    //System.out.println(debugInfo);

                    if (Utils.isVisibleDunno(terrgenerator, (int) player.getX() / tilesetSize, (int) player.getY() / tilesetSize, j, i)) {
                        if (fogOfWarLayer.getCell(j, i) != null) {
                            fogOfWarLayer.getCell(j, i).setTile(null);
                        }
                    } else {
                        if (fogOfWarLayer.getCell(j, i) != null) {
                            fogOfWarLayer.getCell(j, i).setTile(new StaticTiledMapTile(splitTiles[14][1]));
                        }
                    }
                }
            }
        }
    }

    private void calcFowUpRight(int x){

        for (int i = playerTileY; i < x + playerTileY; i++) {
            for (int j = playerTileX; j < playerTileX + x; j++) {

                if (j > 0 || i > 0 || i < terrgenerator.mapWidth - 1 || j < terrgenerator.mapHeight - 1) {
                    TiledMapTileLayer fogOfWarLayer = (TiledMapTileLayer) map.getLayers().get(7);
                    TextureRegion[][] splitTiles = TextureRegion.split(tiles, tilesetSize, tilesetSize);

                    //String debugInfo = "Checking X: " + Integer.toString(j) + " Y: " + Integer.toString(i);
                    //System.out.println(debugInfo);

                    if (Utils.isVisibleRight(terrgenerator, (int) player.getX() / tilesetSize, (int) player.getY() / tilesetSize, j, i)) {
                        if (fogOfWarLayer.getCell(j, i) != null) {
                            fogOfWarLayer.getCell(j, i).setTile(null);
                        }
                    } else {
                        if (fogOfWarLayer.getCell(j, i) != null) {
                            fogOfWarLayer.getCell(j, i).setTile(new StaticTiledMapTile(splitTiles[14][1]));
                        }
                    }
                }
            }
        }
    }

    private void calcFowUpLeft(int x){

        for (int i = playerTileY; i < x + playerTileY; i++) {
            for (int j = playerTileX; j > playerTileX - x; j--) {

                if (j > 0 || i > 0 || i < terrgenerator.mapWidth - 1 || j < terrgenerator.mapHeight - 1) {
                    TiledMapTileLayer fogOfWarLayer = (TiledMapTileLayer) map.getLayers().get(7);
                    TextureRegion[][] splitTiles = TextureRegion.split(tiles, tilesetSize, tilesetSize);

                    //String debugInfo = "Checking X: " + Integer.toString(j) + " Y: " + Integer.toString(i);
                    //System.out.println(debugInfo);

                    if (Utils.isVisibleUp(terrgenerator, (int) player.getX() / tilesetSize, (int) player.getY() / tilesetSize, j, i)) {
                        if (fogOfWarLayer.getCell(j, i) != null) {
                            fogOfWarLayer.getCell(j, i).setTile(null);
                        }
                    } else {
                        if (fogOfWarLayer.getCell(j, i) != null) {
                            fogOfWarLayer.getCell(j, i).setTile(new StaticTiledMapTile(splitTiles[14][1]));
                        }
                    }
                }
            }
        }
    }

    private void updateFoWPositiveLeft(int range) {

       //int j = playerTileY;
       ///int  i = playerTileX;

        for (int i = playerTileY; i < range + playerTileY; i++) {
            for (int j = playerTileX; j > playerTileX - range; j--) {

                //String debugInfo = "Checking X: " + Integer.toString(j) + " Y: " + Integer.toString(i);
                //System.out.println(debugInfo);

                if (j > 0 || i > 0 || i < terrgenerator.mapWidth - 1 || j < terrgenerator.mapHeight - 1) {
                    TiledMapTileLayer fogOfWarLayer = (TiledMapTileLayer) map.getLayers().get(7);
                    TextureRegion[][] splitTiles = TextureRegion.split(tiles, tilesetSize, tilesetSize);
                    if (fogOfWarLayer.getCell(j, i) != null) {
                        fogOfWarLayer.getCell(j, i).setTile(new StaticTiledMapTile(splitTiles[14][1]));
                    }
                }
            }
        }
    }

    private void updateFoWPositiveRight(int range) {

        //int j = playerTileY;
        ///int  i = playerTileX;

        for (int i = playerTileY; i > playerTileY - range; i--) {
            for (int j = playerTileX; j < playerTileX + range; j++) {

                //String debugInfo = "Checking X: " + Integer.toString(j) + " Y: " + Integer.toString(i);
                //System.out.println(debugInfo);

                if (j > 0 || i > 0 || i < terrgenerator.mapWidth - 1 || j < terrgenerator.mapHeight - 1) {
                    TiledMapTileLayer fogOfWarLayer = (TiledMapTileLayer) map.getLayers().get(7);
                    TextureRegion[][] splitTiles = TextureRegion.split(tiles, tilesetSize, tilesetSize);
                    if (fogOfWarLayer.getCell(j, i) != null) {
                        fogOfWarLayer.getCell(j, i).setTile(new StaticTiledMapTile(splitTiles[14][1]));
                    }
                }
            }
        }
    }

    private void updateFoWNegativeLeft(int range) {

        //int j = playerTileY;
        ///int  i = playerTileX;

        for (int i = playerTileY; i > playerTileY - range; i--) {
            for (int j = playerTileX; j > playerTileX - range; j--) {

                //String debugInfo = "Checking X: " + Integer.toString(j) + " Y: " + Integer.toString(i);
                //System.out.println(debugInfo);

                if (j > 0 || i > 0 || i < terrgenerator.mapWidth - 1 || j < terrgenerator.mapHeight - 1) {
                    TiledMapTileLayer fogOfWarLayer = (TiledMapTileLayer) map.getLayers().get(7);
                    TextureRegion[][] splitTiles = TextureRegion.split(tiles, tilesetSize, tilesetSize);
                    if (fogOfWarLayer.getCell(j, i) != null) {
                        fogOfWarLayer.getCell(j, i).setTile(new StaticTiledMapTile(splitTiles[14][1]));
                    }
                }
            }
        }
    }

    private void updateFoWNegativeRight(int range) {

        //int j = playerTileY;
        ///int  i = playerTileX;

        for (int i = playerTileY; i < range + playerTileY; i++) {
            for (int j = playerTileX; j < playerTileX + range; j++) {

                //String debugInfo = "Checking X: " + Integer.toString(j) + " Y: " + Integer.toString(i);
                //System.out.println(debugInfo);

                if (j > 0 || i > 0 || i < terrgenerator.mapWidth - 1 || j < terrgenerator.mapHeight - 1) {
                    TiledMapTileLayer fogOfWarLayer = (TiledMapTileLayer) map.getLayers().get(7);
                    TextureRegion[][] splitTiles = TextureRegion.split(tiles, tilesetSize, tilesetSize);
                    if (fogOfWarLayer.getCell(j, i) != null) {
                        fogOfWarLayer.getCell(j, i).setTile(new StaticTiledMapTile(splitTiles[14][1]));
                    }
                }
            }
        }
    }

    public byte tileExists()
    {
        //return tileX >= 0 && tileY >= 0 &&
        //tileX < map.length && tileY < map[0].length;
        if ((int)player.getX() / player.getRegionWidth() <= 512 && (int)player.getX() / player.getRegionWidth() >= 0){
            if ((int)player.getY() / player.getRegionHeight() <= 512 && (int)player.getY() / player.getRegionHeight() >= 0){
                //System.out.println(("INSIDE MAP ") + (this.getY() / this.getRegionHeight()));
                return 0;
            }else{
                if ((int)player.getY() / player.getRegionHeight() > 512){
                    System.out.println("OUTSIDE MAP UP");
                    terrgenerator.updateMap(terrgenerator.regionX,terrgenerator.regionY + 1);
                    direction = "UP";
                    updateMap();
                    updatePlayer();
                    return 1;
                }
                else if ((int)player.getY() / player.getRegionHeight() < 0){
                    System.out.println("OUTSIDE MAP DOWN");
                    terrgenerator.updateMap(terrgenerator.regionX,terrgenerator.regionY - 1);
                    direction = "DOWN";
                    updateMap();
                    updatePlayer();
                    return -1;
                }
            }
        }
        else{
            if ((int)player.getX() / player.getRegionWidth() > 512) {
                System.out.println("OUTSIDE MAP RIGHT");
                terrgenerator.updateMap(terrgenerator.regionX +1,terrgenerator.regionY);
                direction = "RIGHT";
                updateMap();
                updatePlayer();
                return 2;
            }
            else if ((int)player.getX() / player.getRegionWidth() < 0) {
                System.out.println("OUTSIDE MAP LEFT");
                terrgenerator.updateMap(terrgenerator.regionX -1,terrgenerator.regionY);
                direction = "LEFT";
                updateMap();
                updatePlayer();
                return -2;
            }
        }
        return 0;
    }

    public void goOffPlanet(int x, int y) {
        if (x == terrgenerator.landLocalX && y == terrgenerator.landLocalY) {
            this.dispose();
            //g.setScreen( new PlanetSystemScreen(g,generatedSystem,id));
            g.setScreen(solarScreen);
        }
    }

    public void rotateTiles() {
        for (Iterator i = map.getLayers().iterator(); i.hasNext(); ) {
            TiledMapTileLayer layer = (TiledMapTileLayer) i.next();
            for (int x = 1; x < 512; x++) {
                for (int y = 1; y < 512; y++) {
                    if (layer.getCell(x,y) != null ) {
                        //System.out.println(("ROTATED: ") + (x) + " " + (y));
                        //int actualRotation = layer.getCell(x,y).getRotation() + rotation;
                        if (player.getFacing() == "0") {
                            try {
                                Object tileType = layer.getCell(x, y).getTile().getProperties().get("Stone");
                                if (tileType != null)
                                {
                                    TiledMapTileLayer.Cell cell = terrgenerator.swapToRotatedTile(layer.getCell(x, y),"Stone",tileType,true);
                                    layer.setCell(x,y,cell);
                                }
                            }
                            catch (Exception e){

                            }
                            layer.getCell(x, y).setRotation(ROTATE_0);
                            //System.out.println(layer.getCell(x, y).getRotation());
                        }else if (player.getFacing() == "180") {
                            try {
                                Object tileType = layer.getCell(x, y).getTile().getProperties().get("Stone");
                                if (tileType != null)
                                {
                                    TiledMapTileLayer.Cell cell = terrgenerator.swapToRotatedTile(layer.getCell(x, y),"Stone",tileType,true);
                                    layer.setCell(x,y,cell);
                                }
                            }
                            catch (Exception e){

                            }
                            layer.getCell(x, y).setRotation(ROTATE_180);
                            //System.out.println(layer.getCell(x, y).getRotation());
                        }
                        else if (player.getFacing() == "270") {
                            try {
                                Object tileType = layer.getCell(x, y).getTile().getProperties().get("Stone");
                                if (tileType != null)
                                {
                                    TiledMapTileLayer.Cell cell = terrgenerator.swapToRotatedTile(layer.getCell(x, y),"Stone",tileType,true);
                                    layer.setCell(x,y,cell);
                                }
                            }
                            catch (Exception e){

                            }
                            layer.getCell(x, y).setRotation(ROTATE_90);
                            //System.out.println(layer.getCell(x, y).getRotation());
                        }
                        else if (player.getFacing() == "90") {
                            try {
                                Object tileType = layer.getCell(x, y).getTile().getProperties().get("Stone");
                                if (tileType != null)
                                {
                                    TiledMapTileLayer.Cell cell = terrgenerator.swapToRotatedTile(layer.getCell(x, y),"Stone",tileType,true);
                                    layer.setCell(x,y,cell);
                                }
                            }
                            catch (Exception e){

                            }
                            layer.getCell(x, y).setRotation(ROTATE_270);
                           // System.out.println(layer.getCell(x, y).getRotation());
                        }
                        else if (player.getFacing() == "45") {
                            //layer.getCell(x, y).setRotation(45);
                            try {
                                Object tileType = layer.getCell(x, y).getTile().getProperties().get("Stone");
                                if (tileType != null)
                                {
                                    TiledMapTileLayer.Cell cell = terrgenerator.swapToRotatedTile(layer.getCell(x, y),"Stone",tileType,false);
                                    layer.setCell(x,y,cell);
                                }
                            }
                            catch (Exception e){

                            }
                            layer.getCell(x, y).setRotation(ROTATE_0);
                            //layer.getCell(x, y).getTile().getTextureRegion().

                            //System.out.println(layer.getCell(x, y).getRotation());
                        }
                        else if (player.getFacing() == "135") {
                            //layer.getCell(x, y).setRotation(45);
                            try {
                                Object tileType = layer.getCell(x, y).getTile().getProperties().get("Stone");
                                if (tileType != null)
                                {
                                    TiledMapTileLayer.Cell cell = terrgenerator.swapToRotatedTile(layer.getCell(x, y),"Stone",tileType,false);
                                    layer.setCell(x,y,cell);
                                }
                            }
                            catch (Exception e){

                            }
                            layer.getCell(x, y).setRotation(ROTATE_270);
                        }
                        else if (player.getFacing() == "225") {
                            //layer.getCell(x, y).setRotation(45);
                            try {
                                Object tileType = layer.getCell(x, y).getTile().getProperties().get("Stone");
                                if (tileType != null)
                                {
                                    TiledMapTileLayer.Cell cell = terrgenerator.swapToRotatedTile(layer.getCell(x, y),"Stone",tileType,false);
                                    layer.setCell(x,y,cell);
                                }
                            }
                            catch (Exception e){

                            }
                            layer.getCell(x, y).setRotation(ROTATE_180);
                        }
                        else if (player.getFacing() == "315") {
                            //layer.getCell(x, y).setRotation(45);
                            try {
                                Object tileType = layer.getCell(x, y).getTile().getProperties().get("Stone");
                                if (tileType != null)
                                {
                                    TiledMapTileLayer.Cell cell = terrgenerator.swapToRotatedTile(layer.getCell(x, y),"Stone",tileType,false);
                                    layer.setCell(x,y,cell);
                                }
                            }
                            catch (Exception e){

                            }
                            layer.getCell(x, y).setRotation(ROTATE_90);
                        }
                    }
                }
            }
        }
    }
}
