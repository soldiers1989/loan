package com.mofa.loan.pojo;

import com.mofa.loan.utils.TimeUtils;

import java.util.Random;

public class PersonForHome implements Comparable {

	private String time;
	private Long timeMills;
	private String name;
	private String money;
	private String name2;
	private int money2;
	private Random random = new Random();

	public PersonForHome() {
		super();
		//中国姓和钱
		name = firstName[random.nextInt(firstName.length)];
		money = borrowMoney[random.nextInt(borrowMoney.length)];
//		//越南姓和钱
//		name2 = firstName.charAt(random.nextInt(firstName2.length)) + "";
//		money2 = borrowMoney[random.nextInt(borrowMoney2.length)];
		
		timeMills = System.currentTimeMillis() - random.nextInt(18)*100000;
		time = TimeUtils.parseDate6(timeMills);
		
	}

	public PersonForHome(String time, String name, String money) {
		super();
		this.time = time;
		this.name = name;
		this.money = money;
		
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMoney() {
		return money;
	}

	public long getTimeMills() {
		return timeMills;
	}

	public void setTimeMills(long timeMills) {
		this.timeMills = timeMills;
	}

	public void setMoney(String money) {
		this.money = money;
	}

	@Override
	public String toString() {
		return "PersonForHome [time=" + time + ", name=" + name + ", money="
				+ money + "]";
	}

	//中国姓
	String firstName2 = "赵钱孙李周吴郑王赵钱孙李周吴郑王赵钱孙李周吴郑王冯陈楮卫蒋沈韩杨朱秦尤许何吕施张孔曹严华金魏陶姜戚谢邹喻柏水窦章云苏潘葛奚范彭郎鲁韦昌马苗凤花方俞任袁柳马苗凤花方俞任袁柳酆鲍史唐费廉岑薛雷贺倪汤滕殷罗毕郝邬安常乐于时傅皮卞齐康伍余元卜顾孟平黄和穆萧尹姚邵湛汪祁毛禹狄米贝明臧计伏成戴谈宋茅庞熊纪舒屈项祝董梁杜阮蓝闽席季麻强贾路娄危江童颜郭梅盛林刁锺徐丘骆高夏蔡田樊胡凌霍虞万支柯昝管卢莫经房裘缪干解应宗丁宣贲邓郁单杭洪包诸左石崔吉钮龚程嵇邢滑裴陆荣翁荀羊於惠甄麹家封芮羿储靳汲邴糜松井段富巫乌焦巴弓牧隗山谷车侯宓蓬全郗班仰秋仲伊宫宁仇栾暴甘斜厉戎祖武符刘景詹束龙叶幸司韶郜黎蓟薄印宿白怀蒲邰从鄂索咸籍赖卓蔺屠蒙池乔阴郁胥能苍双闻莘党翟谭贡劳逄姬申扶堵冉宰郦雍郤璩桑桂濮牛寿通边扈燕冀郏浦尚农温别庄晏柴瞿阎充慕连茹习宦艾鱼容向古易慎戈廖庾终居衡步都耿满弘匡国文寇广禄阙东欧沃利蔚越隆师巩聂晁勾敖融冷訾辛阚那简饶空曾沙养鞠须丰关蒯相查后荆红游竺权逑盖益桓公万";
	int [] borrowMoney2 = {500, 700, 1000, 1200, 1500, 2000, 5000, 1200, 1500, 2000, 5000, 2000, 5000, 5000};

	//越南姓
	String[] firstName = {"Nguyễn", "Nguyễn","Nguyễn", "Nguyễn","Nguyễn", "Nguyễn","Nguyễn", "Nguyễn","Nguyễn", "Nguyễn","Trần", "Trần","Trần", "Trần","Lê", "Lê","Lê", "Phạm","Phạm", "Phạm", "Hoàng","Hoàng", "Huỳnh", "Huỳnh","Voòng", "Phan", "Phan","Võ", "Võ", "Vũ","Vũ", "Đặng","Bùi", "Đỗ", "Hồ","Ngô", "Dương","Lý", "Ân", "Đổng","Hoa", "Tiết","Tiêu", "Thang", "Thảo","Phùng", "Kim",};
	String[] borrowMoney = {"2,500,000", "2,500,000", "3,500,000", "3,500,000", "3,500,000", "4,000,000", "4,500,000", "4,500,000", "3,500,000", "5,500,000", "5,500,000","5,500,000", "6,000,000", "6,000,000", "6,500,000", "6,500,000", "8,000,000", "8,000,000", "8,000,000", "8,500,000", "9,000,000", "7,000,000", "7,000,000", "10,000,000", "10,000,000", "10,000,000", "10,000,000"};
	
	
	@Override
	public int compareTo(Object another) {
		PersonForHome person = (PersonForHome) another;
		long otherTime = person.getTimeMills();
		return this.timeMills.compareTo(otherTime);
	}

}
