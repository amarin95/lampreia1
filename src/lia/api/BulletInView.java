package lia.api;

public class BulletInView {
    public float x;
    public float y;
    public float orientation;
    public float velocity;

    public BulletInView(float x, float y,
                        float orientation, float velocity) {
        this.x = x;
        this.y = y;
        this.orientation = orientation;
        this.velocity = velocity;
    }
}
