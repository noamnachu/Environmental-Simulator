package pepse.world.trees;

import danogl.GameObject;
import danogl.components.GameObjectPhysics;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;
import pepse.world.JumpObserver;

import java.awt.*;

/**
 * The Trunk class represents the trunk of a tree in the game world.
 * It extends GameObject and implements JumpObserver.
 *
 * @author Dana Zilca and Noam Nachum
 */
public class Trunk extends GameObject implements JumpObserver {
    /**
     * The color of the trunk.
     */
    public static final Color TRUNK_COLOR = new Color(100, 50, 20);

    /**
     * Maximum height that a tree can reach.
     */
    protected static final int MAX_TREE_HEIGHT = 150;

    /**
     * Minimum height that a tree can have.
     */
    protected static final int MIN_TREE_HEIGHT = 75;

    /**
     * The width of the trunk.
     */
    public static final float TRUNK_WIDTH = 17;

    /**
     * Constructs a Trunk object at the specified position with the given height and renderable.
     *
     * @param topLeftCorner The top-left corner position vector of the trunk.
     * @param height        The height of the trunk.
     * @param renderable    The renderable object used to display the trunk.
     */
    public Trunk(Vector2 topLeftCorner, float height, Renderable renderable) {
        super(topLeftCorner, new Vector2(TRUNK_WIDTH, height), renderable);
        physics().preventIntersectionsFromDirection(Vector2.ZERO);
        physics().setMass(GameObjectPhysics.IMMOVABLE_MASS);
    }

    /**
     * Callback method invoked when a jump action occurs.
     * Changes the trunk's renderable to reflect the TRUNK_COLOR.
     */
    @Override
    public void onJump() {
        renderer().setRenderable(new RectangleRenderable(
                ColorSupplier.approximateColor(TRUNK_COLOR)
        ));
    }
}
