package controller;

import static util.Session.userGu;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

import dao.EquipmentDAO;
import dao.RentalOfficeDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import model.EquipmentViewDTO; // 수정한 DTO를 임포트
import model.RentalOfficeDTO;

// 메인 페이지(장비 조회)_사용자 컨트롤러
public class AdminViewController {
	@FXML
	private ComboBox<String> guComboBox;
	@FXML
	private ComboBox<RentalOfficeDTO> officeComboBox;
	@FXML
	private TextField searchTextField;
	@FXML
	private Button searchButton;
	@FXML
	private TableView<EquipmentViewDTO> equipmentTable;
	@FXML
	private TableColumn<EquipmentViewDTO, String> rentalOfficeCol;
	@FXML
	private TableColumn<EquipmentViewDTO, String> nameCol;
	@FXML
	private TableColumn<EquipmentViewDTO, String> SerialNumCol;
	@FXML
	private TableColumn<EquipmentViewDTO, Number> costCol;
	@FXML
	private TableColumn<EquipmentViewDTO, String> statusCol;
	@FXML
	private ImageView equipmentImage;
	@FXML
	private TextArea equipmentInfo;
	@FXML
	private Button adminEqList;
	@FXML
	private Button adminRentalList;
	@FXML
	private Button overdueHistory;

	private EquipmentDAO equipmentDAO; // EquipmentDAO 인스턴스
	private RentalOfficeDAO rentalOfficeDAO; // RentalOfficeDAO 인스턴스

	public AdminViewController() {
		this.equipmentDAO = new EquipmentDAO();
		this.rentalOfficeDAO = new RentalOfficeDAO();
	}

