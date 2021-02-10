package lia;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Constants are set on runtime from game engine, changing them has
 * no effect. Find the predefined values in data/game-config.json
 * or print them out in processGameEnvironment() method in your bot
 * implementation.
 */
public class Constants {
    /** The duration of the game in seconds. */
    public static float GAME_DURATION;
    /** The width of the map in world units. */
    public static int MAP_WIDTH;
    /** The height of the map in world units. */
    public static int MAP_HEIGHT;
    /** Map as a 2D array of booleans. If map[x][y] equals True that means that at (x,y)
     * there is an obstacle. x=0, y=0 points to bottom left corner. */
    public static boolean[][] MAP;
    /** Approximate location where your team was spawned. */
    public static SpawnPoint SPAWN_POINT;
    /** The diameter of the unit in world units. */
    public static float UNIT_DIAMETER;
    /** A full health of a unit when the game starts. */
    public static int UNIT_FULL_HEALTH;
    /** The velocity in world units per second with which the unit moves forward. */
    public static float UNIT_FORWARD_VELOCITY;
    /** The velocity in world units per second with which the unit moves backward. */
    public static float UNIT_BACKWARD_VELOCITY;
    /** The angle with which the unit's orientation changes per second when rotating normally. */
    public static float UNIT_ROTATION_VELOCITY;
    /** The angle with which the unit's orientation changes per second when rotating slowly. */
    public static float UNIT_SLOW_ROTATION_VELOCITY;
    /** Delay between shooting two pre-loaded bullets. */
    public static float DELAY_BETWEEN_SHOTS;
    /** The time to reload one bullet. */
    public static float RELOAD_TIME;
    /** A maximum number of bullets that a unit can hold at once. */
    public static int MAX_BULLETS;
    /** The time after which the unit starts to regenerate health after being hit by a bullet. */
    public static float HEALTH_REGENERATION_DELAY;
    /** The amount of health points per second that the unit receives when recovering. */
    public static int HEALTH_REGENERATION_PER_SECOND;
    /** The length of unit's viewing area. */
    public static float VIEWING_AREA_LENGTH;
    /** The width of unit's viewing area at the side that is the furthest away from the unit. */
    public static float VIEWING_AREA_WIDTH;
    /** The amount by which is the start of a viewing area offset from the unit's center
     * (negative means towards the back). */
    public static float VIEWING_AREA_OFFSET;
    /** The diameter of the bullet in world units. */
    public static float BULLET_DIAMETER;
    /** The speed in world units per second with which the bullet moves forward. */
    public static float BULLET_VELOCITY;
    /** The damage that a warrior receives when it is hit by a bullet. */
    public static int BULLET_DAMAGE_TO_WARRIOR;
    /** The damage that a worker receives when it is hit by a bullet. */
    public static int BULLET_DAMAGE_TO_WORKER;
    /** The range of the bullet in world units. */
    public static float BULLET_RANGE;
    /** Price in resources for purchasing a warrior unit. */
    public static int WARRIOR_PRICE;
    /** Price in resources for purchasing a worker unit. */
    public static int WORKER_PRICE;
    /** Maximum number of units on your team. */
    public static float MAX_NUMBER_OF_UNITS;
    /** After how many seconds new resources stop spawning */
    public static int STOP_SPAWNING_AFTER;
    /** The maximum duration of the first update() call. */
    public static float FIRST_TICK_TIMEOUT;
    /** The maximum duration of each update() call after the first one. */
    public static float TICK_TIMEOUT;

    protected static void load(JsonObject constantsJson) {
        MAP_WIDTH = constantsJson.get("MAP_WIDTH").getAsInt();
        MAP_HEIGHT = constantsJson.get("MAP_HEIGHT").getAsInt();
        GAME_DURATION = constantsJson.get("GAME_DURATION").getAsFloat();
        UNIT_DIAMETER = constantsJson.get("UNIT_DIAMETER").getAsFloat();
        UNIT_FULL_HEALTH = constantsJson.get("UNIT_FULL_HEALTH").getAsInt();
        UNIT_FORWARD_VELOCITY = constantsJson.get("UNIT_FORWARD_VELOCITY").getAsFloat();
        UNIT_BACKWARD_VELOCITY = constantsJson.get("UNIT_BACKWARD_VELOCITY").getAsFloat();
        UNIT_ROTATION_VELOCITY = constantsJson.get("UNIT_ROTATION_VELOCITY").getAsFloat();
        UNIT_SLOW_ROTATION_VELOCITY = constantsJson.get("UNIT_SLOW_ROTATION_VELOCITY").getAsFloat();
        DELAY_BETWEEN_SHOTS = constantsJson.get("DELAY_BETWEEN_SHOTS").getAsFloat();
        RELOAD_TIME = constantsJson.get("RELOAD_TIME").getAsFloat();
        MAX_BULLETS = constantsJson.get("MAX_BULLETS").getAsInt();
        HEALTH_REGENERATION_DELAY = constantsJson.get("HEALTH_REGENERATION_DELAY").getAsFloat();
        HEALTH_REGENERATION_PER_SECOND = constantsJson.get("HEALTH_REGENERATION_PER_SECOND").getAsInt();
        VIEWING_AREA_LENGTH = constantsJson.get("VIEWING_AREA_LENGTH").getAsFloat();
        VIEWING_AREA_WIDTH = constantsJson.get("VIEWING_AREA_WIDTH").getAsFloat();
        VIEWING_AREA_OFFSET = constantsJson.get("VIEWING_AREA_OFFSET").getAsFloat();
        BULLET_DIAMETER = constantsJson.get("BULLET_DIAMETER").getAsFloat();
        BULLET_VELOCITY = constantsJson.get("BULLET_VELOCITY").getAsFloat();
        BULLET_DAMAGE_TO_WARRIOR = constantsJson.get("BULLET_DAMAGE_TO_WARRIOR").getAsInt();
        BULLET_DAMAGE_TO_WORKER = constantsJson.get("BULLET_DAMAGE_TO_WORKER").getAsInt();
        BULLET_RANGE = constantsJson.get("BULLET_RANGE").getAsFloat();
        WARRIOR_PRICE = constantsJson.get("WARRIOR_PRICE").getAsInt();
        WORKER_PRICE = constantsJson.get("WORKER_PRICE").getAsInt();
        MAX_NUMBER_OF_UNITS = constantsJson.get("MAX_NUMBER_OF_UNITS").getAsFloat();
        FIRST_TICK_TIMEOUT = constantsJson.get("FIRST_TICK_TIMEOUT").getAsFloat();
        TICK_TIMEOUT = constantsJson.get("TICK_TIMEOUT").getAsFloat();
        STOP_SPAWNING_AFTER = constantsJson.get("STOP_SPAWNING_AFTER").getAsInt();
        SPAWN_POINT = new SpawnPoint();
        SPAWN_POINT.x = constantsJson.get("SPAWN_POINT").getAsJsonObject().get("x").getAsFloat();
        SPAWN_POINT.y = constantsJson.get("SPAWN_POINT").getAsJsonObject().get("y").getAsFloat();

        // Parse map
        JsonArray mapRows = constantsJson.get("MAP").getAsJsonArray();
        MAP = new boolean[mapRows.size()][];
        for (int i = 0; i < mapRows.size(); i++) {

            JsonArray mapColumns = mapRows.get(i).getAsJsonArray();
            MAP[i] = new boolean[mapColumns.size()];

            for (int j = 0; j < mapColumns.size(); j++) {
                MAP[i][j] = mapColumns.get(j).getAsBoolean();
            }
        }
    }

    public static class SpawnPoint {
        public float x;
        public float y;
    }
}

