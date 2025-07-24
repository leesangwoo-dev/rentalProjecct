package controller;

import static utils.Session.userLoginId;
import static utils.ShowAlert.showAlert;

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
import javafx.scene.control.Separator;
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

	@FXML
	private Separator rentalDateHeaderSeparator;
	@FXML
	private Separator overdueDaysHeaderSeparator;

	private RentalDAO rentalDAO = new RentalDAO();

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

					// # 변경된 부분: 구분선 추가
					Separator separator = new Separator();
					separator.prefWidthProperty().bind(rentalDateCol.widthProperty().subtract(5));
					separator.setStyle("-fx-background-color: #e0e0e0; -fx-min-height: 1px; -fx-max-height: 1px;");

					// 만약 이 셀에도 구분선을 넣고 싶다면, 아래 라인을 추가해야 합니다.
					Separator cellSeparator = new Separator();
					cellSeparator.prefWidthProperty().bind(rentalDateCol.widthProperty().subtract(5));
					cellSeparator.setStyle("-fx-background-color: #e0e0e0; -fx-min-height: 1px; -fx-max-height: 1px;");
					vbox.getChildren().addAll(rentalDateText, cellSeparator, actualReturnDateText); // 구분선 추가

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

					// # 변경된 부분: 구분선 추가
					Separator separator = new Separator();
					separator.prefWidthProperty().bind(overdueDaysCol.widthProperty().subtract(5));
					separator.setStyle("-fx-background-color: #e0e0e0; -fx-min-height: 1px; -fx-max-height: 1px;");

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
		// # 핵심 변경 부분: 테이블 너비 변경 리스너 추가 (반응형 레이아웃)
		rentalTable.widthProperty().addListener((obs, oldVal, newVal) -> {
			double totalWidth = newVal.doubleValue();
			// 스크롤바 너비를 대략적으로 고려 (Windows 기준 약 17px)
			double scrollBarCompensation = 18;
			double usableWidth = totalWidth - scrollBarCompensation;

			// 각 컬럼에 비율 할당 (총합 1.0 = 100%가 되도록 조정)
			// 이 비율은 임의로 설정한 값이므로, 실제 UI에서 데이터가 잘 보이도록 미세 조정해야 합니다.
			indexCol.setPrefWidth(usableWidth * 0.05); // 5%
			eqNameCol.setPrefWidth(usableWidth * 0.23); // 20%
			officeNameCol.setPrefWidth(usableWidth * 0.22); // 20%
			rentalDateCol.setPrefWidth(usableWidth * 0.20); // 20%
			returnActionCol.setPrefWidth(usableWidth * 0.12); // 10%
//			returnStatusCol.setPrefWidth(usableWidth * 0.15); // 10%
			overdueDaysCol.setPrefWidth(usableWidth * 0.20); // 15%
			// 합계: 0.05 + 0.20 + 0.20 + 0.20 + 0.10 + 0.15 + 0.10 = 1.00 (100%)

			// # FXML에서 fx:id를 부여한 헤더 Separator들의 너비를 바인딩합니다.
			// null 체크를 통해 객체가 로드되었는지 확인합니다.
			// 컬럼 너비에서 약간의 여백을 주는 것이 시각적으로 좋습니다 (예: -5px)
			if (rentalDateHeaderSeparator != null) {
				rentalDateHeaderSeparator.prefWidthProperty().bind(rentalDateCol.widthProperty().subtract(5));
			}
			if (overdueDaysHeaderSeparator != null) {
				overdueDaysHeaderSeparator.prefWidthProperty().bind(overdueDaysCol.widthProperty().subtract(5));
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
		showAlert(Alert.AlertType.INFORMATION, "연체료 발생",
				rental.getEqName() + "이 장비(" + rental.getRentalNum() + ")는 현재 연체되었습니다.\n" + "연체일: "
						+ rental.getOverdueDays() + "일\n" + "납부할 연체료: "
						+ String.format("%,d원", rental.getOverdueFee()));
		processReturnOnly(rental);
	}

	// ========== 순수 반납 처리 로직 메서드 (새로 추가되거나 수정됨) ==========
	private void processReturnOnly(RentalHistoryDTO rental) {
		String resultStatus = rentalDAO.processReturn(rental.getRentalNum());

		switch (resultStatus) {
		case "SUCCESS":
			showAlert(Alert.AlertType.INFORMATION, "반납 완료", rental.getEqName() + " 장비 반납이 성공적으로 처리되었습니다.");
			break;
		case "ALREADY_RETURNED":
			showAlert(Alert.AlertType.ERROR, "반납 실패", "이미 반납이 완료된 장비입니다.");
			break;
		case "NOT_FOUND":
			showAlert(Alert.AlertType.ERROR, "오류", "해당 대여 기록을 찾을 수 없습니다.");
			break;
		case "ERROR": // 기타 SQL 에러 등
		default:
			showAlert(Alert.AlertType.ERROR, "오류", "알 수 없는 오류로 반납에 실패했습니다: " + resultStatus + ". 관리자에게 문의하세요.");
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
}
