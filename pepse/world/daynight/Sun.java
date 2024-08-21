package pepse.world.daynight;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.OvalRenderable;
import danogl.util.Vector2;

import java.awt.*;

/**
 * Represents a sun GameObject that moves in a circular path around a specified point.
 *
 * @author Dana Zilca and Noam Nachum
 */
public class Sun {
    /**
     * Initial angle value for the sun's circular path.
     */
    private static final float INIT_SUN_VALUE = 0f;
    /**
     * Final angle value for the sun's circular path.
     */
    private static final float FINAL_SUN_VALUE = 360f;
    /**
     * Diameter of the sun's oval renderable.
     */
    private static final int SUN_DIMS = 60;
    /**
     * The tag string used to identify the sun GameObject.
     */
    private static final String SUN_TAG = "sun";

    /**
     * Creates a sun GameObject that moves in a circular path around a specified point.
     *
     * @param windowDimensions The dimensions of the game window.
     * @param cycleLength      The time (in seconds) for a complete revolution around the center point.
     * @return The created sun GameObject.
     */
    public static GameObject create(Vector2 windowDimensions, float cycleLength) {
        Vector2 initialSunCenter = new Vector2(windowDimensions.x() / 4, windowDimensions.y() / 4);
        Vector2 cycleCenter = new Vector2(windowDimensions.x() / 2, windowDimensions.y() / 2);

        OvalRenderable renderable = new OvalRenderable(Color.YELLOW);
        GameObject sun = new GameObject(initialSunCenter, new Vector2(SUN_DIMS, SUN_DIMS), renderable);

        sun.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        sun.setTag(SUN_TAG);

        new Transition<Float>(
                sun,
                angle -> sun.setCenter(initialSunCenter.subtract(cycleCenter)
                        .rotated(angle)
                        .add(cycleCenter)),
                INIT_SUN_VALUE,
                FINAL_SUN_VALUE,
                Transition.LINEAR_INTERPOLATOR_FLOAT,
                cycleLength,
                Transition.TransitionType.TRANSITION_LOOP,
                null
        );

        return sun;
    }
}
