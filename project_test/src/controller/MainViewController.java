package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import model.Equipment;

public class MainViewController {

    @FXML
    private TableView<Equipment> equipmentTable;
    @FXML
    private TableColumn<Equipment, String> rentalOfficeCol;
    @FXML
    private TableColumn<Equipment, Equipment> nameCol;
    @FXML
    private TableColumn<Equipment, String> quantityCol;
    @FXML
    private TableColumn<Equipment, String> costCol;
    @FXML
    private TableColumn<Equipment, String> statusCol;

    @FXML
    public void initialize() {
        setupTableColumns();
        loadTableData();
    }

    private void setupTableColumns() {
        // 각 컬럼과 데이터 모델의 속성을 연결
        rentalOfficeCol.setCellValueFactory(cellData -> cellData.getValue().rentalOfficeProperty());
        quantityCol.setCellValueFactory(cellData -> cellData.getValue().quantityProperty());
        costCol.setCellValueFactory(cellData -> cellData.getValue().costProperty());

        // 'Status' 컬럼 커스텀 렌더링
        statusCol.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
        statusCol.setCellFactory(col -> new TableCell<Equipment, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    getStyleClass().removeAll("status-available", "status-rented", "status-broken");
                } else {
                    setText(item);
                    getStyleClass().removeAll("status-available", "status-rented", "status-broken");
                    if (item.equals("대여 가능")) {
                        getStyleClass().add("status-available");
                    } else if (item.equals("대여중")) {
                        getStyleClass().add("status-rented");
                    } else if (item.equals("고장")) {
                        getStyleClass().add("status-broken");
                    }
                }
            }
        });

    }
    private void loadTableData() {
        ObservableList<Equipment> data = FXCollections.observableArrayList(
            new Equipment("중구청", "전동 드릴", "1000원", "대여중", "1/2"),
            new Equipment("test", "전동 드릴", "1000원", "대여 가능", "1/2"),
            new Equipment("test2", "전동 드릴", "1000원", "고장", "1/20"),
            new Equipment("test3", "전동 드릴", "1000원", "대여 가능", "1/2")
        );
        equipmentTable.setItems(data);
    }
}