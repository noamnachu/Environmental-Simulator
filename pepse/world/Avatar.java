package pepse.world;

import danogl.GameObject;
import danogl.gui.ImageReader;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.AnimationRenderable;
import danogl.gui.rendering.ImageRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.world.trees.Fruit;
import pepse.world.trees.Tree;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * The Avatar class represents the player's character in the game.
 * It handles the character's movement, animations, and energy mechanics.
 *
 * @author Dana Zilca and Noam Nachum
 */
public class Avatar extends GameObject {

    /**
     * Horizontal movement velocity.
     */
    private static final float VELOCITY_X = 400;
    /**
     * Vertical movement velocity (jump).
     */
    private static final float VELOCITY_Y = -650;
    /**
     * Gravity affecting the avatar.
     */
    private static final float GRAVITY = 600;

    /**
     * Maximum energy of the avatar.
     */
    public static final float MAX_ENERGY = 100;

    /**
     * Energy gain when the avatar is idle.
     */
    private static final float ENERGY_GAIN_IDLE = 1;

    /**
     * Energy loss when the avatar moves.
     */
    private static final float ENERGY_LOSS_MOVE = 0.5f;

    /**
     * Energy loss when the avatar jumps.
     */
    private static final float ENERGY_LOSS_JUMP = 10;

    /**
     * Energy threshold required for the avatar to jump.
     */
    private static final float ENERGY_THRESHOLD_JUMP = 10;

    /**
     * Tag for identifying the avatar.
     */
    public static final String AVATAR_TAG = "avatar";

    /**
     * Size of the avatar.
     */
    public static final float AVATAR_SIZE = 50;

    /**
     * Duration of each frame in the animation.
     */
    private static final float ANIMATION_FRAME_DURATION = 0.2f;
    /**
     * Listener for user input to control the avatar's movements.
     */
    private final UserInputListener inputListener;

    /**
     * Animation for the avatar's running state.
     */
    private final Renderable runAnimation;

    /**
     * Animation for the avatar's jumping state.
     */
    private final Renderable jumpAnimation;

    /**
     * List of observers to notify when the avatar jumps.
     */
    private final List<JumpObserver> jumpObservers;

    /**
     * Path to the first frame of the avatar's idle animation.
     */
    private static final String IDLE0_PATH = "assets/idle_0.png";

    /**
     * Path to the second frame of the avatar's idle animation.
     */
    private static final String IDLE1_PATH = "assets/idle_1.png";

    /**
     * Path to the third frame of the avatar's idle animation.
     */
    private static final String IDLE2_PATH = "assets/idle_2.png";

    /**
     * Path to the fourth frame of the avatar's idle animation.
     */
    private static final String IDLE3_PATH = "assets/idle_3.png";

    /**
     * Path to the first frame of the avatar's run animation.
     */
    private static final String RUN0_PATH = "assets/run_0.png";

    /**
     * Path to the second frame of the avatar's run animation.
     */
    private static final String RUN1_PATH = "assets/run_1.png";

    /**
     * Path to the third frame of the avatar's run animation.
     */
    private static final String RUN2_PATH = "assets/run_2.png";

    /**
     * Path to the fourth frame of the avatar's run animation.
     */
    private static final String RUN3_PATH = "assets/run_3.png";

    /**
     * Path to the first frame of the avatar's jump animation.
     */
    private static final String JUMP0_PATH = "assets/jump_0.png";

    /**
     * Path to the second frame of the avatar's jump animation.
     */
    private static final String JUMP1_PATH = "assets/jump_1.png";

    /**
     * Path to the third frame of the avatar's jump animation.
     */
    private static final String JUMP2_PATH = "assets/jump_2.png";

    /**
     * Path to the fourth frame of the avatar's jump animation.
     */
    private static final String JUMP3_PATH = "assets/jump_3.png";

    private ImageRenderable[] idleFrames;
    private ImageRenderable[] runFrames;
    private ImageRenderable[] jumpFrames;
    private Renderable idleAnimation;
    private boolean isJumping = false;
    private float currentEnergy = MAX_ENERGY;

    /**
     * Avatar constructor.
     *
     * @param topLeftCorner The initial position of the avatar.
     * @param inputListener The user input listener.
     * @param imageReader   The image reader for loading images.
     */
    public Avatar(Vector2 topLeftCorner,
                  UserInputListener inputListener,
                  ImageReader imageReader) {

        super(topLeftCorner,
                Vector2.ONES.mult(AVATAR_SIZE),
                new ImageRenderable(imageReader.readImage(IDLE0_PATH,
                        false).getImage()));

        setFrames(imageReader);

        this.idleAnimation = new AnimationRenderable(idleFrames, ANIMATION_FRAME_DURATION);
        this.runAnimation = new AnimationRenderable(runFrames, ANIMATION_FRAME_DURATION);
        this.jumpAnimation = new AnimationRenderable(jumpFrames, ANIMATION_FRAME_DURATION);

        setIdleState();

        physics().preventIntersectionsFromDirection(Vector2.ZERO);
        transform().setAccelerationY(GRAVITY);
        this.inputListener = inputListener;
        this.jumpObservers = new ArrayList<>();
        setTag(AVATAR_TAG);

    }

