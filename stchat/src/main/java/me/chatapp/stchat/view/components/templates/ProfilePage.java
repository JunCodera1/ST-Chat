package me.chatapp.stchat.view.components.templates;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import me.chatapp.stchat.view.core.SceneManager;
import me.chatapp.stchat.view.pages.ChatView;

public class ProfilePage {

    public BorderPane getPage() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #2c3e50, #34495e);");

        // Top bar
        HBox topBar = createTopBar();
        root.setTop(topBar);


        //Left sidebar
        VBox leftSidebar = createLeftSidebar();
        root.setLeft(leftSidebar);

        //Right sidebar
        VBox rightSidebar = createRightSidebar();
        root.setRight(rightSidebar);

        //Feed Pane
        ScrollPane feedPane = createFeedContent();
        root.setCenter(feedPane);

        // Avatar
        StackPane avatarPane = new StackPane();
        createAvatar(new Image("https://www.gravatar.com/avatar/?d=mp&s=120"), 60, avatarPane);

        // Name
        Label nameLabel = new Label("Minh Tiến");
        nameLabel.setFont(Font.font("System", FontWeight.BOLD, 24));
        nameLabel.setTextFill(Color.WHITE);

        // Email
        Label emailLabel = new Label("minhtien@example.com");
        emailLabel.setFont(Font.font("System", 14));
        emailLabel.setTextFill(Color.LIGHTGRAY);
        // Chèn vào phần giữa (center) trên cùng
        StackPane centerWithSettings = new StackPane();
        centerWithSettings.getChildren().addAll(feedPane);
        root.setCenter(centerWithSettings);


        return root;
    }

    private static VBox getSettingsBox() {
        MenuItem editItem = new MenuItem("✏️ Edit");
        MenuItem logoutItem = new MenuItem("🚪 Logout");
        MenuItem backItem = new MenuItem("🔙 Quay về");

        // Gắn hành động cho từng lựa chọn
        editItem.setOnAction(e -> {
            System.out.println("Edit profile clicked");
        });
        logoutItem.setOnAction(e -> {
            System.out.println("User logged out");
        });
        backItem.setOnAction(e -> {
            ChatView chatView = new ChatView();
            SceneManager.switchScene(chatView.getScene());
        });

        // MenuButton (biểu tượng bánh răng)
        MenuButton settingsButton = new MenuButton("⚙️", null, editItem, logoutItem, backItem);
        settingsButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");

        // Thêm vào một vị trí phù hợp – ví dụ: topBar hoặc feed (center)
        VBox topRightBox = new VBox(10, settingsButton);
        topRightBox.setAlignment(Pos.TOP_RIGHT);
        topRightBox.setPadding(new Insets(10));
        return topRightBox;
    }

    public static void createAvatar(Image image, double radius, StackPane avatarPane){
        Circle circle = new Circle();
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(radius * 2);
        imageView.setFitHeight(radius * 2);
        imageView.setPreserveRatio(true);
        circle.setRadius(radius);
        circle.setFill(Color.WHITE);

        imageView.setClip(new Circle(radius, radius, radius));
        StackPane pane = new StackPane(circle, imageView);
        avatarPane.getChildren().add(pane);
    }

    public HBox createTopBar() {
        HBox topBar = new HBox(20);
        topBar.setPadding(new Insets(10));
        topBar.setStyle("-fx-background-color: #3b5998;");
        topBar.setAlignment(Pos.CENTER_LEFT);

        Label logo = new Label("ST");
        logo.setTextFill(Color.WHITE);
        logo.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        TextField searchBox = new TextField();
        searchBox.setPromptText("Tìm kiếm trên Facebook...");
        searchBox.setPrefWidth(250);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        ImageView profilePic = new ImageView(new Image("https://www.gravatar.com/avatar/?d=mp&s=40"));
        profilePic.setFitWidth(30);
        profilePic.setFitHeight(30);
        profilePic.setClip(new Circle(15, 15, 15));
        VBox settingBox = getSettingsBox();

        topBar.getChildren().addAll(logo, searchBox, spacer,settingBox, profilePic);
        return topBar;
    }

    public VBox createLeftSidebar() {
        VBox sidebar = new VBox(15);
        sidebar.setPadding(new Insets(15));
        sidebar.setStyle("-fx-background-color: #f0f2f5;");
        sidebar.setPrefWidth(200);

        Label userName = new Label("Minh Tiến");
        userName.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        Label friends = new Label("Bạn bè");
        Label groups = new Label("Nhóm");
        Label marketplace = new Label("Marketplace");

        sidebar.getChildren().addAll(userName, friends, groups, marketplace);
        return sidebar;
    }

    public ScrollPane createFeedContent() {
        VBox feed = new VBox(20);
        feed.setPadding(new Insets(20));
        feed.setStyle("-fx-background-color: white;");

        for (int i = 0; i < 5; i++) {
            VBox post = new VBox(10);
            post.setStyle("-fx-border-color: lightgray; -fx-border-width: 1; -fx-padding: 10;");
            Label author = new Label("Người dùng " + (i + 1));
            Label content = new Label("Đây là nội dung bài đăng số " + (i + 1));
            post.getChildren().addAll(author, content);
            feed.getChildren().add(post);
        }

        ScrollPane scrollPane = new ScrollPane(feed);
        scrollPane.setFitToWidth(true);
        return scrollPane;
    }

    public VBox createRightSidebar() {
        VBox sidebar = new VBox(10);
        sidebar.setPadding(new Insets(15));
        sidebar.setStyle("-fx-background-color: #f0f2f5;");
        sidebar.setPrefWidth(200);

        Label online = new Label("Bạn bè đang hoạt động");
        for (int i = 1; i <= 5; i++) {
            Label name = new Label("Bạn " + i);
            sidebar.getChildren().add(name);
        }

        return sidebar;
    }

}
