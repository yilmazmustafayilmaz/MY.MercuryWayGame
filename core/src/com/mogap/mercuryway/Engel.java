package com.mogap.mercuryway;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Engel {

    Vector2 pozisyon = new Vector2();
    TextureRegion resim;
    boolean gecildi;

    public Engel(float x, float y, TextureRegion resim, boolean isDown) {
        this.pozisyon.x = x;
        this.pozisyon.y =y;
        this.resim = resim;
    }
}
