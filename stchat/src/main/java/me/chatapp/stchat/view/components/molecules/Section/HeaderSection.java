package me.chatapp.stchat.view.components.molecules.Section;

import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import me.chatapp.stchat.view.components.atoms.Icon.LogoIcon;

public class HeaderSection extends VBox {
    private LogoIcon logoIcon;
    private Text title;
    private Text subtitle;

    public HeaderSection(String titleText, String subtitleText) {
        super(25);
        setAlignment(Pos.CENTER);

        createComponents(titleText, subtitleText);
        getChildren().addAll(logoIcon, title, subtitle);
    }

    private void createComponents(String titleText, String subtitleText) {
        logoIcon = new LogoIcon();

        title = new Text(titleText);
        title.setFont(Font.font("System", FontWeight.BOLD, 28));
        title.setFill(Color.web("#2d3748"));

        subtitle = new Text(subtitleText);
        subtitle.setFont(Font.font("System", FontWeight.NORMAL, 14));
        subtitle.setFill(Color.web("#718096"));
    }
}