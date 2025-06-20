package me.chatapp.stchat.view.components.pages;

import javafx.animation.FadeTransition;
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
import me.chatapp.stchat.dao.UserDAO;
import java.util.logging.Logger;

public class ForgotPassword {

    private static final Logger LOGGER = Logger.getLogger(ForgotPassword.class.getName());
    private final Stage stage;
    private final UserDAO userDAO;
    private final Runnable onSwitchToLogin;

    public ForgotPassword(Runnable onSwitchToLogin) {
        this.stage = new Stage();
        this.userDAO = new UserDAO();
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

        // Title
        Text title = new Text("Forgot Password");
        title.setFont(Font.font("System", FontWeight.BOLD, 28));
        title.setFill(Color.web("#2d3748"));

        Text subtitle = new Text("Enter your email to reset your password");
        subtitle.setFont(Font.font("System", FontWeight.NORMAL, 14));
        subtitle.setFill(Color.web("#718096"));

        // Form fields
        VBox formContainer = new VBox(20);
        formContainer.setAlignment(Pos.CENTER);

        // Email field
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

        // Reset password button
        Button resetButton = new Button("Reset Password");
        resetButton.setPrefWidth(300);
        resetButton.setPrefHeight(50);
        resetButton.setFont(Font.font("System", FontWeight.BOLD, 16));

        // Back to login link
        VBox loginContainer = new VBox(5);
        loginContainer.setAlignment(Pos.CENTER);

        Text loginText = new Text("Remember your password?");
        loginText.setFont(Font.font("System", FontWeight.NORMAL, 13));
        loginText.setFill(Color.web("#718096"));

        Button loginButton = new Button("Back to Sign In");
        loginButton.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: #667eea;" +
                        "-fx-font-weight: bold;" +
                        "-fx-cursor: hand;" +
                        "-fx-font-size: 14;"
        );

        loginContainer.getChildren().addAll(loginText, loginButton);

        // Status message
        Text statusMessage = new Text();
        statusMessage.setFont(Font.font("System", FontWeight.MEDIUM, 12));
        statusMessage.setFill(Color.web("#e53e3e"));

        // Add all components to card
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

        // Add focus effects
        addFieldFocusEffects(emailField);
        addButtonHoverEffects(resetButton, loginButton);

        // Event handlers
        resetButton.setOnAction(event -> handleResetPassword(emailField, statusMessage, resetButton));
        loginButton.setOnAction(event -> {
            stage.close();
            onSwitchToLogin.run();
        });

        // Enter key support
        emailField.setOnAction(event -> resetButton.fire());

        // Create scene
        Scene scene = new Scene(root, 500, 700);
        stage.setTitle("ST Chat - Forgot Password");
        stage.setScene(scene);
        stage.setResizable(false);

        // Add entrance animation
        addEntranceAnimation(card);
    }

    private void createBackgroundCircles(StackPane root) {
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

    private void addFieldFocusEffects(TextField emailField) {
        String focusStyle =
                "-fx-background-color: #ffffff;" +
                        "-fx-border-color: #667eea;" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 12;" +
                        "-fx-background-radius: 12;" +
                        "-fx-padding: 0 15;" +
                        "-fx-font-size: 14;" +
                        "-fx-effect: dropshadow(gaussian, rgba(102,126,234,0.3), 10, 0, 0, 2);";

        String normalStyle =
                "-fx-background-color: #f7fafc;" +
                        "-fx-border-color: #e2e8f0;" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 12;" +
                        "-fx-background-radius: 12;" +
                        "-fx-padding: 0 15;" +
                        "-fx-font-size: 14;";

        emailField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            emailField.setStyle(newVal ? focusStyle : normalStyle);
        });
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

    private void addEntranceAnimation(VBox card) {
        card.setOpacity(0);
        card.setScaleY(0.8);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(600), card);
        fadeIn.setToValue(1.0);

        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(600), card);
        scaleIn.setToY(1.0);

        fadeIn.play();
        scaleIn.play();
    }

    private void handleResetPassword(TextField emailField, Text statusMessage, Button resetButton) {
        String email = emailField.getText().trim();

        // Validation
        if (email.isEmpty()) {
            statusMessage.setText("Please enter your email");
            statusMessage.setFill(Color.web("#e53e3e"));
            return;
        }

        // Basic email format validation
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            statusMessage.setText("Please enter a valid email address");
            statusMessage.setFill(Color.web("#e53e3e"));
            return;
        }

        // Disable button during processing
        resetButton.setDisable(true);
        resetButton.setText("Processing...");
        statusMessage.setText("Checking email...");
        statusMessage.setFill(Color.web("#3182ce"));

        // Perform email check in background thread
        new Thread(() -> {
            try {
                boolean emailExists = userDAO.isEmailExists(email);

                // Update UI on JavaFX thread
                javafx.application.Platform.runLater(() -> {
                    resetButton.setDisable(false);
                    resetButton.setText("Reset Password");

                    if (emailExists) {
                        statusMessage.setText("Password reset link sent to your email!");
                        statusMessage.setFill(Color.web("#38a169"));
                        LOGGER.info("Password reset requested for email: " + email);

                        // Success animation
                        ScaleTransition successScale = new ScaleTransition(Duration.millis(200), stage.getScene().getRoot());
                        successScale.setToX(0.95);
                        successScale.setToY(0.95);
                        successScale.play();
                    } else {
                        statusMessage.setText("Email not found in our system");
                        statusMessage.setFill(Color.web("#e53e3e"));

                        // Shake animation for error
                        ScaleTransition shakeScale = new ScaleTransition(Duration.millis(100), statusMessage);
                        shakeScale.setToX(1.1);
                        shakeScale.setAutoReverse(true);
                        shakeScale.setCycleCount(4);
                        shakeScale.play();

                        LOGGER.warning("Password reset failed - email not found: " + email);
                    }
                });

            } catch (Exception e) {
                javafx.application.Platform.runLater(() -> {
                    resetButton.setDisable(false);
                    resetButton.setText("Reset Password");
                    statusMessage.setText("Database connection error. Please try again.");
                    statusMessage.setFill(Color.web("#e53e3e"));
                });

                LOGGER.severe("Database error during password reset: " + e.getMessage());
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