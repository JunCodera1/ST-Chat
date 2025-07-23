package me.chatapp.stchat.util;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.*;
import me.chatapp.stchat.model.AttachmentMessage;
import me.chatapp.stchat.model.Message;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static me.chatapp.stchat.util.CSSUtil.*;
import static me.chatapp.stchat.util.CSSUtil.normalStyle;

public class DisplayUtil {
    public static void updatePasswordStrengthDisplay(String password, Text strengthText) {
        int strength = ValidateUtil.calculatePasswordStrength(password);

        String[] messages = {"", "Password strength: Weak", "Password strength: Fair",
                "Password strength: Good", "Password strength: Strong"};
        Color[] colors = {Color.TRANSPARENT, Color.web("#e53e3e"), Color.web("#dd6b20"),
                Color.web("#3182ce"), Color.web("#38a169")};

        if (strength < messages.length) {
            strengthText.setText(messages[strength]);
            strengthText.setFill(colors[strength]);
        }
    }

    public static void createBackgroundCircles(StackPane root) {
        Circle[] circles = {
                createBackgroundCircle(90, -160, -250, 0.1),
                createBackgroundCircle(70, 190, -180, 0.1),
                createBackgroundCircle(50, -190, 280, 0.1),
                createBackgroundCircle(110, 160, 250, 0.05)
        };

        root.getChildren().addAll(circles);
    }

    public static Circle createBackgroundCircle(double radius, double translateX, double translateY, double opacity) {
        Circle circle = new Circle(radius);
        circle.setFill(Color.web("#ffffff", opacity));
        circle.setTranslateX(translateX);
        circle.setTranslateY(translateY);
        return circle;
    }

    public static VBox createPasswordContainer(String labelText, String promptText) {
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

    public static void addFieldFocusEffects(TextField... fields) {
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
            field.focusedProperty().addListener((obs, oldVal, newVal) -> field.setStyle(newVal ? focusStyle : getFieldStyle()));
        }
    }


