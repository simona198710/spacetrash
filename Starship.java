package com.svamp.spacetrash;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by Simon on 2016-08-08.
 */
public class Starship extends Sprite implements InputProcessor {
    /** the movement velocity */
    private Vector2 velocity = new Vector2();

    private float speed = 2048, gravity = 0, animationTime = 0;
    private float targetTileX = 0;
    private float targetTileY = 0;
    private String moving = "Nowhere";
    private boolean keypressedW  = false;
    private boolean keypressedS  = false;
    private boolean keypressedA  = false;
    private boolean keypressedD  = false;
    private String facing = "Up";

    //private boolean canJump = true;

    private Animation up, left, right, down;
    private TiledMapTileLayer collisionLayer;
    //private byte[][] bodyIDS;
    //private byte[][][] moonIDS;
    //private byte[][] planetToMoonID;
    private String[][] mergedInfo;
    private SolarSystemScreen screenSolar;
    private Texture backgroundTexture;

    public Starship(Animation up, Animation left, Animation right, Animation down, TiledMapTileLayer collisionLayer, String[][] newMergedInfo, SolarSystemScreen screen, Texture newBackgroundTexture) {

        super(up.getKeyFrame(0));
        this.up = up;
        this.left = left;
        this.right = right;
        this.down = down;
        this.collisionLayer = collisionLayer;
        setSize(getWidth(), getHeight() * 1f);
        //bodyIDS = newPlanetIDs;
        //moonIDS = newMoonIDs;
        //planetToMoonID = newPlanetToMoonIDs;
        mergedInfo = newMergedInfo;

        screenSolar = screen;
        backgroundTexture = newBackgroundTexture;
        //System.out.println(newbodyIDs.toString());
    }

    //@Override
    public void draw(Batch stars, Batch suns, Batch planets, Batch moons) {

        stars.begin();
        super.draw(stars);
        stars.end();

        suns.begin();
        super.draw(suns);
        suns.end();

        planets.begin();
        super.draw(planets);
        planets.end();

        moons.begin();
        super.draw(moons);
        moons.end();

        update(Gdx.graphics.getDeltaTime());
    }

    public void update(float delta) {
        // apply gravity
        //velocity.y -= gravity * delta;
        if (screenSolar.tileExists() != 0){
            velocity.y = 0;
            velocity.x = 0;
            if (moving == "Up"){
                targetTileY = getY() + collisionLayer.getTileWidth();
                targetTileX = getX();
                velocity.y = speed;
                velocity.x = 0;
            }
            else if (moving == "Down"){
                targetTileY = getY() - collisionLayer.getTileWidth();
                targetTileX = getX();
                velocity.y = -speed;
                velocity.x = 0;
            }
            else if (moving == "Left"){
                targetTileX = getX() - collisionLayer.getTileWidth();
                targetTileY = getY();
                velocity.x = -speed;
                velocity.y = 0;
            }
            else if (moving == "Right"){
                targetTileX = getX() + collisionLayer.getTileWidth();
                targetTileY = getY();
                velocity.x = speed;
                velocity.y = 0;
            }
        }

        // clamp velocity
        if(velocity.y > speed)
            velocity.y = speed;
        else if(velocity.y < -speed)
            velocity.y = -speed;

        // save old position
        float oldX = getX(), oldY = getY();
        boolean collisionX = false, collisionY = false;

        // move on x
        setX(getX() + velocity.x * delta);

        if(velocity.x < 0) // going left
            collisionX = collidesLeft();
        else if(velocity.x > 0) // going right
            collisionX = collidesRight();

        // react to x collision
        //if(collisionX) {
        //setX(oldX);
        // moving = "Nowhere";
        // velocity.x = 0;
        // }

        // move on y
        //setY(getY() + velocity.y * delta * 5f);
        setY(getY() + velocity.y * delta);

        if(velocity.y < 0) // going down
            //canJump = collisionY = collidesBottom();
            collisionY = collidesBottom();
        else if(velocity.y > 0) // going up
            collisionY = collidesTop();

        // react to y collision
        //if(collisionY) {
        //  setY(oldY);
        //  moving = "Nowhere";
        //   velocity.y = 0;
        // }

        //if (keypressed == false){

        if(getX() >= targetTileX && moving == "Right") {
            velocity.y = 0;
            velocity.x = 0;
            //screenSolar.tileExists();
            if (keypressedD == false) {
                velocity.y = 0;
                velocity.x = 0;
                moving = "Nowhere";
                setX(targetTileX);
                setY(targetTileY);
            }else{
                setX(targetTileX);
                setY(targetTileY);
                targetTileX = getX() + collisionLayer.getTileWidth();
                targetTileY = getY();
                velocity.x = speed;
                velocity.y = 0;

            }
        }

        if(getX() <= targetTileX && moving == "Left") {
            velocity.y = 0;
            velocity.x = 0;
            //screenSolar.tileExists();
            if (keypressedA == false) {
                velocity.y = 0;
                velocity.x = 0;
                moving = "Nowhere";
                setX(targetTileX);
                setY(targetTileY);
            }else{
                setX(targetTileX);
                setY(targetTileY);
                targetTileX = getX() - collisionLayer.getTileWidth();
                targetTileY = getY();
                velocity.x = -speed;
                velocity.y = 0;
            }
        }

        if(getY() >= targetTileY && moving == "Up") {
            velocity.y = 0;
            velocity.x = 0;
            //screenSolar.tileExists();
            if (keypressedW == false) {
                velocity.y = 0;
                velocity.x = 0;
                moving = "Nowhere";
                setX(targetTileX);
                setY(targetTileY);
            }else{
                setX(targetTileX);
                setY(targetTileY);
                targetTileY = getY() + collisionLayer.getTileWidth();
                targetTileX = getX();
                velocity.y = speed;
                velocity.x = 0;
            }
        }

        if(getY() <= targetTileY && moving == "Down") {
            velocity.y = 0;
            velocity.x = 0;
            //screenSolar.tileExists();
            if (keypressedS == false) {
                velocity.y = 0;
                velocity.x = 0;
                moving = "Nowhere";
                setX(targetTileX);
                setY(targetTileY);
            }else{
                setX(targetTileX);
                setY(targetTileY);
                targetTileY = getY() - collisionLayer.getTileWidth();
                targetTileX = getX();
                velocity.y = -speed;
                velocity.x = 0;
            }
        }
        // }

        // update animation
        animationTime += delta;
        setRegion(velocity.x < 0 ? left.getKeyFrame(animationTime) : velocity.x > 0 ? right.getKeyFrame(animationTime) :  velocity.y > 0 ? up.getKeyFrame(animationTime) :  velocity.y < 0 ? down.getKeyFrame(animationTime) : up.getKeyFrame(animationTime));

    }

