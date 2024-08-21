package pepse.world.GUI;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.TextRenderable;
import danogl.util.Vector2;

/**
 * Utility class for creating an energy display GameObject.
 */
public class EnergyDisplay {
    /**
     * Tag identifier for the energy display GameObject.
     */
    private static final String ENERGY_DISPLAY_TAG = "energy_display";

    /**
     * Size of the energy display.
     */
    private static final int DISPLAY_SIZE = 50;

    /**
     * Private constructor to prevent instantiation of the utility class.
     */
    public EnergyDisplay() {}

    /**
     * Creates an energy display GameObject with the provided text renderable.
     *
     * @param textRenderable The TextRenderable component to be displayed.
     * @return The created energy display GameObject.
     */
    public static GameObject create(TextRenderable textRenderable) {
        GameObject energy_level = new GameObject(Vector2.ZERO,
                Vector2.ONES.mult(DISPLAY_SIZE),
                textRenderable);
        energy_level.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        energy_level.setTag(ENERGY_DISPLAY_TAG);
        return energy_level;
    }
}
