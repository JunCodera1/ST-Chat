package me.chatapp.stchat.view.components.pages;

import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import me.chatapp.stchat.api.SocketClient;
import me.chatapp.stchat.util.AnimationUtil;
import me.chatapp.stchat.util.DisplayUtil;
import me.chatapp.stchat.util.ValidateUtil;

import java.util.logging.Logger;

import static me.chatapp.stchat.util.CSSUtil.*;
import static me.chatapp.stchat.util.DisplayUtil.createBackButton;
import static me.chatapp.stchat.util.DisplayUtil.createChangePasswordButton;

public class ChangePassword {
    private static final Logger LOGGER = Logger.getLogger(ChangePassword.class.getName());


    private final Stage stage;
    private final Runnable onGoBack;

    // UI Components
    private TextField emailField;
    private PasswordField currentPasswordField;
    private PasswordField newPasswordField;
    private PasswordField confirmPasswordField;
    private Text statusMessage;
    private Text passwordStrength;

    public ChangePassword(Runnable onGoBack) {
        this.stage = new Stage();
        this.onGoBack = onGoBack;
        setupUI();
    }

    private void setupUI() {
        StackPane root = createMainContainer();
        VBox card = createMainCard();

        root.getChildren().add(card);

        Scene scene = new Scene(root, 520, 750);
        configureStage(scene);

        AnimationUtil.addEntranceAnimation(card);
    }

    private StackPane createMainContainer() {
        StackPane root = new StackPane();
        root.setStyle(GRADIENT_BACKGROUND);
        DisplayUtil.createBackgroundCircles(root);
        return root;
    }

    private VBox createMainCard() {
        VBox card = new VBox(20);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(40, 35, 40, 35));
        card.setMaxWidth(400);
        card.setStyle(
                "-fx-background-color: rgba(255, 255, 255, 0.95);" +
                        "-fx-background-radius: 20;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 25, 0, 0, 10);"
        );

        card.getChildren().addAll(
                createLogoSection(),
                createTitleSection(),
                createFormSection(),
                passwordStrength,
                createButtonSection(),
                statusMessage
        );

