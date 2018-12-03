package com.lotterental.common.util;

import android.util.Log;
import android.widget.Button;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ComAg on 16. 3. 21..
 */
public class Define {

    /**
     * Preference Key에서 사용하는 값
     */
    // 세션 키
    public static String PRKEY_MOBSESSKEY = "mobSessKey";

    /*
        210 : 부품주문 등록
        215 : 부품주문 현황
        220 : 수납 등록
        310 : 출고 등록
        320 : 출고 확정
        321 : 출고 현황
        330 : 구매입고 등록
        340 : 입고 확정
        350 : 입고 현황
        360 : 자재이동 요청
        370 : 자재이동 현황
        380 : 자재이동 미입고 현황
        390 : 재고현황
        410 : 환경설정
     */
    // 로그인 아이디
    public static String PRKEY_LOGINID = "loginID";
    // 로그인 아이디 저장
    public static String PRKEY_LOGINID_SAVE = "saveID";
    // 로그인 일자
    public static String PRKEY_LOGIN_DATE = "loginDate";
    // 푸쉬 키
    public static String PRKEY_LOGIN_PUSH = "PUSH_KEY";
    // 푸쉬 발송여부
    public static String PRKEY_PUSH_ACTION = "PUSH_ACTION";
    // 푸쉬 화면 이동 명칭
    public static String PRKEY_PUSH_SCREEN_NAME = "PUSH_SCREEN_NAME";
    // 푸쉬 화면 아이디
    public static String PRKEY_PUSH_SCREEN_CODE = "PUSH_SCREEN_CODE";


    public static String ID = "0000036189";

    /**
     * 팝업 태그
     */
    // 아무런 작업 안함
    public static String ALERT_NONE = "NONE";
    // 앱 종료 or 로그아웃
    public static String ALERT_EXIT = "EXIT";
    /**
     * 팝업 메시지 정리
     */
    /* Login Message */
    public static String MSG_LOGIN_ID = "아이디를 입력해주세요.";
    public static String MSG_LOGIN_PW = "패스워드를 입력해주세요.";
    /* Exit Message */
    public static String MSG_EXIT = "종료 하시겠습니까?";
    public static String MSG_EXIT_WORKiNG = "업무진행 중에는 앱이 항상 실행중이어야 합니다.\n그래도 앱을 종료하시겠습니까?";
    /* 상단 메뉴 에러 메시지 */
    public static String MSG_TOP_MENU_NONE = "선택 가능한 하위 메뉴가 없습니다.";
    /* 블루투스 활성화 관련 메시지 */
    public static String MSG_BLUETOOTH_UNCONNECT = "블루투스 연결에 실패했습니다.";
    public static String MSG_BLUETOOTH_CONNECT = "블루투스 연결에 성공했습니다.";
    /**
     * 팝업 프래그먼트 정보 정리
     */
    public static String TAG_POPUP_FRAGMENT = "POPUP_FRAGMENT";



    /**
     * null 체크 하여 null이면 공백 반환
     *
     * @param value : 체크 할 값
     * @return
     */
    public static String nullCheck(Object value) {

        return value == null ? "" : value.toString().trim().equals("null") ? "" : value.toString().trim();
    }

    /**
     * 숫자에 ,를 붙여서 반환한다.
     *
     * @param value
     * @return
     */
    public static String formatToMoney(String value) {

        long numberTemp = 0;
        try {
            if (Define.nullCheck(value).length() > 0) {
                if (value.indexOf(".") > -1) {
                    float chNumber = Float.valueOf(value);
                    numberTemp = (long) chNumber;
                } else {
                    numberTemp = Long.valueOf(value);
                }

                StringBuffer sb = new StringBuffer();
                sb.append(NumberFormat.getNumberInstance().format(numberTemp)).append("원");
                return sb.toString().length() == 0 ? "0원" : sb.toString();
            }
        } catch (NumberFormatException e) {
            Log.e("Error", "Error : " + e.getMessage());
            e.printStackTrace();
        }

        return "0원";
    }

    public static String formatToNumber(String value) {

        long numberTemp = 0;
        try {
            if (Define.nullCheck(value).length() > 0) {
                if (value.indexOf(".") > -1) {
                    float chNumber = Float.valueOf(value);
                    numberTemp = (long) chNumber;
                } else {
                    numberTemp = Long.valueOf(value);
                }

                StringBuffer sb = new StringBuffer();
                sb.append(NumberFormat.getNumberInstance().format(numberTemp));
                return sb.toString().length() == 0 ? "0" : sb.toString();
            }
        } catch (ClassCastException e) {
            Log.e("Error", "Error : " + e.getMessage());
//            e.printStackTrace();
        }

        return "0";
    }

