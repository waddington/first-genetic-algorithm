import javafx.scene.shape.Rectangle;

public class Agent {
    String id;
    Gene gene;
    Rectangle shape;
    boolean isAlive;
    boolean gotScore;
    int score;

    int currentStepNumber;
    int[] currentCoordinates;
    boolean takenFirstRender = false;

    static int agentMaximumSteps;

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

    Rectangle getShape() {
        return this.shape;
    }
    void setShape(Rectangle shape) {
        this.shape = shape;
    }

    boolean isAlive() {
        return this.isAlive;
    }
    void setAlive(boolean alive) {
        this.isAlive = alive;
    }

    boolean isGotScore() {
        return this.gotScore;
    }
    void setGotScore(boolean gotScore) {
        this.gotScore = gotScore;
    }

    int getScore() {
        return this.score;
    }
    void setScore(int score) {
        this.score = score;
    }

    int getCurrentStepNumber() {
        return this.currentStepNumber;
    }
    void setCurrentStepNumber(int newStepNumber) {
        this.currentStepNumber = newStepNumber;
    }

    int[] getCurrentCoordinates() {
        return this.currentCoordinates;
    }
    void setCurrentCoordinates(int[] newCoordinates) {
        this.currentCoordinates = newCoordinates;
    }

    void doNextMove() {
        if (!takenFirstRender) {
            takenFirstRender = true;
        } else {
            if (this.currentStepNumber >= Agent.agentMaximumSteps) {
                setAlive(false);
            } else {
                String stepP1 = getGene().getStep(this.currentStepNumber*2);
                String stepP2 = getGene().getStep((this.currentStepNumber*2)+1);
                String step = stepP1+stepP2;

                if (isAlive())
                    this.currentStepNumber++;

                int[] newCoordinates = new int[] {getCurrentCoordinates()[0], getCurrentCoordinates()[1]};

                switch (step) {
                    case "00": {
                        newCoordinates[1] -= 1;
                        break;
                    }
                    case "01": {
                        newCoordinates[0] += 1;
                        break;
                    }
                    case "10": {
                        newCoordinates[1] += 1;
                        break;
                    }
                    case "11": {
                        newCoordinates[0] -= 1;
                        break;
                    }
                }

                setCurrentCoordinates(newCoordinates);
            }
        }
    }
}
