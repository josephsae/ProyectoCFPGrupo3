package org.poli.generatefiles;

import java.io.*;
import java.util.*;

public class GenerateInfoFiles {

	private static final String DATA_DIR = "data/";
	private static final String SALESMEN_FILE = DATA_DIR + "salesmen.csv";
	private static final String PRODUCTS_FILE = DATA_DIR + "products.csv";
	private static final String SALESMEN_SER_FILE = DATA_DIR + "salesmen.ser";
	private static final String PRODUCTS_SER_FILE = DATA_DIR + "products.ser";

	private static final String[] FIRST_NAMES = { "Carlos", "Maria", "Luis", "Andrea", "Pedro", "Sofia", "Alejandra",
			"Jefferson", "Lorena", "Paola", "Diego", "Lucia", "Leidy", "Camila", "Juan", "Vanesa", "Clara" };
	private static final String[] LAST_NAMES = { "Gomez", "Rodriguez", "Lopez", "Fernandez", "Martinez", "Fernandez",
			"Martinez", "Torres", "Mendoza", "Jimenez", "Vargas", "Rios", "Coronado", "Roa", "Betancur" };
	private static final Random RANDOM = new Random();
	private static final String[] PRODUCT_NAMES = { "Laptop", "Mouse", "Keyboard", "Monitor", "Printer", "Phone",
			"Tablet", "Headphones", "GPS", "TV", "Remote", "Security Camera" };
	private static final int PRODUCT_COUNT = 11;
	private static final int SALESMAN_COUNT = 5;

	public static void main(String[] args) {
		try {
			createDirectory(DATA_DIR);
			createProductsFile(PRODUCT_COUNT);
			List<Long> salesmanIds = createSalesmenFile(SALESMAN_COUNT);
			for (long id : salesmanIds) {
				createSalesFileForSalesman(SALESMAN_COUNT, id);
			}
			sortSalesmenBySales();
			serializeData();
			System.out.println("CSV files and serialization completed successfully.");
			readSerializedData(); // Display serialized objects
		} catch (IOException e) {
			System.err.println("Error while generating files: " + e.getMessage());
		}
	}

	private static void createDirectory(String dirPath) {
		File directory = new File(dirPath);
		if (!directory.exists()) {
			directory.mkdirs();
		}
	}

	public static void createSalesFileForSalesman(int randomSalesCount, long id) throws IOException {
		String filename = DATA_DIR + "sales_" + id + ".csv";
		int total = 0;

		try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
			writer.write("DocumentType;SalesmanID\n");
			writer.write("ID;" + id + "\n");
			writer.write("ProductID;Quantity;Total\n");

			Set<Integer> usedProductIds = new HashSet<>();

			while (usedProductIds.size() < randomSalesCount) {
				int productId = RANDOM.nextInt(PRODUCT_COUNT) + 1;
				if (usedProductIds.add(productId)) {
					int quantity = RANDOM.nextInt(5) + 1;
					int price = getProductPrice(String.format("P%03d", productId));
					int subtotal = price * quantity;
					total += subtotal;
					writer.write(String.format("P%03d;%d;%d\n", productId, quantity, subtotal));
				}
			}
			writer.write("Total;" + total);
		}
	}

	public static List<Long> createSalesmenFile(int count) throws IOException {
		Set<Long> ids = new HashSet<>();
		List<Long> idList = new ArrayList<>();

		try (BufferedWriter writer = new BufferedWriter(new FileWriter(SALESMEN_FILE))) {
			for (int i = 0; i < count; i++) {
				long id;
				do {
					id = generateRandomID();
				} while (ids.contains(id));
				ids.add(id);
				idList.add(id);

				String name = FIRST_NAMES[RANDOM.nextInt(FIRST_NAMES.length)] + " "
						+ LAST_NAMES[RANDOM.nextInt(LAST_NAMES.length)];
				writer.write("ID;" + id + ";" + name + "\n");
			}
		}

		return idList;
	}

	public static void createProductsFile(int count) throws IOException {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(PRODUCTS_FILE))) {
			Set<String> usedNames = new HashSet<>();
			for (int i = 1; i <= count; i++) {
				String id = String.format("P%03d", i);
				String name;
				do {
					name = PRODUCT_NAMES[RANDOM.nextInt(PRODUCT_NAMES.length)];
				} while (usedNames.contains(name));
				usedNames.add(name);
				int price = (RANDOM.nextInt(50) + 1) * 1000;
				writer.write(id + ";" + name + ";" + price + "\n");
			}
		}
	}

	public static long generateRandomID() {
		int digits = 8 + RANDOM.nextInt(3);
		long min = (long) Math.pow(10, digits - 1);
		long max = (long) Math.pow(10, digits) - 1;
		return min + (long) (RANDOM.nextDouble() * (max - min));
	}

	public static ArrayList<String> readFile(String path) {
		ArrayList<String> lines = new ArrayList<>();
		try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
			String line;
			while ((line = reader.readLine()) != null) {
				lines.add(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return lines;
	}

	public static int getProductPrice(String id) {
		for (String line : readFile(PRODUCTS_FILE)) {
			String[] parts = line.split(";");
			if (parts[0].equalsIgnoreCase(id)) {
				return Integer.parseInt(parts[2]);
			}
		}
		return 0;
	}

	public static void sortSalesmenBySales() {
		ArrayList<String> data = readFile(SALESMEN_FILE);
		ArrayList<String> sorted = new ArrayList<>();

		for (String line : data) {
			String[] parts = line.split(";");
			String salesFile = DATA_DIR + "sales_" + parts[1] + ".csv";
			ArrayList<String> salesData = readFile(salesFile);
			int total = Integer.parseInt(salesData.get(salesData.size() - 1).split(";")[1]);

			String lineWithTotal = line + ";" + total;
			int index = 0;
			while (index < sorted.size()) {
				int currentTotal = Integer.parseInt(sorted.get(index).split(";")[3]);
				if (total > currentTotal) break;
				index++;
			}
			sorted.add(index, lineWithTotal);
		}

		try (BufferedWriter writer = new BufferedWriter(new FileWriter(SALESMEN_FILE))) {
			for (String line : sorted) {
				writer.write(line + ";\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Serialization

	public static void serializeData() {
		List<Product> products = new ArrayList<>();
		for (String line : readFile(PRODUCTS_FILE)) {
			String[] parts = line.split(";");
			products.add(new Product(parts[0], parts[1], Integer.parseInt(parts[2])));
		}
		serializeObject(products, PRODUCTS_SER_FILE);

		List<Salesman> salesmen = new ArrayList<>();
		for (String line : readFile(SALESMEN_FILE)) {
			String[] parts = line.split(";");
			if (parts.length >= 3)
				salesmen.add(new Salesman(parts[0], Long.parseLong(parts[1]), parts[2]));
		}
		serializeObject(salesmen, SALESMEN_SER_FILE);
	}

	public static void serializeObject(Object obj, String path) {
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path))) {
			oos.writeObject(obj);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public static void readSerializedData() {
		System.out.println("\n📦 Serialized Products:");
		List<Product> products = (List<Product>) deserializeObject(PRODUCTS_SER_FILE);
		if (products != null) products.forEach(System.out::println);

		System.out.println("\n🧑‍💼 Serialized Salesmen:");
		List<Salesman> salesmen = (List<Salesman>) deserializeObject(SALESMEN_SER_FILE);
		if (salesmen != null) salesmen.forEach(System.out::println);
	}

	public static Object deserializeObject(String path) {
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path))) {
			return ois.readObject();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
}
