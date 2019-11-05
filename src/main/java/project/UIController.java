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
import javafx.scene.shape.Circle;
import project.towers.Tower;

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

        setDragLabel();
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

    private void setDragLabel() {
    	Label[] labels = {labelBasicTower, labelIceTower, labelCatapult, labelLaserTower};
    	for (Label l : labels) {
    		l.setOnDragDetected(e -> {
    	        Dragboard db = l.startDragAndDrop(TransferMode.ANY);

    	        ClipboardContent content = new ClipboardContent();
    	        content.putString(l.getText());
    	        db.setContent(content);

    	        e.consume();
    	    });
    	}

    	for (int i = 0; i < MAX_V_NUM_GRID; i++) {
            for (int j = 0; j < MAX_H_NUM_GRID; j++) {
            	Label target = grids[i][j];

            	int x = j * GRID_WIDTH;
            	int y = i * GRID_HEIGHT;
            	Coordinates c = new Coordinates(x, y);

                target.setOnDragOver(e -> {
                    e.acceptTransferModes(TransferMode.COPY_OR_MOVE);
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
            	    if (arena.canBuildTower(c)) {
	            		Image img = null;
	            		String type = null;
	            		Object source = e.getGestureSource();
	            		if (source.equals(labelBasicTower)) {
	            			img = new Image("/basicTower.png", GRID_WIDTH, GRID_HEIGHT, true, true);
	            			type = "basic";
	            		} else if (source.equals(labelIceTower)) {
	            			img = new Image("/iceTower.png", GRID_WIDTH, GRID_HEIGHT, true, true);
	            			type = "ice";
	            		} else if (source.equals(labelCatapult)) {
	            			img = new Image("/catapult.png", GRID_WIDTH, GRID_HEIGHT, true, true);
	            			type = "catapult";
	            		} else if (source.equals(labelLaserTower)) {
	            			img = new Image("/laserTower.png", GRID_WIDTH, GRID_HEIGHT, true, true);
	            			type = "laser";
	            		}

	                    if (img != null) {
	                    	ImageView iv = new ImageView(img);
	                        iv.setX(x);
	                        iv.setY(y);
                            Tower t = arena.buildTower(c, iv, type);

                            if (t != null) { // enough resources
                                setTowerEvent(t);
                                paneArena.getChildren().add(iv);
                                e.setDropCompleted(true);
                            } else {

                            }

	                    }
	                    e.consume();

            		}
            	});
            }
    	}

    }

    private void setTowerEvent(Tower t) {
        ImageView iv = t.getImageView();
        final Circle c = new Circle();
    	// on hover
        iv.setOnMouseEntered(e -> {
            // TODO: display tower information


            // show shooting range
            Coordinates center = Grid.findGridCenter(t.getX(), t.getY());

            c.setCenterX(center.getX());
            c.setCenterY(center.getY());
            c.setRadius(t.getShootingRange());
            c.setFill(Color.rgb(0,101,255,0.4));
            paneArena.getChildren().add(paneArena.getChildren().indexOf(iv), c); // add it before the ImageView
        });

    	// exit
    	// TODO: remove info & shooting range
        iv.setOnMouseExited(e -> {
            paneArena.getChildren().remove(c);
        });

    }
}

