package me.chatapp.stchat.view.components.organisms.Background;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import me.chatapp.stchat.view.components.atoms.Circle.STCircle;

public class BackgroundDecoration {
    public static void addToRoot(StackPane root) {
        STCircle circle1 = new STCircle(90, Color.WHITE, 0.1);
        circle1.setTranslateX(-160);
        circle1.setTranslateY(-250);

        STCircle circle2 = new STCircle(70, Color.WHITE, 0.1);
        circle2.setTranslateX(190);
        circle2.setTranslateY(-180);

        STCircle circle3 = new STCircle(50, Color.WHITE, 0.1);
        circle3.setTranslateX(-190);
        circle3.setTranslateY(300);

        STCircle circle4 = new STCircle(110, Color.WHITE, 0.05);
        circle4.setTranslateX(160);
        circle4.setTranslateY(280);

        root.getChildren().addAll(circle1, circle2, circle3, circle4);
    }
}
