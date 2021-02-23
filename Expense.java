package project;

import java.io.Serializable;

public class Expense implements Serializable{
	//attributes
	private String category;
	private double sum;
	private String description;
	
	//constructor
	public Expense(String category, double sum, String description) {
		this.category = category;
		this.sum = sum;
		this.description = description;
	}
	
	//getters and setters
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public double getSum() {
		return sum;
	}
	public void setSum(double sum) {
		this.sum = sum;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	//method
	public String toString() {
		return "\t" + category + "\t" + sum + "\t" + description;
	}

	public boolean searchCategory(String scategory) {
		if (this.category.compareToIgnoreCase(scategory) == 0) {
			return true;
		}
		return false;
	}
}
