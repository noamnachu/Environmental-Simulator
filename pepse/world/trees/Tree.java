package pepse.world.trees;

import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.PepseGameManager;
import pepse.util.ColorSupplier;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;

import static pepse.world.trees.Trunk.MAX_TREE_HEIGHT;
import static pepse.world.trees.Trunk.MIN_TREE_HEIGHT;

/**
 * The Tree class represents a tree in the game world.
 * It consists of a trunk and potentially other tree components.
 *
 * @author  Dana Zilca and Noam Nachum
 */
public class Tree {
    /**
     * Tag used to identify the trunk in the game world.
     */
    public static final String TRUNK_TAG = "trunk";

    /**
     * Tag used to identify leaves in the game world.
     */
    public static final String LEAF_TAG = "Leaf";

    /**
     * Default size of the tree's square area for leaves and fruit placement.
     */
    private static final int SQUARE_SIZE = 60;

    /**
     * Probability factor for the presence of leaves.
     */
    private static final double LEAF_PROB = 0.5;

    private int treeLocation;
    private Trunk trunk;
    private List<Leaf> leaves;
    private List<Fruit> fruit;
    private Function<Float, Float> groundHeightAt;
    private Consumer<Integer> addEnergy;

    /**
     * Constructs a Tree object at the specified location.
     *
     * @param treeLocation   The x-coordinate of the tree location.
     * @param groundHeightAt A function to get the ground height at a specific x-coordinate.
     */
    public Tree(int treeLocation,
                Function<Float, Float> groundHeightAt,
                Consumer<Integer> addEnergy,
                int seed) {
        this.treeLocation = treeLocation;
        this.groundHeightAt = groundHeightAt;
        this.leaves = new ArrayList<>();
        this.fruit = new ArrayList<>();
        this.addEnergy = addEnergy;

        initTrunk(seed);
        initLeaves(seed);
        initFruits();
    }

    /**
     * Initializes the tree trunk at the specified location with a random height.
     */
    private void initTrunk(int seed) {
        Random random = new Random(seed);
        int randomHeight = random.nextInt(MAX_TREE_HEIGHT) + MIN_TREE_HEIGHT;
        Renderable trunkRender = new RectangleRenderable(ColorSupplier.approximateColor(
                Trunk.TRUNK_COLOR));
        Vector2 topLeftCorner = new Vector2(treeLocation,
                groundHeightAt.apply((float) treeLocation) - randomHeight);
        trunk = new Trunk(topLeftCorner, randomHeight, trunkRender);
        trunk.setTag(TRUNK_TAG);
    }

    /**
     * Initializes the tree leaves.
     */
    private void initLeaves(int seed) {
        Random random = new Random(seed);

        for (int j = 0; j < SQUARE_SIZE; j += Leaf.SIZE) {
            for (int i = 0; i < SQUARE_SIZE; i += Leaf.SIZE) {
                double randomNumber = random.nextDouble();
                if (randomNumber < LEAF_PROB) {
                    Vector2 leafTopLeft = Vector2.of(trunk.getTopLeftCorner().x(),
                            trunk.getTopLeftCorner().y());

                    leaves.add(createLeaf(leafTopLeft, j, i));
                }
            }
        }
    }

    /**
     * Initializes the fruit.
     */
    private void initFruits() {
        Fruit newFruit = new Fruit(Vector2.of(trunk.getTopLeftCorner().x(),
                trunk.getTopLeftCorner().y() - Fruit.DEFAULT_SIZE),
                PepseGameManager.NIGHT_CYCLE,
                addEnergy);

        newFruit.setTag(Fruit.FRUIT_TAG);

        fruit.add(newFruit);
    }

    /**
     * Creates a leaf at a specified position relative to the tree trunk.
     *
     * @param objectTopLeft The top-left corner position of the object.
     * @param x             The x-offset for the leaf position.
     * @param y             The y-offset for the leaf position.
     * @return The created Leaf object.
     */
    private Leaf createLeaf(Vector2 objectTopLeft, int x, int y) {
        Renderable leafsRender = new RectangleRenderable(ColorSupplier.approximateColor(Leaf.LEAF_COLOR));
        Vector2 location = new Vector2(objectTopLeft.x() + x - Trunk.TRUNK_WIDTH, objectTopLeft.y() + y);
        Leaf leaf = new Leaf(location, leafsRender);
        leaf.setTag(LEAF_TAG);
        return leaf;
    }

    /**
     * Gets the trunk of the tree.
     *
     * @return The Trunk object representing the tree's trunk.
     */
    public Trunk getTrunk() {
        return trunk;
    }

    /**
     * Gets the list of leaves on the tree.
     *
     * @return List of Leaf objects.
     */
    public List<Leaf> getLeaves() {
        return this.leaves;
    }

    /**
     * Gets the list of fruit on the tree.
     *
     * @return List of Fruit objects.
     */
    public List<Fruit> getFruit() {
        return this.fruit;
    }
}
