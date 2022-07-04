module ch.clic.newsmaker {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires com.fasterxml.jackson.databind;


    opens ch.clic.newsmaker to javafx.fxml;
    exports ch.clic.newsmaker;
}