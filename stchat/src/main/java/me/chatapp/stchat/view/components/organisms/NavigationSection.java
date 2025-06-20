package me.chatapp.stchat.view.components.organisms;

import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import me.chatapp.stchat.view.components.atoms.STButton;
import me.chatapp.stchat.view.components.atoms.STText;

public class NavigationSection extends VBox {
    private STButton signInButton;

    public NavigationSection(Runnable onSwitchToLogin) {
        super(5);
        setAlignment(Pos.CENTER);

        STText hasAccountText = new STText("Already have an account?", STText.TextType.BODY);
        signInButton = new STButton("Sign In", STButton.ButtonType.LINK);

        signInButton.setOnAction(event -> onSwitchToLogin.run());

        getChildren().addAll(hasAccountText, signInButton);
    }
}
