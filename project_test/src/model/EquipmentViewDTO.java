package model; // 패키지 경로는 실제 프로젝트에 맞게 수정하세요

import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class EquipmentViewDTO {

    private final StringProperty officeName = new SimpleStringProperty();
    private final StringProperty eqName = new SimpleStringProperty();
    private final StringProperty serialNum = new SimpleStringProperty();
    private final LongProperty rentalFee = new SimpleLongProperty();
    private final StringProperty state = new SimpleStringProperty();
    private final StringProperty img = new SimpleStringProperty();
    private final StringProperty eqInfo = new SimpleStringProperty();

    // --- JavaFX Property Getters ---
    public StringProperty officeNameProperty() { return officeName; }
    public StringProperty eqNameProperty() { return eqName; }
    public StringProperty serialNumProperty() { return serialNum; }
    public LongProperty rentalFeeProperty() { return rentalFee; }
    public StringProperty stateProperty() { return state; }
    public StringProperty imgProperty() { return img; }
    public StringProperty eqInfoProperty() { return eqInfo; }

    // --- Standard Getters ---
    public String getOfficeName() { return officeName.get(); }
    public String getEqName() { return eqName.get(); }
    public String getSerialNum() { return serialNum.get(); }
    public long getRentalFee() { return rentalFee.get(); }
    public String getState() { return state.get(); }
    public String getImg() { return img.get(); }
    public String getEqInfo() { return eqInfo.get(); }

    // --- Standard Setters ---
    public void setOfficeName(String value) { this.officeName.set(value); }
    public void setEqName(String value) { this.eqName.set(value); }
    public void setSerialNum(String value) { this.serialNum.set(value); }
    public void setRentalFee(long value) { this.rentalFee.set(value); }
    public void setState(String value) { this.state.set(value); }
    public void setImg(String value) { this.img.set(value); }
    public void setEqInfo(String value) { this.eqInfo.set(value); }
}