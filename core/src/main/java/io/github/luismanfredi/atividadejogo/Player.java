package io.github.luismanfredi.atividadejogo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class Player extends Sprite {

    public Vector2 velocity = new Vector2(0f, 0f); // velocidade de moviemnto

    private float speed = 4f;
    private float gravity = -20f;


    private float jumpStrengh = 8f;

    private boolean grounded = true;

    public Player(Sprite sprite) {
        super(sprite);
        setSize(1, 1);
    }

    public void update(float delta) {
        if (!grounded) {
            velocity.y += gravity * delta;
        }

        translateX(velocity.x * delta);
        translateY(velocity.y * delta);

        if (getY() <= 0) {
            setY(0);
            velocity.y = 0;
            grounded = true;
        }
    }

    public void jump() {
        if (grounded) {
            velocity.y = jumpStrengh;
            grounded = false;
        }
    }

    public void moveRight() {
        velocity.x = speed;
    }

    public void moveLeft() {
        velocity.x = -speed;
    }

    public void draw(SpriteBatch batch) {
        super.draw(batch);
    }
}
