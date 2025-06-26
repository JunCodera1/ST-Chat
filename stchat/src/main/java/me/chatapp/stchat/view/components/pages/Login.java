package me.chatapp.stchat.view.components.pages;

import javafx.animation.ScaleTransition;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;
import me.chatapp.stchat.model.User;
import me.chatapp.stchat.network.SocketClient;
import me.chatapp.stchat.view.components.atoms.Text.StatusText;
import me.chatapp.stchat.view.components.templates.LoginTemplate;
import org.json.JSONObject;

import java.util.function.Consumer;
import java.util.logging.Logger;

public class Login {
    private static final Logger LOGGER = Logger.getLogger(Login.class.getName());
    private final Stage stage;
    private final Runnable onSwitchToSignUp;
    private final Consumer<User> onLoginSuccess;
    private LoginTemplate loginTemplate;
    private User currentUser;

    public Login(Runnable onSwitchToSignUp, Consumer<User> onLoginSuccess) {
        this.stage = new Stage();
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

        if (username.isEmpty() || password.isEmpty()) {
            loginForm.getStatusMessage().setStatus(
                    "Please enter both username and password",
                    StatusText.Status.ERROR
            );
            return;
        }

        loginForm.getLoginButton().setDisable(true);
        loginForm.getLoginButton().setText("Signing in...");
        loginForm.getStatusMessage().setStatus("Authenticating...", StatusText.Status.INFO);

        new Thread(() -> {
            try {
                SocketClient socketClient = new SocketClient("localhost", 12345);

                // Gửi JSON login request
                String request = new JSONObject()
                        .put("type", "LOGIN")
                        .put("username", username)
                        .put("password", password)
                        .toString();

                socketClient.send(request);

                // Nhận JSON response
                String response = socketClient.receive();
                JSONObject json = new JSONObject(response);

                javafx.application.Platform.runLater(() -> {
                    loginForm.getLoginButton().setDisable(false);
                    loginForm.getLoginButton().setText("Sign In");

                    if ("success".equalsIgnoreCase(json.optString("status"))) {
                        User user = new User(json.getString("username"));
                        this.currentUser = user;

                        loginForm.getStatusMessage().setStatus(
                                "Login successful! Welcome " + user.getUsername(),
                                StatusText.Status.SUCCESS
                        );

                        ScaleTransition successScale = new ScaleTransition(Duration.millis(200), stage.getScene().getRoot());
                        successScale.setToX(0.95);
                        successScale.setToY(0.95);
                        successScale.setOnFinished(e -> {
                            stage.close();
                            onLoginSuccess.accept(user);
                        });
                        successScale.play();
                    } else {
                        loginForm.getStatusMessage().setStatus(
                                json.optString("message", "Login failed."),
                                StatusText.Status.ERROR
                        );
                    }
                });

                socketClient.close();

            } catch (Exception e) {
                e.printStackTrace();
                javafx.application.Platform.runLater(() -> {
                    loginForm.getLoginButton().setDisable(false);
                    loginForm.getLoginButton().setText("Sign In");
                    loginForm.getStatusMessage().setStatus(
                            "Could not connect to server.",
                            StatusText.Status.ERROR
                    );
                });
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