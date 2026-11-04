package io.github.luismanfredi.atividadejogo;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Enemy extends Sprite {
    public Vector2 velocity = new Vector2(0f, 0f);
    public Rectangle box = new Rectangle();

    private float health = 30f;
    private float speed = 3f;
    private float gravity = -20f;
    private float jumpStrength = 8f;
    boolean grounded = false;
    public boolean isDead = false;

    private TiledMapTileLayer collisionLayer;

    public Enemy(Sprite sprite, TiledMapTileLayer collisionLayer) {
        super(sprite);
        this.collisionLayer = collisionLayer;
        setSize(1f, 1f);
        box.setSize(1f, 1f);
    }

    public void update(float delta, float x, float y) {
        if (health <= 0) {
            isDead = true;
        }

        if (!grounded) {
            velocity.y += gravity * delta;
        }

        if (!isCellBlocked(getX() + 0.1f, getY() - 0.01f)  || !isCellBlocked(getX() + getWidth() - 0.01f, getY() - 0.01f)) {
            grounded = false;
        }

        if (x > getX()) {
            moveRight();
        } else if (x < getX()) {
            moveLeft();
        }

        float oldX = getX();
        translateX(velocity.x * delta);

        if (velocity.x > 0) {
            if (isCellBlocked(getX() + getWidth(), getY()) || isCellBlocked(getX() + getWidth(), getY() + getHeight() - 0.01f)) {
                setX(oldX);
                velocity.x = 0;
                jump();
            }
        } else if (velocity.x < 0) {
            if (isCellBlocked(getX() , getY()) || isCellBlocked(getX(), getY() + getHeight() - 0.01f)) {
                setX(oldX);
                velocity.x = 0;
                jump();
            }
        }

        float oldY = getY();
        translateY(velocity.y * delta);
        grounded = false;

        if (velocity.y < 0) {
            if (isCellBlocked(getX() + 0.1f, getY()) || isCellBlocked(getX() + getWidth() - 0.1f, getY())) {
                setY((float) Math.floor(getY() + 1f));
                velocity.y = 0;
                grounded = true;
            }
        } else if (velocity.y > 0) {
            if (isCellBlocked(getX() + 0.1f, getY() + getHeight()) || isCellBlocked(getX() + getWidth() - 0.1f, getY() + getHeight())) {
                setY(oldY);
                velocity.y = 0;
            }
        }

        if (getY() <= 0) {
            setY(0);
            velocity.y = 0;
            grounded = true;
        }

        box.setPosition(getX(), getY());
    }

    private boolean isCellBlocked(float x, float y) {
        if (collisionLayer == null) return false;

        int tileX = (int) Math.floor(x);
        int tileY = (int) Math.floor(y);

        if (tileX < 0 || tileX >= collisionLayer.getWidth() || tileY < 0 || tileY >= collisionLayer.getHeight()) {
            return true;
        }

        TiledMapTileLayer.Cell cell = collisionLayer.getCell(tileX, tileY);

        return cell != null && cell.getTile() != null;
    }

    public void takeDamage() {
        health -= 15f;
    }

    public void jump() {
        if (grounded) {
            velocity.y = jumpStrength;
            grounded = false;
        }
    }

    public void moveRight() {
        velocity.x = speed;
    }

    public void moveLeft() {
        velocity.x = -speed;
    }
}
