import javafx.scene.text.Text;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Population {
    private int fullPopulationSize;
    private int currentPopulationSize;
    private int currentGeneration;

    private HashMap<String, Agent> agents; // Map agent ID to the agent so I can lookup the agent by its ID later
    private ArrayList<Agent> agentsGoingToNextGeneration;
    private ArrayList<Agent> newOffspringAgents;

    private int agentShapeSize;

    private String maxScoreAgentId;
    private String minScoreAgentId;
    private double meanScore;

    Population(int fullPopulationSize, int agentShapeSize, int foodShapeSize, int currentGeneration) {
        this.fullPopulationSize = fullPopulationSize;
        this.agentShapeSize = agentShapeSize;
        Agent.setAgentShapeSize(agentShapeSize);
        Agent.setFoodShapeSize(foodShapeSize);
        this.currentGeneration = currentGeneration;

        this.agents = new HashMap<>();
        this.agentsGoingToNextGeneration = new ArrayList<>();
        this.newOffspringAgents = new ArrayList<>();

        this.currentPopulationSize = 0;
        this.maxScoreAgentId = null;
        this.minScoreAgentId = null;

        createInitialPopulation();
    }

    // Getters and Setters
    int getFullPopulationSize() {
        return this.fullPopulationSize;
    }
    void setFullPopulationSize(int fullPopulationSize) {
        this.fullPopulationSize = fullPopulationSize;
    }

    int getCurrentPopulationSize() {
        return this.currentPopulationSize;
    }
    void setCurrentPopulationSize(int currentPopulationSize) {
        this.currentPopulationSize = currentPopulationSize;
    }

    int getCurrentGeneration() {
        return this.currentGeneration;
    }
    void setCurrentGeneration(int currentGeneration) {
        this.currentGeneration = currentGeneration;
    }

    int getAgentShapeSize() {
        return this.agentShapeSize;
    }
    void setAgentShapeSize(int agentShapeSize) {
        this.agentShapeSize = agentShapeSize;
    }

    String getMaxScoreAgentId() {
        return this.maxScoreAgentId;
    }
    void setMaxScoreAgentId(String maxScoreAgentId) {
        this.maxScoreAgentId = maxScoreAgentId;
    }

    String getMinScoreAgentId() {
        return this.minScoreAgentId;
    }
    void setMinScoreAgentId(String minScoreAgentId) {
        this.minScoreAgentId = minScoreAgentId;
    }

    double getMeanScore() {
        return this.meanScore;
    }
    void setMeanScore(int meanScore) {
        this.meanScore = meanScore;
    }

    Agent getAgentById(String id) {
        return this.agents.get(id);
    }
    void addAgentToMasterList(String id, Agent agent) {
        this.agents.put(id, agent);
        setCurrentPopulationSize(getCurrentPopulationSize()+1);
    }

    // Create the first population
    void createInitialPopulation() {
        while (getCurrentPopulationSize() < getFullPopulationSize()) {

            String id = "G"+getCurrentGeneration()+"-"+(getCurrentPopulationSize()+1);

            Agent agent = new Agent();
            Gene gene = createRandomGene();

            agent.setId(id);
            agent.setGene(gene);
            agent.setAlive(true);
            agent.setScore(0);
            agent.setEnergyLevel(100);
            agent.setCurrentPosition(gene.getStartingPosition());

            addAgentToMasterList(id, agent);
        }

        this.maxScoreAgentId = "G1-1";
        this.minScoreAgentId = "G1-1";
    }

    // Create a new gene with random values
    Gene createRandomGene() {
        double minSpeed = Utilities.linearTransform(0,1, 3,6, (ThreadLocalRandom.current().nextDouble(0,1)));
        double maxSpeed = Utilities.linearTransform(0,1, 9,12, (ThreadLocalRandom.current().nextDouble(0,1)));
        int startX = ThreadLocalRandom.current().nextInt(getAgentShapeSize(), (701-getAgentShapeSize()));
        int startY = ThreadLocalRandom.current().nextInt(getAgentShapeSize(), (521-getAgentShapeSize()));
        double k = Utilities.linearTransform(0,1, 0.75,1.25, (ThreadLocalRandom.current().nextDouble(0,1)));
        double directionVariation = ThreadLocalRandom.current().nextDouble(-5, 5);

        Gene gene = new Gene();
        gene.setMinSpeed(minSpeed);
        gene.setMaxSpeed(maxSpeed);
        gene.setStartingPosition(new int[] {startX, startY});
        gene.setK(k);
        gene.setDirectionVariation(directionVariation);

        return gene;
    }

    // Get the IDs of all current agents
    String[] getAllAgentIds() {
        String[] agentIds = new String[this.agents.size()];

        Iterator iterator = this.agents.entrySet().iterator();
        int i = 0;

        while (iterator.hasNext()) {
            Map.Entry pair = (Map.Entry) iterator.next();
            String agentId = (String) pair.getKey();

            agentIds[i] = agentId;

            i++;
        }

        return agentIds;
    }

    // Update the scores of each agent
    void updateScores(long currentPlayStartTime) {
        int maxScore = Integer.MIN_VALUE;
        int minScore = Integer.MAX_VALUE;
        int localMeanScore = 0;

        long difference = System.currentTimeMillis() - currentPlayStartTime;
        int seconds = (int) difference / 1000;
        int milliseconds = (int) difference % 1000;

        Agent.setTimeLasted((seconds * 10) + (milliseconds / 100));

        Iterator iterator = this.agents.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry pair = (Map.Entry) iterator.next();
            String agentId = (String) pair.getKey();
            Agent agent = (Agent) pair.getValue();

            int score = agent.updateScore();
            localMeanScore += score;

            if (score > maxScore) {
                maxScore = score;
                setMaxScoreAgentId(agentId);
            }

            if (score < minScore) {
                minScore = score;
                setMinScoreAgentId(agentId);
            }
        }

        localMeanScore /= getFullPopulationSize();
        setMeanScore(localMeanScore);
    }

    // Update the agents' energy and positions
    boolean updateAgents(Text textStatus) {
        boolean foodShouldMove = false;

        int currentLiveAgentCount = 0;

        Iterator iterator = this.agents.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry pair = (Map.Entry) iterator.next();
            String agentId = (String) pair.getKey();
            Agent agent = (Agent) pair.getValue();

            boolean agentFoodCollision = agent.updatePosition();

            if (!foodShouldMove && agentFoodCollision)
                foodShouldMove = true;


            boolean agentStillAlive = agent.updateEnergy(textStatus);
            if (agentStillAlive) {
                currentLiveAgentCount++;
            }
        }

        setCurrentPopulationSize(currentLiveAgentCount);
        return foodShouldMove;
    }

    // Start the evolutionary process
    void doEvolution() {
        setCurrentGeneration(getCurrentGeneration()+1);
        this.newOffspringAgents.clear();

        // Remove ~50% of the agents
        removeHalfAgents();

        // Selection
        // Crossover
        expandPopulation();

        // Mutation
        // Accepting
        collateNewPopulation();
    }

    void removeHalfAgents() {
        ArrayList<Agent> currentAgents = new ArrayList<>();

        Iterator iterator = agents.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry pair = (Map.Entry) iterator.next();
            currentAgents.add((Agent) pair.getValue());
        }

        currentAgents.sort(Comparator.comparing(Agent::getScore).reversed());

        int lower1 = (int) (currentAgents.size() * 0.1);
        int upper1 = (int) (currentAgents.size() * 0.15);
        int lower2 = (int) (currentAgents.size() * 0.5);
        int upper2 = (int) (currentAgents.size() * 0.85);

        for (int i=currentAgents.size()-1; i>=0; i--) {
            if (i <= upper2 && i >= lower2)
                currentAgents.remove(i);
            if (i <= upper1 && i >= lower1)
                currentAgents.remove(i);
        }

        // Ensure we have an even amount of agents
        if (currentAgents.size()%2 != 0)
            currentAgents.remove(currentAgents.size()-1);

        this.agentsGoingToNextGeneration = currentAgents;
    }

    void expandPopulation() {
        // Selection of parents via fitness proportionate selection
        // https://en.wikipedia.org/wiki/Stochastic_universal_sampling
        double sumOfFitness = 0;
        double previousProbability = 0;
        double[] agentProbabilities = new double[this.agentsGoingToNextGeneration.size()];

        for (int i=0; i<this.agentsGoingToNextGeneration.size(); i++) {
            sumOfFitness += this.agentsGoingToNextGeneration.get(i).getScore();
        }

        for (int i=0; i<this.agentsGoingToNextGeneration.size(); i++) {
            agentProbabilities[i] = previousProbability + (this.agentsGoingToNextGeneration.get(i).getScore() / sumOfFitness);
            previousProbability = agentProbabilities[i];
        }

        int numberOfNewAgentsRequired = getFullPopulationSize() - this.agentsGoingToNextGeneration.size();

        int idCounter = 1;

        while (numberOfNewAgentsRequired > 1) {
            double randomValueForParentSelectionA = Math.random();
            double randomValueForParentSelectionB = Math.random();

            int parentPositionA = 0;
            int parentPositionB = 0;

            boolean parentPositionAFound = false;
            boolean parentPositionBFound = false;

            int i = 0;
            while (!parentPositionAFound || !parentPositionBFound) {
                double agentProbability = agentProbabilities[i];

                if (randomValueForParentSelectionA < agentProbability && !parentPositionAFound) {
                    parentPositionA = i;
                    parentPositionAFound = true;
                }

                if (randomValueForParentSelectionB < agentProbability && !parentPositionBFound) {
                    parentPositionB = i;
                    parentPositionBFound = true;
                }

                i++;
            }

            Agent parentA = this.agentsGoingToNextGeneration.get(parentPositionA);
            Agent parentB = this.agentsGoingToNextGeneration.get(parentPositionB);

            // Create offspring using single-point crossover
            createOffspring(parentA, parentB, idCounter);

            idCounter += 2;
            numberOfNewAgentsRequired -= 2;
        }
    }

    void createOffspring(Agent parentA, Agent parentB, int idCounter) {
        // Create offspring using single-point crossover
        // min-speed | max-speed, starting area (half and half), k, directionVariation
        Gene a = parentA.getGene();
        Gene b = parentB.getGene();

        int[] startA = new int[] {a.getStartingPosition()[0], b.getStartingPosition()[1]};
        int[] startB = new int[] {b.getStartingPosition()[0], a.getStartingPosition()[1]};

        Gene childGeneA = new Gene();
        childGeneA.setMinSpeed(a.getMinSpeed());
        childGeneA.setMaxSpeed(b.getMaxSpeed());
        childGeneA.setStartingPosition(startB);
        childGeneA.setK(b.getK());
        childGeneA.setDirectionVariation(b.getDirectionVariation());

        Gene childGeneB = new Gene();
        childGeneB.setMinSpeed(b.getMinSpeed());
        childGeneB.setMaxSpeed(a.getMaxSpeed());
        childGeneB.setStartingPosition(startA);
        childGeneB.setK(a.getK());
        childGeneB.setDirectionVariation(a.getDirectionVariation());

        String idA = "G"+getCurrentGeneration()+"-"+idCounter;
        String idB = "G"+getCurrentGeneration()+"-"+(idCounter+1);

        Agent childAgentA = new Agent();
        childAgentA.setId(idA);
        childAgentA.setGene(childGeneA);
        childAgentA.setAlive(true);
        childAgentA.setScore(0);
        childAgentA.setEnergyLevel(100);
        childAgentA.setCurrentPosition(childGeneA.getStartingPosition());

        Agent childAgentB = new Agent();
        childAgentB.setId(idB);
        childAgentB.setGene(childGeneB);
        childAgentB.setAlive(true);
        childAgentB.setScore(0);
        childAgentB.setEnergyLevel(100);
        childAgentB.setCurrentPosition(childGeneB.getStartingPosition());

        this.newOffspringAgents.add(childAgentA);
        this.newOffspringAgents.add(childAgentB);
    }

    void collateNewPopulation() {
        this.agents.clear();

        for (int i=0; i<this.agentsGoingToNextGeneration.size(); i++) {
            Agent agent = this.agentsGoingToNextGeneration.get(i);
            agent.setAlive(true);
            agent.setScore(0);
            agent.setEnergyLevel(100);
            agent.setCurrentPosition(agent.getGene().getStartingPosition());
            String id = agent.getId();
            addAgentToMasterList(id, agent);
        }

        for (int i=0; i<this.newOffspringAgents.size(); i++) {
            Agent agent = this.newOffspringAgents.get(i);
            String id = agent.getId();
            addAgentToMasterList(id, agent);
        }

        setCurrentPopulationSize(this.agents.size());
    }
}
