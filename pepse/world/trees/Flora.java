package pepse.world.trees;

import danogl.util.Vector2;
import pepse.util.NoiseGenerator;
import pepse.world.Block;
import pepse.world.JumpObserver;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;


/**
 * The Flora class is responsible for generating Game objects within a given range.
 *
 * @author Dana Zilca and Noam Nachum
 */
public class Flora {
    /** Error message when minX is not less than maxX. */
    private static final String MIN_MAX_ERROR = "minX must be less than maxX";

    /** Probability factor for tree placement. */
    private static final double PROB_FACTOR = 0.1f;

    private final Function<Float,Float> groundHeightAt;
    private List<JumpObserver> treeObjects;
    private final Consumer<Integer> addEnergy;
    private final int seed;


    /**
     * Constructs a Flora instance with the given parameters.
     *
     * @param groundHeightAt Function to get the ground height at a specific x-coordinate.
     * @param addEnergy Consumer function to add energy.
     * @param seed Seed for random number generation.
     */
    public Flora(Function<Float, Float> groundHeightAt,
                 Consumer<Integer> addEnergy,
                 int seed) {
        this.groundHeightAt = groundHeightAt;
        this.addEnergy = addEnergy;
        treeObjects = new ArrayList<>();
        this.seed = seed;
    }

    /**
     * Creates trees within a specified range of x-coordinates.
     *
     * @param minX Minimum x-coordinate for tree placement.
     * @param maxX Maximum x-coordinate for tree placement.
     * @return List of created tree GameObjects.
     */
    public List<JumpObserver> createInRange(int minX, int maxX) {
//        Random random = new Random(seed);

        if (minX >= maxX) {
            throw new IllegalArgumentException(MIN_MAX_ERROR);
        }

        int min = (int) (Math.floor((double) (minX - Block.SIZE) / Block.SIZE) * Block.SIZE);
        int max = (int) (Math.ceil((double) (maxX + Block.SIZE) / Block.SIZE) * Block.SIZE);
        for (int i = min; i < max; i+=Block.SIZE) {
            Random random = new Random(Objects.hash(i, seed));
            double randomNumber = random.nextDouble();
            if (randomNumber < PROB_FACTOR) {
                Tree tree = new Tree(i, groundHeightAt, addEnergy, Objects.hash(i, seed));
                treeObjects.add(tree.getTrunk());
                treeObjects.addAll(tree.getLeaves());
                treeObjects.addAll(tree.getFruit());
            }
        }
        return treeObjects;
    }
}
