package org.poli.generatefiles;

import java.io.*;
import java.util.*;

/**
 * Genera archivos CSV y archivos serializados (.ser) con información simulada
 * de productos, vendedores y ventas.
 *
 * <p>
 * Ejemplo de archivos generados en el directorio 'data/':
 * </p>
 *
 * <pre>
 * - products.csv     (ID, nombre y precio de productos)
 * - salesmen.csv     (Documento y nombre completo de los vendedores)
 * - sales_[ID].csv   (Ventas realizadas por cada vendedor)
 * - products.ser     (Lista serializada de objetos Product)
 * - salesmen.ser     (Lista serializada de objetos Salesman)
 * </pre>
 *
 * @author Poli
 * @version 2.0
 */
public class GenerateInfoFiles {

	private static final String DATA_DIR = "data/";
	private static final String VENDORS_FILE = DATA_DIR + "salesmen.csv";
	private static final String PRODUCTS_FILE = DATA_DIR + "products.csv";
	private static final String VENDORS_SER = DATA_DIR + "salesmen.ser";
	private static final String PRODUCTS_SER = DATA_DIR + "products.ser";

	private static final String[] FIRST_NAMES = { "Carlos", "Maria", "Luis", "Andrea", "Pedro", "Sofia", "Alejandra",
			"Jefferson", "Lorena", "Paola", "Diego", "Lucia", "Leidy", "Camila", "Juan", "Vanesa", "Clara" };
	private static final String[] LAST_NAMES = { "Gomez", "Rodriguez", "Lopez", "Fernandez", "Martinez", "Fernandez",
			"Martinez", "Torres", "Mendoza", "Jimenez", "Vargas", "Rios", "Coronado", "Roa", "Betancur" };
	private static final String[] PRODUCT_NAMES = { "Laptop", "Mouse", "Teclado", "Monitor", "Impresora", "Celular",
			"Tablet", "Auriculares", "GPS", "Televisor", "Control remoto", "Camara de seguridad" };

	private static final int PRODUCT_COUNT = 10;
	private static final int SALESMAN_COUNT = 5;
	private static final Random RANDOM = new Random();

	public static void main(String[] args) {
		try {
			createDirectory(DATA_DIR);

			List<Product> products = createProductsFile(PRODUCT_COUNT);
			serializeObject(products, PRODUCTS_SER);

			List<Salesman> salesmen = createSalesManInfoFile(SALESMAN_COUNT);
			serializeObject(salesmen, VENDORS_SER);

			for (Salesman s : salesmen) {
				for (int i = 0; i < (int) (Math.random() * 3 + 1); i++) {
					createSalesMenFile(PRODUCT_COUNT, s.getId(), i + 1);
				}
			}

			System.out.println("✅ Archivos generados y serializados exitosamente.");
		} catch (IOException e) {
			System.err.println("❌ Error generando archivos: " + e.getMessage());
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
			if (directory.mkdirs()) {
				System.out.println("📁 Directorio creado: " + dirPath);
			} else {
				System.err.println("⚠️ No se pudo crear el directorio: " + dirPath);
			}
		}
	}

	/**
	 * Genera un archivo CSV con ventas aleatorias de productos para un vendedor.
	 * 
	 * @param randomSalesCount Cantidad de productos distintos por vendedor
	 * @param id               Identificación única del vendedor
	 * @throws IOException Si ocurre un error de escritura
	 */
	public static void createSalesMenFile(int randomSalesCount, long id, int count) throws IOException {
		String filename = DATA_DIR + "sales_" + id + "_" + count + ".csv";

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

	/**
	 * Genera el archivo con información de vendedores.
	 * 
	 * @param salesmanCount Número de vendedores a generar
	 * @return Lista de IDs únicos generados
	 * @throws IOException Si ocurre un error de escritura
	 */
	public static List<Salesman> createSalesManInfoFile(int salesmanCount) throws IOException {
		Set<Long> ids = new HashSet<>();
		List<Salesman> salesmen = new ArrayList<>();

		try (BufferedWriter writer = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(VENDORS_FILE), "UTF-8"))) {
			for (int i = 0; i < salesmanCount; i++) {
				long id;
				do {
					id = generateRandomID();
				} while (ids.contains(id));
				ids.add(id);

				String firstName = FIRST_NAMES[RANDOM.nextInt(FIRST_NAMES.length)];
				String lastName = LAST_NAMES[RANDOM.nextInt(LAST_NAMES.length)];
				writer.write("CC;" + id + ";" + firstName + " " + lastName + "\n");

				salesmen.add(new Salesman(firstName, id, lastName));
			}
		}
		return salesmen;
	}

	/**
	 * Genera el archivo CSV de productos y retorna la lista de objetos Product.
	 * 
	 * @param productCount Número de productos a generar
	 * @return Lista de productos generados
	 * @throws IOException Si ocurre un error de escritura
	 */
	public static List<Product> createProductsFile(int productCount) throws IOException {
		List<Product> products = new ArrayList<>();
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
				products.add(new Product(id, name, price));
				writer.write(id + ";" + name + ";" + price + "\n");
			}
		}
		return products;
	}

	/**
	 * Genera un número de cédula aleatorio entre 8 y 10 dígitos.
	 * 
	 * @return Número de identificación generado
	 */
	public static long generateRandomID() {
		int digits = 8 + RANDOM.nextInt(3);
		long min = (long) Math.pow(10, digits - 1);
		long max = (long) Math.pow(10, digits) - 1;
		return min + (long) (RANDOM.nextDouble() * (max - min));
	}

	/**
	 * Serializa un objeto en una ruta dada como archivo .ser
	 * 
	 * @param obj  Objeto a serializar
	 * @param path Ruta de archivo de salida
	 */
	public static void serializeObject(Object obj, String path) {
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path))) {
			oos.writeObject(obj);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
