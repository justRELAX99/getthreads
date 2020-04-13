package com.company;

import com.google.gson.Gson;

public class JsonParser
{
    JsonParser()
    {

    }

    public JsonHandler[] get_AllJsonHandlers(String json)
    {
        Gson gson = new Gson();
        JsonHandler[] all_json_handlers = gson.fromJson(json, JsonHandler[].class);
        return all_json_handlers;
    }
}
