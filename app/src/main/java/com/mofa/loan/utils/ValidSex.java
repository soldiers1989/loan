package com.mofa.loan.utils;

public class ValidSex {
	public int execute(String value)
	{
	value = value.trim();
	if (value == null || (value.length() != 15 && value.length() != 18))
	{
		return 2;
	}
	//0是女1是男
	if (value.length() == 15 || value.length() == 18)
	{
		String lastValue = value.substring(value.length() - 2, value.length()-1);
		int sex;
		if (lastValue.trim().toLowerCase().equals("x")||lastValue.trim().toLowerCase().equals("e"))
		{
			return 1;
		}else
		{
			sex = Integer.parseInt(lastValue) % 2;
			return sex == 0 ? 0 : 1;
		}
	}else{
		return 2;
		}
	}
}
