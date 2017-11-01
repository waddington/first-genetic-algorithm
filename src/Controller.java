import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

enum GameState {
    PLAYING,
    EVOLVING,
    WAITING_TO_EVOLVE,
    WAITING_TO_PLAY
}

public class Controller {
    GameState gameState;
    long currentPlayEndTime;
    Timeline timeline;
    AnimationTimer timer;

    int lengthOfEachRound = 5;
    int fullPopulationSize = 20; // Use an even number
    Population population;

    ArrayList<Circle> agentShapes;
    int agentShapeSize = 3;
    Rectangle foodShape;
    int foodShapeSize = 10;
    boolean foodShouldMove;

    int currentGenerationCount;

    boolean playingMultipleGenerations;
    boolean playingEndlessMode;
    int generationsLeftToSkip;

    // Play/Evolve/skip-gens Buttons
    @FXML Button controlButtonPlay;
    @FXML Button controlButtonEvolve;
    @FXML Button buttonPlayGeneration5x;
    @FXML Button buttonPlayGeneration10x;

    // Time remaining
    @FXML Text textTimeRemaining;

    // Information box
    @FXML Text informationGenerationNumber;
    @FXML Text informationPopulationCount;

    // Fitness information box
    @FXML Text fitnessCountBest;
    @FXML Text fitnessCountWorst;
    @FXML Text fitnessCountMean;

    // Best genetics information box
    @FXML Text geneticsBestId;
    @FXML Text geneticsBestK;
    @FXML Text geneticsBestMinSpeed;
    @FXML Text geneticsBestMaxSpeed;
    @FXML Text geneticsBestDirectionVariation;

    // Worst genetics information box
    @FXML Text geneticsWorstId;
    @FXML Text geneticsWorstK;
    @FXML Text geneticsWorstMinSpeed;
    @FXML Text geneticsWorstMaxSpeed;
    @FXML Text geneticsWorstDirectionVariation;

    // Status bar
    @FXML Text textStatus;

    // Graphics box
    @FXML Pane graphicsBox;

    @FXML
    void initialize() {
        this.currentGenerationCount = 1;
        this.playingMultipleGenerations = false;
        this.playingEndlessMode = false;
        this.agentShapes = new ArrayList<>();

        createPopulation();
        createFoodObject();
        createAgentObjects();

        drawAgents();
        drawFood();

        this.gameState = GameState.WAITING_TO_PLAY;
        this.textTimeRemaining.setText(this.lengthOfEachRound+".00s");
        this.informationPopulationCount.setText("--/"+this.fullPopulationSize);

        theLoopWrapper();
    }

    // Update game status depending on what button is clicked
    @FXML
    void gameButtonClicked(MouseEvent event) {
        // We only want to listen to primary button clicks
        if (event.getButton() == MouseButton.PRIMARY) {
            String buttonId = ((Button) event.getSource()).getId();

            switch (buttonId) {
                case "controlButtonPlay": {
                    playButtonClicked();
                    break;
                }
                case "controlButtonEvolve": {
                    this.gameState = GameState.EVOLVING;
                    this.currentGenerationCount++;
                    break;
                }
                case "buttonPlayGeneration5x": {
                    this.playingMultipleGenerations = true;
                    this.generationsLeftToSkip = 5;
                    playButtonClicked();
                    break;
                }
                case "buttonPlayGeneration10x": {
                    this.playingMultipleGenerations = true;
                    this.generationsLeftToSkip = 10;
                    playButtonClicked();
                    break;
                }
            }
        }
    }

    // Set the game to play
    void playButtonClicked() {
        this.gameState = GameState.PLAYING;
        this.currentPlayEndTime = System.currentTimeMillis() + (this.lengthOfEachRound * 1000);
    }

