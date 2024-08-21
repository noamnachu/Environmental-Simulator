package pepse.world.trees;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.components.Transition;
import danogl.gui.rendering.OvalRenderable;
import danogl.util.Vector2;
import pepse.world.Avatar;
import pepse.world.JumpObserver;

import java.awt.*;
import java.util.function.Consumer;

/**
 * Represents a fruit object in a tree, capable of changing color on jump events
 * and providing energy points when collected by the avatar.
 *
 * @author Dana Zilca and Noam Nachum
 */
public class Fruit extends GameObject implements JumpObserver {

    /**
     * The tag associated with the fruit object.
     */
    public static final String FRUIT_TAG = "fruit";

    /**
     * The default size of the fruit.
     */
    public static final int DEFAULT_SIZE = Math.max(Leaf.SIZE-5, 5);

    /**
     * The default color of the fruit (pink).
     */
    private static final Color DEFAULT_COLOR = Color.PINK;

    /**
     * The secondary color of the fruit (blue).
     */
    private static final Color SECOND_COLOR = Color.BLUE;

    /**
     * The energy points awarded to the avatar upon collecting the fruit.
     */
    private static final int ENERGY_POINTS_ON_COLLECT = 10;

    /**
     * The maximum opaqueness level of the fruit.
     */
    private static final float MAX_OPAQUE = 1f;

    /**
     * The minimum opaqueness level of the fruit.
     */
    private static final float MIN_OPAQUE = 0f;

    private final float cycleLength;
    private final Consumer<Integer> addEnergy;
    private boolean isDefault;

    /**
     * Constructs a fruit object with the given position, cycle length, and energy consumer.
     *
     * @param topLeftCorner The position of the fruit.
     * @param cycleLength   The length of the cycle for the fruit's visibility transition.
     * @param addEnergy     A consumer to add energy points to the avatar.
     */
    public Fruit(Vector2 topLeftCorner, float cycleLength, Consumer<Integer> addEnergy) {
        super(topLeftCorner, Vector2.of(DEFAULT_SIZE, DEFAULT_SIZE), new OvalRenderable(DEFAULT_COLOR));
        this.cycleLength = cycleLength;
        this.addEnergy = addEnergy;
        this.isDefault = true;
    }

    /**
     * Responds to a jump event by changing the color of the fruit.
     * Alternates between the default color and the secondary color.
     */
    @Override
    public void onJump() {
        if (isDefault) {
            renderer().setRenderable(new OvalRenderable(SECOND_COLOR));
            isDefault = false;
        } else {
            renderer().setRenderable(new OvalRenderable(DEFAULT_COLOR));
            isDefault = true;
        }
    }

    /**
     * Determines if this fruit should collide with another GameObject.
     * Only allows collisions with objects tagged as "avatar".
     *
     * @param other The other GameObject to check for collision.
     * @return True if the fruit should collide with the other object, false otherwise.
     */
    @Override
    public boolean shouldCollideWith(GameObject other) {
        return super.shouldCollideWith(other) && (other.getTag().equals(Avatar.AVATAR_TAG));
    }

    /**
     * Handles the event when a collision with another GameObject occurs.
     * If the fruit is fully opaque, it adds energy points to the avatar and starts a transition
     * to make the fruit reappear after a specified cycle length.
     *
     * @param other     The other GameObject involved in the collision.
     * @param collision The collision details.
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);

        if (renderer().getOpaqueness() == MAX_OPAQUE) {
            this.addEnergy.accept(ENERGY_POINTS_ON_COLLECT);
            new Transition<Float>(
                    this,
                    renderer()::setOpaqueness,
                    MIN_OPAQUE,
                    MAX_OPAQUE,
                    Transition.LINEAR_INTERPOLATOR_FLOAT,
                    cycleLength,
                    Transition.TransitionType.TRANSITION_ONCE,
                    null
            );
        }
    }
}
