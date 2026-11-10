package screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor;
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
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import io.github.luismanfredi.atividadejogo.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class PlayScreen implements Screen {

    private final Core game;
    private Stage stage;
    private Player player;
    private ArrayList<Enemy> enemies = new ArrayList<>();
    private ArrayList<Bullet> bullets = new ArrayList<>();
    private GameState gameState = GameState.RUNNING;

    private Texture playerTexture;
    private Texture normalEnemyTexture;
    private Texture tankEnemyTexture;
    private Texture fastEnemyTexture;
    private Texture bulletTexture;
    private Texture barBGTexture;
    private Texture barHealthTexture;

    private Texture upgradeBGTexture;
    private Texture moreDMGTexture;
    private Texture doubleShootTexture;
    private Texture frozenTexture;
    private Texture higherJumpTexture;
    private Texture fasterTexture;
    private Texture dmgTexture;

    private Power moreDMG;
    private Power doubleShoot;
    private Power frozen;
    private Power higherJump;
    private Power faster;
    private Power dmg;
    private ArrayList<Power> powers = new ArrayList<>();

    private StretchViewport viewport;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    private OrthographicCamera camera;
    private HealthBar healthBar;
    private float timeCounter = 0.0f;

    private final Random random = new Random();
    private final int[][] ENEMY_POSITIONS_SPAWN = {{0, 5}, {2, 5}, {6, 4}, {79, 5}, {77, 5}, {75, 5}};
    private float timeToSpawn = 2f;

    private final float MAP_WIDTH = 80f;
    private final float MAP_HEIGHT = 20f;

    public PlayScreen(Core game) {
        this.game = game;
    }

    @Override
    public void render(float delta) {
        switch (gameState) {
            case RUNNING:
                input(delta);
                logic(delta);
                enemySpawner(delta);
                bulletSpawner(delta);

                player.update(delta);
                if (player.isDead) {
                    Gdx.app.exit();
                }

                healthBar.update(delta, camera, player.health);

                drawGame();
                break;
            case UPGRADE_SCREEN:
                drawGame();



                stage.act(delta);
                stage.draw();
                break;
        }
    }

    private void drawGame() {
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

        normalEnemyTexture = new Texture((Gdx.files.internal("inimigo.png")));
        tankEnemyTexture = new Texture((Gdx.files.internal("inimigo2.png")));
        fastEnemyTexture = new Texture((Gdx.files.internal("inimigo3.png")));

        bulletTexture = new Texture((Gdx.files.internal("bala.png")));

        barBGTexture = new Texture(Gdx.files.internal("barra_fundo.png"));
        barHealthTexture = new Texture(Gdx.files.internal("barra_vida.png"));

        healthBar = new HealthBar(new Sprite(barBGTexture), new Sprite(barHealthTexture));
        healthBar.create(player.health, true);


        stage = new Stage(new ExtendViewport(1280, 900), game.spriteBatch);
        Gdx.input.setInputProcessor(stage);

        upgradeBGTexture = new Texture(Gdx.files.internal("upgradeUI/upgrade_bg.png"));
        moreDMGTexture = new Texture(Gdx.files.internal("upgradeUI/more_dmg.png"));
        doubleShootTexture = new Texture(Gdx.files.internal("upgradeUI/double_shoot.png"));
        frozenTexture = new Texture(Gdx.files.internal("upgradeUI/frozen.png"));
        higherJumpTexture = new Texture(Gdx.files.internal("upgradeUI/higher_jump.png"));
        fasterTexture = new Texture(Gdx.files.internal("upgradeUI/faster.png"));
        dmgTexture = new Texture(Gdx.files.internal("upgradeUI/dmg.png"));

        Image upgradeBG = new Image(upgradeBGTexture);

        moreDMG = new Power(moreDMGTexture);
        doubleShoot = new Power(doubleShootTexture);
        frozen = new Power(frozenTexture);
        higherJump = new Power(higherJumpTexture);
        faster = new Power(fasterTexture);
        dmg = new Power(dmgTexture);

        powers.add(moreDMG);
        powers.add(doubleShoot);
        powers.add(frozen);
        powers.add(higherJump);
        powers.add(faster);
        powers.add(dmg);

        Table tableBG = new Table();
        tableBG.setFillParent(true);
        stage.addActor(tableBG);

        tableBG.add(upgradeBG).center().expand().width(570).height(760);
    }

    private Table choosePowers() {
        Table table = new Table();
        table.setFillParent(true);

        Power power;

        ArrayList<Power> AvailablePowers = new ArrayList<>(this.powers);

        for (int i = 0; i < 4; i++) {
            int randomIndex = random.nextInt(AvailablePowers.size());

            power = AvailablePowers.remove(randomIndex);

            powerClick(power.button);
            table.add(power.button).center().width(450).height(120);
            table.row().padTop(50);
        }

        return table;
    }

    private void powerClick(ImageButton powerButton) {
        powerButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                gameState = GameState.RUNNING;
                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Hand);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
            }
        });
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
                new Sprite(normalEnemyTexture),
                (TiledMapTileLayer) map.getLayers().get(0),
                new HealthBar(new Sprite(barBGTexture), new Sprite (barHealthTexture)));

            newEnemy.setPosition(0f, 5f);

            enemies.add(newEnemy);
        }
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
            chooseEnemy();

            if (timeCounter < 10f) {
                timeToSpawn = 5.0f;
            } else if (timeCounter < 30f) {
                timeToSpawn = 2.5f;
            } else if (timeCounter < 60f) {
                timeToSpawn = 2.0f;
            } else if (timeCounter > 60f) {
                timeToSpawn = 1.0f;
            }

        } else {
            timeToSpawn -= delta;
        }
    }

    public void chooseEnemy() {
        int randomEnemy = random.nextInt(100) + 1;
        Enemy newEnemy;

        if (randomEnemy <= 50) {
             newEnemy = new Enemy(
                new Sprite(normalEnemyTexture),
                (TiledMapTileLayer) map.getLayers().get(0),
                new HealthBar(new Sprite(barBGTexture), new Sprite (barHealthTexture)));

            enemies.add(newEnemy);
        } else if (randomEnemy <= 80) {
            newEnemy = new Enemy(
                new Sprite(fastEnemyTexture),
                (TiledMapTileLayer) map.getLayers().get(0),
                new HealthBar(new Sprite(barBGTexture), new Sprite (barHealthTexture)));

            newEnemy.maxHealth = 10f;
            newEnemy.health = newEnemy.maxHealth;
            newEnemy.speed = 5f;
            newEnemy.damage = 12.5f;

            enemies.add(newEnemy);
        } else {
            newEnemy = new Enemy(
                new Sprite(tankEnemyTexture),
                (TiledMapTileLayer) map.getLayers().get(0),
                new HealthBar(new Sprite(barBGTexture), new Sprite (barHealthTexture)));

            newEnemy.maxHealth = 100f;
            newEnemy.health = newEnemy.maxHealth;
            newEnemy.speed = 1.5f;
            newEnemy.damage = 25f;

            enemies.add(newEnemy);
        }

        int[] position = ENEMY_POSITIONS_SPAWN[random.nextInt(0,6)];
        newEnemy.setPosition(position[0], position[1]);
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
                gameState = GameState.UPGRADE_SCREEN;
                Table table = choosePowers();
                stage.addActor(table);
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
