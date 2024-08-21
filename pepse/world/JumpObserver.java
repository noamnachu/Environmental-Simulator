package pepse.world;

/**
 * An interface to be implemented by classes that wish to observe and respond to jumping events
 * within the game. Implementing classes should define the actions to be taken when a jump occurs.
 *
 * @author Dana Zilca and Noam Nachum
 */
public interface JumpObserver {
    /**
     * Called when the avatar performs a jump action.
     */
     void onJump();
}