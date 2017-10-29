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
    WAITING_EVOLVE,
    WAITING_PLAY,
}

public class Controller {
    private int lengthOfEachRound = 10; // How many seconds in a round

    // Play/Pause/Evolve Buttons
    @FXML
    Button controlButtonPlay;
    @FXML
    Button controlButtonEvolve;// Skip-to generationCount buttons
    @FXML
    Button buttonSkipGeneration1x;
    @FXML
    Button buttonSkipGeneration5x;
    @FXML
    Button buttonSkipGeneration10x;
    // Button overall view
    private String[] buttons = {"controlButtonPlay", "controlButtonEvolve", "buttonSkipGeneration1x", "buttonSkipGeneration5x", "buttonSkipGeneration10x"};

    // Status
    private GameState gameControlStatus;

    // Time remaining
    @FXML
    Text textTimeRemaining;

    // Information box
    @FXML
    Text informationGenerationNumber;
    @FXML
    Text informationPopulationCount;

    // Fitness information box
    @FXML
    Text fitnessCountBest;
    @FXML
    Text fitnessCountWorst;
    @FXML
    Text fitnessCountMean;

    // Genetics information boxes
    @FXML
    Text geneticsBestId;
    @FXML
    Text geneticsBestK;
    @FXML
    Text geneticsBestMinSpeed;
    @FXML
    Text geneticsBestMaxSpeed;
    @FXML
    Text geneticsWorstId;
    @FXML
    Text geneticsWorstK;
    @FXML
    Text geneticsWorstMinSpeed;
    @FXML
    Text geneticsWorstMaxSpeed;
    @FXML
    Text geneticsBestDirectionVariation;
    @FXML
    Text geneticsWorstDirectionVariation;


    // Status bar
    @FXML
    Text textStatus;

    // Graphics box
    @FXML
    Pane graphicsBox;

    // Timer stuff
    long playEndTime;

    // Animation stuff
    private Timeline timeline;
    private AnimationTimer timer;

    // Genetic Algorithm stuff starts
    // Population
    Population population;
    int populationSize = 20;
    ArrayList<Circle> agentShapes;
    Rectangle food;
    boolean foodShouldMove;
    int generationCount = 1;

    // Drawing stuff
    int agentShapeRadius = 3;
    int foodWidth = 10;

    boolean shouldSkipGens = false;
    int gensToSkip = 0;

    @FXML
    private void initialize() {
        this.population = new Population(this.populationSize, this.agentShapeRadius, this.generationCount); // Create the initial population
        createFood();
        this.agentShapes = new ArrayList<>(); // Create the shapes for each agent
        createAgentShapes();
        this.gameControlStatus = GameState.WAITING_PLAY; // Set the initial game state to "Waiting for user to press play"
        this.textTimeRemaining.setText(this.lengthOfEachRound+".00s");
        this.informationPopulationCount.setText("--/"+this.populationSize);

        theLoopWrapper(); // Start the animation loop
    }

    // My loop timer function
    // https://stackoverflow.com/a/13060022/3259361
    private void theLoopWrapper() {
        final Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, actionEvent -> {
                    theLoop(); // My function for all the code that should be looped over
                }),
                new KeyFrame(Duration.millis(1000/30))
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    // Moving my loop code to a new function to keep code tidier
    private void theLoop() {
        updateGameButtons(); // Update buttons based on game status
        updateGameText(); // Update the text in the information windows
        if (this.gameControlStatus == GameState.PLAYING || this.gameControlStatus == GameState.WAITING_EVOLVE || this.gameControlStatus == GameState.WAITING_PLAY) {
            drawAgents();
            drawFood();
        }
        if (this.gameControlStatus == GameState.PLAYING) { // If game state is "playing", update the countdown timer and allow agents to move
            updateTimer();
            refreshScores();
            updateGameText();
            this.population.updateAgentPositions();
            this.population.updateAgentEnergies(this.textStatus);
            // TODO: merge update position and energy
        }
        if (this.gameControlStatus == GameState.EVOLVING) {
            // Time to evolve the population
            doPopulationEvolution();
            this.gameControlStatus = GameState.WAITING_PLAY;
        }
    }

