package controller;

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
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.OverdueHistoryDTO;

public class AdminRentalHistoryController implements Initializable {

	@FXML
	private TableView<OverdueHistoryDTO> overdueTable;
	@FXML
	private TableView<OverdueHistoryDTO> rentalTable;
	@FXML
	private TableColumn<OverdueHistoryDTO, Integer> indexCol; // 필요하면 순차 번호 또는 rentalNum
	@FXML
	private TableColumn<OverdueHistoryDTO, String> idCol; // userLoginId
	@FXML
    private TableColumn<OverdueHistoryDTO, String> userNameCol;
	@FXML
	private TableColumn<OverdueHistoryDTO, String> phoneNumberCol; // 이름/번호
	@FXML
	private TableColumn<OverdueHistoryDTO, String> officeNameCol;
	@FXML
	private TableColumn<OverdueHistoryDTO, String> eqNameCol;
	@FXML
	private TableColumn<OverdueHistoryDTO, LocalDateTime> rentalDateCol; // 또는 LocalDateTime
	@FXML
	private TableColumn<OverdueHistoryDTO, Long> overdueDaysCol; // 연체일/연체료
	@FXML
	private TableColumn<OverdueHistoryDTO, Void> returnActionCol; // "반납" 버튼

	@FXML
	private Button adminEqList;
	@FXML
	private Button adminRentalList;
	@FXML
    private Button overdueHistory; // FXML에 fx:id="overdueHistory" 추가할 예정
    

	private RentalDAO rentalDAO = new RentalDAO(); // DAO 인스턴스

//	private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yy-MM-dd HH:mm");
	private final DateTimeFormatter dateOnlyFormatter = DateTimeFormatter.ofPattern("yy-MM-dd");

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		indexCol.setCellFactory(column -> new TableCell<OverdueHistoryDTO, Integer>() {
			@Override
			protected void updateItem(Integer item, boolean empty) {
				super.updateItem(item, empty);
				if (empty) {
					setText(null);
				} else {
					setText(String.valueOf(getIndex() + 1));
					setAlignment(Pos.CENTER);
				}
			}
		});

		// 새로운 컬럼 매핑
		idCol.setCellValueFactory(new PropertyValueFactory<>("loginId")); // DTO 필드명

//		userNameCol.setCellValueFactory(new PropertyValueFactory<>("userName"));
//		phoneNumberCol.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
		// phoneNumberCol에 이름/연락처를 함께 표시하도록 CellFactory 설정
		phoneNumberCol.setCellFactory(param -> new TableCell<OverdueHistoryDTO, String>() {
			@Override
			protected void updateItem(String item, boolean empty) {
				super.updateItem(item, empty);
				if (empty) {
					setGraphic(null);
					setText(null);
				} else {
					OverdueHistoryDTO rental = getTableView().getItems().get(getIndex());
					VBox vbox = new VBox(2);
					vbox.setAlignment(Pos.CENTER);
					Text userNameText = new Text(rental.getUserName()); // userName 추가
					Text phoneNumberText = new Text(rental.getPhoneNumber()); // phoneNumber 추가
					vbox.getChildren().addAll(userNameText, phoneNumberText);
					setGraphic(vbox);
					setText(null);
					setAlignment(Pos.CENTER);
				}
			}
		});

		eqNameCol.setCellValueFactory(new PropertyValueFactory<>("eqName"));
		officeNameCol.setCellValueFactory(new PropertyValueFactory<>("officeName"));

		// 날짜 컬럼 (대여일 / 반납 예정일)
		rentalDateCol.setCellFactory(param -> new TableCell<OverdueHistoryDTO, LocalDateTime>() {
			@Override
			protected void updateItem(LocalDateTime item, boolean empty) {
				super.updateItem(item, empty);
				if (empty) {
					setGraphic(null);
					setText(null);
				} else {
					OverdueHistoryDTO rental = getTableView().getItems().get(getIndex());
					VBox vbox = new VBox(2);
					vbox.setAlignment(Pos.CENTER);
					Text rentalDateText = new Text(rental.getRentalDate().format(dateOnlyFormatter));
					Text actualReturnDateText = new Text();
					if (rental.getActualReturnDate() != null) {
						actualReturnDateText.setText(rental.getActualReturnDate().format(dateOnlyFormatter));
					} else {
						// 실제 반납일이 없으면 반납 예정일을 표시
						actualReturnDateText.setText(rental.getReturnDate().format(dateOnlyFormatter) + " (예정)");
					}
					vbox.getChildren().addAll(rentalDateText, actualReturnDateText);
					setGraphic(vbox);
					setText(null);
					setAlignment(Pos.CENTER);
				}
			}
		});

