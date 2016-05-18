package com.example.juliandobrot.artest;

import android.content.Context;
import android.view.MotionEvent;

import com.google.atap.tangoservice.TangoPoseData;

import org.rajawali3d.lights.DirectionalLight;
import org.rajawali3d.materials.Material;
import org.rajawali3d.materials.textures.ATexture;
import org.rajawali3d.materials.textures.StreamingTexture;
import org.rajawali3d.primitives.ScreenQuad;
import org.rajawali3d.renderer.RajawaliRenderer;

/**
 * Created by Julian Dobrot on 18.05.2016.
 */
public class RoomerRender extends RajawaliRenderer {


    public RoomerRender(Context context) {
        super(context);
    }

    @Override
    protected void initScene() {

        ScreenQuad screenQuad = new ScreenQuad();
        Material material = new Material();
        material.setColorInfluence(0);
        StreamingTexture streamingTexture = new StreamingTexture("camera", (StreamingTexture.ISurfaceListener)null);

        try {
            material.addTexture(streamingTexture);
            screenQuad.setMaterial(material);
        } catch (ATexture.TextureException e) {
            e.printStackTrace();
        }
        getCurrentScene().addChildAt(screenQuad,0);

        DirectionalLight directionalLight = new DirectionalLight(1,0.2,-1);
        directionalLight.setColor(1,1,1);
        directionalLight.setPower(0.8f);
        directionalLight.setPosition(3,2,4);

        getCurrentScene().addLight(directionalLight);



    }

    @Override
    public void onOffsetsChanged(float xOffset, float yOffset, float xOffsetStep, float yOffsetStep, int xPixelOffset, int yPixelOffset) {

    }

    @Override
    public void onTouchEvent(MotionEvent event) {

    }
    public void updateRenderComeraPose (TangoPoseData tangoPoseData) {

    }
}
