package org.example;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

public class Get {
    public static String Stats(String tag, String AuthToken) throws ParseException, IOException {
        StringBuilder toReturn = new StringBuilder();
        URL brawlerUrl = new URL("https://api.brawlstars.com/v1/brawlers");
        HttpURLConnection brawlerCon;
        brawlerCon = (HttpURLConnection) brawlerUrl.openConnection();
        brawlerCon.setRequestMethod("GET");
        brawlerCon.setDoOutput(true);
        brawlerCon.setRequestProperty("JSON", "application/json");
        brawlerCon.setRequestProperty("Authorization", "Bearer "+AuthToken);
        BufferedReader brawlerInput = new BufferedReader(new InputStreamReader(brawlerCon.getInputStream()));
        String brawlerInputLine;
        StringBuilder brawlerContent = new StringBuilder();
        while ((brawlerInputLine = brawlerInput.readLine()) != null) {
            brawlerContent.append(brawlerInputLine);
        }
        brawlerInput.close();
        JSONParser parser = new JSONParser();
        JSONObject brawlerJSON = (JSONObject) parser.parse(String.valueOf(brawlerContent));
        JSONArray brawlerList = (JSONArray) brawlerJSON.get("items");
        int brawlercount = brawlerList.size();
        Integer[] GearsRare = {62000000, 62000001, 62000002, 62000003, 62000004, 62000017};
        Integer[] GearsEpic = {62000005, 62000006, 62000014};
        Integer[] UpgradesCoins = {0, 20, 55, 130, 270, 560, 1040, 1840, 3090, 3965, 7765};
        Integer[] UpgradesPowerPoints = {0, 20, 50, 100, 180, 310, 520, 860, 1410, 2300, 3740};
        int MaxPowerCoins = UpgradesCoins[UpgradesCoins.length-1];
        int MaxPowerPowerPoints = UpgradesPowerPoints[UpgradesPowerPoints.length-1];
        int MaxCoins1 = (MaxPowerCoins + 12000) * brawlercount + 25 * 1500 + 10 * 2000; // 12000 = 4000 from both star powers + 2000 from both gadgets + 6000 from 6 gears. 25 * 1500 = 25 epic gears from 25 brawlers. 10 * 2000 = 10 mythic gears from 10 brawlers.
        int MaxCoins2 = (MaxPowerCoins + 8000) * brawlercount; //8000 = 4000 from both star powers + 2000 from both gadgets + 2000 from two gears
        int MaxCoins3 = (MaxPowerCoins + 5000) * brawlercount; //8000 = 2000 from star power + 1000 from gadget + 2000 from two gears
        int MaxCoins4 = MaxPowerCoins * brawlercount;
        int MaxPowerPoints = brawlercount * MaxPowerPowerPoints;
        if(tag.startsWith("#")){
            tag=tag.substring(1);
        }
        URL url = new URL("https://api.brawlstars.com/v1/players/%23"+tag);
        HttpURLConnection con;
        con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setDoOutput(true);
        con.setRequestProperty("JSON", "application/json");
        con.setRequestProperty("Authorization", "Bearer "+AuthToken);
        int response = con.getResponseCode();
        if(response == 200) {
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            int PowerPoints = 0;
            int Coins1 = 0;
            int Coins2 = 0;
            int Coins3 = 0;
            int Coins4 = 0;
            JSONObject json = (JSONObject) parser.parse(String.valueOf(content));
            String name = String.valueOf(json.get("name"));
            JSONArray brawlers = (JSONArray) json.get("brawlers");
            for (Object brawlerUnprocessed : brawlers) {
                JSONObject brawler = (JSONObject) brawlerUnprocessed;
                JSONArray starPowers = (JSONArray) brawler.get("starPowers");
                JSONArray gadgets = (JSONArray) brawler.get("gadgets");
                JSONArray gears = (JSONArray) brawler.get("gears");
                int power = Integer.parseInt(String.valueOf(brawler.get("power")));
                power--;
                PowerPoints += UpgradesPowerPoints[power];
                int Coins = 0;
                int PowerCost = UpgradesCoins[power];
                Coins += PowerCost;
                Coins4 += Coins;
                Coins3 += Coins;
                int oneStarPowerCost = Math.min(starPowers.size(), 1) * 2000;
                int oneGadgetCost = Math.min(gadgets.size(), 1) * 1000;
                Coins3 += oneStarPowerCost;
                Coins3 += oneGadgetCost;
                int StarPowerCost = starPowers.size() * 2000;
                Coins += StarPowerCost;
                int GadgetCost = gadgets.size() * 1000;
                Coins += GadgetCost;
                Coins1 += Coins;
                Coins2 += Coins;
                int twoGears = Math.min(gears.size(), 2);
                int twoGearsCost = twoGears * 1000;
                Coins2 += twoGearsCost;
                Coins3 += twoGearsCost;
                int gearCost = 0;
                for (Object gearUnprocessed : gears) {
                    JSONObject gear = (JSONObject) gearUnprocessed;
                    Integer id = Integer.parseInt(String.valueOf(gear.get("id")));
                    int cost = 2000;
                    if (Arrays.asList(GearsRare).contains(id)) {
                        cost = 1000;
                    } else if (Arrays.asList(GearsEpic).contains(id)) {
                        cost = 1500;
                    }
                    gearCost += cost;
                }
                Coins1 += gearCost;
            }
            float Coins1Percent = (float) Coins1 / MaxCoins1;
            float Coins2Percent = (float) Coins2 / MaxCoins2;
            float Coins3Percent = (float) Coins3 / MaxCoins3;
            float Coins4Percent = (float) Coins4 / MaxCoins4;
            float PowerPointPercent = (float) PowerPoints / MaxPowerPoints;
            Coins1Percent = (float) Math.round(Coins1Percent * 100000) / 1000;
            Coins2Percent = (float) Math.round(Coins2Percent * 100000) / 1000;
            Coins3Percent = (float) Math.round(Coins3Percent * 100000) / 1000;
            Coins4Percent = (float) Math.round(Coins4Percent * 100000) / 1000;
            PowerPointPercent = (float) Math.round(PowerPointPercent * 100000) / 1000;
            toReturn.append(name).append("'s stats:\n");
            toReturn.append(PowerPoints).append(" / ").append(MaxPowerPoints).append(" power points. ").append(PowerPointPercent).append("% maxed out. ").append(MaxPowerPoints - PowerPoints).append(" power points left.\n");
            toReturn.append(Coins4).append(" / ").append(MaxCoins4).append(" coins. ").append(Coins4Percent).append("% all power 11. ").append(MaxCoins4 - Coins4).append(" coins left.\n");
            toReturn.append(Coins3).append(" / ").append(MaxCoins3).append(" coins. ").append(Coins3Percent).append("% all power 11 + 2 gears, a gadget and a star power. ").append(MaxCoins3 - Coins3).append(" coins left.\n");
            toReturn.append(Coins2).append(" / ").append(MaxCoins2).append(" coins. ").append(Coins2Percent).append("% all power 11 + 2 star powers, gears and gadgets. ").append(MaxCoins2 - Coins2).append(" coins left.\n");
            toReturn.append(Coins1).append(" / ").append(MaxCoins1).append(" coins. ").append(Coins1Percent).append("% fully maxed out. ").append(MaxCoins1 - Coins1).append(" coins left.");
        }else if (response == 404){
            toReturn.append("Player not found!");
        }else{
            toReturn.append("Error ").append(response).append("! Report to the developer!");
        }
        return String.valueOf(toReturn);
    }
}