    // Update the status of the buttons in the game
    // Some can/cannot be clicked depending on what stage the game is in
    private void updateGameButtons() {
        switch (gameControlStatus) {
            case PLAYING: {setButtonDisabled(true,true,true,true,true);break;} // playing
            case EVOLVING: {setButtonDisabled(true,true,true,true,true);break;} // evolving
            case WAITING_EVOLVE: {setButtonDisabled(true,false,true,true,true);break;} // waiting to evolve
            case WAITING_PLAY: {setButtonDisabled(false,true,false,false,false);break;} // waiting to play
        }
    }

    // Function to set button disabled status
    private void setButtonDisabled(boolean a, boolean b, boolean c, boolean d, boolean e) {
        controlButtonPlay.setDisable(a);
        controlButtonEvolve.setDisable(b);
        buttonSkipGeneration1x.setDisable(c);
        buttonSkipGeneration5x.setDisable(d);
        buttonSkipGeneration10x.setDisable(e);
    }

    // Method to update game status depending on button clicked
    @FXML
    private void gameButtonClicked(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) { // We only want clicks from the primary button
            String buttonId = ((Button)event.getSource()).getId();
            switch (buttonId) {
                case "controlButtonPlay": {
                    gameControlStatus = GameState.PLAYING;
                    playButtonClicked();
                    break;
                }
                case "controlButtonEvolve": {
                    gameControlStatus = GameState.EVOLVING;
                    this.generationCount++;
                    break;
                }
                case "buttonSkipGeneration1x": {
                    gameControlStatus = GameState.PLAYING;
                    this.shouldSkipGens = true;
                    this.gensToSkip = 1;
                    playButtonClicked();
                    break;
                }
                case "buttonSkipGeneration5x": {
                    gameControlStatus = GameState.PLAYING;
                    this.shouldSkipGens = true;
                    this.gensToSkip = 5;
                    playButtonClicked();
                    break;
                }
                case "buttonSkipGeneration10x": {
                    gameControlStatus = GameState.PLAYING;
                    this.shouldSkipGens = true;
                    this.gensToSkip = 10;
                    playButtonClicked();
                    break;
                }
            }
        }
    }

    // Set the game to "play" - countdown timer then update game status
    private void playButtonClicked() {
        playEndTime = System.currentTimeMillis() + (lengthOfEachRound * 1000);
    }

    // Update the countdown timer
    private void updateTimer() {
        long difference = playEndTime - System.currentTimeMillis(); // Calculating time remaining
        int s = (int) difference/1000; // Getting the seconds remaining
        String S = (s < 10) ? "0"+s : ""+s; // Formatting

        int ms = (int) (difference%1000) / 10; // Getting the milliseconds remaining and reducing to 2 decimal places
        ms = (ms<0)?0:ms; // Ensuring time always ends on 0
        String Ms = (ms < 10) ? "0"+ms : ""+ms; // Formatting

        textTimeRemaining.setText(S+"."+Ms+"s"); // Updating the text of the countdown timer to the time remaining

        if (s <= 0 && ms <= 0) {
            gameControlStatus = GameState.WAITING_EVOLVE; // Updating the game status when the timer reaches zero
            if (this.shouldSkipGens && (this.gensToSkip > 1)) {
                this.gameControlStatus = GameState.EVOLVING;
                this.generationCount++;
                doPopulationEvolution();
                this.gensToSkip--;
                this.gameControlStatus = GameState.PLAYING;
                playButtonClicked();
            }
        }
    }

    // Create the shapes that the agents will use
    void createAgentShapes() {
        this.agentShapes.clear();
        ArrayList<Circle> shapes = new ArrayList<>();

        for (int i=0; i<this.populationSize; i++) {
            Circle circle = new Circle(0,0, this.agentShapeRadius);
            circle.setVisible(false);
            shapes.add(circle);
            this.graphicsBox.getChildren().add(circle);
        }

        this.agentShapes = shapes;
    }

    // Draw all the agents to the screen
    void drawAgents() {
        int[][] agentPositions = this.population.getAgentPositions();

        for (int i=0; i<agentPositions.length; i++) {
            if (this.population.agents.get(i).isAlive()) {
                int[] position = agentPositions[i];
                this.agentShapes.get(i).setCenterX(position[0]);
                this.agentShapes.get(i).setCenterY(position[1]);
                this.agentShapes.get(i).setVisible(true);

                // If an agent is touching (within the radius of) the food then move the food
                int topWall = this.population.getFoodCoords()[1] - (this.foodWidth / 2);
                int bottomWall = this.population.getFoodCoords()[1] + (this.foodWidth / 2);
                int leftWall = this.population.getFoodCoords()[0] - (this.foodWidth / 2);
                int rightWall = this.population.getFoodCoords()[0] + (this.foodWidth / 2);

                // Collision check
                if ((position[0] > leftWall) && (position[0] < rightWall) && (position[1] > topWall) && (position[1] < bottomWall)) {
                    this.foodShouldMove = true;
                    this.population.agents.get(i).giveFoodEnergyBoost();
                    this.textStatus.setText("Agent #"+this.population.agents.get(i).getId()+" received an energy boost from food.");
                }
            } else {
                this.agentShapes.get(i).setVisible(false);
            }
        }
    }

    // Create the food shape
    void createFood() {
        int x = ThreadLocalRandom.current().nextInt((this.foodWidth),(701-this.foodWidth));
        int y = ThreadLocalRandom.current().nextInt((this.foodWidth),(521-this.foodWidth));
        this.food = new Rectangle(x, y, this.foodWidth, this.foodWidth);
        this.food.setVisible(false);
        this.food.setFill(Color.RED);

        this.graphicsBox.getChildren().add(this.food);
        this.foodShouldMove = false;

        this.population.setFoodCoords(x,y);
    }

    // Draw the food to the screen
    void drawFood() {
        // If the food should change location then give it a new location
        if (this.foodShouldMove) {
            int newX = ThreadLocalRandom.current().nextInt((this.foodWidth),(701-this.foodWidth));
            int newY = ThreadLocalRandom.current().nextInt((this.foodWidth),(521-this.foodWidth));
            this.food.setX(newX);
            this.food.setY(newY);
            this.population.setFoodCoords(newX, newY);
            foodShouldMove = false;
        }
        this.food.setVisible(true);
    }

    // update the text in the window
    void updateGameText() {
        this.informationGenerationNumber.setText(""+this.generationCount);
        this.informationPopulationCount.setText(""+this.population.getCurrentPopulationSize()+"/"+this.populationSize);

        this.fitnessCountBest.setText(""+this.population.getMaxScore());
        this.fitnessCountWorst.setText(""+this.population.getMinScore());
        this.fitnessCountMean.setText(""+(int)this.population.getMeanScore());

        this.geneticsBestId.setText(""+this.population.getMaxScoreId());
        this.geneticsBestK.setText(String.format("%.2f", this.population.getMaxScoreK()));
        this.geneticsBestMaxSpeed.setText(String.format("%.2f", this.population.getMaxScoreMaxSpeed()));
        this.geneticsBestMinSpeed.setText(String.format("%.2f", this.population.getMaxScoreMinSpeed()));
        this.geneticsBestDirectionVariation.setText(String.format("%.2f", this.population.getMaxScoreDirectionVariation()));

        this.geneticsWorstId.setText(""+this.population.getWorstScoreId());
        this.geneticsWorstK.setText(String.format("%.2f", this.population.getWorstScoreK()));
        this.geneticsWorstMaxSpeed.setText(String.format("%.2f",this.population.getWorstScoreMaxSpeed()));
        this.geneticsWorstMinSpeed.setText(String.format("%.2f", this.population.getWorstScoreMinSpeed()));
        this.geneticsWorstDirectionVariation.setText(String.format("%.2f", this.population.getWorstScoreDirectionVariation()));
    }

    // update the agents scores
    void refreshScores() {
        this.population.updateScores((this.playEndTime) - (this.lengthOfEachRound * 1000));
    }

    // Go through the evolution process
    void doPopulationEvolution() {
        this.population.doEvolution();
        // re draw the agents
        drawAgents();
    }
}
