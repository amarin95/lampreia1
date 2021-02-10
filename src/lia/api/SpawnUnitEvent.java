package lia.api;

public class SpawnUnitEvent {
    public int index;
    public UnitType type;

    public SpawnUnitEvent(int index, UnitType type) {
        this.index = index;
        this.type = type;
    }
}
