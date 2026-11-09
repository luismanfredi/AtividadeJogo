package screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import io.github.luismanfredi.atividadejogo.Core;

public class TitleScreen implements Screen {

    private final Core game;
    private Stage stage;
    private Texture menuTexture;
    private Texture titleTexture;
    private Texture quitTexture;
    private Texture startTexture;

    public TitleScreen(Core game) {
        this.game = game;
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void show() {
        stage = new Stage(new ExtendViewport(1280, 720), game.spriteBatch);
        Gdx.input.setInputProcessor(stage); // faz o stage receber cliques

        menuTexture = new Texture(Gdx.files.internal("ui/menu.png"));
        titleTexture = new Texture(Gdx.files.internal("ui/game_title.png"));
        startTexture = new Texture(Gdx.files.internal("ui/button_play.png"));
        quitTexture = new Texture(Gdx.files.internal("ui/button_exit.png"));

        Image title = new Image(titleTexture);

        Image background = new Image(menuTexture);
        background.setFillParent(true);
        stage.addActor(background);

        ImageButton startButton = new ImageButton(new TextureRegionDrawable(startTexture));
        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new PlayScreen(game));
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

        Table table = new Table();
        table.setFillParent(true); // ocupa stage inteiro
        stage.addActor(table);


        table.add(title).center().width(500).height(120);
        table.row().padTop(100);
        table.add(startButton).center().width(380).height(120);
        table.row().padTop(80);
        table.add(quitButton).center().width(380).height(120);
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
    }

    @Override
    public void dispose() {
        stage.dispose();
        menuTexture.dispose();
        startTexture.dispose();
        quitTexture.dispose();
    }

}
