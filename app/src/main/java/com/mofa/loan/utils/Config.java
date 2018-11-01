package com.mofa.loan.utils;

public class Config {
    //	public static final String URL = "http://www.shandkj.com/";
//	public static final String URL = "http://www.ttxxfz.com/";
    public static final String URL = "http://192.168.0.113/";
//    public static final String URL = "http://www.movabank.com/";

    //主页
//	public static final String HOME_INIT=URL+"servlet/current/JBDUserAction?function=FindUsernameAppYN";
    public static final String HOME_INIT = URL + "servlet/current/JBDUserAction?function=FindOLAVAAppHome";
//    public static final String HOME_INIT = URL + "servlet/current/JBDUserAction?function=MofaHomedateFD";

    //登录接口
//	public static final String LOGIN_CODE="servlet/current/JBDUserAction?function=LoginE2YN";
//	public static final String LOGIN_CODE="servlet/current/JBDUserAction?function=LoginOLVnew";
    public static final String LOGIN_CODE = "servlet/current/JBDUserAction?function=LUihyBNeKuse";
//    public static final String LOGIN_CODE = "servlet/current/JBDUserAction?function=MofaLoginUserNew";

    //注册获取短信验证码
//	public static final String SEND_CODE = "servlet/current/JBDUserAction?function=SDSendMsgNewOLAVA";
    public static final String SEND_CODE = "servlet/current/JBDUserAction?function=SDSendNewNewOLAVA";
//    public static final String SEND_CODE = "servlet/current/JBDUserAction?function=MofaSendMsgUserIn";

    //注册FACEBOOK or 短信 验证码
    public static final String SEND_FACEBOOK_CODE = "servlet/current/JBDUserAction?function=GetjkuseGhdJ";
//    public static final String SEND_FACEBOOK_CODE = "servlet/current/JBDUserAction?function=MofaChangeResit";

    //注册接口
//	public static final String REGISTER = "servlet/current/JBDUserAction?function=RegisterNewYN";
    public static final String REGISTER = "servlet/current/JBDUserAction?function=RhkdeENDkseIsJe";
//    public static final String REGISTER = "servlet/current/JBDUserAction?function=MofaReLoginPuT";
    public static final String REGISTER_SENDFACEBOOK = "servlet/current/JBDUserAction?function=UjeowUEHndkOWL";
//    public static final String REGISTER_SENDFACEBOOK = "servlet/current/JBDUserAction?function=MofaRegistLogin";

    //忘记密码发送验证码
//	public static final String FORGET_SEND_CODE="servlet/current/JBDUserAction?function=YNSendMsgNewFindPwd";
    public static final String FORGET_SEND_CODE = "servlet/current/JBDUserAction?function=OLAVASendFindNewPwd";
//    public static final String FORGET_SEND_CODE = "servlet/current/JBDUserAction?function=MofaFindPwdCome";

    //忘记密码核对验证码
//	public static final String CHECK_CODE="servlet/current/JBDUserAction?function=FindPasswordYN";
    public static final String CHECK_CODE = "servlet/current/JBDUserAction?function=FindOLAVApwdYN";
//    public static final String CHECK_CODE = "servlet/current/JBDUserAction?function=MofaQRPwdOk";

    //更改密码
//	public static final String FORGET_CODE="servlet/current/JBDUserAction?function=FindPasswordChange";
    public static final String FORGET_CODE = "servlet/current/JBDUserAction?function=FindChangeOLAVApwd";
//    public static final String FORGET_CODE = "servlet/current/JBDUserAction?function=MofaChangePWd";

    //消息中心
//	public static final String NEWS_CORD=URL+"servlet/current/JBDUserAction?function=FindMsg";
    public static final String NEWS_CORD = URL + "servlet/current/JBDUserAction?function=FindOLAVANewMsg";
//    public static final String NEWS_CORD = URL + "servlet/current/JBDUserAction?function=MofaNewMsgZX";

    //认证身份
//	public static final String GETSHENFEN = URL + "servlet/current/JBDUserAction?function=GetShenfen";
    public static final String GETSHENFEN = URL + "servlet/current/JBDUserAction?function=GetSfenLLDfen";
//    public static final String GETSHENFEN = URL + "servlet/current/JBDUserAction?function=MofaShenFenRZ";

