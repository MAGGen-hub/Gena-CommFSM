module csfm.gui {
    requires javafx.controls;
    requires javafx.fxml;

    opens csfm.gui to javafx.fxml;
    exports csfm.gui;
}