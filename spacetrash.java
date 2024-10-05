package com.svamp.spacetrash;

import com.badlogic.gdx.Game;

public class spacetrash extends Game {

        @Override
        public void create() {
            this.setScreen(new MainMenu(this));
        }
}
