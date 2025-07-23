package me.chatapp.stchat.view.components.pages;

import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
import org.json.JSONObject;

import java.util.logging.Logger;

import static me.chatapp.stchat.util.AnimationUtil.addEntranceAnimation;
import static me.chatapp.stchat.util.CSSUtil.LOGIN_BUTTON;
import static me.chatapp.stchat.util.DisplayUtil.addFieldFocusEffects;
import static me.chatapp.stchat.util.DisplayUtil.createBackgroundCircles;

public class ForgotPassword {

    private static final Logger LOGGER = Logger.getLogger(ForgotPassword.class.getName());
    private final Stage stage;
    private final Runnable onSwitchToLogin;

    public ForgotPassword(Runnable onSwitchToLogin) {
        this.stage = new Stage();
        this.onSwitchToLogin = onSwitchToLogin;
        setupUI();
    }

    private void setupUI() {
        // Main container
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: linear-gradient(135deg, #667eea 0%, #764ba2 100%);");

        // Background decoration circles
        createBackgroundCircles(root);

        // Main content card
        VBox card = new VBox(25);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(50, 40, 50, 40));
        card.setMaxWidth(380);
        card.setStyle(
                "-fx-background-color: rgba(255, 255, 255, 0.95);" +
                        "-fx-background-radius: 20;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 25, 0, 0, 10);"
        );

        // Logo/Icon area
        StackPane logoContainer = new StackPane();
        Circle logoCircle = new Circle(35);
        logoCircle.setFill(Color.web("#667eea"));
        logoCircle.setEffect(new DropShadow(15, Color.web("#667eea", 0.3)));

        Text logoText = new Text("🔑");
        logoText.setFont(Font.font(30));
        logoContainer.getChildren().addAll(logoCircle, logoText);

        Text title = new Text("Forgot Password");
        title.setFont(Font.font("System", FontWeight.BOLD, 28));
        title.setFill(Color.web("#2d3748"));

        Text subtitle = new Text("Enter your email to reset your password");
        subtitle.setFont(Font.font("System", FontWeight.NORMAL, 14));
        subtitle.setFill(Color.web("#718096"));

        VBox formContainer = new VBox(20);
        formContainer.setAlignment(Pos.CENTER);

        VBox emailContainer = new VBox(8);
        Label emailLabel = new Label("Email");
        emailLabel.setFont(Font.font("System", FontWeight.MEDIUM, 14));
        emailLabel.setTextFill(Color.web("#4a5568"));

        TextField emailField = new TextField();
        emailField.setPromptText("Enter your email");
        emailField.setPrefHeight(45);
        emailField.getStyleClass().add("email-field");

        emailContainer.getChildren().addAll(emailLabel, emailField);

        formContainer.getChildren().add(emailContainer);

        Button resetButton = new Button("Reset Password");
        resetButton.setPrefWidth(300);
        resetButton.setPrefHeight(50);
        resetButton.setFont(Font.font("System", FontWeight.BOLD, 16));

        VBox loginContainer = new VBox(5);
        loginContainer.setAlignment(Pos.CENTER);

        Text loginText = new Text("Remember your password?");
        loginText.setFont(Font.font("System", FontWeight.NORMAL, 13));
        loginText.setFill(Color.web("#718096"));

        Button loginButton = new Button("Back to Sign In");
        loginButton.setStyle(LOGIN_BUTTON);

        loginContainer.getChildren().addAll(loginText, loginButton);

        Text statusMessage = new Text();
        statusMessage.setFont(Font.font("System", FontWeight.MEDIUM, 12));
        statusMessage.setFill(Color.web("#e53e3e"));

        card.getChildren().addAll(
                logoContainer,
                title,
                subtitle,
                formContainer,
                resetButton,
                loginContainer,
                statusMessage
        );

        root.getChildren().add(card);

        addFieldFocusEffects(emailField);
        addButtonHoverEffects(resetButton, loginButton);

        resetButton.setOnAction(event -> handleResetPassword(emailField, statusMessage, resetButton));
        loginButton.setOnAction(event -> {
            stage.close();
            onSwitchToLogin.run();
        });

        emailField.setOnAction(event -> resetButton.fire());

        Scene scene = new Scene(root, 500, 700);
        stage.setTitle("ST Chat - Forgot Password");
        stage.setScene(scene);
        stage.setResizable(false);

        addEntranceAnimation(card);
    }

    private void addButtonHoverEffects(Button... buttons) {
        for (Button button : buttons) {
            button.setOnMouseEntered(e -> {
                ScaleTransition scale = new ScaleTransition(Duration.millis(100), button);
                scale.setToX(1.05);
                scale.setToY(1.05);
                scale.play();
            });

            button.setOnMouseExited(e -> {
                ScaleTransition scale = new ScaleTransition(Duration.millis(100), button);
                scale.setToX(1.0);
                scale.setToY(1.0);
                scale.play();
            });
        }
    }

    private void handleResetPassword(TextField emailField, Text statusMessage, Button resetButton) {
        String email = emailField.getText().trim();

        if (email.isEmpty()) {
            statusMessage.setText("Please enter your email");
            statusMessage.setFill(Color.web("#e53e3e"));
            return;
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            statusMessage.setText("Please enter a valid email address");
            statusMessage.setFill(Color.web("#e53e3e"));
            return;
        }

        resetButton.setDisable(true);
        resetButton.setText("Processing...");
        statusMessage.setText("Checking email...");
        statusMessage.setFill(Color.web("#3182ce"));

        new Thread(() -> {
            try {
                SocketClient socketClient = new SocketClient("localhost", 8080);

                String request = new JSONObject()
                        .put("type", "FORGOT_PASSWORD")
                        .put("email", email)
                        .toString();

                socketClient.send(request);

                String response = socketClient.receive();
                JSONObject json = new JSONObject(response);

                javafx.application.Platform.runLater(() -> {
                    resetButton.setDisable(false);
                    resetButton.setText("Reset Password");

                    String status = json.optString("status", "error");
                    String message = json.optString("message", "Unknown error");

                    if ("success".equalsIgnoreCase(status)) {
                        statusMessage.setText(message);
                        statusMessage.setFill(Color.web("#38a169"));
                        LOGGER.info("Password reset success: " + message);

                        ScaleTransition successScale = new ScaleTransition(Duration.millis(200), stage.getScene().getRoot());
                        successScale.setToX(0.95);
                        successScale.setToY(0.95);
                        successScale.play();
                    } else {
                        statusMessage.setText(message);
                        statusMessage.setFill(Color.web("#e53e3e")); // đỏ

                        ScaleTransition shakeScale = new ScaleTransition(Duration.millis(100), statusMessage);
                        shakeScale.setToX(1.1);
                        shakeScale.setAutoReverse(true);
                        shakeScale.setCycleCount(4);
                        shakeScale.play();

                        LOGGER.warning("Password reset failed: " + message);
                    }
                });

                socketClient.close();

            } catch (Exception e) {
                javafx.application.Platform.runLater(() -> {
                    resetButton.setDisable(false);
                    resetButton.setText("Reset Password");
                    statusMessage.setText("Could not connect to server.");
                    statusMessage.setFill(Color.web("#e53e3e"));
                });

                LOGGER.severe("Connection error during password reset: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }


    public void show() {
        stage.show();
    }

    public void close() {
        stage.close();
    }
}