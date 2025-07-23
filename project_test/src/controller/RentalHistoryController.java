package controller;

import static util.Session.userLoginId;

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
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.RentalHistoryDTO;

public class RentalHistoryController implements Initializable {

	@FXML
	private TableColumn<RentalHistoryDTO, Integer> indexCol;
	@FXML
	private TableView<RentalHistoryDTO> rentalTable;
	@FXML
	private TableColumn<RentalHistoryDTO, String> eqNameCol;
	@FXML
	private TableColumn<RentalHistoryDTO, String> officeNameCol;
	@FXML
	private TableColumn<RentalHistoryDTO, LocalDateTime> rentalDateCol; // 대여일 / 반납일
	@FXML
	private TableColumn<RentalHistoryDTO, String> returnStatusCol; // 상태
	@FXML
	private TableColumn<RentalHistoryDTO, Long> overdueDaysCol; // 연체일 / 연체료
	@FXML
	private TableColumn<RentalHistoryDTO, Void> returnActionCol; // 반납하기 버튼

	@FXML
	private Button eqList;
	@FXML
	private Button myInfoButton;

	private RentalDAO rentalDAO = new RentalDAO();

	private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yy-MM-dd HH:mm");
	private final DateTimeFormatter dateOnlyFormatter = DateTimeFormatter.ofPattern("yy-MM-dd");

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		indexCol.setCellFactory(column -> new TableCell<RentalHistoryDTO, Integer>() {
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

		eqNameCol.setCellValueFactory(new PropertyValueFactory<>("eqName"));
		officeNameCol.setCellValueFactory(new PropertyValueFactory<>("officeName"));

		rentalDateCol.setCellFactory(param -> new TableCell<RentalHistoryDTO, LocalDateTime>() {
			@Override
			protected void updateItem(LocalDateTime item, boolean empty) {
				super.updateItem(item, empty);
				if (empty) {
					setGraphic(null);
					setText(null);
				} else {
					RentalHistoryDTO rental = getTableView().getItems().get(getIndex());
					VBox vbox = new VBox(2);
					vbox.setAlignment(Pos.CENTER);
					Text rentalDateText = new Text(rental.getRentalDate().format(dateOnlyFormatter));
					Text actualReturnDateText = new Text();
					if (rental.getActualReturnDate() != null) {
						actualReturnDateText.setText(rental.getActualReturnDate().format(dateOnlyFormatter));
					} else {
						actualReturnDateText.setText("");
					}
					vbox.getChildren().addAll(rentalDateText, actualReturnDateText);
					setGraphic(vbox);
					setText(null);
					setAlignment(Pos.CENTER);
				}
			}
		});

		overdueDaysCol.setCellFactory(param -> new TableCell<RentalHistoryDTO, Long>() {
			@Override
			protected void updateItem(Long item, boolean empty) {
				super.updateItem(item, empty);
				if (empty) {
					setGraphic(null);
					setText(null);
				} else {
					RentalHistoryDTO rental = getTableView().getItems().get(getIndex());
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

		returnActionCol.setCellFactory(param -> new TableCell<RentalHistoryDTO, Void>() {
			private final Button returnButton = new Button();

			@Override
			protected void updateItem(Void item, boolean empty) {
				super.updateItem(item, empty);
				if (empty) {
					setGraphic(null);
				} else {
					RentalHistoryDTO rental = getTableView().getItems().get(getIndex());

					if ("반납완료".equals(rental.getReturnStatus())) {
						returnButton.setText("반납완료");
						returnButton.setStyle(
								"-fx-background-color: #f0f0f0; -fx-text-fill: #555555; -fx-background-radius: 20; -fx-border-radius: 20;");
						returnButton.setDisable(true);
					} else if ("대여중".equals(rental.getReturnStatus())) {
						returnButton.setText("반납하기");
						returnButton.setStyle(
								"-fx-background-color: #28a745; -fx-text-fill: white; -fx-background-radius: 20; -fx-border-radius: 20;");
						returnButton.setDisable(false);

						returnButton.setOnAction(event -> {
							System.out.println("반납하기 클릭: " + rental.getRentalNum() + " - " + rental.getEqName());

							long currentOverdueFee = rental.getOverdueFee();

							if (currentOverdueFee > 0) {
								showOverduePaymentProcess(rental); // 연체료 발생시 프로세스 시작
							} else {
								processReturnOnly(rental); // 일반 반납 처리
							}
						});

					} else {
						setGraphic(null);
						return;
					}

					returnButton.setPrefWidth(110);
					returnButton.setPrefHeight(25);
					setAlignment(Pos.CENTER);
					setGraphic(returnButton);
				}
			}
		});

		loadRentalHistory();
	}

	private void loadRentalHistory() {
		List<RentalHistoryDTO> list = rentalDAO.findRentalsByUserId(userLoginId);
		ObservableList<RentalHistoryDTO> observableList = FXCollections.observableArrayList(list);
		rentalTable.setItems(observableList);
	}
	
	private void showOverduePaymentProcess(RentalHistoryDTO rental) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		showAlert(
				"연체료 발생", 
				rental.getEqName() + " (대여번호: " + rental.getRentalNum() + ")",
				"이 장비는 현재 연체되었습니다.\n" + 
				"연체일: " + rental.getOverdueDays() + "일\n" + 
				"납부할 연체료: "+ String.format("%,d원", rental.getOverdueFee())
		);
		processReturnOnly(rental);
	}

	// ========== 순수 반납 처리 로직 메서드 (새로 추가되거나 수정됨) ==========
	private void processReturnOnly(RentalHistoryDTO rental) {
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
		loadRentalHistory(); // 테이블 갱신
	}

	// ========== 내비게이션 메서드 (기존과 동일, 변경 없음) ==========
	public void handleEqList(ActionEvent event) {
		try {
			Parent mainView = FXMLLoader.load(getClass().getResource("/view/MainView.fxml"));
			Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			stage.setScene(new Scene(mainView));
			stage.setTitle("MainView");
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
//	@FXML
//	public void handleAdminEqList(ActionEvent event) {
//		try {
//			Parent adminView = FXMLLoader.load(getClass().getResource("/view/AdminView.fxml"));
//			Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
//			stage.setScene(new Scene(adminView));
//			stage.setTitle("관리자 장비 조회");
//			stage.show();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}

	@FXML
	private void handleMyInfo(ActionEvent event) {
		try {
			// FXML 로더를 사용하여 "내 정보 수정" FXML 로드
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MyInfoView.fxml")); // FXML 파일명 확인!
			Parent myInfoView = loader.load();

			// 1. 새로운 Stage (팝업 창) 생성
			Stage myInfoStage = new Stage();
			myInfoStage.setTitle("내 정보 수정");
			myInfoStage.setScene(new Scene(myInfoView));

			// 2. 모달리티 설정: APPLICATION_MODAL 또는 WINDOW_MODAL
			// APPLICATION_MODAL: 이 애플리케이션의 모든 다른 창을 차단합니다.
			// WINDOW_MODAL: 특정 부모 창만 차단합니다.
			myInfoStage.initModality(Modality.APPLICATION_MODAL); // <-- 이 부분이 핵심!

			// 3. 부모 창 설정 (선택 사항이지만 권장):
			// 팝업 창이 어떤 창에 종속되는지 지정합니다.
			// 이렇게 하면 부모 창이 최소화될 때 자식 창도 함께 최소화되는 등의 동작을 합니다.
			Stage ownerStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			myInfoStage.initOwner(ownerStage); // <-- 이 부분이 부모 창을 지정합니다.

			// 팝업 창을 보여줍니다. 이 창이 닫힐 때까지 이 메서드는 블록됩니다.
			myInfoStage.showAndWait(); // <-- show() 대신 showAndWait() 사용!

		} catch (IOException e) {
			e.printStackTrace();
			// 오류 발생 시 사용자에게 알림
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("오류");
			alert.setHeaderText("페이지 로드 실패");
			alert.setContentText("내 정보 수정 화면을 불러오는 데 실패했습니다.");
			alert.showAndWait();
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
