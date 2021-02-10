package lia.api;

public class SaySomethingEvent {
    public int index;
    public int unitId;
    public String text;

    public SaySomethingEvent(int index, int unitId, String text) {
        this.index = index;
        this.unitId = unitId;
        this.text = text;
    }
}
