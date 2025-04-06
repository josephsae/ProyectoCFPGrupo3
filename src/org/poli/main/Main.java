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
			generateSortedProductSales();
		} catch (Exception e) {
			System.err.println("❌ Error al generar los reportes: " + e.getMessage());
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
	
    public static void generateSortedProductSales() {
        Map<String, Integer> productSales = new HashMap<>();
        List<String> lines = readFile(DATA_DIR + "salesmen.csv");

       
        for (String line : lines) {
            String[] parts = line.split(";");
            if (parts.length >= 2) {
                String id = parts[1];
                String salesFile = DATA_DIR + "sales_" + id + ".csv";
                File file = new File(salesFile);
           
                 if (!file.exists()) {
                     System.err.println("⚠️ Archivo no encontrado: " + salesFile);
                     continue;
                    }
                 List<String> salesLines = readFile(salesFile);
                 
                 for (int i = 3; i < salesLines.size(); i++) {
                     String[] saleParts = salesLines.get(i).split(";");
                     if (saleParts.length >= 2) {
                         String productId = saleParts[0];
                         int quantity = Integer.parseInt(saleParts[1]);
                         productSales.put(productId, productSales.getOrDefault(productId, 0) + quantity);   
                     }
               }
           }
       }
       List<Map.Entry<String, Integer>> sortedSales = new ArrayList<>(productSales.entrySet());
       sortedSales.sort((a, b) -> b.getValue().compareTo(a.getValue()));
       
       try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_DIR + "products_sold.csv"))) {
           writer.write("ProductID;TotalQuantity\n");
           for (Map.Entry<String, Integer> entry : sortedSales) {
                writer.write(entry.getKey() + ";" + entry.getValue() + "\n");
           }
           System.out.println("✅ Archivo 'products_sold.csv' generado exitosamente.");
       }catch (IOException e) {
           System.err.println("❌ Error escribiendo el archivo de productos vendidos: " + e.getMessage());
       }
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
