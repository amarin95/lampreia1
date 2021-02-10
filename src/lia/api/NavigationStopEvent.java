package lia.api;

public class NavigationStopEvent {
    public int index;
    public int unitId;

    public NavigationStopEvent(int index, int unitId) {
        this.index = index;
        this.unitId = unitId;
    }
}
