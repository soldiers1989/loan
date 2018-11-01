package com.mofa.loan.pojo;

public class Contact2 {
	
	public String type;
	public String name;
	public String number;
	public int callDuration;
	public String callDateStr;
	
	public Contact2() {
		super();
	}

	public Contact2(String type, String name, String number, int callDuration,
                    String callDateStr) {
		super();
		this.type = type;
		this.name = name;
		this.number = number;
		this.callDuration = callDuration;
		this.callDateStr = callDateStr;
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

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public int getCallDuration() {
		return callDuration;
	}

	public void setCallDuration(int callDuration) {
		this.callDuration = callDuration;
	}

	public String getCallDateStr() {
		return callDateStr;
	}

	public void setCallDateStr(String callDateStr) {
		this.callDateStr = callDateStr;
	}

	@Override
	public String toString() {
		return "Contact2 [type=" + type + ", name=" + name + ", number="
				+ number + ", callDuration=" + callDuration + ", callDateStr="
				+ callDateStr + "]";
	}
	
}
