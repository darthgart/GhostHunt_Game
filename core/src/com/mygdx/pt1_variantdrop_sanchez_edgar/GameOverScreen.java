package com.mygdx.pt1_variantdrop_sanchez_edgar;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import jdk.internal.net.http.common.Log;

public class GameOverScreen implements Screen {
    final MainMenu game;
    private Texture backgroundTexture, retryButtonTexture, exitButtonTexture, ghost;
    private Sprite background;
    private Stage stage;
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private int score;
    private FreeTypeFontGenerator generator;
    private BitmapFont font;

    public GameOverScreen(final MainMenu game, int score) {
        this.game = game;
        this.score = score;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        backgroundTexture = new Texture(Gdx.files.internal("background_gameover.png"));
        ghost = new Texture(Gdx.files.internal("ghost1.png"));
        background = new Sprite(backgroundTexture);
        background.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        retryButtonTexture = new Texture(Gdx.files.internal("start_button.png"));
        exitButtonTexture = new Texture(Gdx.files.internal("quit_button.png"));

        generator = new FreeTypeFontGenerator(Gdx.files.internal("Minecraft.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.color = com.badlogic.gdx.graphics.Color.WHITE;
        parameter.size = 100;
        font = generator.generateFont(parameter);

        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        // Botón Start Game
        ImageButton startButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("start_button.png")))));
        startButton.setSize(400, 200);
        startButton.setPosition(Gdx.graphics.getWidth() / 2 - startButton.getWidth() - 100, Gdx.graphics.getHeight() / 4 - startButton.getHeight() / 2);
        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new GhostGame(game));
                dispose();
            }
        });

        // Botón Quit Game
        ImageButton quitButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("quit_button.png")))));
        quitButton.setSize(400, 200);
        quitButton.setPosition(Gdx.graphics.getWidth() / 2 + 100, Gdx.graphics.getHeight() / 4 - quitButton.getHeight() / 2);
        quitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new Ghostbuster(game));
                dispose();
            }
        });

        stage.addActor(startButton);
        stage.addActor(quitButton);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        background.draw(batch);
        font.draw(batch, "Score: " + score, Gdx.graphics.getWidth() / 2 - 200, Gdx.graphics.getHeight() / 2 );
        batch.end();
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        batch.dispose();
        backgroundTexture.dispose();
        retryButtonTexture.dispose();
        exitButtonTexture.dispose();
    }
}