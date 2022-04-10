package com.example.mandelbrot;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

import java.util.ArrayList;

public class MandelbrotZoom extends Application {


    // increase this for more clarity in the image
    // be aware that the higher this is, the more you need to zoom in to see detail.
    // the nature of the coloring algorithm used means that lower numbers are
    // generally more pretty when zoomed out, and you need more iterations
    // to zoom in further.
    private final int max_iter = 40;

    // real c, imaginary c
    private double cr, ci;

    @Override
    public void start(Stage stage) {

        double height = 1000;
        double width = 1000;

        stage.setTitle("Mandelbrot Zoom");
        Group root = new Group();
        Scene scene = new Scene(root, width, height, Color.rgb(0, 0, 0));
        stage.setScene(scene);

        //How zoomed in the set should be and the cartesian point that should be centered in the image
        double zoomPercent = 100;
        double centerX = 0;
        double centerY = 0;

        ImageView iv = new ImageView(createSet(width, height, centerX, centerY, zoomPercent));
        root.getChildren().add(iv);

        ArrayList<Line> intervalLines = new ArrayList<>();

        //Add X and Y axes at where (0,0) would be on the cartesian plane
        Line xAxis = new Line(0, height / 2 + centerY * height / 4,
                width, height / 2 + centerY * height / 4);

        Line yAxis = new Line(width / 2 - centerX * width / 4, 0,
                width / 2 - centerX * width / 4, height);
        yAxis.setStroke(Color.RED);


        for (int i = -2; i <= 2; i ++) {
            if (i != 0) {

                //X intervals
                intervalLines.add(new Line(width / 2 - centerX * width / 4 - i * width / 4,
                        height / 2 + centerY * height / 4 - 10,
                        width / 2 - centerX * width / 4 - i * width / 4, height / 2 + centerY * height / 4 + 10));

                //Y intervals
                intervalLines.add(new Line(width / 2 - centerX * width / 4 - 10,
                        height / 2 + centerY * height / 4 - i * height / 4,
                        width / 2 - centerX * width / 4 + 10,height / 2 + centerY * height / 4 - i * height / 4));

            }
        }

        intervalLines.add(xAxis);
        intervalLines.add(yAxis);

        for (Line line : intervalLines)
            line.setStroke(Color.RED);

        root.getChildren().addAll(intervalLines);


        stage.show();
    }

    private WritableImage createSet(double width, double height, double centerX, double centerY, double zoomPercent) {

        //Mandelbrot set's bounds
        double cr = -2;
        double ci = 2;

        WritableImage set = new WritableImage((int) width, (int) height);
        PixelWriter writer = set.getPixelWriter();


        /* Mandelbrot set's max magnitude is 2, so the full possible range is < 4. Iterate per pixel by
        the set's total width/height divided by the number of pixels you have (width/height of the image)

        When you zoom in, each point in the mandelbrot set is represented by more pixels, so you need to increment ci
        and cr by a larger number;
         */

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                writer.setColor(i, j,
                        xyColor(cr + centerX , ci + centerY));
                ci -= 4.0 / width * zoomPercent / 100;
            }

            cr += 4.0 / width * zoomPercent / 100;
            ci = 2;
        }
        return set;
    }

    private Color xyColor(double cr, double ci) {
        int i = iterate(cr, ci);
        double h = i==max_iter? 0: ((double)i/max_iter)*360;
        double s = i <= max_iter ? (double)i / max_iter: 1;
        double b = i < max_iter ? 1: 0;
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

    public static void main(String[] args) {
        launch();
    }
}


//TODO - Add a little margin around the edges so you can see the ticks at magnitude 2