		// 연체일 / 연체료 컬럼
		overdueDaysCol.setCellFactory(param -> new TableCell<OverdueHistoryDTO, Long>() {
			@Override
			protected void updateItem(Long item, boolean empty) {
				super.updateItem(item, empty);
				if (empty) {
					setGraphic(null);
					setText(null);
				} else {
					OverdueHistoryDTO rental = getTableView().getItems().get(getIndex());
					VBox vbox = new VBox(2);
					vbox.setAlignment(Pos.CENTER);
					Text overdueDaysText = new Text(rental.getOverdueDays() + "일");
					Text overdueFeeText = new Text(String.format("%,d원", rental.getOverdueFee()));
					vbox.getChildren().addAll(overdueDaysText, overdueFeeText);
					setGraphic(vbox);
					setText(null);
					setAlignment(Pos.CENTER);
				}
			}
		});

		// 반납 처리 버튼 컬럼 (관리자용)
		returnActionCol.setCellFactory(param -> new TableCell<OverdueHistoryDTO, Void>() {
			private final Button returnButton = new Button();

			@Override
			protected void updateItem(Void item, boolean empty) {
				super.updateItem(item, empty);
				if (empty) {
					setGraphic(null);
				} else {
					OverdueHistoryDTO rental = getTableView().getItems().get(getIndex());

					// 관리자는 '대여중' 상태일 때만 반납 처리 버튼을 활성화
					if ("대여중".equals(rental.getReturnStatus())) {
						returnButton.setText("반납처리"); // 관리자 화면에 맞게 텍스트 변경
						returnButton.setStyle(
								"-fx-background-color: #28a745; -fx-text-fill: white; -fx-background-radius: 20; -fx-border-radius: 20;");
						returnButton.setDisable(false); // 활성화

						returnButton.setOnAction(event -> {
							System.out.println("관리자 반납 처리 클릭: " + rental.getRentalNum() + " - " + rental.getEqName());

							long currentOverdueFee = rental.getOverdueFee();

							if (currentOverdueFee > 0) {
								// 연체료가 있을 경우, 관리자는 '연체료를 0으로 만드는 프로세스'를 추가로 진행
								showAdminOverdueProcess(rental);
							} else {
								// 연체료가 없으면 바로 반납 처리
								processReturnOnly(rental);
							}
						});
					} else { // '반납완료' 등의 상태이면 버튼 비활성화
						returnButton.setText("완료"); // 또는 "반납완료"
						returnButton.setStyle(
								"-fx-background-color: #f0f0f0; -fx-text-fill: #555555; -fx-background-radius: 20; -fx-border-radius: 20;");
						returnButton.setDisable(true);
					}

					returnButton.setPrefWidth(110);
					returnButton.setPrefHeight(25);
					setAlignment(Pos.CENTER);
					setGraphic(returnButton);
				}
			}
		});

