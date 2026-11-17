package screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
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
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import io.github.luismanfredi.atividadejogo.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class PlayScreen implements Screen {

    private final Core game;
    private Stage upgradeStage;
    private Stage defeatStage;
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

    private ArrayList<Power> powers = new ArrayList<>();
    private Table table;
    private Table defeatTable;

    private final Bullet BULLET_MODEL = new Bullet();

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
                Gdx.input.setInputProcessor(null);
                input(delta);
                logic(delta);
                enemySpawner(delta);
                bulletSpawner(delta);

                player.update(delta);
                if (player.isDead) {
                    Gdx.input.setInputProcessor(defeatStage);
                    gameState = GameState.DEFEAT_SCREEN;
                }

                if (player.playerUpgraded) {
                    if (player.level <= player.MAX_LEVEL) {
                        Gdx.input.setInputProcessor(upgradeStage);
                        gameState = GameState.UPGRADE_SCREEN;
                        table.clear();
                        choosePowers();
                    }
                }

                healthBar.update(delta, camera, player.health);
                drawGame();
                break;
            case UPGRADE_SCREEN:
                drawGame();
                upgradeStage.act(delta);
                upgradeStage.draw();
                break;
            case DEFEAT_SCREEN:
                drawGame();
                defeatStage.act(delta);
                defeatStage.draw();
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

        upgradeStage = new Stage(new ExtendViewport(1280, 900), game.spriteBatch);

        Texture BGTexture = new Texture(Gdx.files.internal("upgradeUI/upgrade_bg.png"));
        Texture defeatBGTexture = new Texture(Gdx.files.internal("ui/defeat_bg.png"));
        Texture moreDMGTexture = new Texture(Gdx.files.internal("upgradeUI/more_dmg.png"));
        Texture doubleShootTexture = new Texture(Gdx.files.internal("upgradeUI/double_shoot.png"));
        Texture frozenTexture = new Texture(Gdx.files.internal("upgradeUI/frozen.png"));
        Texture higherJumpTexture = new Texture(Gdx.files.internal("upgradeUI/higher_jump.png"));
        Texture fasterTexture = new Texture(Gdx.files.internal("upgradeUI/faster.png"));
        Texture fasterBulletTexture = new Texture(Gdx.files.internal("upgradeUI/faster_bullet.png"));
        Texture moreHealthTexture = new Texture(Gdx.files.internal("upgradeUI/more_health.png"));

        Image upgradeBG = new Image(BGTexture);
        Image defeatBG = new Image(defeatBGTexture);

        Power moreDMG = new Power(moreDMGTexture, Powers.MORE_DMG, PowerType.PLAYER);
        Power doubleShoot = new Power(doubleShootTexture, Powers.DOUBLE_SHOOT, PowerType.BULLET);
        Power frozen = new Power(frozenTexture, Powers.FROZEN, PowerType.BULLET);
        Power higherJump = new Power(higherJumpTexture, Powers.JUMP_BOOST, PowerType.PLAYER);
        Power faster = new Power(fasterTexture, Powers.MORE_SPEED, PowerType.PLAYER);
        Power fasterBullet = new Power(fasterBulletTexture, Powers.FASTER_BULLET, PowerType.BULLET);
        Power moreHealth = new Power(moreHealthTexture, Powers.MORE_HEALTH, PowerType.PLAYER);

        powers.add(moreDMG);
        powers.add(doubleShoot);
        powers.add(frozen);
        powers.add(higherJump);
        powers.add(faster);
        powers.add(fasterBullet);
        powers.add(moreHealth);

        for (Power power : powers) {
            powerClick(power);
        }

        Table tableBG = new Table();
        tableBG.setFillParent(true);
        tableBG.add(upgradeBG).center().expand().width(570).height(760);
        upgradeStage.addActor(tableBG);

        table = new Table();
        table.setFillParent(true);
        upgradeStage.addActor(table);

        Texture youDiedTexture = new Texture(Gdx.files.internal("ui/died.png"));
        Texture quitTexture = new Texture(Gdx.files.internal("ui/button_exit.png"));

        ImageButton quitButton = new ImageButton(new TextureRegionDrawable(quitTexture));
        quitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
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

        Image youDied = new Image(youDiedTexture);

        defeatStage = new Stage(new ExtendViewport(1280, 720), game.spriteBatch);

        Table defeatBGTable = new Table();
        defeatBGTable.setFillParent(true);
        defeatBGTable.add(defeatBG).center().expand().width(570).height(460);
        defeatStage.addActor(defeatBGTable);

        defeatTable = new Table();
        defeatTable.setFillParent(true);
        defeatTable.add(youDied).center().width(310).height(150);
        defeatTable.row().padTop(40);
        defeatTable.add(quitButton).center().width(380).height(120);

        defeatStage.addActor(defeatTable);
    }

    private void choosePowers() {
        ArrayList<Power> AvailablePowers = new ArrayList<>();
        for (Power power : powers) {
            if (!power.active) {
                AvailablePowers.add(power);
            }
        }

        int powerCounter = Math.min(4, AvailablePowers.size());

        for (int i = 0; i < powerCounter; i++) {
            int randomIndex = random.nextInt(AvailablePowers.size());

            Power power = AvailablePowers.remove(randomIndex);

            if (power.active) {
                continue;
            }

            table.add(power.button).center().width(450).height(120);
            table.row().padTop(50);
        }
    }

    private void powerClick(Power power) {
        power.button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                power.active = true;
                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);

                if (power.powerType == PowerType.PLAYER) {
                    power.update(player);
                } else if (power.powerType == PowerType.BULLET) {
                    if (power.powers == Powers.DOUBLE_SHOOT) {
                        BULLET_MODEL.doubleShoot = true;
                    } else if (power.powers == Powers.FROZEN) {
                        BULLET_MODEL.frozen = true;
                    } else if (power.powers == Powers.FASTER_BULLET) {
                        BULLET_MODEL.fasterBullet = true;
                    }
                }
                gameState = GameState.RUNNING;
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

            if (BULLET_MODEL.doubleShoot) {
                Bullet newBullet2 = new Bullet(new Sprite(bulletTexture), player.direction, (TiledMapTileLayer) map.getLayers().get(0));
                newBullet2.spawn(player.getX(), player.getY(), player.direction);
                newBullet.spawn(player.getX(), player.getY() - 0.2f, player.direction);
                bullets.add(newBullet);
                bullets.add(newBullet2);
            }
            else {
                newBullet.spawn(player.getX(), player.getY(), player.direction);
                bullets.add(newBullet);
            }

            if (BULLET_MODEL.fasterBullet) {
                for (Bullet bullet : bullets) {
                    bullet.velocity.x = 24f;
                }
            }
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
            newEnemy.speed = 7f;
            newEnemy.MAX_SPEED = newEnemy.speed;
            newEnemy.damage = 12.5f;

            enemies.add(newEnemy);
        } else {
            newEnemy = new Enemy(
                new Sprite(tankEnemyTexture),
                (TiledMapTileLayer) map.getLayers().get(0),
                new HealthBar(new Sprite(barBGTexture), new Sprite (barHealthTexture)));

            newEnemy.maxHealth = 100f;
            newEnemy.health = newEnemy.maxHealth;
            newEnemy.speed = 2.5f;
            newEnemy.MAX_SPEED = newEnemy.speed;
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
                    enemy.takeDamage(player.damage);
                    if (BULLET_MODEL.frozen) {
                        enemy.frozen(delta);
                    }
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
}
