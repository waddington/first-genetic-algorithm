import javafx.scene.text.Text;

import java.util.concurrent.ThreadLocalRandom;

public class Agent {
    String id;
    boolean isAlive;
    Gene gene;
    int score; // time lasted * 10 + energy remaining
    static int timeLasted;
    double energyLevel; // 0.0001-100
    static int[] foodCoords; // Area is 700x520
    static int foodEnergyBoost = 20;
    int[] currentPosition; // Area is 700x520
    int agentShapeRadius;
    static double m = 25; // For calculating energy used at different speeds x = (y - c) / m
    static double c = -3; // For calculating energy used at different speeds x = (y - c) / m

    // Constructor(s)
    Agent(String id, boolean isInitialPopulation, int agentShapeRadius) {
        this.id = id;
        this.isAlive = true;
        this.score = 0;
        this.energyLevel = 100;
        this.agentShapeRadius = agentShapeRadius;

        if (isInitialPopulation) this.gene = createRandomGene();
    }

    // id
    String getId() {
        return this.id;
    }

    // isAlive
    boolean isAlive() {
        return this.isAlive;
    }
    void setAlive(boolean isAlive) {
        this.isAlive = isAlive;
    }

    // gene
    Gene getGene() {
        return this.gene;
    }
    void setGene(Gene gene) {
        this.gene = gene;
    }

    // score
    int getScore() {
        return this.score;
    }
    void setScore(int score) {
        this.score = score;
    }
    int updateScore() {
        if (isAlive()) {
            this.score = Agent.timeLasted + (int) this.getEnergyLevel();
        }
        return this.score;
    }

    // energyLevel
    double getEnergyLevel() {
        return this.energyLevel;
    }
    void setEnergyLevel(double energy) {
        this.energyLevel = energy;
    }
    boolean updateEnergy(Text textStatus) {
        // returns true if agent is still alive
        if (isAlive()) {
            double energyUsed = getEnergyUsedAtSpeed(getSpeedOfAgent());
            this.energyLevel -= energyUsed;

            if (this.energyLevel < 0.0001) {
                this.isAlive = false;
                textStatus.setText("DEATH: Agent #"+getId()+" ran out of energy and died.");
                return false;
            }

            return true;
        }
        return false;
    }
    void giveFoodEnergyBoost() {
        setEnergyLevel((getEnergyLevel() + Agent.foodEnergyBoost));
        if (getEnergyLevel() > 100) // Limit the maximum energy to 100
            setEnergyLevel(100);
    }

    // foodCoords
    static int[] getFoodCoords() {
        return foodCoords;
    }
    static void setFoodCoords(int[] newFoodCoords) {
        foodCoords = newFoodCoords;
    }

    // currentPosition
    int[] getCurrentPosition() {
        return this.currentPosition;
    }
    void setCurrentPosition(int[] newPosition) {
        this.currentPosition = newPosition;
    }

    // Calculate the agents' next position
    // Moves towards food source with some variation in direction
    // https://math.stackexchange.com/a/1630886
    void updatePosition() {
        // TODO: allow agent to move at slightly different gradient to add variation
        int x0 = getCurrentPosition()[0];
        int y0 = getCurrentPosition()[1];

        int x1 = getFoodCoords()[0];
        int y1 = getFoodCoords()[1];

        double d = Math.sqrt((Math.pow((x1-x0),2)) + (Math.pow((y1-y0),2)));
        double dt = getSpeedOfAgent();

        double t = dt / d;

        // Add some variation into the direction of the genes
        // Also helps to avoid clustering
        double xVariation = (ThreadLocalRandom.current().nextDouble(0,1) > 0.65) ? this.gene.getDirectionVariation() : this.gene.getDirectionVariation() * -1;
        double yVariation = (ThreadLocalRandom.current().nextDouble(0,1) < 0.65) ? this.gene.getDirectionVariation() : this.gene.getDirectionVariation() * -1;

        int xt = (int) (((t * x1) + ((1-t)*x0)) + (xVariation));
        int yt = (int) (((t * y1) + ((1-t)*y0)) + (yVariation));

        setCurrentPosition(new int[]{xt,yt});
    }

    // get the energy usage at the current speed
    double getEnergyUsedAtSpeed(double speed) {
        // y = mx + c
        // x = (y - c) / m

        // y = speed
        // x = energy
        double energyAtSpeed = (speed - c) / m;
        return energyAtSpeed;
    }

    // get the speed of the agent based on current energy levels
    double getSpeedOfAgent() {
        return getSpeedUsingSigmoid();
    }
    double getSpeedUsingSigmoid() {
        double rawSpeedOutput;

        // f(x) = 1 / (1 + exp(-K * x))
        double x = linearTransform(0,100, -6,6, this.getEnergyLevel()); // Need to map energy levels from 0-100 to -6-6
        double K = this.gene.getK();

        rawSpeedOutput = 1 / (1 + Math.pow(Math.E, (-K * x)));

        // Map y value of 0-1 to min/max speed
        return linearTransform(0,1, this.gene.getMinSpeed(),this.gene.getMaxSpeed(), rawSpeedOutput);
    }

    // Map x from range a-b to new range c-d
    double linearTransform(double a, double b, double c, double d, double x) {
        double mappedNumber = 0;

        // y = (x - a)/(b - a) * (d - c) + c
        mappedNumber = (x - a)/(b - a) * (d - c) + c;

        return mappedNumber;
    }

    // Create Gene of initial population
    Gene createRandomGene() {
        double minSpeed = linearTransform(0,1, 3,6, (ThreadLocalRandom.current().nextDouble(0,1)));
        double maxSpeed = linearTransform(0,1, 9,12, (ThreadLocalRandom.current().nextDouble(0,1)));

        int startX = ThreadLocalRandom.current().nextInt(this.agentShapeRadius,(701-this.agentShapeRadius));
        int startY = ThreadLocalRandom.current().nextInt(this.agentShapeRadius,(521-this.agentShapeRadius));
        int[] startingArea = {startX, startY};

        double k = linearTransform(0,1, 0.75,1.25, (ThreadLocalRandom.current().nextDouble(0,1)));

        double directionVariation = ThreadLocalRandom.current().nextDouble(-5, 5);

        Gene gene = new Gene(minSpeed, maxSpeed, startingArea, k, directionVariation);

        this.currentPosition = startingArea;

        return gene;
    }

    // Update how long the agents have lasted
    static void setTimeLasted(int timeLasted) {
        Agent.timeLasted = timeLasted;
    }
}
