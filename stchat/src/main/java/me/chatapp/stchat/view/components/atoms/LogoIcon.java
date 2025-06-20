package me.chatapp.stchat.view.components.atoms;

import javafx.geometry.Pos;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class LogoIcon extends StackPane {
    public LogoIcon() {
        super();
        createLogo();
    }

    private void createLogo() {
        setAlignment(Pos.CENTER);

        Circle logoCircle = new Circle(35);
        logoCircle.setFill(Color.web("#667eea"));
        logoCircle.setEffect(new DropShadow(15, Color.web("#667eea", 0.3)));

        Text logoText = new Text("💬");
        logoText.setFont(Font.font(30));

        getChildren().addAll(logoCircle, logoText);
    }
}
