package pepse.world.daynight;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.OvalRenderable;
import danogl.util.Vector2;

import java.awt.*;

/**
 * Represents a halo around a sun GameObject.
 *
 * @author  Dana Zilca and Noam Nachum
 */
public class SunHalo {

    /**
     * Dimensions of the halo GameObject.
     */
    private static final int HALO_DIMS = 120;

    /**
     * Color of the halo with transparency.
     */
    private static final Color HALO_COLOR = new Color(255, 255, 0, 20);

    /**
     * Tag to uniquely identify the sun halo GameObject.
     */
    private static final String HALO_TAG = "sun_halo";

    /**
     * Creates a halo GameObject around a specified sun GameObject.
     *
     * @param sun The sun GameObject around which the halo is created.
     * @return The created halo GameObject.
     */
    public static GameObject create(GameObject sun) {
        OvalRenderable renderable = new OvalRenderable(HALO_COLOR);
        GameObject halo = new GameObject(sun.getCenter(), new Vector2(HALO_DIMS, HALO_DIMS), renderable);

        halo.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        halo.setTag(HALO_TAG);

        sun.addComponent(deltaTime -> halo.setCenter(sun.getCenter()));

        return halo;
    }
}
