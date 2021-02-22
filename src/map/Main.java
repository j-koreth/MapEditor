package map;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Main extends Application {

    enum Action {TerrainDrawing, Move, PoliticalDrawing}
    Action currentAction;

    HexData.TerrainType currentTerrain = HexData.TerrainType.Ocean;
    State currentState;

    HexData.Modifier currentModifier = HexData.Modifier.Move;

    boolean mapChange = false;

    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Map Editor");
        FileChooser fileChooser = new FileChooser();

        Canvas basicCanvas = new Canvas(980, 600);
        GraphicsContext basicGC = basicCanvas.getGraphicsContext2D();

        Canvas actionCanvas = new Canvas(980, 600);
        GraphicsContext actionGC = actionCanvas.getGraphicsContext2D();

        Canvas politicalCanvas = new Canvas(980, 600);
        GraphicsContext politicalGC = actionCanvas.getGraphicsContext2D();

        Group root = new Group();
        VBox holder = new VBox();

        VBox controls = new VBox();
        HBox terrainControls = new HBox();
        HBox actionControls = new HBox();
        HBox politicalControls = new HBox();

        controls.getChildren().addAll(terrainControls, actionControls, politicalControls);

        Label tileTypes = new Label("Tile Types");
        Button oceanButton = new Button("Ocean");
        Button landButton = new Button("Land");
        Button lakebutton = new Button("Lake");
        terrainControls.getChildren().addAll(tileTypes, oceanButton, landButton, lakebutton);

        Label actionTypes = new Label("Action Types");
        Button moveButton = new Button("Move");
        actionControls.getChildren().addAll(actionTypes, moveButton);

        Label politicalTypes = new Label("States");
        Button romanButton = new Button("Roman Empire");
        Button hanButton = new Button("Han Dynasty");
        Button parthianButton = new Button("Parthian Empire");

        politicalControls.getChildren().addAll(politicalTypes, romanButton, hanButton, parthianButton);


        terrainControls.setSpacing(10);
        terrainControls.setPadding(new Insets(10));

        actionControls.setSpacing(10);
        actionControls.setPadding(new Insets(10));

        politicalControls.setSpacing(10);
        politicalControls.setPadding(new Insets(10));

        oceanButton.setOnMouseClicked(event -> {
            currentAction = Action.TerrainDrawing;
            currentTerrain = HexData.TerrainType.Ocean;
        });

        landButton.setOnMouseClicked(event -> {
            currentAction = Action.TerrainDrawing;
            currentTerrain = HexData.TerrainType.Land;
        });

        lakebutton.setOnMouseClicked(event -> {
            currentAction = Action.TerrainDrawing;
            currentTerrain = HexData.TerrainType.Lake;
        });

        moveButton.setOnMouseClicked(event -> {
            currentAction = Action.Move;
        });


        ArrayList<State> states = new ArrayList<>();
        State romanEmpire = new State("Roman Empire", Color.rgb(102, 2,60));
        State hanDynasty = new State("Han Empire", Color.rgb(0,49,191	));
        State parthianEmpire = new State("Parthian Empire", Color.YELLOW);

        romanButton.setOnMouseClicked(event -> {
            currentAction = Action.PoliticalDrawing;
            currentState = romanEmpire;
        });

        hanButton.setOnMouseClicked(event -> {
            currentAction = Action.PoliticalDrawing;
            currentState = hanDynasty;
        });

        parthianButton.setOnMouseClicked(event -> {
            currentAction = Action.PoliticalDrawing;
            currentState = parthianEmpire;
        });

        MapData mapData = new MapData();

        Map basicMap = new BasicMap(mapData, basicGC);
        basicMap.selected = true;

        Map terrainMap = new TerrainMap(mapData, basicGC);
        Map hexInfoMap = new HexInfoMap(mapData, basicGC);
        Map actionMap = new ActionMap(mapData, actionGC);
        Map politicalMap = new PoliticalMap(mapData, politicalGC, states);

        mapChange = true;

        ArrayList<Map> mapOrder = new ArrayList<>();
        mapOrder.add(basicMap);
        mapOrder.add(terrainMap);
        mapOrder.add(hexInfoMap);
        mapOrder.add(actionMap);
        mapOrder.add(politicalMap);

        CheckBox terrainCheck = new CheckBox("Terrain Map");
        CheckBox hexInfoCheck = new CheckBox("Hex Info Map");
        CheckBox actionCheck = new CheckBox("Action Map");
        CheckBox politicalCheck = new CheckBox("Political Map");

        MenuBar menuBar = new MenuBar();
        Menu menu = new Menu("File");
        MenuItem saveItem = new MenuItem("Save");
        MenuItem openItem = new MenuItem("Open");
        menu.getItems().addAll(saveItem, openItem);
        menuBar.getMenus().add(menu);

        BorderPane borderPane = new BorderPane();
        Pane pane = new Pane();
        pane.getChildren().add(basicCanvas);
        pane.getChildren().add(actionCanvas);
        pane.getChildren().add(politicalCanvas);

        borderPane.setTop(menuBar);
        borderPane.setCenter(pane);
        borderPane.setBottom(controls);

        holder.getChildren().addAll(menuBar, borderPane);

        VBox mapTypesBox = new VBox(terrainCheck, actionCheck, hexInfoCheck, politicalCheck);
        mapTypesBox.setSpacing(20);
        mapTypesBox.setPadding(new Insets(20));

        borderPane.setRight(mapTypesBox);

        root.getChildren().addAll(holder);

        primaryStage.setScene(new Scene(root));
        primaryStage.show();

        terrainCheck.selectedProperty().addListener((ov, old_val, new_val) -> {
            terrainMap.selected = new_val;
            mapChange = true;
        });

        actionCheck.selectedProperty().addListener((ov, old_val, new_val) -> {
            actionMap.selected = new_val;
            mapChange = true;
        });

        hexInfoCheck.selectedProperty().addListener((ov, old_val, new_val) -> {
            hexInfoMap.selected = new_val;
            mapChange = true;
        });

        politicalCheck.selectedProperty().addListener((ov, old_val, new_val) -> {
            politicalMap.selected = new_val;
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

            if(currentAction != null){
                switch (currentAction){
                    case TerrainDrawing:
                        mapData.getHexData(est).setTerrain(currentTerrain);
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
                    case PoliticalDrawing:
                        if(mapData.getHexData(est).ownedState == null){
                            mapData.getHexData(est).setOwnedState(currentState);
                        }
                        else{
                            mapData.getHexData(est).ownedState = null;
                        }
                        mapChange = true;
                        break;
                }
            }
        });

        new AnimationTimer(){
            @Override
            public void handle(long now) {
                if(mapChange){
                    basicGC.clearRect(0,0,1000,1000);
                    actionGC.clearRect(0,0,1000,1000);
                    politicalGC.clearRect(0,0,1000,1000);

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
