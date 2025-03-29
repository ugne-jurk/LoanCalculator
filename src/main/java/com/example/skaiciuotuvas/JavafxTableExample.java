package com.example.skaiciuotuvas;



import javafx.application.Application;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

public class JavafxTableExample extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        TableView<Person> table = new TableView<>();
        TableColumn<Person, Number> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(cellData -> cellData.getValue().idProperty());

        TableColumn<Person, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(cellData -> cellData.getValue().nameProperty());

        TableColumn<Person, Number> sumCol = new TableColumn<>("Sum");
        sumCol.setCellValueFactory(cellData -> cellData.getValue().sumProperty());
        table.getColumns().add(idCol);
        table.getColumns().add(sumCol);
        table.getColumns().add(nameCol);

        ObservableList<Person> data = Person.getSampleData();
        table.setItems(data);
        idCol.setPrefWidth(50);
        sumCol.setPrefWidth(50);
        nameCol.setPrefWidth(100);
        primaryStage.setScene(new Scene(table, 200, 230));
        primaryStage.show();



    }
    public static void main(String[] args) {
        launch(args);
    }
}
