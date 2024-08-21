package pepse;

import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.components.CoordinateSpace;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.gui.WindowController;
import danogl.gui.rendering.Camera;
import danogl.gui.rendering.TextRenderable;
import danogl.util.Vector2;
import pepse.world.*;
import pepse.world.GUI.EnergyDisplay;
import pepse.world.daynight.Night;
import pepse.world.daynight.Sun;
import pepse.world.daynight.SunHalo;
import pepse.world.trees.Flora;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import static pepse.world.trees.Tree.LEAF_TAG;

/**
 * The PepseGameManager class is responsible for managing the game.
 * It initializes the game window and adds the necessary game objects.
 *
 * @author Dana Zilca and Noam Nachum
 */
public class PepseGameManager extends GameManager {

    /**
     *  Night cycle length in seconds.
     */
    public static final int NIGHT_CYCLE = 30;

    /**
     * Terrain seed for random.
     */
    private static final int TERRAIN_SEED = 1;

    /**
     * The background width.
     */
    public static float BACKGROUND_WIDTH;
    private int terrainStart = 0;
    private int terrainEnd = 0;
    private Avatar avatar;
    private Vector2 windowDimensions;
    private List<GameObject> updatableGameObjects;

    /**
     * Constructor for PepseGameManager.
     */
    public PepseGameManager() {
        super();
        this.updatableGameObjects = new ArrayList<>();
    }

    /**
     * The main method to run the game.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        new PepseGameManager().run();
    }

    /**
     * Initializes the game by setting up the game window and adding the sky background, terrain, night cycle,
     * sun, sun halo, avatar, energy display, and flora.
     *
     * @param imageReader      An object used to read images from the disk.
     * @param soundReader      An object used to read sound files from the disk.
     * @param inputListener    An object used to get user input from the keyboard and mouse.
     * @param windowController An object used to control the game window.
     */
    @Override
    public void initializeGame(ImageReader imageReader,
                               SoundReader soundReader,
                               UserInputListener inputListener,
                               WindowController windowController) {
        super.initializeGame(imageReader, soundReader, inputListener, windowController);
        this.windowDimensions = windowController.getWindowDimensions();
        BACKGROUND_WIDTH = this.windowDimensions.x();
        initSky(windowDimensions);
        initNightCycle(windowDimensions);
        initSunHalo(windowDimensions);
        initAvatar(inputListener, imageReader);
        this.terrainStart = (int) (avatar.getCenter().x() - (BACKGROUND_WIDTH));
        this.terrainEnd = (int) (avatar.getCenter().x() + (BACKGROUND_WIDTH));
        Terrain terrain = createTerrain(terrainStart, terrainEnd);
        createFlora(terrain, terrainStart, terrainEnd);
        initEnergyDisplay(avatar);
        setCamera(new Camera(avatar,
                Vector2.ZERO,
                windowDimensions,
                windowDimensions
        ));
    }

    /**
     * Initializes the sky background.
     *
     * @param windowDimensions Dimensions of the game window.
     */
    private void initSky(Vector2 windowDimensions) {
        GameObject sky = Sky.create(windowDimensions);
        sky.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        gameObjects().addGameObject(sky, Layer.BACKGROUND);
    }

    /**
     * Creates the terrain within given range.
     *
     * @param rangeStart Where in the world to start creating terrain.
     * @param rangeEnd   Where in the world to stop creating terrain.
     */
    private Terrain createTerrain(int rangeStart, int rangeEnd) {
        Terrain terrain = new Terrain(windowDimensions, TERRAIN_SEED);
        try {
            List<Block> blocks = terrain.createInRange(rangeStart, rangeEnd);
            for (Block block : blocks) {
                gameObjects().addGameObject(block, Layer.STATIC_OBJECTS);
                updatableGameObjects.add(block);
            }
        } catch (IllegalArgumentException e) {
            System.err.println(e);
        }
        return terrain;
    }

    /**
     * Initializes the night cycle.
     *
     * @param windowDimensions Dimensions of the game window.
     */
    private void initNightCycle(Vector2 windowDimensions) {
        Night night = new Night();
        GameObject nightGameObject = night.create(windowDimensions, NIGHT_CYCLE);
        nightGameObject.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        gameObjects().addGameObject(nightGameObject, Layer.FOREGROUND);
    }

