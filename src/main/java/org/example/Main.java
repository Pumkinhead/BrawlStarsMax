package org.example;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;
import java.util.stream.IntStream;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
public class Main {
    public static void main(String[] args) throws ParseException, IOException {
        String AuthToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiIsImtpZCI6IjI4YTMxOGY3LTAwMDAtYTFlYi03ZmExLTJjNzQzM2M2Y2NhNSJ9.eyJpc3MiOiJzdXBlcmNlbGwiLCJhdWQiOiJzdXBlcmNlbGw6Z2FtZWFwaSIsImp0aSI6IjU5YTEwYmNiLWVjMTAtNGQ2Mi04NDFmLTNhMTgwOTk1ZTQzOSIsImlhdCI6MTcxNDQ5NTQ2Nywic3ViIjoiZGV2ZWxvcGVyLzhhOTJlMzk3LTJhZDAtNTFiZS1lM2M1LTBkZDExZjgzNGYxZSIsInNjb3BlcyI6WyJicmF3bHN0YXJzIl0sImxpbWl0cyI6W3sidGllciI6ImRldmVsb3Blci9zaWx2ZXIiLCJ0eXBlIjoidGhyb3R0bGluZyJ9LHsiY2lkcnMiOlsiNzguNjEuNjEuMTc0Il0sInR5cGUiOiJjbGllbnQifV19.Ll1XXKL0q1FOmJmvTe4l01d-nDm3vF8KvvMW6V3KPpSQBZTvAuVeVlh4deSv4KzLydJjHlrCNTa4bggAi_oEfQ";
        Integer brawlercount = 79;
        Integer[] GearsRare = {62000000, 62000001, 62000002, 62000003, 62000004, 62000017};
        Integer[] GearsEpic = {62000005, 62000006, 62000014};
        Integer[] UpgradesCoins = {0, 20, 55, 130, 270, 560, 1040, 1840, 3090, 3965, 7765};
        Integer[] UpgradesPowerPoints = {0, 20, 50, 100, 180, 310, 520, 860, 1410, 2300, 3740};
        Integer MaxPowerCoins = UpgradesCoins[UpgradesCoins.length-1];
        Integer MaxPowerPowerPoints = UpgradesPowerPoints[UpgradesPowerPoints.length-1];
        Integer MaxCoins1 = (MaxPowerCoins + 12000) * brawlercount + 25 * 1500 + 10 * 2000; // 12000 = 4000 from both star powers + 2000 from both gadgets + 6000 from 6 gears. 25 * 1500 = 25 epic gears from 25 brawlers. 10 * 2000 = 10 mythic gears from 10 brawlers.
        Integer MaxCoins2 = (MaxPowerCoins + 8000) * brawlercount; //8000 = 4000 from both star powers + 2000 from both gadgets + 2000 from two gears
        Integer MaxCoins3 = MaxPowerCoins * brawlercount;
        Integer MaxPowerPoints = brawlercount * MaxPowerPowerPoints;
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String tag = reader.readLine();
        if(tag.startsWith("#")){
            tag=tag.substring(1);
        }
        URL url = new URL("https://api.brawlstars.com/v1/players/%23"+tag);
        HttpURLConnection con = null;
        con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setDoOutput(true);
        con.setRequestProperty("JSON", "application/json");
        con.setRequestProperty("Authorization", "Bearer "+AuthToken);
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        Integer PowerPoints = 0;
        Integer Coins1 = 0;
        Integer Coins2 = 0;
        Integer Coins3 = 0;
        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(String.valueOf(content));
        String name = String.valueOf(json.get("name"));
        JSONArray brawlers = (JSONArray) json.get("brawlers");
        for (Object brawlerUnprocessed : brawlers) {
            Integer startCoins = Coins1;
            JSONObject brawler = (JSONObject) brawlerUnprocessed;
            JSONArray starPowers = (JSONArray) brawler.get("starPowers");
            JSONArray gadgets = (JSONArray) brawler.get("gadgets");
            JSONArray gears = (JSONArray) brawler.get("gears");
            int power = Integer.parseInt(String.valueOf(brawler.get("power")));
            power--;
            PowerPoints += UpgradesPowerPoints[power];
            int Coins=0;
            int PowerCost = UpgradesCoins[power];
            Coins += PowerCost;
            Coins3 += Coins;
            int StarPowerCost = starPowers.size() * 2000;
            Coins += StarPowerCost;
            int GadgetCost = gadgets.size() * 1000;
            Coins += GadgetCost;
            Coins1 += Coins;
            Coins2 += Coins;
            int twoGears = Math.min(gears.size(), 2);
            int twoGearsCost = twoGears * 1000;
            Coins2 += twoGearsCost;
            int gearCost=0;
            for(Object gearUnprocessed : gears){
                JSONObject gear = (JSONObject) gearUnprocessed;
                Integer id = Integer.parseInt(String.valueOf(gear.get("id")));
                int cost = 2000;
                if(Arrays.asList(GearsRare).contains(id)){
                    cost = 1000;
                }else if(Arrays.asList(GearsEpic).contains(id)){
                    cost = 1500;
                }
                gearCost+=cost;
            }
            Coins1+=gearCost;
        }
        float Coins1Percent = (float) Coins1 / MaxCoins1;
        float Coins2Percent = (float) Coins2 / MaxCoins2;
        float Coins3Percent = (float) Coins3 / MaxCoins3;
        float PowerPointPercent = (float) PowerPoints / MaxPowerPoints;
        Coins1Percent = (float) Math.round(Coins1Percent * 100000) / 1000;
        Coins2Percent = (float) Math.round(Coins2Percent * 100000) / 1000;
        Coins3Percent = (float) Math.round(Coins3Percent * 100000) / 1000;
        PowerPointPercent = (float) Math.round(PowerPointPercent * 100000) / 1000;
        System.out.println(name + "'s stats:");
        System.out.println(PowerPoints + " / " + MaxPowerPoints + " power points. " + PowerPointPercent + "% maxed out. " + (MaxPowerPoints - PowerPoints) + " power points left.");
        System.out.println(Coins3 + " / " + MaxCoins3 + " coins. " + Coins3Percent + "% all power 11. " + (MaxCoins3 - Coins3) + " coins left.");
        System.out.println(Coins2 + " / " + MaxCoins2 + " coins. " + Coins2Percent + "% all power 11 + 2 star powers, gears and gadgets. " + (MaxCoins2 - Coins2) + " coins left.");
        System.out.println(Coins1 + " / " + MaxCoins1 + " coins. " + Coins1Percent + "% fully maxed out. " + (MaxCoins1 - Coins1) + " coins left.");
    }
}