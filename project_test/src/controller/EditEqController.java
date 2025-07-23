package controller;

import java.io.File;

import dao.EquipmentDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.EquipmentViewDTO;

public class EditEqController {

	@FXML
	private ComboBox<String> stateCombo;
	@FXML
	private Label dragLabel, phoneLabel;
	@FXML
	private StackPane imageDropPane;
	@FXML
	private ImageView imagePreview;
	@FXML
	private Button chooseImageButton;
	@FXML
	private TextField eqNameField, serialNumField, costField, unitPriceField, acquisitionDateField;
	@FXML
	private TextArea eqInfoArea;

	private File selectedImageFile;

	@FXML
	private Button refreshImageButton;
	@FXML
	private Button EditButton;

	private EquipmentViewDTO equipment;
	private AdminViewController adminController;
	private File newImageFile;

	public void setmainController(AdminViewController adminController) {
		this.adminController = adminController;
	}

	@FXML
	private void handleDragOver(DragEvent event) {
		if (event.getGestureSource() != imageDropPane && event.getDragboard().hasFiles()) {
			event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
		}
		event.consume();
	}

	@FXML
	private void handleDragDropped(DragEvent event) {
		Dragboard db = event.getDragboard();
		boolean success = false;

		if (db.hasFiles()) {
			selectedImageFile = db.getFiles().get(0);
			loadImagePreview(selectedImageFile);
			success = true;
		}
		event.setDropCompleted(success);
		event.consume();
	}

	@FXML
	private void handleImageSelect(ActionEvent event) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("이미지 선택");
		fileChooser.getExtensionFilters()
				.addAll(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
		File file = fileChooser.showOpenDialog(imageDropPane.getScene().getWindow());

		if (file != null) {
			selectedImageFile = file;
			loadImagePreview(selectedImageFile);
		}
	}

	private void loadImagePreview(File file) {
		if (file != null && file.exists()) {
			imagePreview.setImage(new Image(file.toURI().toString()));

			// ▶ Drag/Drop 안내와 버튼 숨기기
			dragLabel.setVisible(false);
			chooseImageButton.setVisible(false);
		}
	}
	@FXML
	private void handleRefreshImage(ActionEvent event) {
	    FileChooser chooser = new FileChooser();
	    chooser.setTitle("새 이미지 선택");
	    chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("이미지 파일", "*.png", "*.jpg", "*.jpeg"));
	    File file = chooser.showOpenDialog(refreshImageButton.getScene().getWindow());
	    if (file != null) {
	        newImageFile = file;
	        imagePreview.setImage(new Image(file.toURI().toString()));
	    }
	}

	
	public void setEquipmentDTO(EquipmentViewDTO dto) {
		this.equipment = dto;

		eqNameField.setText(dto.getEqName());
		serialNumField.setText(dto.getSerialNum());
		costField.setText(String.valueOf(dto.getRentalFee()) + "원");
		eqInfoArea.setText(dto.getEqInfo());
		stateCombo.getItems().addAll("사용가능", "수리", "대여중");
		stateCombo.setValue(dto.getState());
		if (dto.getImg() != null) {
			File imgFile = new File(dto.getImg());
			imagePreview.setImage(new Image(imgFile.toURI().toString()));
		}
	}
	
	// ───────────────── EditEqController.java ─────────────────
	@FXML
	private void handleUpdate(ActionEvent event) {

	    String serial   = serialNumField.getText();
	    String newState = stateCombo.getValue();

	    // 새 이미지가 없으면 기존 경로 사용
	    String newImgPath = (newImageFile != null) ? newImageFile.getAbsolutePath() : equipment.getImg();

	    // ★ 숫자 필드 : "12000원" → 12000
	    Integer fee = null;
	    String feeTxt = costField.getText().replaceAll("[^0-9]", "");
	    if (!feeTxt.isEmpty()) fee = Integer.parseInt(feeTxt);

	    Integer unitPrice = null;
	    String unitTxt = unitPriceField.getText().replaceAll("[^0-9]", "");
	    if (!unitTxt.isEmpty()) unitPrice = Integer.parseInt(unitTxt);

	    String newInfo = eqInfoArea.getText();

	    EquipmentDAO dao = new EquipmentDAO();
	    boolean success = dao.updateEquipment(
	            serial,
	            newState,         // 상태
	            newImgPath,       // 이미지 경로
	            newInfo,          // 설명
	            fee,              // 대여료
	            unitPrice         // 단가
	    );

	    if (success) {
	        showAlert(Alert.AlertType.INFORMATION, "장비가 수정되었습니다.");
	        if (adminController != null) adminController.loadTableData("");
	        ((Stage)EditButton.getScene().getWindow()).close();
	    } else {
	        showAlert(Alert.AlertType.ERROR, "장비 수정 실패");
	    }
	}


	private void showAlert(Alert.AlertType type, String message) {
		Alert alert = new Alert(type);
		alert.setContentText(message);
		alert.showAndWait();
	}
}
