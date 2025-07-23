package me.chatapp.stchat.view.components.pages;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.animation.ScaleTransition;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;
import me.chatapp.stchat.AppContext;
import me.chatapp.stchat.model.User;
import me.chatapp.stchat.api.SocketClient;
import me.chatapp.stchat.view.components.atoms.Text.StatusText;
import me.chatapp.stchat.view.components.templates.LoginTemplate;
import org.json.JSONObject;

import java.util.function.Consumer;

public class Login {
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

        loginForm.getLoginButton().setOnAction(event -> handleLogin());

        loginForm.getForgotPasswordButton().setOnAction(event -> {
            stage.close();
            ForgotPassword forgotPassword = new ForgotPassword(() -> {
                new Login(onSwitchToSignUp, onLoginSuccess).show();
            });
            forgotPassword.show();
        });

        loginForm.getChangePasswordButton().setOnAction(event -> {
            stage.close();;
            ChangePassword changePassword = new ChangePassword(() ->{
                new Login(onSwitchToSignUp, onLoginSuccess).show();
            });
            changePassword.show();
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
                SocketClient socketClient = new SocketClient("localhost", 8080);

                // Gửi JSON login request
                String request = new JSONObject()
                        .put("type", "LOGIN")
                        .put("username", username)
                        .put("password", password)
                        .toString();

                socketClient.send(request);

                // Nhận JSON response
                String response = socketClient.receive();
                System.out.println(">>> [Client] Received from server: " + response);

                JSONObject json = new JSONObject(response);

                javafx.application.Platform.runLater(() -> {
                    loginForm.getLoginButton().setDisable(false);
                    loginForm.getLoginButton().setText("Sign In");

                    if ("success".equalsIgnoreCase(json.optString("status"))) {
                        try {
                            JSONObject userJson = json.getJSONObject("user");

                            ObjectMapper mapper = new ObjectMapper();
                            mapper.registerModule(new JavaTimeModule());

                            User user = mapper.readValue(userJson.toString(), User.class);

                            this.currentUser = user;
                            AppContext.getInstance().setSocketClient(socketClient);

                            loginForm.getStatusMessage().setStatus(
                                    "Login successful! Welcome " + user.getUsername(),
                                    StatusText.Status.SUCCESS
                            );

                            socketClient.startListening(msg -> {
                                System.out.println("Server said: " + msg);
                            });

                            ScaleTransition successScale = new ScaleTransition(Duration.millis(200), stage.getScene().getRoot());
                            successScale.setToX(0.95);
                            successScale.setToY(0.95);
                            successScale.setOnFinished(e -> {
                                stage.close();
                                onLoginSuccess.accept(user);
                            });
                            successScale.play();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            loginForm.getStatusMessage().setStatus(
                                    "Login successful, but failed to parse user data.",
                                    StatusText.Status.ERROR
                            );
                        }
                    } else {
                        loginForm.getStatusMessage().setStatus(
                                json.optString("message", "Login failed."),
                                StatusText.Status.ERROR
                        );
                    }
                });
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
}