    /**
     * Initializes the sun and halo.
     *
     * @param windowDimensions Dimensions of the game window.
     */
    private void initSunHalo(Vector2 windowDimensions) {
        Sun sun = new Sun();
        GameObject sunGameObject = sun.create(windowDimensions, NIGHT_CYCLE);
        sunGameObject.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        gameObjects().addGameObject(sunGameObject, Layer.BACKGROUND);

        SunHalo sunHalo = new SunHalo();
        GameObject sunHaloGameObject = sunHalo.create(sunGameObject);
        gameObjects().addGameObject(sunHaloGameObject, Layer.BACKGROUND);
    }

    /**
     * Initializes the avatar.
     *
     * @param inputListener An object used to get user input from the keyboard and mouse.
     * @param imageReader   An object used to read images from the disk.
     */
    private void initAvatar(UserInputListener inputListener, ImageReader imageReader) {
        Avatar avatarObject = new Avatar(Vector2.ZERO,
                inputListener,
                imageReader);
        gameObjects().addGameObject(avatarObject);

        this.avatar = avatarObject;
    }

    /**
     * Initializes the energy display.
     *
     * @param avatar Our avatar object.
     */
    private void initEnergyDisplay(Avatar avatar) {
        EnergyDisplay energyDisplay = new EnergyDisplay();
        TextRenderable energyText = new TextRenderable(Float.toString(avatar.getCurrentEnergy()));
        GameObject energyDisplayObject = energyDisplay.create(energyText);
        gameObjects().addGameObject(energyDisplayObject, Layer.BACKGROUND);
        energyDisplayObject.addComponent((deltaTime) -> energyText.setString(
                Float.toString(avatar.getCurrentEnergy())
        ));
    }

    /**
     * Creates the flora (trees and leaves) within given range.
     *
     * @param terrain    Terrain in world to create the flora.
     * @param rangeStart Where in world to start creating terrain.
     * @param rangeEnd   Where in world to stop creating terrain.
     */
    private void createFlora(Terrain terrain, int rangeStart, int rangeEnd) {
        Flora flora = new Flora(terrain::groundHeightAt,
                avatar::addEnergy,
                TERRAIN_SEED);

        try {
            List<JumpObserver> treesObjects = flora.createInRange(rangeStart, rangeEnd);

            for (JumpObserver jumpObserver : treesObjects) {
                avatar.addJumpObserver(jumpObserver);
                GameObject object = (GameObject) jumpObserver;
                updatableGameObjects.add(object);
                if (Objects.equals(object.getTag(), LEAF_TAG)) {
                    gameObjects().addGameObject(object, Layer.FOREGROUND);
                } else {
                    gameObjects().addGameObject(object, Layer.STATIC_OBJECTS);
                }
            }
        } catch (IllegalArgumentException e) {
            System.err.println(e);
        }
    }

    /**
     * Updates the game world by generating new terrain and flora based on the avatar's position.
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        float halfBackground = BACKGROUND_WIDTH / 2;
        if (avatar.getCenter().x() - terrainStart < halfBackground ||
                terrainEnd - avatar.getCenter().x() < halfBackground ) {
            int newTerrainStart = (int) (avatar.getCenter().x() - BACKGROUND_WIDTH);
            int newTerrainEnd = (int) (avatar.getCenter().x() + BACKGROUND_WIDTH);
            if (newTerrainStart < terrainStart) {
                Terrain newTerrain = createTerrain(newTerrainStart, terrainStart);
                createFlora(newTerrain, newTerrainStart, terrainStart);
            }
            if (newTerrainEnd > terrainEnd) {
                Terrain newTerrain1 = createTerrain(terrainEnd, newTerrainEnd);
                createFlora(newTerrain1, terrainEnd, newTerrainEnd);
            }
            terrainStart = newTerrainStart;
            terrainEnd = newTerrainEnd;
            removeGameObjectsOutofRange(terrainStart, terrainEnd);
        }
    }

    /**
     * Removes game objects that are outside the specified range.
     *
     * @param rangeStart The start of the range.
     * @param rangeEnd   The end of the range.
     */
    private void removeGameObjectsOutofRange(float rangeStart, float rangeEnd) {
        Iterator<GameObject> iterator = updatableGameObjects.iterator();

        while (iterator.hasNext()) {
            GameObject gameObject = iterator.next();
            float x = gameObject.getCenter().x();
            if (x > rangeEnd || x < rangeStart) {
                gameObjects().removeGameObject(gameObject,
                        gameObject.getTag().equals(LEAF_TAG) ?
                                Layer.FOREGROUND : Layer.STATIC_OBJECTS);
                iterator.remove();
            }
        }
    }
}
