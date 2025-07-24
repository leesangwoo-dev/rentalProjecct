package controller;

import static utils.Session.userGu;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.time.LocalDate;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
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
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.EquipmentViewDTO; // 수정한 DTO를 임포트
import model.RentalOfficeDTO;

// 메인 페이지(장비 조회)_사용자 컨트롤러
public class MainViewController {
	@FXML
	private Label todayLabel;
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
	private Button myInfoButton;
	@FXML
	private Button rentalHistory;

	private EquipmentDAO equipmentDAO; // EquipmentDAO 인스턴스
	private RentalOfficeDAO rentalOfficeDAO; // RentalOfficeDAO 인스턴스

	public MainViewController() {
		this.equipmentDAO = new EquipmentDAO();
		this.rentalOfficeDAO = new RentalOfficeDAO();
	}

	
	@FXML
	public void initialize() {
		setLocationInfo();		// 위치 정보 세팅
		setupTableColumns();	// 컬럼 세팅
		loadTableData(searchTextField.getText().trim()); // 필터 세팅
		tableClickEvent();	// 이벤트 핸들러 등록
		imgClickEvent();	// 이미지 클릭 이벤트 핸들러 등록
	}

	// 위치 정보(구, 지역, 대여소) 세팅
	private void setLocationInfo() {
		LocalDate now = LocalDate.now();
		todayLabel.setText("Today : " + now.toString());
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

	}

	// `guComboBox` 변경 시 호출될 메서드
	private void loadOfficesByGu(String selectedGu) {
		officeComboBox.setValue(null); // 현재 선택된 값도 초기화
		officeComboBox.getItems().clear();

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

	// 검색 버튼 클릭 이벤트 핸들러
	@FXML
	private void search() {
		String searchText = searchTextField.getText().trim(); // 검색 필드 텍스트
		loadTableData(searchText);
	}

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

	public void loadTableData(String searchText) {
		String selectedGu = guComboBox.getValue();
		String selectedOffice = officeComboBox.getValue().getOfficeName();
		List<EquipmentViewDTO> equipmentData = equipmentDAO.getEquipmentList(selectedGu, selectedOffice, searchText);
		equipmentTable.getItems().setAll(equipmentData);
	}

	@FXML
	public void doubleClickItem(MouseEvent event) {
		if (event.getClickCount() == 2) { // 더블 클릭 확인
			EquipmentViewDTO selectedItem = equipmentTable.getSelectionModel().getSelectedItem();
			if (selectedItem != null) {
				System.out.println("선택된 아이템: " + selectedItem.getEqName());
			}
		}
	}

	// 내 정보 수정 창
	@FXML
	private void handleMyInfo(ActionEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MyInfoView.fxml")); // FXML 파일명 확인!
			Parent myInfoView = loader.load();

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

	// 대여내역 창
	public void handleRentalHistory(ActionEvent event) {
		try {
			Parent handleRentalHistory = FXMLLoader.load(getClass().getResource("/view/RentalHistoryView.fxml"));

			// 현재 창(Stage)을 얻어서 씬 변경
			Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			stage.setScene(new Scene(handleRentalHistory));
			stage.setTitle("대여내역");
			stage.show();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 대여하기 버튼
	@FXML
	private void RentButton() {
		EquipmentViewDTO selected = equipmentTable.getSelectionModel().getSelectedItem();
		if (selected != null) {
			openDetailView(selected);
		}
	}

	// 클릭 이벤트 설정
	public void tableClickEvent() {
		equipmentTable.setOnMouseClicked(event -> {
			EquipmentViewDTO selected = equipmentTable.getSelectionModel().getSelectedItem();
			if (selected != null) {
				// 단일 클릭: 오른쪽 이미지와 설명 표시
				equipmentInfo.setText(selected.getEqInfo());

				String dbPath = selected.getImg();

				File imgFile = new File(dbPath);
				URI uri = imgFile.toURI();

				equipmentImage.setImage(new Image(uri.toASCIIString()));

			}

			// 더블 클릭: 상세 보기 띄우기
			if (event.getClickCount() == 2 && selected != null) {
				openDetailView(selected);
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

	// 더블 클릭시 상세페이지 띄우기
	private void openDetailView(EquipmentViewDTO equipment) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/DetailView.fxml"));
			Parent root = loader.load();

			DetailViewController controller = loader.getController();
			controller.setSerialNumber(equipment.getSerialNum());
			controller.setMainController(this);

			Stage stage = new Stage();
			stage.setTitle("장비 상세정보");
			stage.setScene(new Scene(root));
			stage.show();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}