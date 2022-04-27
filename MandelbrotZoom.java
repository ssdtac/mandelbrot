package com.example.mandelbrot;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.util.ArrayList;
import javafx.scene.layout.VBox;



public class MandelbrotZoom extends Application {

    private int max_iter = 80;
    /* increase this for more clarity in the image
    be aware that the higher this is, the more you need to zoom in to see detail.
    the nature of the coloring algorithm used means that lower numbers are
    generally more pretty when zoomed out, and you need more iterations
    to zoom in further. */

    // window dimensions
    private final int width = 1000, height = 1000;

    // keep these global
    private double zoomPercent = 100;
    private double centerX = 0, centerY = 0;

    // real c, imaginary c
    private double cr, ci;

    @Override
    public void start(Stage stage) {
        stage.setTitle("Mandelbrot Zoom");
        Group root = new Group();
        Scene scene = new Scene(root, width, height, Color.rgb(0, 0, 0));
        stage.setResizable(false);
        stage.setScene(scene);


        //buttons to refresh view, zoom in, zoom out
        // text fields to control centerX and centerY

        Button zoomIn = new Button("+");
        Button zoomOut = new Button("-");
        HBox zoomButtons = new HBox();
        zoomButtons.getChildren().addAll(zoomOut, zoomIn);

        Button refineButton = new Button("refine image");
        Button resetButton = new Button("reset image");
        TextField maxIterControl = new TextField();

        Button up = new Button("^");
        Button down = new Button("v");
        Button left = new Button("<");
        Button right = new Button(">");

        BorderPane directions = new BorderPane();


        BorderPane.setAlignment(up, Pos.TOP_CENTER);
        BorderPane.setAlignment(down, Pos.BASELINE_CENTER);
        directions.setTop(up);
        directions.setBottom(down);

        directions.setLeft(left);
        directions.setRight(right);

        // vertical box to hold all the items
        VBox box = new VBox();
        //horizontal box to hold the direction buttons
        HBox buttons = new HBox();

        maxIterControl.setPromptText("Set max iter (int)");

        buttons.getChildren().addAll(refineButton, resetButton);
        box.getChildren().addAll(buttons, directions, maxIterControl, zoomButtons);

        //How zoomed in the set should be and the cartesian point that should be centered in the image
        ImageView iv = new ImageView(createSet(width, height, centerX, centerY, zoomPercent));

        root.getChildren().add(iv);
        root.getChildren().addAll(createIntervalLines());
        root.getChildren().add(box);

        refineButton.setOnAction(e -> {
            max_iter = maxIterControl.getText().isBlank() ? max_iter : Integer.parseInt(maxIterControl.getText());
            ImageView view = new ImageView(createSet(width, height, centerX, centerY, zoomPercent));
            root.getChildren().set(0, view);
        });

        resetButton.setOnAction(e -> {
            max_iter = 80;
            centerX = 0;
            centerY = 0;
            zoomPercent = 100;
            ImageView view = new ImageView(createSet(width, height, centerX, centerY, zoomPercent));
            root.getChildren().set(0, view);
        });


        up.setOnAction(e ->{
            centerY += (100.0 / zoomPercent) * 0.1;
            ImageView view = new ImageView(createSet(width, height, centerX, centerY, zoomPercent));
            root.getChildren().set(0, view);
        });

        down.setOnAction(e ->{
            centerY -= (100.0 / zoomPercent) * 0.1;
            ImageView view = new ImageView(createSet(width, height, centerX, centerY, zoomPercent));
            root.getChildren().set(0, view);
        });

        left.setOnAction(e ->{
            centerX -= (100.0 / zoomPercent) * 0.1;
            ImageView view = new ImageView(createSet(width, height, centerX, centerY, zoomPercent));
            root.getChildren().set(0, view);
        });

        right.setOnAction(e ->{
            centerX += (100.0 / zoomPercent) * 0.1;
            ImageView view = new ImageView(createSet(width, height, centerX, centerY, zoomPercent));
            root.getChildren().set(0, view);
        });

        zoomIn.setOnAction(e -> {
            zoomPercent += zoomPercent/100 * 10;
            ImageView view = new ImageView(createSet(width, height, centerX, centerY, zoomPercent));
            root.getChildren().set(0, view);
        });

        zoomOut.setOnAction(e -> {
            zoomPercent -= zoomPercent/100 * 10;
            ImageView view = new ImageView(createSet(width, height, centerX, centerY, zoomPercent));
            root.getChildren().set(0, view);
        });


        stage.show();
    }

