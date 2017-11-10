import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

public class Population {
    int fullPopulationSize;
    int currentPopulationSize;
    double mutationRate;

    int currentGeneration;

    Agent[] agents;
    Agent[] safeParents;
    Agent[] newAgents;
    int agentSize;

    int agentMaximumSteps;
    int[] nestCoordinates;

    Random random;

    Population(int fullPopulationSize, int agentSize, int agentMaximumSteps, int[] nestCoordinates, double mutationRate) {
        this.fullPopulationSize = fullPopulationSize;
        this.currentPopulationSize = 0;
        this.mutationRate = mutationRate;

        this.currentGeneration = 1;

        this.agents = new Agent[fullPopulationSize];
        this.agentSize = agentSize;

        this.safeParents = new Agent[(int)Math.floor(this.agents.length / 2)];
        this.newAgents = new Agent[this.fullPopulationSize - this.safeParents.length];

        this.agentMaximumSteps = agentMaximumSteps;
        Agent.agentMaximumSteps = agentMaximumSteps;
        this.nestCoordinates = nestCoordinates;

        this.random = new Random();

        createPopulation();
    }

    void createPopulation() {
        for (int i=0; i<this.fullPopulationSize; i++) {
            String agentId = "G"+this.currentGeneration+"-"+(this.currentPopulationSize+1);
            Gene agentGene = createRandomGene();
            Rectangle agentShape = createAgentShape();

            Agent agent = new Agent();
            agent.setId(agentId);
            agent.setGene(agentGene);
            agent.setShape(agentShape);
            agent.setAlive(true);
            agent.setGotScore(false);
            agent.setCurrentStepNumber(0);
            agent.setCurrentCoordinates(this.nestCoordinates);

            this.agents[i] = agent;
        }

        this.currentPopulationSize = this.agents.length;
    }

    Gene createRandomGene() {
        String[] steps = new String[this.agentMaximumSteps*2];

        for (int i=0; i<this.agentMaximumSteps; i++) {
            steps[i] = (this.random.nextDouble() > 0.5) ? "1" : "0";
        }

        Gene gene = new Gene();
        gene.setSteps(steps);

        return gene;
    }

    Rectangle createAgentShape() {
        Color color = Color.CORNFLOWERBLUE;
        Rectangle shape = new Rectangle(0,0, this.agentSize, this.agentSize);
        shape.setFill(color);
        shape.setOpacity(0.6);

        return shape;
    }

    Agent[] getAgents() {
        return this.agents;
    }

    void doEvolution() {
        this.currentGeneration++;

        removeHalfAgents();

        expandPopulation();

        mutateNewAgents();

        redistributeAgentShapes();
        collateNewPopulation();
    }

    void removeHalfAgents() {
        // Taking 50% of agents
        Arrays.sort(this.agents, new AgentScoreComparator().reversed());

        this.safeParents = new Agent[this.safeParents.length];

        int perc0 = 0;
        int perc10 = (int) Math.floor(this.agents.length * 0.1);
        int perc20 = (int) Math.floor(this.agents.length * 0.2);
        int perc30 = (int) Math.floor(this.agents.length * 0.3);
        int perc40 = (int) Math.floor(this.agents.length * 0.4);
        int perc50 = (int) Math.floor(this.agents.length * 0.5);
        int perc60 = (int) Math.floor(this.agents.length * 0.6);
        int perc70 = (int) Math.floor(this.agents.length * 0.7);
        int perc80 = (int) Math.floor(this.agents.length * 0.8);
        int perc90 = (int) Math.floor(this.agents.length * 0.9);
        int perc100 = this.agents.length;

        int indexTracker = 0;
        for (int i=0; i<this.agents.length; i++) {
            if (i >= perc0 && i < perc40)
                this.safeParents[indexTracker++] = this.agents[i];

            if (i >= perc60 && i< perc70)
                this.safeParents[indexTracker++] = this.agents[i];
        }
    }

