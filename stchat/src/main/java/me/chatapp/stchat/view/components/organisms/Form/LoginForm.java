package me.chatapp.stchat.view.components.organisms.Form;

import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import me.chatapp.stchat.view.components.atoms.Button.LinkButton;
import me.chatapp.stchat.view.components.atoms.Button.PrimaryButton;
import me.chatapp.stchat.view.components.atoms.Field.StyledPasswordField;
import me.chatapp.stchat.view.components.atoms.Field.StyledTextField;
import me.chatapp.stchat.view.components.atoms.Text.StatusText;
import me.chatapp.stchat.view.components.molecules.Form.FormField;

public class LoginForm extends VBox {
    private FormField usernameField;
    private FormField passwordField;
    private PrimaryButton loginButton;
    private LinkButton forgotPasswordButton;
    private StatusText statusMessage;

    public LoginForm() {
        super(20);
        setAlignment(Pos.CENTER);
        createFormComponents();
        setupFormLayout();
    }

    private void createFormComponents() {
        // Username field
        StyledTextField usernameTextField = new StyledTextField("Enter your username");
        usernameField = new FormField("Username", usernameTextField);

        // Password field
        StyledPasswordField passwordField = new StyledPasswordField("Enter your password");
        this.passwordField = new FormField("Password", passwordField);

        // Login button
        loginButton = new PrimaryButton("Sign In");

        // Forgot password button
        forgotPasswordButton = new LinkButton("Forgot Password?");
        forgotPasswordButton.setUnderline(true);
        forgotPasswordButton.setStyle(forgotPasswordButton.getStyle() + "-fx-font-size: 13;");

        // Status message
        statusMessage = new StatusText();
    }

    private void setupFormLayout() {
        VBox formContainer = new VBox(20);
        formContainer.setAlignment(Pos.CENTER);
        formContainer.getChildren().addAll(usernameField, passwordField);

        getChildren().addAll(
                formContainer,
                loginButton,
                forgotPasswordButton,
                statusMessage
        );
    }

    // Getters
    public StyledTextField getUsernameField() {
        return (StyledTextField) usernameField.getInputField();
    }

    public StyledPasswordField getPasswordField() {
        return (StyledPasswordField) passwordField.getInputField();
    }

    public PrimaryButton getLoginButton() {
        return loginButton;
    }

    public LinkButton getForgotPasswordButton() {
        return forgotPasswordButton;
    }

    public StatusText getStatusMessage() {
        return statusMessage;
    }

    // Utility methods
    public void setupEnterKeyHandlers() {
        getUsernameField().setOnAction(e -> loginButton.fire());
        getPasswordField().setOnAction(e -> loginButton.fire());
    }
}