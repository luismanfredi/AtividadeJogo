package io.github.luismanfredi.atividadejogo;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class Power  {
    public ImageButton button;
    public boolean active = false;

    public Power(Texture texture) {
        this.button = new ImageButton(new TextureRegionDrawable(texture));
    }
}
