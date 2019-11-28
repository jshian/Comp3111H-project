package project.ui;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
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
import javafx.scene.shape.Line;
import javafx.util.Duration;

import project.Player;
import project.control.ArenaManager;
import project.entity.ArenaObjectFactory;
import project.entity.Tower;
import project.entity.ArenaObjectFactory.TowerType;
import project.event.EventHandler;
import project.event.eventargs.EventArgs;
import project.util.Geometry;


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
     * The player of the arena.
     */
    private Player player;

    /**
     * Contains a reference to line, circle, and monster explosion on the arena.
     * These nodes are temporary and will be removed after a certain period.
     */
    private HashMap<Node, Integer> temporaryNodes = new HashMap<>();

    /**
     * An enum to show game state.
     */
    public static enum GameMode {
        /**
         * The stage before the game is run.
         */
        NORMAL,

        /**
         * The game is being run in simulate mode.
         */
        SIMULATE,

        /**
         * The game is being run in play node.
         */
        PLAY,

        /**
         * The game has ended.
         */
        END;
    };

    /**
     * the game state.
     */
    static GameMode mode = GameMode.NORMAL;

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
    private Label grids[][] = new Label[ArenaManager.getMaxVerticalGrids()][ArenaManager.getMaxHorizontalGrids()];

    /**
     * The method invoked when the game is over.
     */
    private EventHandler<EventArgs> onGameover = (sender, args) -> {
        System.out.println("Gameover");
        mode = GameMode.END;
        enableGameButton();
        showAlert("Gameover","Gameover").setOnCloseRequest(e -> resetGame());
    };

    /**
     * A function that create the Arena
     */
    @FXML
    public void createArena() {
        if (grids[0][0] != null) return; //created already

        short maxV = ArenaManager.getMaxVerticalGrids();
        short maxH = ArenaManager.getMaxHorizontalGrids();

        for (short i = 0; i < maxV; i++)
            for (short j = 0; j < maxH; j++) {
                Label newLabel = new Label();
                if (j == ArenaManager.getEndGridXPos() && i == ArenaManager.getEndGridYPos()) {
                    Image image1 = new Image("/end-zone.png", ArenaManager.GRID_WIDTH, ArenaManager.GRID_HEIGHT, true, true);
                    BackgroundImage backgroundImage1= new BackgroundImage(image1, BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
                    newLabel.setBackground(new Background(backgroundImage1));
                } else if (j == ArenaManager.getStartingGridXPos() && i == ArenaManager.getStartingGridYPos()) {
                    Image image1 = new Image("/show-up.png", ArenaManager.GRID_WIDTH, ArenaManager.GRID_HEIGHT, true, true);
                    BackgroundImage backgroundImage1 = new BackgroundImage(image1, BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
                    newLabel.setBackground(new Background(backgroundImage1));
//                } else if (j % 2 == 0 || i == ((j + 1) / 2 % 2) * (MaxV - 1))
//                    newLabel.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
//                else
//                    newLabel.setBackground(new Background(new BackgroundFill(Color.GREEN, CornerRadii.EMPTY, Insets.EMPTY)));
                } else {
                    newLabel.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
                }
                newLabel.setLayoutX(j * ArenaManager.GRID_WIDTH);
                newLabel.setLayoutY(i * ArenaManager.GRID_HEIGHT);
                newLabel.setMinWidth(ArenaManager.GRID_WIDTH);
                newLabel.setMaxWidth(ArenaManager.GRID_WIDTH);
                newLabel.setMinHeight(ArenaManager.GRID_HEIGHT);
                newLabel.setMaxHeight(ArenaManager.GRID_HEIGHT);
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

        setupNewGame();
        setDragLabel();
    }
    
    /**
     * Sets up a new game.
     */
    private void setupNewGame() {
        player = new Player("name", 200);
        remainingResources.textProperty().bind(Bindings.format("Money: %d", player.resourcesProperty()));
        ArenaManager.loadNew(this, player);
        ArenaManager.getActiveEventRegister().ARENA_GAME_OVER.subscribe(onGameover);
    }

    /**
     * Runs the next frame.
     */
    @FXML
    private void nextFrame() {
        if (mode != GameMode.END) {
            LinkedList<Node> nodesToRemove = new LinkedList<>();
            for (Map.Entry<Node, Integer> entry : temporaryNodes.entrySet()) {
                Node key = entry.getKey();
                Integer value = entry.getValue();
                if (value > 0) {
                    entry.setValue(--value);
                } else {
                    nodesToRemove.add(key);
                }
            }
            for (Node n : nodesToRemove){
                temporaryNodes.remove(n);
                removeFromPane(n);
            }

            ArenaManager.getActiveEventRegister().ARENA_NEXT_FRAME.invoke(this, new EventArgs());
            ArenaManager.getActiveEventRegister().ARENA_NEXT_FRAME_END.invoke(this, new EventArgs());
        }
    }

    /**
     * Play the game. Build towers are allowed.
     */
    @FXML
    private void play() {
        run(GameMode.PLAY);
    }

    /**
     * Simulate the game. Build towers are not allowed.
     */
    @FXML
    private void simulate() {
        run(GameMode.SIMULATE);
    }

    /**
     * Pause the game.
     */
    @FXML
    private void pause() {
        if (mode == GameMode.SIMULATE) {
            buttonSimulate.setDisable(false);
        } else if (mode == GameMode.PLAY) {
            buttonPlay.setDisable(false);
        }
        buttonNextFrame.setDisable(false);
        timeline.pause();
    }

    /**
     * Load the game
     */
    @FXML
    private void load() {
        // TODO
        resetGame();
    }

    /**
     * Saves the game.
     */
    @FXML
    private void save() {
        // TODO
        ArenaManager.save(player);
    }

    /**
     * Run the game.
     * @param gameMode specify the mode of the game.
     */
    private void run(GameMode gameMode) {
        if (mode == GameMode.END)
            resetGame();

        mode = gameMode;
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
        setupNewGame();
        mode = GameMode.NORMAL;
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
     * Initialise the tower labels for drag and drop use.
     */
    private void setDragLabel() {
    	Label[] labels = {labelBasicTower, labelIceTower, labelCatapult, labelLaserTower};
    	for (Label l : labels) {
    	    dragLabelEvent(l);
        }
        
        short maxV = ArenaManager.getMaxVerticalGrids();
        short maxH = ArenaManager.getMaxHorizontalGrids();

    	for (short i = 0; i < maxV; i++) {
            for (short j = 0; j < maxH; j++) {
            	Label target = grids[i][j];

                short x = ArenaManager.getGridCenterXFromPos(j);
            	short y = ArenaManager.getGridCenterYFromPos(i);

                target.setOnDragOver(e -> {
                    if(mode != GameMode.SIMULATE && mode != GameMode.END) {
                        e.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                        Object source = e.getGestureSource();
                        TowerType type = null;
                        
                        if (source.equals(labelBasicTower)) type = TowerType.BASIC;
                        else if (source.equals(labelIceTower)) type = TowerType.ICE;
                        else if (source.equals(labelCatapult)) type = TowerType.CATAPULT;
                        else if (source.equals(labelLaserTower)) type = TowerType.LASER;
                        
                        if (type != null) {
                            if (ArenaManager.getActiveArenaInstance().canBuildTowerAt(x, y)
                                    && ArenaManager.getActivePlayer().hasResources(type.getBuildingCost())) {
                                target.setStyle("-fx-border-color: blue;");
                            } else {
                                target.setStyle("-fx-border-color: red;");
                            }
                        }
                    }
            		e.consume();
            	});

            	target.setOnDragExited(e -> {
            		target.setStyle("-fx-border-color: black;");
            		e.consume();
            	});

            	target.setOnDragDropped(e -> {
            	    if (mode != GameMode.SIMULATE && mode != GameMode.END) {
                        Object source = e.getGestureSource();
                        TowerType type = null;
                        
                        if (source.equals(labelBasicTower)) type = TowerType.BASIC;
                        else if (source.equals(labelIceTower)) type = TowerType.ICE;
                        else if (source.equals(labelCatapult)) type = TowerType.CATAPULT;
                        else if (source.equals(labelLaserTower)) type = TowerType.LASER;

                        if (type != null) {
                            if (!ArenaManager.getActivePlayer().hasResources(type.getBuildingCost())) {
                                // not enough resources
                                showAlert("Not enough resources", "Do not have enough resources to build " + type.getDefaultName() + "!");
    
                            } else if (ArenaManager.getActiveArenaInstance().canBuildTowerAt(x, y) && ArenaManager.getActivePlayer().hasResources(type.getBuildingCost())) {
                                Tower newTower = ArenaObjectFactory.createTower(UIController.this, type, x, y);
                                setTowerEvent(newTower);
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
        ImageView iv = t.getImageView();

        iv.setOnMouseEntered(e -> {
            drawTowerCircle(t);
            tp = new Tooltip(t.getDisplayDetails());
            tp.show(t.getImageView(), e.getScreenX()+8, e.getScreenY()+7);
        });
        iv.setOnMouseMoved(e -> tp.show(t.getImageView(), e.getScreenX()+8, e.getScreenY()+7));
        iv.setOnMouseExited(e -> {
            paneArena.getChildren().remove(towerCircle);
            tp.hide();
        });

        iv.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                showTowerVBox(t);
            }
        });

    }

    /**
     * Draw circle to show the shooting range of tower.
     * @param t the tower that need to show shooting range.
     */
    private void drawTowerCircle(Tower t) {
        ImageView iv = t.getImageView();

        // display shooting range
        towerCircle = new Circle();
        towerCircle.setCenterX(t.getX());
        towerCircle.setCenterY(t.getY());
        // set StrokeWidth to simulate a ring (for catapult).
        double avgOfRangeAndLimit = (t.getMaxRange() + t.getMinRange()) / 2;
        towerCircle.setRadius(avgOfRangeAndLimit);
        towerCircle.setFill(Color.TRANSPARENT);
        towerCircle.setStrokeWidth(t.getMaxRange() - t.getMinRange());
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
            type = TowerType.BASIC;
        } else if (l.equals(labelIceTower)) {
            type = TowerType.ICE;
        } else if (l.equals(labelCatapult)) {
            type = TowerType.CATAPULT;
        } else if (l.equals(labelLaserTower)) {
            type = TowerType.LASER;
        }
        tp.setText(String.format("Build Cost: %d", type.getBuildingCost()));
        tp.setShowDelay(Duration.ZERO);
        tp.setHideDelay(Duration.ZERO);

        l.setOnMouseEntered(e -> tp.show(l, e.getScreenX()+8, e.getScreenY()+7));
        l.setOnMouseMoved(e -> tp.show(l, e.getScreenX()+8, e.getScreenY()+7));
        l.setOnMouseExited(e -> tp.hide());
        l.setOnMouseExited(e -> tp.hide());

        l.setOnDragDetected(e -> {
            if (mode != GameMode.SIMULATE) {
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
     * @param t the tower that need to upgrade/destroy.
     */
    private void showTowerVBox(Tower t) {
        if (paneArena.getChildren().contains(vb)) {
            paneArena.getChildren().remove(vb);
        }
        short coorX = (short) (t.getX() - ArenaManager.GRID_WIDTH/2);
        short coorY = (short) (t.getY() - ArenaManager.GRID_HEIGHT/2);

        vb = new VBox(15);
        vb.setStyle("-fx-padding: 5px; -fx-text-alignment: center;");
        vb.setBackground(new Background(new BackgroundFill(Color.rgb(255,255,255, 0.7), new CornerRadii(5), Insets.EMPTY)));
        vb.setAlignment(Pos.CENTER);
        vb.setMinWidth(ArenaManager.GRID_WIDTH * 2);
        vb.setMinHeight(ArenaManager.GRID_HEIGHT * 2);
        double positionX = (coorX > paneArena.getWidth()/2) ? coorX-ArenaManager.GRID_HEIGHT*2 : coorX+ArenaManager.GRID_HEIGHT;
        double positionY = (coorY >= paneArena.getWidth()-ArenaManager.GRID_HEIGHT) ? coorY-ArenaManager.GRID_HEIGHT : coorY;
        vb.setLayoutX(positionX);
        vb.setLayoutY(positionY);
        Button upgradeBtn = new Button("upgrade");
        Button destroyBtn = new Button("destroy");
        vb.getChildren().addAll(upgradeBtn, destroyBtn);

        upgradeBtn.setOnAction(e2 -> {
            t.tryUpgrade();
            paneArena.getChildren().remove(vb);
        });
        destroyBtn.setOnAction(e2 -> {
            ArenaObjectFactory.removeObject(UIController.this, t);
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

    /**
     * Adds a node to the {@link AnchorPane}.
     * @param n The node to add.
     */
    public void addToPane(Node n) {
        Platform.runLater(() -> paneArena.getChildren().add(n));
    }

    /**
     * Removes a node from the {@link AnchorPane}.
     * @param n The node to remove.
     */
    public void removeFromPane(Node n) {
        Platform.runLater(() -> paneArena.getChildren().remove(n));
    }

    /**
     * Draws an image at a specified point.
     * @param img The image.
     * @param x The x-coordinate of the point.
     * @param y The y-coordinate of the point.
     * @param duration The duration in number of frames that the image will remain on the arena.
     */
    public void drawImage(Image img, short x, short y, int duration) {
        ImageView iv = new ImageView(img);
        iv.setX(x);
        iv.setY(y);
        addToPane(iv);
        temporaryNodes.put(iv, duration);
    }

    /**
     * Draws a ray from one point to another point, extending beyond it towards the edge of the arena.
     * @param sourceX The x-coordinate of the source point.
     * @param sourceY The y-coordinate of the source point.
     * @param targetX The x-coordinate of the target point.
     * @param targetY The y-coordinate of the target point.
     * @param duration The duration in number of frames that the ray will remain on the arena.
     */
    public void drawRay(short sourceX, short sourceY, short targetX, short targetY, int duration) {
        Point2D edgePt = Geometry.intersectBox(sourceX, sourceY, targetX, targetY,
                                                    0, 0, ArenaManager.ARENA_WIDTH, ArenaManager.ARENA_HEIGHT);
        
        Line ray = new Line(sourceX, sourceY, edgePt.getX(), edgePt.getY());
        ray.setStroke(javafx.scene.paint.Color.rgb(255, 255, 0));
        ray.setStrokeWidth(3);
        addToPane(ray);
        temporaryNodes.put(ray, duration);
    }

    /**
     * Draws a specific circle at a specific location.
     * @param centerX The x-coordinate of the circle center.
     * @param centerY The y-coordinate of the circle center.
     * @param radius The radius of the circle.
     * @param duration The duration in number of frames that the circle will remain on the arena.
     */
    public void drawCircle(short centerX, short centerY, short radius, int duration) {
        Circle circle = new Circle(centerX, centerY, radius);
        circle.setFill(Color.rgb(128, 64, 0));
        addToPane(circle);
        temporaryNodes.put(circle, duration);
    }
}

