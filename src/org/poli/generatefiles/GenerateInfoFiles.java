package org.poli.generatefiles;

import java.io.*;
import java.util.*;

/**
 * Genera archivos CSV con información simulada de productos, vendedores y ventas.
 * 
 * <p>
 * Ejemplo de uso:
 * 
 * <pre>{@code
 * - Archivos generados en 'data/':
 *   1. products.csv     (Productos con ID, nombre y precio)
 *   2. salesmen.csv     (Vendedores con documento y nombres)
 *   3. sales_[ID].csv   (Ventas por vendedor)
 *   4. products.ser     (Lista serializada de objetos Producto)
 *   5. salesmen.ser     (Lista serializada de objetos Vendedor)
 * }</pre>
 * 
 * @author Poli
 * @version 1.1
 */
public class GenerateInfoFiles {

	private static final String DATA_DIR = "data/";
	private static final String SALESPEOPLE_FILE = DATA_DIR + "salesmen.csv";
	private static final String PRODUCTS_FILE = DATA_DIR + "products.csv";
	private static final String PRODUCTS_SER = DATA_DIR + "products.ser";
	private static final String SALESPEOPLE_SER = DATA_DIR + "salesmen.ser";

	private static final String[] FIRST_NAMES = { "Carlos", "Maria", "Luis", "Andrea", "Pedro", "Sofia", "Alejandra",
			"Jefferson", "Lorena", "Paola", "Diego", "Lucia", "Leidy", "Camila", "Juan", "Vanesa", "Clara" };
	private static final String[] LAST_NAMES = { "Gomez", "Rodriguez", "Lopez", "Fernandez", "Martinez", "Fernandez",
			"Martinez", "Torres", "Mendoza", "Jimenez", "Vargas", "Rios", "Coronado", "Roa", "Betancur" };
	private static final String[] PRODUCT_NAMES = { "Laptop", "Mouse", "Teclado", "Monitor", "Impresora", "Celular",
			"Tablet", "Auriculares", "GPS", "Televisor", "Control remoto", "Cámara de seguridad" };

	private static final Random RANDOM = new Random();
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
			System.out.println("Archivos generados y serializados correctamente.");
			readSerializedData();
		} catch (IOException e) {
			System.err.println("Error al generar archivos: " + e.getMessage());
		}
	}

	/**
	 * Crea un directorio si no existe.
	 * 
	 * @param dirPath Ruta del directorio a crear
	 */
	private static void createDirectory(String dirPath) {
		File directory = new File(dirPath);
		if (!directory.exists()) {
			directory.mkdirs();
		}
	}

	/**
	 * Genera un archivo de ventas para un vendedor específico.
	 * 
	 * @param randomSalesCount Cantidad de productos únicos en el reporte
	 * @param id               Identificación única del vendedor
	 * @throws IOException Si ocurre un error de escritura
	 */
	public static void createSalesFileForSalesman(int randomSalesCount, long id) throws IOException {
		String filename = DATA_DIR + "sales_" + id + ".csv";
		int total = 0;

		try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
			writer.write("TipoDocumentoVendedor;IDVendedor\n");
			writer.write("CC;" + id + "\n");
			writer.write("ProductID;Cantidad;Total\n");

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

	/**
	 * Genera el archivo con información de vendedores.
	 * 
	 * @param count Número de vendedores a generar
	 * @return Lista de IDs únicos generados
	 * @throws IOException Si ocurre un error de escritura
	 */
	public static List<Long> createSalesmenFile(int count) throws IOException {
		Set<Long> ids = new HashSet<>();
		List<Long> idList = new ArrayList<>();

		try (BufferedWriter writer = new BufferedWriter(new FileWriter(SALESPEOPLE_FILE))) {
			for (int i = 0; i < count; i++) {
				long id;
				do {
					id = generateRandomID();
				} while (ids.contains(id));
				ids.add(id);
				idList.add(id);

				String name = FIRST_NAMES[RANDOM.nextInt(FIRST_NAMES.length)] + " "
						+ LAST_NAMES[RANDOM.nextInt(LAST_NAMES.length)];
				writer.write("CC;" + id + ";" + name + "\n");
			}
		}

		return idList;
	}

	/**
	 * Genera el archivo con información de productos.
	 * 
	 * @param count Número de productos a generar
	 * @throws IOException Si ocurre un error de escritura
	 */
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

	/**
	 * Genera un número de identificación aleatorio válido.
	 * 
	 * @return Número de 8 a 10 dígitos simulando una cédula colombiana
	 */
	public static long generateRandomID() {
		int digits = 8 + RANDOM.nextInt(3);
		long min = (long) Math.pow(10, digits - 1);
		long max = (long) Math.pow(10, digits) - 1;
		return min + (long) (RANDOM.nextDouble() * (max - min));
	}

	/**
	 * Lee el contenido de un archivo y lo retorna como lista de líneas.
	 * 
	 * @param path Ruta del archivo
	 * @return Lista de líneas
	 */
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

	/**
	 * Busca el precio de un producto por su ID.
	 * 
	 * @param id ID del producto
	 * @return Precio del producto
	 */
	public static int getProductPrice(String id) {
		for (String line : readFile(PRODUCTS_FILE)) {
			String[] parts = line.split(";");
			if (parts[0].equalsIgnoreCase(id)) {
				return Integer.parseInt(parts[2]);
			}
		}
		return 0;
	}

	/**
	 * Reorganiza los vendedores de acuerdo a sus ventas totales.
	 */
	public static void sortSalesmenBySales() {
		ArrayList<String> data = readFile(SALESPEOPLE_FILE);
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

		try (BufferedWriter writer = new BufferedWriter(new FileWriter(SALESPEOPLE_FILE))) {
			for (String line : sorted) {
				writer.write(line + ";\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Serializa los datos de Product y Salesman a archivos .ser.
	 */
	public static void serializeData() {
		List<Product> products = new ArrayList<>();
		for (String line : readFile(PRODUCTS_FILE)) {
			String[] parts = line.split(";");
			products.add(new Product(parts[0], parts[1], Integer.parseInt(parts[2])));
		}
		serializeObject(products, PRODUCTS_SER);

		List<Salesman> salesmen = new ArrayList<>();
		for (String line : readFile(SALESPEOPLE_FILE)) {
			String[] parts = line.split(";");
			if (parts.length >= 3)
				salesmen.add(new Salesman(parts[0], Long.parseLong(parts[1]), parts[2]));
		}
		serializeObject(salesmen, SALESPEOPLE_SER);
	}

	/**
	 * Serializa un objeto en la ruta indicada.
	 * 
	 * @param obj  Objeto a serializar
	 * @param path Ruta de salida
	 */
	public static void serializeObject(Object obj, String path) {
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path))) {
			oos.writeObject(obj);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Lee e imprime los datos serializados de Product y Salesman.
	 */
	@SuppressWarnings("unchecked")
	public static void readSerializedData() {
		System.out.println("\n📦 Productos serializados:");
		List<Product> products = (List<Product>) deserializeObject(PRODUCTS_SER);
		if (products != null) products.forEach(System.out::println);

		System.out.println("\n🧑‍💼 Vendedores serializados:");
		List<Salesman> salesmen = (List<Salesman>) deserializeObject(SALESPEOPLE_SER);
		if (salesmen != null) salesmen.forEach(System.out::println);
	}


	/**
	 * Deserializa un archivo .ser y retorna el objeto.
	 * 
	 * @param path Ruta del archivo
	 * @return Objeto deserializado
	 */
	public static Object deserializeObject(String path) {
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path))) {
			return ois.readObject();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
}
