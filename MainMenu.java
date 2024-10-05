package com.svamp.spacetrash;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

/**
 * Created by Simon on 2016-08-06.
 */
public class MainMenu implements Screen {
    private Skin skin;
    private Stage stage;
    private SpriteBatch batch;

    private Game game;
    private BitmapFont bfont = new BitmapFont();
    private long seed = 12345;

    public MainMenu(Game g){
        game=g;
    }

    @Override
    public void show(){
        batch = new SpriteBatch();
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        // A skin can be loaded via JSON or defined programmatically, either is fine. Using a skin is optional but strongly
        // recommended solely for the convenience of getting a texture, region, etc as a drawable, tinted drawable, etc.
        skin = new Skin();
        // Generate a 1x1 white texture and store it in the skin named "white".
        Pixmap pixmap = new Pixmap(100, 50, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.BLUE);
        pixmap.fill();

        Pixmap pixmap2 = new Pixmap(50, 50, Pixmap.Format.RGBA8888);
        pixmap2.setColor(Color.BLUE);
        pixmap2.fill();

        skin.add("white", new Texture(pixmap));
        skin.add("white2", new Texture(pixmap2));

        // Store the default libgdx font under the name "default".
        bfont.getData().setScale(1, 1); //font scale/size
        skin.add("default",bfont);

        // Configure a TextButtonStyle and name it "default". Skin resources are stored by type, so this doesn't overwrite the font.
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.up = skin.newDrawable("white", Color.DARK_GRAY);
        textButtonStyle.down = skin.newDrawable("white", Color.DARK_GRAY);
        textButtonStyle.checked = skin.newDrawable("white", Color.BLUE);
        textButtonStyle.over = skin.newDrawable("white", Color.LIGHT_GRAY);

        textButtonStyle.font = skin.getFont("default");

        skin.add("default", textButtonStyle);

        TextButton.TextButtonStyle textButtonStyle2 = new TextButton.TextButtonStyle();
        textButtonStyle2.up = skin.newDrawable("white2", Color.DARK_GRAY);
        textButtonStyle2.down = skin.newDrawable("white2", Color.DARK_GRAY);
        textButtonStyle2.checked = skin.newDrawable("white2", Color.BLUE);
        textButtonStyle2.over = skin.newDrawable("white2", Color.LIGHT_GRAY);

        textButtonStyle2.font = skin.getFont("default");

        skin.add("default", textButtonStyle);
        skin.add("default", textButtonStyle2);

        // Create a button with the "default" TextButtonStyle. A 3rd parameter can be used to specify a name other than "default".
        final TextButton textButton=new TextButton("PLAY",textButtonStyle);
        textButton.setPosition(Gdx.graphics.getWidth() / 2 - 50, Gdx.graphics.getHeight() / 2);
        stage.addActor(textButton);
        //stage.addActor(textButton); //vrf tre gånger???
       // stage.addActor(textButton);

        final TextButton quitButton=new TextButton("QUIT",textButtonStyle);
        quitButton.setPosition(Gdx.graphics.getWidth() / 2 - 50, Gdx.graphics.getHeight() / 2 - 60);
        stage.addActor(quitButton);

        final TextButton plusButton=new TextButton("+",textButtonStyle2);
        plusButton.setPosition(Gdx.graphics.getWidth() / 2 + 70, Gdx.graphics.getHeight() / 2 + 60);
        stage.addActor(plusButton);

        final TextButton minusButton=new TextButton("-",textButtonStyle2);
        minusButton.setPosition(Gdx.graphics.getWidth() / 2 - 130, Gdx.graphics.getHeight() / 2 + 60);
        stage.addActor(minusButton);

        // Add a listener to the button. ChangeListener is fired when the button's checked state changes, eg when clicked,
        // Button#setChecked() is called, via a key press, etc. If the event.cancel() is called, the checked state will be reverted.
        // ClickListener could have been used, but would only fire when clicked. Also, canceling a ClickListener event won't
        // revert the checked state.
        textButton.addListener(new ChangeListener() {
            public void changed (ChangeEvent event, Actor actor) {
                //System.out.println("Clicked! Is checked: " + button.isChecked());
                //textButton.setText("Starting new game");
                //game.setScreen( new PlanetScreen(game,seed));
                dispose();
                //game.setScreen(new SolarSystemScreen(game,seed));
                game.setScreen(new PlanetScreen(game,seed,null));

            }
        });

        quitButton.addListener(new ChangeListener() {
            public void changed (ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });

        plusButton.addListener(new ChangeListener() {
            public void changed (ChangeEvent event, Actor actor) {
                seed = seed +1;
            }
        });

        minusButton.addListener(new ChangeListener() {
            public void changed (ChangeEvent event, Actor actor) {
                seed = seed -1;
            }
        });

    }

    public void render (float delta) {
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();

        String seedText = "Seed: " + Long.toString(seed);

        batch.begin();
        bfont.draw(batch, seedText, Gdx.graphics.getWidth() / 2 - 50, Gdx.graphics.getHeight() / 2 + 80);
        batch.end();

        //Table.drawDebug(stage);
    }

    @Override
    public void resize (int width, int height) {
    }

    @Override
    public void dispose () {
        stage.dispose();
        skin.dispose();
        batch.dispose();
        bfont.dispose();
    }

    @Override
    public void hide() {
        // TODO Auto-generated method stub

    }

    @Override
    public void pause() {
        // TODO Auto-generated method stub

    }

    @Override
    public void resume() {
        // TODO Auto-generated method stub

    }
}
