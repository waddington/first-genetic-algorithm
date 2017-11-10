import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.HashSet;

public class Controller {
    int fullPopulationSize = 60;
    Population population;
    double mutationRate = 0.01;

    int agentMaximumSteps = 2000;
    int movesPerSecond = 50;

    int boardWidth = 700;
    int boardHeight = 500;
    int tileSize = 10;

    ArrayList<Wall> walls;
    HashSet<String> wallCoordinates;

    int[] nestCoordintates = new int[] {5, 5};
    int[] targetCoordinates = new int[] {40, 43};

    boolean roundEnded;
    boolean allScoresCalculated;

    AStar astar;

    ScoreTracker scoreTracker;

    // Information boxes
    @FXML Text fitnessCountBest;
    @FXML Text fitnessCountWorst;
    @FXML Text fitnessCountMean;

    @FXML Text informationGenerationNumber;
    @FXML Text informationPopulationCount;

    // Graphics box
    @FXML
    Pane graphicsBox;

    @FXML
    void initialize() {
        this.population = new Population(this.fullPopulationSize, this.tileSize, this.agentMaximumSteps, this.nestCoordintates, this.mutationRate);
        this.walls = new ArrayList<>();
        this.wallCoordinates = new HashSet<>();
        this.roundEnded = false;
        this.allScoresCalculated = false;

        this.scoreTracker = new ScoreTracker(this.fullPopulationSize, this.mutationRate);

        createWalls();
        addWallsToPane();

        createAndRenderNest();
        createAndRenderTarget();

        this.astar = new AStar(this.wallCoordinates, this.targetCoordinates, this.boardWidth, this.boardHeight, this.tileSize);

        addAgentsToPane();

        theLoopWrapper();
    }

    @FXML
    void gameButtonClicked(MouseEvent event) {
        // We only want to listen to primary button clicks
        if (event.getButton() == MouseButton.PRIMARY) {}
    }

    // My loop timer function
    // https://stackoverflow.com/a/13060022/3259361
    void theLoopWrapper() {
        final Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, actionEvent -> {
                    theLoop(); // My function for all the code that should be looped over - it's the last function in this file
                }),
                new KeyFrame(Duration.millis(1000/this.movesPerSecond))
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    void addAgentsToPane() {
        Agent[] agents = this.population.getAgents();

        for (Agent agent: agents) {
            this.graphicsBox.getChildren().add(agent.getShape());
        }
    }

    int[] coordinatesToPixel(int[] coordinates) {
        int[] pixelPosition = new int[] {coordinates[0], coordinates[1]};

        pixelPosition[0] *= this.tileSize;
        pixelPosition[1] *= this.tileSize;

        return pixelPosition;
    }

