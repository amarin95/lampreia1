package lia.api;

public class UnitData {
    public int id;
    public UnitType type;
    public int health;
    public float x;
    public float y;
    public float orientationAngle;
    public Speed speed;
    public Rotation rotation;
    public boolean canShoot;
    public int nBullets;
    public OpponentInView[] opponentsInView;
    public BulletInView[] opponentBulletsInView;
    public ResourceInView[] resourcesInView;
    public Point[] navigationPath;

    public UnitData(int id,
                    UnitType type,
                    int health,
                    float x, float y,
                    float orientationAngle,
                    Speed speed,
                    Rotation rotation,
                    boolean canShoot,
                    int nBullets,
                    OpponentInView[] opponentsInView,
                    BulletInView[] opponentBulletsInView,
                    ResourceInView[] resourcesInView,
                    Point[] navigationPath) {
        this.id = id;
        this.type = type;
        this.health = health;
        this.x = x;
        this.y = y;
        this.orientationAngle = orientationAngle;
        this.speed = speed;
        this.rotation = rotation;
        this.canShoot = canShoot;
        this.nBullets = nBullets;
        this.opponentsInView = opponentsInView;
        this.opponentBulletsInView = opponentBulletsInView;
        this.resourcesInView = resourcesInView;
        this.navigationPath = navigationPath;
    }
}

