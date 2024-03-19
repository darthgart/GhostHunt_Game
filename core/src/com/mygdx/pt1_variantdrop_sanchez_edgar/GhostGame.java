package com.mygdx.pt1_variantdrop_sanchez_edgar;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

public class GhostGame implements Screen {
    final MainMenu game;
    Texture ghost, badGhost, ghostmini, trap, heartFull, heartEmpty, pause, backgroundTexture;
    Rectangle bucket;
    Music ghostMusic;
    Sound laserSound, lostSound;
    int dropsGathered;
    Sprite background;
    OrthographicCamera camera;
    Array<Rectangle> ghostDrops, badGhostDrops;
    int lives, ghostsCaptured, ghostsCapturedSinceLastSpeedIncrease;
    int ghostsPerSpawn;
    private float fallSpeed, initialFallSpeed;
    private long lastDropTime, ghostSpawnFrequency, ghostSpawnIncreaseInterval, ghostSpawnIncreaseAmount, timeSinceLastIncrease, initialGhostSpawnFrequency;


    public GhostGame (final MainMenu game) {
        this.game = game;
        //load images
        ghost = new Texture(Gdx.files.internal("ghost1.png"));
        badGhost = new Texture(Gdx.files.internal("bad_ghost.png"));
        trap = new Texture(Gdx.files.internal("ghost_trap.png"));
        pause = new Texture(Gdx.files.internal("pause.png"));
        ghostmini = new Texture(Gdx.files.internal("ghostmini.png"));
        heartFull = new Texture(Gdx.files.internal("heart_full.png"));
        heartEmpty = new Texture(Gdx.files.internal("heart_empty.png"));
        background = new Sprite(new Texture(Gdx.files.internal("background.png")));
        //load music
        ghostMusic = Gdx.audio.newMusic(Gdx.files.internal("sound_base2.wav"));
        laserSound = Gdx.audio.newSound(Gdx.files.internal("laserSound.mp3"));
        lostSound = Gdx.audio.newSound(Gdx.files.internal("lost.wav"));
        ghostMusic.setLooping(true);
        //create camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);
        //create a Rectangle to logically represent the bucket
        bucket = new Rectangle();
        bucket.x = 800 / 2 - 64 / 2;
        bucket.y = 20;
        bucket.width = 64;
        bucket.height = 64;
        //define lives, ghostsCaptured and fall speed
        lives = 3;
        ghostSpawnFrequency = 500000000L; // 0.5 segundos
        initialFallSpeed = 200; // Velocidad de caída inicial
        ghostSpawnIncreaseInterval = 20000000000L; // 20 segundos
        ghostSpawnIncreaseAmount = 1000000000L; // 0.01 segundos
        ghostsCapturedSinceLastSpeedIncrease = 0;
        ghostsPerSpawn = 1;
        ghostsCaptured = 0;
        fallSpeed = initialFallSpeed;
        initialGhostSpawnFrequency = 1000000000L; // 1 segundo
        //create the ghostDrops array and spawn the first ghost
        ghostDrops = new Array<Rectangle>();
        badGhostDrops = new Array<Rectangle>();
        spawnGhost();
    }

    @Override
    public void show() {
        ghostMusic.play();
    }

