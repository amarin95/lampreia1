package lia.api;

public class OpponentInView {
    public int id;
    public UnitType type;
    public int health;
    public float x;
    public float y;
    public float orientationAngle;
    public Speed speed;
    public Rotation rotation;

    public OpponentInView(int id, UnitType type, int health, float x, float y, float orientationAngle, Speed speed, Rotation rotation) {
        this.id = id;
        this.type = type;
        this.health = health;
        this.x = x;
        this.y = y;
        this.orientationAngle = orientationAngle;
        this.speed = speed;
        this.rotation = rotation;
    }
}
