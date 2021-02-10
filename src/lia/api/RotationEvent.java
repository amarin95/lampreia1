package lia.api;

public class RotationEvent {
    public int index;
    public int unitId;
    public Rotation rotation;

    public RotationEvent(int index, int unitId, Rotation rotation) {
        this.index = index;
        this.unitId = unitId;
        this.rotation = rotation;
    }
}
