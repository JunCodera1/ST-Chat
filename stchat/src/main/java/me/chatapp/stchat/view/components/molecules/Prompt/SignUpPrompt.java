package me.chatapp.stchat.view.components.molecules.Prompt;

import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import me.chatapp.stchat.view.components.atoms.Button.LinkButton;

public class SignUpPrompt extends VBox {
    private LinkButton signUpButton;

    public SignUpPrompt(Runnable onSignUpClick) {
        super(5);
        setAlignment(Pos.CENTER);

        createComponents(onSignUpClick);
    }

    private void createComponents(Runnable onSignUpClick) {
        Text noAccountText = new Text("Don't have an account?");
        noAccountText.setFont(Font.font("System", FontWeight.NORMAL, 13));
        noAccountText.setFill(Color.web("#718096"));

        signUpButton = new LinkButton("Create Account");
        signUpButton.setStyle(signUpButton.getStyle() +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 14;"
        );

        signUpButton.setOnAction(e -> onSignUpClick.run());

        getChildren().addAll(noAccountText, signUpButton);
    }

    public LinkButton getSignUpButton() {
        return signUpButton;
    }
}