    //绑定银行卡
//	public static final String GETBANKCARD=URL+"servlet/current/JBDUserAction?function=GetBankCard";
//	public static final String GETBANKCARD2=URL+"servlet/current/JBDUserAction?function=GetBankCardOLAVA";
    public static final String GETBANKCARD2 = URL + "servlet/current/JBDUserAction?function=GetOLVBankNewCard";
//    public static final String GETBANKCARD2 = URL + "servlet/current/JBDUserAction?function=MofaUserBankRZ";

    //联系人认证
//	public static final String GETCONTACT = URL + "servlet/current/JBDUserAction?function=GetContact";
    public static final String GETCONTACT = URL + "servlet/current/JBDUserAction?function=GetNewConLLSD";
//    public static final String GETCONTACT = URL + "servlet/current/JBDUserAction?function=MofaContactRZ";

    //认证工作
//	public static final String GETWORK = URL + "servlet/current/JBDUserAction?function=GetWork";
    public static final String GETWORK = URL + "servlet/current/JBDUserAction?function=GetGZsdWork";
//    public static final String GETWORK = URL + "servlet/current/JBDUserAction?function=MofaWorkRZ";

    //认证学生
//	public static final String GETSCHOOL = URL + "servlet/current/JBDUserAction?function=GetSchool";
    public static final String GETSCHOOL = URL + "servlet/current/JBDUserAction?function=GetXXSSnelSchool";
//    public static final String GETSCHOOL = URL + "servlet/current/JBDUserAction?function=MofaSchoolRZ";

    //认证facebook
//	public static final String GETFACEBOOK = URL + "servlet/current/JBDUserAction?function=GetFaceBook";
    public static final String GETFACEBOOK = URL + "servlet/current/JBDUserAction?function=GetLLDfcBook";

    //借款
//	public static final String IWANTMONEY_CORD=URL+"servlet/current/JBDUserAction?function=AddUserJK";
    public static final String IWANTMONEY_CORD = URL + "servlet/current/JBDUserAction?function=NewJKLLDUser";
//    public static final String IWANTMONEY_CORD = URL + "servlet/current/JBDUserAction?function=MofaTiJiaoJK";

    //借款记录
//	public static final String JKRECORD_CORD=URL+"servlet/current/JBDUserAction?function=ShowJKJD&type=0";	
    public static final String JKRECORD_CORD = URL + "servlet/current/JBDUserAction?function=ShowZSolvJD";
//    public static final String JKRECORD_CORD = URL + "servlet/current/JBDUserAction?function=MofaJKjiLU";

    //还款项目
//	public static final String BACKMONEYINIT_CORD=URL+"servlet/current/JBDUserAction?function=RepaymentJK";
    public static final String BACKMONEYINIT_CORD = URL + "servlet/current/JBDUserAction?function=RepaySDrrHC";
//    public static final String BACKMONEYINIT_CORD = URL + "servlet/current/JBDUserAction?function=MofaHuKuXM";

    //上传视频
//	public static final String VIDEO_CORD=URL+"servlet/current/JBDUserAction?function=GetSPSH";
    public static final String VIDEO_CORD = URL + "servlet/current/JBDUserAction?function=GetVideokoc";
//    public static final String VIDEO_CORD = URL + "servlet/current/JBDUserAction?function=MofaVideoLoad";

    //还款
//	public static final String CONFIRMBM_MONEY_BACK = URL + "servlet/current/JBDUserAction?function=HKQD";
    public static final String CONFIRMBM_MONEY_BACK = URL + "servlet/current/JBDUserAction?function=GetNewSDhuanQ";
//    public static final String CONFIRMBM_MONEY_BACK = URL + "servlet/current/JBDUserAction?function=MofaHKPinZhen";

    //银行卡管理
//	public static final String CARD_BANAME_CORD=URL+"servlet/current/JBDUserAction";
    public static final String CARD_BANAME_CORD = URL + "servlet/current/JBDUserAction?function=DeFUCnewKB";
//    public static final String CARD_BANAME_CORD = URL + "servlet/current/JBDUserAction?function=MofaBankGuanL";

    //getIP
    public static final String GETLOCATION = URL + "servlet/current/JBDUserAction?function=GetIP";
//    public static final String GETLOCATION = URL + "servlet/current/JBDUserAction?function=MofaDingWIP";


    //版本升级
    public static final String UPDATE_CODE_OLD = "apk/updateolava.json";
    public static final String UPDATE_CODE = "apk/updateolavanew.json";

    //常见问题
    public static final String PROLEM_CODE = URL + "androidHtml/YNlldcjwt_olava.html";

