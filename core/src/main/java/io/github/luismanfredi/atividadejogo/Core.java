package io.github.luismanfredi.atividadejogo;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import screens.PlayScreen;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */

// Core herda atributos e metodos de Game
public class Core extends Game {
    public SpriteBatch spriteBatch;

    @Override
    public void create() {
        // metodo chamado quando o jogo é criado
        spriteBatch = new SpriteBatch();
        setScreen(new PlayScreen(this)); // passa a instância, o proprio Core
    }

    @Override
    public void render() {
        // metodo que é chamado no loop do jogo, cada vez do render ele roda
        super.render();
    }

    @Override
    public void dispose() {
        // metodo chamado quando o jogo é fechado
        super.dispose();
        spriteBatch.dispose();
    }
}
