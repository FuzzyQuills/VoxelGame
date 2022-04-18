package org.voxelgame.rendering.lighting;

import org.joml.Vector3f;

public class PointLight {
    Vector3f position;
    Vector3f color;
    float intensity;
    Attenuation att;

    public PointLight(){
        position = new Vector3f(0.0f, 0.0f, 0.0f);
        color = new Vector3f(1.0f, 1.0f, 1.0f);
        intensity = 0.5f;
        att = new Attenuation(0.0f, 0.0f,1.0f);
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public Vector3f getColor() {
        return color;
    }

    public void setColor(Vector3f color) {
        this.color = color;
    }

    public float getIntensity() {
        return intensity;
    }

    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }

    public Attenuation getAtt() {
        return att;
    }

    public void setAtt(Attenuation att) {
        this.att = att;
    }
}

