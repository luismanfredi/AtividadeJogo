package io.github.luismanfredi.atividadejogo;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

public class Bullet extends Sprite {

    public Vector2 velocity = new Vector2(12f, 0f);
    Direction direction;
    private boolean dead = false;
    public float damage = 10f;

    public Rectangle box = new Rectangle();

    private TiledMapTileLayer collisionLayer;

    public Bullet(Sprite sprite, Direction direction, TiledMapTileLayer collisionLayer) {
        super(sprite);
        this.direction = direction;
        this.collisionLayer = collisionLayer;
        setSize(0.3f, 0.2f);
        box.setSize(0.3f, 0.2f);
    }

    public void update(float delta) {
        if (direction == Direction.RIGHT) {
            translateX(velocity.x * delta);
        } else if (direction == Direction.LEFT) {
            translateX(-velocity.x * delta);
        }

        if (velocity.x > 0) {
            if (isCellBlocked(getX() + getWidth(), getY()) || isCellBlocked(getX() + getWidth(), getY() + getHeight() - 0.01f)) {
                dead = true;
            }
        } else if (velocity.x < 0) {
            if (isCellBlocked(getX() , getY()) || isCellBlocked(getX(), getY() + getHeight() - 0.01f)) {
                dead = true;
            }
        }

        box.setPosition(getX(), getY());
    }

    public void spawn(float x, float y, Direction playerDirection) {
        this.direction = playerDirection;
        if (playerDirection == Direction.RIGHT) {
            setPosition(x + 1f, y + 0.5f);
        } else if (playerDirection == Direction.LEFT) {
            setPosition(x - 0.3f, y + 0.5f);
            flip(true, false);
        }
    }

    public boolean isDead() {
        return dead;
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
}


