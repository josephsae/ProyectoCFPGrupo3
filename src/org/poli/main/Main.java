package org.poli.main;

import java.io.*;
import java.util.*;

public class Main {

	private static final String DATA_DIR = "data/";
	private static final String VENDORS_FILE = DATA_DIR + "salesmen.csv";
	private static final String PRODUCTS_FILE = DATA_DIR + "products.csv";
	private static final String OUTPUT_FILE = DATA_DIR + "sales_report.csv";

	public static void main(String[] args) {
		try {
			generateSalesReport();
			System.out.println("✅ Reporte de ventas generado exitosamente en: " + OUTPUT_FILE);
		} catch (Exception e) {
			System.err.println("❌ Error al generar el reporte: " + e.getMessage());
		}
	}

	private static void generateSalesReport() throws IOException {
		Map<String, Integer> productPrices = loadProductPrices();
		List<SalesEntry> salesEntries = new ArrayList<>();

		List<String> vendors = readFile(VENDORS_FILE);
		for (String line : vendors) {
			String[] parts = line.split(";");
			if (parts.length < 3) continue;

			String id = parts[1];
			String fullName = parts[2];

			int total = calculateTotalSalesForVendor(id, productPrices);
			salesEntries.add(new SalesEntry(fullName, total));
		}

		// Ordenar de mayor a menor por total recaudado
		salesEntries.sort((a, b) -> Integer.compare(b.total, a.total));

		// Escribir archivo CSV de reporte
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(OUTPUT_FILE))) {
			for (SalesEntry entry : salesEntries) {
				writer.write(entry.name + ";" + entry.total + "\n");
			}
		}
	}

	private static Map<String, Integer> loadProductPrices() throws IOException {
		Map<String, Integer> prices = new HashMap<>();
		List<String> lines = readFile(PRODUCTS_FILE);
		for (String line : lines) {
			String[] parts = line.split(";");
			if (parts.length >= 3) {
				prices.put(parts[0], Integer.parseInt(parts[2]));
			}
		}
		return prices;
	}

	private static int calculateTotalSalesForVendor(String id, Map<String, Integer> productPrices) {
		String salesFile = DATA_DIR + "sales_" + id + ".csv";
		List<String> lines = readFile(salesFile);
		int total = 0;

		boolean readingProducts = false;

		for (String line : lines) {
			if (line.startsWith("ProductID")) {
				readingProducts = true;
				continue;
			}
			if (!readingProducts) continue;

			String[] parts = line.split(";");
			if (parts.length >= 2) {
				String productId = parts[0];
				int quantity = Integer.parseInt(parts[1]);
				Integer price = productPrices.get(productId);
				if (price != null) {
					total += price * quantity;
				}
			}
		}
		return total;
	}

	private static List<String> readFile(String path) {
		List<String> result = new ArrayList<>();
		try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
			String line;
			while ((line = reader.readLine()) != null) {
				result.add(line);
			}
		} catch (IOException e) {
			System.err.println("⚠️ No se pudo leer el archivo: " + path);
		}
		return result;
	}

	private static class SalesEntry {
		String name;
		int total;

		public SalesEntry(String name, int total) {
			this.name = name;
			this.total = total;
		}
	}
}