    // My loop timer function
    // https://stackoverflow.com/a/13060022/3259361
    void theLoopWrapper() {
        final Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, actionEvent -> {
                    theLoop(); // My function for all the code that should be looped over - it's the last function in this file
                }),
                new KeyFrame(Duration.millis(1000/30))
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    // Create the initial population of agents
    void createPopulation() {
        this.population = new Population(this.fullPopulationSize, this.agentShapeSize, this.foodShapeSize, this.currentGenerationCount);
    }

    // Create the shape that will represent the food
    void createFoodObject() {
        int xPos = ThreadLocalRandom.current().nextInt((this.foodShapeSize),(701-this.foodShapeSize));
        int yPos = ThreadLocalRandom.current().nextInt((this.foodShapeSize),(521-this.foodShapeSize));

        this.foodShape = new Rectangle(xPos,yPos, this.foodShapeSize, this.foodShapeSize);
        this.foodShape.setVisible(false);
        this.foodShape.setFill(Color.RED);

        this.foodShouldMove = false;

        this.graphicsBox.getChildren().add(this.foodShape);

        Agent.setFoodCoords(new int[] {xPos, yPos});
    }

    // Create the shapes that will represent the agents
    void createAgentObjects() {
        this.agentShapes.clear();

        for (int i=0; i<this.fullPopulationSize; i++) {
            Circle agentShape = new Circle(0,0, this.agentShapeSize);
            agentShape.setVisible(false);

            this.agentShapes.add(agentShape);
            this.graphicsBox.getChildren().add(agentShape);
        }
    }

    // Draw the agent objects to the screen
    void drawAgents() {
        String[] agentIds = this.population.getAllAgentIds();

        for (int i=0; i<agentIds.length; i++) {
            Agent agent = this.population.getAgentById(agentIds[i]);
            if (agent.isAlive()) {
                int[] agentPosition = Utilities.fitToBounds(agent.getCurrentPosition(), this.agentShapeSize);

                this.agentShapes.get(i).setCenterX(agentPosition[0]);
                this.agentShapes.get(i).setCenterY(agentPosition[1]);
                this.agentShapes.get(i).setVisible(true);
            } else {
                this.agentShapes.get(i).setVisible(false);
            }
        }
    }

    // Draw the food object on the screen
    void drawFood() {
        if (this.foodShouldMove) {
            int newX = ThreadLocalRandom.current().nextInt((this.foodShapeSize),(701-this.foodShapeSize));
            int newY = ThreadLocalRandom.current().nextInt((this.foodShapeSize),(521-this.foodShapeSize));

            int[] newPositions = Utilities.fitToBounds(new int[] {newX, newY}, this.foodShapeSize);

            this.foodShape.setX(newPositions[0]);
            this.foodShape.setY(newPositions[1]);

            Agent.setFoodCoords(newPositions);

            this.foodShouldMove = false;
            this.foodShape.setVisible(true);
        } else {
            this.foodShape.setVisible(true);
        }
    }

    // Check what state each of the buttons should be in
    void updateGameButtons() {
        switch (this.gameState) {
            case PLAYING: {
                setButtonDisabled(true,true,true,true);
                break;
            }
            case EVOLVING: {
                setButtonDisabled(true,true,true,true);
                break;
            }
            case WAITING_TO_PLAY: {
                setButtonDisabled(false,true,false,false);
                break;
            }
            case WAITING_TO_EVOLVE: {
                setButtonDisabled(true,false,true,true);
                break;
            }
        }
    }

    // Function to set button disabled status
    void setButtonDisabled(boolean playButton, boolean evolveButton, boolean play5Button, boolean play10Button) {
        controlButtonPlay.setDisable(playButton);
        controlButtonEvolve.setDisable(evolveButton);
        buttonPlayGeneration5x.setDisable(play5Button);
        buttonPlayGeneration10x.setDisable(play10Button);
    }

