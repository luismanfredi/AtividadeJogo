package io.github.luismanfredi.atividadejogo;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Player extends Sprite {
    public Vector2 velocity = new Vector2(0f, 0f);
    public Direction direction = Direction.RIGHT;
    public Rectangle box = new Rectangle();

    private final float MAX_HEALTH = 100f;
    public float health;
    private float speed = 7f;
    private float gravity = -20f;
    private float jumpStrength = 8f;
    public int xp = 0;
    public int[] xpNeeded = {100, 120, 160, 200, 240};
    private int playerLevel = 0;
    private boolean grounded = false;
    public boolean isDead = false;

    public Vector2 knockbackVelocity = new Vector2(0f, 0f);
    private final float KNOCKBACK_DECAY = 5f;

    public boolean isInvulnerable = false;
    public float invulnerabilityTimer = 0f;
    private final float INVULNERABILITY_DURATION = 1.0f;

    private TiledMapTileLayer collisionLayer;

    public Player(Sprite sprite, TiledMapTileLayer collisionLayer) {
        super(sprite);
        this.collisionLayer = collisionLayer;
        setSize(1f, 1f);
        box.setSize(1f, 1f);
        this.health = this.MAX_HEALTH;
    }

    public void update(float delta) {
        if (xp >= xpNeeded[playerLevel]) {
            xp = 0;
            playerLevel += 1;
        }

        if (isInvulnerable) {
            invulnerabilityTimer -= delta;
            if ((int)(invulnerabilityTimer * 15) % 2 == 0) {
                setColor(1, 1, 1, 0.3f);
            } else {
                setColor(1, 1, 1, 1f);
            }

            if (invulnerabilityTimer <= 0) {
                isInvulnerable = false;
                setColor(1, 1, 1, 1f);
            }
        }

        translateX(knockbackVelocity.x * delta);
        translateY(knockbackVelocity.y * delta);

        if (health <= 0) {
            isDead = true;
        }

        if (!grounded) {
            velocity.y += gravity * delta;
        }

        if (!isCellBlocked(getX() + 0.1f, getY() - 0.01f)  || !isCellBlocked(getX() + getWidth() - 0.01f, getY() - 0.01f)) {
            grounded = false;
        }

        float totalVelX = velocity.x + knockbackVelocity.x;
        float oldX = getX();
        translateX(totalVelX * delta);

        if (totalVelX > 0) {
            if (isCellBlocked(getX() + getWidth(), getY()) || isCellBlocked(getX() + getWidth(), getY() + getHeight() - 0.01f)) {
                setX(oldX);
                velocity.x = 0;
                knockbackVelocity.x = 0;
            }
        } else if (totalVelX < 0) {
            if (isCellBlocked(getX() , getY()) || isCellBlocked(getX(), getY() + getHeight() - 0.01f)) {
                setX(oldX);
                velocity.x = 0;
                knockbackVelocity.x = 0;
            }
        }

        float totalVelY = velocity.y + knockbackVelocity.y;
        float oldY = getY();
        translateY(velocity.y * delta);
        grounded = false;

        if (totalVelY < 0) {
            if (isCellBlocked(getX() + 0.1f, getY()) || isCellBlocked(getX() + getWidth() - 0.1f, getY())) {
                setY((float) Math.floor(getY() + 1f));
                velocity.y = 0;
                knockbackVelocity.y = 0;
                grounded = true;
            }
        } else if (totalVelY > 0) {
            if (isCellBlocked(getX() + 0.1f, getY() + getHeight()) || isCellBlocked(getX() + getWidth() - 0.1f, getY() + getHeight())) {
                setY(oldY);
                velocity.y = 0;
                knockbackVelocity.y = 0;
            }
        }

        knockbackVelocity.x = MathUtils.lerp(knockbackVelocity.x, 0, KNOCKBACK_DECAY * delta);
        knockbackVelocity.y = MathUtils.lerp(knockbackVelocity.y, 0, KNOCKBACK_DECAY * delta);

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

    public void takeDamage(float damage) {
        if (!isInvulnerable) {
            health -= damage;

            isInvulnerable = true;
            invulnerabilityTimer = INVULNERABILITY_DURATION;


        }
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
