package com.stockwatch;

import java.util.Scanner;

/**
 * Entry point for the Stock Market Analysis CLI app.
 */
public class MainApp {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        boolean keepRunning = true;

        while (keepRunning) {

            System.out.println("📈 Welcome to StockWatch CLI");
            System.out.println("----------------------------");
            System.out.println("1. Fetch Stock Data");
            System.out.println("2. Add Stock to Watchlist");
            System.out.println("3. Delete Stock from Watchlist");
            System.out.println("4. View Watchlist");
            System.out.println("5. Exit");
            System.out.print("Choose an option (1-5): ");

            int choice = -1;
            try {
                choice = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid input. Please enter a number between 1 and 5.");
                return;
            }

            switch (choice) {
                case 1:
                    System.out.print("🔎 Enter stock symbol (e.g., RELIANCE.BSE): ");
                    String symbol = scanner.nextLine().trim().toUpperCase();
                    StockFetcher.getStockData(symbol);
                    break;

                case 2:
                    System.out.print("➕ Enter stock symbol to add to watchlist: ");
                    String symbolToAdd = scanner.nextLine().trim().toUpperCase();
                    WatchlistManager.addToWatchlist(symbolToAdd);
                    break;

                case 3:
                    System.out.print("❌ Enter stock symbol to delete from watchlist: ");
                    String symbolToDelete = scanner.nextLine().trim().toUpperCase();
                    WatchlistManager.deleteFromWatchlist(symbolToDelete);
                    break;

                case 4:
                    WatchlistManager.viewWatchlist();
                    break;

                case 5:
                    System.out.println("👋 Exiting StockWatch. Have a great day!");
                    keepRunning = false;
                    break;

                default:
                    System.out.println("⚠️ Invalid choice! Please select a valid option.");
            }
        }
    }
}