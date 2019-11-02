package project;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.event.*;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.CornerRadii;
import javafx.geometry.Insets;
import javafx.scene.paint.Color;

public class UIController {
    @FXML
    private Button buttonNextFrame;

    @FXML
    private Button buttonSimulate;

    @FXML
    private Button buttonPlay;

    @FXML
    private AnchorPane paneArena;

    @FXML
    private Label labelBasicTower;

    @FXML
    private Label labelIceTower;

    @FXML
    private Label labelCatapult;

    @FXML
    private Label labelLaserTower;

    static final int ARENA_WIDTH = 480;
    static final int ARENA_HEIGHT = 480;
    static final int GRID_WIDTH = 40;
    static final int GRID_HEIGHT = 40;
    static final int MAX_H_NUM_GRID = ARENA_WIDTH / GRID_WIDTH;
    static final int MAX_V_NUM_GRID = ARENA_HEIGHT / GRID_HEIGHT;

    
    private Arena arena = new Arena();
    private Label grids[][] = new Label[MAX_V_NUM_GRID][MAX_H_NUM_GRID]; //the grids on arena. grids[y][x]
    private int x = -1, y = 0; //where is my monster
    /**
     * A dummy function to show how button click works
     */
    @FXML
    private void play() {
   	 Label newLabel = new Label();
   	 newLabel.setLayoutX(GRID_WIDTH * 3 / 4);
   	 newLabel.setLayoutY(GRID_WIDTH * 3 / 4);
   	 newLabel.setMinWidth(GRID_WIDTH / 5);
   	 newLabel.setMaxWidth(GRID_WIDTH / 5);
   	 newLabel.setMinHeight(GRID_WIDTH / 3);
   	 newLabel.setMaxHeight(GRID_WIDTH / 3);
   	 newLabel.setStyle("-fx-border-color: black;");
   	 newLabel.setText("*");
   	 newLabel.setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));
   	 paneArena.getChildren().addAll(newLabel);
    }

    /**
     * A function that create the Arena
     */
    @FXML
    public void createArena() {
        if (grids[0][0] != null)
            return; //created already
        for (int i = 0; i < MAX_V_NUM_GRID; i++)
            for (int j = 0; j < MAX_H_NUM_GRID; j++) {
                Label newLabel = new Label();
                if (j == MAX_H_NUM_GRID - 1 && i == 0) {
                    Image image1 = new Image("/end-zone.png", GRID_WIDTH, GRID_HEIGHT, true, true);
                    BackgroundImage backgroundImage1= new BackgroundImage(image1, BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
                    newLabel.setBackground(new Background(backgroundImage1));
                } else if (j == 0 && i == 0) {
                	Image image1 = new Image("/show-up.png", GRID_WIDTH, GRID_HEIGHT, true, true);
                    BackgroundImage backgroundImage1= new BackgroundImage(image1, BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
                    newLabel.setBackground(new Background(backgroundImage1));
                } else if (j % 2 == 0 || i == ((j + 1) / 2 % 2) * (MAX_V_NUM_GRID - 1))
                    newLabel.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
                else
                    newLabel.setBackground(new Background(new BackgroundFill(Color.GREEN, CornerRadii.EMPTY, Insets.EMPTY)));
                newLabel.setLayoutX(j * GRID_WIDTH);
                newLabel.setLayoutY(i * GRID_HEIGHT);
                newLabel.setMinWidth(GRID_WIDTH);
                newLabel.setMaxWidth(GRID_WIDTH);
                newLabel.setMinHeight(GRID_HEIGHT);
                newLabel.setMaxHeight(GRID_HEIGHT);
                newLabel.setStyle("-fx-border-color: black;");
                grids[i][j] = newLabel;
                paneArena.getChildren().addAll(newLabel);
            }

        setDragTwoer();
    }

    @FXML
    private void nextFrame() {
        if (x == -1) {
            grids[0][0].setText("M");
            x = 0;
            return;
        }
        if (y == MAX_V_NUM_GRID - 1)
            return;
        grids[y++][x].setText("");
        grids[y][x].setText("M");
    }
    
    private void setDragTwoer() {
    	Label[] labels = {labelBasicTower, labelIceTower, labelCatapult, labelLaserTower};
    	for (Label l : labels) {
    		l.setOnDragDetected(new DragEventHandler(l));
    	}
    	
    	for (int i = 0; i < MAX_V_NUM_GRID; i++) {
            for (int j = 0; j < MAX_H_NUM_GRID; j++) {
            	Label target = grids[i][j];
            	
            	int x = (int)((j)*GRID_WIDTH);
            	int y = (int)((i)*GRID_HEIGHT);
            	Coordinates c = new Coordinates(arena, x, y);
            	
                target.setOnDragOver(e -> {
                    /* accept it only if it is  not dragged from the same node
                     * and if it has a string data */
                    if (e.getGestureSource() != target &&
                            e.getDragboard().hasString()) {
                        /* allow for both copying and moving, whatever user chooses */
                        e.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                    }

                    e.consume();
                });
                
            	target.setOnDragEntered(e -> { // grids[y][x]
            		if (arena.canBuildTower(c)) {
            			target.setStyle("-fx-border-color: blue;");
            		}
            		e.consume();
            	});
            	
            	target.setOnDragExited(e -> {
            		target.setStyle("-fx-border-color: black;");
            		e.consume();
            	});
            	
            	target.setOnDragDropped(e -> {
            		// ui part
            		Image img = null;
            		Object source = e.getGestureSource();
            		if (source.equals(labelBasicTower))
            			img = new Image("/basicTower.png", GRID_WIDTH, GRID_HEIGHT, true, true);
            		else if (source.equals(labelIceTower))
            			img = new Image("/iceTower.png", GRID_WIDTH, GRID_HEIGHT, true, true);
            		else if (source.equals(labelCatapult))
            			img = new Image("/catapult.png", GRID_WIDTH, GRID_HEIGHT, true, true);
            		else if (source.equals(labelLaserTower))
            			img = new Image("/laserTower.png", GRID_WIDTH, GRID_HEIGHT, true, true);
            		
                    if (img != null) {
                    	ImageView iv = new ImageView(img);
                        iv.setX(x);
                        iv.setY(y);
                        paneArena.getChildren().add(iv);
                        e.setDropCompleted(true);
                    }
                    e.consume();
                    
                    // back-end part
                    // TODO: arena.buildTower(coordinates);
                    // arena.buildTower(c);
                    
            	});
            }
    	}
    	
    }
}

class DragEventHandler implements EventHandler<MouseEvent> {
    private Label source;
    public DragEventHandler(Label e) {
        source = e;
    }
    @Override
    public void handle (MouseEvent event) {
        Dragboard db = source.startDragAndDrop(TransferMode.ANY);

        ClipboardContent content = new ClipboardContent();
        content.putString(source.getText());
        db.setContent(content);

        event.consume();
    }
}