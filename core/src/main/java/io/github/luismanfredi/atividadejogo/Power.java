package io.github.luismanfredi.atividadejogo;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class Power  {
    public ImageButton button;
    public boolean active = false;
    public Powers powers;

    public Power(Texture texture, Powers powers) {
        this.button = new ImageButton(new TextureRegionDrawable(texture));
        this.powers = powers;
    }

    public void update(Player player) {
        if (powers == Powers.MORE_SPEED) {
            player.speed = 9f;
        } else if (powers == Powers.JUMP_BOOST) {
            player.jumpStrength = 14f;
            player.gravity = -26f;
        } else if (powers == Powers.MORE_DMG) {
            player.damage = 18f;
        }
    }
}
