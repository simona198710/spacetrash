package com.svamp.spacetrash;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Simon on 2016-08-04.
 */
public class player extends Sprite implements InputProcessor {

    /** the movement velocity */
    private Vector2 velocity = new Vector2();

    private float speed = 64, gravity = 0, animationTime = 0;
    private float targetTileX = 0;
    private float targetTileY = 0;
    private String moving = "Nowhere";
    private boolean keypressedW  = false;
    private String facing = "0";
    private PlanetScreen planetScreen;

    //private boolean canJump = true;

    private Animation up, left, right, down;
    private TiledMapTileLayer collisionLayer;

    //private String blockedKey = "blocked";

    private double oxygen = 100;
    private double HP = 100;


    public player(Animation up, Animation left, Animation right, Animation down, TiledMapTileLayer collisionLayer, PlanetScreen screen) {
        super(up.getKeyFrame(0));
        this.up = up;
        this.left = left;
        this.right = right;
        this.down = down;
        this.collisionLayer = collisionLayer;
        setSize(getWidth(), getHeight() * 1f);
        planetScreen = screen;
    }

    //@Override
    public void draw(Batch batch1,Batch batch2,Batch batch3,Batch batch4, Batch batch5,  Batch batch6, Batch batch7) {


        batch1.begin();
        super.draw(batch1);
        batch1.end();

        batch2.begin();
        super.draw(batch2);
        batch2.end();

        batch3.begin();
        super.draw(batch3);
        batch3.end();

        batch4.begin();
        super.draw(batch4);
        batch4.end();

        batch5.begin();
        super.draw(batch5);
        batch5.end();

        batch6.begin();
        super.draw(batch6);
        batch6.end();

        batch7.begin();
        super.draw(batch7);
        batch7.end();

        update(Gdx.graphics.getDeltaTime());
    }

    public void update(float delta) {

        if (planetScreen.tileExists() != 0){
            velocity.y = 0;
            velocity.x = 0;
            if (moving == "0"){
                targetTileY = getY() + collisionLayer.getTileWidth();
                targetTileX = getX();
                velocity.y = speed;
                velocity.x = 0;
            }
            else if (moving == "45"){
                targetTileY = getY() + collisionLayer.getTileWidth();
                targetTileX = getX() + collisionLayer.getTileWidth();
                velocity.y = speed;
                velocity.x = speed;
            }
            else if (moving == "315"){
                targetTileX = getX() - collisionLayer.getTileWidth();
                targetTileY = getY() + collisionLayer.getTileWidth();
                velocity.x = -speed;
                velocity.y = speed;
            }
            else if (moving == "180"){
                targetTileY = getY() - collisionLayer.getTileWidth();
                targetTileX = getX();
                velocity.y = -speed;
                velocity.x = 0;
            }
            else if (moving == "225"){
                targetTileX = getX() - collisionLayer.getTileWidth();
                targetTileY = getY() - collisionLayer.getTileWidth();
                velocity.x = -speed;
                velocity.y = -speed;
            }
            else if (moving == "270"){
                targetTileX = getX() - collisionLayer.getTileWidth();
                targetTileY = getY();
                velocity.x = -speed;
                velocity.y = 0;
            }
            else if (moving == "90"){
                targetTileX = getX() + collisionLayer.getTileWidth();
                targetTileY = getY();
                velocity.x = speed;
                velocity.y = 0;
            }
            else if (moving == "135"){
                targetTileX = getX() + collisionLayer.getTileWidth();
                targetTileY = getY() - collisionLayer.getTileWidth();
                velocity.x = speed;
                velocity.y = -speed;
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

        if(getX() >= targetTileX && moving == "90") {
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
                targetTileX = getX() + collisionLayer.getTileWidth();
                targetTileY = getY();
                velocity.x = speed;
                velocity.y = 0;

            }
        }

        if(getX() >= targetTileX && moving == "135") {
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
                targetTileX = getX() + collisionLayer.getTileWidth();
                targetTileY = getY() - collisionLayer.getTileWidth();
                velocity.x = speed;
                velocity.y = -speed;

            }
        }

        if(getX() <= targetTileX && moving == "270") {
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
                targetTileX = getX() - collisionLayer.getTileWidth();
                targetTileY = getY();
                velocity.x = -speed;
                velocity.y = 0;
            }
        }

        if(getY() >= targetTileY && moving == "0") {
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

        if(getY() >= targetTileY && moving == "45") {
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
                targetTileX = getX() + collisionLayer.getTileWidth();
                velocity.y = speed;
                velocity.x = speed;
            }
        }

