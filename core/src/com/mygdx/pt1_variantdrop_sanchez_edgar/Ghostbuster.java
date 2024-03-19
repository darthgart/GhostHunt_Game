package com.mygdx.pt1_variantdrop_sanchez_edgar;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

public class Ghostbuster implements Screen {
	private SpriteBatch batch;
	private Music baseMusic;
	private Sound laserSound;
	private Texture backgroundTexture, infoBackgroundTexture;
	private Sprite background;
	private Stage stage;
	private OrthographicCamera camera;
	final MainMenu game;

	private FreeTypeFontGenerator generator;
	private BitmapFont font;


	public Ghostbuster(final MainMenu game) {
		this.game = game;
		camera = new OrthographicCamera();
		camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}

	@Override
	public void show() {
		batch = new SpriteBatch();
		camera = new OrthographicCamera();
		camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		backgroundTexture = new Texture(Gdx.files.internal("mainmenu.png"));
		infoBackgroundTexture = new Texture(Gdx.files.internal("background_info.png"));
		background = new Sprite(backgroundTexture);
		background.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		laserSound = Gdx.audio.newSound(Gdx.files.internal("laserSound.mp3"));
		baseMusic = Gdx.audio.newMusic(Gdx.files.internal("sound_base.mp3"));
		baseMusic.setLooping(true);
		baseMusic.play();

		generator = new FreeTypeFontGenerator(Gdx.files.internal("Minecraft.ttf"));
		FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		parameter.color = com.badlogic.gdx.graphics.Color.WHITE;
		parameter.borderColor = com.badlogic.gdx.graphics.Color.BLACK;
		parameter.size = 80;
		font = generator.generateFont(parameter);

		stage = new Stage();
		Gdx.input.setInputProcessor(stage);

		// Botón Start Game
		ImageButton startButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("start_button.png")))));
		startButton.setSize(400, 200);
		startButton.setPosition(Gdx.graphics.getWidth() / 2 - startButton.getWidth() / 2, Gdx.graphics.getHeight() / 4 - startButton.getHeight() / 2);
		startButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				laserSound.play();
				baseMusic.stop();
				game.setScreen(new GhostGame(game));
				dispose();
			}
		});

		// Botón Quit Game
		ImageButton quitButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("quit_button.png")))));
		quitButton.setSize(400, 200);
		quitButton.setPosition(Gdx.graphics.getWidth() / 2 - quitButton.getWidth() / 2, Gdx.graphics.getHeight() / 4 - quitButton.getHeight() * 1.5f);
		quitButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				laserSound.play();
				Gdx.app.exit();
			}
		});

		ImageButton infoButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("info_Button.png")))));
		infoButton.setSize(200, 100);
		infoButton.setPosition(Gdx.graphics.getWidth() - infoButton.getWidth() - 50, 40);
		infoButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				// Abre el modal con la información del juego
				openInfoModal();
			}
		});

		stage.addActor(startButton);
		stage.addActor(quitButton);
		stage.addActor(infoButton);
	}

	private void openInfoModal() {
		Dialog infoDialog = new Dialog("", new Window.WindowStyle(new BitmapFont(), Color.WHITE, null));
		Label.LabelStyle labelStyle = new Label.LabelStyle();
		labelStyle.font = font;
		labelStyle.fontColor = Color.WHITE;
		labelStyle.background = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("background_info.png"))));
		labelStyle.font.getData().setScale(0.5f);
		Label infoLabel = new Label("¡Bienvenido a Ghostbuster!\n\n" +
				"Este es un juego simple donde tu objetivo es atrapar fantasmas y evitar a los fantasmas malos.\n" +
				"Usa los controles izquierda y derecha para mover el atrapador de fantasmas.\n" +
				"¡Diviertete y buena suerte!", labelStyle);
		TextButton closeButton = new TextButton("Cerrar", new TextButton.TextButtonStyle(null, null, null, font));
		closeButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				infoDialog.hide();
			}
		});

		infoLabel.setAlignment(Align.center);
		infoLabel.setWrap(true);
		infoDialog.getContentTable().add(infoLabel).width(Gdx.graphics.getWidth() * 0.8f).pad(10);


		infoDialog.button(closeButton);
//		infoDialog.setWidth(Gdx.graphics.getWidth() * 0.9f);
//		infoDialog.setHeight(Gdx.graphics.getHeight() * 0.9f);
		infoDialog.setPosition(Gdx.graphics.getWidth() / 2f - infoDialog.getWidth() / 2f, Gdx.graphics.getHeight() / 2f - infoDialog.getHeight() / 2f);
		infoDialog.show(stage);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		background.draw(batch);
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
	public void dispose () {
		batch.dispose();
		backgroundTexture.dispose();
		stage.dispose();
	}
}
