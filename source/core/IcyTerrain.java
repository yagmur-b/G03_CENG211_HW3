package core;

import enums.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// --- TEMPORARY STUB CLASSES (TO BE DELETED WHEN YAGMUR FINISHES) ---

interface ITerrainObject {}
interface IHazard extends ITerrainObject {}

class Penguin implements ITerrainObject {
    String id;
    public Penguin(String id) { this.id = id; }
    public void collectFood(Food f) { System.out.println(id + " ate food."); }
    public void stun() { System.out.println(id + " is stunned."); }
    public void dropLightestFood() { System.out.println(id + " dropped food."); }
    public void eliminate() { System.out.println(id + " fell into water/eliminated."); }
}

class Food implements ITerrainObject {}

// Hazard Types
class LightIceBlock implements IHazard {}
class HeavyIceBlock implements IHazard {}
class SeaLion implements IHazard {}
class HoleInIce implements IHazard { int row, col; }
class PluggedHole implements ITerrainObject {} // Plugged hole

public class IcyTerrain {
    // Grid structure: Nested ArrayLists are required
    private List<List<ITerrainObject>> grid;
    private final int ROWS = 10; // 10x10 Grid
    private final int COLS = 10;
    
    // Lists to track game objects
    public List<Penguin> penguins;
    private List<IHazard> hazards;
    private List<Food> foods;
    
    private Random random;

    public IcyTerrain() {
        this.grid = new ArrayList<>();
        this.penguins = new ArrayList<>();
        this.hazards = new ArrayList<>();
        this.foods = new ArrayList<>();
        this.random = new Random();

        initializeGrid(); // Create empty grid
        placeObjects();   // Place objects
    }

    // Fills the grid with null (empty) squares
    private void initializeGrid() {
        for (int i = 0; i < ROWS; i++) {
            List<ITerrainObject> row = new ArrayList<>();
            for (int j = 0; j < COLS; j++) {
                row.add(null); // Everywhere is empty initially
            }
            grid.add(row);
        }
    }

    // Places objects according to the rules
    private void placeObjects() {
        // 1. Place Penguins (3 items, ONLY ON EDGES)
        // Note: A Penguin factory could be used in the real code.
        for (int i = 1; i <= 3; i++) {
            Penguin p = new Penguin("P" + i); // Yagmur's class
            placePenguinOnEdge(p);
            penguins.add(p);
        }

        // 2. Place Hazards (15 items, cannot overlap with Penguins)
        for (int i = 0; i < 15; i++) {
            IHazard h = generateRandomHazard(); // Generate random hazard
            placeRandomly(h, false); // false = cannot be on top of a penguin
            hazards.add(h);
        }

        // 3. Place Food (20 items, cannot overlap with Hazards)
        for (int i = 0; i < 20; i++) {
            Food f = new Food(); // Random food
            placeRandomly(f, true); // true = can temporarily exist with penguin
            foods.add(f);
        }
    }

    // Places penguins only on edge squares (Row 0/9 or Col 0/9)
    private void placePenguinOnEdge(Penguin p) {
        int r, c;
        do {
            // Simple logic: Pick a random edge
            if (random.nextBoolean()) {
                r = random.nextBoolean() ? 0 : ROWS - 1;
                c = random.nextInt(COLS);
            } else {
                r = random.nextInt(ROWS);
                c = random.nextBoolean() ? 0 : COLS - 1;
            }
        } while (getObjectAt(r, c) != null); // Retry if occupied

        setObjectAt(r, c, p);
    }

    // Random placement for Hazards and Food
    private void placeRandomly(ITerrainObject obj, boolean allowPenguinOverlap) {
        int r, c;
        boolean placed = false;
        while (!placed) {
            r = random.nextInt(ROWS);
            c = random.nextInt(COLS);
            ITerrainObject existing = getObjectAt(r, c);

            if (existing == null) {
                setObjectAt(r, c, obj);
                placed = true;
            } 
            // Can food land where a penguin is? According to the rules, they can "temporarily exist".
            // However, for simplicity and a clean start, we place them on empty spots here.
        }
    }

