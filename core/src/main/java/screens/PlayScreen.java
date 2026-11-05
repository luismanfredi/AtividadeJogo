package screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import io.github.luismanfredi.atividadejogo.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class PlayScreen implements Screen {

    private final Core game;
    private Player player;
    private ArrayList<Enemy> enemies = new ArrayList<>();
    private ArrayList<Bullet> bullets = new ArrayList<>();

    private Texture playerTexture;
    private Texture enemyTexture;
    private Texture bulletTexture;
    private Texture barBGTexture;
    private Texture barHealthTexture;
    private StretchViewport viewport;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    private OrthographicCamera camera;
    private HealthBar healthBar;
    private BitmapFont font = new BitmapFont();
    private float timeCounter = 0.0f;

    private Random random = new Random();
    private final int[][] ENEMY_POSITIONS_SPAWN = {{0, 5}, {2, 5}, {1, 6}, {79, 16}, {77, 16}, {75, 15}};
    private float timeToSpawn = 2f;

    private final float MAP_WIDTH = 80f;
    private final float MAP_HEIGHT = 20f;

    public PlayScreen(Core game) {
        this.game = game;
    }

    @Override
    public void render(float delta) {
        input(delta);
        logic(delta);
        enemySpawner(delta);
        bulletSpawner(delta);

        player.update(delta);
        if (player.isDead) {
            Gdx.app.exit();
        }

        healthBar.update(delta, camera, player.health);

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
            enemy.healthBar.draw(game.spriteBatch);
            enemy.healthBar.hBar.draw(game.spriteBatch);
        }

        for (Bullet b : bullets) {
            b.draw(game.spriteBatch);
        }

        healthBar.draw(game.spriteBatch);
        healthBar.hBar.draw(game.spriteBatch);

        game.spriteBatch.end();
    }

    private void input(float delta) {
        player.velocity.x = 0;

        if (player.knockbackVelocity.len() < 5f) {
            if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                player.moveRight();
                player.direction = Direction.RIGHT;
            }

            if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                player.moveLeft();
                player.direction = Direction.LEFT;
            }
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
            Enemy newEnemy = new Enemy(
                new Sprite(enemyTexture),
                (TiledMapTileLayer) map.getLayers().get(0),
                new HealthBar(new Sprite(barBGTexture), new Sprite (barHealthTexture)));

            newEnemy.setPosition(0f, 5f);

            enemies.add(newEnemy);
        }
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

        barBGTexture = new Texture(Gdx.files.internal("barra_fundo.png"));
        barHealthTexture = new Texture(Gdx.files.internal("barra_vida.png"));

        healthBar = new HealthBar(new Sprite(barBGTexture), new Sprite(barHealthTexture));
        healthBar.create(player.health, true);
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
        barBGTexture.dispose();
        barHealthTexture.dispose();
    }

    public void enemySpawner(float delta) {
        timeCounter += delta;

        if (timeToSpawn <= 0) {
            Enemy newEnemy = new Enemy(
                new Sprite(enemyTexture),
                (TiledMapTileLayer) map.getLayers().get(0),
                new HealthBar(new Sprite(barBGTexture), new Sprite (barHealthTexture)));

            int[] position = ENEMY_POSITIONS_SPAWN[random.nextInt(0,6)];
            newEnemy.setPosition(position[0], position[1]);

            enemies.add(newEnemy);

            if (timeCounter < 10f) {
                timeToSpawn = 5f;
            } else if (timeCounter < 30f) {
                timeToSpawn = 2.5f;
            } else if (timeCounter < 60f) {
                timeToSpawn = 1.0f;
            } else if (timeCounter > 60f) {
                timeToSpawn = 0.25f;
            }
        } else {
            timeToSpawn -= delta;
        }
    }

    public void bulletSpawner(float delta) {
        Iterator<Bullet> bulletIterator = bullets.iterator();
        while (bulletIterator.hasNext()) {
            Bullet bullet = bulletIterator.next();
            bullet.update(delta);

            if (bullet.isDead() || bullet.getX() > MAP_WIDTH || bullet.getX() < 0) {
                bulletIterator.remove();
            }
        }
    }

    public void logic(float delta) {
        float playerWidth = player.getWidth();
        player.setX(MathUtils.clamp(player.getX(), 0, MAP_WIDTH - playerWidth)); // personagem não ir para fora do mapa

        Iterator<Enemy> iterator = enemies.iterator();
        while (iterator.hasNext()) {
            Enemy enemy = iterator.next();

            Iterator<Bullet> bulletIterator2 = bullets.iterator();
            while (bulletIterator2.hasNext()) {
                Bullet bullet = bulletIterator2.next();

                if (bullet.box.overlaps(enemy.box)) {
                    enemy.takeDamage(bullet.damage);
                    bulletIterator2.remove();
                    break;
                }
            }

            enemy.update(delta, player.getX(), player.getY());

            if (enemy.isDead) {
                iterator.remove();
                player.xp += 25;
                continue;
            }

            if (player.box.overlaps(enemy.box) && !player.isInvulnerable) {
                player.takeDamage(enemy.damage);

                Vector2 direction = new Vector2(player.getX() - enemy.getX(), player.getY() - enemy.getY());
                direction.nor();

                float knockbackStrength = 20f;
                player.knockbackVelocity.set(direction.x * knockbackStrength, direction.y * knockbackStrength);
            }
        }
    }
}
