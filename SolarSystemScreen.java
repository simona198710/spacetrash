package com.svamp.spacetrash;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Colors;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

import static com.badlogic.gdx.graphics.Color.*;

/**
 * Created by Simon on 2016-08-08.
 */
public class SolarSystemScreen implements Screen {

    private TiledMap map;

    private OrthographicCamera camera;
    private TextureAtlas starshipAtlas;
    private Starship ship;

    private SpriteBatch cordinatesBatch;
    private BitmapFont bfont = new BitmapFont();
    private static Texture backgroundTexture;

    private OrthogonalTiledMapRenderer stars;
    private OrthogonalTiledMapRenderer suns;
    private OrthogonalTiledMapRenderer planets;
    private OrthogonalTiledMapRenderer moons;
    public GalaxyGen galaxy;

    public SolarGenDynamic solarSystem;
    private Solar generatedSystem;

    private long seed;

    private Game g;

    private String direction = "UP";

    public boolean startup = true;

    public AssetManager manager = new AssetManager();

    public SolarSystemScreen(Game game, long customSeed)
    {
        g = game;
        galaxy = new GalaxyGen(3717,100,100);
        seed = galaxy.getLocationSeed();
    }

    public void show(){
        if (startup == true) {

            float w = Gdx.graphics.getWidth();
            float h = Gdx.graphics.getHeight();
            cordinatesBatch = new SpriteBatch();

            //new GalaxyGen(seed,0);
            manager.load("SolarSystemTiles/stars.png", Texture.class);
            manager.load("SolarSystemTiles/svampSolarSystemTiles.png", Texture.class);
            manager.load("svampStarship.pack", TextureAtlas.class);

            do {
                //System.out.println("WAITING FOR SOLARSYSTEM ASSETS");
                manager.update();
            } while (!manager.isLoaded("SolarSystemTiles/stars.png"));

            backgroundTexture = manager.get("SolarSystemTiles/stars.png", Texture.class);

            do {
                //System.out.println("WAITING FOR SOLARSYSTEM ASSETS");
                manager.update();
            } while (!manager.isLoaded("SolarSystemTiles/svampSolarSystemTiles.png"));

            solarSystem = new SolarGenDynamic(manager.get("SolarSystemTiles/svampSolarSystemTiles.png", Texture.class), 1024, 1024, 64, seed);
            updateMap();

            camera = new OrthographicCamera();
            //camera.setToOrtho(false, (w / h) * 480, 720);
            camera.setToOrtho(false, (w / h) * 900, 1600);
            camera.update();

            //player movement animation stuff
            //starshipAtlas = new TextureAtlas("svampStarship.pack");
            do {
                //System.out.println("WAITING FOR STARSHIP ASSETS");
                manager.update();
            } while (!manager.isLoaded("svampStarship.pack"));


            initStuff();
            startup = false;
        }
        else {
            Gdx.input.setInputProcessor(ship);
        }
    }

    private void initStuff() {
        starshipAtlas = manager.get("svampStarship.pack", TextureAtlas.class);

        Animation up, left, right, down;
        up = new Animation(1 / 6f, starshipAtlas.findRegions("starshipUP"));
        left = new Animation(1 / 6f, starshipAtlas.findRegions("starshipLeft"));
        right = new Animation(1 / 6f, starshipAtlas.findRegions("starshipRight"));
        down = new Animation(1 / 6f, starshipAtlas.findRegions("starshipDown"));

        //create player
        ship = new Starship(up, left, right, down, (TiledMapTileLayer) map.getLayers().get(4), solarSystem.returnMergedInfo(), this, backgroundTexture);
        updatePlayer();

        Gdx.input.setInputProcessor(ship);
    }