    /**
     * 전화번호 형식으로 반환한다.
     *
     * @param telNumber
     * @return
     */
    public static String formatToTelNumber(String telNumber) {

        if (telNumber == null)
            return "";

        Pattern tellPattern = Pattern.compile("^(01\\d{1}|02|0\\d{1,2})-?(\\d{3,4})-?(\\d{4})");
        Matcher matcher = tellPattern.matcher(telNumber);
        if (matcher.matches()) {
            return matcher.group(1) + "-" + matcher.group(2) + "-" + matcher.group(3);
        } else {
            return telNumber;
        }
    }

    /**
     * 날짜를 입력받아 yyyy.MM.dd (월) 형식으로 반환
     *
     * @param date
     * @return
     */
    public static String formatToDate(String date) {

        if (date == null)
            return "";

        String pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS";
        String pattern2 = "yyyy-MM-dd HH:mm:ss";
        String pattern3 = "yyyy-MM-dd HH:mm";
        String pattern4 = "yyyy.MM.dd HH:mm:ss";
        String pattern5 = "yyyy-MM-dd'T'HH:mm:ss";

        Calendar calendar = getPatternToDate(pattern, date);
        if (calendar == null)
            calendar = getPatternToDate(pattern2, date);
        if (calendar == null)
            calendar = getPatternToDate(pattern3, date);
        if (calendar == null)
            calendar = getPatternToDate(pattern4, date);
        if (calendar == null)
            calendar = getPatternToDate(pattern5, date);

        if (calendar == null) {
            // 특수문자 제거
            date = date.replaceAll("\\p{Punct}", "");

            int[] dates = DateUtil.getDateArr(date);

            if (dates != null) {
                StringBuffer sb = new StringBuffer();
                sb.append(DateUtil.getDateFormat(date, "-")).append("(");
                sb.append(DateUtil.WEEK_TYPE1[DateUtil.getDayWeek(dates[0], dates[1] - 1, dates[2]) - 1]).append(")");

                return sb.toString();
            }
        } else {

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            int day = calendar.get(Calendar.DATE);
            int week = calendar.get(Calendar.DAY_OF_WEEK) - 1;

            StringBuffer sb = new StringBuffer();
            sb.append(String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month, day));
            sb.append(String.format(Locale.getDefault(), "(%s)", DateUtil.WEEK_TYPE1[week]));

            return sb.toString();
        }