    void createWalls() {
        this.wallCoordinates.add("0-"+"15");
        this.wallCoordinates.add("0-"+"16");
        this.wallCoordinates.add("1-"+"15");
        this.wallCoordinates.add("1-"+"16");
        this.wallCoordinates.add("2-"+"15");
        this.wallCoordinates.add("2-"+"16");
        this.wallCoordinates.add("3-"+"15");
        this.wallCoordinates.add("3-"+"16");
        this.wallCoordinates.add("4-"+"15");
        this.wallCoordinates.add("4-"+"16");
        this.wallCoordinates.add("5-"+"15");
        this.wallCoordinates.add("5-"+"16");
        this.wallCoordinates.add("6-"+"15");
        this.wallCoordinates.add("6-"+"16");
        this.wallCoordinates.add("7-"+"15");
        this.wallCoordinates.add("7-"+"16");
        this.wallCoordinates.add("8-"+"15");
        this.wallCoordinates.add("8-"+"16");
        this.wallCoordinates.add("9-"+"15");
        this.wallCoordinates.add("9-"+"16");
        this.wallCoordinates.add("10-"+"15");
        this.wallCoordinates.add("10-"+"16");
        this.wallCoordinates.add("11-"+"15");
        this.wallCoordinates.add("11-"+"16");
        this.wallCoordinates.add("12-"+"15");
        this.wallCoordinates.add("12-"+"16");
        this.wallCoordinates.add("13-"+"15");
        this.wallCoordinates.add("13-"+"16");
        this.wallCoordinates.add("14-"+"15");
        this.wallCoordinates.add("14-"+"16");
        this.wallCoordinates.add("15-"+"15");
        this.wallCoordinates.add("15-"+"16");
        this.wallCoordinates.add("16-"+"15");
        this.wallCoordinates.add("16-"+"16");
        this.wallCoordinates.add("17-"+"15");
        this.wallCoordinates.add("17-"+"16");
        this.wallCoordinates.add("18-"+"15");
        this.wallCoordinates.add("18-"+"16");
        this.wallCoordinates.add("19-"+"15");
        this.wallCoordinates.add("19-"+"16");
        this.wallCoordinates.add("20-"+"15");
        this.wallCoordinates.add("20-"+"16");
        this.wallCoordinates.add("21-"+"15");
        this.wallCoordinates.add("21-"+"16");
        this.wallCoordinates.add("22-"+"15");
        this.wallCoordinates.add("22-"+"16");
        this.wallCoordinates.add("23-"+"15");
        this.wallCoordinates.add("23-"+"16");
        this.wallCoordinates.add("24-"+"15");
        this.wallCoordinates.add("24-"+"16");
        this.wallCoordinates.add("25-"+"15");
        this.wallCoordinates.add("25-"+"16");

        this.wallCoordinates.add("24-"+"14");
        this.wallCoordinates.add("25-"+"14");
        this.wallCoordinates.add("24-"+"13");
        this.wallCoordinates.add("25-"+"13");
        this.wallCoordinates.add("24-"+"12");
        this.wallCoordinates.add("25-"+"12");
        this.wallCoordinates.add("24-"+"11");
        this.wallCoordinates.add("25-"+"11");
        this.wallCoordinates.add("24-"+"10");
        this.wallCoordinates.add("25-"+"10");
        this.wallCoordinates.add("24-"+"9");
        this.wallCoordinates.add("25-"+"9");

        this.wallCoordinates.add("40-"+"0");
        this.wallCoordinates.add("41-"+"0");
        this.wallCoordinates.add("40-"+"1");
        this.wallCoordinates.add("41-"+"1");
        this.wallCoordinates.add("40-"+"2");
        this.wallCoordinates.add("41-"+"2");
        this.wallCoordinates.add("40-"+"3");
        this.wallCoordinates.add("41-"+"3");
        this.wallCoordinates.add("40-"+"4");
        this.wallCoordinates.add("41-"+"4");
        this.wallCoordinates.add("40-"+"5");
        this.wallCoordinates.add("41-"+"5");
        this.wallCoordinates.add("40-"+"6");
        this.wallCoordinates.add("41-"+"6");
        this.wallCoordinates.add("40-"+"7");
        this.wallCoordinates.add("41-"+"7");
        this.wallCoordinates.add("40-"+"8");
        this.wallCoordinates.add("41-"+"8");
        this.wallCoordinates.add("40-"+"9");
        this.wallCoordinates.add("41-"+"9");
        this.wallCoordinates.add("40-"+"10");
        this.wallCoordinates.add("41-"+"10");
        this.wallCoordinates.add("40-"+"11");
        this.wallCoordinates.add("41-"+"11");
        this.wallCoordinates.add("40-"+"12");
        this.wallCoordinates.add("41-"+"12");
        this.wallCoordinates.add("40-"+"13");
        this.wallCoordinates.add("41-"+"13");
        this.wallCoordinates.add("40-"+"14");
        this.wallCoordinates.add("41-"+"14");
        this.wallCoordinates.add("40-"+"15");
        this.wallCoordinates.add("41-"+"15");
        this.wallCoordinates.add("40-"+"16");
        this.wallCoordinates.add("41-"+"16");
        this.wallCoordinates.add("40-"+"17");
        this.wallCoordinates.add("41-"+"17");
        this.wallCoordinates.add("40-"+"18");
        this.wallCoordinates.add("41-"+"18");
        this.wallCoordinates.add("40-"+"19");
        this.wallCoordinates.add("41-"+"19");
        this.wallCoordinates.add("40-"+"20");
        this.wallCoordinates.add("41-"+"20");
        this.wallCoordinates.add("40-"+"21");
        this.wallCoordinates.add("41-"+"21");
        this.wallCoordinates.add("40-"+"22");
        this.wallCoordinates.add("41-"+"22");
        this.wallCoordinates.add("40-"+"23");
        this.wallCoordinates.add("41-"+"23");
        this.wallCoordinates.add("40-"+"24");
        this.wallCoordinates.add("41-"+"24");
        this.wallCoordinates.add("40-"+"25");
        this.wallCoordinates.add("41-"+"25");
        this.wallCoordinates.add("40-"+"26");
        this.wallCoordinates.add("41-"+"26");

        this.wallCoordinates.add("39-"+"25");
        this.wallCoordinates.add("39-"+"26");
        this.wallCoordinates.add("38-"+"25");
        this.wallCoordinates.add("38-"+"26");
        this.wallCoordinates.add("37-"+"25");
        this.wallCoordinates.add("37-"+"26");
        this.wallCoordinates.add("36-"+"25");
        this.wallCoordinates.add("36-"+"26");
        this.wallCoordinates.add("35-"+"25");
        this.wallCoordinates.add("35-"+"26");
        this.wallCoordinates.add("34-"+"25");
        this.wallCoordinates.add("34-"+"26");
        this.wallCoordinates.add("33-"+"25");
        this.wallCoordinates.add("33-"+"26");
        this.wallCoordinates.add("32-"+"25");
        this.wallCoordinates.add("32-"+"26");
        this.wallCoordinates.add("31-"+"25");
        this.wallCoordinates.add("31-"+"26");
        this.wallCoordinates.add("30-"+"25");
        this.wallCoordinates.add("30-"+"26");
        this.wallCoordinates.add("29-"+"25");
        this.wallCoordinates.add("29-"+"26");
        this.wallCoordinates.add("28-"+"25");
        this.wallCoordinates.add("28-"+"26");
        this.wallCoordinates.add("27-"+"25");
        this.wallCoordinates.add("27-"+"26");
        this.wallCoordinates.add("26-"+"25");
        this.wallCoordinates.add("26-"+"26");
        this.wallCoordinates.add("25-"+"25");
        this.wallCoordinates.add("25-"+"26");
        this.wallCoordinates.add("24-"+"25");
        this.wallCoordinates.add("24-"+"26");
        this.wallCoordinates.add("23-"+"25");
        this.wallCoordinates.add("23-"+"26");
        this.wallCoordinates.add("22-"+"25");
        this.wallCoordinates.add("22-"+"26");
        this.wallCoordinates.add("21-"+"25");
        this.wallCoordinates.add("21-"+"26");
        this.wallCoordinates.add("20-"+"25");
        this.wallCoordinates.add("20-"+"26");

        this.wallCoordinates.add("34-"+"49");
        this.wallCoordinates.add("35-"+"49");
        this.wallCoordinates.add("34-"+"48");
        this.wallCoordinates.add("35-"+"48");
        this.wallCoordinates.add("34-"+"47");
        this.wallCoordinates.add("35-"+"47");
        this.wallCoordinates.add("34-"+"46");
        this.wallCoordinates.add("35-"+"46");
        this.wallCoordinates.add("34-"+"45");
        this.wallCoordinates.add("35-"+"45");
        this.wallCoordinates.add("34-"+"44");
        this.wallCoordinates.add("35-"+"44");
        this.wallCoordinates.add("34-"+"43");
        this.wallCoordinates.add("35-"+"43");
        this.wallCoordinates.add("34-"+"42");
        this.wallCoordinates.add("35-"+"42");
        this.wallCoordinates.add("34-"+"41");
        this.wallCoordinates.add("35-"+"41");
        this.wallCoordinates.add("34-"+"40");
        this.wallCoordinates.add("35-"+"40");
        this.wallCoordinates.add("34-"+"39");
        this.wallCoordinates.add("35-"+"39");
        this.wallCoordinates.add("34-"+"38");
        this.wallCoordinates.add("35-"+"38");
        this.wallCoordinates.add("34-"+"37");
        this.wallCoordinates.add("35-"+"37");
        this.wallCoordinates.add("34-"+"36");
        this.wallCoordinates.add("35-"+"36");

        this.wallCoordinates.add("36-"+"36");
        this.wallCoordinates.add("36-"+"37");
        this.wallCoordinates.add("37-"+"36");
        this.wallCoordinates.add("37-"+"37");
        this.wallCoordinates.add("38-"+"36");
        this.wallCoordinates.add("38-"+"37");
        this.wallCoordinates.add("39-"+"36");
        this.wallCoordinates.add("39-"+"37");
        this.wallCoordinates.add("40-"+"36");
        this.wallCoordinates.add("40-"+"37");
        this.wallCoordinates.add("41-"+"36");
        this.wallCoordinates.add("41-"+"37");
        this.wallCoordinates.add("42-"+"36");
        this.wallCoordinates.add("42-"+"37");
        this.wallCoordinates.add("43-"+"36");
        this.wallCoordinates.add("43-"+"37");
        this.wallCoordinates.add("44-"+"36");
        this.wallCoordinates.add("44-"+"37");
        this.wallCoordinates.add("45-"+"36");
        this.wallCoordinates.add("45-"+"37");
        this.wallCoordinates.add("46-"+"36");
        this.wallCoordinates.add("46-"+"37");
        this.wallCoordinates.add("47-"+"36");
        this.wallCoordinates.add("47-"+"37");
        this.wallCoordinates.add("48-"+"36");
        this.wallCoordinates.add("48-"+"37");
        this.wallCoordinates.add("49-"+"36");
        this.wallCoordinates.add("49-"+"37");
        this.wallCoordinates.add("50-"+"36");
        this.wallCoordinates.add("50-"+"37");
        this.wallCoordinates.add("51-"+"36");
        this.wallCoordinates.add("51-"+"37");
        this.wallCoordinates.add("52-"+"36");
        this.wallCoordinates.add("52-"+"37");
        this.wallCoordinates.add("53-"+"36");
        this.wallCoordinates.add("53-"+"37");
        this.wallCoordinates.add("54-"+"36");
        this.wallCoordinates.add("54-"+"37");

        this.wallCoordinates.add("53-"+"35");
        this.wallCoordinates.add("54-"+"35");
        this.wallCoordinates.add("53-"+"34");
        this.wallCoordinates.add("54-"+"34");
        this.wallCoordinates.add("53-"+"33");
        this.wallCoordinates.add("54-"+"33");
        this.wallCoordinates.add("53-"+"32");
        this.wallCoordinates.add("54-"+"32");
        this.wallCoordinates.add("53-"+"31");
        this.wallCoordinates.add("54-"+"31");
        this.wallCoordinates.add("53-"+"30");
        this.wallCoordinates.add("54-"+"30");
        this.wallCoordinates.add("53-"+"29");
        this.wallCoordinates.add("54-"+"29");
        this.wallCoordinates.add("53-"+"28");
        this.wallCoordinates.add("54-"+"28");
        this.wallCoordinates.add("53-"+"27");
        this.wallCoordinates.add("54-"+"27");
        this.wallCoordinates.add("53-"+"26");
        this.wallCoordinates.add("54-"+"26");
        this.wallCoordinates.add("53-"+"25");
        this.wallCoordinates.add("54-"+"25");
        this.wallCoordinates.add("53-"+"24");
        this.wallCoordinates.add("54-"+"24");
        this.wallCoordinates.add("53-"+"23");
        this.wallCoordinates.add("54-"+"23");

        for (String coord: this.wallCoordinates) {
            int[] coords = new int[2];

            String[] coordSplit = coord.split("-");

            int coordX = Integer.parseInt(coordSplit[0]);
            int coordY = Integer.parseInt(coordSplit[1]);

            int[] wallPosition = coordinatesToPixel(new int[] {coordX, coordY});
            Rectangle wallShape = new Rectangle(wallPosition[0], wallPosition[1], this.tileSize, this.tileSize);

            Wall wall = new Wall();
            wall.setWallPosition(wallPosition);
            wall.setWallShape(wallShape);

            this.walls.add(wall);
        }
    }