    // Update the text fields
    void updateGameText() {
        this.informationGenerationNumber.setText(""+this.currentGenerationCount);
        this.informationPopulationCount.setText(""+this.population.getCurrentPopulationSize()+"/"+this.fullPopulationSize);

        Agent bestAgent = this.population.getAgentById(this.population.getMaxScoreAgentId());
        Agent worstAgent = this.population.getAgentById(this.population.getMinScoreAgentId());

        this.fitnessCountBest.setText(""+bestAgent.getScore());
        this.fitnessCountWorst.setText(""+worstAgent.getScore());
        this.fitnessCountMean.setText(""+(int)this.population.getMeanScore());

        this.geneticsBestId.setText(""+this.population.getMaxScoreAgentId());
        this.geneticsBestK.setText(String.format("%.2f", bestAgent.getGene().getK()));
        this.geneticsBestMaxSpeed.setText(String.format("%.2f", bestAgent.getGene().getMaxSpeed()));
        this.geneticsBestMinSpeed.setText(String.format("%.2f", bestAgent.getGene().getMinSpeed()));
        this.geneticsBestDirectionVariation.setText(String.format("%.2f", bestAgent.getGene().getDirectionVariation()));

        this.geneticsWorstId.setText(""+this.population.getMinScoreAgentId());
        this.geneticsWorstK.setText(String.format("%.2f", worstAgent.getGene().getK()));
        this.geneticsWorstMaxSpeed.setText(String.format("%.2f",worstAgent.getGene().getMaxSpeed()));
        this.geneticsWorstMinSpeed.setText(String.format("%.2f", worstAgent.getGene().getMinSpeed()));
        this.geneticsWorstDirectionVariation.setText(String.format("%.2f", worstAgent.getGene().getDirectionVariation()));
    }

    // Update the count-down timer
    void updateTimer() {
        long difference = this.currentPlayEndTime - System.currentTimeMillis();

        int secondsAsInt = (int) difference / 1000;
        String seconds = (secondsAsInt < 10) ? "0"+secondsAsInt : ""+secondsAsInt;

        int msAsInt = (int) (difference % 1000) / 10;
        msAsInt = (msAsInt < 0) ? 0 : msAsInt;
        String milliseconds = (msAsInt < 10) ? "0"+msAsInt : ""+msAsInt;

        this.textTimeRemaining.setText(seconds+"."+milliseconds+"s");

        if (secondsAsInt <= 0 && msAsInt <= 0) {
            this.gameState = GameState.WAITING_TO_EVOLVE;
        }
    }

    // Get the latest agent scores
    void updateScores() {
        this.population.updateScores(this.currentPlayEndTime - (this.lengthOfEachRound * 1000));
    }

    // Update the agents' positions' and energies'
    boolean updateAgents() {
        return this.population.updateAgents(this.textStatus);
    }

    // Go through the evolutionary process
    void doPopulationEvolution() {
        this.population.doEvolution();
        drawAgents();
    }

    // All the code that will be looped over
    void theLoop() {
        if (this.gameState == GameState.WAITING_TO_PLAY && this.playingMultipleGenerations) {
            if (this.generationsLeftToSkip >= 1) {
                this.gameState = GameState.PLAYING;
                playButtonClicked();
            }
        }

        if (this.gameState == GameState.WAITING_TO_EVOLVE && this.playingMultipleGenerations) {
            if (this.generationsLeftToSkip >= 1) {
                this.gameState = GameState.EVOLVING;
                this.currentGenerationCount++;
                if (!this.playingEndlessMode)
                    this.generationsLeftToSkip--;
            } else {
                this.playingMultipleGenerations = false;
            }
        }

        updateGameButtons();

        if (this.gameState == GameState.PLAYING ||
            this.gameState == GameState.WAITING_TO_PLAY ||
            this.gameState == GameState.WAITING_TO_EVOLVE ) {
            drawAgents();
            drawFood();
            updateGameText();
        }

        if (this.gameState == GameState.PLAYING) {
            updateTimer();
            updateScores();
            updateGameText();
            this.foodShouldMove = updateAgents();
        }

        if (this.gameState == GameState.EVOLVING) {
            doPopulationEvolution();
            this.gameState = GameState.WAITING_TO_PLAY;
        }
    }
}
