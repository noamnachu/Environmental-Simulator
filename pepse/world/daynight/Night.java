package pepse.world.daynight;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;

import java.awt.*;

/**
 * Represents the night layer in the game world, managing opacity transitions.
 *
 * @author Dana Zilca and Noam Nachum
 */
public class Night {
    /**
     * The opacity value representing no opacity (fully transparent).
     */
    public static final float NO_OPACITY = 0f;

    /**
     * The opacity value representing midnight opacity.
     */
    public static final float MIDNIGHT_OPACITY = 0.5f;

    /**
     * The opacity value representing complete opacity (fully opaque).
     */
    public static final float COMPLETE_OPACITY = 1f;

    /**
     * The tag string used to identify the nightLayer GameObject.
     */
    private static final String NIGHT_STRING = "nightLayer";

    /**
     * Creates a new GameObject representing the night layer.
     *
     * @param windowDimensions The dimensions of the game window.
     * @param cycleLength      The length of the day-night cycle in seconds.
     * @return The created night GameObject.
     */
    public static GameObject create(Vector2 windowDimensions, float cycleLength) {
        RectangleRenderable renderable = new RectangleRenderable(Color.BLACK);
        GameObject night = new GameObject(Vector2.ZERO ,windowDimensions, renderable);
        night.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        night.setTag(NIGHT_STRING);
        
        night.renderer().setOpaqueness(COMPLETE_OPACITY);

        new Transition<Float>(
                night,
                night.renderer()::setOpaqueness,
                NO_OPACITY,
                MIDNIGHT_OPACITY,
                Transition.CUBIC_INTERPOLATOR_FLOAT,
                cycleLength / 2,
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                null
        );

        return night;

    }
}
