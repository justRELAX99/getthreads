package com.company;

public class JsonHandler
{
    int id;
    int type;
    String proxy;
    String user_agent;
    String cookies_path;
    String url;
    String js1;
    int time_js1;
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
                "proxy='" + proxy + '\'' + "," +
                "user_agent='" + user_agent + '\'' + "," +
                "cookies_path='" + cookies_path + '\'' + "," +
                "url='" + url + '\'' + "," +
                "js1='" + js1 + '\'' + "," +
                "time_js1='" + time_js1 + '\'' + "," +
                "js1='" + js2 + '\'' + "," +
                "time_js2='" + time_js2 +
                '\'' + "}";
    }

}
