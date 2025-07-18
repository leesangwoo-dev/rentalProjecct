package controller;

import java.util.List;
import dao.EquipmentDAO; // DAO 이름이 EquipmentDAO라고 가정
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent; // awt.event.MouseEvent가 아닌 javafx.scene.input.MouseEvent 사용
import model.EquipmentViewDTO; // 수정한 DTO를 임포트

// 메인 페이지(장비 조회)_사용자 컨트롤러
public class MainViewController {

    // 1. TableView와 Column의 타입을 EquipmentViewDTO로 변경
    @FXML
    private TableView<EquipmentViewDTO> equipmentTable;
    @FXML
    private TableColumn<EquipmentViewDTO, String> rentalOfficeCol;
    @FXML
    private TableColumn<EquipmentViewDTO, String> nameCol; // 이름은 String이므로 타입 변경
    @FXML
    private TableColumn<EquipmentViewDTO, String> SerialNumCol; 
    @FXML
    private TableColumn<EquipmentViewDTO, Number> costCol; // 비용은 숫자 타입이므로 Number로 변경
    @FXML
    private TableColumn<EquipmentViewDTO, String> statusCol;
    
    // ImageView가 있다면 FXML에 선언하고 여기에 추가
    // @FXML private ImageView equipmentImageView;

    @FXML
    public void initialize() {
        setupTableColumns();
        loadTableData();
    }

    private void setupTableColumns() {
        // 2. 각 컬럼과 EquipmentViewDTO의 프로퍼티를 연결
        rentalOfficeCol.setCellValueFactory(cellData -> cellData.getValue().officeNameProperty());
        nameCol.setCellValueFactory(cellData -> cellData.getValue().eqNameProperty());
        costCol.setCellValueFactory(cellData -> cellData.getValue().rentalFeeProperty());
        statusCol.setCellValueFactory(cellData -> cellData.getValue().stateProperty());
        SerialNumCol.setCellValueFactory(cellData -> cellData.getValue().serialNumProperty());
        // 'quantityCol'은 DTO에 없는 필드. 개별 장비를 보여주므로 수량은 항상 1
        
        // 'Status' 컬럼 커스텀 렌더링 (타입만 EquipmentViewDTO로 변경)
        statusCol.setCellFactory(col -> new TableCell<EquipmentViewDTO, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    // 스타일 클래스 초기화
                    getStyleClass().removeAll("status-available", "status-rented", "status-broken");
                } else {
                    setText(item);
                    getStyleClass().removeAll("status-available", "status-rented", "status-broken");
                    // DB에서 가져온 상태값(STATE 컬럼)에 따라 스타일 적용
                    // 나중에 바꿔야함(대여 가능, 대여중, 고장 등) DB의 실제 값과 일치해야 함
                    if (item.equals("사용가능")) { 
                        getStyleClass().add("status-available");
                    } else if (item.equals("대여")) {
                        getStyleClass().add("status-rented");
                    } else if (item.equals("수리")) {
                        getStyleClass().add("status-broken");
                    }
                }
            }
        });
    }

    private void loadTableData() {
        EquipmentDAO dao = new EquipmentDAO();
        // DB 프로시저에 "대여가능" 상태를 전달하여 데이터 조회
        List<EquipmentViewDTO> equipmentData = dao.findEquipmentByState();

        // 3. 가져온 데이터를 TableView에 설정 (주석 해제!)
        equipmentTable.getItems().setAll(equipmentData);

        // 첫 번째 아이템의 이미지를 ImageView에 표시하는 예시
        if (!equipmentData.isEmpty()) {
            String imagePath = equipmentData.get(0).getImg();
            // File file = new File(imagePath);
            // if(file.exists()) {
            //    Image image = new Image(file.toURI().toString());
            //    equipmentImageView.setImage(image);
            // }
        }

        System.out.println("조회된 장비 수: " + equipmentData.size());
    }

    // 4. 이벤트 핸들러의 타입도 JavaFX 것으로 변경
    @FXML
    public void doubleClickItem(MouseEvent event) {
        if (event.getClickCount() == 2) { // 더블 클릭 확인
            EquipmentViewDTO selectedItem = equipmentTable.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                System.out.println("선택된 아이템: " + selectedItem.getEqName());
                // 여기서 상세 정보 창을 띄우거나 다른 작업을 수행할 수 있습니다.
            }
        }
    }
}