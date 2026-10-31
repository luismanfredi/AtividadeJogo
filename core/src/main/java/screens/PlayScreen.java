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
        this.game = game; // construtor para passar o Core
    }

    @Override
    public void render(float delta) {
        input(delta); // input de comandos, delta é o tempo desde o ultimo frame
        logic(delta); // por enquanto não permite que o personagem saia da tela

        player.update(delta);

        ScreenUtils.clear(Color.BLACK); // limpa a tela

        camera.update(); // recalcula a projeção da camera, sempre usar depois de mexer com a camera
        renderer.setView(camera);
        renderer.render(); // renderiza o mapa

        game.spriteBatch.setProjectionMatrix(camera.combined); // como o viewport é aplicado ao spritebatch, para imagens aparecem no lugar certo

        game.spriteBatch.begin(); // todas chamadas de draw deve estar entre .begin() e .end()

        player.draw(game.spriteBatch); // desenha o player na tela

        game.spriteBatch.end();
    }

    private void input(float delta) {
        player.velocity.x = 0;

        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            player.moveRight(); // quanto o sprite mexe por segundo
        }

        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            player.moveLeft();
        }

        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            player.jump();
        }

        if (Gdx.input.isTouched()) {
            // se clicou
        }
    }

    private void logic(float delta) {
        float playerWidth = player.getWidth();
        player.setX(MathUtils.clamp(player.getX(), 0, MAP_WIDTH - playerWidth)); // verifica se o player está no tamnho certo, se não fixa ele no maximo
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void show() {
        float scale = 1f / 32f; // escala do tela

        camera = new OrthographicCamera();

        // a altura e largura fixas
        viewport = new FitViewport(MAP_WIDTH, MAP_HEIGHT, camera);

        TmxMapLoader loader = new TmxMapLoader(); // objeto para carregar mapa .tmx
        map = loader.load("mapa/MAPA.tmx"); // carrega o mapa
        renderer = new OrthogonalTiledMapRenderer(map, scale); // renderer do mapa

        playerTexture = new Texture(Gdx.files.internal("player.png")); // textura do personagem
        player = new Player(new Sprite(playerTexture), (TiledMapTileLayer) map.getLayers().get(0)); // cria um Player com a textura do personagem
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