    void addWallsToPane() {
        for (Wall wall: this.walls) {
            this.graphicsBox.getChildren().add(wall.getWallShape());
        }
    }

    void createAndRenderNest() {
        int[] nestPixelPosition = coordinatesToPixel(this.nestCoordintates);
        Rectangle nestShape = new Rectangle(nestPixelPosition[0], nestPixelPosition[1], this.tileSize, this.tileSize);
        nestShape.setFill(Color.GREEN);

        this.graphicsBox.getChildren().add(nestShape);
    }

    void createAndRenderTarget() {
        int[] targetPixelPosition = coordinatesToPixel(this.targetCoordinates);
        Rectangle targetShape = new Rectangle(targetPixelPosition[0], targetPixelPosition[1], this.tileSize, this.tileSize);
        targetShape.setFill(Color.RED);

        this.graphicsBox.getChildren().add(targetShape);
    }

    void checkRoundEnded() {
        Agent[] agents = this.population.getAgents();

        boolean agentsAlive = false;

        for (Agent agent: agents) {
            if (agent.isAlive())
                agentsAlive = true;
        }

        if (!agentsAlive)
            this.roundEnded = true;
    }

    void showPopulationInformation() {
        this.population.countPopulation();
        this.informationGenerationNumber.setText(""+this.population.currentGeneration);
        this.informationPopulationCount.setText(""+this.population.currentPopulationSize+" / "+this.population.fullPopulationSize);
    }

