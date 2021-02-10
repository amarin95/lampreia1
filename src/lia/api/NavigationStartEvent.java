package lia.api;

public class NavigationStartEvent {
    public int index;
    public int unitId;
    public float x;
    public float y;
    public boolean moveBackwards;

    public NavigationStartEvent(int index, int unitId, float x, float y, boolean moveBackwards) {
        this.index = index;
        this.unitId = unitId;
        this.x = x;
        this.y = y;
        this.moveBackwards = moveBackwards;
    }
}