        if(getX() <= targetTileX && moving == "315") {
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
                targetTileX = getX() - collisionLayer.getTileWidth();
                targetTileY = getY() + collisionLayer.getTileWidth();
                velocity.x = -speed;
                velocity.y = speed;
            }
        }


        if(getY() <= targetTileY && moving == "180") {
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
                targetTileY = getY() - collisionLayer.getTileWidth();
                targetTileX = getX();
                velocity.y = -speed;
                velocity.x = 0;
            }
        }
        if(getX() <= targetTileX && moving == "225") {
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
                targetTileX = getX() - collisionLayer.getTileWidth();
                targetTileY = getY() - collisionLayer.getTileWidth();
                velocity.x = -speed;
                velocity.y = -speed;

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

    public float getGravity() {
        return gravity;
    }

    public void setGravity(float gravity) {
        this.gravity = gravity;
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
            case Keys.SPACE:
                int positionX = (int) getX() / this.getRegionWidth();
                int positionY = (int) getY() / this.getRegionHeight();
                planetScreen.goOffPlanet(positionX,positionY);
                break;
            case Keys.W:
                if (moving == "Nowhere") {
                    if (facing == "0") {
                        if (!isCellBlocked(getX(), getY() + collisionLayer.getTileWidth())) {
                            targetTileY = getY() + collisionLayer.getTileWidth();
                            targetTileX = getX();
                            velocity.y = speed;
                            velocity.x = 0;
                            moving = "0";
                            keypressedW = true;
                        }
                    }
                    if (facing == "45") {
                        if (!isCellBlocked(getX() + collisionLayer.getTileWidth(), getY() + collisionLayer.getTileWidth())) {
                            targetTileY = getY() + collisionLayer.getTileWidth();
                            targetTileX = getX() + collisionLayer.getTileWidth();
                            velocity.y = speed;
                            velocity.x = speed;
                            moving = "45";
                            keypressedW = true;
                        }
                    }
                    if (facing == "315") {
                        if (!isCellBlocked(getX() - collisionLayer.getTileWidth(), getY() + collisionLayer.getTileWidth())) {
                            targetTileX = getX() - collisionLayer.getTileWidth();
                            targetTileY = getY() + collisionLayer.getTileWidth();
                            velocity.x = -speed;
                            velocity.y = speed;
                            moving = "315";
                            keypressedW = true;
                        }
                    }
                    if (facing == "180") {
                        if (!isCellBlocked(getX(), getY() - collisionLayer.getTileWidth())) {
                            targetTileY = getY() - collisionLayer.getTileWidth();
                            targetTileX = getX();
                            velocity.y = -speed;
                            velocity.x = 0;
                            moving = "180";
                            keypressedW = true;
                        }
                    }
                    if (facing == "225") {
                        if (!isCellBlocked(targetTileX = getX() - collisionLayer.getTileWidth(), getY() - collisionLayer.getTileWidth())) {
                            targetTileX = getX() - collisionLayer.getTileWidth();
                            targetTileY = getY() - collisionLayer.getTileWidth();
                            velocity.x = -speed;
                            velocity.y = -speed;
                            moving = "225";
                            keypressedW = true;
                        }
                    }
                    if (facing == "270") {
                        if (!isCellBlocked(targetTileX = getX() - collisionLayer.getTileWidth(), getY())) {
                            targetTileX = getX() - collisionLayer.getTileWidth();
                            targetTileY = getY();
                            velocity.x = -speed;
                            velocity.y = 0;
                            moving = "270";
                            keypressedW = true;
                        }
                    }
                    if (facing == "90") {
                        if (!isCellBlocked(targetTileX = getX() + collisionLayer.getTileWidth(), getY())) {
                            targetTileX = getX() + collisionLayer.getTileWidth();
                            targetTileY = getY();
                            velocity.x = speed;
                            velocity.y = 0;
                            moving = "90";
                            keypressedW = true;
                        }
                    }
                    if (facing == "135") {
                        if (!isCellBlocked(targetTileX = getX() + collisionLayer.getTileWidth(), getY())) {
                            targetTileX = getX() + collisionLayer.getTileWidth();
                            targetTileY = getY() - collisionLayer.getTileWidth();
                            velocity.x = speed;
                            velocity.y = -speed;
                            moving = "135";
                            keypressedW = true;
                        }
                    }
                }
                break;
            case Keys.S:
                if (moving == "Nowhere") {
                    if (facing == "Up") {
                        if (!isCellBlocked(getX(), getY() - collisionLayer.getTileWidth())) {
                            targetTileY = getY() - collisionLayer.getTileWidth();
                            targetTileX = getX();
                            velocity.y = -speed;
                            velocity.x = 0;
                            moving = "Down";
                        }
                    }
                    if (facing == "Down") {
                        if (!isCellBlocked(getX(), getY() + collisionLayer.getTileWidth())) {
                            targetTileY = getY() + collisionLayer.getTileWidth();
                            targetTileX = getX();
                            velocity.y = speed;
                            velocity.x = 0;
                            moving = "Up";
                        }
                    }
                    if (facing == "Left") {
                        if (!isCellBlocked(targetTileX = getX() + collisionLayer.getTileWidth(), getY())) {
                            targetTileX = getX() + collisionLayer.getTileWidth();
                            targetTileY = getY();
                            velocity.x = speed;
                            velocity.y = 0;
                            moving = "Right";
                        }
                    }
                    if (facing == "Right") {
                        if (!isCellBlocked(targetTileX = getX() - collisionLayer.getTileWidth(), getY())) {
                            targetTileX = getX() - collisionLayer.getTileWidth();
                            targetTileY = getY();
                            velocity.x = -speed;
                            velocity.y = 0;
                            moving = "Left";
                        }
                    }
                }
                break;
            case Keys.A:
                if (moving == "Nowhere") {
                    if (facing == "0") {
                        facing = "315";
                    }
                    else if (facing == "315") {
                        facing = "270";
                    }
                    else if (facing == "270") {
                        facing = "225";
                    }
                    else if (facing == "225") {
                        facing = "180";
                    }
                    else if (facing == "180") {
                        facing = "135";
                    }
                    else if (facing == "135") {
                        facing = "90";
                    }
                    else if (facing == "90") {
                        facing = "45";
                    }
                    else if (facing == "45") {
                        facing = "0";
                    }
                    //System.out.println(facing);
                   // planetScreen.camera.rotate(-45);
                   // planetScreen.rotateTiles();
                   // this.rotate(45);
                }
                //animationTime = 0;
                break;
            case Keys.D:
                if (moving == "Nowhere") {
                    if (facing == "0") {
                        facing = "45";
                    }
                    else if (facing == "45") {
                        facing = "90";
                    }
                   else if (facing == "90") {
                        facing = "135";
                    }
                   else if (facing == "135") {
                        facing = "180";
                    }
                    else if (facing == "180") {
                        facing = "225";
                    }
                    else if (facing == "225") {
                        facing = "270";
                    }
                    else if (facing == "270") {
                        facing = "315";
                    }
                    else if (facing == "315") {
                        facing = "0";
                    }
                    //System.out.println(facing);
                    //planetScreen.camera.rotate(45);
                    //planetScreen.rotateTiles();
                    //this.rotate(-45);
                }
                break;
            /*
            case Keys.Q:
                if (moving == "Nowhere") {
                    if (facing == "Up") {
                        facing = "Left";
                    }
                    else if (facing == "Left") {
                        facing = "Down";
                    }
                    else if (facing == "Down") {
                        facing = "Right";
                    }
                    else if (facing == "Right") {
                        facing = "Up";
                    }
                    planetScreen.camera.rotate(-45);
                    planetScreen.rotateTiles();
                    this.rotate(45);
                    //System.out.println(facing);
                }
                //animationTime = 0;
                break;
            case Keys.E:
                if (moving == "Nowhere") {
                    if (facing == "Up") {
                        facing = "Right";
                    }
                    else if (facing == "Right") {
                        facing = "Down";
                    }
                    else if (facing == "Down") {
                        facing = "Left";
                    }
                    else if (facing == "Left") {
                        facing = "Up";
                    }
                    planetScreen.camera.rotate(45);
                    planetScreen.rotateTiles();
                    this.rotate(-45);
                    //System.out.println(facing);
                }
               */
                //animationTime = 0;
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        switch(keycode) {
            case Keys.W:
                keypressedW = false;
                break;
            /*
            case Keys.E:
                if (moving == "Nowhere") {
                    if (facing == "Up") {
                        facing = "Left";
                    }
                    else if (facing == "Left") {
                        facing = "Down";
                    }
                    else if (facing == "Down") {
                        facing = "Right";
                    }
                    else if (facing == "Right") {
                        facing = "Up";
                    }
                    //System.out.println(facing);
                    planetScreen.camera.rotate(-45);
                    planetScreen.rotateTiles();
                    this.rotate(45);
                }
                //animationTime = 0;
                break;
            case Keys.Q:
                if (moving == "Nowhere") {
                    if (facing == "Up") {
                        facing = "Right";
                    }
                    else if (facing == "Right") {
                        facing = "Down";
                    }
                    else if (facing == "Down") {
                        facing = "Left";
                    }
                    else if (facing == "Left") {
                        facing = "Up";
                    }
                    planetScreen.camera.rotate(45);
                    planetScreen.rotateTiles();
                    this.rotate(-45);
                    //System.out.println(facing);
                }
                */
        }
        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
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

    public double getOxygen(){
        return oxygen;
    }

    public double getHP(){
        return HP;
    }

    public void setOxygen(double newOxygen){
        oxygen = newOxygen;
    }

    public void setHP(double newHP){
        HP = newHP;
    }

}
