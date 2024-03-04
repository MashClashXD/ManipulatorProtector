package mashclash.scamprotector.main;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class HypixelAPIRequest {

    private static final String API_KEY = "API-KEY"; // Replace with your actual API key
    private static Map<String, Double> cachedPrices = new HashMap<String, Double>();
    private static String cachedBazaarJSON = null;
    private static String cachedItemsJSON = null;

    static {
        // Initialize the cached JSON data when the class is loaded
        updateCachedBazaarJSON();
        updateCachedItemsJSON();
        // Start a separate thread to update the cached JSON data every 5 minutes
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(30 * 60 * 1000); // Sleep for 5 minutes
                        updateCachedBazaarJSON();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private static void updateCachedBazaarJSON() {
        try {
            // Create URL object with the Bazaar API endpoint
            URL url = new URL("https://api.hypixel.net/v2/skyblock/bazaar");

            // Create HttpURLConnection object
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set request method
            connection.setRequestMethod("GET");

            // Set request header with API key
            connection.setRequestProperty("API-Key", API_KEY);

            // Get the response code
            int responseCode = connection.getResponseCode();
            System.out.println("Bazaar Response Code: " + responseCode);

            // Read the response from the server
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = reader.readLine()) != null) {
                response.append(inputLine);
            }
            reader.close();

            // Close the connection
            connection.disconnect();

            // Update the cached Bazaar JSON data
            cachedBazaarJSON = response.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void updateCachedItemsJSON() {
        try {
            // Create URL object with the Items API endpoint
            URL url = new URL("https://api.hypixel.net/v2/resources/skyblock/items");

            // Create HttpURLConnection object
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set request method
            connection.setRequestMethod("GET");

            // Set request header with API key
            connection.setRequestProperty("API-Key", API_KEY);

            // Get the response code
            int responseCode = connection.getResponseCode();
            System.out.println("Items Response Code: " + responseCode);

            // Read the response from the server
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = reader.readLine()) != null) {
                response.append(inputLine);
            }
            reader.close();

            // Close the connection
            connection.disconnect();

            // Update the cached Items JSON data
            cachedItemsJSON = response.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static double getPriceForItem(String itemName, double price) {
        try {
            // Check if cachedBazaarJSON is null or empty, if so, update it
            if (cachedBazaarJSON == null || cachedBazaarJSON.isEmpty()) {
                updateCachedBazaarJSON();
            }

            // Parse JSON response using Gson
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(cachedBazaarJSON, JsonObject.class);

            // Check if the item exists in the response
            if (jsonObject.has("products") && jsonObject.getAsJsonObject("products").has(itemName)) {
                JsonObject itemData = jsonObject.getAsJsonObject("products").getAsJsonObject(itemName);
                JsonArray sellSummary = itemData.getAsJsonArray("sell_summary");
                if (sellSummary.size() > 0) {
                    // Assuming the first entry in the sell_summary array is the latest
                    JsonObject latestSell = sellSummary.get(0).getAsJsonObject();
                    return latestSell.get("pricePerUnit").getAsDouble();
                } else {
                    System.out.println("No sell summary available for the item: " + itemName);
                    return price; // or throw an exception
                }
            } else {
                System.out.println("Item not found in response: " + itemName);
                return price; // or throw an exception
            }

        } catch (Exception e) {
            e.printStackTrace();
            return price; // or throw an exception
        }
    }

    public static String getItemId(String itemName) {
        try {
            // Check if cachedItemsJSON is null or empty, if so, update it
            if (cachedItemsJSON == null || cachedItemsJSON.isEmpty()) {
                updateCachedItemsJSON();
            }

            // Parse JSON response using Gson
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(cachedItemsJSON, JsonObject.class);

            // Look for the item ID using the provided item name
            if (jsonObject.has("items")) {
                JsonArray itemsArray = jsonObject.getAsJsonArray("items");
                for (int i = 0; i < itemsArray.size(); i++) {
                    JsonObject item = itemsArray.get(i).getAsJsonObject();
                    if (item.has("name") && item.get("name").getAsString().equalsIgnoreCase(itemName)) {
                        return item.get("id").getAsString();
                    }
                }
            }

            // If item ID not found, return null
            return null;

        } catch (Exception e) {
            e.printStackTrace();
            return null; // or throw an exception
        }
    }
    public static double getMeanPriceForItem(String itemName) {
        // Check if the item's mean price is already cached
        if (cachedPrices.containsKey(itemName)) {
            return cachedPrices.get(itemName);
        }

        try {
            // Create URL object with the API endpoint
            URL url = new URL("https://sky.coflnet.com/api/item/price/" + itemName);

            // Create HttpURLConnection object
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set request method
            connection.setRequestMethod("GET");

            connection.setRequestProperty("User-Agent", "ScamProc/1.0");

            // Get the response code
            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            // Read the response from the server
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = reader.readLine()) != null) {
                response.append(inputLine);
            }
            reader.close();

            // Close the connection
            connection.disconnect();

            // Parse JSON response using Gson
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(response.toString(), JsonObject.class);

            // Extract and cache the mean value from the JSON response
            double meanPrice = jsonObject.get("mean").getAsDouble();
            cachedPrices.put(itemName, meanPrice);

            return meanPrice;
        } catch (IOException e) {
            e.printStackTrace();
            return -1; // or throw an exception
        }
    }
}
