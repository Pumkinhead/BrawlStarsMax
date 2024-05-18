package org.example;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
public class Main {
    public static void main(String[] args) throws ParseException, IOException {
        System.out.print("Input player tag: ");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String tag = reader.readLine();
        System.out.println(Get.Stats(tag));
    }
}