    private boolean isCellBlocked(float x, float y) {
        TiledMapTileLayer.Cell cell = collisionLayer.getCell((int) (x / collisionLayer.getTileWidth()), (int) (y / collisionLayer.getTileHeight()));
        //return cell != null && cell.getTile() != null && cell.getTile().getProperties().containsKey(blockedKey);
        return cell != null && cell.getTile() != null;
    }

    public boolean collidesRight() {
        for(float step = 0; step < getHeight(); step += collisionLayer.getTileHeight() / 2)
            if(isCellBlocked(getX() + getWidth(), getY() + step))
                return true;
        return false;
    }

    public boolean collidesLeft() {
        for(float step = 0; step < getHeight(); step += collisionLayer.getTileHeight() / 2)
            if(isCellBlocked(getX(), getY() + step))
                return true;
        return false;
    }

    public boolean collidesTop() {
        for(float step = 0; step < getWidth(); step += collisionLayer.getTileWidth() / 2)
            if(isCellBlocked(getX() + step, getY() + getHeight()))
                return true;
        return false;

    }

    public boolean collidesBottom() {
        for(float step = 0; step < getWidth(); step += collisionLayer.getTileWidth() / 2)
            if(isCellBlocked(getX() + step, getY()))
                return true;
        return false;
    }

