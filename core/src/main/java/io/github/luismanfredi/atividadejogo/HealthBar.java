package io.github.luismanfredi.atividadejogo;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class HealthBar extends Sprite {

    public Sprite hBar;
    private float barWidth = 5f;
    public float time = 0f;

    public HealthBar(Sprite bgBar, Sprite hBar){
        super(bgBar);
        this.hBar = hBar;

    }

    public void create(float health, boolean isPlayer) {
        if (isPlayer) {
            setSize(barWidth,1f);
            hBar.setSize(barWidth,1f);
        } else {
            barWidth *= (health / 100);
            setSize(barWidth, 0.15f);
            setColor(1, 1, 1, 0);
            hBar.setColor(1, 1, 1, 0);
        }
    }

    public void update(float delta, OrthographicCamera camera, float playerHealth){
        setPosition(camera.position.x - 15f,camera.position.y - 9f);
        hBar.setPosition(camera.position.x - 15f, camera.position.y - 9f);
        hBar.setSize(barWidth * (playerHealth / 100f), 1f);
    }

    public void update(float delta, float x, float y, float enemyHealth, float maxHealth) {
        time -= delta;

        if (time > 0) {
            setPosition(x - 0.35f, y + 1.5f);
            hBar.setPosition(x - 0.35f, y + 1.5f);
            hBar.setSize(barWidth * (enemyHealth / maxHealth), 0.15f);
        } else {
            setColor(1, 1, 1, 0);
            hBar.setColor(1, 1, 1, 0);
        }

    }
}
