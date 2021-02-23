package project;

import java.io.Serializable;

public class Date implements Comparable<Date>, Serializable {
//Attributes
	private int day, month, year;
	
	public static final String[] MONTHNAMES = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};

//Constructors
	public Date() {
		java.time.LocalDate today = java.time.LocalDate.now();
		this.day = today.getDayOfMonth();
		this.month = today.getMonthValue();
		this.year = today.getYear();
	}
	public Date(int day, int month, int year) {
		this.day = day;
		this.month = month;
		this.year = year;
	}
	public Date(String date) {
		String[] dateArr = date.split("\\.");
		this.day = Integer.parseInt(dateArr[0]);
		this.month = Integer.parseInt(dateArr[1]);
		this.year = Integer.parseInt(dateArr[2]);
	}
	
//Getters
	public int getDay() {
		return day;
	}
	public int getMonth() {
		return month;
	}
	public int getYear() {
		return year;
	}

//Setters
	public void setDay(int day) {
		this.day = day;
	}
	public void setMonth(int month) {
		this.month = month;
	}
	public void setYear(int year) {
		this.year = year;
	}
	
//Methods
	public String toString() {
		return day + "." + month + "." + year;
	}
	public boolean isSame(Date another) {
		if (this.day == another.day && this.month == another.month && this.year == another.year)
			return true;
		return false;
	}
	public String longDate() {
		String date = MONTHNAMES[month - 1] + " " + day;
		
		if (day == 1 || day == 21 || day == 31)
			date += "st";
		else if (day == 2 || day == 22)
			date += "nd";
		else if (day == 3 || day == 23)
			date += "rd";
		else
			date += "th";
		date += ", " + year; 
		
		return date; 
	}
	public static int monthName(String month) {
		int i = 0;

		for (String m: MONTHNAMES) {
			if (m.compareToIgnoreCase(month) == 0)
				return i+1;
			else 
				i++;
		}
		
		return -1;		
	}

	@Override
	public int compareTo(Date another) {
		if(this.year == another.year) {
			if(this.month == another.month) {
				if(this.day == another.day) {
					return 0;
				}
				else if(this.day > another.day) {
					return 1;
				}
				else if(this.day < another.day) {
					return -1;
				}
			}
			else if(this.month > another.month) {
				return 1;
			}
			else if(this.month < another.month) {
				return -1;
			}
		}
		else if (this.year > another.year) {
			return 1;
		}
		else if(this.year < another.year) {
			return -1;
		}
		
		return 0;
	}
	
	
}