	@FXML
	public void initialize() {
		guComboBox.getItems().addAll("유성구", "중구", "서구", "동구", "대덕구");
		guComboBox.setValue(userGu);
		loadOfficesByGu(userGu);
		// 구 콤보박스 변경시 테이블 최신화
		guComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
			if (newVal != null && !newVal.equals(oldVal)) {
				System.out.println("구 콤보박스 변경: " + oldVal + " -> " + newVal);
				loadOfficesByGu(newVal);
				search();
			}
		});
		// 대여소 콤보박스 변경시 테이블 최신화
		officeComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
			if (newVal != null && !newVal.equals(oldVal)) {
				System.out.println("대여소 콤보박스 변경: " + oldVal + " -> " + newVal);
				search();
			}
		});

		setupTableColumns();
		loadTableData(searchTextField.getText().trim());
		tableClickEvent();
		imgClickEvent();
	}

	//
	private void setupTableColumns() {
		// 각 열과 EquipmentViewDTO의 프로퍼티를 연결
		// 열이 어떤 데이터를 보여줄지 결정
		rentalOfficeCol.setCellValueFactory(new PropertyValueFactory<>("officeName"));
		nameCol.setCellValueFactory(new PropertyValueFactory<>("eqName"));
		SerialNumCol.setCellValueFactory(new PropertyValueFactory<>("serialNum"));
		costCol.setCellValueFactory(new PropertyValueFactory<>("rentalFee")); // rentalFee 필드명
		statusCol.setCellValueFactory(new PropertyValueFactory<>("state")); // state 필드명

		// Status에 따른 css 적용(색상이나 폰트 등)
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
					} else if (item.equals("대여중")) {
						getStyleClass().add("status-rented");
					} else if (item.equals("수리")) {
						getStyleClass().add("status-broken");
					}
				}
			}
		});
	}

	private void loadOfficesByGu(String selectedGu) {
		officeComboBox.setValue(null); // 현재 선택된 값도 초기화

		// RentalOfficeDAO를 사용하여 DB에서 대여소 목록 가져오기
		List<RentalOfficeDTO> offices = rentalOfficeDAO.getOfficesByGu(selectedGu);

		// '전체' 옵션 추가 (선택 사항: ID가 null인 RentalOfficeDTO 객체)
		RentalOfficeDTO allOption = new RentalOfficeDTO(null, "전체", null, null, null); // ID는 null, 이름은 "전체"
		officeComboBox.getItems().add(allOption);

		// 콤보박스에 항목 추가
		officeComboBox.getItems().addAll(offices);

		// 기본값 설정: '전체'
		officeComboBox.setValue(allOption);
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
//	            showAlert("페이지 로드 오류", null, "장비조회 페이지를 로드할 수 없습니다.");
		}
	}

	// 연체정보 nav
	public void handleOverdueHistory(ActionEvent event) {
		try {
			// FXML 파일 로드
			Parent handleOverdueHistory = FXMLLoader.load(getClass().getResource("/view/OverdueHistoryView.fxml"));
			// 현재 창(Stage)을 얻어서 씬 변경
			Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			stage.setScene(new Scene(handleOverdueHistory));
			stage.setTitle("연체정보");
			stage.show();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 장비 추가
	@FXML
	private void AddEqButton() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AddEqView.fxml"));
			Parent root = loader.load();

			AddEqController controller = loader.getController();
			controller.setMainController(this);

			Stage stage = new Stage();
			stage.setTitle("장비 추가 정보");
			stage.setScene(new Scene(root));
			stage.show();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 장비 상태 변경
	@FXML
	private void changeStatusButton() {
		EquipmentViewDTO selected = equipmentTable.getSelectionModel().getSelectedItem();
		if (selected == null) {
			showAlert(AlertType.WARNING, "경고", "장비를 선택하세요.");
			return;
		}

		// 상태 옵션 제공
		List<String> statusOptions = Arrays.asList("사용가능", "수리", "대여중");
		ChoiceDialog<String> dialog = new ChoiceDialog<>(selected.getState(), statusOptions);
		dialog.setTitle("장비 상태 변경");
		dialog.setHeaderText("현재 상태: " + selected.getState());
		dialog.setContentText("장비 상태 변경:");

		dialog.showAndWait().ifPresent(newState -> {
			EquipmentDAO dao = new EquipmentDAO();
			boolean success = dao.updateState(selected.getSerialNum(), newState);
			if (success) {
				showAlert(AlertType.INFORMATION, "알림", "상태가 [" + newState + "](으)로 변경되었습니다.");
				loadTableData(searchTextField.getText().trim()); // 테이블 갱신
			} else {
				showAlert(AlertType.ERROR, "오류", "상태 변경 실패");
			}
		});
	}

	// 클릭 이벤트 설정
	public void tableClickEvent() {
		equipmentTable.setOnMouseClicked(event -> {
			EquipmentViewDTO selected = equipmentTable.getSelectionModel().getSelectedItem();
			if (selected != null) {
				// 단일 클릭: 오른쪽 이미지와 설명 표시
				equipmentInfo.setText(selected.getEqInfo());
				System.out.println(selected.getImg());
				String dbPath = selected.getImg();

				File imgFile = new File(dbPath);
				URI uri = imgFile.toURI();

				System.out.println("파일 존재?: " + imgFile.exists());
				System.out.println("URI: " + uri.toString());

				equipmentImage.setImage(new Image(uri.toASCIIString()));
			}

			// 더블 클릭: 상세 보기 띄우기
			if (event.getClickCount() == 2 && selected != null) {
				// openDetailView(selected);
			}
		});
	}

	// 이미지 클릭 시 사진 띄우기
	public void imgClickEvent() {
		equipmentImage.setOnMouseClicked(event -> {
			if (event.getClickCount() == 2) {
				showEnlargedImage(equipmentImage.getImage());
			}
		});
	}

	// 이미지 클릭 시 사진 띄우기
	private void showEnlargedImage(Image image) {
		Stage stage = new Stage();
		ImageView enlargedView = new ImageView(image);
		enlargedView.setPreserveRatio(true);
		enlargedView.setFitWidth(600); // 원하는 크기

		StackPane root = new StackPane(enlargedView);
		Scene scene = new Scene(root, 600, 400); // 크기 조정 가능
		stage.setScene(scene);
		stage.setTitle("이미지 확대 보기");
		stage.show();
	}

	// 더블 클릭시 장비 추가창 띄우기
	private void openEqAddView(EquipmentViewDTO equipment) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/EqAddView.fxml"));
			Parent root = loader.load();

			DetailViewController controller = loader.getController();
			controller.setSerialNumber(equipment.getSerialNum());
			// controller.setMainController(this);

			Stage stage = new Stage();
			stage.setTitle("장비 상세정보");
			stage.setScene(new Scene(root));
			stage.show();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	private void search() {
		String searchText = searchTextField.getText().trim(); // 검색 필드 텍스트
		loadTableData(searchText);
	}

	public void loadTableData(String searchText) {
		String selectedGu = guComboBox.getValue();
		String selectedOffice = officeComboBox.getValue().getOfficeName();
		List<EquipmentViewDTO> equipmentData = equipmentDAO.getEquipmentList(selectedGu, selectedOffice, searchText);
		// 가져온 데이터를 TableView에 설정합니다.
		equipmentTable.getItems().setAll(equipmentData);
	}

	private void showAlert(AlertType type, String title, String message) {
		Alert alert = new Alert(type);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}

}