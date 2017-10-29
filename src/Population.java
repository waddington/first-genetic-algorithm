import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.ThreadLocalRandom;

public class Population {
    int requiredPopulationSize;
    int currentPopulationSize;
    int latestAgentId;
    ArrayList<Agent> agents;
    ArrayList<Agent> safeAgents;
    ArrayList<Agent> newAgents;
    int agentShapeRadius;
    int maxScore;
    int minScore;
    double meanScore;
    String maxScoreId = "--";
    double maxScoreK;
    double maxScoreMinSpeed;
    double maxScoreMaxSpeed;
    double maxScoreDirectionVariation;
    String worstScoreId = "--";
    double worstScoreK;
    double worstScoreMaxSpeed;
    double worstScoreMinSpeed;
    double worstScoreDirectionVariation;
    int currentGeneration;

    Population(int populationSize, int agentShapeRadius, int generationCount) {
        this.requiredPopulationSize = populationSize;
        this.currentPopulationSize = 0;
        this.agents = new ArrayList<>();
        this.newAgents = new ArrayList<>();
        this.agentShapeRadius = agentShapeRadius;
        this.currentGeneration = generationCount;

        createInitialPopulation();
    }

    void createInitialPopulation() {
        this.latestAgentId = 0;
        for (int i=0; i<requiredPopulationSize; i++) {
            Agent agent = new Agent("G"+this.currentGeneration+"-"+(i+1), true, this.agentShapeRadius);
            this.agents.add(agent);
            this.currentPopulationSize++;
        }
    }

    int[][] getAgentPositions() {
        int[][] agentPositions = new int[this.agents.size()][2];

        for (int i=0; i<this.agents.size(); i++) {
            agentPositions[i] = this.agents.get(i).getCurrentPosition();
        }

        return agentPositions;
    }

    void setFoodCoords(int x, int y) {
        Agent.setFoodCoords(new int[]{x,y});
    }
    int[] getFoodCoords() {
        return Agent.getFoodCoords();
    }

    void updateAgentPositions() {
        for (int i=0; i<this.agents.size(); i++)
            this.agents.get(i).updatePosition();
    }

    void updateAgentEnergies(Text textStatus) {
        int currentCount = 0;
        for (int i=0; i<this.agents.size(); i++)
            if (this.agents.get(i).updateEnergy(textStatus)) currentCount++;
        this.currentPopulationSize = currentCount;
    }

    int getCurrentPopulationSize() {
        return this.currentPopulationSize;
    }

    int getMaxScore() {
        return this.maxScore;
    }
    int getMinScore() {
        return this.minScore;
    }
    double getMeanScore() {
        return this.meanScore;
    }
    String getMaxScoreId() {
        return this.maxScoreId;
    }
    double getMaxScoreK() {
        return maxScoreK;
    }
    double getMaxScoreMinSpeed() {
        return maxScoreMinSpeed;
    }
    double getMaxScoreMaxSpeed() {
        return maxScoreMaxSpeed;
    }
    String getWorstScoreId() {
        return this.worstScoreId;
    }
    double getWorstScoreK() {
        return worstScoreK;
    }
    double getWorstScoreMaxSpeed() {
        return worstScoreMaxSpeed;
    }
    double getWorstScoreMinSpeed() {
        return worstScoreMinSpeed;
    }
    double getMaxScoreDirectionVariation() {
        return this.maxScoreDirectionVariation;
    }
    double getWorstScoreDirectionVariation() {
        return this.worstScoreDirectionVariation;
    }
    void updateScores(long playStartTime) {
        this.maxScore = Integer.MIN_VALUE;
        this.minScore = Integer.MAX_VALUE;
        this.meanScore = 0;

        long difference = System.currentTimeMillis() - playStartTime;
        int seconds = (int) difference/1000;
        int mSeconds = (int) difference%1000;

        Agent.setTimeLasted((seconds * 10) + (mSeconds/100));

        for (int i=0; i<this.agents.size(); i++) {
            int score = this.agents.get(i).updateScore();
            this.meanScore += score;
            if (score > this.maxScore) {
                this.maxScore = score;
                this.maxScoreId = this.agents.get(i).getId();
                this.maxScoreK = this.agents.get(i).getGene().getK();
                this.maxScoreMaxSpeed = this.agents.get(i).getGene().getMaxSpeed();
                this.maxScoreMinSpeed = this.agents.get(i).getGene().getMinSpeed();
                this.maxScoreDirectionVariation = this.agents.get(i).getGene().getDirectionVariation();
            }
            if (score < this.minScore) {
                this.minScore = score;
                this.worstScoreId = this.agents.get(i).getId();
                this.worstScoreK = this.agents.get(i).getGene().getK();
                this.worstScoreMaxSpeed = this.agents.get(i).getGene().getMaxSpeed();
                this.worstScoreMinSpeed = this.agents.get(i).getGene().getMinSpeed();
                this.worstScoreDirectionVariation = this.agents.get(i).getGene().getDirectionVariation();
            }
        }

        this.meanScore = this.meanScore / this.agents.size();
    }

