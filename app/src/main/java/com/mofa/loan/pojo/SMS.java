package com.mofa.loan.pojo;

public class SMS {

	private String phone;
	private int person;
	private String content;
	private String date;
	private String type;
	
	public SMS() {
		super();
	}

	public SMS(String phone, int person, String content, String date,
               String type) {
		super();
		this.phone = phone;
		this.person = person;
		this.content = content;
		this.date = date;
		this.type = type;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}


	public int getPerson() {
		return person;
	}


	public void setPerson(int person) {
		this.person = person;
	}


	public String getContent() {
		return content;
	}


	public void setContent(String content) {
		this.content = content;
	}


	public String getDate() {
		return date;
	}


	public void setDate(String date) {
		this.date = date;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	@Override
	public String toString() {
		return "SMS [phone=" + phone + ", person=" + person + ", content="
				+ content + ", date=" + date + ", type=" + type + "]";
	}
	
}
