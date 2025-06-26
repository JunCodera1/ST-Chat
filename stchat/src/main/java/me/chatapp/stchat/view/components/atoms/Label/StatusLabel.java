package me.chatapp.stchat.view.components.atoms.Label;

import javafx.scene.control.Label;

public class StatusLabel {

    private final Label label;
    private String status;

    public StatusLabel(String status) {
        this.status = status;
        this.label = new Label(status);
        initializeComponent();
    }

    private void initializeComponent() {
        label.setStyle("-fx-text-fill: #72767d; -fx-font-size: 11px;");
    }

    public void setStatus(String status) {
        this.status = status;
        label.setText(status);
    }

    public String getStatus() {
        return status;
    }

    public Label getComponent() {
        return label;
    }
}