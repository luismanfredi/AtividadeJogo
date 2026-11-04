package io.github.luismanfredi.atividadejogo;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class HealthBar extends Sprite {

    public Sprite hBar;
    private float barWidth = 5f;

    public HealthBar(Sprite bgBar, Sprite hBar){
        super(bgBar);
        this.hBar = hBar;
        setSize(5f,1f);
        hBar.setSize(5f,1f);
    }

    public void update(float delta, OrthographicCamera camera, float playerHealth){
        setPosition(camera.position.x - 15f,camera.position.y - 9f);
        hBar.setPosition(camera.position.x - 15f, camera.position.y - 9f);
        hBar.setSize(barWidth * (playerHealth / 100f), 1f);
    }

}
