package io.github.luismanfredi.atividadejogo;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;

public class Player extends Sprite {
    public Vector2 velocity = new Vector2(0f, 0f);
    public Direction direction = Direction.RIGHT;

    private float speed = 4f;
    private float gravity = -20f;
    private float jumpStrength = 8f;
    private boolean grounded = false;

    private TiledMapTileLayer collisionLayer;

    public Player(Sprite sprite, TiledMapTileLayer collisionLayer) {
        super(sprite);
        this.collisionLayer = collisionLayer;
        setSize(1f, 1f);
    }

    public void update(float delta) {
        if (!grounded) {
            velocity.y += gravity * delta;
        }

        if (!isCellBlocked(getX() + 0.1f, getY() - 0.01f)  || !isCellBlocked(getX() + getWidth() - 0.01f, getY() - 0.01f)) {
            grounded = false;
        }

        float oldX = getX();
        translateX(velocity.x * delta);

        if (velocity.x > 0) {
            if (isCellBlocked(getX() + getWidth(), getY()) || isCellBlocked(getX() + getWidth(), getY() + getHeight() - 0.01f)) {
                setX(oldX);
                velocity.x = 0;
            }
        } else if (velocity.x < 0) {
            if (isCellBlocked(getX() , getY()) || isCellBlocked(getX(), getY() + getHeight() - 0.01f)) {
                setX(oldX);
                velocity.x = 0;
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

    public void draw(SpriteBatch batch) {
        super.draw(batch);
    }
}
