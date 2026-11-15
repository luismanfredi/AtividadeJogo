package io.github.luismanfredi.atividadejogo;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class Power  {
    public ImageButton button;
    public boolean active = false;
    public PowerType powerType;

    public Power(Texture texture, PowerType powerType) {
        this.button = new ImageButton(new TextureRegionDrawable(texture));
        this.powerType = powerType;
    }

    public void update(Player player) {
        if (powerType == PowerType.MORE_SPEED) {
            player.speed = 9f;
        } else if (powerType == PowerType.JUMP_BOOST) {
            player.jumpStrength = 14f;
            player.gravity = -26f;
        } else if (powerType == PowerType.MORE_DMG) {
            player.damage = 18f;
        }
    }
}
