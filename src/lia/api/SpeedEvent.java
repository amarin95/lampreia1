package lia.api;

public class SpeedEvent {
    public int index;
    public int unitId;
    public Speed speed;

    public SpeedEvent(int index, int unitId, Speed speed) {
        this.index = index;
        this.unitId = unitId;
        this.speed = speed;
    }
}