    private WritableImage createSet(int width, int height, double centerX, double centerY, double zoomPercent) {

        //Mandelbrot set's bounds
        double cr = -2;
        double ci = 2;

        WritableImage set = new WritableImage(width, height);
        PixelWriter writer = set.getPixelWriter();


        /* Mandelbrot set's max magnitude is 2, so the full possible range is < 4. Iterate per pixel by
        the set's total width/height divided by the number of pixels you have (width/height of the image)

        When you zoom in, each point in the mandelbrot set is represented by more pixels, so you need to increment ci
        and cr by a larger number;
         */

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                writer.setColor(i, j, xyColor(cr + centerX + ((zoomPercent-100)/zoomPercent * 2), ci + centerY - ((zoomPercent-100)/zoomPercent * 2)));
                ci -= 4.0 / width * 100 / zoomPercent ;
            }

            cr += 4.0 / width * 100 / zoomPercent ;
            ci = 2;
        }
        return set;
    }

    private Color xyColor(double cr, double ci) {
        int i = iterate(cr, ci);
        double h = i==max_iter? 0: (1 - ((double)i/max_iter))*360;
        double s = i < max_iter ? (double)i / max_iter: 0;
        double b = i < max_iter ? 1 : 0;
        return Color.hsb(h, s, b);
    }

    private int iterate(double realC, double imagC) {
        int iterations = 0;
        double zr = 0.0;
        double zi = 0.0;
        double zrSq = 0.0;
        double ziSq = 0.0;
        double temp;

        // the Mandelbrot set is defined as being convergent UNDER 2, so the complex
        // number's distance from the origin must be less than 2.
        // more simply, the while loop tests the pythagorean theorem ensuring the hypotenuse is less than 2.
        while(iterations < max_iter && zrSq + ziSq < 4) {
            temp = zrSq - ziSq + realC;
            zi = (2 * zr * zi) + imagC;
            zr = temp;
            zrSq = zr * zr;
            ziSq = zi * zi;
            iterations++;
        }
        return iterations;
    }

    private ArrayList<Line> createIntervalLines() {
        ArrayList<Line> intervalLines = new ArrayList<>();

        //Add X and Y axes at where (0,0) would be on the cartesian plane
        Line xAxis = new Line(0, height / 2 + centerY * height / 4,
                width, height / 2 + centerY * height / 4);

        Line yAxis = new Line(width / 2 - centerX * width / 4, 0,
                width / 2 - centerX * width / 4, height);
        xAxis.setStroke(Color.RED);
        yAxis.setStroke(Color.RED);


        for (int i = -2; i <= 2; i ++) {
            if (i != 0) {

                //X intervals
                //TODO: use variables for these so that you can more easily edit them, have them be edited by other methods
                intervalLines.add(new Line((double)width / 2 - centerX * width / 4 - (double)i * width / 4,
                        (double)height / 2 + centerY * height / 4 - 10,
                        (double)width / 2 - centerX * width / 4 - (double)i * width / 4, (double)height / 2 + centerY * height / 4 + 10));

                //Y intervals
                intervalLines.add(new Line((double)width / 2 - centerX * width / 4 - 10,
                        (double)height / 2 + centerY * height / 4 - (double)i * height / 4,
                        (double)width / 2 - centerX * width / 4 + 10, (double)height / 2 + centerY * height / 4 - (double)i * height / 4));

            }
        }

        for (Line line : intervalLines)
            line.setStroke(Color.RED);

        intervalLines.add(xAxis);
        intervalLines.add(yAxis);

        return intervalLines;
    }

    public static void main(String[] args) {
        launch();
    }
}


/*TODO - Add a little margin around the edges so you can see the ticks at magnitude 2
    Make grid lines work with zoom, not just pan
    Make zooming work better, zoom into middle of screen not the top left corner
    Add click panning (not inherently necessary, could be nice to have)
    Eventually make reloading dynamic so you can actively zoom in (event listeners?)
 */

