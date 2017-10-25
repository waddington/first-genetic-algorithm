import java.util.ArrayList;

public class Population {
    int requiredPopulationSize;
    int currentPopulationSize;
    int latestAgentId;
    ArrayList<Agent> agents;
    int agentShapeRadius;

    Population(int populationSize, int agentShapeRadius) {
        this.requiredPopulationSize = populationSize;
        this.currentPopulationSize = 0;
        this.latestAgentId = 0;
        this.agents = new ArrayList<>();
        this.agentShapeRadius = agentShapeRadius;

        createInitialPopulation();
    }

    void createInitialPopulation() {
        for (int i=0; i<requiredPopulationSize; i++) {
            Agent agent = new Agent(++this.latestAgentId, true, this.agentShapeRadius);
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

    void updateAgentEnergies() {
        for (int i=0; i<this.agents.size(); i++)
            this.agents.get(i).updateEnergy();
    }
}
