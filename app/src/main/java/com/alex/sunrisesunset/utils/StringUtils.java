package com.alex.sunrisesunset.utils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class StringUtils {

    //String "(x,y)" -> List {lat = x, lng = y}
    public static List<String> coordinate(String coordinates){

        String lat;
        String lng;

        lat = coordinates.substring(coordinates.indexOf('(') + 1, coordinates.indexOf(','));
        lng = coordinates.substring(coordinates.indexOf(',') + 1, coordinates.lastIndexOf(')'));
        ArrayList<String> list = new ArrayList();
        list.add(lat);
        list.add(lng);

        return list;
    }
}
