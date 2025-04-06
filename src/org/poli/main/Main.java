package org.poli.main;

import java.io.*;
import java.util.*;

import org.poli.generatefiles.Product;
import org.poli.generatefiles.Salesman;

public class Main {

	private static final String DATA_DIR = "data/";
	private static final String VENDORS_SER = DATA_DIR + "salesmen.ser";
	private static final String PRODUCTS_SER = DATA_DIR + "products.ser";
	private static final String OUTPUT_FILE = DATA_DIR + "sales_report.csv";

	public static void main(String[] args) {
		try {
			generateSalesReport();
			System.out.println("✅ Reporte de ventas generado exitosamente en: " + OUTPUT_FILE);
		} catch (Exception e) {
			System.err.println("❌ Error al generar el reporte: " + e.getMessage());
		}
	}

	private static void generateSalesReport() throws IOException, ClassNotFoundException {
		Map<String, Integer> productPrices = loadProductPrices();
		List<Salesman> salesmen = loadSalesmen();
		List<SalesEntry> salesEntries = new ArrayList<>();

		for (Salesman s : salesmen) {
			String id = String.valueOf(s.getId());
			String fullName = s.getName();
			int total = calculateTotalSalesForVendor(id, productPrices);
			salesEntries.add(new SalesEntry(fullName, total));
		}

		salesEntries.sort((a, b) -> Integer.compare(b.total, a.total));

		try (BufferedWriter writer = new BufferedWriter(new FileWriter(OUTPUT_FILE))) {
			for (SalesEntry entry : salesEntries) {
				writer.write(entry.name + ";" + entry.total + "\n");
			}
		}
	}

	/**
	 * Carga los precios de los productos desde el archivo serializado.
	 */
	private static Map<String, Integer> loadProductPrices() throws IOException, ClassNotFoundException {
		Map<String, Integer> prices = new HashMap<>();
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(PRODUCTS_SER))) {
			@SuppressWarnings("unchecked")
			List<Product> products = (List<Product>) ois.readObject();
			for (Product p : products) {
				prices.put(p.getId(), p.getPrice());
			}
		}
		return prices;
	}

	private static List<Salesman> loadSalesmen() throws IOException, ClassNotFoundException {
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(VENDORS_SER))) {
			@SuppressWarnings("unchecked")
			List<Salesman> salespeople = (List<Salesman>) ois.readObject();
			return salespeople;
		}
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
