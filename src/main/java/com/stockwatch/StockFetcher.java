package com.stockwatch;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.json.JSONObject;

import javax.swing.*;
import java.io.IOException;
import java.util.Iterator;

/**
 * This class is responsible for fetching stock data from Alpha Vantage API,
 * calculating SMA, and displaying a 7-day line chart.
 */
public class StockFetcher {

    private static final String API_KEY = "QZXDIFMCPU7TBE6D"; // Replace with your own API key
    private static final String BASE_URL = "https://www.alphavantage.co/query";

    /**
     * Fetches stock data, calculates 7-day SMA, and shows a price chart.
     *
     * @param symbol The stock symbol (e.g., RELIANCE.BSE)
     */
    public static void getStockData(String symbol) {
        String url = BASE_URL + "?function=TIME_SERIES_DAILY&symbol=" + symbol + "&apikey=" + API_KEY;

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(url);
            try (CloseableHttpResponse response = client.execute(request)) {
                String jsonResponse = EntityUtils.toString(response.getEntity());
                JSONObject json = new JSONObject(jsonResponse);

                if (!json.has("Time Series (Daily)")) {
                    System.out.println("❌ No data found for symbol: " + symbol);
                    return;
                }

                JSONObject timeSeries = json.getJSONObject("Time Series (Daily)");
                Iterator<String> keys = timeSeries.keys();
                DefaultCategoryDataset dataset = new DefaultCategoryDataset();

                double sum = 0;
                int counter = 0;
                while (keys.hasNext() && counter < 7) {
                    String date = keys.next();
                    JSONObject dailyData = timeSeries.getJSONObject(date);
                    double closePrice = Double.parseDouble(dailyData.getString("4. close"));

                    dataset.addValue(closePrice, symbol, date);
                    sum += closePrice;
                    counter++;
                }

                double sma = sum / 7;
                System.out.printf("📊 Simple Moving Average (SMA) for last 7 days: ₹%.2f%n", sma);

                showChart(symbol, dataset);

            }
        } catch (IOException e) {
            System.out.println("Error fetching stock data: " + e.getMessage());
        }
    }

    /**
     * Displays a line chart using JFreeChart.
     *
     * @param symbol  Stock symbol for title
     * @param dataset Data for plotting
     */
    private static void showChart(String symbol, DefaultCategoryDataset dataset) {
        JFreeChart chart = ChartFactory.createLineChart(
                "Stock Price (Last 7 Days) - " + symbol,
                "Date",
                "Price (₹)",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        JFrame chartFrame = new JFrame("📈 Price Chart");
        chartFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        chartFrame.add(new ChartPanel(chart));
        chartFrame.pack();
        chartFrame.setLocationRelativeTo(null); // Center on screen
        chartFrame.setVisible(true);
    }
}