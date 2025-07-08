package me.chatapp.stchat.view.components.organisms.Bar;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class IconSidebar {

    private final VBox root;
    private final List<SidebarIconItem> iconItems = new ArrayList<>();
    private SidebarIconItem activeItem;

    public IconSidebar() {
        root = new VBox();
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-background-color: #2b2d31;");
        root.setSpacing(10);
        root.setPadding(new Insets(10));
        root.setPrefWidth(60);

        // Add icons
        addIcon("fa-comments", "Chats", true);
        addIcon("fa-user", "Profile");
        addIcon("fa-envelope", "Messages");
        addIcon("fa-phone", "Calls");
        addIcon("fa-bookmark", "Bookmarks");

        root.getChildren().add(createSpacer());

        addIcon("fa-cog", "Settings");

        // Avatar cuối cùng
        Image image = null;
        try {
            image = new Image(Objects.requireNonNull(getClass().getResource("/image/tech.jpg")).toExternalForm());
        } catch (Exception e) {
            System.err.println("⚠️ Không tìm thấy ảnh avatar: " + e.getMessage());
        }

        ImageView avatar = new ImageView();
        if (image != null && !image.isError()) {
            avatar.setImage(image);
            avatar.setFitWidth(32);
            avatar.setFitHeight(32);
            avatar.setClip(new Circle(16, 16, 16));
        } else {
            avatar.setImage(new Image("https://via.placeholder.com/32"));
        }


        StackPane avatarWrapper = new StackPane(avatar);
        avatarWrapper.setPadding(new Insets(5));
        avatarWrapper.setStyle("-fx-background-color: #3c3f45; -fx-background-radius: 16px;");
        Tooltip.install(avatarWrapper, new Tooltip("Your Profile"));

        root.getChildren().add(avatarWrapper);
    }

    public Node getComponent() {
        return root;
    }

    private void addIcon(String iconLiteral, String tooltip) {
        addIcon(iconLiteral, tooltip, false);
    }

    private void addIcon(String iconLiteral, String tooltip, boolean active) {
        SidebarIconItem item = new SidebarIconItem(iconLiteral, tooltip);
        if (active) {
            setActive(item);
        }

        item.setOnClick(() -> {
            setActive(item);
            System.out.println("Clicked: " + tooltip);
        });

        iconItems.add(item);
        root.getChildren().add(item.getNode());
    }

    private void setActive(SidebarIconItem item) {
        if (activeItem != null) activeItem.setActive(false);
        item.setActive(true);
        activeItem = item;
    }

    private Region createSpacer() {
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        return spacer;
    }

    public void setIconAction(String tooltip, Runnable action) {
        for (SidebarIconItem item : iconItems) {
            if (item.getTooltipText().equalsIgnoreCase(tooltip)) {
                item.setOnClick(() -> {
                    setActive(item);
                    action.run();
                });
                break;
            }
        }
    }


    private static class SidebarIconItem {
        private final StackPane wrapper;
        private final FontIcon icon;
        private Runnable onClick;
        private final String tooltipText;

        public SidebarIconItem(String iconLiteral, String tooltipText) {
            this.tooltipText = tooltipText;
            icon = new FontIcon(iconLiteral);
            icon.setIconSize(20);
            icon.setIconColor(javafx.scene.paint.Color.web("#a0a0a0"));

            wrapper = new StackPane(icon);
            wrapper.setPadding(new Insets(12));
            wrapper.setStyle("-fx-background-radius: 10px;");
            wrapper.setOnMouseEntered(e -> wrapper.setStyle("-fx-background-color: #4640DE; -fx-background-radius: 10px;"));
            wrapper.setOnMouseExited(e -> wrapper.setStyle("-fx-background-color: transparent;"));

            wrapper.setOnMouseClicked(e -> {
                if (onClick != null) onClick.run();
            });

            Tooltip.install(wrapper, new Tooltip(tooltipText));
        }

        public void setOnClick(Runnable action) {
            this.onClick = action;
        }

        public void setActive(boolean active) {
            if (active) {
                wrapper.setStyle("-fx-background-color: #4640DE; -fx-background-radius: 10px;");
                icon.setIconColor(javafx.scene.paint.Color.WHITE);
            } else {
                wrapper.setStyle("-fx-background-color: transparent;");
                icon.setIconColor(javafx.scene.paint.Color.web("#a0a0a0"));
            }
        }
        public String getTooltipText() {
            return tooltipText;
        }

        public Node getNode() {
            return wrapper;
        }
    }
}
