package org.example;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.*;

public class TitlesBrowser extends Application {
    private TableView<AuthorBook> tableView = new TableView<>();
    private TextField searchField = new TextField();

    @Override
    public void start(Stage primaryStage) {
        TableColumn<AuthorBook, String> firstCol = new TableColumn<>("First Name");
        firstCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("firstName"));

        TableColumn<AuthorBook, String> lastCol = new TableColumn<>("Last Name");
        lastCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("lastName"));

        TableColumn<AuthorBook, String> isbnCol = new TableColumn<>("ISBN");
        isbnCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("isbn"));

        TableColumn<AuthorBook, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("title"));
        titleCol.setPrefWidth(230);

        tableView.getColumns().addAll(firstCol, lastCol, isbnCol, titleCol);
        searchField.setPromptText("Last-name prefix");
        Button searchBtn = new Button("Search");
        HBox searchBox = new HBox(10, searchField, searchBtn);
        searchBox.setPadding(new Insets(10));

        VBox root = new VBox(10, tableView, searchBox);
        root.setPadding(new Insets(15));
        Scene scene = new Scene(root, 640, 400);
        primaryStage.setTitle("Titles Browser");
        primaryStage.setScene(scene);
        primaryStage.show();

        loadData("%");

        searchBtn.setOnAction(e -> {
            String prefix = searchField.getText().trim();
            if (prefix.isEmpty()) {
                loadData("%");
            } else {
                loadData(prefix + "%");
            }
        });
    }

    private void loadData(String pattern) {
        ObservableList<AuthorBook> items = FXCollections.observableArrayList();
        String sql = "SELECT a.FirstName, a.LastName, t.ISBN, t.Title " +
                     "FROM Authors a " +
                     "INNER JOIN AuthorISBN ai ON a.AuthorID = ai.AuthorID " +
                     "INNER JOIN Titles t ON ai.ISBN = t.ISBN " +
                     "WHERE a.LastName LIKE ? " +
                     "ORDER BY a.LastName, a.FirstName";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, pattern);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                items.add(new AuthorBook(
                    rs.getString("FirstName"),
                    rs.getString("LastName"),
                    rs.getString("ISBN"),
                    rs.getString("Title")
                ));
            }
            tableView.setItems(items);
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Database error: " + e.getMessage());
            alert.showAndWait();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
