package me.chatapp.stchat.view.factories;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2MZ;

public class SettingsViewFactory {

    public static VBox create(Runnable onBackClicked) {
        VBox content = new VBox();
        content.setStyle("-fx-background-color: #f8f9fa;");

        // Header with a gradient background
        VBox header = createHeader(onBackClicked);

        // Profile section
        VBox profileSection = createProfileSection();

        // Settings sections
        VBox settingsSections = createSettingsSections();

        // ScrollPane for content
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(settingsSections);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        content.getChildren().addAll(header, profileSection, scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        return content;
    }

    private static VBox createHeader(Runnable onBackClicked) {
        VBox header = new VBox();
        header.setPrefHeight(120);
        header.setStyle("-fx-background-color: linear-gradient(to bottom right, #8B4513, #A0522D, #CD853F);");

        HBox headerContent = new HBox();
        headerContent.setAlignment(Pos.CENTER_LEFT);
        headerContent.setPadding(new Insets(15, 20, 15, 20));

        // Back button
        Button backButton = new Button();
        FontIcon backIcon = new FontIcon(Feather.EDIT_3);
        backIcon.setIconColor(Color.WHITE);
        backIcon.setIconSize(20);
        backButton.setGraphic(backIcon);
        backButton.setStyle("-fx-background-color: rgba(255,255,255,0.2); -fx-background-radius: 50; -fx-border-radius: 50; -fx-padding: 8;");
        backButton.setOnAction(e -> onBackClicked.run());

        // Title
        Label titleLabel = new Label("Settings");
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 24px; -fx-font-weight: bold;");
        HBox.setMargin(titleLabel, new Insets(0, 0, 0, 20));

        headerContent.getChildren().addAll(backButton, titleLabel);
        header.getChildren().add(headerContent);

        return header;
    }

    private static VBox createProfileSection() {
        VBox profileSection = new VBox();
        profileSection.setAlignment(Pos.CENTER);
        profileSection.setPadding(new Insets(20));
        profileSection.setStyle("-fx-background-color: white;");

        // Profile picture
        Circle profilePicture = new Circle(40);
        profilePicture.setStyle("-fx-fill: #e9ecef;");

        // Camera icon overlay
        Button cameraButton = new Button();
        FontIcon cameraIcon = new FontIcon(Feather.CAMERA);
        cameraIcon.setIconColor(Color.GRAY);
        cameraIcon.setIconSize(16);
        cameraButton.setGraphic(cameraIcon);
        cameraButton.setStyle("-fx-background-color: white; -fx-background-radius: 50; -fx-border-radius: 50; -fx-border-color: #dee2e6; -fx-padding: 5;");

        StackPane profileStack = new StackPane();
        profileStack.getChildren().addAll(profilePicture, cameraButton);
        StackPane.setAlignment(cameraButton, Pos.BOTTOM_RIGHT);

        // Name and status
        Label nameLabel = new Label("Kathryn Swarey");
        nameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #212529;");

        HBox statusBox = new HBox(5);
        statusBox.setAlignment(Pos.CENTER);
        Circle statusDot = new Circle(4);
        statusDot.setStyle("-fx-fill: #28a745;");
        Label statusLabel = new Label("Active");
        statusLabel.setStyle("-fx-text-fill: #28a745; -fx-font-size: 14px;");
        Button statusDropdown = new Button("▼");
        statusDropdown.setStyle("-fx-background-color: transparent; -fx-text-fill: #6c757d; -fx-font-size: 10px;");
        statusBox.getChildren().addAll(statusDot, statusLabel, statusDropdown);

        profileSection.getChildren().addAll(profileStack, nameLabel, statusBox);
        profileSection.setSpacing(10);

        return profileSection;
    }

    private static VBox createSettingsSections() {
        VBox sections = new VBox();
        sections.setSpacing(0);
        sections.setPadding(new Insets(10, 0, 0, 0));

        // Personal info section
        VBox personalInfoSection = createCollapsibleSection("Personal info", Material2MZ.PERSON, true);
        VBox personalInfoContent = new VBox();
        personalInfoContent.getChildren().addAll(
                createInfoRow("Name", "Kathryn Swarey", true),
                createInfoRow("Email", "adc@123.com", false),
                createInfoRow("Location", "California, USA", false)
        );
        personalInfoSection.getChildren().add(personalInfoContent);

        // Themes section
        VBox themesSection = createCollapsibleSection("Themes", Material2MZ.PALETTE, false);
        VBox themesContent = createThemesContent();
        themesSection.getChildren().add(themesContent);

        // Privacy section
        VBox privacySection = createCollapsibleSection("Privacy", Material2MZ.QR_CODE, false);
        VBox privacyContent = createPrivacyContent();
        privacySection.getChildren().add(privacyContent);

        // Security section
        VBox securitySection = createCollapsibleSection("Security", Material2MZ.SECURITY, false);
        VBox securityContent = createSecurityContent();
        securitySection.getChildren().add(securityContent);

        // Help section
        VBox helpSection = createCollapsibleSection("Help", Material2MZ.PERM_CONTACT_CALENDAR, false);
        VBox helpContent = createHelpContent();
        helpSection.getChildren().add(helpContent);

        sections.getChildren().addAll(
                personalInfoSection,
                createSeparator(),
                themesSection,
                createSeparator(),
                privacySection,
                createSeparator(),
                securitySection,
                createSeparator(),
                helpSection
        );

        return sections;
    }

    private static VBox createCollapsibleSection(String title, Object iconCode, boolean isExpanded) {
        VBox section = new VBox();
        section.setStyle("-fx-background-color: white;");

        Button sectionButton = new Button();
        sectionButton.setMaxWidth(Double.MAX_VALUE);
        sectionButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #495057; -fx-font-size: 16px; -fx-alignment: center-left; -fx-padding: 15 20 15 20;");

        HBox sectionContent = new HBox(10);
        sectionContent.setAlignment(Pos.CENTER_LEFT);

        FontIcon sectionIcon = new FontIcon((Ikon) iconCode);
        sectionIcon.setIconColor(Color.GRAY);
        sectionIcon.setIconSize(20);

        Label sectionLabel = new Label(title);
        sectionLabel.setStyle("-fx-text-fill: #495057; -fx-font-size: 16px;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        FontIcon expandIcon = new FontIcon(isExpanded ? Feather.CHEVRON_UP : Feather.CHEVRON_DOWN);
        expandIcon.setIconColor(Color.GRAY);
        expandIcon.setIconSize(16);

        sectionContent.getChildren().addAll(sectionIcon, sectionLabel, spacer, expandIcon);
        sectionButton.setGraphic(sectionContent);

        section.getChildren().add(sectionButton);

        return section;
    }

    private static HBox createInfoRow(String label, String value, boolean hasEdit) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(10, 20, 10, 20));

        VBox labelBox = new VBox();
        Label labelText = new Label(label);
        labelText.setStyle("-fx-text-fill: #6c757d; -fx-font-size: 14px;");
        Label valueText = new Label(value);
        valueText.setStyle("-fx-text-fill: #212529; -fx-font-size: 16px;");
        labelBox.getChildren().addAll(labelText, valueText);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        if (hasEdit) {
            Button editButton = new Button();
            FontIcon editIcon = new FontIcon(Feather.EDIT);
            editIcon.setIconColor(Color.rgb(40, 167, 69));
            editIcon.setIconSize(16);
            editButton.setGraphic(editIcon);
            editButton.setStyle("-fx-background-color: transparent;");
            row.getChildren().addAll(labelBox, spacer, editButton);
        } else {
            row.getChildren().addAll(labelBox, spacer);
        }

        return row;
    }