    @Override
    public void render(float delta) {
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        renderBackground();
        game.batch.draw(ghostmini, 10, 420);
        game.font.draw(game.batch, " : " + dropsGathered, 30, 435);
        game.batch.draw(trap, bucket.x, bucket.y,bucket.width, bucket.height);
        for(Rectangle ghostdrop: ghostDrops) {
            game.batch.draw(ghost, ghostdrop.x, ghostdrop.y);
        }
        for(Rectangle badGhostDrop: badGhostDrops) {
            game.batch.draw(badGhost, badGhostDrop.x, badGhostDrop.y);
        }
        game.batch.end();

        livesGame();

        if(Gdx.input.isTouched()) {
            Vector3 touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
            bucket.x = touchPos.x - 64 / 2;
        }

        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) bucket.x -= 200 * Gdx.graphics.getDeltaTime();
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) bucket.x += 200 * Gdx.graphics.getDeltaTime();

        if (bucket.x < 0)  bucket.x = 0;
        if (bucket.x > 800 - 64) bucket.x = 800 - 64;

        if(TimeUtils.nanoTime() - lastDropTime > 1000000000) spawnGhost();

        // Aumenta la frecuencia de aparición de fantasmas cada vez que pase el intervalo de aumento
        timeSinceLastIncrease += Gdx.graphics.getDeltaTime();
        if (timeSinceLastIncrease > ghostSpawnIncreaseInterval) {
            ghostSpawnFrequency -= ghostSpawnIncreaseAmount;
            if (ghostSpawnFrequency < 0) {
                ghostSpawnFrequency = 0;
            }
            timeSinceLastIncrease = 0;
        }

        // Genera fantasmas si ha pasado el tiempo de aparición
        if (TimeUtils.nanoTime() - lastDropTime > ghostSpawnFrequency) {
            spawnGhost();
        }

        if (ghostsCaptured % 20 == 0 && ghostsCaptured != 0) {
            fallSpeed += 2;
        }

        Iterator<Rectangle> iter = ghostDrops.iterator();
        while (iter.hasNext()) {
            Rectangle raindrop = iter.next();
            raindrop.y -= fallSpeed * Gdx.graphics.getDeltaTime();
            if (raindrop.y + 64 < 0){
                lostSound.play();
                lives--;
                iter.remove();
            }
            if (raindrop.overlaps(bucket)) {
                laserSound.play();
                dropsGathered++;
                ghostsCaptured++;
                iter.remove();
            }
        }

        Iterator<Rectangle> badIter = badGhostDrops.iterator();
        while (badIter.hasNext()) {
            Rectangle badGhostDrop = badIter.next();
            badGhostDrop.y -= fallSpeed * Gdx.graphics.getDeltaTime();
            if (badGhostDrop.y + 64 < 0){
                lostSound.play();
                badIter.remove();
            }
            if (badGhostDrop.overlaps(bucket)) {
                game.setScreen(new GameOverScreen(game, getScore()));
                badIter.remove();
            }
        }
    }

    @Override
    public void dispose () {
        ghost.dispose();
        trap.dispose();
    }

    private void spawnGhost() {
        int random = MathUtils.random(0, 10); // Genera un número aleatorio entre 0 y 3
        if (random == 5) {
            spawnBadGhost(); // Si el número es 3, genera un mal fantasma
        } else {
            spawnGoodGhost(); // Si no, genera un fantasma normal
        }
    }

    private void spawnGoodGhost() {
        for (int i = 0; i < ghostsPerSpawn; i++) {
            Rectangle ghostdrop = new Rectangle();
            ghostdrop.x = MathUtils.random(0, 800 - 64);
            ghostdrop.y = 480;
            ghostdrop.width = 64;
            ghostdrop.height = 64;
            ghostDrops.add(ghostdrop);
        }
        lastDropTime = TimeUtils.nanoTime();
    }

    private void spawnBadGhost() {
        for (int i = 0; i < ghostsPerSpawn; i++) {
            Rectangle badGhostdrop = new Rectangle();
            badGhostdrop.x = MathUtils.random(0, 800 - 64);
            badGhostdrop.y = 480;
            badGhostdrop.width = 64;
            badGhostdrop.height = 64;
            badGhostDrops.add(badGhostdrop);
        }
        lastDropTime = TimeUtils.nanoTime();
    }

    public void renderBackground() {
        game.batch.draw(background,0, 0, 800, 480);
    }

    public void livesGame(){
        if (lives == 3) {
            game.batch.begin();
            game.batch.draw(heartFull, 10, 450);
            game.batch.draw(heartFull, 40, 450);
            game.batch.draw(heartFull, 70, 450);
            game.batch.end();
        } else if (lives == 2) {
            game.batch.begin();
            game.batch.draw(heartFull, 10, 450);
            game.batch.draw(heartFull, 40, 450);
            game.batch.draw(heartEmpty, 70, 450);
            game.batch.end();
        } else if (lives == 1) {
            game.batch.begin();
            game.batch.draw(heartFull, 10, 450);
            game.batch.draw(heartEmpty, 40, 450);
            game.batch.draw(heartEmpty, 70, 450);
            game.batch.end();
        } else if (lives <= 0) {
            ghostMusic.stop();
            game.setScreen(new GameOverScreen(game, getScore()));
            dispose();
        }
    }

    public int getScore() {
        return ghostsCaptured;
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
        ghostMusic.stop();
    }


}