    /**
     * Initializes the frames for the avatar's animations: idle, running, and jumping.
     *
     * @param imageReader The image reader for loading the animation frames.
     */
    private void setFrames(ImageReader imageReader) {
        this.idleFrames = new ImageRenderable[]{
                new ImageRenderable(imageReader.readImage(IDLE0_PATH, false).getImage()),
                new ImageRenderable(imageReader.readImage(IDLE1_PATH, false).getImage()),
                new ImageRenderable(imageReader.readImage(IDLE2_PATH, false).getImage()),
                new ImageRenderable(imageReader.readImage(IDLE3_PATH, false).getImage())
        };
        this.runFrames = new ImageRenderable[]{
                new ImageRenderable(imageReader.readImage(RUN0_PATH, false).getImage()),
                new ImageRenderable(imageReader.readImage(RUN1_PATH, false).getImage()),
                new ImageRenderable(imageReader.readImage(RUN2_PATH, false).getImage()),
                new ImageRenderable(imageReader.readImage(RUN3_PATH, false).getImage()),
        };
        this.jumpFrames = new ImageRenderable[]{
                new ImageRenderable(imageReader.readImage(JUMP0_PATH, false).getImage()),
                new ImageRenderable(imageReader.readImage(JUMP1_PATH, false).getImage()),
                new ImageRenderable(imageReader.readImage(JUMP2_PATH, false).getImage()),
                new ImageRenderable(imageReader.readImage(JUMP3_PATH, false).getImage())
        };
    }

    /**
     * Updates the avatar's state. This method is called once per frame.
     *
     * @param deltaTime The time that has passed since the last frame.
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        float xVel = 0;

        if (inputListener.isKeyPressed(KeyEvent.VK_LEFT)) {
            xVel = runLeft(xVel);

        } else if (inputListener.isKeyPressed(KeyEvent.VK_RIGHT)) {
            xVel = runRight(xVel);

        } else if (inputListener.isKeyPressed(KeyEvent.VK_SPACE) &&
                getVelocity().y() == 0) {
            jump();
        } else {
            idle();
        }
         transform().setVelocityX(xVel);
         if (!inputListener.isKeyPressed(KeyEvent.VK_SPACE) &&
                 isJumping &&
                 getVelocity().y() == 0) {
             isJumping = false;
             this.renderer().setRenderable(idleAnimation);
         }

    }

    /**
     * Adds a jump observer to the avatar.
     *
     * @param observer The observer to be added.
     */
    public void addJumpObserver(JumpObserver observer) {
        jumpObservers.add(observer);
    }

    /**
     * Notifies all jump observers about a jump event.
     */
    private void notifyJumpListeners() {
        for (JumpObserver observer : jumpObservers) {
            observer.onJump();
        }
    }

    /**
     * Handles the idle state of the avatar. Increases energy if the avatar is idle and not jumping.
     */
    private void idle() {
        if (currentEnergy < MAX_ENERGY && !isJumping) {
            currentEnergy = Math.min(MAX_ENERGY, currentEnergy + ENERGY_GAIN_IDLE);
            this.renderer().setRenderable(idleAnimation);
        }
    }

    /**
     * Handles the jump action of the avatar. Decreases energy and sets velocity for jumping.
     */
    private void jump() {
        if (currentEnergy - ENERGY_THRESHOLD_JUMP >= 0) {
            currentEnergy -= ENERGY_LOSS_JUMP;
            transform().setVelocityY(VELOCITY_Y);
            this.renderer().setRenderable(jumpAnimation);
            isJumping = true;
            notifyJumpListeners();
        } else {
            this.renderer().setRenderable(idleAnimation);
        }
    }

    /**
     * Handles the left running action of the avatar. Decreases energy and sets velocity for running left.
     *
     * @param xVel The current horizontal velocity.
     * @return The updated horizontal velocity.
     */
    private float runLeft(float xVel) {
        if (currentEnergy - ENERGY_LOSS_MOVE >= 0) {
            currentEnergy -= ENERGY_LOSS_MOVE;
            xVel -= VELOCITY_X;
            this.renderer().setRenderable(runAnimation);
            this.renderer().setIsFlippedHorizontally(true);
            return xVel;
        } else {
            this.renderer().setRenderable(idleAnimation);
        }
        return 0;
    }

    /**
     * Handles the right running action of the avatar. Decreases energy and sets velocity for running right.
     *
     * @param xVel The current horizontal velocity.
     * @return The updated horizontal velocity.
     */
    private float runRight(float xVel) {
        if (currentEnergy - ENERGY_LOSS_MOVE >= 0) {
            currentEnergy -= ENERGY_LOSS_MOVE;
            xVel += VELOCITY_X;
            this.renderer().setRenderable(runAnimation);
            this.renderer().setIsFlippedHorizontally(false);
            return xVel;
        } else {
            this.renderer().setRenderable(idleAnimation);
        }
        return 0;
    }

    /**
     * Sets the avatar's state to idle by setting the idle animation.
     */
    private void setIdleState() {
        if (idleAnimation == null) {
            idleAnimation = new AnimationRenderable(idleFrames, 0.2f);
        }

        setCurrentAnimation(idleAnimation);
    }

    /**
     * Sets the current animation to the specified animation.
     *
     * @param animation The animation to be set.
     */
    private void setCurrentAnimation(Renderable animation) {
        renderer().setRenderable(animation);
    }

    /**
     * Adds energy to current energy
     *
     * @param energyToAdd Amount of energy to add to current energy.
     */
    public void addEnergy(int energyToAdd) {
        currentEnergy = Math.min(MAX_ENERGY, energyToAdd+currentEnergy);
    }

    /**
     * Gets the current energy of the avatar.
     *
     * @return The current energy.
     */
    public float getCurrentEnergy() {
        return currentEnergy;
    }

    /**
     * Determines if the avatar should collide with another game object based on its tag.
     *
     * @param other The other game object.
     * @return True if a collision should occur, false otherwise.
     */
    @Override
    public boolean shouldCollideWith(GameObject other) {
        return super.shouldCollideWith(other) &&
                (other.getTag().equals(Fruit.FRUIT_TAG) ||
                other.getTag().equals(Terrain.TERRAIN_TAG) ||
                other.getTag().equals(Tree.TRUNK_TAG));
    }

}
