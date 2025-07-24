
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
import javafx.scene.control.Separator;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.OverdueHistoryDTO; // 모든 대여 기록을 포함하는 DTO로 간주
import utils.Session;

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
	private Button adminEqList; // 장비 조회 버튼
	@FXML
	private Button adminRentalList;
<<<<<<< HEAD

=======
>>>>>>> stash
	@FXML
	private CheckBox showOverdueOnlyCheckBox; // 연체자만 필터링하는 체크박스 추가
	@FXML
	private Separator phoneNumHeaderSeparator;
	@FXML
	private Separator eqNameHeaderSeparator;
	@FXML
	private Separator rentalDateHeaderSeparator;
	@FXML
	private Separator overdueDaysHeaderSeparator;

	private RentalDAO rentalDAO = new RentalDAO(); // DAO 인스턴스
<<<<<<< HEAD

	// datetime 포맷용도
=======
>>>>>>> stash
	private final DateTimeFormatter dateOnlyFormatter = DateTimeFormatter.ofPattern("yy-MM-dd");
	private ObservableList<OverdueHistoryDTO> allRentals; // 전체 대여 기록을 저장할 리스트 (필터링을 위해 필요)
	private String currentUserGu; // # 추가된 부분: 현재 로그인한 관리자의 담당 지역을 저장할 필드

	// init 컬럼 매핑
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// # 변경된 부분: 초기화 시 현재 로그인한 관리자의 GU 가져오기
		currentUserGu = Session.userGu;

		// 관리자 역할이고 대여소 정보가 없는 경우 오류 메시지
		if ("ADMIN".equals(Session.userRole) && (currentUserGu == null || currentUserGu.trim().isEmpty())) {
			showAlert("오류", null, "관리자 대여소 정보가 없습니다. 다시 로그인해주세요.");
			// 이 경우, 테이블을 로드하지 않거나 비활성화하는 등의 추가 처리가 필요할 수 있습니다.
			return;
		}
		// 순번 컬럼 설정
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

					// # 변경된 부분: 구분선 추가
					Separator separator = new Separator();
					separator.prefWidthProperty().bind(phoneNumberCol.widthProperty().subtract(5));
					separator.setStyle("-fx-background-color: #e0e0e0; -fx-min-height: 1px; -fx-max-height: 1px;"); // 구분선

					vbox.getChildren().addAll(userNameText, separator, phoneNumberText); // 구분선 추가
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
					Text serialNumText = new Text(rental.getSerialNum());

					// # 변경된 부분: 데이터 셀 내부의 Separator 너비 바인딩
					Separator separator = new Separator();
					separator.prefWidthProperty().bind(eqNameCol.widthProperty().subtract(5));
					separator.setStyle("-fx-background-color: #e0e0e0; -fx-min-height: 1px; -fx-max-height: 1px;");

					vbox.getChildren().addAll(eqNameText, separator, serialNumText);
					setGraphic(vbox); // setGraphic은 TableCell에 한번만 호출
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

					// # 변경된 부분: 구분선 추가
					Separator separator = new Separator();
					separator.prefWidthProperty().bind(rentalDateCol.widthProperty().subtract(5));
					separator.setStyle("-fx-background-color: #e0e0e0; -fx-min-height: 1px; -fx-max-height: 1px;");

					vbox.getChildren().addAll(rentalDateText, separator, returnDateText); // 구분선 추가
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

						// # 변경된 부분: 구분선 추가
						Separator separator = new Separator();
						separator.prefWidthProperty().bind(overdueDaysCol.widthProperty().subtract(5));
						separator.setStyle("-fx-background-color: #e0e0e0; -fx-min-height: 1px; -fx-max-height: 1px;");

						vbox.getChildren().addAll(overdueDaysText, separator, overdueFeeText); // 구분선 추가
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

		// # 변경된 부분: TableView의 너비 변화에 따라 컬럼 너비를 동적으로 조정합니다.
		rentalTable.widthProperty().addListener((obs, oldVal, newVal) -> {
			double totalWidth = newVal.doubleValue();
			// 스크롤바 너비를 대략적으로 고려 (필요에 따라 조정)
			// Windows의 기본 스크롤바 너비는 약 17px 정도이며, 이 값을 고려하여 컬럼 너비를 조정합니다.
			// 정확한 값은 운영체제나 JavaFX 버전에 따라 다를 수 있으므로, 실행 후 미세 조정이 필요합니다.
			double scrollBarCompensation = 18; // 스크롤바 너비만큼 제외

			double usableWidth = totalWidth - scrollBarCompensation;

			// 각 컬럼에 비율 할당 (총합 1.0 = 100%가 되도록 조정)
			// 이 비율은 임의로 설정한 값이므로, 실제 UI에서 데이터가 잘 보이도록 미세 조정해야 합니다.
			indexCol.setPrefWidth(usableWidth * 0.04); // 4%
			idCol.setPrefWidth(usableWidth * 0.08); // 8%
			phoneNumberCol.setPrefWidth(usableWidth * 0.15); // 15%
			officeNameCol.setPrefWidth(usableWidth * 0.20); // 20%
			eqNameCol.setPrefWidth(usableWidth * 0.20); // 20%
			rentalDateCol.setPrefWidth(usableWidth * 0.17); // 17%
			overdueDaysCol.setPrefWidth(usableWidth * 0.17); // 16%
			// 합계: 0.04 + 0.08 + 0.15 + 0.20 + 0.20 + 0.17 + 0.16 = 1.00 (100%)

			// # 선택 사항: 각 컬럼의 최소/최대 너비 설정
			// # 예를 들어, indexCol.setMinWidth(30); indexCol.setMaxWidth(60);
			// # 이를 통해 창 크기가 너무 작아지거나 커질 때 컬럼이 비정상적으로 줄어들거나 늘어나는 것을 방지할 수 있습니다.

			// # 변경된 부분: FXML에서 fx:id를 부여한 헤더 Separator들의 너비를 바인딩합니다.
			// null 체크를 통해 객체가 로드되었는지 확인합니다.
			// 컬럼 너비에서 약간의 여백을 주는 것이 시각적으로 좋습니다 (예: -5px)
			if (phoneNumHeaderSeparator != null) {
				phoneNumHeaderSeparator.prefWidthProperty().bind(phoneNumberCol.widthProperty().subtract(5));
			}
			if (eqNameHeaderSeparator != null) {
				eqNameHeaderSeparator.prefWidthProperty().bind(eqNameCol.widthProperty().subtract(5));
			}
			if (rentalDateHeaderSeparator != null) {
				rentalDateHeaderSeparator.prefWidthProperty().bind(rentalDateCol.widthProperty().subtract(5));
			}
			if (overdueDaysHeaderSeparator != null) {
				overdueDaysHeaderSeparator.prefWidthProperty().bind(overdueDaysCol.widthProperty().subtract(5));
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
		// DAO는 모든 대여 기록을 가져오고, 필터링은 자바 코드 (applyFilter 메서드)에서 담당합니다.
		List<OverdueHistoryDTO> list = rentalDAO.findAllRentalsForAdmin();
		System.out.println(list);
		allRentals = FXCollections.observableArrayList(list);
		applyFilter(); // 로드 후 필터 적용
	}

	// 필터를 적용하여 테이블 뷰를 업데이트하는 메서드
	private void applyFilter() {
		// 관리자의 대여소 이름 필터링 로직을 추가
		List<OverdueHistoryDTO> filteredByUserGu;

		// 로그인한 사용자가 'ADMIN' 역할이고, 세션에 대여소 이름이 유효하게 있는 경우에만 대여소 이름으로 필터링
		if ("ADMIN".equals(Session.userRole) && currentUserGu != null
				&& !currentUserGu.trim().isEmpty()) {
			filteredByUserGu = allRentals.stream()
					.filter(rental -> currentUserGu.equals(rental.getUserGu()))
					.collect(Collectors.toList());
		} else {
			// 일반 사용자이거나 (AdminRentalHistoryController에 접근하면 안되지만 만약의 경우),
			// 또는 관리자인데 대여소 정보가 없는 경우 (위 initialize에서 경고 후 return하지만, 여기서는 전체 데이터 사용),
			// 모든 데이터를 필터링 없이 사용합니다.
			filteredByUserGu = allRentals;
		}

		if (showOverdueOnlyCheckBox.isSelected()) {
			// 연체된 기록만 필터링 (1차 필터링된 목록에서 다시 필터링)
			List<OverdueHistoryDTO> finalFilteredList = filteredByUserGu.stream()
					.filter(rental -> rental.getOverdueDays() > 0 || "연체".equals(rental.getReturnStatus()))
					.collect(Collectors.toList());
			rentalTable.setItems(FXCollections.observableArrayList(finalFilteredList));
		} else {
			// 모든 기록 표시 (1차 필터링된 목록만 표시)
			rentalTable.setItems(FXCollections.observableArrayList(filteredByUserGu));
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