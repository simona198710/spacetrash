package com.svamp.spacetrash;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Colors;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

import static com.badlogic.gdx.graphics.Color.*;


/**
 * Created by Simon on 2016-08-08.
 */
public class PlanetSystemScreen implements Screen {

    private TiledMap map;

    private OrthographicCamera camera;
    private TextureAtlas starshipAtlas;
    private Starship ship;

    private SpriteBatch cordinatesBatch;
    private BitmapFont bfont = new BitmapFont();

    private OrthogonalTiledMapRenderer stars;
    private OrthogonalTiledMapRenderer suns;
    private OrthogonalTiledMapRenderer planets;
    private OrthogonalTiledMapRenderer asteroids;

    private Solar solarSystem;
    private PlanetSystem planetSystem;

    private int planetID;
    private Game g;

    public PlanetSystemScreen(Game game, Solar newSolarSystem, int newPlanetID)
    {
        planetID = newPlanetID;
        solarSystem = newSolarSystem;
        g = game;
    }

    public void show(){

        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        cordinatesBatch = new SpriteBatch();

        //new GalaxyGen();

        planetSystem = new PlanetSystem("SolarSystemTiles/svampSolarSystemTilesTEST.png",4096,4096,64,solarSystem,planetID);
       // System.out.println("Done WITH MAP");
        map = planetSystem.returnMap();

        //System.out.println("Done WITH MAP");

        stars = new OrthogonalTiledMapRenderer(map);
        suns = new OrthogonalTiledMapRenderer(map);
        planets = new OrthogonalTiledMapRenderer(map);
        asteroids = new OrthogonalTiledMapRenderer(map);

        camera = new OrthographicCamera();
        camera.setToOrtho(false, (w / h) * 900, 1600);
        //camera.setToOrtho(false, (w / h) * 900, 1600);
        camera.update();

        //player movement animation stuff
        starshipAtlas = new TextureAtlas("svampStarship.pack");
        Animation up, left, right, down;
        up = new Animation(1 / 6f, starshipAtlas.findRegions("starshipUP"));
        left = new Animation(1 / 6f, starshipAtlas.findRegions("starshipLeft"));
        right = new Animation(1 / 6f, starshipAtlas.findRegions("starshipRight"));
        down = new Animation(1 / 6f, starshipAtlas.findRegions("starshipDown"));

        //create player
        //System.out.println(planetSystem.returnBodyIDs().toString());
        //ship = new Starship(up, left, right, down, (TiledMapTileLayer) map.getLayers().get(3),planetSystem.returnBodyIDs(), this);
        ship.setPosition(2048 * ship.getCollisionLayer().getTileWidth(), (ship.getCollisionLayer().getHeight() - 2048) * ship.getCollisionLayer().getTileHeight());

        Gdx.input.setInputProcessor(ship);
    }

    public void render (float delta){
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.position.set(ship.getX() + ship.getWidth() / 2, ship.getY() + ship.getHeight() / 2, 0);
        camera.update();

        stars.setView(camera);
        suns.setView(camera);
        planets.setView(camera);
        asteroids.setView(camera);

        int[] starsLayersToRender = new int[1];
        int[] sunsLayersToRender = new int[1];
        int[] planetsLayersToRender = new int[1];
        int[] asteroidsLayersToRender = new int[1];

        starsLayersToRender[0] = 0;
        sunsLayersToRender[0] = 1;
        planetsLayersToRender[0] = 2;
        asteroidsLayersToRender[0] = 3;

        stars.getBatch().setColor(WHITE);
        suns.getBatch().setColor(YELLOW);
        planets.getBatch().setColor(GREEN);
        asteroids.getBatch().setColor(GRAY);

        stars.render(starsLayersToRender);
        suns.render(sunsLayersToRender);
        planets.render(planetsLayersToRender);
        asteroids.render(asteroidsLayersToRender);

        int playerTileX = (int)ship.getX() / 64;
        int playerTileY = (int)ship.getY() / 64;
        String debugText = "X: " + playerTileX + " Y: " + playerTileY + " FPS: " + Gdx.graphics.getFramesPerSecond() ;

        cordinatesBatch.begin();
        bfont.draw(cordinatesBatch, debugText, Gdx.graphics.getWidth() / 2 - 300, Gdx.graphics.getHeight() / 2 + 230);
        cordinatesBatch.end();


        ship.draw(stars.getBatch(), suns.getBatch(), planets.getBatch(), asteroids.getBatch());
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
        asteroids.dispose();
        cordinatesBatch.dispose();
        starshipAtlas.dispose();
        bfont.dispose();
        map.dispose();
        planetSystem = null;
        System.gc();
    }

    public void landOnPlanet(int id){
        //System.out.println(solarSystem.children.get(id).biosphere.);
        this.dispose();
        g.setScreen( new PlanetScreen(g,solarSystem.children.get(id).seed,null));
    }

}