    //隐私权政策
    public static final String PRIVACY_CODE = "https://olava.vn/CSBM-olava.html";

    //职业选择
    public static final String GETPROFESSION = URL + "servlet/current/JBDUserAction?function=GetProfession";

    //分享红包
    public static final String SHAREHONGBAO = URL + "servlet/current/JBDUserAction?function=FenXiangHB";

    public static final String SHARELOGO_CODE = URL + "page/images/lld.png";
    public static final String SHOWNOFITION_CODE = URL + "page/images/lld.png";
    public static final String REGISTERADDRES_CODE = URL + "wx/main/YNlldzcxy.html";
    public static final String OUTMONEYPROTOL_CODE = URL + "wx/main/lldjkxy.html";
    public static final String BANKCARDPROTOL_CODE = URL + "wx/main/lldrzxy.html";


    public static final String OUTMONEY_RECORD = URL + "servlet/current/JBDUserAction?function=GetShowJD";
    public static final String PAY_RESULT_CORD = URL + "servlet/current/JBDModelAction?function=getAuthResultApp";
    public static final String PAY_START_CORD = URL + "servlet/current/JBDModelAction?function=AppPayAuth";
    public static final String RELATION_INFO_CORD = URL + "servlet/current/JBDUserAction?function=AddContactIofo";
    public static final String WORK_INFO_CORD = URL + "servlet/current/JBDUserAction?function=AddJobIofo";

    public static final String AUTHMOBILE_CORD = URL + "servlet/current/JBDUserAction?function=GetConversation";
    public static final String AUTHTAOBAO_CORD = URL + "servlet/current/JBDUserAction?function=GetTabao";
    public static final String AUTHJD_CORD = URL + "servlet/current/JBDUserAction?function=GetJD";
    public static final String JKSEND_CORD = URL + "servlet/current/JBDUserAction?function=SendGSDJkMsg";

    public static final String MONEYRECORDE_CORD = URL + "servlet/current/JBDUserAction?function=ShowTGInfo";

    public static final String SHARE_CORD = URL + "LLD/lld.html?";
    public static final String BACKMONEY_CORD = URL + "servlet/current/JBDModelAction?function=RepaymentJKMoney";
    public static final String ASKMONEYINIT_CORD = URL + "servlet/current/JBDUserAction?function=GetYHKT";
    public static final String ASKMONEY_CORD = URL + "servlet/current/JBDUserAction?function=TjJKMoney";

    public static final String COMMITINVESTER_CORD = URL
            + "servlet/current/JBDInvestAction?function=AddInvestor";
    public static final String BORROWPERSON_CORD = URL
            + "servlet/current/JBDInvestAction?function=GetInvestList";
    public static final String BORROWPERSONDETAIL_CORD = URL
            + "servlet/current/JBDInvestAction?function=ToInvest";
    public static final String CHECKBORROWPERSON_CORD = URL
            + "servlet/current/JBDInvestAction?function=SubmitInvestment";
    public static final String CANCLEBORROWPERSON_CORD = URL
            + "servlet/current/JBDInvestAction?function=InvestmentReturn";
    public static final String PAYBORROWPERSON_CORD = URL
            + "servlet/current/JBDInvestAction?function=InvestmentResults";
    public static final String INVESTMONEY_CORD = URL
            + "servlet/current/JBDInvestAction?function=InvestmentInformation";
    public static final String INVESTMONEYRECORDE_CORD = URL
            + "servlet/current/JBDInvestAction?function=onloan";
    public static final String OVERDUELIST_CORD = URL
            + "servlet/current/JBDInvestAction?function=OverdueList";
    public static final String OVERDUEPHONE_CORD = URL
            + "servlet/current/JBDInvestAction?function=Tocollect";
    public static final String TXMONEY_CORD = URL
            + "servlet/current/JBDUserAction?function=PresentationRecord";
    public static final String PAY_RESULTLL_CORD = URL
            + "servlet/current/JBDLLpayAction?function=GetAuthResultApp";//连连支付
    public static final String PAY_LLSTART_CORD = URL
            + "servlet/current/JBDLLpayAction?function=AppPayAuth";//连连支付
    public static final String BACKMONEYLL_CORD = URL
            + "servlet/current/JBDLLpayAction?function=RepaymentJKMoney";//连连支付
    public static final String CONFIRMBM_CORD = URL
            + "servlet/current/JBDcms3Action?function=Repayment";

