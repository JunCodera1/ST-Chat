package me.chatapp.stchat.view.components.pages;

import javafx.animation.ScaleTransition;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;
import me.chatapp.stchat.dao.UserDAO;
import me.chatapp.stchat.model.User;
import me.chatapp.stchat.view.components.atoms.StatusText;
import me.chatapp.stchat.view.components.templates.LoginTemplate;

import java.util.function.Consumer;
import java.util.logging.Logger;

public class Login {
    private static final Logger LOGGER = Logger.getLogger(Login.class.getName());
    private final Stage stage;
    private final UserDAO userDAO;
    private final Runnable onSwitchToSignUp;
    private final Consumer<User> onLoginSuccess;
    private LoginTemplate loginTemplate;
    private User currentUser;

    public Login(Runnable onSwitchToSignUp, Consumer<User> onLoginSuccess) {
        this.stage = new Stage();
        this.userDAO = new UserDAO();
        this.onSwitchToSignUp = onSwitchToSignUp;
        this.onLoginSuccess = onLoginSuccess;
        setupUI();
    }

    private void setupUI() {
        loginTemplate = new LoginTemplate(() -> {
            stage.close();
            onSwitchToSignUp.run();
        });

        setupEventHandlers();

        Scene scene = new Scene(loginTemplate, 500, 700);
        stage.setTitle("ST Chat - Sign In");
        stage.setScene(scene);
        stage.setResizable(false);
    }

    private void setupEventHandlers() {
        var loginForm = loginTemplate.getLoginForm();

        // Login button handler
        loginForm.getLoginButton().setOnAction(event -> handleLogin());

        // Forgot password button handler
        loginForm.getForgotPasswordButton().setOnAction(event -> {
            stage.close();
            ForgotPassword forgotPassword = new ForgotPassword(() -> {
                new Login(onSwitchToSignUp, onLoginSuccess).show();
            });
            forgotPassword.show();
        });
    }

    private void handleLogin() {
        var loginForm = loginTemplate.getLoginForm();
        String username = loginForm.getUsernameField().getText().trim();
        String password = loginForm.getPasswordField().getText();

        // Validation
        if (username.isEmpty() || password.isEmpty()) {
            loginForm.getStatusMessage().setStatus(
                    "Please enter both username and password",
                    StatusText.Status.ERROR
            );
            return;
        }

        // Disable button during authentication
        loginForm.getLoginButton().setDisable(true);
        loginForm.getLoginButton().setText("Signing in...");
        loginForm.getStatusMessage().setStatus("Authenticating...", StatusText.Status.INFO);

        // Perform authentication in background thread
        new Thread(() -> {
            try {
                boolean isAuthenticated = userDAO.authenticateUser(username, password);

                javafx.application.Platform.runLater(() -> {
                    loginForm.getLoginButton().setDisable(false);
                    loginForm.getLoginButton().setText("Sign In");

                    if (isAuthenticated) {
                        User user = userDAO.getUserByUsername(username);
                        if (user != null) {
                            this.currentUser = user;
                            loginForm.getStatusMessage().setStatus(
                                    "Login successful! Welcome " + user.getUsername(),
                                    StatusText.Status.SUCCESS
                            );

                            LOGGER.info("User logged in successfully: " + user.getUsername());

                            // Success animation
                            ScaleTransition successScale = new ScaleTransition(
                                    Duration.millis(200),
                                    stage.getScene().getRoot()
                            );
                            successScale.setToX(0.95);
                            successScale.setToY(0.95);
                            successScale.setOnFinished(e -> {
                                stage.close();
                                onLoginSuccess.accept(user);
                            });
                            successScale.play();
                        } else {
                            loginForm.getStatusMessage().setStatus(
                                    "Error retrieving user information",
                                    StatusText.Status.ERROR
                            );
                            LOGGER.warning("Authentication succeeded but failed to retrieve user: " + username);
                        }
                    } else {
                        loginForm.getStatusMessage().setStatus(
                                "Invalid username or password",
                                StatusText.Status.ERROR
                        );
                        LOGGER.warning("Failed login attempt for username: " + username);
                    }
                });

            } catch (Exception e) {
                javafx.application.Platform.runLater(() -> {
                    loginForm.getLoginButton().setDisable(false);
                    loginForm.getLoginButton().setText("Sign In");
                    loginForm.getStatusMessage().setStatus(
                            "Database connection error. Please try again.",
                            StatusText.Status.ERROR
                    );
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

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isUserLoggedIn() {
        return currentUser != null;
    }
}