    void doEvolution() {
        this.currentGeneration++;

        // Get rid of 50% of agents (Selection)
        cullAgents();

        // Create and mutate the new agents using crossover
        expandPopulation();

        // Mutate new agents
        // TODO: do mutations
        mutateNewAgents();

        // Collate all agents back to list
        collateAgents();

        // Update information
        updateInformation();
    }

    // Remove the worst performing agents (keep some bad ones and remove some good ones)
    void cullAgents() {
        // Order all agents by their score
        this.agents.sort(Comparator.comparing(Agent::getScore).reversed());

        // Remove 50% of agents
        int lower1 = (int) (this.agents.size() * 0.1);
        int upper1 = (int) (this.agents.size() * 0.15);
        int lower2 = (int) (this.agents.size() * 0.5);
        int upper2 = (int) (this.agents.size() * 0.85);

        for (int i=agents.size()-1; i>=0; i--) {
            if (i <= upper2 && i >= lower2)
                this.agents.remove(i);
            if (i <= upper1 && i >= lower1)
                this.agents.remove(i);
        }

        // Ensure we have an even amount of agents
        if (this.agents.size()%2 != 0)
            this.agents.remove(this.agents.size()-1);

        this.safeAgents = (ArrayList<Agent>) this.agents.clone();
    }

    // Create the new agents from the remaining agents
    void expandPopulation() {
        int newAgentsRequired = this.requiredPopulationSize - this.safeAgents.size();
        this.latestAgentId = 0;
        this.newAgents.clear();

        // Create new agents
        for (int i=0; i<newAgentsRequired; i+=2) {
            // Choose 2 parent agents at random
            Gene a = this.safeAgents.get(ThreadLocalRandom.current().nextInt(0, this.safeAgents.size())).getGene();
            Gene b = this.safeAgents.get(ThreadLocalRandom.current().nextInt(0, this.safeAgents.size())).getGene();

            // Using single-point crossover
            // min-speed | max-speed, starting area (half and half), k, directionVariation
            // TODO: change starting
            int[] startA = new int[]{(a.getStartingArea()[0]), (b.getStartingArea()[1])};
            int[] startB = new int[]{(b.getStartingArea()[0]), (a.getStartingArea()[1])};
            Gene childGeneA = new Gene(a.getMinSpeed(), b.getMaxSpeed(), startB, b.getK(), b.getDirectionVariation());
            Gene childGeneB = new Gene(b.getMinSpeed(), a.getMaxSpeed(), startA, a.getK(), a.getDirectionVariation());

            // Create the new agents and give them their new genes
            Agent newAgentA = new Agent("G"+this.currentGeneration+"-"+(i+1), false, this.agentShapeRadius);
            newAgentA.setGene(childGeneA);
            newAgentA.setCurrentPosition(childGeneA.getStartingArea());
            Agent newAgentB = new Agent("G"+this.currentGeneration+"-"+(i+1), false, this.agentShapeRadius);
            newAgentB.setGene(childGeneB);
            newAgentB.setCurrentPosition(childGeneB.getStartingArea());

            // Add the new agents to an array
            this.newAgents.add(newAgentA);
            this.newAgents.add(newAgentB);
        }
    }

    // Mutate the agents
    void mutateNewAgents() {}

    // Add all the agents back to the master list
    void collateAgents() {
        this.agents.clear();

        for (int i=0; i<this.safeAgents.size(); i++) {
            this.agents.add(this.safeAgents.get(i));
        }
        for (int i=0; i<this.newAgents.size(); i++) {
            this.agents.add(this.newAgents.get(i));
        }

        for (int i=0; i<this.agents.size(); i++) {
            this.agents.get(i).setAlive(true);
            this.agents.get(i).setEnergyLevel(100);
        }
    }

    // Update population information
    void updateInformation() {
        this.currentPopulationSize = this.agents.size();
    }
}
