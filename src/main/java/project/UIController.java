package project;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import project.arena.Arena;
import project.arena.Coordinates;
import project.arena.Grid;
import project.arena.towers.*;

import javax.tools.Tool;


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

    public static final int ARENA_WIDTH = 480;
    public static final int ARENA_HEIGHT = 480;
    public static final int GRID_WIDTH = 40;
    public static final int GRID_HEIGHT = 40;
    public static final int MAX_H_NUM_GRID = ARENA_WIDTH / GRID_WIDTH;
    public static final int MAX_V_NUM_GRID = ARENA_HEIGHT / GRID_HEIGHT;

    static enum modes {normal, simulate, play, paused, end};
    static modes mode = modes.normal;

    private Arena arena;
    private Circle towerCircle; // circle that shows shooting range of tower
    private Label towerLabel; // Label that shows the information of tower
    private VBox vb; // the vbox that use to upgrade/destory tower
    private Timeline timeline;

    private Label grids[][] = new Label[MAX_V_NUM_GRID][MAX_H_NUM_GRID]; //the grids on arena. grids[y][x]
    private int x = -1, y = 0; //where is my monster
    /**
     * Play the game. Build towers are allowed.
     */
    @FXML
    private void play() {
        run(modes.play);
    }

    /**
     * Simulate the game. Build towers are not allowed.
     */
    @FXML
    private void simulate() {
        run(modes.simulate);
    }

    /**
     * Pause the game.
     */
    @FXML
    private void pause() {
        if (timeline != null) {
            if (this.mode == modes.simulate) {
                buttonSimulate.setDisable(false);
            } else if (this.mode == modes.play) {
                buttonPlay.setDisable(false);
            }
            buttonNextFrame.setDisable(false);
            this.mode = modes.paused;
            timeline.pause();
        }
    }

    /**
     * Save the game
     */
    @FXML
    private void save() {

    }

    /**
     * Run the game.
     * @param mode specify the mode of the game.
     */
    private void run(modes mode) {
        if (this.mode == modes.end)
            resetGame();

        this.mode = mode;
        disableGameButton();
        timeline = new Timeline(new KeyFrame(Duration.seconds(0.2), e -> {
            nextFrame();
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    /**
     * Reset the game.
     */
    private void resetGame() {
        paneArena = new AnchorPane();
        createArena();
        arena = new Arena(remainingResources, paneArena);
    }

    /**
     * Disable "next frame", "simulate" and "play" button.
     */
    private void disableGameButton() {
        buttonNextFrame.setDisable(true);
        buttonPlay.setDisable(true);
        buttonSimulate.setDisable(true);
    }

    /**
     * Enable "next frame", "simulate" and "play" button.
     */
    private void enableGameButton() {
        buttonNextFrame.setDisable(false);
        buttonPlay.setDisable(false);
        buttonSimulate.setDisable(false);
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
                    BackgroundImage backgroundImage1 = new BackgroundImage(image1, BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
                    newLabel.setBackground(new Background(backgroundImage1));
//                } else if (j % 2 == 0 || i == ((j + 1) / 2 % 2) * (MAX_V_NUM_GRID - 1))
//                    newLabel.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
//                else
//                    newLabel.setBackground(new Background(new BackgroundFill(Color.GREEN, CornerRadii.EMPTY, Insets.EMPTY)));
                } else {
                    newLabel.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
                }
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

        paneArena.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                if(paneArena.getChildren().contains(vb)) {
                    paneArena.getChildren().remove(vb);
                }
            }
        });

        arena = new Arena(remainingResources, paneArena);
        setDragLabel();
    }

    /**
     * A function that shows next Frame of the game.
     */
    @FXML
    private void nextFrame() {
        if (this.mode != modes.end) {
            boolean gameOver = arena.nextFrame();
            if (gameOver) {
                mode = modes.end;
                enableGameButton();
                showAlert("Gameover","Gameover");
                if (timeline != null) {
                    timeline.stop();
                }
            }
        }
    }

    /**
     * Initialise the tower labels for drag and drop use.
     */
    private void setDragLabel() {
    	Label[] labels = {labelBasicTower, labelIceTower, labelCatapult, labelLaserTower};
    	for (Label l : labels) {
    	    dragLabelEvent(l);
    	}

    	for (int i = 0; i < MAX_V_NUM_GRID; i++) {
            for (int j = 0; j < MAX_H_NUM_GRID; j++) {
            	Label target = grids[i][j];

            	int x = j * GRID_WIDTH;
            	int y = i * GRID_HEIGHT;
            	Coordinates c = new Coordinates(x, y);

                target.setOnDragOver(e -> {
                    if(mode != modes.simulate && mode != modes.end) {
                        e.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                        Object source = e.getGestureSource();
                        Arena.TowerType type = null;
                        if (source.equals(labelBasicTower)) {
                            type = Arena.TowerType.BasicTower;
                        } else if (source.equals(labelIceTower)) {
                            type = Arena.TowerType.IceTower;
                        } else if (source.equals(labelCatapult)) {
                            type = Arena.TowerType.Catapult;
                        } else if (source.equals(labelLaserTower)) {
                            type = Arena.TowerType.LaserTower;
                        }
                        if (arena.canBuildTower(c, type)) {
                            target.setStyle("-fx-border-color: blue;");
                        } else {
                            target.setStyle("-fx-border-color: red;");
                        }
                    }
            		e.consume();
            	});

            	target.setOnDragExited(e -> {
            		target.setStyle("-fx-border-color: black;");
            		e.consume();
            	});

            	target.setOnDragDropped(e -> {
            	    if (mode != modes.simulate && mode != modes.end) {
                        Image img = null;
                        Arena.TowerType type = null;
                        Object source = e.getGestureSource();
                        if (source.equals(labelBasicTower)) {
                            img = new Image("/basicTower.png", GRID_WIDTH, GRID_HEIGHT, true, true);
                            type = Arena.TowerType.BasicTower;
                        } else if (source.equals(labelIceTower)) {
                            img = new Image("/iceTower.png", GRID_WIDTH, GRID_HEIGHT, true, true);
                            type = Arena.TowerType.IceTower;
                        } else if (source.equals(labelCatapult)) {
                            img = new Image("/catapult.png", GRID_WIDTH, GRID_HEIGHT, true, true);
                            type = Arena.TowerType.Catapult;
                        } else if (source.equals(labelLaserTower)) {
                            img = new Image("/laserTower.png", GRID_WIDTH, GRID_HEIGHT, true, true);
                            type = Arena.TowerType.LaserTower;
                        }

                        if (!arena.hasResources(type)) {
                            // not enough resources
                            showAlert("Not enough resources", "Do not have enough resources to build " + type + "!");

                        } else if (img != null && arena.canBuildTower(c, type)) {
                            ImageView iv = new ImageView(img);
                            Tower t = arena.buildTower(c, iv, type);

                            if (t != null) {
                                setTowerEvent(t);
                                e.setDropCompleted(true);
                            }
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
        Coordinates center = Grid.findGridCenter(new Coordinates(t.getX(), t.getY()));

        ImageView iv = t.getImageView();

        iv.setOnMouseEntered(e -> {
            drawTowerCircle(center, t);
            displayTowerInfo(center, t);
        });
        iv.setOnMouseExited(e -> {
            paneArena.getChildren().removeAll(towerCircle, towerLabel);
        });

        iv.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                showTowerVBox(center, t);
            }
        });

    }

    /**
     * Draw circle to show the shooting range of tower.
     * @param center center coordinate of tower.
     * @param t the tower that need to show shooting range.
     */
    private void drawTowerCircle(Coordinates center, Tower t) {
        ImageView iv = t.getImageView();
        // display shooting range
        towerCircle = new Circle();
        towerCircle.setCenterX(center.getX());
        towerCircle.setCenterY(center.getY());
        // set StrokeWidth to simulate a ring (for catapult).
        double avgOfRangeAndLimit = (t.getMaxShootingRange() + t.getMinShootingRange()) / 2;
        towerCircle.setRadius(avgOfRangeAndLimit);
        towerCircle.setFill(Color.TRANSPARENT);
        towerCircle.setStrokeWidth(t.getMaxShootingRange() - t.getMinShootingRange());
        towerCircle.setStroke(Color.rgb(0,101,255,0.4));
        paneArena.getChildren().add(paneArena.getChildren().indexOf(iv), towerCircle);
    }

    /**
     * set the event on label that used to build tower by drag/drop.
     * @param l the label that used to build tower by drag/drop.
     */
    private void dragLabelEvent(Label l) {
        Tooltip t = new Tooltip();
        Tower temp;
        if (l.equals(labelBasicTower)) {
            temp = new BasicTower(arena, new Coordinates(0,0));
            t.setText(String.format("building cost: %d", temp.getBuildingCost()));
        } else if (l.equals(labelIceTower)) {
            temp = new IceTower(arena, new Coordinates(0,0));
            t.setText(String.format("building cost: %d", temp.getBuildingCost()));
        } else if (l.equals(labelCatapult)) {
            temp = new Catapult(arena, new Coordinates(0,0));
            t.setText(String.format("building cost: %d", temp.getBuildingCost()));
        } else if (l.equals(labelLaserTower)) {
            temp = new LaserTower(arena, new Coordinates(0,0));
            t.setText(String.format("building cost: %d", temp.getBuildingCost()));
        }
        t.setShowDelay(Duration.ZERO);
        Tooltip.install(l, t);

        l.setOnDragDetected(e -> {
            if (mode != modes.simulate) {
                Dragboard db = l.startDragAndDrop(TransferMode.ANY);

                ClipboardContent content = new ClipboardContent();
                content.putString(l.getText());
                db.setContent(content);
            }

            e.consume();
        });
    }

    /**
     * Display tower information.
     * @param center center coordinate of tower.
     * @param t the tower that need to display information.
     */
    private void displayTowerInfo(Coordinates center, Tower t) {
        Coordinates coor = new Coordinates((short) (center.getX() - GRID_WIDTH/2), (short) (center.getY() - GRID_HEIGHT/2));

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
    }

    /**
     * Display the VBox that perform upgrade/destroy of a tower.
     * @param center center coordinate of tower.
     * @param t the tower that need to upgrade/destroy.
     */
    private void showTowerVBox(Coordinates center, Tower t) {
        if (paneArena.getChildren().contains(vb)) {
            paneArena.getChildren().remove(vb);
        }
        Coordinates coor = new Coordinates(center.getX() - GRID_WIDTH/2, center.getY() - GRID_HEIGHT/2);

        vb = new VBox(15);
        vb.setStyle("-fx-padding: 5px; -fx-text-alignment: center;");
        vb.setBackground(new Background(new BackgroundFill(Color.rgb(255,255,255, 0.7), new CornerRadii(5), Insets.EMPTY)));
        vb.setAlignment(Pos.CENTER);
        vb.setMinWidth(GRID_WIDTH * 2);
        vb.setMinHeight(GRID_HEIGHT * 2);
        double positionX = (coor.getX() > paneArena.getWidth()/2) ? coor.getX()-GRID_HEIGHT*2 : coor.getX()+GRID_HEIGHT;
        double positionY = (coor.getY() >= paneArena.getWidth()-GRID_HEIGHT) ? coor.getY()-GRID_HEIGHT : coor.getY();
        vb.setLayoutX(positionX);
        vb.setLayoutY(positionY);
        Button upgradeBtn = new Button("upgrade");
        Button destroyBtn = new Button("destroy");
        vb.getChildren().addAll(upgradeBtn, destroyBtn);

        upgradeBtn.setOnAction(e2 -> {
            arena.upgradeTower(t);
            paneArena.getChildren().remove(vb);
        });
        destroyBtn.setOnAction(e2 -> {
            arena.removeObject(t);
            paneArena.getChildren().remove(vb);
        });

        paneArena.getChildren().add(vb);
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
        alert.show();
    }
}

