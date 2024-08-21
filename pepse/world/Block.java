package pepse.world;
import danogl.GameObject;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import danogl.components.GameObjectPhysics;

/**
 * The Block class represents a rectangular block in the game world.
 * Blocks have a fixed size and special physical properties to prevent intersections and immovable mass.
 * They serve as the basic building blocks for the game's environment.
 *
 * @author Dana Zilca and Noam Nachum
 */
public class Block extends GameObject{
    /**
     * The fixed size of the block.
     */
    public static final int SIZE = 30;

    /**
     * Constructs a new Block instance.
     *
     * @param topLeftCorner The top-left corner position of the block.
     * @param renderable The renderable object representing the block's appearance.
     */
    public Block(Vector2 topLeftCorner, Renderable renderable) {
        super(topLeftCorner, Vector2.ONES.mult(SIZE), renderable);
        physics().preventIntersectionsFromDirection(Vector2.ZERO);
        physics().setMass(GameObjectPhysics.IMMOVABLE_MASS);
    }
}
