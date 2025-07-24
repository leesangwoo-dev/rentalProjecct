
package controller;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors; // 필터링을 위해 추가

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
import javafx.scene.control.CheckBox; // 연체자 필터링 체크박스를 위해 추가
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.OverdueHistoryDTO; // 모든 대여 기록을 포함하는 DTO로 간주

public class AdminRentalHistoryController implements Initializable {

	@FXML
	private TableView<OverdueHistoryDTO> rentalTable; // overdueTable 제거, rentalTable로 통합
	@FXML
	private TableColumn<OverdueHistoryDTO, Integer> indexCol; // 순차번호 컬럼
	@FXML
	private TableColumn<OverdueHistoryDTO, String> idCol; // userLoginId
	@FXML
	private TableColumn<OverdueHistoryDTO, String> phoneNumberCol; // 이름/번호
	@FXML
	private TableColumn<OverdueHistoryDTO, String> officeNameCol;
	@FXML
	private TableColumn<OverdueHistoryDTO, String> eqNameCol; // 장비명/시리얼 번호
	@FXML
	private TableColumn<OverdueHistoryDTO, LocalDateTime> rentalDateCol; // 대여일 / 반납일
	@FXML
	private TableColumn<OverdueHistoryDTO, Long> overdueDaysCol; // 연체일/연체료

	@FXML
	private Button adminEqList;
	@FXML
	private Button adminRentalList;
	// @FXML private Button overdueHistory; // 연체정보 nav 제거

	@FXML
	private CheckBox showOverdueOnlyCheckBox; // 연체자만 필터링하는 체크박스 추가

	private RentalDAO rentalDAO = new RentalDAO(); // DAO 인스턴스

	private final DateTimeFormatter dateOnlyFormatter = DateTimeFormatter.ofPattern("yy-MM-dd");

	// 전체 대여 기록을 저장할 리스트 (필터링을 위해 필요)
	private ObservableList<OverdueHistoryDTO> allRentals;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// 일련번호 컬럼 설정
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

        officeNameCol.setCellValueFactory(new PropertyValueFactory<>("officeName"));

		// (장비명 / 시리얼 번호를 함께 표시하도록 CellFactory 설정 )
		eqNameCol.setCellFactory(param -> new TableCell<OverdueHistoryDTO, String>() {
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
					Text eqNameText = new Text(rental.getEqName());
					Text serialNumText = new Text(rental.getSerialNum()); // DTO에서 serialNum 가져오기
					vbox.getChildren().addAll(eqNameText, serialNumText);
					setGraphic(vbox);
					setText(null);
					setAlignment(Pos.CENTER);
				}
			}
		});
		// eqNameCol의 PropertyValueFactory는 필요하지 않음, CellFactory가 직접 데이터를 설정하므로.
		// 하지만 DTO 필드와 컬럼 타입을 맞추기 위해 PropertyValueFactory를 유지하는 것이 일반적입니다.
		// 여기서는 String 타입으로 설정되어 있으므로, DTO의 eqName 필드와 연결됩니다.

		// 날짜 컬럼 (대여일 / 반납 예정일 또는 실제 반납일)
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
					Text returnDateText = new Text();
					if (rental.getActualReturnDate() != null) {
						returnDateText.setText(rental.getActualReturnDate().format(dateOnlyFormatter));
					} else {
						// 실제 반납일이 없으면 반납 예정일을 표시
						returnDateText.setText(rental.getReturnDate().format(dateOnlyFormatter) + " (예정)");
					}
					vbox.getChildren().addAll(rentalDateText, returnDateText);
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
					// 연체일이 0보다 크거나 반납상태가 '연체'인 경우에만 표시
					if (rental.getOverdueDays() > 0 || "연체".equals(rental.getReturnStatus())) {
						Text overdueDaysText = new Text(rental.getOverdueDays() + "일");
						Text overdueFeeText = new Text(String.format("%,d원", rental.getOverdueFee()));
						vbox.getChildren().addAll(overdueDaysText, overdueFeeText);
					} else {
						// 연체가 아닌 경우 빈 칸으로 표시
						vbox.getChildren().add(new Text(""));
					}
					setGraphic(vbox);
					setText(null);
					setAlignment(Pos.CENTER);
				}
			}
		});

		// 연체자 필터링 체크박스 리스너 설정
		showOverdueOnlyCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
			applyFilter();
		});

		loadAllRentals(); // 모든 대여 목록 로드
	}

	// 모든 대여 목록을 로드하는 메서드 (초기 로딩 및 갱신 시 사용)
	private void loadAllRentals() {
		List<OverdueHistoryDTO> list = rentalDAO.findAllRentalsForAdmin(); // 모든 대여 기록을 가져오는 DAO 메서드 호출
		System.out.println(list);
		allRentals = FXCollections.observableArrayList(list);
		applyFilter(); // 로드 후 필터 적용 (초기에는 필터링 해제 상태로 모든 데이터 표시)
	}

	// 필터를 적용하여 테이블 뷰를 업데이트하는 메서드
	private void applyFilter() {
		if (showOverdueOnlyCheckBox.isSelected()) {
			// 연체된 기록만 필터링
			List<OverdueHistoryDTO> filteredList = allRentals.stream()
					.filter(rental -> rental.getOverdueDays() > 0 || "연체".equals(rental.getReturnStatus())) // 연체일이 0보다
																											// 크거나 상태가
																											// '연체'인 경우
					.collect(Collectors.toList());
			rentalTable.setItems(FXCollections.observableArrayList(filteredList));
		} else {
			// 모든 기록 표시
			rentalTable.setItems(allRentals);
		}
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

	// 대여정보 nav (현재 페이지이므로 새로고침 효과)
	@FXML
	private void handleAdminRentalList(ActionEvent event) {
		// 현재 페이지를 다시 로드하여 최신 상태를 반영
		try {
			Parent adminRentalView = FXMLLoader.load(getClass().getResource("/view/AdminRentalHistoryView.fxml"));
			Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			stage.setScene(new Scene(adminRentalView));
			stage.setTitle("전체 대여내역 조회");
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
			showAlert("페이지 로드 오류", null, "대여정보 페이지를 로드할 수 없습니다.");
		}
	}

	// 연체 처리 관련 메서드는 모두 제거됩니다.
	// showAdminOverdueProcess 및 processReturnOnly 메서드는 더 이상 필요 없습니다.

	// ========== 일반적인 알림창 메서드 ==========
	private void showAlert(String title, String header, String content) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(content);
		alert.showAndWait();
	}
}