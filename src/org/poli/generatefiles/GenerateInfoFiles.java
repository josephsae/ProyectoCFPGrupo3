package org.poli.generatefiles;

import java.io.*;
import java.util.*;

public class GenerateInfoFiles {

	private static final String DATA_DIR = "data/";
	private static final String VENDORS_FILE = DATA_DIR + "salesmen.csv";
	private static final String PRODUCTS_FILE = DATA_DIR + "products.csv";
	private static final String[] FIRST_NAMES = { "Carlos", "Maria", "Luis", "Andrea", "Pedro", "Sofia", "Alejandra",
			"Jefferson", "Lorena", "Paola", "Diego", "Lucia", "Leidy", "Camila", "Juan", "Vanesa", "Clara" };
	private static final String[] LAST_NAMES = { "Gomez", "Rodriguez", "Lopez", "Fernandez", "Martinez", "Fernandez",
			"Martinez", "Torres", "Mendoza", "Jimenez", "Vargas", "Rios", "Coronado", "Roa", "Betancur" };
	private static final Random RANDOM = new Random();
	private static final String[] PRODUCT_NAMES = { "Laptop", "Mouse", "Teclado", "Monitor", "Impresora", "Celular",
			"Tablet", "Auriculares", "GPS", "Televisor", "Control remoto", "Camara de seguridad" };
	private static final int PRODUCT_COUNT = 10;
	private static final int SALESMAN_COUNT = 5;

	public static void main(String[] args) {
		try {
			createDirectory(DATA_DIR);
			createProductsFile(PRODUCT_COUNT);
			List<Long> salesmanIds = createSalesManInfoFile(SALESMAN_COUNT);
			for (long id : salesmanIds) {
				createSalesMenFile(5, id);
			}
			System.out.println("Files generated successfully.");
		} catch (IOException e) {
			System.err.println("Error generating files: " + e.getMessage());
		}
	}

	private static void createDirectory(String dirPath) {
		File directory = new File(dirPath);
		if (!directory.exists()) {
			if (directory.mkdirs()) {
				System.out.println("Directory created: " + dirPath);
			} else {
				System.err.println("Failed to create directory: " + dirPath);
			}
		}
	}

	public static void createSalesMenFile(int randomSalesCount, long id) throws IOException {
		String filename = DATA_DIR + "sales_" + id + ".csv";

		try (BufferedWriter writer = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(filename), "UTF-8"))) {
			writer.write("TipoDocumentoVendedor;IDVendedor\n");
			writer.write("CC;" + id + "\n");
			writer.write("ProductID;Quantity\n");

			Set<Integer> usedProductIds = new HashSet<>();

			while (usedProductIds.size() < randomSalesCount) {
				int productId = RANDOM.nextInt(PRODUCT_COUNT) + 1;
				if (usedProductIds.add(productId)) {
					int quantity = RANDOM.nextInt(5) + 1;
					writer.write(String.format("P%03d;%d\n", productId, quantity));
				}
			}
		}
	}

	public static List<Long> createSalesManInfoFile(int salesmanCount) throws IOException {
		Set<Long> ids = new HashSet<>();
		List<Long> idList = new ArrayList<>();

		try (BufferedWriter writer = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(VENDORS_FILE), "UTF-8"))) {
			for (int i = 0; i < salesmanCount; i++) {
				long id;
				do {
					id = generateRandomID();
				} while (ids.contains(id));
				ids.add(id);
				idList.add(id);

				String firstName = FIRST_NAMES[RANDOM.nextInt(FIRST_NAMES.length)];
				String lastName = LAST_NAMES[RANDOM.nextInt(LAST_NAMES.length)];
				writer.write("CC;" + id + ";" + firstName + " " + lastName + "\n");
			}
		}

		return idList;
	}

	public static void createProductsFile(int productCount) throws IOException {
		try (BufferedWriter writer = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(PRODUCTS_FILE), "UTF-8"))) {

			Set<String> usedProductNames = new HashSet<>();

			for (int i = 1; i <= productCount; i++) {
				String id = String.format("P%03d", i);

				String name;
				do {
					name = PRODUCT_NAMES[RANDOM.nextInt(PRODUCT_NAMES.length)];
				} while (usedProductNames.contains(name));

				usedProductNames.add(name);

				int price = (RANDOM.nextInt(50) + 1) * 1000;
				writer.write(id + ";" + name + ";" + price + "\n");
			}
		}
	}

	// Generates a random ID (C.C.) between 8 and 10 digits
	public static long generateRandomID() {
		int digits = 8 + RANDOM.nextInt(3);
		long min = (long) Math.pow(10, digits - 1);
		long max = (long) Math.pow(10, digits) - 1;
		return min + (long) (RANDOM.nextDouble() * (max - min));
	}
}