    // Helper: Get object from Grid
    public ITerrainObject getObjectAt(int r, int c) {
        if (!isValidPosition(r, c)) return null;
        return grid.get(r).get(c);
    }

    // Helper: Set object in Grid
    public void setObjectAt(int r, int c, ITerrainObject obj) {
        if (isValidPosition(r, c)) {
            grid.get(r).set(c, obj);
            if (obj != null) {
                // We should also update the object's internal coordinate info if available
                // ((AbstractTerrainObject)obj).setPosition(r, c); // Uncomment if Yagmur adds this
            }
        }
    }
    
    // Helper: Is the coordinate within the grid?
    public boolean isValidPosition(int r, int c) {
        return r >= 0 && r < ROWS && c >= 0 && c < COLS;
    }

    // Random hazard generator (Dummy logic)
    private IHazard generateRandomHazard() {
        int type = random.nextInt(4);
        switch (type) {
            case 0: return new LightIceBlock();
            case 1: return new HeavyIceBlock();
            case 2: return new SeaLion();
            default: return new HoleInIce();
        }
    }

    // Returns grid as string table for Husna's menu
    public void printGrid() {
        // ... Hüsna's Part!
    }

    /**
     * EGE'S MOVEMENT ENGINE
     * Slides an object (Penguin or Hazard) in a specific direction.
     */
    public void moveObject(ITerrainObject obj, Direction dir, int maxSteps) {
        // Find current position
        int currentRow = -1, currentCol = -1;
        
        // Scan Grid to find object (Inefficient but safe, faster if obj keeps x,y)
        outerLoop:
        for(int i=0; i<ROWS; i++) {
            for(int j=0; j<COLS; j++) {
                if(getObjectAt(i, j) == obj) {
                    currentRow = i;
                    currentCol = j;
                    break outerLoop;
                }
            }
        }
        
        if (currentRow == -1) return; // Do nothing if object is not in grid

        int nextRow = currentRow + getRowDelta(dir);
        int nextCol = currentCol + getColDelta(dir);
        int stepsTaken = 0; // Step counter.

        // --- MOVEMENT LOOP ---
        // Proceed if no obstacle and not out of bounds
        while (isValidPosition(nextRow, nextCol) && stepsTaken < maxSteps) {
            ITerrainObject target = getObjectAt(nextRow, nextCol);

            // 1. EMPTY SQUARE: Move
            if (target == null) {
                // Clear old position
                setObjectAt(currentRow, currentCol, null);
                // Set to new position
                setObjectAt(nextRow, nextCol, obj);
                
                // Update coordinates
                currentRow = nextRow;
                currentCol = nextCol;
                
                // Calculate next step
                nextRow += getRowDelta(dir);
                nextCol += getColDelta(dir);

                // Steps are counted.
                stepsTaken++;
            } 
            // 2. IF FOOD: (Only Penguin can eat)
            else if (target instanceof Food) {
                if (obj instanceof Penguin) {
                    // Penguin takes food and STOPS
                    ((Penguin) obj).collectFood((Food) target);
                    foods.remove(target); // Remove from list
                    
                    // Clear old position, put penguin in new position (Food is removed)
                    setObjectAt(currentRow, currentCol, null);
                    setObjectAt(nextRow, nextCol, obj);
                    return; // Movement ends
                } else {
                    // If Hazard, in rare cases it crushes the food
                    foods.remove(target); // Food disappears
                    setObjectAt(currentRow, currentCol, null);
                    setObjectAt(nextRow, nextCol, obj);
                    currentRow = nextRow;
                    currentCol = nextCol;
                    nextRow += getRowDelta(dir);
                    nextCol += getColDelta(dir);
                    stepsTaken++;
                }
            }
            // 3. IF OBSTACLE: Collision Handling
            else {
                handleCollision(obj, target, currentRow, currentCol, dir);
                return; // Movement of this object ends upon collision (Except SeaLion case)
            }
        }

        // LOOP ENDED BUT STILL IN LOOP LOGIC -> MEANS FELL INTO WATER
        if (!isValidPosition(nextRow, nextCol) && stepsTaken < maxSteps) {
            handleFallingIntoWater(obj, currentRow, currentCol);
        }
    }