    public static final int CODE_UPDATE_DIALOG = 0;
    public static final int CODE_URL_ERROR = 1;
    public static final int CODE_NET_ERROR = 2;
    public static final int CODE_JSON_ERROR = 3;
    public static final int CODE_ENTER_HOME = 4;
    public static final int CODE_TIMEOUT_ERROR = 5;
    public static final int CODE_ERROR = 6;
    public static final int CODE_LOGIN = 1001;
    public static final int CODE_SEND = 1002;
    public static final int CODE_RGISTER = 1003;
    public static final int CODE_FORGET_PWD = 1004;
    public static final int CODE_FORGET_PWD_FACEBOOK = 1134;
    public static final int CODE_FORGET_SEND_PWD = 1005;
    public static final int CODE_CHECk_SEND_PWD = 1006;
    public static final int CODE_HOME_INIT = 1007;
    public static final int CODE_OUTMONEY_INIT = 1008;
    public static final int CODE_PAY_RESULT = 1009;
    public static final int CODE_PAY_START = 1010;
    public static final int CODE_RELATION_INFO = 1011;
    public static final int CODE_WORK_INFO = 1012;
    public static final int CODE_CARD_MANAGE = 1013;
    public static final int CODE_IWANTMONEY = 1014;
    public static final int CODE_AUTHMOBILE = 1015;
    public static final int CODE_JKSEND = 1016;
    public static final int CODE_JKRECORD = 1017;
    public static final int CODE_MONEYRECORDE = 1018;
    public static final int CODE_VIDEO = 1019;
    public static final int CODE_SHARE = 1020;
    public static final int CODE_BACKMONEY = 1021;
    public static final int CODE_BACKMONEYINIT = 1022;
    public static final int CODE_ASKMONEYINIT = 1023;
    public static final int CODE_ASKMONEY = 1024;
    public static final int CODE_NEWS = 1025;
    public static final int CODE_BANK = 1026;
    public static final int CODE_ZMSTART = 1027;
    public static final int CODE_ZMEND = 1028;
    public static final int CODE_TXMONEY = 1029;
    public static final int CODE_CONFIRMBACK = 1031;
    public static final int CODE_SENDFACEBOOK = 1032;
    public static final int CODE_SENDFACEBOOK_FORGET = 1033;

    public static final int CODE_GETSHENFEN = 1050;
    public static final int CODE_GETBANKCARD = 1051;
    public static final int CODE_GETWORK = 1052;
    public static final int CODE_GETSCHOOL = 1053;
    public static final int CODE_GETCONTACT = 1054;
    public static final int CODE_CONFIRMMONEYBACK = 1055;
    public static final int CODE_GETFACEBOOK = 1056;
    public static final int CODE_GETPROFESSION = 1057;

    public static final int FRONT_PROGRESS = 201;
    public static final int BACK_PROGRESS = 202;
    public static final int HANDHELD_PROGRESS = 203;

    public static final int FRONT_SUCCESSED = 2011;
    public static final int BACK_SUCCESSED = 2022;
    public static final int HANDHELD_SUCCESSED = 2033;
    public static final int FRONT_FAILED = 2012;
    public static final int BACK_FAILED = 2023;
    public static final int HANDHELD_FAILED = 2034;

    public static final int UPLOAD_SUCCESSED = 2011;
    public static final int UPLOAD_FAILED = 2012;

    public static final int GET_CONTACTS = 3331;
    public static final int CODE_CONTACT = 3332;
    public static final int CODE_CONTACT_FAILED = 3333;
    public static final int CODE_CONTACT_SUCCESS = 3334;
    public static final int CODE_CONTACT_FAIL = 3335;

    public static final int CODE_GET_LOCATION = 2000;
    public static final int CODE_GET_LOCATION_LOAN = 2001;
    public static final int CODE_GETIP = 2002;

    public static final int CODE_HONGBAO = 2003;

    public static final String OSS_ACCESSKEYID = "LTAIZmB1IQUSkHvU";
    public static final String OSS_ACCESSKEYSECRET = "WZFGiWk1ferhNhdC2ZiFm76L0xYqoP";

    public static String PUCLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC3+bWQP/qqpjzT2XruGDwhbWDlofPNuvufFm5+vrQvG8WhBrN63hfwg+NhR2NQxvLRaGNoklWKp4hUZa3rm6myMLaBGKV3BEuXl1CSWf1TBJSBMT1zy5C/G+3w1/jrf779QtX0gUixQ9YdwHShJTcBE+0qhS7MqF+y5Z5NXy+fLwIDAQAB";

}
