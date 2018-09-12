package com.example.hp.iris;

/**
 * Created by Aravindh balaji on 22-03-2018.
 */

public class ImagePathAdapter {

    public ImagePathAdapter() {
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public static String path;
    public static String name;

    public static String getName() {
        return name;
    }

    public static void setName(String name) {
        ImagePathAdapter.name = name;
    }

    public static String getRelation() {
        return relation;
    }

    public static void setRelation(String relation) {
        ImagePathAdapter.relation = relation;
    }

    public static String relation ;

}
