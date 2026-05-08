import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.util.Random;

public class RPS extends Application {
    private int wins = 0, losses = 0, ties = 0, roundsPlayed = 0;
    private int rounds = 5;
    private boolean bestOf = false;
    private Label scoreLabel;
    private Label resultMessage;
    private Label vsLabel;
    private ImageView playerView = new ImageView();
    private ImageView computerView = new ImageView();
    private Random rand = new Random();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        resultMessage = new Label("READY?");
        resultMessage.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #333;");

        playerView.setFitHeight(150);
        playerView.setPreserveRatio(true);
        computerView.setFitHeight(150);
        computerView.setPreserveRatio(true);

        vsLabel = new Label("Ready to play?");
        vsLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #aaaaaa;");

        HBox battleArena = new HBox(40, playerView, vsLabel, computerView);
        battleArena.setAlignment(Pos.CENTER);
        battleArena.setPrefHeight(220);
        battleArena.setStyle("-fx-background-color: #2b2b2b; -fx-background-radius: 15;");
        battleArena.setPadding(new Insets(20));

        scoreLabel = new Label("Score - Wins: 0 | Losses: 0 | Ties: 0");
        scoreLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 20px;");

        TextField roundsInput = new TextField();
        roundsInput.setPromptText("Rounds");
        roundsInput.setMaxWidth(80);

        CheckBox bestOfCheck = new CheckBox("Best Of");
        Button startBtn = new Button("Start New Game");
        startBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");

        Button rockBtn = new Button("🪨 ROCK");
        Button paperBtn = new Button("📄 PAPER");
        Button scissorsBtn = new Button("✂️ SCISSORS");
        
        String btnStyle = "-fx-font-size: 18px; -fx-font-weight: bold; -fx-padding: 10 20; -fx-cursor: hand;";
        rockBtn.setStyle(btnStyle);
        paperBtn.setStyle(btnStyle);
        scissorsBtn.setStyle(btnStyle);

        setButtonsDisabled(true, rockBtn, paperBtn, scissorsBtn);

        HBox setupBox = new HBox(15, new Label("Rounds:"), roundsInput, bestOfCheck, startBtn);
        setupBox.setAlignment(Pos.CENTER);

        HBox gameBox = new HBox(20, rockBtn, paperBtn, scissorsBtn);
        gameBox.setAlignment(Pos.CENTER);

        VBox root = new VBox(20, setupBox, scoreLabel, resultMessage, battleArena, gameBox);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #f0f0f0;");

        startBtn.setOnAction(e -> {
            try {
                int inputRounds = Integer.parseInt(roundsInput.getText());
                if (inputRounds <= 0) {
                    vsLabel.setText("Why'd you start the game then.");
                    setButtonsDisabled(true, rockBtn, paperBtn, scissorsBtn);
                    return;
                }
                rounds = inputRounds;
                bestOf = bestOfCheck.isSelected();
                wins = losses = ties = roundsPlayed = 0;
                updateScoreLabel();
                resultMessage.setText("GO!");
                resultMessage.setTextFill(Color.BLACK);
                vsLabel.setText("VS");
                vsLabel.setStyle("-fx-font-size: 40px; -fx-font-weight: bold; -fx-text-fill: #777;");
                playerView.setImage(null);
                computerView.setImage(null);
                setButtonsDisabled(false, rockBtn, paperBtn, scissorsBtn);
            } catch (NumberFormatException ex) {
                vsLabel.setText("Enter a number!");
            }
        });

        rockBtn.setOnAction(e -> playRound("rock", rockBtn, paperBtn, scissorsBtn));
        paperBtn.setOnAction(e -> playRound("paper", rockBtn, paperBtn, scissorsBtn));
        scissorsBtn.setOnAction(e -> playRound("scissors", rockBtn, paperBtn, scissorsBtn));

        stage.setScene(new Scene(root, 750, 650));
        stage.setTitle("Professional RPS");
        stage.show();
    }

    private void playRound(String playerChoice, Button... buttons) {
        String[] choices = {"rock", "paper", "scissors"};
        String computerChoice = choices[rand.nextInt(3)];

        playerView.setImage(getImageFor(playerChoice));
        computerView.setImage(getImageFor(computerChoice));

        if (playerChoice.equals(computerChoice)) {
            resultMessage.setText("TIE!");
            resultMessage.setTextFill(Color.DARKGOLDENROD);
            ties++;
        } else {
            if ((playerChoice.equals("rock") && computerChoice.equals("scissors")) ||
                (playerChoice.equals("paper") && computerChoice.equals("rock")) ||
                (playerChoice.equals("scissors") && computerChoice.equals("paper"))) {
                resultMessage.setText("YOU WIN!");
                resultMessage.setTextFill(Color.GREEN);
                wins++;
            } else {
                resultMessage.setText("YOU LOSE.");
                resultMessage.setTextFill(Color.RED);
                losses++;
            }
            roundsPlayed++;
        }

        updateScoreLabel();

        boolean bestOfReached = bestOf && (wins > (double)rounds / 2 || losses > (double)rounds / 2);
        if (bestOfReached || roundsPlayed >= rounds) {
            endGame(buttons);
        }
    }

    private Image getImageFor(String choice) {
        switch (choice) {
            case "rock":
                if (rand.nextInt(100) < 5) {
                    return new Image("https://i.redd.it/the-rock-eyebrow-raise-meme-lgbt-rainbow-pride-flag-v0-g0cdxdf7uwyf1.png?width=4292&format=png&auto=webp&s=463d6949bce60e84b1238b910e2a3cc65c6652ad");
                } else {
                    return new Image("https://png.pngtree.com/png-clipart/20250704/original/pngtree-big-rock-stone-isolated-on-transparent-background-png-image_21201528.png");
                }
            case "paper":
                return new Image("https://static.vecteezy.com/system/resources/thumbnails/022/219/336/small/white-torn-paper-isolated-on-a-transparent-background-png.png");
            case "scissors":
                return new Image("https://img.pikbest.com/png-images/20241023/scissors-png-isolated-on-transparent-background-high-quality_10995751.png!bw700");
            default:
                return null;
        }
    }

    private void updateScoreLabel() {
        scoreLabel.setText(String.format("Wins: %d | Losses: %d | Ties: %d", wins, losses, ties));
    }

    private void setButtonsDisabled(boolean disabled, Button... buttons) {
        for (Button b : buttons) b.setDisable(disabled);
    }

    private void endGame(Button... buttons) {
        setButtonsDisabled(true, buttons);
        if (wins > losses) {
            resultMessage.setText("GAME OVER: YOU WIN!");
            resultMessage.setTextFill(Color.BLUE);
        } else if (losses > wins) {
            resultMessage.setText("YOU LOSE WOMP WOMP");
            resultMessage.setTextFill(Color.RED);
        } else {
            resultMessage.setText("GAME OVER: DRAW");
        }
    }
}
