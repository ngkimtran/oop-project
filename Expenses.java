package project;

import java.util.ArrayList;
import java.io.Serializable;

public class Expenses implements Serializable, Comparable<Expenses> {
	//attributes
	private Date date;
	private ArrayList<Expense> list = new ArrayList<>();
	
	//constructors
	public Expenses(Date date) {
		this.date = date;
	}	
	public Expenses() {
		
	}
	
	//getters and setters
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}

	//methods
	public void addExpense(Expense e) {
		list.add(e);
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(date.toString() + ":\n");
		for (Expense e : list) {
			sb.append(e.toString() + "\n");
		}
		return sb.toString();
	}

	
	//this method returns the sum of expenses that match the user input category
	public double getSumByCategory(String sCat) {
		double result = 0;
		for (Expense e : list) {
			if (e.getCategory().compareToIgnoreCase(sCat) == 0) {
				result += e.getSum();
			}
		}
		return result;
	}
	
	//compare method
	@Override
	public int compareTo(Expenses o) {
		return date.compareTo(o.getDate());
	}
	
}
