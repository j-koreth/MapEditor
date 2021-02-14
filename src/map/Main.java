package map;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Main extends Application {

    enum Action {Drawing, Move}
    Action currentAction;

    HexData.TerrainType currentDrawing = HexData.TerrainType.Ocean;
    HexData.Modifier currentModifier = HexData.Modifier.Move;

    boolean mapChange = false;

    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Map Editor");
        FileChooser fileChooser = new FileChooser();

        Canvas canvas = new Canvas(980, 600);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        Canvas canvas2  = new Canvas(980, 600);
        GraphicsContext gc2 = canvas2.getGraphicsContext2D();

        Group root = new Group();
        VBox holder = new VBox();

        VBox controls = new VBox();
        HBox terrainControls = new HBox();
        HBox actionControls = new HBox();

        controls.getChildren().addAll(terrainControls, actionControls);

        Label tileTypes = new Label("Tile Types");
        Button oceanButton = new Button("Ocean");
        Button landButton = new Button("Land");
        Button lakebutton = new Button("Lake");
        terrainControls.getChildren().addAll(tileTypes, oceanButton, landButton, lakebutton);

        Label actionTypes = new Label("Action Types");
        Button moveButton = new Button("Move");
        actionControls.getChildren().addAll(actionTypes, moveButton);

        terrainControls.setSpacing(10);
        terrainControls.setPadding(new Insets(10));

        actionControls.setSpacing(10);
        actionControls.setPadding(new Insets(10));

        oceanButton.setOnMouseClicked(event -> {
            currentAction = Action.Drawing;
            currentDrawing = HexData.TerrainType.Ocean;
        });

        landButton.setOnMouseClicked(event -> {
            currentAction = Action.Drawing;
            currentDrawing = HexData.TerrainType.Land;
        });

        lakebutton.setOnMouseClicked(event -> {
            currentAction = Action.Drawing;
            currentDrawing = HexData.TerrainType.Lake;
        });

        moveButton.setOnMouseClicked(event -> {
            currentAction = Action.Move;
        });

        MapData mapData = new MapData();

        Map basicMap = new BasicMap(mapData, gc);
        basicMap.selected = true;

        Map terrainMap = new TerrainMap(mapData, gc);
        Map actionMap = new ActionMap(mapData, gc2);

        ArrayList<Map> mapOrder = new ArrayList<>();
        mapOrder.add(basicMap);
        mapOrder.add(terrainMap);
        mapOrder.add(actionMap);

        CheckBox terrainCheck = new CheckBox("Terrain Map");
        CheckBox actionCheck = new CheckBox("Action Map");

        MenuBar menuBar = new MenuBar();
        Menu menu = new Menu("File");
        MenuItem saveItem = new MenuItem("Save");
        MenuItem openItem = new MenuItem("Open");
        menu.getItems().addAll(saveItem, openItem);
        menuBar.getMenus().add(menu);

        BorderPane borderPane = new BorderPane();
        Pane pane = new Pane();
        pane.getChildren().add(canvas);
        pane.getChildren().add(canvas2);
        borderPane.setTop(menuBar);
        borderPane.setCenter(pane);
        borderPane.setBottom(controls);


        holder.getChildren().addAll(menuBar, borderPane);

        VBox mapTypesBox = new VBox(terrainCheck, actionCheck);
        mapTypesBox.setSpacing(20);
        mapTypesBox.setPadding(new Insets(20));

        borderPane.setRight(mapTypesBox);

        root.getChildren().addAll(holder);

        primaryStage.setScene(new Scene(root));
        primaryStage.show();

        basicMap.drawMap();

        terrainCheck.selectedProperty().addListener((ov, old_val, new_val) -> {
            terrainMap.selected = new_val;
            mapChange = true;
        });

        actionCheck.selectedProperty().addListener((ov, old_val, new_val) -> {
            actionMap.selected = new_val;
            mapChange = true;
        });

        saveItem.setOnAction(event -> {
            File file = fileChooser.showSaveDialog(primaryStage);
            try {
                ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
                out.writeObject(mapData.getData());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        openItem.setOnAction(event -> {
            File file = fileChooser.showOpenDialog(primaryStage);
            try {
                ObjectInputStream out = new ObjectInputStream(new FileInputStream(file));

                mapData.setData((HashMap<Hexagon, HexData>) out.readObject());
                mapChange = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        pane.setOnMouseClicked(event -> {
            Hexagon est = mapData.pixel_to_Hex(new Point(event.getX(), event.getY()));

            switch (currentAction){
                case Drawing:
                    mapData.getHexData(est).setTerrain(currentDrawing);
                    mapChange = true;
                    break;
                case Move:
                    mapData.getHexData(est).setModifier(currentModifier);
                    for(Hexagon neighbor : mapData.getNeighbors(est)){
                        if(mapData.getHexData(neighbor).traversable){
                            mapData.getHexData(neighbor).setModifier(currentModifier);
                        }
                    }
                    mapChange = true;
                    break;
            }
        });

        new AnimationTimer(){

            @Override
            public void handle(long now) {
                if(mapChange){
                    gc.clearRect(0,0,1000,1000);
                    gc2.clearRect(0,0,1000,1000);

                    for(Map map : mapOrder){
                        if(map.selected){
                            map.drawMap();
                        }
                    }
                    mapChange = false;
                }
            }
        }.start();
    }
}
