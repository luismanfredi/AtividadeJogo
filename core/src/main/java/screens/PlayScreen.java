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
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import io.github.luismanfredi.atividadejogo.*;

import java.util.ArrayList;
import java.util.Iterator;

public class PlayScreen implements Screen {

    private final Core game;
    private Player player;
    private ArrayList<Enemy> enemies = new ArrayList<>();
    private ArrayList<Bullet> bullets = new ArrayList<>();

    private Texture playerTexture;
    private Texture enemyTexture;
    private Texture bulletTexture;
    public StretchViewport viewport;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    private OrthographicCamera camera;

    private final float MAP_WIDTH = 80f;
    private final float MAP_HEIGHT = 20f;

    public PlayScreen(Core game) {
        this.game = game;
    }

    @Override
    public void render(float delta) {
        input(delta);
        logic(delta);

        player.update(delta);

        for (Enemy enemy : enemies) {
            enemy.update(delta, player.getX(), player.getY());

            if (player.box.overlaps(enemy.box)) {
                enemies.remove(enemy);
            }
        }

        Iterator<Bullet> iterator = bullets.iterator();
        while (iterator.hasNext()) {
            Bullet b = iterator.next();
            b.update(delta);

            if (b.isDead() || b.getX() > MAP_WIDTH || b.getX() < 0) {
                iterator.remove();
            }
        }


        float cameraHalfWidth = viewport.getWorldWidth() / 2f;

        float targetX = MathUtils.clamp(player.getX() + player.getWidth() / 2f, cameraHalfWidth, MAP_WIDTH - cameraHalfWidth);

        camera.position.set(targetX, MAP_HEIGHT / 2f, 0f);
        camera.update();

        ScreenUtils.clear(Color.BLACK);

        renderer.setView(camera);
        renderer.render();

        game.spriteBatch.setProjectionMatrix(camera.combined);

        game.spriteBatch.begin();

        player.draw(game.spriteBatch);

        for (Enemy enemy : enemies) {
            enemy.draw(game.spriteBatch);
        }

        for (Bullet b : bullets) {
            b.draw(game.spriteBatch);
        }

        game.spriteBatch.end();
    }

    private void input(float delta) {
        player.velocity.x = 0;

        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            player.moveRight();
            player.direction = Direction.RIGHT;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            player.moveLeft();
            player.direction = Direction.LEFT;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            player.jump();
        }

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            Bullet newBullet = new Bullet(new Sprite(bulletTexture), player.direction, (TiledMapTileLayer) map.getLayers().get(0));

            newBullet.spawn(player.getX(), player.getY(), player.direction);

            bullets.add(newBullet);
        }

        if (Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) {
            Enemy newEnemy = new Enemy(new Sprite(enemyTexture), (TiledMapTileLayer) map.getLayers().get(0));

            newEnemy.setPosition(9f, 4f);

            enemies.add(newEnemy);
        }
    }

    private void logic(float delta) {
        float playerWidth = player.getWidth();
        player.setX(MathUtils.clamp(player.getX(), 0, MAP_WIDTH - playerWidth));
    }

    @Override
    public void resize(int width, int height) {
        float aspectRadio = (float) width / (float) height;
        float currentWorldWidth = MAP_HEIGHT * aspectRadio;

        viewport.setWorldSize(currentWorldWidth, MAP_HEIGHT);
        viewport.update(width, height, true);
    }

    @Override
    public void show() {
        float scale = 1f / 32f;

        camera = new OrthographicCamera();

        float aspectRatio = (float) Gdx.graphics.getWidth() / (float) Gdx.graphics.getHeight();
        viewport = new StretchViewport(MAP_WIDTH * aspectRatio, MAP_HEIGHT, camera);

        TmxMapLoader loader = new TmxMapLoader();
        map = loader.load("mapa/MAPA.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, scale);

        playerTexture = new Texture(Gdx.files.internal("player.png"));
        player = new Player(new Sprite(playerTexture), (TiledMapTileLayer) map.getLayers().get(0));
        player.setPosition(9f, 4f);

        enemyTexture = new Texture((Gdx.files.internal("inimigo.png")));

        bulletTexture = new Texture((Gdx.files.internal("bala.png")));
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
        bulletTexture.dispose();
    }
}
