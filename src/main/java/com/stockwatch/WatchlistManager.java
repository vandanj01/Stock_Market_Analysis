package com.stockwatch;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.*;

/**
 * Manages all database operations related to the user's stock watchlist.
 */
public class WatchlistManager {

    // Database credentials
    private static final String DB_URL = "jdbc:mysql://localhost:3306/stock_watchlist";
    private static final String USER = "root";
    private static final String PASS = "wUbnex-hismen-supfu9"; // Replace with your password

    private static final String API_KEY = "QZXDIFMCPU7TBE6D"; // Replace with your Alpha Vantage API key
    private static final String BASE_URL = "https://www.alphavantage.co/query";

    /**
     * Adds a stock symbol to the watchlist table.
     *
     * @param symbol Stock symbol to add
     */

    public static boolean validateSymbol(String symbol) {
        String url = BASE_URL + "?function=TIME_SERIES_DAILY&symbol=" + symbol + "&apikey=" + API_KEY;

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(url);
            try (CloseableHttpResponse response = client.execute(request)) {
                String jsonResponse = EntityUtils.toString(response.getEntity());

                JSONObject json = new JSONObject(jsonResponse);
                // If the API returns "Time Series (Daily)", we have valid data
                return json.has("Time Series (Daily)");
            }
        } catch (IOException e) {
            System.out.println("Error checking symbol: " + e.getMessage());
        }
        return false;
    }

    public static void addToWatchlist(String symbol) {
        if (!validateSymbol(symbol)) {
            System.out.println("❌ Invalid symbol! Please enter a valid stock symbol.");
            return;
        }

        String query = "INSERT INTO watchlist (symbol) VALUES (?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, symbol);
            stmt.executeUpdate();
            System.out.println("✅ Stock symbol '" + symbol + "' added to your watchlist.");

        } catch (SQLException e) {
            System.out.println("❌ Error adding to watchlist: " + e.getMessage());
        }
    }

    /**
     * Deletes a stock symbol from the watchlist table.
     *
     * @param symbol Stock symbol to delete
     */
    public static void deleteFromWatchlist(String symbol) {
        String query = "DELETE FROM watchlist WHERE symbol = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, symbol);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("✅ '" + symbol + "' removed from your watchlist.");
            } else {
                System.out.println("⚠️ Symbol '" + symbol + "' not found in your watchlist.");
            }

        } catch (SQLException e) {
            System.out.println("❌ Error deleting from watchlist: " + e.getMessage());
        }
    }

    /**
     * Displays all stock symbols saved in the watchlist.
     */
    public static void viewWatchlist() {
        String query = "SELECT * FROM watchlist";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            System.out.println("\n📋 Your Watchlist:");
            boolean isEmpty = true;
            while (rs.next()) {
                isEmpty = false;
                System.out.println(rs.getInt("id") + ". " + rs.getString("symbol"));
            }

            if (isEmpty) {
                System.out.println("🔍 Your watchlist is currently empty.");
            }

        } catch (SQLException e) {
            System.out.println("❌ Error retrieving watchlist: " + e.getMessage());
        }
    }
}