    void updateAgentPositions() {
        Agent[] agents = this.population.getAgents();

        for (Agent agent: agents) {
            if (agent.isAlive()) {
                agent.doNextMove();
            }
        }
    }

    void renderAgents() {
        Agent[] agents = this.population.getAgents();

        for (Agent agent: agents) {
            if (agent.isAlive()) {
                int[] agentCoordinates = agent.getCurrentCoordinates();

                agent.setAlive(agentInBounds(agentCoordinates));

                int[] agentPixelPosition = coordinatesToPixel(agentCoordinates);
                agent.getShape().setX(agentPixelPosition[0]);
                agent.getShape().setY(agentPixelPosition[1]);
            }
        }
    }

    boolean agentInBounds(int[] agentCoordinates) {
        // Agent is still in box
        if (agentCoordinates[0] < 0 || agentCoordinates[0] > 69 || agentCoordinates[1] < 0 || agentCoordinates[1] > 49)
            return false;

        // If agent hit a wall
        String coordsInWallFormat = agentCoordinates[0]+"-"+agentCoordinates[1];
        if (this.wallCoordinates.contains(coordsInWallFormat))
            return false;

        return true;
    }

    void updateAgentScores() {
        if (this.roundEnded) {
            Agent[] agents = this.population.getAgents();

            for (Agent agent: agents) {
                if (!agent.isGotScore()) {
                    int score = calculateAStarDistance(agent);
                    // TODO: somehow add how many moves they took
                    score = this.agentMaximumSteps - score;
                    agent.setScore(score);
                    agent.setGotScore(true);
                }
            }
            displayScores();
        }

        this.allScoresCalculated = true;
    }

