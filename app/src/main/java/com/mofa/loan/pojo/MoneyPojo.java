package com.mofa.loan.pojo;

public class MoneyPojo {

	private String money;
	private String name;
	private String time;
	private String type;
	private int id;
	private String date;
	public String getDate() {
		return date;
	}
	
	public void setDate(String date) {
		this.date = date;
	}
	
	public String getMoney() {
		return money;
	}
	public void setMoney(String money) {
		this.money = money;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "MoneyPojo [money=" + money + ", name=" + name + ", time="
				+ time + ", type=" + type + ", id=" + id + ", date=" + date
				+ "]";
	}
	
}
