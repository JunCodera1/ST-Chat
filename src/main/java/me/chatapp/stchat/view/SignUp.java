package me.chatapp.stchat.view;

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
import me.chatapp.stchat.database.DatabaseConnection;

import java.util.logging.Logger;
import java.util.regex.Pattern;

public class SignUp {

    private static final Logger LOGGER = Logger.getLogger(SignUp.class.getName());
    private final Stage stage;
    private final Runnable onSwitchToLogin;
    private final UserDAO userDAO;
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$"
    );

    public SignUp(Runnable onSwitchToLogin) {
        this.stage = new Stage();
        this.onSwitchToLogin = onSwitchToLogin;
        this.userDAO = new UserDAO();
        setupUI();
    }

    private void setupUI() {
        // Main container
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: linear-gradient(135deg, #667eea 0%, #764ba2 100%);");

        // Background decoration
        createBackgroundCircles(root);

        // Main content card
        VBox card = new VBox(20);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(40, 35, 40, 35));
        card.setMaxWidth(400);
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

        Text logoText = new Text("✨");
        logoText.setFont(Font.font(30));
        logoContainer.getChildren().addAll(logoCircle, logoText);

        // Title
        Text title = new Text("Create Account");
        title.setFont(Font.font("System", FontWeight.BOLD, 26));
        title.setFill(Color.web("#2d3748"));

        Text subtitle = new Text("Join ST Chat community today");
        subtitle.setFont(Font.font("System", FontWeight.NORMAL, 14));
        subtitle.setFill(Color.web("#718096"));

        // Form fields container
        VBox formContainer = new VBox(18);
        formContainer.setAlignment(Pos.CENTER);

        // Username field
        VBox usernameContainer = createFieldContainer("Username", "Choose a unique username");
        TextField userTextField = (TextField) usernameContainer.getChildren().get(1);

        // Email field
        VBox emailContainer = createFieldContainer("Email", "Enter your email address");
        TextField emailTextField = (TextField) emailContainer.getChildren().get(1);

        // Password field
        VBox passwordContainer = createPasswordContainer("Password", "Create a strong password");
        PasswordField passwordField = (PasswordField) passwordContainer.getChildren().get(1);

        // Confirm Password field
        VBox confirmPasswordContainer = createPasswordContainer("Confirm Password", "Confirm your password");
        PasswordField confirmPasswordField = (PasswordField) confirmPasswordContainer.getChildren().get(1);

        formContainer.getChildren().addAll(
                usernameContainer,
                emailContainer,
                passwordContainer,
                confirmPasswordContainer
        );

        // Password strength indicator
        Text passwordStrength = new Text();
        passwordStrength.setFont(Font.font("System", FontWeight.NORMAL, 12));

        // Database connection status
        Text dbStatus = new Text();
        dbStatus.setFont(Font.font("System", FontWeight.NORMAL, 12));
        updateDatabaseStatus(dbStatus);

        // Register button
        Button registerButton = new Button("Create Account");
        registerButton.setPrefWidth(320);
        registerButton.setPrefHeight(50);
        registerButton.setFont(Font.font("System", FontWeight.BOLD, 16));
        registerButton.setStyle(
                "-fx-background-color: linear-gradient(135deg, #667eea 0%, #764ba2 100%);" +
                        "-fx-text-fill: white;" +
                        "-fx-background-radius: 25;" +
                        "-fx-cursor: hand;" +
                        "-fx-effect: dropshadow(gaussian, rgba(102,126,234,0.4), 15, 0, 0, 5);"
        );

        // Sign in link
        VBox signInContainer = new VBox(5);
        signInContainer.setAlignment(Pos.CENTER);

        Text hasAccountText = new Text("Already have an account?");
        hasAccountText.setFont(Font.font("System", FontWeight.NORMAL, 13));
        hasAccountText.setFill(Color.web("#718096"));

        Button signInButton = new Button("Sign In");
        signInButton.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: #667eea;" +
                        "-fx-font-weight: bold;" +
                        "-fx-cursor: hand;" +
                        "-fx-font-size: 14;"
        );

        signInContainer.getChildren().addAll(hasAccountText, signInButton);

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
                passwordStrength,
                dbStatus,
                registerButton,
                signInContainer,
                statusMessage
        );

        root.getChildren().add(card);

        // Add interactive effects
        addFieldFocusEffects(userTextField, emailTextField, passwordField, confirmPasswordField);
        addButtonHoverEffects(registerButton, signInButton);
        addPasswordStrengthIndicator(passwordField, passwordStrength);

        // Event handlers
        registerButton.setOnAction(event ->
                handleRegistration(userTextField, emailTextField, passwordField, confirmPasswordField, statusMessage)
        );

        signInButton.setOnAction(event -> {
            stage.close();
            onSwitchToLogin.run();
        });

        // Enter key support
        confirmPasswordField.setOnAction(event -> registerButton.fire());

        // Create scene
        Scene scene = new Scene(root, 520, 830);
        stage.setTitle("ST Chat - Create Account");
        stage.setScene(scene);
        stage.setResizable(false);

        // Add entrance animation
        addEntranceAnimation(card);
    }

    private void updateDatabaseStatus(Text dbStatus) {
        if (DatabaseConnection.testConnection()) {
            dbStatus.setText("🟢 Database Connected");
            dbStatus.setFill(Color.web("#38a169"));
        } else {
            dbStatus.setText("🔴 Database Disconnected");
            dbStatus.setFill(Color.web("#e53e3e"));
        }
    }

    private VBox createFieldContainer(String labelText, String promptText) {
        VBox container = new VBox(8);

        Label label = new Label(labelText);
        label.setFont(Font.font("System", FontWeight.MEDIUM, 14));
        label.setTextFill(Color.web("#4a5568"));

        TextField textField = new TextField();
        textField.setPromptText(promptText);
        textField.setPrefHeight(45);
        textField.setStyle(getFieldStyle());

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
        passwordField.setStyle(getFieldStyle());

        container.getChildren().addAll(label, passwordField);
        return container;
    }

    private String getFieldStyle() {
        return "-fx-background-color: #f7fafc;" +
                "-fx-border-color: #e2e8f0;" +
                "-fx-border-width: 2;" +
                "-fx-border-radius: 12;" +
                "-fx-background-radius: 12;" +
                "-fx-padding: 0 15;" +
                "-fx-font-size: 14;";
    }

    private void createBackgroundCircles(StackPane root) {
        Circle circle1 = new Circle(90);
        circle1.setFill(Color.web("#ffffff", 0.1));
        circle1.setTranslateX(-160);
        circle1.setTranslateY(-250);

        Circle circle2 = new Circle(70);
        circle2.setFill(Color.web("#ffffff", 0.1));
        circle2.setTranslateX(190);
        circle2.setTranslateY(-180);

        Circle circle3 = new Circle(50);
        circle3.setFill(Color.web("#ffffff", 0.1));
        circle3.setTranslateX(-190);
        circle3.setTranslateY(300);

        Circle circle4 = new Circle(110);
        circle4.setFill(Color.web("#ffffff", 0.05));
        circle4.setTranslateX(160);
        circle4.setTranslateY(280);

        root.getChildren().addAll(circle1, circle2, circle3, circle4);
    }

    private void addFieldFocusEffects(TextField... fields) {
        String focusStyle =
                "-fx-background-color: #ffffff;" +
                        "-fx-border-color: #667eea;" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 12;" +
                        "-fx-background-radius: 12;" +
                        "-fx-padding: 0 15;" +
                        "-fx-font-size: 14;" +
                        "-fx-effect: dropshadow(gaussian, rgba(102,126,234,0.3), 10, 0, 0, 2);";

        for (TextField field : fields) {
            field.focusedProperty().addListener((obs, oldVal, newVal) -> {
                field.setStyle(newVal ? focusStyle : getFieldStyle());
            });
        }
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

    private void addPasswordStrengthIndicator(PasswordField passwordField, Text strengthText) {
        passwordField.textProperty().addListener((obs, oldVal, newVal) -> {
            int strength = calculatePasswordStrength(newVal);

            switch (strength) {
                case 0:
                    strengthText.setText("");
                    break;
                case 1:
                    strengthText.setText("Password strength: Weak");
                    strengthText.setFill(Color.web("#e53e3e"));
                    break;
                case 2:
                    strengthText.setText("Password strength: Fair");
                    strengthText.setFill(Color.web("#dd6b20"));
                    break;
                case 3:
                    strengthText.setText("Password strength: Good");
                    strengthText.setFill(Color.web("#3182ce"));
                    break;
                case 4:
                    strengthText.setText("Password strength: Strong");
                    strengthText.setFill(Color.web("#38a169"));
                    break;
            }
        });
    }

    private int calculatePasswordStrength(String password) {
        if (password.isEmpty()) return 0;

        int score = 0;
        if (password.length() >= 8) score++;
        if (password.matches(".*[a-z].*")) score++;
        if (password.matches(".*[A-Z].*")) score++;
        if (password.matches(".*[0-9].*")) score++;
        if (password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) score++;

        return Math.min(score, 4);
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

    private void handleRegistration(TextField userField, TextField emailField,
                                    PasswordField passField, PasswordField confirmField,
                                    Text statusMessage) {
        String username = userField.getText().trim();
        String email = emailField.getText().trim();
        String password = passField.getText();
        String confirmPassword = confirmField.getText();

        // Reset status message
        statusMessage.setText("");

        // Kiểm tra kết nối database trước khi xử lý
        if (!DatabaseConnection.testConnection()) {
            showMessage(statusMessage, "Database connection failed. Please try again later.", Color.web("#e53e3e"));
            return;
        }

        // Validation
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showMessage(statusMessage, "Please fill in all fields", Color.web("#e53e3e"));
            return;
        }

        if (username.length() < 3) {
            showMessage(statusMessage, "Username must be at least 3 characters", Color.web("#e53e3e"));
            return;
        }

        if (username.length() > 50) {
            showMessage(statusMessage, "Username must be less than 50 characters", Color.web("#e53e3e"));
            return;
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            showMessage(statusMessage, "Please enter a valid email address", Color.web("#e53e3e"));
            return;
        }

        if (email.length() > 100) {
            showMessage(statusMessage, "Email must be less than 100 characters", Color.web("#e53e3e"));
            return;
        }

        if (password.length() < 6) {
            showMessage(statusMessage, "Password must be at least 6 characters", Color.web("#e53e3e"));
            return;
        }

        if (password.length() > 50) {
            showMessage(statusMessage, "Password must be less than 50 characters", Color.web("#e53e3e"));
            return;
        }

        if (!password.equals(confirmPassword)) {
            showMessage(statusMessage, "Passwords do not match", Color.web("#e53e3e"));
            return;
        }

        // Kiểm tra username đã tồn tại chưa
        if (userDAO.isUsernameExists(username)) {
            showMessage(statusMessage, "Username already exists. Please choose another one.", Color.web("#e53e3e"));
            return;
        }

        // Kiểm tra email đã tồn tại chưa
        if (userDAO.isEmailExists(email)) {
            showMessage(statusMessage, "Email already exists. Please use another email.", Color.web("#e53e3e"));
            return;
        }

        // Thực hiện đăng ký
        boolean registrationSuccess = userDAO.registerUser(username, email, password);

        if (registrationSuccess) {
            showMessage(statusMessage, "Account created successfully! Redirecting to login...", Color.web("#38a169"));

            // Làm rỗng các trường
            userField.clear();
            emailField.clear();
            passField.clear();
            confirmField.clear();

            // Success animation
            ScaleTransition successScale = new ScaleTransition(Duration.millis(200), stage.getScene().getRoot());
            successScale.setToX(0.95);
            successScale.setToY(0.95);
            successScale.setOnFinished(e -> {
                // Auto-switch to login after 2 seconds
                new Thread(() -> {
                    try {
                        Thread.sleep(2000);
                        javafx.application.Platform.runLater(() -> {
                            stage.close();
                            onSwitchToLogin.run();
                        });
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                        LOGGER.warning("Thread interrupted during redirect delay: " + ex.getMessage());
                    }
                }).start();
            });
            successScale.play();
        } else {
            showMessage(statusMessage, "Registration failed. Please try again.", Color.web("#e53e3e"));
        }
    }

    private void showMessage(Text messageText, String message, Color color) {
        messageText.setText(message);
        messageText.setFill(color);

        // Fade in animation cho message
        FadeTransition fade = new FadeTransition(Duration.millis(300), messageText);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
    }

    public void show() {
        stage.show();
    }

    public void close() {
        stage.close();
    }

    public Stage getStage() {
        return stage;
    }
}