    public Vector2 getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector2 velocity) {
        this.velocity = velocity;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public TiledMapTileLayer getCollisionLayer() {
        return collisionLayer;
    }

    public void setCollisionLayer(TiledMapTileLayer collisionLayer) {
        this.collisionLayer = collisionLayer;
    }


    //controll stuff

    @Override
    public boolean keyDown(int keycode) {
        switch(keycode) {
            case Input.Keys.NUM_1:
                speed = 128;
                break;
            case Input.Keys.NUM_2:
                speed = 256;
                break;
            case Input.Keys.NUM_3:
                speed = 512;
                break;
            case Input.Keys.NUM_4:
                speed = 1024;
                break;
            case Input.Keys.NUM_5:
                speed = 2048;
                break;
            case Input.Keys.NUM_6:
                speed = 4096;
                break;
            case Input.Keys.NUM_7:
                speed = 8192;
                break;
            case Input.Keys.M:
                screenSolar.goToMap();
                break;
            case Input.Keys.W:
                if (moving == "Nowhere") {
                        if (!isCellBlocked(getX(), getY() + collisionLayer.getTileWidth())) {
                            targetTileY = getY() + collisionLayer.getTileWidth();
                            targetTileX = getX();
                            velocity.y = speed;
                            velocity.x = 0;
                            moving = "Up";
                            keypressedW = true;
                        }
                }
                break;
            case Input.Keys.S:
                if (moving == "Nowhere") {
                        if (!isCellBlocked(getX(), getY() - collisionLayer.getTileWidth())) {
                            targetTileY = getY() - collisionLayer.getTileWidth();
                            targetTileX = getX();
                            velocity.y = -speed;
                            velocity.x = 0;
                            moving = "Down";
                            keypressedS = true;
                        }
                }
                break;
            case Input.Keys.A:
                if (moving == "Nowhere") {
                    if (!isCellBlocked(targetTileX = getX() - collisionLayer.getTileWidth(), getY())) {
                        targetTileX = getX() - collisionLayer.getTileWidth();
                        targetTileY = getY();
                        velocity.x = -speed;
                        velocity.y = 0;
                        moving = "Left";
                        keypressedA = true;
                    }
                }
                //animationTime = 0;
                break;
            case Input.Keys.D:
                if (moving == "Nowhere") {
                        if (!isCellBlocked(targetTileX = getX() + collisionLayer.getTileWidth(), getY())) {
                            targetTileX = getX() + collisionLayer.getTileWidth();
                            targetTileY = getY();
                            velocity.x = speed;
                            velocity.y = 0;
                            moving = "Right";
                            keypressedD = true;
                        }
                    //System.out.println(facing);
                }
                break;
            case Input.Keys.SPACE:
                if (moving == "Nowhere") {
                    int positionX = (int) getX() / this.getRegionWidth();
                    int positionY = (int) getY() / this.getRegionHeight();
                    String stringToSplit = mergedInfo[positionX][positionY];
                    System.out.println(stringToSplit);

                    if (stringToSplit != null) {
                        String[] bodyData = stringToSplit.split(":");

                        if (Integer.parseInt(bodyData[1]) == 0) {
                            System.out.println("Landing on planet");
                            //System.out.println(system.children.get(bodyIDS[(int) clickedTileX][(int) clickedTileY]).seed);
                            // g.setScreen( new PlanetScreen(g,system.children.get(bodyIDS[(int) clickedTileX][(int) clickedTileY]).seed));

                            if (screenSolar != null) {

                                screenSolar.goToPlanet(Integer.parseInt(bodyData[0])-1);
                            }
                        } else if (Integer.parseInt(bodyData[1]) > 0) {
                            System.out.println("Landing on moon");
                            //System.out.println(system.children.get(bodyIDS[(int) clickedTileX][(int) clickedTileY]).seed);
                            // g.setScreen( new PlanetScreen(g,system.children.get(bodyIDS[(int) clickedTileX][(int) clickedTileY]).seed));


                            if (screenSolar != null) {

                                screenSolar.goToMoon(Integer.parseInt(bodyData[0])-1, Integer.parseInt(bodyData[1])-1);
                            }
                        }
                        //System.out.println(facing);
                    }
                }
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        switch(keycode) {
            case Input.Keys.D:
                keypressedD = false;
                break;
            case Input.Keys.A:
                keypressedA = false;
                break;
            case Input.Keys.S:
                keypressedS = false;
                break;
            case Input.Keys.W:
                keypressedW = false;
                break;
        }

        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        /*
        Vector3 clickCoordinates = new Vector3(screenX / this.getRegionWidth(),screenY / this.getRegionHeight(),0);
        System.out.println((int)clickCoordinates.x + (this.getX() /this.getRegionWidth()) - ((Gdx.graphics.getWidth() / 64) / 2));
        System.out.println((int)clickCoordinates.y + (this.getY() /this.getRegionHeight()) - ((Gdx.graphics.getHeight() / 64) / 2));
        float clickedTileX = clickCoordinates.x + (this.getX() /this.getRegionWidth()) - (((Gdx.graphics.getWidth() / this.getRegionWidth())) / 2);
        float clickedTileY = clickCoordinates.y + (this.getY() /this.getRegionHeight()) - (((Gdx.graphics.getHeight() / this.getRegionHeight())) / 2);

        if (bodyIDS[(int) clickedTileX][(int) clickedTileY] >= 1){
            //System.out.println(bodyIDS[(int) clickedTileX][(int) clickedTileY]);
            //System.out.println(system.children.get(bodyIDS[(int) clickedTileX][(int) clickedTileY]).seed);
           // g.setScreen( new PlanetScreen(g,system.children.get(bodyIDS[(int) clickedTileX][(int) clickedTileY]).seed));
            if (screenSolar != null) {
                screenSolar.goToPlanet(bodyIDS[(int) clickedTileX][(int) clickedTileY]);
            }
            else if (screenPlanet != null) {
                screenPlanet.landOnPlanet(bodyIDS[(int) clickedTileX][(int) clickedTileY]);
            }
        }
        /*
        /*
        else if (bodyIDS[(int) clickedTileX][(int) clickedTileY +1] >= 1){
            System.out.println(bodyIDS[(int) clickedTileX][(int) clickedTileY +1]);

        }
        else if (bodyIDS[(int) clickedTileX +1][(int) clickedTileY] >= 1){
            System.out.println(bodyIDS[(int) clickedTileX +1][(int) clickedTileY]);

        }
        else if (bodyIDS[(int) clickedTileX][(int) clickedTileY -1] >= 1){
            System.out.println(bodyIDS[(int) clickedTileX][(int) clickedTileY -1]);

        }
        else if (bodyIDS[(int) clickedTileX -1][(int) clickedTileY] >= 1){
            System.out.println(bodyIDS[(int) clickedTileX -1][(int) clickedTileY]);

        }
        */

        //System.out.println(clickCoordinates.x);
        //System.out.println(clickedTileX);

        //System.out.println(clickCoordinates.y);
        //System.out.println(clickedTileY);

        //System.out.println(clickCoordinates.Y);
        //Vector3 position = camera.unproject(clickCoordinates);
        //sprite.setPosition(position.x, position.y);

        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

    public String getFacing(){
        return facing;
    }

}
