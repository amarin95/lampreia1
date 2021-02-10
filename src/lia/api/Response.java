package lia.api;

public class Response {
    public long uid;
    public MessageType type;
    public SpeedEvent[] speedEvents;
    public RotationEvent[] rotationEvents;
    public ShootEvent[] shootEvents;
    public NavigationStartEvent[] navigationStartEvents;
    public NavigationStopEvent[] navigationStopEvents;
    public SaySomethingEvent[] saySomethingEvents;
    public SpawnUnitEvent[] spawnUnitEvents;

    public Response(long uid, MessageType type,
                    SpeedEvent[] speedEvents,
                    RotationEvent[] rotationEvents,
                    ShootEvent[] shootEvents,
                    NavigationStartEvent[] navigationStartEvents,
                    NavigationStopEvent[] navigationStopEvents,
                    SaySomethingEvent[] saySomethingEvents,
                    SpawnUnitEvent[] spawnUnitEvents) {
        this.uid = uid;
        this.type = type;
        this.speedEvents = speedEvents;
        this.rotationEvents = rotationEvents;
        this.shootEvents = shootEvents;
        this.navigationStartEvents = navigationStartEvents;
        this.navigationStopEvents = navigationStopEvents;
        this.saySomethingEvents = saySomethingEvents;
        this.spawnUnitEvents = spawnUnitEvents;
    }
}
