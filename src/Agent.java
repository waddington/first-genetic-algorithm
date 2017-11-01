import javafx.scene.text.Text;

import java.util.concurrent.ThreadLocalRandom;

public class Agent {
    private String id;
    private Gene gene;

    private boolean isAlive;

    private int score;
    private double energyLevel;
    private int[] currentPosition;

    private static int timeLasted;

    private static int agentShapeSize;
    private static int foodShapeSize;

    private static int[] foodCoords;
    private static int foodEnergyBoost = 20;

    private static double m = 25;
    private static double c = -3;

    // Getters and Setters
    String getId() {
        return this.id;
    }
    void setId(String id) {
        this.id = id;
    }

    Gene getGene() {
        return this.gene;
    }
    void setGene(Gene gene) {
        this.gene = gene;
    }

    boolean isAlive() {
        return this.isAlive;
    }
    void setAlive(boolean alive) {
        this.isAlive = alive;
    }

    int getScore() {
        return this.score;
    }
    void setScore(int score) {
        this.score = score;
    }
    int updateScore() {
        if (isAlive()) {
            setScore(Agent.timeLasted + (int) getEnergyLevel());
        }
        return getScore();
    }

    double getEnergyLevel() {
        return this.energyLevel;
    }
    void setEnergyLevel(double energyLevel) {
        if (energyLevel > 100)
            energyLevel = 100;
        this.energyLevel = energyLevel;
    }
    boolean updateEnergy(Text textStatus) {
        if (isAlive) {
            double energyUsed = getEnergyUsedAtSpeed(getSpeedOfAgent());
            setEnergyLevel(getEnergyLevel() - energyUsed);

            if (getEnergyLevel() < 0.001) {
                setAlive(false);
                textStatus.setText("DEATH: Agent #"+getId()+" ran out of energy and died.");
                return false;
            }

            return true;
        }
        return false;
    }

    int[] getCurrentPosition() {
        return this.currentPosition;
    }
    void setCurrentPosition(int[] currentPosition) {
        this.currentPosition = currentPosition;
    }
    boolean updatePosition() {
        // https://math.stackexchange.com/a/1630886
        int x0 = getCurrentPosition()[0];
        int y0 = getCurrentPosition()[1];

        int x1 = getFoodCoords()[0];
        int y1 = getFoodCoords()[1];

        double d = Math.sqrt((Math.pow((x1-x0),2)) + (Math.pow((y1-y0),2)));
        double dt = getSpeedOfAgent();
        double t = dt / d;

        double directionVariation = getGene().getDirectionVariation();

        double xVariation = (ThreadLocalRandom.current().nextDouble(0,1) > 0.65) ? directionVariation : directionVariation * -1;
        double yVariation = (ThreadLocalRandom.current().nextDouble(0,1) > 0.65) ? directionVariation : directionVariation * -1;

        int xt = (int) (((t * x1) + ((1-t)*x0)) + (xVariation));
        int yt = (int) (((t * y1) + ((1-t)*y0)) + (yVariation));

        setCurrentPosition(new int[] {xt, yt});

        // Check for collision with food
        int foodTop = Agent.getFoodCoords()[1];
        int foodBottom = Agent.getFoodCoords()[1] + Agent.getFoodShapeSize();
        int foodLeft = Agent.getFoodCoords()[0];
        int foodRight = Agent.getFoodCoords()[0] + Agent.getFoodShapeSize();

        int agentTop = getCurrentPosition()[1] - Agent.getAgentShapeSize();
        int agentBottom = getCurrentPosition()[1] + Agent.getAgentShapeSize();
        int agentLeft = getCurrentPosition()[0] - Agent.getAgentShapeSize();
        int agentRight = getCurrentPosition()[0] + Agent.getAgentShapeSize();

        // if (agentRight > foodLeft && agentLeft < foodRight && agentTop < foodBottom && agentBottom > foodTop)
        if (agentRight > foodLeft && agentLeft < foodRight && agentTop < foodBottom && agentBottom > foodTop) {
            setEnergyLevel(getEnergyLevel()+Agent.foodEnergyBoost);
            return true;
        }
        return false;
    }

    static int getTimeLasted() {
        return Agent.timeLasted;
    }
    static void setTimeLasted(int timeLasted) {
        Agent.timeLasted = timeLasted;
    }

    public static int getAgentShapeSize() {
        return Agent.agentShapeSize;
    }
    public static void setAgentShapeSize(int agentShapeSize) {
        Agent.agentShapeSize = agentShapeSize;
    }

    public static int getFoodShapeSize() {
        return Agent.foodShapeSize;
    }
    public static void setFoodShapeSize(int foodShapeSize) {
        Agent.foodShapeSize = foodShapeSize;
    }

    static int[] getFoodCoords() {
        return Agent.foodCoords;
    }
    static void setFoodCoords(int[] foodCoords) {
        Agent.foodCoords = foodCoords;
    }

    double getSpeedOfAgent() {
        return Utilities.getSpeedUsingSigmoid(0,100, -6,6, getEnergyLevel(), getGene());
    }
    double getEnergyUsedAtSpeed(double speedOfAgent) {
        // y = mx + c
        // x = (y - c) / m

        // y = speed
        // x = energy
        return (speedOfAgent - Agent.c) / Agent.m;
    }
}
