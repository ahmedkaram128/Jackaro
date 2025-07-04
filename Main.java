package application;

import java.awt.Point;
import java.util.ArrayList;

import model.Colour;
import model.card.Card;
import model.card.standard.Seven;
import model.player.Marble;
import model.player.Player;
import engine.Game;
import engine.board.Cell;
import engine.board.SafeZone;
import exception.GameException;
import exception.InvalidCardException;
import exception.InvalidMarbleException;
import view.AssetLoader;
import view.GameView;
import view.StartView;
import javafx.scene.input.KeyCode;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeType;
import javafx.util.Duration;

public class Main extends Application {
	private Game game;
	private Player humanPlayer;
	private GameView gameView;
	private StartView startView;
	private ArrayList<Point> trackCords;
	
	@Override
	public void start(Stage primaryStage) {
		startView = new StartView();
		Scene startScreen = new Scene(startView.getView(), 1920, 1080);
		TextField name = startView.getNameBox();
		Button startGame = startView.getStartGame();
		
		trackCords = AssetLoader.loadTrack(12, 25);
		startGame.setOnAction(event -> {
			if(name.getText().equals("")){
				displayAlert("Name Error!!!", "Please enter a name!!!", "Try again");
				return;
			}

			try {
				game = new Game(name.getText());
				humanPlayer = game.getPlayers().get(0);
				gameView = new GameView(game);
				gameView.getName(0).setText(name.getText());
				gameView.setLabelColors(game.getPlayers().get(0).getColour(),
						game.getPlayers().get(1).getColour(), 
						game.getPlayers().get(2).getColour(), 
						game.getPlayers().get(3).getColour());
				
				StackPane root = gameView.getView();
				Scene gameScene = new Scene(root, 1920, 1200);
				updateBoard(game.getBoard().getTrack(), game.getBoard().getSafeZones());
				updateHomeZones(game.getPlayers());
				updateHands(game.getPlayers());
				updateTurns();
				
				gameView.getDeleteCard().setOnAction(e -> {
					if(game.getActivePlayerColour() == humanPlayer.getColour() && humanPlayer.getSelectedCard()!= null){
						game.endPlayerTurn();
						updateHands(game.getPlayers());
						updateFireZone();
						updateTurns();
						playCpuWithDelay(0,3, primaryStage, startScreen);
					}
					
				});
				
				gameView.getPlayButton().setOnAction(e -> {
					try {
						if(game.getActivePlayerColour() == humanPlayer.getColour() &&game.canPlayTurn()){
							int homeZoneSize = humanPlayer.getMarbles().size();
							
							game.playPlayerTurn();
							
							if(humanPlayer.getMarbles().size() > homeZoneSize){
								displayTrapAlert("Trap", "You fell in a trap cell");
							}
							updateBoard(game.getBoard().getTrack(), game.getBoard().getSafeZones());
							updateHomeZones(game.getPlayers());
							if(game.checkWin() == game.getActivePlayerColour()){
								displayWin("Game Over", game.getActivePlayerColour() + " wins", primaryStage, startScreen);
							}
							else {	
								game.endPlayerTurn();
								updateHands(game.getPlayers());
								updateFireZone();
								updateTurns();
								playCpuWithDelay(0, 3, primaryStage, startScreen);
							}
						}
						else if(game.getActivePlayerColour() == humanPlayer.getColour()){
							humanPlayer.deselectAll();
							game.endPlayerTurn();
							updateHands(game.getPlayers());
							updateFireZone();
							updateTurns();
							playCpuWithDelay(0, 3, primaryStage, startScreen);
						}
						
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						displayAlert("Invalid action", e1.getMessage(), "Try again");
					}
				});
				
				gameView.getDeselectAllButton().setOnAction(e1 -> {
					if(game.getActivePlayerColour() == humanPlayer.getColour()){
						humanPlayer.deselectAll();
						updateBoard(game.getBoard().getTrack(), game.getBoard().getSafeZones());
						updateHomeZones(game.getPlayers());
					}
				});
				
				gameScene.setOnKeyPressed(e1 -> {
				    if (e1.getCode() == KeyCode.W) {
				        try {
							game.fieldMarble();
							updateBoard(game.getBoard().getTrack(), game.getBoard().getSafeZones());
							updateHomeZones(game.getPlayers());
							updateHands(game.getPlayers());
							updateFireZone();
							updateTurns();
						} catch (Exception e2) {
							// TODO Auto-generated catch block
							e2.printStackTrace();
						}
				    }
				});
				
				gameScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
				primaryStage.setScene(gameScene);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		});	
		
		name.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                startGame.fire(); // works when entering {enter}
            }
        });		
		startScreen.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		primaryStage.setScene(startScreen);
		primaryStage.show();
	}
	
	
	public void updateBoard(ArrayList<Cell> track, ArrayList<SafeZone> safeZones){
		gameView.getBoard().getChildren().clear();
		updateTrack(track, 0, 100);
		
		for(int i = 0; i < 4; i++)
			updateSafeZone(safeZones.get(i), i);
		
		
	}
	
	private void updateTrack(ArrayList<Cell> track, int start, int stop){
		Point cords;
		Colour c;
		for(int i = start; i<stop; i++){
			Marble thisMarble = track.get(i).getMarble();
			cords = trackCords.get(i);
			if(thisMarble == null){
				c = null;
			}
			else{
				c = thisMarble.getColour();
			}
			
			Circle marbleView = gameView.addCell(c, cords.x, cords.y, 4);
			marbleView.setOnMouseClicked(e -> {
				try {
					humanPlayer.selectMarble(thisMarble);
					marbleView.setStroke(Color.BROWN);
					marbleView.setStrokeType(StrokeType.INSIDE);
				} catch (InvalidMarbleException exc) {
					displayAlert("Invalid Marble Selected", "stop", "try again");
				}
			});
		}
	}
	
	private void updateSafeZone(SafeZone safeZone, int safeZoneIndex){
		Point cords = getEntryCords(safeZoneIndex);
		int col = cords.x;
		int row = cords.y;
		int thisCol;
		int thisRow;
		Colour c;
		for(int i =0; i< 4; i++){
			Marble thisMarble = safeZone.getCells().get(i).getMarble();
			if(thisMarble == null){
				c = null;
			}
			else
				c = thisMarble.getColour();
			switch(safeZoneIndex){
			case 0: thisCol = col; thisRow =row - i - 1; break; 
			case 1: thisCol = col + i + 1; thisRow =row; break; 
			case 2: thisCol = col; thisRow =row + i + 1; break; 
			case 3: thisCol = col - 1- i; thisRow =row; break; 
			default: thisCol = col; thisRow = row; break;
			}
			
			Circle marbleView = gameView.addCell(c, thisCol, thisRow, 5+safeZoneIndex);
			marbleView.setOnMouseClicked(e -> {
				try {
					humanPlayer.selectMarble(thisMarble);
					marbleView.setStroke(Color.BROWN);
					marbleView.setStrokeType(StrokeType.INSIDE);
				} catch (InvalidMarbleException exc) {
					displayAlert("Invalid Marble Selected", "stop", "try again");
				}
			});
		}
	}
	
	public void updateHands(ArrayList<Player> players){
		ArrayList<Card> hand;
		Region test;
		HBox testH;
		VBox testV;
		ArrayList<Region> hands = gameView.getHands();
		for(int i = 0; i<4; i++){
			test = hands.get(i);
			if(test instanceof HBox){
				testH = (HBox)(test);
				testH.getChildren().clear();
			}
			else{
				testV = (VBox)(test);
				testV.getChildren().clear();
			}
			
			hand = players.get(i).getHand();
			for(int j = 0; j< hand.size(); j++){
				Card card = hand.get(j);
				if(card == null) return;
				StackPane r = gameView.addCard(card, i);
				r.getChildren().forEach(child -> child.setMouseTransparent(true));
				r.setOnMouseClicked(e -> {
					try {
						if(card instanceof Seven){
							displayEditSplitDistance("Split Distance", "Please enter the split distance: ");
						}
						else{
							
						}
						humanPlayer.selectCard(card);
						System.out.println("Selected " + card.getName());
					} catch (InvalidCardException e1) {
						displayAlert("invalid selection", "incorrect", "wait your turn");
					}
				});
			}
		}
	}
	
	public void updateHomeZones(ArrayList<Player> players){
		for(int i = 0; i<4; i++){
			updateHomeZone(players.get(i), i);
		}
	}
	
	private void updateHomeZone(Player player, int index){
		Colour c = null;
		ArrayList<Marble> homeZone;
		Marble thisMarble;
		homeZone = player.getMarbles();
		int len = homeZone.size();
		int i = 0;
		ArrayList<Point> cords = getHomeZoneCords(index);
		for(; i< len; i++){
			thisMarble = homeZone.get(i);
			if(thisMarble != null)
				c = thisMarble.getColour();
			int col = cords.get(i).x;
			int row = cords.get(i).y;
			gameView.addCell(c, col, row, index);	
		}
		for(; i<4; i++){
			int col = cords.get(i).x;
			int row = cords.get(i).y;
			gameView.addCell(null, col, row, index);
		}
	}
	
	private ArrayList<Point> getHomeZoneCords(int index){
		ArrayList<Point> cords = new ArrayList<>();
		switch(index){
			case 0:
				cords.add(new Point(17,21)); cords.add(new Point(18,21));
				cords.add(new Point(17,22)); cords.add(new Point(18,22));
				break;
			case 1:
				cords.add(new Point(5,16)); cords.add(new Point(5,17));
				cords.add(new Point(4,16)); cords.add(new Point(4,17));
				break;
			case 2:
				cords.add(new Point(11,5)); cords.add(new Point(10,5));
				cords.add(new Point(11,4)); cords.add(new Point(10,4));
				break;
			case 3:
				cords.add(new Point(23,10)); cords.add(new Point(23,9));
				cords.add(new Point(24,10)); cords.add(new Point(24,9));
				break;
		}
		return cords;
	}
	
	private void updateFireZone(){
		ArrayList<Card> firePit = game.getFirePit();
		if(firePit.size() ==0)
			return;
		Card card = firePit.get(firePit.size() - 1);
		if(card == null)
			return;
		gameView.addCard(card, 5);
		
	}
	
	private void updateTurns(){
		int index = getPlayerIndex(game.getActivePlayerColour());
		gameView.getTurns().setText("Current player: "+ gameView.getPlayerNames().get(index).getText() 
				+ ", Next player: " + gameView.getPlayerNames().get((index + 1)%4).getText());
	}
	
	public int getPlayerIndex(Colour colour){
		int i = 0;
		for(; i< 4; i++){
			if(game.getPlayers().get(i).getColour() == colour)
				return i;
		}
		return i; 
		
	}
	
	private Point getEntryCords(int safeZoneIndex){
		switch(safeZoneIndex){
			case 0: return new Point(14, 25);
			case 1: return new Point(1, 13);
			case 2: return new Point(14, 1);
			default: return new Point(27, 13);
		}
	}
	
	private void displayAlert(String title, String message, String exitMessage) {
	    Stage alertStage = new Stage();
	    alertStage.setTitle(title);
	    alertStage.setResizable(false);

	    Label label = new Label(message);
	    label.setWrapText(true);
	    label.setStyle(
	        "-fx-font-size: 18px;" +
	        "-fx-text-fill: #856404;" + // dark yellow-brown
	        "-fx-font-weight: bold;"
	    );
	    label.setAlignment(Pos.CENTER);

	    Button closeButton = new Button(exitMessage);
	    closeButton.setStyle(
	        "-fx-background-color: #ffc107;" + // amber
	        "-fx-text-fill: black;" +
	        "-fx-font-size: 14px;" +
	        "-fx-font-weight: bold;" +
	        "-fx-background-radius: 10px;" +
	        "-fx-padding: 8px 16px;"
	    );
	    closeButton.setOnMouseEntered(e -> closeButton.setStyle(
	        "-fx-background-color: #e0a800;" + // darker amber on hover
	        "-fx-text-fill: black;" +
	        "-fx-font-size: 14px;" +
	        "-fx-font-weight: bold;" +
	        "-fx-background-radius: 10px;" +
	        "-fx-padding: 8px 16px;"
	    ));
	    closeButton.setOnMouseExited(e -> closeButton.setStyle(
	        "-fx-background-color: #ffc107;" +
	        "-fx-text-fill: black;" +
	        "-fx-font-size: 14px;" +
	        "-fx-font-weight: bold;" +
	        "-fx-background-radius: 10px;" +
	        "-fx-padding: 8px 16px;"
	    ));

	    closeButton.setOnAction(event -> alertStage.close());

	    VBox content = new VBox(20, label, closeButton);
	    content.setAlignment(Pos.CENTER);
	    content.setStyle(
	        "-fx-background-color: #fff3cd;" + // light yellow
	        "-fx-border-color: #ffeeba;" +
	        "-fx-border-width: 3px;" +
	        "-fx-border-radius: 15px;" +
	        "-fx-background-radius: 15px;" +
	        "-fx-padding: 30px;"
	    );

	    Scene scene = new Scene(content, 450, 180);
	    alertStage.setScene(scene);
	    alertStage.show();
	}

	
	private void displayTrapAlert(String title, String message) {
	    Stage alertStage = new Stage();
	    alertStage.setTitle(title);
	    alertStage.setResizable(false);

	    Label label = new Label(message);
	    label.setWrapText(true);
	    label.setAlignment(Pos.CENTER);
	    label.setStyle(
	        "-fx-font-size: 20px;" +
	        "-fx-font-weight: bold;" +
	        "-fx-text-fill: #721c24;"
	    );

	    VBox content = new VBox(label);
	    content.setAlignment(Pos.CENTER);
	    content.setStyle(
	        "-fx-background-color: #f8d7da;" +  // light red/pink
	        "-fx-border-color: #f5c6cb;" +
	        "-fx-border-width: 3px;" +
	        "-fx-border-radius: 15px;" +
	        "-fx-background-radius: 15px;" +
	        "-fx-padding: 30px;"
	    );

	    Scene scene = new Scene(content, 450, 180);
	    alertStage.setScene(scene);
	    alertStage.show();

	    PauseTransition delay = new PauseTransition(Duration.seconds(2));
	    delay.setOnFinished(e -> alertStage.close());
	    delay.play();
	}

	
	private void displayEditSplitDistance(String title, String message) {
	    Stage alertStage = new Stage();
	    alertStage.setTitle(title);
	    alertStage.setResizable(false);

	    Label label = new Label(message);
	    label.setStyle(
	        "-fx-font-size: 18px;" +
	        "-fx-font-weight: bold;" +
	        "-fx-text-fill: #0c5460;"
	    );

	    TextField splitDistance = new TextField();
	    splitDistance.setPromptText("Enter split distance...");
	    splitDistance.setStyle(
	        "-fx-background-radius: 6px;" +
	        "-fx-padding: 6px;" +
	        "-fx-font-size: 14px;"
	    );

	    Button submitButton = new Button("Enter");
	    submitButton.setStyle(
	        "-fx-background-color: #17a2b8;" +  // blue-teal
	        "-fx-text-fill: white;" +
	        "-fx-font-weight: bold;" +
	        "-fx-background-radius: 10px;" +
	        "-fx-padding: 8px 16px;"
	    );
	    submitButton.setOnMouseEntered(e -> submitButton.setStyle(
	        "-fx-background-color: #117a8b;" +
	        "-fx-text-fill: white;" +
	        "-fx-font-weight: bold;" +
	        "-fx-background-radius: 10px;" +
	        "-fx-padding: 8px 16px;"
	    ));
	    submitButton.setOnMouseExited(e -> submitButton.setStyle(
	        "-fx-background-color: #17a2b8;" +
	        "-fx-text-fill: white;" +
	        "-fx-font-weight: bold;" +
	        "-fx-background-radius: 10px;" +
	        "-fx-padding: 8px 16px;"
	    ));

	    submitButton.setOnAction(event -> {
	        if (!splitDistance.getText().trim().isEmpty()) {
	            try {
	                game.editSplitDistance(Integer.parseInt(splitDistance.getText().trim()));
	                alertStage.close();
	            } catch (Exception e1) {
	                displayAlert("Split Distance Error", e1.getMessage(), "Try again");
	            }
	        }
	    });

	    VBox view = new VBox(15, label, splitDistance, submitButton);
	    view.setAlignment(Pos.CENTER);
	    view.setStyle(
	        "-fx-background-color: #d1ecf1;" +  // light blue
	        "-fx-border-color: #bee5eb;" +
	        "-fx-border-width: 3px;" +
	        "-fx-border-radius: 15px;" +
	        "-fx-background-radius: 15px;" +
	        "-fx-padding: 30px;"
	    );

	    Scene scene = new Scene(view, 480, 220);
	    alertStage.setScene(scene);
	    alertStage.show();
	}

	
	private void displayWin(String title, String message, Stage primaryStage, Scene startScreen) {
	    Stage alertStage = new Stage();
	    alertStage.setTitle(title);
	    alertStage.setResizable(false);

	    // Winner message label
	    Label label = new Label(message);
	    label.setStyle(
	        "-fx-font-size: 32px;" +
	        "-fx-font-weight: bold;" +
	        "-fx-text-fill: #28a745;" +
	        "-fx-alignment: center;"
	    );
	    label.setWrapText(true);
	    label.setAlignment(Pos.CENTER);

	    // "Play Again" button
	    Button tryAgainButton = new Button("🔁 Play Again");
	    tryAgainButton.setStyle(
	        "-fx-background-color: #0078d7;" +
	        "-fx-text-fill: white;" +
	        "-fx-font-size: 16px;" +
	        "-fx-font-weight: bold;" +
	        "-fx-padding: 10px 20px;" +
	        "-fx-background-radius: 8px;"
	    );
	    tryAgainButton.setOnMouseEntered(e -> tryAgainButton.setStyle(
	        "-fx-background-color: #005a9e;" +
	        "-fx-text-fill: white;" +
	        "-fx-font-size: 16px;" +
	        "-fx-font-weight: bold;" +
	        "-fx-padding: 10px 20px;" +
	        "-fx-background-radius: 8px;"
	    ));
	    tryAgainButton.setOnMouseExited(e -> tryAgainButton.setStyle(
	        "-fx-background-color: #0078d7;" +
	        "-fx-text-fill: white;" +
	        "-fx-font-size: 16px;" +
	        "-fx-font-weight: bold;" +
	        "-fx-padding: 10px 20px;" +
	        "-fx-background-radius: 8px;"
	    ));

	    tryAgainButton.setOnAction(event -> {
	        primaryStage.setScene(startScreen);
	        alertStage.close();
	    });

	    // Layout container
	    VBox contentBox = new VBox(30, label, tryAgainButton);
	    contentBox.setAlignment(Pos.CENTER);
	    contentBox.setStyle(
	        "-fx-background-color: #ffffff;" +
	        "-fx-border-color: #28a745;" +
	        "-fx-border-width: 3px;" +
	        "-fx-border-radius: 15px;" +
	        "-fx-background-radius: 15px;" +
	        "-fx-padding: 40px;"
	    );

	    Scene scene = new Scene(contentBox, 500, 250);
	    alertStage.setScene(scene);
	    alertStage.show();
	}

	
	private void playCpuWithDelay(int index, int max, Stage primaryStage, Scene startScreen){
		if(index >= max)
			return;
		
		PauseTransition pause = new PauseTransition(Duration.seconds(3.0));
        pause.setOnFinished(event -> {
        	Player thisPlayer = game.getPlayers().get(getPlayerIndex(game.getActivePlayerColour()));
        	int homeZoneSize = thisPlayer.getMarbles().size();			
        	try {
        		if(game.canPlayTurn()){
        			game.playPlayerTurn();
        		}
        		if(thisPlayer.getMarbles().size() > homeZoneSize){
        			displayTrapAlert("Trap", "CPU" + index +" fell in a trap");
        		}
			
			
        	
        	updateBoard(game.getBoard().getTrack(), game.getBoard().getSafeZones());
			updateHomeZones(game.getPlayers());
			if(game.checkWin() == game.getActivePlayerColour()){
				displayWin("Game Over", game.getActivePlayerColour() + " wins", primaryStage, startScreen);
			}
			else {
				game.endPlayerTurn();
				updateHands(game.getPlayers());
				updateFireZone();
				updateTurns();
				playCpuWithDelay(index + 1, max, primaryStage, startScreen);
			}
        	} catch (GameException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        });
        pause.play();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
}
