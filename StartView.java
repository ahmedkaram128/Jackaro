package view;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javafx.animation.FadeTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class StartView {

    private TextField nameBox;
    private Button startGame;

    private Label welcomeMessage;
    private Label subtitle;
    private Label quote;

    public StartView() {
        // Name input box
        nameBox = new TextField();
        nameBox.setPromptText("Please enter your name");
        nameBox.setMinWidth(200);
        nameBox.setPrefWidth(200);
        nameBox.setMaxWidth(400);
        nameBox.setAlignment(Pos.CENTER);
        nameBox.setStyle("-fx-font-size: 14px; -fx-background-radius: 8px; -fx-padding: 6px;");

        // Start Game button
        startGame = new Button("🎮 Start Game");
        startGame.setStyle(
            "-fx-background-color: #28a745;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 18px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 10px;" +
            "-fx-padding: 10px 20px;"
        );
        startGame.setOnMouseEntered(e -> startGame.setStyle(
            "-fx-background-color: #1e7e34;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 18px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 10px;" +
            "-fx-padding: 10px 20px;"
        ));
        startGame.setOnMouseExited(e -> startGame.setStyle(
            "-fx-background-color: #28a745;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 18px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 10px;" +
            "-fx-padding: 10px 20px;"
        ));

        // Labels
        welcomeMessage = new Label("Roll In & Rule the Board!");
        welcomeMessage.setStyle("-fx-font-size: 90px; -fx-text-fill: gold; -fx-font-weight: bold;");
        welcomeMessage.setWrapText(true);
        welcomeMessage.setAlignment(Pos.CENTER);

        subtitle = new Label("Your cards. Your moves. Your win.");
        subtitle.setStyle("-fx-font-size: 24px; -fx-text-fill: #ffffff; -fx-font-style: italic;");
        subtitle.setWrapText(true);
        subtitle.setAlignment(Pos.CENTER);

        quote = new Label("\"It's not just luck... it's strategy.\"");
        quote.setStyle("-fx-text-fill: #dddddd; -fx-font-size: 16px; -fx-font-style: italic;");
        quote.setWrapText(true);
        quote.setAlignment(Pos.CENTER);

        // Fade in subtitle
        FadeTransition fade = new FadeTransition(Duration.seconds(2), subtitle);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.setCycleCount(1);
        fade.play();
    }

    public StackPane getView() {
        StackPane root = new StackPane();

        try {
            Image bgImage = new Image(new FileInputStream("res/view/view_start_background.png"));
            ImageView bgView = new ImageView(bgImage);
            bgView.setFitWidth(1950);
            bgView.setFitHeight(1400);
            bgView.setPreserveRatio(false);

            VBox content = new VBox(20);
            content.setAlignment(Pos.CENTER);
            content.getChildren().addAll(welcomeMessage, subtitle, quote, nameBox, startGame);

            root.getChildren().addAll(bgView, content);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return root;
    }

    public TextField getNameBox() {
        return nameBox;
    }

    public Button getStartGame() {
        return startGame;
    }
}
