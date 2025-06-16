package me.chatapp.stchat.view.pages;

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
import me.chatapp.stchat.dao.UserDAO;
import me.chatapp.stchat.model.User;

import java.util.function.Consumer;
import java.util.logging.Logger;

public class Login {

    private static final Logger LOGGER = Logger.getLogger(Login.class.getName());
    private final Stage stage;
    private final UserDAO userDAO;
    private final Runnable onSwitchToSignUp;
    private final Consumer<User> onLoginSuccess; // Changed to pass User object

    // Current logged in user
    private User currentUser;

    public Login(Runnable onSwitchToSignUp, Consumer<User> onLoginSuccess) {
        this.stage = new Stage();
        this.userDAO = new UserDAO();
        this.onSwitchToSignUp = onSwitchToSignUp;
        this.onLoginSuccess = onLoginSuccess;
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

        Text logoText = new Text("💬");
        logoText.setFont(Font.font(30));
        logoContainer.getChildren().addAll(logoCircle, logoText);

        // Title
        Text title = new Text("Welcome Back");
        title.setFont(Font.font("System", FontWeight.BOLD, 28));
        title.setFill(Color.web("#2d3748"));

        Text subtitle = new Text("Sign in to continue to ST Chat");
        subtitle.setFont(Font.font("System", FontWeight.NORMAL, 14));
        subtitle.setFill(Color.web("#718096"));

        // Form fields
        VBox formContainer = new VBox(20);
        formContainer.setAlignment(Pos.CENTER);

        // Username field
        VBox usernameContainer = new VBox(8);
        Label usernameLabel = new Label("Username");
        usernameLabel.setFont(Font.font("System", FontWeight.MEDIUM, 14));
        usernameLabel.setTextFill(Color.web("#4a5568"));

        TextField userTextField = new TextField();
        userTextField.setPromptText("Enter your username");
        userTextField.setPrefHeight(45);
        userTextField.getStyleClass().add("user-text-field");

        usernameContainer.getChildren().addAll(usernameLabel, userTextField);

        // Password field
        VBox passwordContainer = new VBox(8);
        Label passwordLabel = new Label("Password");
        passwordLabel.setFont(Font.font("System", FontWeight.MEDIUM, 14));
        passwordLabel.setTextFill(Color.web("#4a5568"));

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        passwordField.setPrefHeight(45);
        passwordField.getStyleClass().add("password-field");

        passwordContainer.getChildren().addAll(passwordLabel, passwordField);

        formContainer.getChildren().addAll(usernameContainer, passwordContainer);

        // Login button
        Button loginButton = new Button("Sign In");
        loginButton.setPrefWidth(300);
        loginButton.setPrefHeight(50);
        loginButton.setFont(Font.font("System", FontWeight.BOLD, 16));

        // Forgot password link
        Button forgotPasswordButton = new Button("Forgot Password?");
        forgotPasswordButton.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: #667eea;" +
                        "-fx-underline: true;" +
                        "-fx-cursor: hand;" +
                        "-fx-font-size: 13;"
        );

        // Sign up link
        VBox signUpContainer = new VBox(5);
        signUpContainer.setAlignment(Pos.CENTER);

        Text noAccountText = new Text("Don't have an account?");
        noAccountText.setFont(Font.font("System", FontWeight.NORMAL, 13));
        noAccountText.setFill(Color.web("#718096"));

