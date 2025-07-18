package me.chatapp.stchat.view.components.pages;

import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import me.chatapp.stchat.api.SocketClient;
import me.chatapp.stchat.util.ValidateUtil;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.logging.Logger;

import static me.chatapp.stchat.util.AnimationUtil.*;
import static me.chatapp.stchat.util.CSSUtil.REGISTER_BACKGROUND;
import static me.chatapp.stchat.util.DisplayUtil.*;


public class SignUp {

    private static final Logger LOGGER = Logger.getLogger(SignUp.class.getName());
    private final Stage stage;
    private final Runnable onSwitchToLogin;
    private File selectedAvatarFile;

    public SignUp(Runnable onSwitchToLogin) {
        this.stage = new Stage();
        this.onSwitchToLogin = onSwitchToLogin;
        setupUI();
    }

    private void setupUI() {
        // Main container
        StackPane root = new StackPane();
        root.setStyle(REGISTER_BACKGROUND);

        // Background decoration
        createBackgroundCirclesRegister(root);

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

        // First name
        VBox firstNameContainer = createFieldContainer("First Name", "Enter your first name");
        TextField firstNameField = (TextField) firstNameContainer.getChildren().get(1);

        // Last name
        VBox lastNameContainer = createFieldContainer("Last Name", "Enter your last name");
        TextField lastNameField = (TextField) lastNameContainer.getChildren().get(1);

        // Avatar selection section
        VBox avatarContainer = createAvatarSelectionContainer();
        Circle avatarPreview = (Circle) ((StackPane) ((HBox) avatarContainer.getChildren().get(1)).getChildren().get(0)).getChildren().get(0);
        Text avatarLabel = (Text) ((StackPane) ((HBox) avatarContainer.getChildren().get(1)).getChildren().get(0)).getChildren().get(1);
        Button selectAvatarButton = (Button) ((HBox) avatarContainer.getChildren().get(1)).getChildren().get(1);

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
                avatarContainer,
                firstNameContainer,
                lastNameContainer,
                usernameContainer,
                emailContainer,
                passwordContainer,
                confirmPasswordContainer
        );

