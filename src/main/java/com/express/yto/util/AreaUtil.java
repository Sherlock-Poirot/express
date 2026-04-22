package com.express.yto.util;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Detective
 * @date Created in 2025/9/16
 */
public class AreaUtil {

    /**
     * 一区
     */
    public static final String AREA_1 = "江苏省、浙江省、上海、安徽省";

    /**
     * 二区
     */
    public static final String AREA_2 = "北京、广东省、湖南省、福建省、山东省、湖北省、天津、江西省、河南省、河北省";

    /**
     * 三区
     */
    public static final String AREA_3 = "云南省、四川省、辽宁省、贵州省、陕西省、吉林省、山西省、重庆、广西壮族自治区、黑龙江省";

    /**
     * 四区
     */
    public static final String AREA_4 = "海南省、青海省、甘肃省、宁夏回族自治区、内蒙古自治区";

    public static final String AREA_4_2 = "青海省、甘肃省、宁夏回族自治区、内蒙古自治区";

    /**
     * 五区
     */
    public static final String AREA_5 = "新疆维吾尔自治区、西藏自治区";

    public static Map<Integer, String> AREA_DICT = new HashMap<>(8);

    static {
        AREA_DICT.put(1, "一区");
        AREA_DICT.put(2, "二区");
        AREA_DICT.put(3, "三区");
        AREA_DICT.put(4, "四区");
        AREA_DICT.put(5, "五区");
        AREA_DICT.put(6, "六区");
        AREA_DICT.put(7, "七区");
        AREA_DICT.put(8, "八区");
        AREA_DICT.put(9, "九区");
        AREA_DICT.put(10, "十区");    }

    /**
     * 判断某个省份是否是一区二区三区...
     *
     * @param strArea 目的地
     * @param intArea 一区二区....
     * @return 目的地是否符合
     */
    public static Boolean judgeArea(String strArea, Integer intArea) {
        Integer area = getArea(strArea);
        return area.equals(intArea);
    }


    public static Integer getArea(String strArea) {
        int area = 0;
        if (AREA_1.contains(strArea)) {
            area = 1;
        }
        if (AREA_2.contains(strArea)) {
            area = 2;
        }
        if (AREA_3.contains(strArea)) {
            area = 3;
        }
        if (AREA_4.contains(strArea)) {
            area = 4;
        }
        if (AREA_5.contains(strArea)) {
            area = 5;
        }
        return area;
    }

    public static String getAreaName(String area) {
        switch (area) {
            case "1":
                return "一区";
            case "2":
                return "二区";
            case "3":
                return "三区";
            case "4":
                return "四区";
            case "5":
                return "五区";
            default:
                return "未知区域";
        }
    }

    /**
     * 判断是否是
     *
     * @param strArea
     * @param intArea
     * @param flagHaiNan
     * @return
     */
    public static Boolean isThisArea(String strArea, Integer intArea, Boolean flagHaiNan) {
        if (StringUtils.isBlank(strArea)) {
            return false;
        }
        if (null == intArea) {
            return false;
        }
        if ("海南省".equals(strArea) && flagHaiNan && intArea == 3) {
            return true;
        }
        if ("海南省".equals(strArea) && flagHaiNan) {
            return false;
        }
        return judgeArea(strArea, intArea);
    }

    public static Boolean isThisArea(Map<String, Integer> areaMap, String strArea, Integer intArea, Boolean flag){
        if (StringUtils.isBlank(strArea)) {
            return false;
        }
        if (null == intArea) {
            return false;
        }
        if (MapUtils.isEmpty(areaMap)){
            return false;
        }
        if ("海南省".equals(strArea) && flag && intArea == 3) {
            return true;
        }
        if ("海南省".equals(strArea) && flag) {
            return false;
        }
        for (String city : areaMap.keySet()) {
            if (city.contains(strArea)){
               return intArea.equals(areaMap.get(city));
            }
        }
        return false;
    }

}
