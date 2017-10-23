import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class Controller {
    private int lengthOfEachRound = 10; // How many seconds in a round

    // Play/Pause/Evolve Buttons
    @FXML
    Button controlButtonPlay;
    @FXML
    Button controlButtonEvolve;// Skip-to generation buttons
    @FXML
    Button buttonSkipGeneration1x;
    @FXML
    Button buttonSkipGeneration5x;
    @FXML
    Button buttonSkipGeneration10x;
    // Button overall view
    private String[] buttons = {"controlButtonPlay", "controlButtonEvolve", "buttonSkipGeneration1x", "buttonSkipGeneration5x", "buttonSkipGeneration10x"};

    // Status
    private String[] gameStatusNameOptions = {"playing", "evolving", "skippingGens", "waitingForEvolve", "waitingForPlay"};
    private int gameControlStatus;

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

    @FXML
    private void initialize() {
        this.gameControlStatus = 4; // Set the initial game state to "Waiting for user to press play"

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

        if (gameControlStatus == 0) updateTimer(); // If game state is "playing", update the countdown timer
    }

    // Update the status of the buttons in the game
    // Some can/cannot be clicked depending on what stage the game is in
    private void updateGameButtons() {
        switch (gameControlStatus) {
            case 0: {setButtonDisabled(true,true,false,false,false);break;} // playing
            case 1: {setButtonDisabled(true,true,true,true,true);break;} // evolving
            case 2: {setButtonDisabled(true,true,true,true,true);break;} // skipping generations
            case 3: {setButtonDisabled(true,false,false,false,false);break;} // waiting to evolve
            case 4: {setButtonDisabled(false,true,false,false,false);break;} // waiting to play
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
                case "controlButtonPlay": {gameControlStatus = 0;playButtonClicked();break;}
                case "controlButtonEvolve": {gameControlStatus=1;break;}
                case "buttonSkipGeneration1x": {gameControlStatus=2;break;}
                case "buttonSkipGeneration5x": {gameControlStatus=2;break;}
                case "buttonSkipGeneration10x": {gameControlStatus=2;break;}
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

        if (s <= 0 && ms <= 0) gameControlStatus = 3; // Updating the game status when the timer reaches zero
    }
}
