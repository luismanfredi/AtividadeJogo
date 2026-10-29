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

    @Override
    public void render() {
        // metodo que é chamado no loop do jogo, cada vez do render ele roda

        input();
        draw();
        logic();
    }

    @Override
    public void dispose() {
        // metodo chamado quando o jogo é fechado
        playerTexture.dispose(); // descarta texturas e batch para evitar vazamento
        spriteBatch.dispose();
    }

    @Override
    public void resize(int width, int height) {
        spriteBatch.getProjectionMatrix().setToOrtho2D(0, 0, width, height); // serve para objetos não mudem de tamnho quando voce muda o tamnho da janela
        // evita textura ficar esticado e etc.

        viewport.update(width, height, true);
    }

    private void draw() {
        ScreenUtils.clear(Color.BLACK); // limpa a tela
        viewport.apply();
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined); // como o viewport é aplicado ao spritebatch, para imagens aparecem no lugar certo
        spriteBatch.begin(); // todas chamadas de draw deve estar entre .begin() e .end()

        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();

        spriteBatch.draw(backgroundTexture, 0, 0, worldWidth, worldHeight); // .draw(textura, posiçãox, posiçãoy, tamanhox, tamanhoy)
        player.sprite.draw(spriteBatch); // sprites tem o proprio metodo draw

        spriteBatch.end();
    }

    private void input() {
        float delta = Gdx.graphics.getDeltaTime();

        player.update(delta);

        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            player.moveRight(delta); // quanto o sprite mexe por segundo
        }

        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            player.moveLeft(delta);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            player.jump();
        }

        if (Gdx.input.isTouched()) {
            // se clicou
        }
    }

    private void logic() {
        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();

        float playerWidth = player.sprite.getWidth();
        float playerheight = player.sprite.getHeight();

        player.sprite.setX(MathUtils.clamp(player.sprite.getX(), 0, worldWidth - playerWidth)); // verifica se o player está no tamnho certo, se não fixa ele no maximo
    }
}
