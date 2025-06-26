package me.chatapp.stchat.view.components.pages;

import javafx.animation.FadeTransition;
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
import me.chatapp.stchat.network.SocketClient;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

public class ChangePassword {

    private static final Logger LOGGER = Logger.getLogger(ChangePassword.class.getName());
    private static final String GRADIENT_BACKGROUND = "-fx-background-color: linear-gradient(135deg, #667eea 0%, #764ba2 100%);";
    private static final String FIELD_STYLE = "-fx-background-color: #f7fafc;" +
            "-fx-border-color: #e2e8f0;" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 12;" +
            "-fx-background-radius: 12;" +
            "-fx-padding: 0 15;" +
            "-fx-font-size: 14;";
    private static final String FOCUS_STYLE = "-fx-background-color: #ffffff;" +
            "-fx-border-color: #667eea;" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 12;" +
            "-fx-background-radius: 12;" +
            "-fx-padding: 0 15;" +
            "-fx-font-size: 14;" +
            "-fx-effect: dropshadow(gaussian, rgba(102,126,234,0.3), 10, 0, 0, 2);";

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

        addEntranceAnimation(card);
    }

    private StackPane createMainContainer() {
        StackPane root = new StackPane();
        root.setStyle(GRADIENT_BACKGROUND);
        createBackgroundCircles(root);
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
        addButtonHoverEffects(changePasswordButton, backButton);

        buttonSection.getChildren().addAll(changePasswordButton, backButton);
        return buttonSection;
    }

    private Button createChangePasswordButton() {
        Button button = new Button("Change Password");
        button.setPrefWidth(320);
        button.setPrefHeight(50);
        button.setFont(Font.font("System", FontWeight.BOLD, 16));
        button.setStyle(
                GRADIENT_BACKGROUND +
                        "-fx-text-fill: white;" +
                        "-fx-background-radius: 25;" +
                        "-fx-cursor: hand;" +
                        "-fx-effect: dropshadow(gaussian, rgba(102,126,234,0.4), 15, 0, 0, 5);"
        );
        return button;
    }

    private Button createBackButton() {
        Button button = new Button("← Back to Profile");
        button.setPrefWidth(320);
        button.setPrefHeight(45);
        button.setFont(Font.font("System", FontWeight.MEDIUM, 14));
        button.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-border-color: #667eea;" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 22;" +
                        "-fx-text-fill: #667eea;" +
                        "-fx-cursor: hand;"
        );
        return button;
    }

    private void setupFormInteractions() {
        addFieldFocusEffects(emailField, currentPasswordField, newPasswordField, confirmPasswordField);
        addPasswordStrengthIndicator(newPasswordField, passwordStrength);

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

    private void createBackgroundCircles(StackPane root) {
        Circle[] circles = {
                createBackgroundCircle(90, -160, -250, 0.1),
                createBackgroundCircle(70, 190, -180, 0.1),
                createBackgroundCircle(50, -190, 280, 0.1),
                createBackgroundCircle(110, 160, 250, 0.05)
        };

        root.getChildren().addAll(circles);
    }

    private Circle createBackgroundCircle(double radius, double translateX, double translateY, double opacity) {
        Circle circle = new Circle(radius);
        circle.setFill(Color.web("#ffffff", opacity));
        circle.setTranslateX(translateX);
        circle.setTranslateY(translateY);
        return circle;
    }

    private void addFieldFocusEffects(javafx.scene.control.Control... fields) {
        for (javafx.scene.control.Control field : fields) {
            field.focusedProperty().addListener((obs, oldVal, newVal) ->
                    field.setStyle(newVal ? FOCUS_STYLE : FIELD_STYLE));
        }
    }

    private void addButtonHoverEffects(@NotNull Button... buttons) {
        for (Button button : buttons) {
            button.setOnMouseEntered(e -> animateButtonScale(button, 1.05));
            button.setOnMouseExited(e -> animateButtonScale(button, 1.0));
        }
    }

    private void animateButtonScale(Button button, double scale) {
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(100), button);
        scaleTransition.setToX(scale);
        scaleTransition.setToY(scale);
        scaleTransition.play();
    }

    private void addPasswordStrengthIndicator(@NotNull PasswordField passwordField, Text strengthText) {
        passwordField.textProperty().addListener((obs, oldVal, newVal) -> {
            updatePasswordStrengthDisplay(newVal, strengthText);
        });
    }

    private void updatePasswordStrengthDisplay(String password, Text strengthText) {
        int strength = calculatePasswordStrength(password);

        String[] messages = {"", "Password strength: Weak", "Password strength: Fair",
                "Password strength: Good", "Password strength: Strong"};
        Color[] colors = {Color.TRANSPARENT, Color.web("#e53e3e"), Color.web("#dd6b20"),
                Color.web("#3182ce"), Color.web("#38a169")};

        if (strength < messages.length) {
            strengthText.setText(messages[strength]);
            strengthText.setFill(colors[strength]);
        }
    }

    private int calculatePasswordStrength(@NotNull String password) {
        if (password.isEmpty()) return 0;

        int score = 0;
        if (password.length() >= 8) score++;
        if (password.matches(".*[a-z].*")) score++;
        if (password.matches(".*[A-Z].*")) score++;
        if (password.matches(".*[0-9].*")) score++;
        if (password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*")) score++;

        return Math.min(score, 4);
    }

    private void addEntranceAnimation(@NotNull VBox card) {
        card.setOpacity(0);
        card.setScaleY(0.8);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(600), card);
        fadeIn.setToValue(1.0);

        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(600), card);
        scaleIn.setToY(1.0);

        fadeIn.play();
        scaleIn.play();
    }

    private void handlePasswordChange() {
        String email = emailField.getText();
        String currentPassword = currentPasswordField.getText();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (!validateInputs(email, currentPassword, newPassword, confirmPassword)) {
            return;
        }

        performPasswordChange(email, currentPassword, newPassword);
    }

    private boolean validateInputs(String email, String currentPassword, String newPassword, String confirmPassword) {
        if (email.isEmpty() || currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showMessage("Please fill in all fields", Color.web("#e53e3e"));
            return false;
        }

        if (!isValidEmail(email)) {
            showMessage("Please enter a valid email address", Color.web("#e53e3e"));
            return false;
        }

        if (newPassword.length() < 6) {
            showMessage("New password must be at least 6 characters", Color.web("#e53e3e"));
            return false;
        }

        if (!newPassword.equals(confirmPassword)) {
            showMessage("New passwords do not match", Color.web("#e53e3e"));
            return false;
        }

        if (currentPassword.equals(newPassword)) {
            showMessage("New password must be different from current password", Color.web("#e53e3e"));
            return false;
        }

        return true;
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }

    private void performPasswordChange(String email, String currentPassword, String newPassword) {
        new Thread(() -> {
            try {
                SocketClient client = new SocketClient("localhost", 12345);
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
        showMessage("Password changed successfully!", Color.web("#38a169"));
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