        Button signUpButton = new Button("Create Account");
        signUpButton.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: #667eea;" +
                        "-fx-font-weight: bold;" +
                        "-fx-cursor: hand;" +
                        "-fx-font-size: 14;"
        );

        signUpContainer.getChildren().addAll(noAccountText, signUpButton);

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
                loginButton,
                forgotPasswordButton,
                signUpContainer,
                statusMessage
        );

        root.getChildren().add(card);

        // Add focus effects
        addFieldFocusEffects(userTextField, passwordField);
        addButtonHoverEffects(loginButton, signUpButton, forgotPasswordButton);

        // Event handlers
        loginButton.setOnAction(event -> handleLogin(userTextField, passwordField, statusMessage, loginButton));
        signUpButton.setOnAction(event -> {
            stage.close();
            onSwitchToSignUp.run();
        });
        forgotPasswordButton.setOnAction(event -> {
            statusMessage.setText("Forgot Password feature coming soon!");
            statusMessage.setFill(Color.web("#3182ce"));
        });

        // Enter key support
        userTextField.setOnAction(event -> loginButton.fire());
        passwordField.setOnAction(event -> loginButton.fire());

        // Create scene
        Scene scene = new Scene(root, 500, 700);
        stage.setTitle("ST Chat - Sign In");
        stage.setScene(scene);
        stage.setResizable(false);

        // Add entrance animation
        addEntranceAnimation(card);
    }

    private void createBackgroundCircles(StackPane root) {
        // Decorative circles
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

    private void addFieldFocusEffects(TextField userField, PasswordField passField) {
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

        userField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            userField.setStyle(newVal ? focusStyle : normalStyle);
        });

        passField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            passField.setStyle(newVal ? focusStyle : normalStyle);
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

    private void handleLogin(TextField userField, PasswordField passField, Text statusMessage, Button loginButton) {
        String username = userField.getText().trim();
        String password = passField.getText();

        // Validation
        if (username.isEmpty() || password.isEmpty()) {
            statusMessage.setText("Please enter both username and password");
            statusMessage.setFill(Color.web("#e53e3e"));
            return;
        }

        // Disable button during authentication
        loginButton.setDisable(true);
        loginButton.setText("Signing in...");
        statusMessage.setText("Authenticating...");
        statusMessage.setFill(Color.web("#3182ce"));

        // Perform authentication in background thread to avoid UI blocking
        new Thread(() -> {
            try {
                boolean isAuthenticated = userDAO.authenticateUser(username, password);

                // Update UI on JavaFX thread
                javafx.application.Platform.runLater(() -> {
                    loginButton.setDisable(false);
                    loginButton.setText("Sign In");

                    if (isAuthenticated) {
                        // Get user information
                        User user = userDAO.getUserByUsername(username);

                        if (user != null) {
                            this.currentUser = user;
                            statusMessage.setText("Login successful! Welcome " + user.getUsername());
                            statusMessage.setFill(Color.web("#38a169"));

                            LOGGER.info("User logged in successfully: " + user.getUsername() +
                                    " (ID: " + user.getId() + ", Email: " + user.getEmail() + ")");

                            // Add success animation before closing
                            ScaleTransition successScale = new ScaleTransition(Duration.millis(200), stage.getScene().getRoot());
                            successScale.setToX(0.95);
                            successScale.setToY(0.95);
                            successScale.setOnFinished(e -> {
                                stage.close();
                                // Pass user object to callback
                                onLoginSuccess.accept(user);
                            });
                            successScale.play();
                        } else {
                            statusMessage.setText("Error retrieving user information");
                            statusMessage.setFill(Color.web("#e53e3e"));
                            LOGGER.warning("Authentication succeeded but failed to retrieve user: " + username);
                        }
                    } else {
                        statusMessage.setText("Invalid username or password");
                        statusMessage.setFill(Color.web("#e53e3e"));

                        // Shake animation for error
                        ScaleTransition shakeScale = new ScaleTransition(Duration.millis(100), statusMessage);
                        shakeScale.setToX(1.1);
                        shakeScale.setAutoReverse(true);
                        shakeScale.setCycleCount(4);
                        shakeScale.play();

                        LOGGER.warning("Failed login attempt for username: " + username);
                    }
                });

            } catch (Exception e) {
                javafx.application.Platform.runLater(() -> {
                    loginButton.setDisable(false);
                    loginButton.setText("Sign In");
                    statusMessage.setText("Database connection error. Please try again.");
                    statusMessage.setFill(Color.web("#e53e3e"));
                });

                LOGGER.severe("Database error during login: " + e.getMessage());
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

    // Getter for current logged in user
    public User getCurrentUser() {
        return currentUser;
    }

    // Method to check if user is logged in
    public boolean isUserLoggedIn() {
        return currentUser != null;
    }
}