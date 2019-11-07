package project;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.event.*;
import javafx.fxml.FXML;
import javafx.scene.layout.*;
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

    @FXML
    private Label remainingResources;

    static final int ARENA_WIDTH = 480;
    static final int ARENA_HEIGHT = 480;
    static final int GRID_WIDTH = 40;
    static final int GRID_HEIGHT = 40;
    static final int MAX_H_NUM_GRID = ARENA_WIDTH / GRID_WIDTH;
    static final int MAX_V_NUM_GRID = ARENA_HEIGHT / GRID_HEIGHT;

    static enum modes {normal, simulate, play};
    static modes mode = modes.normal;

    private Arena arena;
    private Circle towerCircle; // circle that shows shooting range of tower
    private Label towerLabel; // Label that shows the information of tower
    private VBox vb; // the vbox that use to upgrade/destory tower


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

        arena = new Arena(remainingResources);
        setDragLabel();
    }

    /**
     * A function that shows next Frame of the game.
     */
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

    /**
     * Initialise the tower labels for drag and drop use.
     */
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
                    if(mode != modes.simulate) {
                        e.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                    }
                    e.consume();
                });

            	target.setOnDragEntered(e -> { // grids[y][x]
                    Object source = e.getGestureSource();
            	    String type = null;
                    if (source.equals(labelBasicTower)) {
                        type = "Basic Tower";
                    } else if (source.equals(labelIceTower)) {
                        type = "Ice Tower";
                    } else if (source.equals(labelCatapult)) {
                        type = "Catapult";
                    } else if (source.equals(labelLaserTower)) {
                        type = "Laser Tower";
                    }
            		if (arena.canBuildTower(c, type)) {
            			target.setStyle("-fx-border-color: blue;");
            		} else {
            		    target.setStyle("-fx-border-color: red;");
                    }
            		e.consume();
            	});

            	target.setOnDragExited(e -> {
            		target.setStyle("-fx-border-color: black;");
            		e.consume();
            	});

            	target.setOnDragDropped(e -> {
                    Image img = null;
                    String type = null;
                    Object source = e.getGestureSource();
                    if (source.equals(labelBasicTower)) {
                        img = new Image("/basicTower.png", GRID_WIDTH, GRID_HEIGHT, true, true);
                        type = "Basic Tower";
                    } else if (source.equals(labelIceTower)) {
                        img = new Image("/iceTower.png", GRID_WIDTH, GRID_HEIGHT, true, true);
                        type = "Ice Tower";
                    } else if (source.equals(labelCatapult)) {
                        img = new Image("/catapult.png", GRID_WIDTH, GRID_HEIGHT, true, true);
                        type = "Catapult";
                    } else if (source.equals(labelLaserTower)) {
                        img = new Image("/laserTower.png", GRID_WIDTH, GRID_HEIGHT, true, true);
                        type = "Laser Tower";
                    }

                    if(!arena.hasResources(type)) {
                        // not enough resources
                        showAlert("Not enough resources", "Do not have enough resources to build " + type + "!");

                    } else if (img != null && arena.canBuildTower(c, type)) {
                        ImageView iv = new ImageView(img);
                        Tower t = arena.buildTower(c, iv, type);

                        if (t != null) {
                            setTowerEvent(t);
                            paneArena.getChildren().add(iv);
                            e.setDropCompleted(true);
                        }
            		}

                    e.consume();
            	});

            }
    	}

    }

    /**
     * add event listener to display the tower information and upgrade/destroy the tower.
     * @param t a tower in the arena.
     */
    private void setTowerEvent(Tower t) {
        Coordinates coor = new Coordinates(t.getX(), t.getY());
        Coordinates center = Grid.findGridCenter(coor);

        ImageView iv = t.getImageView();

        iv.setOnMouseEntered(e -> {
            // display shooting range
            towerCircle = new Circle();
            towerCircle.setCenterX(center.getX());
            towerCircle.setCenterY(center.getY());
            // set StrokeWidth to simulate a ring (for catapult).
            double avgOfRangeAndLimit = (t.getShootingRange() + t.getShootLimit()) / 2;
            towerCircle.setRadius(avgOfRangeAndLimit);
            towerCircle.setFill(Color.TRANSPARENT);
            towerCircle.setStrokeWidth(t.getShootingRange() - t.getShootLimit());
            towerCircle.setStroke(Color.rgb(0,101,255,0.4));
            paneArena.getChildren().add(paneArena.getChildren().indexOf(iv), towerCircle);

            // display tower information
            towerLabel = new Label(t.getInformation());
            towerLabel.setAlignment(Pos.CENTER);
            towerLabel.setMinWidth(GRID_WIDTH * 3);
            towerLabel.setMinHeight(GRID_HEIGHT * 2);
            double positionX = coor.getX() > paneArena.getWidth()/2 ? coor.getX() - GRID_WIDTH * 3: coor.getX() + GRID_WIDTH;
            double positionY = coor.getY() > paneArena.getHeight()/2 ? coor.getY() - GRID_HEIGHT * 2: coor.getY() + GRID_HEIGHT;
            towerLabel.setLayoutX(positionX);
            towerLabel.setLayoutY(positionY);
            towerLabel.setStyle("-fx-padding: 5px; -fx-text-alignment: center;");
            towerLabel.setBackground(new Background(new BackgroundFill(Color.rgb(255,255,255, 0.7), new CornerRadii(5), Insets.EMPTY)));
            paneArena.getChildren().add(towerLabel);
        });
        iv.setOnMouseExited(e -> {
            // remove info & shooting range
            paneArena.getChildren().removeAll(towerCircle, towerLabel);
        });

        iv.setOnMouseClicked(e -> {
            if (paneArena.getChildren().contains(vb)) {
                paneArena.getChildren().remove(vb);
            }

            MouseButton button = e.getButton();
            if (button == MouseButton.PRIMARY) {
                vb = new VBox(15);
                vb.setBackground(new Background(new BackgroundFill(Color.rgb(255,255,255, 0.7), new CornerRadii(5), Insets.EMPTY)));
                vb.setAlignment(Pos.CENTER);
                Button upgradeBtn = new Button("upgrade");
                Button destroyBtn = new Button("destroy");
                vb.getChildren().addAll(upgradeBtn, destroyBtn);

                upgradeBtn.setOnAction(e2 -> {
                    if(arena.upgradeTower(t)) {
                        // TODO: a line 'XXX tower is being upgraded' is printed on the console.

                    } else {
                        // TODO: a line 'not enough resource to upgrade XXX tower' is printed on the console
                    }
                    paneArena.getChildren().remove(vb);
                });
                destroyBtn.setOnAction(e2 -> {
                    paneArena.getChildren().remove(vb);
                    arena.destroyTower(t, paneArena);
                });

                paneArena.getChildren().add(vb);
            }
        });

    }

    /**
     * show an alert to notify the player some information.
     * @param title title of the dialog box.
     * @param content content of the dialog box.
     */
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}

