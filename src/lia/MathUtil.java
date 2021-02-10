package lia;

import lia.api.UnitData;

public class MathUtil {

    /**
     * Calculates the distance between the points (x1,y1) and (x2,y2).
     * @return distance
     */
    public static float distance(float x1, float y1, float x2, float y2) {
        return (float) Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
    }

    /**
     * Calculates the angle of a vector from (x1,y1) to (x2,y2) relative to the x-axis.
     * Angles are measured from x-axis in counter-clockwise direction and between 0 and 360
     * @return angle in degrees
     */
    public static float angle(float x1, float y1, float x2, float y2) {
        float angle = (float) Math.toDegrees(Math.atan2(y2 - y1, x2 - x1));
        if (angle < 0) angle += 360f;
        return angle;
    }


    /**
     * Returns an angle between where is unit looking at (it's orientation) and the specified point.
     * If the angle is 0, unit looks directly at a point, if angle is negative the unit looks to the left
     * side of the point and needs to rotate right to decrease the angle and if the angle is positive the
     * unit looks to the right side of the point and it needs to turn left to look closer to the point.
     * @return angle in degrees between -180 to 180 degrees
     */
    public static float angleBetweenUnitAndPoint(UnitData unit, float x, float y) {
        return angleBetweenUnitAndPoint(unit.x, unit.y, unit.orientationAngle, x, y);
    }

    public static float angleBetweenUnitAndPoint(float unitX, float unitY, float unitOrientationAngle,
                                 float pointX, float pointY) {

        float unitToPointAngle = angle(unitX, unitY, pointX, pointY);

        float angle = unitToPointAngle - unitOrientationAngle;

        if (angle > 180) angle -= 360;
        else if (angle < -180) angle += 360;
        return angle;
    }

}
