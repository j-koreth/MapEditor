package map;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
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
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Main extends Application {

    enum Action {TerrainDrawing, Move, PoliticalDrawing, BuildingDraw, Selector}
    Action currentAction;

    HexType currentType;

    boolean mapChange = true;

    public static void main(String[] args) {
        launch(args);
    }

    public void addType(HBox controls, HexType type, Action action){
        Button button = new Button(type.getName());
        button.setOnMouseClicked(event -> {
            currentAction = action;
            currentType = type;
        });

        controls.getChildren().add(button);
    }

    public void setUpTerrains(HBox terrainControls){
        addType(terrainControls, new Terrain("Ocean", "#2273B8"), Action.TerrainDrawing);
        addType(terrainControls, new Terrain("Land", 1, "#F2CB84"), Action.TerrainDrawing);
        addType(terrainControls, new Terrain("Lake", "#8CC4DB"), Action.TerrainDrawing);
        addType(terrainControls, new Terrain("Forest", 2, "#558B29"), Action.TerrainDrawing);
        addType(terrainControls, new Terrain("Desert", 4, "#DE8D3A"), Action.TerrainDrawing);
    }

    public void setUpStates(HBox politicalControls){
        addType(politicalControls, new State("Roman Empire", "#66023C") ,Action.PoliticalDrawing);
        addType(politicalControls, new State("Han Empire", "#0031BF") ,Action.PoliticalDrawing);
        addType(politicalControls, new State("Parthian Empire", "#9ACD32") ,Action.PoliticalDrawing);
    }

    public void setUpBuildings(HBox buildingControls){
        addType(buildingControls, new Building("Trade Post"), Action.BuildingDraw);
        addType(buildingControls, new Building("City", Building.Shape.Circle), Action.BuildingDraw);
    }

    public void setUpControls(HBox controls){
        controls.setSpacing(10);
        controls.setPadding(new Insets(10));
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Map Editor");

        VBox holder = new VBox();
        BorderPane borderPane = new BorderPane();

        //Basic Canvas holding Terrain, Basic Map, and HexInfo Map
        Canvas basicCanvas = new Canvas(980, 600);
        GraphicsContext basicGC = basicCanvas.getGraphicsContext2D();

        //Action Canvas holding movement Actions
        Canvas actionCanvas = new Canvas(980, 600);
        GraphicsContext actionGC = actionCanvas.getGraphicsContext2D();

        //Political Canvas holding States
        Canvas politicalCanvas = new Canvas(980, 600);
        GraphicsContext politicalGC = politicalCanvas.getGraphicsContext2D();

        //Bottom Controls
        VBox controls = new VBox();
        HBox terrainControls = new HBox(new Label("Terrain Types"));
        HBox politicalControls = new HBox(new Label("States"));
        HBox buildingControls = new HBox(new Label("Building Types"));
        HBox actionControls = new HBox();

        controls.getChildren().addAll(terrainControls, actionControls, politicalControls, buildingControls);

        //Terrain
        setUpTerrains(terrainControls);
        setUpControls(terrainControls);

        //Action Buttons
        Label actionTypes = new Label("Action Types");
        Button moveButton = new Button("Move");
        actionControls.getChildren().addAll(actionTypes, moveButton);
        setUpControls(actionControls);

        //Painting States Buttons
        setUpStates(politicalControls);
        setUpControls(politicalControls);

        //Painting Trading Nodes Buttons
        setUpBuildings(buildingControls);
        setUpControls(buildingControls);

        //Action Buttons
        moveButton.setOnMouseClicked(event -> {
            currentAction = Action.Move;
        });

        MapData mapData = new MapData();

        //Always Will be rendered basically
        Map basicMap = new BasicMap(mapData, basicGC);
        basicMap.selected = true;

        TerrainMap terrainMap = new TerrainMap(mapData, basicGC);
        HexInfoMap hexInfoMap = new HexInfoMap(mapData, basicGC);
        ActionMap actionMap = new ActionMap(mapData, actionGC);
        PoliticalMap politicalMap = new PoliticalMap(mapData, politicalGC);
        BuildingMap buildingMap = new BuildingMap(mapData, basicGC);

        ArrayList<Map> mapOrder = new ArrayList<>();
        mapOrder.add(basicMap);
        mapOrder.add(terrainMap);
        mapOrder.add(buildingMap);
        mapOrder.add(actionMap);
        mapOrder.add(politicalMap);
        mapOrder.add(hexInfoMap);

        //Map Type Checkboxes
        CheckBox terrainCheck = new CheckBox("Terrain Map");
        CheckBox hexInfoCheck = new CheckBox("Hex Info Map");
        CheckBox actionCheck = new CheckBox("Action Map");
        CheckBox politicalCheck = new CheckBox("Political Map");
        CheckBox buildingCheck = new CheckBox("Building Map");

        //MenuBar
        MenuBar menuBar = new MenuBar();
        Menu menu = new Menu("File");
        MenuItem saveItem = new MenuItem("Save");
        MenuItem openItem = new MenuItem("Open");
        menu.getItems().addAll(saveItem, openItem);
        menuBar.getMenus().add(menu);

        //Adding Canvases to BorderPane
        Pane pane = new Pane();
        pane.getChildren().addAll(basicCanvas, actionCanvas, politicalCanvas);

        //Adding Checkboxes to a VBox on Right
        VBox mapTypesBox = new VBox(terrainCheck, actionCheck, hexInfoCheck, politicalCheck, buildingCheck);
        mapTypesBox.setSpacing(20);
        mapTypesBox.setPadding(new Insets(20));

        //Setting BorderPane
        borderPane.setTop(menuBar);
        borderPane.setCenter(pane);
        borderPane.setBottom(controls);
        borderPane.setRight(mapTypesBox);

        //Adding MenuBar and BorderPane to a VBox Holder
        holder.getChildren().addAll(menuBar, borderPane);

        //Map Checkbox Listeners
        terrainCheck.selectedProperty().addListener((ov, old, newValue) -> {
            terrainMap.selected = newValue;
            mapChange = true;
        });

        actionCheck.selectedProperty().addListener((ov, old, newValue) -> {
            actionMap.selected = newValue;
            mapChange = true;
        });

        hexInfoCheck.selectedProperty().addListener((ov, old, newValue) -> {
            hexInfoMap.selected = newValue;
            mapChange = true;
        });

        politicalCheck.selectedProperty().addListener((ov, old, newValue) -> {
            politicalMap.selected = newValue;
            mapChange = true;
        });

        buildingCheck.selectedProperty().addListener((ov, old, newValue) -> {
            buildingMap.selected = newValue;
            mapChange = true;
        });

        FileChooser fileChooser = new FileChooser();

        //Save Data
        saveItem.setOnAction(event -> {
            File file = fileChooser.showSaveDialog(primaryStage);
            try {
                ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
                out.writeObject(mapData.getData());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        //Loading Data
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

        for(HashMap.Entry<Hexagon, HexData> entry : mapData.data.entrySet()){
            entry.getValue().setTerrain(new Terrain("Ocean", "#2273B8"));
        }

        pane.setOnMouseClicked(event -> {
            Hexagon est = mapData.pixelToHex(new Point(event.getX(), event.getY()));

            if(currentAction != null){
                switch (currentAction){
                    case TerrainDrawing:
                        mapData.getHexData(est).setTerrain((Terrain) currentType);
                        break;
                    case Move:
                        actionMap.setMovable(est, 4);
                        break;
                    case PoliticalDrawing:
                        mapData.getHexData(est).setState((State) currentType);
                        break;
                    case BuildingDraw:
                        mapData.getHexData(est).setBuilding((Building) currentType);
                        System.out.println(est);
                        System.out.println(mapData.getHexData(est));
                        break;
                }
                mapChange = true;

            }
        });

        primaryStage.setScene(new Scene(holder));
        primaryStage.show();

        new AnimationTimer(){
            @Override
            public void handle(long now) {
                if(mapChange){
                    basicGC.clearRect(0,0, basicCanvas.getWidth(), basicCanvas.getHeight());
                    actionGC.clearRect(0,0, actionCanvas.getWidth(), actionCanvas.getHeight());
                    politicalGC.clearRect(0,0, politicalCanvas.getWidth(), politicalCanvas.getHeight());

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
