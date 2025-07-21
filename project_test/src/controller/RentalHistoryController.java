package controller;

import static util.Session.userId;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

import dao.RentalDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.RentalHistoryDTO;

public class RentalHistoryController implements Initializable {

//	@FXML private TableView<RentalHistoryDTO> rentalTable;
//	@FXML private TableColumn<RentalHistoryDTO, String> eqNameCol;
//	@FXML private TableColumn<RentalHistoryDTO, String> officeNameCol;
//	@FXML private TableColumn<RentalHistoryDTO, LocalDateTime> rentalDateCol; // <--- 이 부분이 rentalDateCol이어야 합니다.
//	@FXML private TableColumn<RentalHistoryDTO, LocalDateTime> actualReturnDateCol; // <--- 이 부분이 actualReturnDateCol이어야 합니다.
//	@FXML private TableColumn<RentalHistoryDTO, String> returnStatusCol; // <--- 이 부분이 returnStatusCol이어야 합니다.
//	
//	@FXML private TableColumn<RentalHistoryDTO, Long> overdueDaysCol; // <--- 새 컬럼
//	@FXML private TableColumn<RentalHistoryDTO, Long> overdueFeeCol;  // <--- 새 컬럼
//	@FXML private TableColumn<RentalHistoryDTO, Void> returnActionCol; // 새로 추가된 버튼 컬럼

	// 순번 컬럼 추가
    @FXML private TableColumn<RentalHistoryDTO, Integer> indexCol; // Integer 타입 사용

	@FXML private TableView<RentalHistoryDTO> rentalTable;
	@FXML private TableColumn<RentalHistoryDTO, String> eqNameCol;
	@FXML private TableColumn<RentalHistoryDTO, String> officeNameCol;
	@FXML private TableColumn<RentalHistoryDTO, LocalDateTime> rentalDateCol; // 대여일 / 반납일
	@FXML private TableColumn<RentalHistoryDTO, String> returnStatusCol;     // 상태
	@FXML private TableColumn<RentalHistoryDTO, Long> overdueDaysCol;       // 연체일 / 연체료
//	@FXML private TableColumn<RentalHistoryDTO, Void> returnActionCol;      // 반납하기 버튼
	
    @FXML private Button eqList;
    @FXML private Button myInfoButton;

    private RentalDAO rentalDAO = new RentalDAO();
    
 // 날짜/시간 포맷터 정의 (원하는 형식으로 조절 가능)
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yy-MM-dd HH:mm");
    private final DateTimeFormatter dateOnlyFormatter = DateTimeFormatter.ofPattern("yy-MM-dd"); // 날짜만 필요한 경우


    @Override
    public void initialize(URL location, ResourceBundle resources) {
//        eqNameCol.setCellValueFactory(new PropertyValueFactory<>("eqName"));
//        officeNameCol.setCellValueFactory(new PropertyValueFactory<>("officeName"));
//        returnStatusCol.setCellValueFactory(new PropertyValueFactory<>("returnState"));
//        rentalDateCol.setCellValueFactory(new PropertyValueFactory<>("rentalDate")); // 수정
//        actualReturnDateCol.setCellValueFactory(new PropertyValueFactory<>("actualReturnDate")); // 수정
//     // 반납상태 컬럼은 DTO 필드 값을 직접 표시
//        returnStatusCol.setCellValueFactory(new PropertyValueFactory<>("returnStatus"));
//        
//        overdueDaysCol.setCellValueFactory(new PropertyValueFactory<>("overdueDays")); // Bind to overdueDays
//        overdueFeeCol.setCellValueFactory(new PropertyValueFactory<>("overdueFee"));   // Bind to overdueFee
    	// 순번 컬럼 설정
        indexCol.setCellFactory(column -> new TableCell<RentalHistoryDTO, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(String.valueOf(getIndex() + 1)); // 0부터 시작하므로 +1
                    setAlignment(Pos.CENTER); // 셀 내용 중앙 정렬
                }
            }
        });
        
    	// 기존 컬럼 설정
        eqNameCol.setCellValueFactory(new PropertyValueFactory<>("eqName"));
        officeNameCol.setCellValueFactory(new PropertyValueFactory<>("officeName"));
        returnStatusCol.setCellValueFactory(new PropertyValueFactory<>("returnStatus"));
        
     // ========== 2. 상태 컬럼 중앙 정렬 추가 (내용 중앙 정렬) ==========
        // PropertyValueFactory로 데이터는 가져오지만, CellFactory를 통해 렌더링 방식 제어
        returnStatusCol.setCellValueFactory(new PropertyValueFactory<>("returnStatus"));
        returnStatusCol.setCellFactory(column -> {
            return new TableCell<RentalHistoryDTO, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item);
                        setAlignment(Pos.CENTER); // <-- 여기에 중앙 정렬 명시
                    }
                }
            };
        });
        
        // ========= 대여일 / 반납일 컬럼 커스터마이징 (두 줄 표시) =========
        rentalDateCol.setCellFactory(param -> new TableCell<RentalHistoryDTO, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    setText(null);
                } else {
                    RentalHistoryDTO rental = getTableView().getItems().get(getIndex());
                    VBox vbox = new VBox(2); // 2는 간격
                    vbox.setAlignment(Pos.CENTER); // VBox 내용을 중앙 정렬

                    Text rentalDateText = new Text(rental.getRentalDate().format(dateOnlyFormatter)); // 대여일
                    Text actualReturnDateText = new Text(); // 반납일

                    if (rental.getActualReturnDate() != null) {
                        actualReturnDateText.setText(rental.getActualReturnDate().format(dateOnlyFormatter));
                    } else {
                        actualReturnDateText.setText(""); // 반납일이 없으면 빈 문자열
                    }
                    
                    vbox.getChildren().addAll(rentalDateText, actualReturnDateText);
                    setGraphic(vbox); // VBox를 셀의 그래픽으로 설정
                    setText(null); // 텍스트는 비워둠 (그래픽으로 표시)
                    setAlignment(Pos.CENTER); // 셀 내용 중앙 정렬
                }
            }
        });

        // ========= 연체일 / 연체료 컬럼 커스터마이징 (두 줄 표시) =========
        overdueDaysCol.setCellFactory(param -> new TableCell<RentalHistoryDTO, Long>() {
            @Override
            protected void updateItem(Long item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    setText(null);
                } else {
                    RentalHistoryDTO rental = getTableView().getItems().get(getIndex());
                    VBox vbox = new VBox(2); // 2는 간격
                    vbox.setAlignment(Pos.CENTER); // VBox 내용을 중앙 정렬

                    Text overdueDaysText = new Text(rental.getOverdueDays() + "일");
                    Text overdueFeeText = new Text(String.format("%,d원", rental.getOverdueFee())); // 천 단위 콤마와 '원' 표시

                    vbox.getChildren().addAll(overdueDaysText, overdueFeeText);
                    setGraphic(vbox);
                    setText(null);
                    setAlignment(Pos.CENTER);
                }
            }
        });

    	
    	