		loadAllRentals(); // 모든 대여 목록 로드
	}

	// 모든 대여 목록을 로드하는 메서드
    private void loadAllRentals() {
        // 이 부분에 모든 대여 기록을 가져오는 DAO 메서드를 호출하도록 변경해야 합니다.
        List<OverdueHistoryDTO> list = rentalDAO.findAllRentalsForAdmin(); // 새로 만들 DAO 메서드
        ObservableList<OverdueHistoryDTO> observableList = FXCollections.observableArrayList(list);
        rentalTable.setItems(observableList);
    }

	// 관리자 연체 처리 프로세스
	private void showAdminOverdueProcess(OverdueHistoryDTO rental) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("연체료 처리");
		alert.setHeaderText(rental.getEqName() + " (대여번호: " + rental.getRentalNum() + ") 연체료 발생");
		alert.setContentText("연체일: " + rental.getOverdueDays() + "일\n" + "현재 연체료: "
				+ String.format("%,d원", rental.getOverdueFee()) + "\n\n" + "연체료를 0으로 처리하시겠습니까? (이 작업 후 반납 처리됩니다)");

		// 확인 버튼을 누르면 연체료 0으로 처리 후 반납 진행
		alert.showAndWait().ifPresent(response -> {
			if (response == javafx.scene.control.ButtonType.OK) {
				// 연체료를 0으로 만드는 프로시저 호출
				String zeroFeeStatus = rentalDAO.updateOverdueFeeToZero(rental.getRentalNum());

				if ("SUCCESS".equals(zeroFeeStatus)) {
					showAlert("연체료 처리 완료", null, "연체료가 성공적으로 0으로 처리되었습니다. 이제 장비를 반납합니다.");
					processReturnOnly(rental); // 연체료 0 처리 후 일반 반납 진행
				} else if ("FAIL_NOT_FOUND".equals(zeroFeeStatus)) {
					showAlert("오류", null, "해당 대여 기록을 찾을 수 없거나 이미 연체료가 0입니다.");
				} else {
					showAlert("오류", null, "연체료 0 처리 중 알 수 없는 오류가 발생했습니다: " + zeroFeeStatus);
				}
			} else {
				// 취소하면 아무것도 하지 않음
				showAlert("취소", null, "연체료 처리가 취소되었습니다.");
			}
		});
	}

	// ========== 순수 반납 처리 로직 메서드 (기존 RentalHistoryController와 유사) ==========
	private void processReturnOnly(OverdueHistoryDTO rental) {
		String resultStatus = rentalDAO.processReturn(rental.getRentalNum());

		switch (resultStatus) {
		case "SUCCESS":
			showAlert("반납 완료", null, rental.getEqName() + " 장비 반납이 성공적으로 처리되었습니다.");
			break;
		case "ALREADY_RETURNED":
			showAlert("반납 실패", null, "이미 반납이 완료된 장비입니다.");
			break;
		case "NOT_FOUND":
			showAlert("오류", null, "해당 대여 기록을 찾을 수 없습니다.");
			break;
		case "ERROR": // 기타 SQL 에러 등
		default:
			showAlert("오류", null, "알 수 없는 오류로 반납에 실패했습니다: " + resultStatus + ". 관리자에게 문의하세요.");
			break;
		}
		loadAllRentals(); // 테이블 갱신
	}

	// FXML에서 호출될 '장비조회' 버튼 핸들러
	@FXML
	public void handleAdminEqList(ActionEvent event) {
		try {
			Parent adminView = FXMLLoader.load(getClass().getResource("/view/AdminView.fxml"));
			Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			stage.setScene(new Scene(adminView));
			stage.setTitle("관리자 장비 조회");
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
			showAlert("페이지 로드 오류", null, "장비조회 페이지를 로드할 수 없습니다.");
		}
	}

	// 대여정보 nav
	@FXML
	private void handleAdminRentalList(ActionEvent event) {
		try {
			Parent adminRnetalView = FXMLLoader.load(getClass().getResource("/view/AdminRentalHistoryView.fxml"));
			Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			stage.setScene(new Scene(adminRnetalView));
			stage.setTitle("전체 대여내역 조회");
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
			showAlert("페이지 로드 오류", null, "장비조회 페이지를 로드할 수 없습니다.");
		}
	}
	
	// 연체정보 nav
	@FXML
	private void handleOverdueHistory(ActionEvent event) {
		try {
			Parent adminRnetalView = FXMLLoader.load(getClass().getResource("/view/OverdueHistoryView.fxml"));
			Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			stage.setScene(new Scene(adminRnetalView));
			stage.setTitle("전체 대여내역 조회");
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
			showAlert("페이지 로드 오류", null, "장비조회 페이지를 로드할 수 없습니다.");
		}
	}
	

	// ========== 일반적인 알림창 메서드 ==========
	private void showAlert(String title, String header, String content) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(content);
		alert.showAndWait();
	}
}
