package com.example.mandelbrot;

import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.image.*;
import javafx.scene.paint.Color;
import javafx.stage.*;

public class HelloApplication extends Application {
    // increase this for more clarity in the image
    private final int max_iter = 120;

    //global counter to help with coloring (maybe, idk yet)
    private int counter = 0;

    // real c, imaginary c
    private double cr, ci;

    @Override
    public void start(Stage stage) {
        stage.setTitle("Mandelbrot Zoom");
        Group root = new Group();
        Scene scene = new Scene(root, 800, 800, Color.rgb(255, 0, 0));
        stage.setScene(scene);
        ImageView iv = new ImageView(createSet(800, 800));
        root.getChildren().add(iv);
        //TODO: add zoom functionality (method or class?) + add colors

        stage.show();
    }

    private WritableImage createSet(int width, int height) {
        double cr = -2.0;
        double ci = 2.0;
        WritableImage set = new WritableImage(width, height);
        PixelWriter writer = set.getPixelWriter();

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                writer.setColor(i, j, xyColor(cr, ci));
                ci -= 4.0/width;
            }
            cr += 4.0/width;
            ci = 2;
        }
        return set;
    }

    private Color xyColor(double cr, double ci) {
        int i = iterate(cr, ci);
        i = i < max_iter ? i % 255: 255;
        return Color.rgb(i, i, i);
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