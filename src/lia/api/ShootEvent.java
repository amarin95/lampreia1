package lia.api;

public class ShootEvent {
    public int index;
    public int unitId;

    public ShootEvent(int index, int unitId) {
        this.index = index;
        this.unitId = unitId;
    }
}