    void expandPopulation() {
        // Selection of parents via fitness proportionate selection
        // https://en.wikipedia.org/wiki/Stochastic_universal_sampling
        double sumOfFitness = 0;
        double previousProbability = 0;
        double[] agentProbabilities = new double[this.safeParents.length];

        for (Agent agent: this.safeParents) {
            sumOfFitness += agent.getScore();
        }

        for (int i=0; i<this.safeParents.length; i++) {
            agentProbabilities[i] = previousProbability + (safeParents[i].getScore() / sumOfFitness);
            previousProbability = agentProbabilities[i];
        }

        int numNewAgentsRequired = this.fullPopulationSize - this.safeParents.length;
        int idCounter = 1;
        // Create some agents
        while (numNewAgentsRequired > 1) {

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

            Agent parentA = this.safeParents[parentPositionA];
            Agent parentB = this.safeParents[parentPositionB];

            createOffspring(parentA, parentB, idCounter);

            idCounter += 2;
            numNewAgentsRequired -= 2;
        }
    }

    void createOffspring(Agent parentA, Agent parentB, int idCounter) {
        // Use uniform crossover
        Gene a = parentA.getGene();
        Gene b = parentB.getGene();

        String[] stepsA = a.getSteps();
        String[] stepsB = b.getSteps();

        String[] childStepsA = new String[stepsA.length];
        String[] childStepsB = new String[stepsB.length];

        for (int i=0; i<stepsA.length; i++) {
            String stepA = stepsA[i];
            String stepB = stepsB[i];

            int shouldSwap = (Math.random() > 0.5) ? 1 : 0;

            if (shouldSwap > 0) {
                childStepsA[i] = stepB;
                childStepsB[i] = stepA;
            } else {
                childStepsA[i] = stepA;
                childStepsB[i] = stepB;
            }
        }

        Gene childGeneA = new Gene();
        childGeneA.setSteps(childStepsA);
        Gene childGeneB = new Gene();
        childGeneB.setSteps(childStepsB);

        String idA = "G"+this.currentGeneration+"-"+idCounter;
        String idB = "G"+this.currentGeneration+"-"+(idCounter+1);

        Agent childAgentA = new Agent();
        childAgentA.setId(idA);
        childAgentA.setGene(childGeneA);
        childAgentA.setAlive(true);
        childAgentA.setScore(0);
        childAgentA.setCurrentStepNumber(0);
        childAgentA.setCurrentCoordinates(this.nestCoordinates);

        Agent childAgentB = new Agent();
        childAgentB.setId(idB);
        childAgentB.setGene(childGeneB);
        childAgentB.setAlive(true);
        childAgentB.setScore(0);
        childAgentB.setCurrentStepNumber(0);
        childAgentB.setCurrentCoordinates(this.nestCoordinates);

        this.newAgents[idCounter-1] = childAgentA;
        this.newAgents[idCounter] = childAgentB;
    }

    void mutateNewAgents() {
        for (Agent agent: this.newAgents) {
            Gene gene = agent.getGene();
            String[] steps = gene.getSteps();

            for (int i=0; i<steps.length; i++) {
                int shouldFlip = (Math.random() <= this.mutationRate) ? 1 : 0;

                if (shouldFlip == 1) {
                    if ("0".equals(steps[i])) {
                        steps[i] = "1";
                    } else {
                        steps[i] = "0";
                    }
                }
            }

            gene.setSteps(steps);
            agent.setGene(gene);
        }
    }

    void redistributeAgentShapes() {
        // Go through all old agents and take their shapes and reassign them

        Rectangle[] shapes = new Rectangle[this.agents.length];

        for (int i=0; i<this.agents.length; i++) {
            shapes[i] = this.agents[i].getShape();
        }

        for (int i=0; i<this.safeParents.length; i++) {
            this.safeParents[i].setShape(shapes[2*i]);
            this.newAgents[i].setShape(shapes[(2*i)+1]);
        }
    }

    void collateNewPopulation() {
        this.agents = new Agent[this.agents.length];

        for (int i=0; i<this.safeParents.length; i++) {
            this.agents[2*i] = this.safeParents[i];
            this.agents[(2*i)+1] = this.newAgents[i];
        }

        for (Agent agent: this.agents) {
            agent.setAlive(true);
            agent.setGotScore(false);
            agent.setScore(0);
            agent.setCurrentStepNumber(0);
            agent.setCurrentCoordinates(this.nestCoordinates);
        }
    }

    void countPopulation() {
        int populationSize = 0;
        for (Agent agent: this.agents) {
            if (agent.isAlive())
                populationSize++;
        }
        this.currentPopulationSize = populationSize;
    }

    // Custom comparator for agent scores
    class AgentScoreComparator implements Comparator<Agent> {
        public int compare(Agent a1, Agent a2) {
            return Integer.compare(a1.getScore(), a2.getScore());
        }
    }
}
