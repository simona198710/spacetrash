package com.svamp.spacetrash;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

import static com.badlogic.gdx.graphics.Color.WHITE;

/**
 * Created by Simon on 2016-08-14.
 */
public class GalaxyScreen implements Screen {

    private TiledMap map;
    private OrthographicCamera camera;
    private OrthogonalTiledMapRenderer stars;
    private TextureAtlas scrollerAtlas;
    private GalaxyScreenScroller scroller;

    public GalaxyPainter galaxyPainter;
    public GalaxyGen generatedGalaxy;
    private long seed;
    private SolarSystemScreen solarScreen;
    private GalaxyGen galaxy;

    private Game g;
    private String direction = "UP";
    private double zoom = 1;

    public AssetManager manager = new AssetManager();

    public GalaxyScreen(Game game, long customSeed)
    {
        seed = customSeed;
        g = game;
    }

    public GalaxyScreen(Game game, SolarSystemScreen sentSolarScreen)
    {
        g = game;
        solarScreen = sentSolarScreen;
        galaxy = solarScreen.galaxy;
        seed = galaxy.getSeed();
    }

    public void show(){
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        manager.load("SolarSystemTiles/planet.png", Texture.class);
        manager.load("svampStarship.pack", TextureAtlas.class);

        do
        {
            //System.out.println("WAITING FOR SOLARSYSTEM ASSETS");
            manager.update();
        }while(!manager.isLoaded("SolarSystemTiles/planet.png"));
        galaxyPainter = new GalaxyPainter(manager.get("SolarSystemTiles/planet.png", Texture.class),512,512,64,galaxy);
        updateMap();

        camera = new OrthographicCamera();
        //camera.setToOrtho(false, (w / h) * 480, 720);
        camera.setToOrtho(false, (512 * 64) * (float) zoom, (512 * 64) * (float) zoom);
        camera.update();

        do
        {
            //System.out.println("WAITING FOR STARSHIP ASSETS");
            manager.update();
        }while(!manager.isLoaded("svampStarship.pack"));

        scrollerAtlas = manager.get("svampStarship.pack", TextureAtlas.class);

        Animation up, left, right, down;
        up = new Animation(1 / 6f, scrollerAtlas.findRegions("starshipUP"));
        left = new Animation(1 / 6f, scrollerAtlas.findRegions("starshipLeft"));
        right = new Animation(1 / 6f, scrollerAtlas.findRegions("starshipRight"));
        down = new Animation(1 / 6f, scrollerAtlas.findRegions("starshipDown"));

        scroller = new GalaxyScreenScroller(up, left, right, down, (TiledMapTileLayer) map.getLayers().get(1),this);
        updatePlayer();

        Gdx.input.setInputProcessor(scroller);

    }

    private void updateMap(){
        map = galaxyPainter.returnMap();
        generatedGalaxy = galaxyPainter.returnGeneratedGalaxy();

        stars = new OrthogonalTiledMapRenderer(map);
    }

    /* //No moving scrolling betwen sectors in galaxy map yet.
    private void updatePlayer(){
        if (direction == "UP"){
            scroller.setPosition((scroller.getX() / 64) * 64, (512 - 512) * 64);
        }
        else if (direction == "DOWN"){
            scroller.setPosition((scroller.getX() / 64) * 64, (512 - 0) * 64);
        }
        else if (direction == "RIGHT"){
            scroller.setPosition(0 * 64, (512 - (scroller.getY() / 64)) * 64);
        }
        else if (direction == "LEFT"){
            scroller.setPosition(512 * 64, (512 - (scroller.getY() / 64)) * 64);
        }
    }
    */

    private void updatePlayer(){
        scroller.setPosition((galaxy.getLocalCords()[0]) * 64, (galaxy.getLocalCords()[1]) * 64);
    }

    public void render(float delta){
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.position.set(scroller.getX() + scroller.getWidth() / 2, scroller.getY() + scroller.getHeight() / 2, 0);
        camera.update();

        stars.setView(camera);

        int[] starsLayersToRender = new int[1];
        starsLayersToRender[0] = 0;
        stars.getBatch().setColor(WHITE);

        stars.render(starsLayersToRender);

        scroller.draw(stars.getBatch());

    }

    public void hide(){

    }

    public void resume(){

    }

    public void pause(){

    }

    public void resize(int width, int height){

    }

    public void dispose(){
        map.dispose();
        stars.dispose();
        scrollerAtlas.dispose();
    }

    public void updateZoom(String zoomCommand){
        if (zoomCommand == "Minus"){
            if (zoom >= 1){
                zoom = 1;
            }else{
                zoom = zoom + 0.05;
            }
        }
        else if (zoomCommand == "Plus"){
            if (zoom <= 0.05){
            }else{
                zoom = zoom - 0.05;
            }
        }

        camera.setToOrtho(false, (512 * 64) * (float) zoom, (512 * 64) * (float) zoom);
        camera.update();

    }

    public void goOffMap(){
        this.dispose();
        //g.setScreen( new PlanetSystemScreen(g,generatedSystem,id));
        g.setScreen(solarScreen);
    }

}
