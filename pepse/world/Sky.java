package pepse.world;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import java.awt.Color;

/**
 * The Sky class is responsible for creating the sky background in the game.
 * The sky is represented as a rectangle with a specific color that covers the entire window.
 * This class provides a method to create the sky GameObject with appropriate settings
 *
 * @author Dana Zilca and Noam Nachum
 */
public class Sky {

    /**
     * The basic color of the sky.
     */
    private static final Color BASIC_SKY_COLOR = Color.decode("#80C6E5");

    /**
     * The tag string used to identify the sky GameObject.
     */
    private static final String SKY_STRING = "sky";

    /**
     * Creates a GameObject that represents the sky.
     * The sky is a rectangle with a specific color that moves with the camera.
     *
     * @param windowDimensions The dimensions of the game window.
     * @return The created GameObject representing the sky.
     */
    public static GameObject create(Vector2 windowDimensions) {
        GameObject sky = new GameObject(
                Vector2.ZERO, windowDimensions,
                new RectangleRenderable(BASIC_SKY_COLOR));
        sky.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        sky.setTag(SKY_STRING);
        return sky;
    }
}
