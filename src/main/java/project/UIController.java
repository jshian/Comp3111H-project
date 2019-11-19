package project;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
import project.arena.ArenaObjectFactory;
import project.arena.Coordinates;
import project.arena.Grid;
import project.arena.ArenaObjectFactory.TowerType;
import project.arena.towers.*;


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

    /**
     * width of arena.
     */
    public static final int ARENA_WIDTH = 480;
    /**
     * height of arena.
     */
    public static final int ARENA_HEIGHT = 480;
    /**
     * width of a grid.
     */
    public static final int GRID_WIDTH = 40;
    /**
     * height of a grid.
     */
    public static final int GRID_HEIGHT = 40;
    /**
     * maximum number of grids in one row.
     */
    public static final int MAX_H_NUM_GRID = ARENA_WIDTH / GRID_WIDTH;
    /**
     * maximum number of grids in one column.
     */
    public static final int MAX_V_NUM_GRID = ARENA_HEIGHT / GRID_HEIGHT;

    /**
     * An enum to show game state.
     */
    static enum GameMode {normal, simulate, play, end};
    /**
     * the game state.
     */
    static GameMode mode = GameMode.normal;

    /**
     * the arena of the game.
     */
    private Arena arena;

    /**
     * circle that shows shooting range of tower.
     */
    private Circle towerCircle;

    /**
     * Tooltip that shows the information of tower.
     */
    private Tooltip tp;

    /**
     * the VBox that use to upgrade/destroy tower.
     */
    private VBox vb;

    /**
     * the timeline for running the game.
     */
    private Timeline timeline = new Timeline();

    /**
     * the grids on arena.
     */
    private Label grids[][] = new Label[MAX_V_NUM_GRID][MAX_H_NUM_GRID];
    /**
     * Play the game. Build towers are allowed.
     */
    @FXML
    private void play() {
        run(GameMode.play);
    }

    /**
     * Simulate the game. Build towers are not allowed.
     */
    @FXML
    private void simulate() {
        run(GameMode.simulate);
    }

    /**
     * Pause the game.
     */
    @FXML
    private void pause() {
        if (this.mode == GameMode.simulate) {
            buttonSimulate.setDisable(false);
        } else if (this.mode == GameMode.play) {
            buttonPlay.setDisable(false);
        }
        buttonNextFrame.setDisable(false);
        timeline.pause();
    }

    /**
     * Save the game
     */
    @FXML
    private void save() {
        // TODO: save the game
    }

    /**
     * Load the game
     */
    private void load() {
        // TODO: load the game
        // TODO: load arena instead of creating a new one.
        Arena arena = new Arena(remainingResources, paneArena);

        resetGame();
    }

    /**
     * Run the game.
     * @param mode specify the mode of the game.
     */
    private void run(GameMode mode) {
        if (this.mode == GameMode.end)
            resetGame();

        this.mode = mode;
        disableGameButton();
        timeline = new Timeline(new KeyFrame(Duration.seconds(0.2), e -> nextFrame()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    /**
     * Reset the game.
     */
    private void resetGame() {
        timeline.stop();
        paneArena.getChildren().removeAll(paneArena.getChildren());
        paneArena.getChildren().addAll(initialArena().getChildren());
        arena = new Arena(remainingResources, paneArena);
        this.mode = GameMode.normal;
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
        for (short i = 0; i < MAX_V_NUM_GRID; i++)
            for (short j = 0; j < MAX_H_NUM_GRID; j++) {
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
     * A function to get an AnchorPane with initial grids of the game.
     * @return return an AnchorPane with initial grids of the game.
     */
    public AnchorPane initialArena() {
        AnchorPane newPane = new AnchorPane();
        for (int j = 0; j < grids.length; j++) {
            for (int i = 0; i < grids[0].length; i++) {
                newPane.getChildren().add(grids[j][i]);
            }
        }
        return newPane;
    }

    /**
     * A function that shows next Frame of the game.
     */
    @FXML
    private void nextFrame() {
        if (this.mode != GameMode.end) {
            boolean gameOver = arena.nextFrame();
            if (gameOver) {
                mode = GameMode.end;
                enableGameButton();
                showAlert("Gameover","Gameover").setOnCloseRequest(e -> resetGame());
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

    	for (short i = 0; i < MAX_V_NUM_GRID; i++) {
            for (short j = 0; j < MAX_H_NUM_GRID; j++) {
            	Label target = grids[i][j];

            	short x = (short) (j * GRID_WIDTH);
            	short y = (short) (i * GRID_HEIGHT);
            	Coordinates c = new Coordinates(x, y);

                target.setOnDragOver(e -> {
                    if(mode != GameMode.simulate && mode != GameMode.end) {
                        e.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                        Object source = e.getGestureSource();
                        TowerType type = null;
                        if (source.equals(labelBasicTower)) {
                            type = TowerType.BasicTower;
                        } else if (source.equals(labelIceTower)) {
                            type = TowerType.IceTower;
                        } else if (source.equals(labelCatapult)) {
                            type = TowerType.Catapult;
                        } else if (source.equals(labelLaserTower)) {
                            type = TowerType.LaserTower;
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
            	    if (mode != GameMode.simulate && mode != GameMode.end) {
                        TowerType type = null;
                        Object source = e.getGestureSource();
                        if (source.equals(labelBasicTower)) {
                            type = TowerType.BasicTower;
                        } else if (source.equals(labelIceTower)) {
                            type = TowerType.IceTower;
                        } else if (source.equals(labelCatapult)) {
                            type = TowerType.Catapult;
                        } else if (source.equals(labelLaserTower)) {
                            type = TowerType.LaserTower;
                        }

                        if (!arena.hasResources(type)) {
                            // not enough resources
                            showAlert("Not enough resources", "Do not have enough resources to build " + type + "!");

                        } else if (arena.canBuildTower(c, type)) {
                            Tower t = arena.buildTower(c, type);

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
            tp = new Tooltip(t.toString());
            tp.show(t.getImageView(), e.getScreenX()+8, e.getScreenY()+7);
        });
        iv.setOnMouseMoved(e -> tp.show(t.getImageView(), e.getScreenX()+8, e.getScreenY()+7));
        iv.setOnMouseExited(e -> {
            paneArena.getChildren().remove(towerCircle);
            tp.hide();
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
        Tooltip tp = new Tooltip();
        TowerType type = null;
        if (l.equals(labelBasicTower)) {
            type = TowerType.BasicTower;
        } else if (l.equals(labelIceTower)) {
            type = TowerType.IceTower;
        } else if (l.equals(labelCatapult)) {
            type = TowerType.Catapult;
        } else if (l.equals(labelLaserTower)) {
            type = TowerType.LaserTower;
        }
        tp.setText(String.format("building cost: %d", arena.findTowerBuildingCost(type)));
        tp.setShowDelay(Duration.ZERO);
        tp.setHideDelay(Duration.ZERO);

        l.setOnMouseEntered(e -> tp.show(l, e.getScreenX()+8, e.getScreenY()+7));
        l.setOnMouseMoved(e -> tp.show(l, e.getScreenX()+8, e.getScreenY()+7));
        l.setOnMouseExited(e -> tp.hide());

        l.setOnDragDetected(e -> {
            if (mode != GameMode.simulate) {
                Dragboard db = l.startDragAndDrop(TransferMode.ANY);

                ClipboardContent content = new ClipboardContent();
                content.putString(l.getText());
                db.setContent(content);
            }

            e.consume();
        });
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
        Coordinates coor = new Coordinates((short) (center.getX() - GRID_WIDTH/2), (short) (center.getY() - GRID_HEIGHT/2));

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
     * @return the alert being shown.
     */
    private Alert showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.show();
        return alert;
    }
}

