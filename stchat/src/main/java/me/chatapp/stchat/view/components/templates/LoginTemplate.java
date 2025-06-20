package me.chatapp.stchat.view.components.templates;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import me.chatapp.stchat.view.components.molecules.Section.HeaderSection;
import me.chatapp.stchat.view.components.molecules.Prompt.SignUpPrompt;
import me.chatapp.stchat.view.components.organisms.Background.BackgroundDecorator;
import me.chatapp.stchat.view.components.organisms.Form.LoginForm;

public class LoginTemplate extends StackPane {
    private HeaderSection headerSection;
    private LoginForm loginForm;
    private SignUpPrompt signUpPrompt;
    private VBox mainCard;

    public LoginTemplate(Runnable onSwitchToSignUp) {
        super();
        createTemplate(onSwitchToSignUp);
        addEntranceAnimation();
    }

    private void createTemplate(Runnable onSwitchToSignUp) {
        // Background
        setStyle("-fx-background-color: linear-gradient(135deg, #667eea 0%, #764ba2 100%);");
        BackgroundDecorator.addBackgroundCircles(this);

        // Main card
        mainCard = new VBox(25);
        mainCard.setAlignment(Pos.CENTER);
        mainCard.setPadding(new Insets(50, 40, 50, 40));
        mainCard.setMaxWidth(380);
        mainCard.setStyle(
                "-fx-background-color: rgba(255, 255, 255, 0.95);" +
                        "-fx-background-radius: 20;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 25, 0, 0, 10);"
        );

        // Components
        headerSection = new HeaderSection("Welcome Back", "Sign in to continue to ST Chat");
        loginForm = new LoginForm();
        signUpPrompt = new SignUpPrompt(onSwitchToSignUp);

        // Setup form handlers
        loginForm.setupEnterKeyHandlers();

        // Add components to card
        mainCard.getChildren().addAll(
                headerSection,
                loginForm,
                signUpPrompt
        );

        getChildren().add(mainCard);
    }

    private void addEntranceAnimation() {
        mainCard.setOpacity(0);
        mainCard.setScaleY(0.8);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(600), mainCard);
        fadeIn.setToValue(1.0);

        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(600), mainCard);
        scaleIn.setToY(1.0);

        fadeIn.play();
        scaleIn.play();
    }

    // Getters for accessing components
    public LoginForm getLoginForm() {
        return loginForm;
    }

    public HeaderSection getHeaderSection() {
        return headerSection;
    }

    public SignUpPrompt getSignUpPrompt() {
        return signUpPrompt;
    }
}