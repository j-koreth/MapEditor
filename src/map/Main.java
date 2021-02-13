package map;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Main extends Application {


    Map.TerrainType currentDrawing = Map.TerrainType.Ocean;

    enum Action {Drawing, Move};
    Action currentAction;

    Map.Modifier currentModifier = Map.Modifier.Move;

    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Map");
        Group root = new Group();
        Canvas canvas = new Canvas(980, 600);
        FileChooser fileChooser = new FileChooser();

        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setStroke(Color.WHEAT);
        gc.setLineWidth(2);

        VBox holder = new VBox();
        HBox controls = new HBox();

        Label tileTypes = new Label("Tile Types");
        Button oceanButton = new Button("Ocean");
        Button landButton = new Button("Land");
        Button lakebutton = new Button("Lake");

        Button moveButton = new Button("Move");

        Button saveButton = new Button("Save");
        Button openButton = new Button("Open");

        oceanButton.setOnMouseClicked(event -> {
            currentAction = Action.Drawing;
            currentDrawing = Map.TerrainType.Ocean;
        });

        landButton.setOnMouseClicked(event -> {
            currentAction = Action.Drawing;
            currentDrawing = Map.TerrainType.Land;
        });

        lakebutton.setOnMouseClicked(event -> {
            currentAction = Action.Drawing;
            currentDrawing = Map.TerrainType.Lake;
        });

        moveButton.setOnMouseClicked(event -> {
            currentAction = Action.Move;
        });

        controls.setSpacing(10);

        controls.getChildren().addAll(tileTypes, oceanButton, landButton, lakebutton, moveButton, saveButton, openButton);
        holder.getChildren().addAll(canvas, controls);
        root.getChildren().addAll(holder);

        primaryStage.setScene(new Scene(root));
        primaryStage.show();

        Map map = new Map();
        map.drawMap(gc);

        saveButton.setOnMouseClicked(event -> {
            File file = fileChooser.showSaveDialog(primaryStage);
            try {
                ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
                out.writeObject(map.map);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        openButton.setOnMouseClicked(event -> {
            File file = fileChooser.showOpenDialog(primaryStage);
            try {
                ObjectInputStream out = new ObjectInputStream(new FileInputStream(file));
                map.loadMap((HashMap<Hexagon, HexData>) out.readObject());

                map.clearMap(gc);
                System.out.println(map);
                map.drawMap(gc);

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        root.setOnMouseClicked(event -> {
            Hexagon est = map.pixel_to_Hex(map.layout, new Point(event.getSceneX(), event.getSceneY()));


            switch (currentAction){
                case Drawing:
                    map.getHexData(est).setTerrain(currentDrawing);
                    map.drawHex(map.hex_points(map.layout, est), gc, map.getHexData(est));
                    break;
                case Move:
                    for(Hexagon neighbor : map.getNeighbors(est)){
                        if(map.getHexData(neighbor).traversable){
                            map.getHexData(neighbor).setModifier(currentModifier);
                        }
                        map.drawHex(map.hex_points(map.layout, neighbor), gc, map.getHexData(neighbor));
                    }
            }




        });
    }
}
