package pepse.world;

import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;
import pepse.util.NoiseGenerator;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * Terrain class is responsible for generating ground blocks and providing
 * the ground height at a given X coordinate.
 *
 * @author Dana Zilca and Noam Nachum
 */
public class Terrain {
    /** The base color of the ground blocks. */
    private static final Color BASE_GROUND_COLOR = new Color(212, 123, 74);

    /** The depth of the terrain, defined as the number of vertical blocks. */
    private static final int TERRAIN_DEPTH = 20;

    /**
     * The tag string used to identify the terrain GameObject.
     */
    public static final String TERRAIN_TAG = "terrain";

    /** Min max error message*/
    private static final String MIN_MAX_ERROR = "minX must be less than maxX";

    private final int groundHeightAtX0;
    private final NoiseGenerator noiseGenerator;
    private final Vector2 windowDims;

    /**
     * Constructs a Terrain object responsible for generating ground blocks.
     * @param windowDimensions The dimensions of the game window.
     * @param seed A seed for random number generation.
     */
    public Terrain(Vector2 windowDimensions, int seed) {
        this.groundHeightAtX0 = (int) windowDimensions.y() * 2 / 3;
        this.noiseGenerator = new NoiseGenerator(seed, groundHeightAtX0);
        this.windowDims = windowDimensions;
    }

    /**
     * Returns the ground height at a given x-coordinate.
     * @param x The x-coordinate.
     * @return The ground height at the given x-coordinate.
     */
    public float groundHeightAt(float x) {
        float noise = (float) noiseGenerator.noise(x, Block.SIZE * TERRAIN_DEPTH);
        return groundHeightAtX0 + noise;
    }

    /**
     * Creates a list of ground blocks within a specified x-range.
     * @param minX The minimum x-coordinate.
     * @param maxX The maximum x-coordinate.
     * @return A list of created ground blocks.
     */
    public List<Block> createInRange(int minX, int maxX) {
        List<Block> blocks = new ArrayList<>();

        if (minX >= maxX) {
            throw new IllegalArgumentException(MIN_MAX_ERROR);
        }

        int min = (int) (Math.floor((double) (minX - Block.SIZE) / Block.SIZE) * Block.SIZE);
        int max = (int) (Math.ceil((double) (maxX + Block.SIZE) / Block.SIZE) * Block.SIZE);

        for (int x = min; x < max; x += Block.SIZE) {
            float groundHeight = (float) Math.floor(groundHeightAt(x) / Block.SIZE) * Block.SIZE;
            for (int y = (int) groundHeight; y < windowDims.y()*2; y += Block.SIZE) {
                Block block = new Block(new Vector2(x, y),
                        new RectangleRenderable(ColorSupplier.approximateColor(BASE_GROUND_COLOR)));
                block.setTag(TERRAIN_TAG);
                blocks.add(block);
            }
        }
        return blocks;
    }
}