        return "";
    }

    /**
     * 날짜, 시간을 입력받아 yyyy-MM-dd(월)HH:mm 형식으로 반환
     * Formatter 형식 : yyyy-MM-dd'T'HH:mm:ss.SSS
     *
     * @param date
     * @return - 데이터가 에러이면 공백
     */
    public static String formatToDateTime(String date) {

        if (date == null)
            return "";

        String pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS";
        String pattern2 = "yyyy-MM-dd HH:mm:ss";
        String pattern3 = "yyyy-MM-dd HH:mm";
        String pattern4 = "yyyy.MM.dd HH:mm:ss";
        String pattern5 = "yyyy-MM-dd'T'HH:mm:ss";

        Calendar calendar = getPatternToDate(pattern, date);
        if (calendar == null)
            calendar = getPatternToDate(pattern2, date);
        if (calendar == null)
            calendar = getPatternToDate(pattern3, date);
        if (calendar == null)
            calendar = getPatternToDate(pattern4, date);
        if (calendar == null)
            calendar = getPatternToDate(pattern5, date);

        if (calendar != null) {

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            int day = calendar.get(Calendar.DATE);
            int week = calendar.get(Calendar.DAY_OF_WEEK) - 1;
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int min = calendar.get(Calendar.MINUTE);

            StringBuffer sb = new StringBuffer();
            sb.append(String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month, day));
            sb.append(String.format(Locale.getDefault(), "(%s)", DateUtil.WEEK_TYPE1[week]));
            sb.append(String.format(Locale.getDefault(), " %02d:%02d", hour, min));

            return sb.toString();
        }

        return "";
    }

    /**
     * 날짜를 입력 받아 yyyy-MM-dd 형식으로 반환
     *
     * @param date
     * @return
     */
    public static String formatToSendingDate(String date) {

        if (date == null)
            return "";

        String pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS";
        String pattern2 = "yyyy-MM-dd HH:mm:ss";
        String pattern3 = "yyyy-MM-dd HH:mm";
        String pattern4 = "yyyy.MM.dd HH:mm:ss";
        String pattern5 = "yyyy-MM-dd'T'HH:mm:ss";

        Calendar calendar = getPatternToDate(pattern, date);
        if (calendar == null)
            calendar = getPatternToDate(pattern2, date);
        if (calendar == null)
            calendar = getPatternToDate(pattern3, date);
        if (calendar == null)
            calendar = getPatternToDate(pattern4, date);
        if (calendar == null)
            calendar = getPatternToDate(pattern5, date);

        if (calendar != null) {

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            int day = calendar.get(Calendar.DATE);

            StringBuffer sb = new StringBuffer();
            sb.append(String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month, day));

            return sb.toString();
        }

        return "";
    }

    /**
     * 패턴에 맞는 데이트 format형식으로 반환한다.
     *
     * @param date
     * @return
     */
    private static Calendar getPatternToDate(String pattern, String date) {

        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

            Date dateTime = simpleDateFormat.parse(date);

            if (dateTime != null) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(dateTime);

                return calendar;
            }

        } catch (ParseException e) {
//            e.printStackTrace();
        }

        return null;
    }

    /**
     * 한달 이전값을 계산해서 반환한다.
     *
     * @param dates : 한달 이전값을 계산할 기준 값
     * @return
     */
    public static int[] getBeforOneMonthDate(int[] dates) {

        int[] beforeDate = new int[3];

        if (dates == null || dates.length < 3) {

            return beforeDate;
        }

        if (dates[1] == 1) {
            beforeDate[0] = dates[0] - 1;
            beforeDate[1] = 12;
            beforeDate[2] = dates[2] + 1;
        } else {
            beforeDate[0] = dates[0];
            beforeDate[1] = dates[1] - 1;

            int lastDay = DateUtil.getLastDay(beforeDate[0], beforeDate[1] - 1);
            if (lastDay < dates[2] + 1) {
                beforeDate[2] = lastDay;
            } else {
                beforeDate[2] = dates[2] + 1;
            }
        }

        return beforeDate;
    }

    /**
     * 이메일 형식을 체크해서 반환한다.
     * @param email
     * @return
     */
    public static boolean validEmail(String email) {

        final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX .matcher(email);
        return matcher.find();
    }

    /**
     * 버튼에 날짜를 지정한다
     *
     * @param btnDate
     * @param date
     */
    public static void setButtonDateValueTag(Button btnDate, int[] date) {

        if (date == null || date.length < 3) {

            return;
        }

        int sWeek = DateUtil.getDayWeek(date[0], date[1] - 1, date[2]);

        String displayDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", date[0], date[1], date[2]);
        String tagDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", date[0], date[1], date[2]);

        btnDate.setText(displayDate);
        btnDate.setTag(tagDate);
    }

    /**
     * 버튼에 날짜, 시간을 지정한다
     *
     * @param btnDate
     * @param date
     */
    public static void setButtonDateTimeValueTag(Button btnDate, int[] date, String hour, String minute) {

        if (date == null || date.length < 3) {

            return;
        }

        int sWeek = DateUtil.getDayWeek(date[0], date[1] - 1, date[2]);

        String displayDate = String.format(Locale.getDefault(), "%04d-%02d-%02d (%s) %s:%s", date[0], date[1], date[2], DateUtil.WEEK_TYPE1[sWeek - 1], hour, minute);
        String tagDate = String.format(Locale.getDefault(), "%04d-%02d-%02d %s%s", date[0], date[1], date[2], hour, minute);

        btnDate.setText(displayDate);
        btnDate.setTag(tagDate);
    }
    /**
     * JsonArray 정보를 ArrayList<HashMap<String, String>> 형태로 반환
     *
     * @param jsonArray
     * @return : 정보가 없다면 기본적으로 생성만 해서 전달
     */
    public static ArrayList<HashMap<String, String>> jsonArrayToArrayList(JSONArray jsonArray) {

        ArrayList<HashMap<String, String>> arrDatas = new ArrayList<HashMap<String, String>>();

        if (jsonArray == null)
            return arrDatas;

        for (int i = 0; i < jsonArray.length(); i++) {

            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                HashMap<String, String> mapDatas = new HashMap<String, String>();

                Iterator<String> keys = jsonObject.keys();

                while (keys.hasNext()) {
                    String key = keys.next();
                    mapDatas.put(key, Define.nullCheck(jsonObject.get(key)));
                }

                arrDatas.add(mapDatas);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return arrDatas;
    }

    /**
     * jsonObject 데이타를 HashMap 데이타로 변환
     *
     * @param jsonObject
     * @return
     */
    public static HashMap<String, String> jsonObjectToHashMap(JSONObject jsonObject) {

        HashMap<String, String> mapDatas = new HashMap<String, String>();

        if (jsonObject == null)
            return mapDatas;

        Iterator<String> keys = jsonObject.keys();
        try {
            while (keys.hasNext()) {
                String key = keys.next();
                mapDatas.put(key, Define.nullCheck(jsonObject.get(key)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return mapDatas;
    }

    public static void validDataCheckMap(String[] keys, CnnType[] types, HashMap<String, String> map) {

        for (int i = 0; i < keys.length; i++) {

            if (types[i] == CnnType.TEXT) {

                if (nullCheck(map.get(keys[i])).length() == 0) {

                    map.remove(keys[i]);
                }
            } else if (types[i] == CnnType.NUM) {

                if (nullCheck(map.get(keys[i])).length() == 0) {

                    map.put(keys[i], "0");
                } else if (nullCheck(map.get(keys[i])).indexOf(".") > -1) {

                    String[] numValue = nullCheck(map.get(keys[i])).split("\\.");
                    if (numValue != null && numValue.length > 0) {
                        map.put(keys[i], numValue[0]);
                    }
                }
            }
        }
    }

    public static void validDataCheckList(String[] keys, CnnType[] types, ArrayList<HashMap<String, String>> list) {

        for (HashMap<String, String> map : list) {

            validDataCheckMap(keys, types, map);
        }
    }

    /**
     * LOGIN : 로그인(9999)
     * HOME : 홈(10000)
     * CIRCUIT_MAINTENANCE : 순회정비(100)
     * BUSINESS : 영업수납(200)
     * PART : 부품관리(300)
     * ETC : 기타업무(400)
     */
    public static enum ScreenCode {
        LOGIN("9999"), HOME("10000"), CIRCUIT_MAINTENANCE("100"), BUSINESS("200"), PART("300"), ETC("400");

        public String code;

        ScreenCode(String code) {
            this.code = code;
        }
    }

    /*
        000 : Login 화면
        001 : HomeTypeA
        002 : HomeTypeA
        003 : HomeTypeA
        110 : 영
        210 : 부품주문 등록
        215 : 부품주문 현황
        220 : 수납 등록
        310 : 출고 등록
        320 : 출고 확정
        321 : 출고 현황
        330 : 구매입고 등록
        340 : 입고 확정
        350 : 입고 현황
        360 : 자재이동 요청
        370 : 자재이동 현황
        380 : 자재이동 미입고 현황
        390 : 재고현황
        410 : 환경설정
        425 : 업무콜 수신관리
     */
    public enum Screen2Code {

        LOGIN("000"),
        HOME_A("001"), HOME_B("002"), HOME_C("003"),
        MAINTENANCE_PLAN("110"),
        PART_ORDER_REG("210"), PART_ORDER_REG_LIST("215"), PART_ESTIMATE_REG("216"), PART_ESTIMATE_DOC("2160"), PART_ORDER_RETRUN("217"), RECEIVING_MONEY("220"), PART_ORDER_MANAGER("230"),
        PART_OUT_REQUEST("310"), NEW_PART_REQUEST("315"), PART_OUT_CONFIRM("320"), PART_OUT_LIST("321"),
        PART_IN_REQUEST("330"), PART_IN_CONFIRM("340"), PART_IN_LIST("350"),
        MATERIAL_MOVE_REQUEST("360"), MATERIAL_MOVE_LIST("370"), MATERIAL_MOVE_NOT_IN_LIST("380"),
        STOCK_STATE("390"), STOCK_INVESTIGATION("395"),
        NOTICE("405"), SETTING("410"), WORK_DOC("415") ,  PARTNER_SEARCH("420"), WORK_CALL("425"),PUSH_HISTORY("430"), USER_SEARCH("435"), LOCATION_TEST("499"),
        NONE("100000");

        public String code;

        Screen2Code(String code) {
            this.code = code;
        }
    }

    public enum CnnType {

        TEXT, NUM
    }

}
