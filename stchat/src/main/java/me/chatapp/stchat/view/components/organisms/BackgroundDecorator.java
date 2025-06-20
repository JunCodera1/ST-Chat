package me.chatapp.stchat.view.components.organisms;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class BackgroundDecorator {
    public static void addBackgroundCircles(StackPane root) {
        Circle circle1 = new Circle(80);
        circle1.setFill(Color.web("#ffffff", 0.1));
        circle1.setTranslateX(-150);
        circle1.setTranslateY(-200);

        Circle circle2 = new Circle(60);
        circle2.setFill(Color.web("#ffffff", 0.1));
        circle2.setTranslateX(180);
        circle2.setTranslateY(-150);

        Circle circle3 = new Circle(40);
        circle3.setFill(Color.web("#ffffff", 0.1));
        circle3.setTranslateX(-180);
        circle3.setTranslateY(250);

        Circle circle4 = new Circle(100);
        circle4.setFill(Color.web("#ffffff", 0.05));
        circle4.setTranslateX(150);
        circle4.setTranslateY(200);

        root.getChildren().addAll(circle1, circle2, circle3, circle4);
    }
}