    public static void createBackgroundCirclesRegister(StackPane root) {
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

    public static VBox createFieldContainer(String labelText, String promptText) {
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

    public static Button createBackButton() {
        Button button = new Button("← Back to Profile");
        button.setPrefWidth(320);
        button.setPrefHeight(45);
        button.setFont(Font.font("System", FontWeight.MEDIUM, 14));
        button.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-border-color: #667eea;" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 22;" +
                        "-fx-text-fill: #667eea;" +
                        "-fx-cursor: hand;"
        );
        return button;
    }

    public static Button createChangePasswordButton() {
        Button button = new Button("Change Password");
        button.setPrefWidth(320);
        button.setPrefHeight(50);
        button.setFont(Font.font("System", FontWeight.BOLD, 16));
        button.setStyle(
                GRADIENT_BACKGROUND +
                        "-fx-text-fill: white;" +
                        "-fx-background-radius: 25;" +
                        "-fx-cursor: hand;" +
                        "-fx-effect: dropshadow(gaussian, rgba(102,126,234,0.4), 15, 0, 0, 5);"
        );
        return button;
    }

    public static void addFieldFocusEffects(TextField emailField) {
        emailField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            emailField.setStyle(newVal ? focusStyle : normalStyle);
        });
    }

    public static void styleMessageBubble(VBox bubble, Label senderLabel, TextFlow contentFlow, Message message) {
        switch (message.getType()) {
            case USER:
                bubble.setStyle("-fx-background-color: #3498db; -fx-background-radius: 18 18 4 18;");
                senderLabel.setTextFill(Color.WHITE);
                contentFlow.getChildren().forEach(node -> {
                    if (node instanceof Text) {
                        ((Text) node).setFill(Color.WHITE);
                    }
                });
                break;

            case BOT:
                if (message.getContent().startsWith("(Private)")) {
                    bubble.setStyle("-fx-background-color: #fdf2f2; -fx-background-radius: 18 18 18 4; -fx-border-color: #e74c3c; -fx-border-width: 1; -fx-border-radius: 18 18 18 4;");
                    senderLabel.setTextFill(Color.web("#c0392b"));
                    contentFlow.getChildren().forEach(node -> {
                        if (node instanceof Text) {
                            ((Text) node).setFill(Color.web("#2c3e50"));
                        }
                    });
                } else {
                    bubble.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 18 18 18 4; -fx-border-color: #e9ecef; -fx-border-width: 1; -fx-border-radius: 18 18 18 4;");
                    senderLabel.setTextFill(Color.web("#27ae60"));
                    contentFlow.getChildren().forEach(node -> {
                        if (node instanceof Text) {
                            ((Text) node).setFill(Color.web("#2c3e50"));
                        }
                    });
                }
                break;

            case SYSTEM:
                bubble.setStyle("-fx-background-color: #fff3cd; -fx-background-radius: 12; -fx-border-color: #ffeaa7; -fx-border-width: 1; -fx-border-radius: 12;");
                senderLabel.setTextFill(Color.web("#856404"));
                contentFlow.getChildren().forEach(node -> {
                    if (node instanceof Text) {
                        ((Text) node).setFill(Color.web("#856404"));
                    }
                });
                break;
        }

        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(0, 0, 0, 0.08));
        shadow.setRadius(4);
        shadow.setOffsetX(0);
        shadow.setOffsetY(2);
        bubble.setEffect(shadow);
    }


    public static Circle createAvatar(Message message) {
        Circle avatar = new Circle(16);

        switch (message.getType()) {
            case USER:
                avatar.setFill(Color.web("#3498db"));
                break;
            case BOT:
                if (message.getContent().startsWith("(Private)")) {
                    avatar.setFill(Color.web("#e74c3c"));
                } else {
                    avatar.setFill(Color.web("#27ae60"));
                }
                break;
            case SYSTEM:
                avatar.setFill(Color.web("#f39c12"));
                break;
        }

        // Add subtle shadow
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(0, 0, 0, 0.1));
        shadow.setRadius(2);
        shadow.setOffsetX(0);
        shadow.setOffsetY(1);
        avatar.setEffect(shadow);

        return avatar;
    }

    public static Region createSeparator() {
        Region separator = new Region();
        separator.setPrefHeight(1);
        separator.setStyle(STYLE_SEPARATOR);
        return separator;
    }

    public static TextFlow parseMessageToTextFlow(String rawContent) {
        TextFlow flow = new TextFlow();

        Pattern pattern = Pattern.compile(":(\\w[\\w\\-]*):");
        Matcher matcher = pattern.matcher(rawContent);

        int lastEnd = 0;

        while (matcher.find()) {
            if (matcher.start() > lastEnd) {
                String text = rawContent.substring(lastEnd, matcher.start());
                flow.getChildren().add(new Text(text));
            }

            String iconLiteral = matcher.group(1);
            try {
                FontIcon icon = new FontIcon(iconLiteral);
                icon.setIconSize(16);
                flow.getChildren().add(icon);
            } catch (Exception e) {
                flow.getChildren().add(new Text(matcher.group()));
            }

            lastEnd = matcher.end();
        }

        if (lastEnd < rawContent.length()) {
            String remaining = rawContent.substring(lastEnd);
            flow.getChildren().add(new Text(remaining));
        }

        flow.setLineSpacing(4);
        flow.setTextAlignment(TextAlignment.LEFT);

        flow.setMaxWidth(Double.MAX_VALUE);
        flow.setPrefWidth(Region.USE_COMPUTED_SIZE);

        return flow;
    }

    public static Button createModernButton(String text, FontAwesome icon) {
        Button button = new Button(text);
        button.setGraphic(new FontIcon(icon));
        button.setStyle(String.format(
                "-fx-background-color: transparent; " +
                        "-fx-text-fill: %s; " +
                        "-fx-border-color: %s; " +
                        "-fx-border-radius: 6px; " +
                        "-fx-background-radius: 6px; " +
                        "-fx-padding: 8px 16px; " +
                        "-fx-font-size: 13px; " +
                        "-fx-font-weight: 500; " +
                        "-fx-cursor: hand;",
                PRIMARY_COLOR, BORDER_COLOR
        ));

        // Add hover effect
        button.setOnMouseEntered(e -> {
            button.setStyle(String.format(
                    "-fx-background-color: %s; " +
                            "-fx-text-fill: %s; " +
                            "-fx-border-color: %s; " +
                            "-fx-border-radius: 6px; " +
                            "-fx-background-radius: 6px; " +
                            "-fx-padding: 8px 16px; " +
                            "-fx-font-size: 13px; " +
                            "-fx-font-weight: 500; " +
                            "-fx-cursor: hand;",
                    HOVER_COLOR, PRIMARY_COLOR, PRIMARY_COLOR
            ));
        });

        button.setOnMouseExited(e -> {
            button.setStyle(String.format(
                    "-fx-background-color: transparent; " +
                            "-fx-text-fill: %s; " +
                            "-fx-border-color: %s; " +
                            "-fx-border-radius: 6px; " +
                            "-fx-background-radius: 6px; " +
                            "-fx-padding: 8px 16px; " +
                            "-fx-font-size: 13px; " +
                            "-fx-font-weight: 500; " +
                            "-fx-cursor: hand;",
                    PRIMARY_COLOR, BORDER_COLOR
            ));
        });

        // Add tooltip
        Tooltip tooltip = new Tooltip(text);
        tooltip.setStyle("-fx-font-size: 12px;");
        button.setTooltip(tooltip);

        return button;
    }

    public static HBox createActionButtons(AttachmentMessage attachment, MessageActions messageActions) {
        HBox actionBox = new HBox();
        actionBox.setSpacing(12);
        actionBox.setAlignment(Pos.CENTER_LEFT);
        actionBox.setPadding(new Insets(8, 0, 0, 0));

        Button downloadBtn = createModernButton("Download", FontAwesome.DOWNLOAD);
        downloadBtn.setOnAction(e -> {
            try {
                if (messageActions != null) {
                    messageActions.handleDownload(attachment);
                } else {
                    System.err.println("MessageActions is null");
                }
            } catch (Exception ex) {
                System.err.println("Error downloading file: " + ex.getMessage());
            }
        });

        Button openBtn = createModernButton("Open", FontAwesome.EXTERNAL_LINK);
        openBtn.setOnAction(e -> {
            try {
                if (messageActions != null) {
                    messageActions.handleOpenFile(attachment);
                } else {
                    // Fallback: Open with system default application
                    try {
                        java.awt.Desktop.getDesktop().open(new File(attachment.getFilePath()));
                    } catch (Exception desktopEx) {
                        System.err.println("Cannot open file: " + desktopEx.getMessage());
                    }
                }
            } catch (Exception ex) {
                System.err.println("Error opening file: " + ex.getMessage());
            }
        });

        // Add share button
        Button shareBtn = createModernButton("Share", FontAwesome.SHARE);
        shareBtn.setOnAction(e -> {
            try {
                // Implement share functionality safely
                System.out.println("Share file: " + attachment.getFileName());
                // You can add actual share logic here
            } catch (Exception ex) {
                System.err.println("Error sharing file: " + ex.getMessage());
            }
        });

        actionBox.getChildren().addAll(downloadBtn, openBtn, shareBtn);
        return actionBox;
    }

    // Error message component
    public static VBox createErrorMessage() {
        VBox errorContainer = new VBox();
        errorContainer.setSpacing(8);
        errorContainer.setPadding(new Insets(12));
        errorContainer.setStyle(String.format(
                "-fx-background-color: %s; " +
                        "-fx-background-radius: 8px; " +
                        "-fx-border-color: #ff6b6b; " +
                        "-fx-border-radius: 8px; " +
                        "-fx-border-width: 1px;",
                HOVER_COLOR
        ));

        Label errorLabel = new Label("Video file not found");
        errorLabel.setStyle("-fx-text-fill: #ff6b6b; -fx-font-weight: 500;");
        errorContainer.getChildren().add(errorLabel);

        return errorContainer;
    }

    // Fallback video player using external application
    public static VBox createVideoPlayerFallback(AttachmentMessage attachment) {
        VBox container = new VBox();
        container.setSpacing(12);
        container.setPadding(new Insets(12));
        container.setStyle(String.format(
                "-fx-background-color: %s; " +
                        "-fx-background-radius: 8px; " +
                        "-fx-border-color: %s; " +
                        "-fx-border-radius: 8px; " +
                        "-fx-border-width: 1px;",
                HOVER_COLOR, BORDER_COLOR
        ));

        // Video thumbnail or icon
        Label videoIcon = new Label("🎬");
        videoIcon.setStyle(String.format(
                "-fx-font-size: 48px; " +
                        "-fx-text-fill: %s; " +
                        "-fx-background-color: %s; " +
                        "-fx-background-radius: 8px; " +
                        "-fx-padding: 16px; " +
                        "-fx-alignment: center;",
                PRIMARY_COLOR, BACKGROUND_COLOR
        ));

        Label infoLabel = new Label("Video preview not available");
        infoLabel.setStyle(String.format(
                "-fx-text-fill: %s; " +
                        "-fx-font-size: 14px; " +
                        "-fx-font-weight: 500;",
                TEXT_SECONDARY
        ));

        Label fileLabel = new Label(attachment.getFileName());
        fileLabel.setStyle(String.format(
                "-fx-text-fill: %s; " +
                        "-fx-font-size: 13px;",
                TEXT_PRIMARY
        ));

        Button openExternalBtn = createModernButton("Open in External Player", FontAwesome.EXTERNAL_LINK);
        openExternalBtn.setStyle(String.format(
                "-fx-background-color: %s; " +
                        "-fx-text-fill: white; " +
                        "-fx-border-color: %s; " +
                        "-fx-border-radius: 6px; " +
                        "-fx-background-radius: 6px; " +
                        "-fx-padding: 8px 16px; " +
                        "-fx-font-size: 13px; " +
                        "-fx-font-weight: 500;",
                PRIMARY_COLOR, PRIMARY_COLOR
        ));

        openExternalBtn.setOnAction(e -> {
            try {
                java.awt.Desktop.getDesktop().open(new File(attachment.getFilePath()));
            } catch (Exception ex) {
                System.err.println("Cannot open external player: " + ex.getMessage());
            }
        });

        VBox contentBox = new VBox(8);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.getChildren().addAll(videoIcon, infoLabel, fileLabel);

        container.getChildren().addAll(contentBox, openExternalBtn);
        container.setAlignment(Pos.CENTER);

        return container;
    }
}
