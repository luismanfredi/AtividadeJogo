package io.github.luismanfredi.atividadejogo;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Core extends ApplicationAdapter {
    public Player player;
    public SpriteBatch spriteBatch;
    private Texture playerTexture;
    private Texture backgroundTexture;
    public FitViewport viewport;

    @Override
    public void create() {
        // metodo chamado quando o jogo é criado

        // as imagens tem que ficar em assets
        playerTexture = new Texture(Gdx.files.internal("player.png"));

        player = new Player(playerTexture);
        viewport = new FitViewport(8, 5);
        backgroundTexture = new Texture(Gdx.files.internal("images.jpeg"));
        spriteBatch = new SpriteBatch();
    }


}
