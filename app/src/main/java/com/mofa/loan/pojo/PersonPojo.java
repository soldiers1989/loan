package com.mofa.loan.pojo;

public class PersonPojo {

	private String create_date;
	
	private String cardusername;
	private int jk_money;
	private String phone;
	private int day;
	private String jkPhone;
	private String jkName;
	private double hk_money;
	
	public PersonPojo() {
	}
	
	 public  PersonPojo(final String create_date, final String cardusername) {
	        this.create_date = create_date;
	        this.cardusername = cardusername;
	    }
	    
	
	public String getCardusername() {
		return cardusername;
	}
	public void setCardusername(String cardusername) {
		this.cardusername = cardusername;
	}
	public int getJk_money() {
		return jk_money;
	}
	public void setJk_money(int jk_money) {
		this.jk_money = jk_money;
	}
	public String getCreate_date() {
		return create_date;
	}
	public void setCreate_date(String create_date) {
		this.create_date = create_date;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public String getJkPhone() {
		return jkPhone;
	}

	public void setJkPhone(String jkPhone) {
		this.jkPhone = jkPhone;
	}

	public String getJkName() {
		return jkName;
	}

	public void setJkName(String jkName) {
		this.jkName = jkName;
	}

	public double getHk_money() {
		return hk_money;
	}

	public void setHk_money(double hk_money) {
		this.hk_money = hk_money;
	}
}
