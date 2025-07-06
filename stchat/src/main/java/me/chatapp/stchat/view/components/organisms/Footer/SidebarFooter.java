package me.chatapp.stchat.view.components.organisms.Footer;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import me.chatapp.stchat.controller.ChatController;
import me.chatapp.stchat.model.User;
import me.chatapp.stchat.api.SocketClient;
import me.chatapp.stchat.view.components.atoms.Button.SidebarIconButton;
import me.chatapp.stchat.view.components.atoms.Label.StatusLabel;
import javafx.scene.control.Button;
import javafx.stage.Popup;
import javafx.stage.Window;
import me.chatapp.stchat.view.components.pages.ChatView;
import me.chatapp.stchat.view.components.pages.Login;
import me.chatapp.stchat.view.config.ChatViewConfig;
import org.jetbrains.annotations.NotNull;

import static me.chatapp.stchat.util.CSSUtil.baseButtonStyle;
import static me.chatapp.stchat.util.CSSUtil.hoverButtonStyle;

public class SidebarFooter extends VBox {

    private Label userNameLabel;
    private StatusLabel statusLabel;
    private final SocketClient client;
    private final Stage stage;
    private final Runnable onSettingsClicked;


    public SidebarFooter(User user, SocketClient client, Stage stage, Runnable onSettingsClicked) {
        this.client = client;
        this.stage = stage;
        this.onSettingsClicked = onSettingsClicked;
        setSpacing(5);
        setPadding(new Insets(10, 15, 15, 15));
        setStyle("-fx-background-color: #1a1d21;");

        Label archivedLabel = new Label("📁 Archived Contacts");
        archivedLabel.setStyle("-fx-text-fill: #72767d; -fx-font-size: 12px;");

        HBox userProfile = new HBox();
        userProfile.setAlignment(Pos.CENTER_LEFT);
        userProfile.setSpacing(10);
        userProfile.setPadding(new Insets(10, 0, 0, 0));

        Circle avatar = new Circle(15);
        avatar.setFill(Color.web("#7289da"));

        VBox userInfo = new VBox();
        userInfo.setSpacing(2);

        userNameLabel = new Label(user.getUsername());
        userNameLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");

        statusLabel = new StatusLabel("🟢 Online");

        userInfo.getChildren().addAll(userNameLabel, statusLabel.getComponent());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        SidebarIconButton settingsButton = getSidebarIconButton();

        userProfile.getChildren().addAll(avatar, userInfo, spacer, settingsButton.getComponent());
        getChildren().addAll(archivedLabel, userProfile);
    }

    @NotNull
    private SidebarIconButton getSidebarIconButton() {
        SidebarIconButton settingsButton = new SidebarIconButton("fas-cog", "Settings");

        StackPane settingsNode = settingsButton.getComponent();

        settingsButton.setOnAction(() -> {
            Popup popup = new Popup();

            VBox content = new VBox(5);
            content.setPadding(new Insets(8));
            content.setStyle("-fx-background-color: #2c2f33; -fx-background-radius: 6; -fx-border-radius: 6;");

            Button profileSettings = createHoverButton("Profile Settings");
            Button changeStatus = createHoverButton("Change Status");
            Button logout = createHoverButton("Logout");

            logout.setOnAction(event -> {
                client.logoutAndClose();
                if (stage != null) {
                    stage.close();
                }

                new Login(
                        onSettingsClicked,
                        user -> {
                            try {
                                ChatViewConfig config = new ChatViewConfig();
                                ChatView view = new ChatView(config, user, stage);
                                ChatController controller = new ChatController(user, stage);

                                Stage mainStage = new Stage();
                                mainStage.setTitle("ST Chat - " + user.getUsername());
                                mainStage.setScene(view.getScene());
                                mainStage.setMinWidth(900);
                                mainStage.setMinHeight(650);
                                mainStage.show();

                                controller.initialize();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                ).show();
            });




            content.getChildren().addAll(profileSettings, changeStatus, logout);
            popup.getContent().add(content);
            popup.setAutoHide(true);

            Window window = settingsNode.getScene().getWindow();
            double x = settingsNode.localToScene(0, 0).getX() + window.getX() + settingsNode.getScene().getX();
            double y = settingsNode.localToScene(0, 0).getY() + window.getY() + settingsNode.getScene().getY() + settingsNode.getHeight();

            popup.show(window, x, y);
        });

        return settingsButton;
    }

    private Button createHoverButton(String text) {
        Button button = new Button(text);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setStyle(baseButtonStyle());

        button.setOnMouseEntered(e -> button.setStyle(hoverButtonStyle()));
        button.setOnMouseExited(e -> button.setStyle(baseButtonStyle()));
        return button;
    }

    public void updateUser(User newUser) {
        userNameLabel.setText(newUser.getUsername());
        System.out.println("Name: " + newUser.getUsername());
    }
}
