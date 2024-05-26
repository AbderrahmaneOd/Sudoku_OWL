module org.ensa.sudoku {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires org.apache.jena.core;

    opens org.ensa.sudoku to javafx.fxml;
    exports org.ensa.sudoku;
}