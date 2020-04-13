package com.company;

public class JsonHandler
{
    int id;
    int type;
    String js1;
    int time_js1;
    String url;
    String js2;
    int time_js2;

    JsonHandler()
    {

    }

    public String all_to_string()
    {
        return "JsonHandler{" +
                "id='" + id + '\'' + "," +
                "type='" + type + '\'' + "," +
                "js1='" + js1 + '\'' + "," +
                "time_js1='" + time_js1 + '\'' + "," +
                "url='" + url + '\'' + "," +
                "js1='" + js2 + '\'' + "," +
                "time_js2='" + time_js2 +
                '\'' + "}";
    }

}