    // Helper method for chain reaction.
    public void moveObject(ITerrainObject obj, Direction dir) {
        moveObject(obj, dir, 100); // Defalut: Infinite (100) steps. 
    }

    // --- COLLISION MANAGER ---
    private void handleCollision(ITerrainObject mover, ITerrainObject obstacle, int r, int c, Direction dir) {
        
        int targetRow = r + getRowDelta(dir);
        int targetCol = c + getColDelta(dir);

        // Case A: Mover is PENGUIN
        if (mover instanceof Penguin) {
            Penguin p = (Penguin) mover;
            
            if (obstacle instanceof Penguin) {
                // Tries to push the other penguin (Momentum Transfer)
                // Colliding penguin (p) stops at the square right before (Already at r,c).
                // Stationary penguin (obstacle) starts sliding.
                moveObject(obstacle, dir); 
            }
            else if (obstacle instanceof LightIceBlock) {
                // Penguin stops (stunned - Yagmur needs to add stun flag), Block slides
                p.stun(); 
                moveObject(obstacle, dir);
            }
            else if (obstacle instanceof HeavyIceBlock) {
                // Penguin stops, drops lightest food
                p.dropLightestFood();
            }
            else if (obstacle instanceof SeaLion) {
                // Penguin bounces (Reverse direction), SeaLion moves forward
                moveObject(obstacle, dir); // SeaLion forward
                moveObject(p, dir.opposite()); // Penguin bounces back
            }
            else if (obstacle instanceof HoleInIce) {
                 // Penguin falls
                 // (Note: We are here because target was occupied in the While loop.
                 // Penguin must enter the hole square and then vanish.)
                 handleFallingIntoWater(p, r, c); //The penguin is eliminated because it has fallen into the water.
                 // Hole is not plugged (Only objects plug it)
            }
        }
        
        // Case B: Mover is HAZARD (Chain reaction)
        else if (mover instanceof IHazard) {
            if (obstacle instanceof HoleInIce) {
                // Block or SeaLion plugs the hole
                if (mover instanceof LightIceBlock || mover instanceof SeaLion) {
                    setObjectAt(r, c, null); // Mover removed from old spot
                    // Replace hole with "PluggedHole" (Need Yagmur's class)
                    // For now, destroy hole and mover (Simple logic)
                    setObjectAt(targetRow, targetCol, new PluggedHole());
                }
            }
            // Other hazard collisions (Usually they stop)
        }
    }

    // --- FALLING INTO WATER ---
    private void handleFallingIntoWater(ITerrainObject obj, int r, int c) {
        setObjectAt(r, c, null); // Remove from grid
        if (obj instanceof Penguin) {
            ((Penguin) obj).eliminate(); // Skip remaining turns
            penguins.remove(obj);
        }
        // Object completely disappears.
    }

    // Direction helpers
    private int getRowDelta(Direction d) {
        if (d == Direction.UP) return -1;
        if (d == Direction.DOWN) return 1;
        return 0;
    }
    private int getColDelta(Direction d) {
        if (d == Direction.LEFT) return -1;
        if (d == Direction.RIGHT) return 1;
        return 0;
    }
    
    // --- Getters for External Access ---
    public List<Penguin> getPenguins() { return penguins; }
    public List<IHazard> getHazards() { return hazards; }
    public List<Food> getFoods() { return foods; }
    public int getRows() { return ROWS; }
    public int getCols() { return COLS; }
}