        // Password strength indicator
        Text passwordStrength = new Text();
        passwordStrength.setFont(Font.font("System", FontWeight.NORMAL, 12));

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
                registerButton,
                signInContainer,
                statusMessage
        );

        root.getChildren().add(card);

        // Add interactive effects
        addFieldFocusEffects(userTextField, emailTextField, passwordField, confirmPasswordField);
        addButtonSignUpHoverEffects(registerButton, signInButton);
        addPasswordStrengthIndicator(passwordField, passwordStrength);

        // Avatar selection handler
        selectAvatarButton.setOnAction(event -> handleAvatarSelection(avatarPreview, avatarLabel));

        registerButton.setOnAction(event ->
                handleRegistration(
                        userTextField,
                        emailTextField,
                        passwordField,
                        confirmPasswordField,
                        firstNameField,
                        lastNameField,
                        statusMessage
                )
        );

        signInButton.setOnAction(event -> {
            stage.close();
            onSwitchToLogin.run();
        });

        // Enter key support
        confirmPasswordField.setOnAction(event -> registerButton.fire());

        // Create scene
        Scene scene = new Scene(root, 520, 1000);
        stage.setTitle("ST Chat - Create Account");
        stage.setScene(scene);
        stage.setResizable(false);

        // Add entrance animation
        addEntranceAnimation(card);
    }

    private VBox createAvatarSelectionContainer() {
        VBox container = new VBox(8);
        container.setAlignment(Pos.CENTER);

        // Label
        Text label = new Text("Avatar (Optional)");
        label.setFont(Font.font("System", FontWeight.MEDIUM, 13));
        label.setFill(Color.web("#2d3748"));

        // Content container
        HBox contentContainer = new HBox(15);
        contentContainer.setAlignment(Pos.CENTER);

        // Avatar preview container
        StackPane avatarContainer = new StackPane();

        // Avatar circle
        Circle avatarCircle = new Circle(40);
        avatarCircle.setFill(Color.web("#f7fafc"));
        avatarCircle.setStroke(Color.web("#e2e8f0"));
        avatarCircle.setStrokeWidth(2);
        avatarCircle.setEffect(new DropShadow(5, Color.web("#000000", 0.1)));

        // Default avatar icon
        Text avatarIcon = new Text("👤");
        avatarIcon.setFont(Font.font(35));
        avatarIcon.setFill(Color.web("#a0aec0"));

        avatarContainer.getChildren().addAll(avatarCircle, avatarIcon);

        // Select button
        Button selectButton = new Button("Choose Image");
        selectButton.setPrefWidth(120);
        selectButton.setPrefHeight(35);
        selectButton.setFont(Font.font("System", FontWeight.MEDIUM, 12));
        selectButton.setStyle(
                "-fx-background-color: #667eea;" +
                        "-fx-text-fill: white;" +
                        "-fx-background-radius: 18;" +
                        "-fx-cursor: hand;" +
                        "-fx-effect: dropshadow(gaussian, rgba(102,126,234,0.3), 10, 0, 0, 3);"
        );

        // Hover effect for select button
        selectButton.setOnMouseEntered(e -> selectButton.setStyle(
                "-fx-background-color: #5a67d8;" +
                        "-fx-text-fill: white;" +
                        "-fx-background-radius: 18;" +
                        "-fx-cursor: hand;" +
                        "-fx-effect: dropshadow(gaussian, rgba(102,126,234,0.4), 12, 0, 0, 4);"
        ));

        selectButton.setOnMouseExited(e -> selectButton.setStyle(
                "-fx-background-color: #667eea;" +
                        "-fx-text-fill: white;" +
                        "-fx-background-radius: 18;" +
                        "-fx-cursor: hand;" +
                        "-fx-effect: dropshadow(gaussian, rgba(102,126,234,0.3), 10, 0, 0, 3);"
        ));

        contentContainer.getChildren().addAll(avatarContainer, selectButton);

        container.getChildren().addAll(label, contentContainer);
        return container;
    }

    private void handleAvatarSelection(Circle avatarPreview, Text avatarLabel) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Avatar Image");

        // Set extension filters
        FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter(
                "Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp"
        );
        fileChooser.getExtensionFilters().add(imageFilter);

        // Show open dialog
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            try {
                // Load and display image
                Image image = new Image(new FileInputStream(selectedFile));

                // Create circular image pattern
                ImagePattern imagePattern = new ImagePattern(image);
                avatarPreview.setFill(imagePattern);

                // Store selected file
                selectedAvatarFile = selectedFile;

                // Update label (hide the icon)
                avatarLabel.setVisible(false);

                LOGGER.info("Avatar selected: " + selectedFile.getName());

            } catch (IOException e) {
                LOGGER.severe("Error loading avatar image: " + e.getMessage());
                // Show error message to user
                showRegisterMessage(
                        (Text) stage.getScene().getRoot().lookup(".status-message"),
                        "Error loading image. Please try another file.",
                        Color.web("#e53e3e")
                );
            }
        }
    }

    private String encodeImageToBase64(File imageFile) {
        try {
            byte[] fileContent = Files.readAllBytes(imageFile.toPath());
            return Base64.getEncoder().encodeToString(fileContent);
        } catch (IOException e) {
            LOGGER.severe("Error encoding image to base64: " + e.getMessage());
            return null;
        }
    }

    private void handleRegistration(TextField userField, TextField emailField,
                                    PasswordField passField, PasswordField confirmField,
                                    TextField firstnameField, TextField lastnameField,
                                    Text statusMessage) {
        String username = userField.getText().trim();
        String firstname = firstnameField.getText().trim();
        String lastname = lastnameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passField.getText();
        String confirmPassword = confirmField.getText();

        String error = ValidateUtil.validateRegister(username, email, password, confirmPassword);
        if (error != null) {
            showRegisterMessage(statusMessage, error, Color.web("#e53e3e"));
            return;
        }

        new Thread(() -> {
            try {
                SocketClient client = new SocketClient("localhost", 8080);
                JSONObject requestJson = new JSONObject()
                        .put("type", "REGISTER")
                        .put("username", username)
                        .put("email", email)
                        .put("password", password)
                        .put("lastname", lastname)
                        .put("firstname", firstname);

                // Add avatar if selected
                if (selectedAvatarFile != null) {
                    String base64Image = encodeImageToBase64(selectedAvatarFile);
                    if (base64Image != null) {
                        requestJson.put("avatar", base64Image);
                        requestJson.put("avatarFileName", selectedAvatarFile.getName());
                    }
                }

                String request = requestJson.toString();

                client.send(request);
                String response = client.receive();

                JSONObject resJson = new JSONObject(response);

                Platform.runLater(() -> {
                    if ("success".equalsIgnoreCase(resJson.optString("status"))) {
                        showRegisterMessage(statusMessage, "Account created successfully! Please sign in.", Color.web("#38a169"));
                        getScaleTransition().play();
                    } else {
                        String message = resJson.optString("message", "Registration failed.");
                        showRegisterMessage(statusMessage, message, Color.web("#e53e3e"));
                    }
                });

                client.close();

            } catch (Exception e) {
                Platform.runLater(() ->
                        showRegisterMessage(statusMessage, "Unable to connect to server.", Color.web("#e53e3e"))
                );
                LOGGER.severe("Registration socket error: " + e.getMessage());
            }
        }).start();
    }

    @NotNull
    private ScaleTransition getScaleTransition() {
        ScaleTransition successScale = new ScaleTransition(Duration.millis(200), stage.getScene().getRoot());
        successScale.setToX(0.95);
        successScale.setToY(0.95);
        successScale.setOnFinished(e -> {
            // Auto-switch to login after 2 seconds
            new Thread(() -> {
                try {
                    Thread.sleep(2000);
                    Platform.runLater(() -> {
                        stage.close();
                        onSwitchToLogin.run();
                    });
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        });
        return successScale;
    }

    public void show() {
        stage.show();
    }
}