    private static VBox createThemesContent() {
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));

        // Theme colors
        Label colorLabel = new Label("CHOOSE THEME COLOR:");
        colorLabel.setStyle("-fx-text-fill: #6c757d; -fx-font-size: 12px; -fx-font-weight: bold;");

        HBox colorBox = new HBox(10);
        colorBox.setAlignment(Pos.CENTER_LEFT);

        String[] colors = {"#28a745", "#007bff", "#6610f2", "#e83e8c", "#dc3545", "#6c757d"};
        for (int i = 0; i < colors.length; i++) {
            Button colorButton = new Button();
            colorButton.setPrefSize(30, 30);
            colorButton.setStyle("-fx-background-color: " + colors[i] + "; -fx-background-radius: 50; -fx-border-radius: 50;");
            if (i == 0) { // First color selected
                colorButton.setStyle(colorButton.getStyle() + "; -fx-border-color: white; -fx-border-width: 2;");
            }
            colorBox.getChildren().add(colorButton);
        }

        // Theme images
        Label imageLabel = new Label("CHOOSE THEME IMAGE:");
        imageLabel.setStyle("-fx-text-fill: #6c757d; -fx-font-size: 12px; -fx-font-weight: bold;");

        HBox imageBox = new HBox(10);
        imageBox.setAlignment(Pos.CENTER_LEFT);

        for (int i = 0; i < 3; i++) {
            Button imageButton = new Button();
            imageButton.setPrefSize(40, 40);
            imageButton.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 8; -fx-border-color: #dee2e6; -fx-border-radius: 8;");
            if (i == 2) { // Third image selected
                FontIcon checkIcon = new FontIcon(Feather.CHECK);
                checkIcon.setIconColor(Color.rgb(40, 167, 69));
                checkIcon.setIconSize(16);
                imageButton.setGraphic(checkIcon);
            }
            imageBox.getChildren().add(imageButton);
        }

        content.getChildren().addAll(colorLabel, colorBox, imageLabel, imageBox);
        return content;
    }

    private static VBox createPrivacyContent() {
        VBox content = new VBox(10);
        content.setPadding(new Insets(20));

        content.getChildren().addAll(
                createToggleRow("Profile photo", "Selected", false),
                createToggleRow("Last seen", "", true),
                createToggleRow("Status", "Everyone", false),
                createPlainRow("displayStatus"),
                createToggleRow("Read receipts", "", true),
                createToggleRow("Groups", "Everyone", false)
        );

        return content;
    }

    private static VBox createSecurityContent() {
        VBox content = new VBox(10);
        content.setPadding(new Insets(20));

        content.getChildren().add(createToggleRow("Show security notification", "", false));

        return content;
    }

    private static VBox createHelpContent() {
        VBox content = new VBox(10);
        content.setPadding(new Insets(20));

        content.getChildren().addAll(
                createPlainRow("FAQs"),
                createPlainRow("Contact"),
                createPlainRow("Terms & Privacy policy")
        );

        return content;
    }

    private static HBox createToggleRow(String label, String value, boolean isEnabled) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(10, 0, 10, 0));

        VBox labelBox = new VBox();
        Label labelText = new Label(label);
        labelText.setStyle("-fx-text-fill: #212529; -fx-font-size: 16px;");
        labelBox.getChildren().add(labelText);

        if (!value.isEmpty()) {
            Label valueText = new Label(value);
            valueText.setStyle("-fx-text-fill: #6c757d; -fx-font-size: 14px;");
            labelBox.getChildren().add(valueText);
        }

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Toggle switch
        Button toggleButton = new Button();
        toggleButton.setPrefSize(50, 25);
        if (isEnabled) {
            toggleButton.setStyle("-fx-background-color: #28a745; -fx-background-radius: 15; -fx-border-radius: 15;");
            Circle toggleCircle = new Circle(10);
            toggleCircle.setStyle("-fx-fill: white;");
            toggleButton.setGraphic(toggleCircle);
        } else {
            toggleButton.setStyle("-fx-background-color: #dee2e6; -fx-background-radius: 15; -fx-border-radius: 15;");
            Circle toggleCircle = new Circle(10);
            toggleCircle.setStyle("-fx-fill: white;");
            toggleButton.setGraphic(toggleCircle);
        }

        row.getChildren().addAll(labelBox, spacer, toggleButton);
        return row;
    }

    private static HBox createPlainRow(String text) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(15, 0, 15, 0));

        Label label = new Label(text);
        label.setStyle("-fx-text-fill: #212529; -fx-font-size: 16px;");

        row.getChildren().add(label);
        return row;
    }

    private static Region createSeparator() {
        Region separator = new Region();
        separator.setPrefHeight(1);
        separator.setStyle("-fx-background-color: #dee2e6;");
        return separator;
    }
}