        return card;
    }

    private StackPane createLogoSection() {
        StackPane logoContainer = new StackPane();
        Circle logoCircle = new Circle(35);
        logoCircle.setFill(Color.web("#667eea"));
        logoCircle.setEffect(new DropShadow(15, Color.web("#667eea", 0.3)));

        Text logoText = new Text("🔒");
        logoText.setFont(Font.font(30));
        logoContainer.getChildren().addAll(logoCircle, logoText);

        return logoContainer;
    }

    private VBox createTitleSection() {
        VBox titleSection = new VBox(5);
        titleSection.setAlignment(Pos.CENTER);

        Text title = new Text("Change Password");
        title.setFont(Font.font("System", FontWeight.BOLD, 26));
        title.setFill(Color.web("#2d3748"));

        Text subtitle = new Text("Update your account password");
        subtitle.setFont(Font.font("System", FontWeight.NORMAL, 14));
        subtitle.setFill(Color.web("#718096"));

        titleSection.getChildren().addAll(title, subtitle);
        return titleSection;
    }

    private VBox createFormSection() {
        VBox formContainer = new VBox(18);
        formContainer.setAlignment(Pos.CENTER);

        VBox emailContainer = createEmailContainer();
        VBox currentPasswordContainer = createPasswordContainer("Current Password", "Enter your current password");
        VBox newPasswordContainer = createPasswordContainer("New Password", "Enter your new password");
        VBox confirmPasswordContainer = createPasswordContainer("Confirm New Password", "Confirm your new password");

        // Extract fields from containers
        emailField = (TextField) emailContainer.getChildren().get(1);
        currentPasswordField = (PasswordField) currentPasswordContainer.getChildren().get(1);
        newPasswordField = (PasswordField) newPasswordContainer.getChildren().get(1);
        confirmPasswordField = (PasswordField) confirmPasswordContainer.getChildren().get(1);

        // Initialize password strength indicator
        passwordStrength = new Text();
        passwordStrength.setFont(Font.font("System", FontWeight.NORMAL, 12));

        // Initialize status message
        statusMessage = new Text();
        statusMessage.setFont(Font.font("System", FontWeight.MEDIUM, 12));
        statusMessage.setFill(Color.web("#e53e3e"));

        formContainer.getChildren().addAll(
                emailContainer,
                currentPasswordContainer,
                newPasswordContainer,
                confirmPasswordContainer
        );

        setupFormInteractions();
        return formContainer;
    }

    private VBox createEmailContainer() {
        VBox container = new VBox(8);

        Label label = new Label("Email");
        label.setFont(Font.font("System", FontWeight.MEDIUM, 14));
        label.setTextFill(Color.web("#4a5568"));

        TextField textField = new TextField();
        textField.setPromptText("Enter your email address");
        textField.setPrefHeight(45);
        textField.setStyle(FIELD_STYLE);

        container.getChildren().addAll(label, textField);
        return container;
    }

    private VBox createPasswordContainer(String labelText, String promptText) {
        VBox container = new VBox(8);

        Label label = new Label(labelText);
        label.setFont(Font.font("System", FontWeight.MEDIUM, 14));
        label.setTextFill(Color.web("#4a5568"));

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText(promptText);
        passwordField.setPrefHeight(45);
        passwordField.setStyle(FIELD_STYLE);

        container.getChildren().addAll(label, passwordField);
        return container;
    }

    private VBox createButtonSection() {
        VBox buttonSection = new VBox(15);
        buttonSection.setAlignment(Pos.CENTER);

        Button changePasswordButton = createChangePasswordButton();
        Button backButton = createBackButton();

        setupButtonEvents(changePasswordButton, backButton);
        AnimationUtil.addButtonHoverEffects(changePasswordButton, backButton);

        buttonSection.getChildren().addAll(changePasswordButton, backButton);
        return buttonSection;
    }

    private void setupFormInteractions() {
        addFieldFocusEffects(emailField, currentPasswordField, newPasswordField, confirmPasswordField);
        AnimationUtil.addPasswordStrengthIndicator(newPasswordField, passwordStrength);

        // Enter key support
        confirmPasswordField.setOnAction(event -> handlePasswordChange());
    }

    private void setupButtonEvents(Button changePasswordButton, Button backButton) {
        changePasswordButton.setOnAction(event -> handlePasswordChange());

        backButton.setOnAction(event -> {
            stage.close();
            onGoBack.run();
        });
    }

    private void configureStage(Scene scene) {
        stage.setTitle("ST Chat - Change Password");
        stage.setScene(scene);
        stage.setResizable(false);
    }

    private void addFieldFocusEffects(javafx.scene.control.Control... fields) {
        for (javafx.scene.control.Control field : fields) {
            field.focusedProperty().addListener((obs, oldVal, newVal) ->
                    field.setStyle(newVal ? FOCUS_STYLE : FIELD_STYLE));
        }
    }

    private void handlePasswordChange() {
        String email = emailField.getText();
        String currentPassword = currentPasswordField.getText();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        String error = ValidateUtil.getValidationError(email, currentPassword, newPassword, confirmPassword);
        if (error != null) {
            showMessage(error, Color.web("#e53e3e"));
            return;
        }

        performPasswordChange(email, currentPassword, newPassword);
    }

    private void performPasswordChange(String email, String currentPassword, String newPassword) {
        new Thread(() -> {
            try {
                SocketClient client = new SocketClient("localhost", 8080);
                String request = new org.json.JSONObject()
                        .put("type", "CHANGE_PASSWORD")
                        .put("email", email)
                        .put("currentPassword", currentPassword)
                        .put("newPassword", newPassword)
                        .toString();

                client.send(request);
                String response = client.receive();

                handlePasswordChangeResponse(response);
                client.close();

            } catch (Exception e) {
                javafx.application.Platform.runLater(() ->
                        showMessage("Unable to connect to server.", Color.web("#e53e3e"))
                );
                LOGGER.severe("Change password socket error: " + e.getMessage());
            }
        }).start();
    }

    private void handlePasswordChangeResponse(String response) {
        org.json.JSONObject resJson = new org.json.JSONObject(response);

        javafx.application.Platform.runLater(() -> {
            if ("success".equalsIgnoreCase(resJson.optString("status"))) {
                handleSuccessfulPasswordChange();
            } else {
                String message = resJson.optString("message", "Password change failed.");
                showMessage(message, Color.web("#e53e3e"));
            }
        });
    }

    private void handleSuccessfulPasswordChange() {
        showMessage("Password changed successfully! Please check your email for confirm a new password", Color.web("#38a169"));
        clearAllFields();
        scheduleAutoClose();
    }

    private void clearAllFields() {
        emailField.clear();
        currentPasswordField.clear();
        newPasswordField.clear();
        confirmPasswordField.clear();
    }

    private void scheduleAutoClose() {
        new Thread(() -> {
            try {
                Thread.sleep(2000);
                javafx.application.Platform.runLater(() -> {
                    stage.close();
                    onGoBack.run();
                });
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    private void showMessage(String message, Color color) {
        statusMessage.setText(message);
        statusMessage.setFill(color);

        ScaleTransition messageScale = new ScaleTransition(Duration.millis(100), statusMessage);
        messageScale.setToX(1.1);
        messageScale.setAutoReverse(true);
        messageScale.setCycleCount(2);
        messageScale.play();
    }

    public void show() {
        stage.show();
    }
}