    int calculateAStarDistance(Agent agent) {
        return this.astar.getScore(agent);
    }

    void displayScores() {
        Agent[] agents = this.population.getAgents();
        double totalScores = 0;
        int maxScore = Integer.MIN_VALUE;
        int minScore = Integer.MAX_VALUE;

        for (Agent agent: agents) {
            int score = agent.getScore();
            totalScores += score;

            if (score > maxScore)
                maxScore = score;
            if (score < minScore)
                minScore = score;
        }

        double meanScore = totalScores / agents.length;

        // Display the scores
        this.fitnessCountBest.setText(""+maxScore);
        this.fitnessCountWorst.setText(""+minScore);
        this.fitnessCountMean.setText(""+meanScore);
    }

    void saveScores() {
        Agent[] agents = this.population.getAgents();
        double totalScores = 0;
        int maxScore = Integer.MIN_VALUE;
        int minScore = Integer.MAX_VALUE;

        for (Agent agent: agents) {
            int score = agent.getScore();
            totalScores += score;

            if (score > maxScore)
                maxScore = score;
            if (score < minScore)
                minScore = score;
        }

        double meanScore = totalScores / agents.length;

        ScoreTracker.addScore(maxScore, minScore, meanScore);
    }

    void doEvolution() {
        if (this.roundEnded && this.allScoresCalculated) {
            saveScores();
            this.population.doEvolution();

        }
        resetAllFlags();
    }

    void resetAllFlags() {
        this.roundEnded = false;
        this.allScoresCalculated = false;
    }

    void theLoop() {
        checkRoundEnded();
        showPopulationInformation();
        updateAgentPositions();
        updateAgentScores();
        renderAgents();

        doEvolution();
    }
}
