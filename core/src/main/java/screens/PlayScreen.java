package screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import io.github.luismanfredi.atividadejogo.Core;
import io.github.luismanfredi.atividadejogo.Player;

public class PlayScreen implements Screen {

    private final Core game;
    private Player player;

    private Texture playerTexture;
    public FitViewport viewport;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    private OrthographicCamera camera;

    private final float MAP_WIDTH = 30f;
    private final float MAP_HEIGHT = 20f;

    public PlayScreen(Core game) {
        this.game = game;
    }

    @Override
    public void render(float delta) {
        input(delta);
        logic(delta);

        player.update(delta);

        ScreenUtils.clear(Color.BLACK);

        camera.update();
        renderer.setView(camera);
        renderer.render();

        game.spriteBatch.setProjectionMatrix(camera.combined);

        game.spriteBatch.begin();

        player.draw(game.spriteBatch);

        game.spriteBatch.end();
    }

    private void input(float delta) {
        player.velocity.x = 0;

        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            player.moveRight();
        }

        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            player.moveLeft();
        }

        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            player.jump();
        }
    }

    private void logic(float delta) {
        float playerWidth = player.getWidth();
        player.setX(MathUtils.clamp(player.getX(), 0, MAP_WIDTH - playerWidth));
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void show() {
        float scale = 1f / 32f;

        camera = new OrthographicCamera();
        viewport = new FitViewport(MAP_WIDTH, MAP_HEIGHT, camera);

        TmxMapLoader loader = new TmxMapLoader();
        map = loader.load("mapa/MAPA.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, scale);

        playerTexture = new Texture(Gdx.files.internal("player.png"));
        player = new Player(new Sprite(playerTexture), (TiledMapTileLayer) map.getLayers().get(0));
        player.setPosition(9f, 4f);
    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {
        map.dispose();
        renderer.dispose();
        playerTexture.dispose();
    }
}