    private void updatePlayer(){
        if (direction == "UP"){
            ship.setPosition((ship.getX() / 64) * 64, (1024 - 1024) * 64);
        }
        else if (direction == "DOWN"){
            ship.setPosition((ship.getX() / 64) * 64, (1024 - 0) * 64);
        }
        else if (direction == "RIGHT"){
            ship.setPosition(0 * 64, (1024 - (ship.getY() / 64)) * 64);
        }
        else if (direction == "LEFT"){
            ship.setPosition(1024 * 64, (1024 - (ship.getY() / 64)) * 64);
        }
    }

    private void updateMap(){
        map = solarSystem.returnMap();
        generatedSystem = solarSystem.returnGeneratedSystem();

        stars = new OrthogonalTiledMapRenderer(map);
        suns = new OrthogonalTiledMapRenderer(map);
        planets = new OrthogonalTiledMapRenderer(map);
        moons = new OrthogonalTiledMapRenderer(map);
    }

    public void render (float delta){
            Gdx.gl.glClearColor(1, 1, 1, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            camera.position.set(ship.getX() + ship.getWidth() / 2, ship.getY() + ship.getHeight() / 2 + (64 * 0), 0);
            camera.update();

            stars.setView(camera);
            suns.setView(camera);
            planets.setView(camera);
            moons.setView(camera);

            int[] starsLayersToRender = new int[1];
            int[] sunsLayersToRender = new int[1];
            int[] planetsLayersToRender = new int[1];
            int[] moonsLayersToRender = new int[1];

            starsLayersToRender[0] = 0;
            sunsLayersToRender[0] = 1;
            planetsLayersToRender[0] = 2;
            moonsLayersToRender[0] = 3;

            stars.getBatch().setColor(WHITE);
            suns.getBatch().setColor(YELLOW);
            planets.getBatch().setColor(GREEN);
            moons.getBatch().setColor(GRAY);

            stars.render(starsLayersToRender);
            suns.render(sunsLayersToRender);
            planets.render(planetsLayersToRender);
            moons.render(moonsLayersToRender);

            int playerTileX = (int) ship.getX() / 64;
            int playerTileY = (int) ship.getY() / 64;
            int playerRealTileX = playerTileX + (solarSystem.regionX * 1024);
            int playerRealTileY = playerTileY + (solarSystem.regionY * 1024);
            String debugText = "L X: " + playerTileX + " L Y: " + playerTileY + " S X: " + playerRealTileX + " S Y: " + playerRealTileY + " G X: " + galaxy.locationX+ " G Y: " + galaxy.locationY + " FPS: " + Gdx.graphics.getFramesPerSecond();

            cordinatesBatch.begin();
            //cordinatesBatch.draw(backgroundRegion);
            //cordinatesBatch.draw(backgroundTexture,0,0,Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            bfont.draw(cordinatesBatch, debugText, Gdx.graphics.getWidth() / 2 - 300, Gdx.graphics.getHeight() / 2 + 230);
            cordinatesBatch.end();

            ship.draw(stars.getBatch(), suns.getBatch(), planets.getBatch(), moons.getBatch());
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
        stars.dispose();
        suns.dispose();
        planets.dispose();
        moons.dispose();
        cordinatesBatch.dispose();
        manager.dispose();
        map.dispose();
        solarSystem = null;
        System.gc();
        //manager.unload("svampStarship.pack");
    }

    public void goToPlanet(int id){
        //this.dispose();
        //g.setScreen( new PlanetSystemScreen(g,generatedSystem,id));
        g.setScreen( new PlanetScreen(g,generatedSystem.children.get(id).seed,this));
    }
    public void goToMoon(int planetId, int moonId){
        //this.dispose();
        //g.setScreen( new PlanetSystemScreen(g,generatedSystem,id));
        g.setScreen( new PlanetScreen(g,generatedSystem.children.get(planetId).children.get(moonId).seed,this));
    }

    public byte tileExists()
    {
        //return tileX >= 0 && tileY >= 0 &&
        //tileX < map.length && tileY < map[0].length;
        if ((int)ship.getX() / ship.getRegionWidth() <= 1024 && (int)ship.getX() / ship.getRegionWidth() >= 0){
            if ((int)ship.getY() / ship.getRegionHeight() <= 1024 && (int)ship.getY() / ship.getRegionHeight() >= 0){
                //System.out.println(("INSIDE MAP ") + (this.getY() / this.getRegionHeight()));
                return 0;
            }else{
                if ((int)ship.getY() / ship.getRegionHeight() > 1024){
                    direction = "UP";
                    if (solarSystem.updateMap(solarSystem.regionX,solarSystem.regionY + 1) == 0) {
                        System.out.println("OUTSIDE MAP UP");
                        updateMap();
                        updatePlayer();
                        return 1;
                    }
                    else{
                        gotoNewSolarsystem((byte)1);
                        return 1;
                    }
                }
                else if ((int)ship.getY() / ship.getRegionHeight() < 0){
                    direction = "DOWN";
                    if (solarSystem.updateMap(solarSystem.regionX,solarSystem.regionY - 1) == 0) {
                        System.out.println("OUTSIDE MAP DOWN");
                        updateMap();
                        updatePlayer();
                        return -1;
                    }
                    else{
                        gotoNewSolarsystem((byte)-1);
                        return -1;
                    }
                }
            }
        }
        else{
            if ((int)ship.getX() / ship.getRegionWidth() > 1024) {
                direction = "RIGHT";
               if (solarSystem.updateMap(solarSystem.regionX +1,solarSystem.regionY) == 0) {
                   System.out.println("OUTSIDE MAP RIGHT");
                   updateMap();
                   updatePlayer();
                   return 2;
               }
               else{
                   gotoNewSolarsystem((byte)2);
                   return 2;
               }
            }
            else if ((int)ship.getX() / ship.getRegionWidth() < 0) {
                direction = "LEFT";
                if (solarSystem.updateMap(solarSystem.regionX -1,solarSystem.regionY) == 0) {
                    System.out.println("OUTSIDE MAP LEFT");
                    updateMap();
                    updatePlayer();
                    return -2;
                }
                else{
                    gotoNewSolarsystem((byte)-2);
                    return -2;
                }
            }
        }
        return 0;
    }

    public void gotoNewSolarsystem(byte direction){

        long result[] = {};

        if (direction == 1){ //up
            result = galaxy.updateCords( galaxy.locationX, galaxy.locationY +1);
        }
        else if (direction == -1){ //down
            result = galaxy.updateCords( galaxy.locationX, galaxy.locationY -1);
        }
        else if (direction == 2){ //right
            result = galaxy.updateCords( galaxy.locationX +1, galaxy.locationY);
        }
        else if (direction == -2){ //left
            result = galaxy.updateCords( galaxy.locationX -1, galaxy.locationY);
        }

        if (result[0] == 0){ //no star, load empty space
            solarSystem = new SolarGenDynamic(manager.get("SolarSystemTiles/svampSolarSystemTiles.png", Texture.class),1024,1024,64,result[1],false);
            System.out.println("EMPTY SYSTEM");
        }
        else if (result[0] == 1){ //there is a star get the seed
            solarSystem = new SolarGenDynamic(manager.get("SolarSystemTiles/svampSolarSystemTiles.png", Texture.class),1024,1024,64,result[1]);
            System.out.println("SYSTEM HAS SOMETHING");
        }

        if (direction == 1){ //up
            solarSystem.updateMap(solarSystem.regionX,0);
        }
        else if (direction == -1){ //down
            solarSystem.updateMap(solarSystem.regionX,32);
        }
        else if (direction == 2){ //right
            solarSystem.updateMap(0,solarSystem.regionY);
        }
        else if (direction == -2){ //left
            solarSystem.updateMap(32,solarSystem.regionY);
        }

        updateMap();
        updatePlayer();

    }

    public void goToMap(){
        //this.dispose();
        //g.setScreen( new PlanetSystemScreen(g,generatedSystem,id));
        g.setScreen( new GalaxyScreen(g,this));
    }

}
