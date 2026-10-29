package io.github.luismanfredi.atividadejogo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Player {
    public Sprite sprite;

    private float velocityX = 4f;
    private float velocityY = 0f;
    private float gravity = -20f;
    private float jumpStrengh = 8f;

    private boolean grounded = true;

    public Player(Texture texture) {
        sprite = new Sprite(texture);
        sprite.setSize(1, 1);
    }

    public void update(float delta) {
        if (!grounded) {
            velocityY += gravity * delta;
            sprite.translateY(velocityY * delta);

            if (sprite.getY() <= 0) {
                sprite.setY(0);
                velocityY = 0;
                grounded = true;
            }
        }
    }

    public void jump() {
        if (grounded) {
            velocityY = jumpStrengh;
            grounded = false;
        }
    }

    public void moveRight(float delta) {
        sprite.translateX(velocityX * delta);
    }

    public void moveLeft(float delta) {
        sprite.translateX(-velocityX * delta);
    }

    public void draw(SpriteBatch batch) {
        sprite.draw(batch);
    }
}
