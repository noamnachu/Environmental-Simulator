package pepse.world.trees;

import danogl.GameObject;
import danogl.components.GameObjectPhysics;
import danogl.components.ScheduledTask;
import danogl.components.Transition;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.world.Block;
import pepse.world.JumpObserver;

import java.awt.*;
import java.util.Random;

/**
 * Represents a leaf object in a tree, capable of swaying in the wind.
 * The leaf periodically changes its angle and width to simulate natural movements.
 * It also responds to a jump action, triggering a rotation animation.
 *
 * @author Dana Zilca and Noam Nachum
 */
public class Leaf extends GameObject implements JumpObserver {
    /**
     * The color of the leaf (green).
     */
    public static final Color LEAF_COLOR = new Color(34, 139, 34);
    /**
     * The size of the leaf.
     */
    public static final int SIZE = 30;
    /**
     * The initial angle for the sway transition.
     */
    private static final float SWAY_START_ANGLE = 1f;
    /**
     * The final angle for the sway transition.
     */
    private static final float SWAY_FINAL_ANGLE = 2f;
    /**
     * A random float value used to vary the sway angles.
     */
    private static final float SWAY_RANDOM_FLOAT = 50f;
    /**
     * The initial width factor for the width transition.
     */
    private static final float START_WIDTH = 1f;
    /**
     * The final width factor for the width transition.
     */
    private static final float FINAL_WIDTH = 0.6f;
    /**
     * The initial angle for the jump transition.
     */
    private static final float JUMP_START_ANGLE = 0.0f;
    /**
     * The final angle for the jump transition.
     */
    private static final float JUMP_FINAL_ANGLE = 90f;
    /**
     * The duration of the width transition, in seconds.
     */
    private static final float WIDTH_TRANSITION_TIME = 5f;
    /**
     * The duration of the sway transition, in seconds.
     */
    private static final float SWAY_TRANSITION_TIME = 3f;
    /**
     * The duration of the jump transition, in seconds.
     */
    private static final float JUMP_TRANSITION_TIME = 2.5f;

    /**
     * Constructs a leaf object with the given position and dimensions.
     *
     * @param topLeftCorner   The position of the leaf.
     * @param renderable      The leaf's renderable object.
     */
    public Leaf(Vector2 topLeftCorner, Renderable renderable) {
        super(topLeftCorner, Vector2.ONES.mult(SIZE), renderable);
        physics().preventIntersectionsFromDirection(Vector2.ZERO);
        physics().setMass(GameObjectPhysics.IMMOVABLE_MASS);
        swayInWind();
    }

    /**
     * Makes the leaf sway in the wind periodically.
     *
     */
    public void swayInWind() {
        Random random = new Random();
        new ScheduledTask(
                this,
                random.nextFloat(),
                false,
                this::angleTransitions
        );

        widthTransitions();
    }

    /**
     * Initiates the angle transitions for the swaying motion.
     * The leaf's angle oscillates between random values within specified bounds.
     */
    private void angleTransitions() {
        Random random = new Random();
        new Transition<Float>(
                this,
                this.renderer()::setRenderableAngle,
                SWAY_START_ANGLE * random.nextFloat(SWAY_RANDOM_FLOAT),
                SWAY_FINAL_ANGLE * random.nextFloat(SWAY_RANDOM_FLOAT),
                Transition.LINEAR_INTERPOLATOR_FLOAT,
                SWAY_TRANSITION_TIME,
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                null
                );
    }

    /**
     * Initiates the width transitions for the leaf.
     * The leaf's width oscillates between specified start and final widths.
     */
    private void widthTransitions() {
        new Transition<Float>(
                this,
                (Float width) -> this.setDimensions(Vector2.of(Block.SIZE, Block.SIZE).mult(width)),
                START_WIDTH,
                FINAL_WIDTH,
                Transition.CUBIC_INTERPOLATOR_FLOAT,
                WIDTH_TRANSITION_TIME,
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                null
        );
    }

    /**
     * Responds to a jump event by initiating a jump transition.
     * The leaf rotates to simulate a response to the jump action.
     */
    @Override
    public void onJump() {
        new Transition<Float>(this,
                this.renderer()::setRenderableAngle,
                JUMP_START_ANGLE,
                JUMP_FINAL_ANGLE,
                Transition.CUBIC_INTERPOLATOR_FLOAT,
                JUMP_TRANSITION_TIME,
                Transition.TransitionType.TRANSITION_ONCE,
                null
        );
    }
}
