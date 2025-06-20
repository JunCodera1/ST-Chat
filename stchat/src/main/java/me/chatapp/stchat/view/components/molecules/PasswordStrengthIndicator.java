package me.chatapp.stchat.view.components.molecules;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class PasswordStrengthIndicator extends Text {
    public PasswordStrengthIndicator() {
        setFont(Font.font("System", FontWeight.NORMAL, 12));
    }

    public void updateStrength(String password) {
        int strength = calculatePasswordStrength(password);

        switch (strength) {
            case 0:
                setText("");
                break;
            case 1:
                setText("Password strength: Weak");
                setFill(Color.web("#e53e3e"));
                break;
            case 2:
                setText("Password strength: Fair");
                setFill(Color.web("#dd6b20"));
                break;
            case 3:
                setText("Password strength: Good");
                setFill(Color.web("#3182ce"));
                break;
            case 4:
                setText("Password strength: Strong");
                setFill(Color.web("#38a169"));
                break;
        }
    }

    private int calculatePasswordStrength(String password) {
        if (password.isEmpty()) return 0;

        int score = 0;
        if (password.length() >= 8) score++;
        if (password.matches(".*[a-z].*")) score++;
        if (password.matches(".*[A-Z].*")) score++;
        if (password.matches(".*[0-9].*")) score++;
        if (password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*")) score++;

        return Math.min(score, 4);
    }
}