//     // 반납 액션 컬럼 설정
//        returnActionCol.setCellFactory(param -> new TableCell<RentalHistoryDTO, Void>() {
//            private final Button returnButton = new Button(); // 버튼 생성
//
//            @Override
//            protected void updateItem(Void item, boolean empty) {
//                super.updateItem(item, empty);
//                if (empty) {
//                    setGraphic(null);
//                } else {
//                    RentalHistoryDTO rental = getTableView().getItems().get(getIndex()); // 해당 행의 DTO 가져오기
//
//                    // 반납 상태에 따라 버튼 텍스트 및 활성화 여부 설정
//                    if ("반납완료".equals(rental.getReturnStatus())) {
//                        returnButton.setText("반납완료");
//                        returnButton.setStyle("-fx-background-color: #f0f0f0; -fx-text-fill: #555555;"); // 비활성화된 버튼 스타일
//                        returnButton.setDisable(true); // 버튼 비활성화
//                    } else if ("대여중".equals(rental.getReturnStatus())) { // '대여중' 상태일 때만 '반납하기' 버튼
//                        returnButton.setText("반납하기");
//                        returnButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white;"); // 활성화된 버튼 스타일
//                        returnButton.setDisable(false); // 버튼 활성화
//                        returnButton.setOnAction(event -> {
//                            // 여기에 반납 처리 로직 구현 (예: DAO 호출)
//                            // 예시: showReturnConfirmation(rental);
//                            System.out.println("반납하기 클릭: " + rental.getRentalNum() + " - " + rental.getEqName());
//                            // TODO: 실제 반납 처리 로직 추가 (DB 업데이트 등)
//                            // 처리 후 테이블 갱신 (선택 사항)
//                             loadRentalHistory();
//                        });
//                    } else {
//                        // 그 외의 알 수 없는 상태는 버튼 표시 안 함
//                        setGraphic(null);
//                        return; // 버튼을 표시하지 않고 종료
//                    }
//                    
//                    // 버튼의 크기 및 정렬 설정
//                    returnButton.setPrefWidth(80); // 버튼 너비 고정
//                    returnButton.setPrefHeight(25); // 버튼 높이 고정
//                    setAlignment(Pos.CENTER); // 셀 중앙 정렬
//                    setGraphic(returnButton); // 버튼을 셀의 그래픽으로 설정
//                }
//            }
//        });



        loadRentalHistory();
    }

    private void loadRentalHistory() {
        List<RentalHistoryDTO> list = rentalDAO.findRentalsByUserId(userId);
        ObservableList<RentalHistoryDTO> observableList = FXCollections.observableArrayList(list);
        rentalTable.setItems(observableList);
    }

    public void handleEqList(ActionEvent event) {
        try {
            Parent mainView = FXMLLoader.load(getClass().getResource("/view/root.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(mainView));
            stage.setTitle("MainView");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleMyInfo(ActionEvent event) {
        try {
            Parent myInfoView = FXMLLoader.load(getClass().getResource("/view/my_info.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(myInfoView));
            stage.setTitle("내 정보");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
