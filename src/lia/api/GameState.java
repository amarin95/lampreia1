package lia.api;

public class GameState {
    public long uid;
    public MessageType type;
    public float time;
    public int numberOfOpponentUnits;
    public int resources;
    public boolean canSaySomething;
    public UnitData[] units;

    public GameState(long uid,
                     MessageType type,
                     float time,
                     int numberOfOpponentUnits,
                     int resources,
                     boolean canSaySomething,
                     UnitData[] units) {
        this.uid = uid;
        this.type = type;
        this.time = time;
        this.resources = resources;
        this.numberOfOpponentUnits = numberOfOpponentUnits;
        this.canSaySomething = canSaySomething;
        this.units = units;
    }
}


