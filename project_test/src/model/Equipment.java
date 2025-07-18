package model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Equipment {
	private final StringProperty rentalOffice;
	private final StringProperty name;
	private final StringProperty quantity;
	private final StringProperty cost;
	private final StringProperty status;

	public Equipment(String rentalOffice, String name,String cost, String status, String quantity) {
		this.rentalOffice = new SimpleStringProperty(rentalOffice);
		this.name = new SimpleStringProperty(name);
		this.quantity = new SimpleStringProperty(quantity);
		this.cost = new SimpleStringProperty(cost);
		this.status = new SimpleStringProperty(status);
	}

	// --- Property Getters ---
	public StringProperty rentalOfficeProperty() {
		return rentalOffice;
	}


	public StringProperty nameProperty() {
		return name;
	}

	public StringProperty quantityProperty() {
		return quantity;
	}

	public StringProperty costProperty() {
		return cost;
	}


	public StringProperty statusProperty() {
		return status;
	}


	// --- String Getters for convenience ---
	public String getStatus() {
		return status.get();
	}


	public String getName() {
		return name.get();
	}
}