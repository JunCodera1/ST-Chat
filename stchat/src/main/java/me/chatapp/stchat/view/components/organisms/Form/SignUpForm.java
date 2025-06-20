package me.chatapp.stchat.view.components.organisms.Form;

import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import me.chatapp.stchat.dao.UserDAO;
import me.chatapp.stchat.view.components.atoms.Button.STButton;
import me.chatapp.stchat.view.components.molecules.Indicator.PasswordStrengthIndicator;
import me.chatapp.stchat.view.components.molecules.Form.SignUpFormField;
import me.chatapp.stchat.view.components.molecules.Message.StatusMessage;

public class SignUpForm extends VBox {
    private SignUpFormField usernameField;
    private SignUpFormField emailField;
    private SignUpFormField passwordField;
    private SignUpFormField confirmPasswordField;
    private PasswordStrengthIndicator strengthIndicator;
    private STButton registerButton;
    private StatusMessage statusMessage;

    private UserDAO userDAO;

    public SignUpForm(UserDAO userDAO) {
        super(18);
        this.userDAO = userDAO;
        setAlignment(Pos.CENTER);

        setupFormFields();
        setupButton();
        setupStatusMessage();
        setupEventHandlers();

        getChildren().addAll(
                usernameField,
                emailField,
                passwordField,
                confirmPasswordField,
                strengthIndicator,
                registerButton,
                statusMessage
        );
    }

    private void setupFormFields() {
        usernameField = new SignUpFormField("Username", "Choose a unique username", false);
        emailField = new SignUpFormField("Email", "Enter your email address", false);
        passwordField = new SignUpFormField("Password", "Create a strong password", true);
        confirmPasswordField = new SignUpFormField("Confirm Password", "Confirm your password", true);

        strengthIndicator = new PasswordStrengthIndicator();
    }

    private void setupButton() {
        registerButton = new STButton("Create Account", STButton.ButtonType.PRIMARY);
        registerButton.setPrefWidth(320);
        registerButton.setPrefHeight(50);
    }

    private void setupEventHandlers() {
        // Password strength monitoring
        passwordField.getInputField().textProperty().addListener((obs, oldVal, newVal) ->
                strengthIndicator.updateStrength(newVal));

        // Registration handler
        registerButton.setOnAction(event -> handleRegistration());

        // Enter key support
        confirmPasswordField.getInputField().setOnAction(event -> registerButton.fire());
    }

    private void handleRegistration() {
        // Validation và registration logic
        // (Di chuyển từ class chính)
    }

    private void setupStatusMessage() {
        statusMessage = new StatusMessage();
    }
}
