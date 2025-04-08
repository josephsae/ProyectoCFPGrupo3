package org.poli.main;

import java.io.*;
import java.util.*;
import org.poli.generatefiles.Product;
import org.poli.generatefiles.Salesman;

/**
 * Clase principal para generar reportes de ventas a partir de datos
 * almacenados.
 */
public class Main {

    private static final String DATA_DIR = "data/";
    private static final String VENDORS_SER = DATA_DIR + "salesmen.ser";
    private static final String PRODUCTS_SER = DATA_DIR + "products.ser";
    private static final String SALES_REPORT_FILE = DATA_DIR + "sales_report.csv";
    private static final String PRODUCTS_REPORT_FILE = DATA_DIR + "products_report.csv";

    /**
     * Método principal que inicia la generación de reportes.
     *
     * @param args Argumentos de línea de comandos (no utilizados)
     */
    public static void main(String[] args) {
        try {
            generateSalesReport();
            generateSortedProductSales();
        } catch (Exception e) {
            System.err.println("❌ Error al generar los reportes: " + e.getMessage());
        }
    }

    /**
     * Genera el reporte de ventas ordenado por total descendente.
     *
     * @throws IOException Si hay error leyendo/escribiendo archivos
     * @throws ClassNotFoundException Si falla la deserialización
     */

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

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SALES_REPORT_FILE))) {
            for (SalesEntry entry : salesEntries) {
                writer.write(entry.name + ";" + entry.total + "\n");
            }
            System.out.println("✅ Reporte de ventas generado exitosamente en: " + SALES_REPORT_FILE);
        } catch (IOException e) {
            System.err.println("❌ Error escribiendo el archivo de reporte de ventas: " + e.getMessage());
        }
    }

    /**
     * Carga los precios de productos desde archivo serializado.
     *
     * @return Mapa con ID de producto y su precio
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

    /**
     * Carga la lista de vendedores desde archivo serializado.
     *
     * @return Lista de objetos Salesman
     */

    private static List<Salesman> loadSalesmen() throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(VENDORS_SER))) {
            @SuppressWarnings("unchecked")
            List<Salesman> salespeople = (List<Salesman>) ois.readObject();
            return salespeople;
        }
    }

    /**
     * Calcula el total de ventas para un vendedor.
     *
     * @param id ID del vendedor
     * @param productPrices Mapa de precios de productos
     * @return Total de ventas calculado
     */

    private static int calculateTotalSalesForVendor(String id, Map<String, Integer> productPrices) {
        List<String> files = listFilesByFolder(new File(DATA_DIR), id);
        int total = 0;
        for (String file : files) {
            String salesFile = DATA_DIR + file;
            List<String> lines = readFile(salesFile);
            boolean readingProducts = false;

            for (String line : lines) {
                if (line.startsWith("ProductID")) {
                    readingProducts = true;
                    continue;
                }
                if (!readingProducts) {
                    continue;
                }

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
        }
        return total;
    }

    /**
     * Lee un archivo y devuelve sus líneas.
     *
     * @param path Ruta del archivo
     * @return Lista de líneas leídas
     */
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

    /**
     * Genera reporte de productos vendidos ordenados por cantidad.
     */
    public static void generateSortedProductSales() {
        Map<String, Integer> productSales = new HashMap<>();

        try {
            List<Salesman> salesmen = loadSalesmen();

            for (Salesman s : salesmen) {
                String id = String.valueOf(s.getId());
                List<String> salesFiles = listFilesByFolder(new File(DATA_DIR), id);

                for (String filename : salesFiles) {
                    String salesFile = DATA_DIR + filename;
                    List<String> salesLines = readFile(salesFile);

                    boolean readingProducts = false;
                    for (String line : salesLines) {
                        if (line.startsWith("ProductID")) {
                            readingProducts = true;
                            continue;
                        }
                        if (!readingProducts) {
                            continue;
                        }

                        String[] parts = line.split(";");
                        if (parts.length >= 2) {
                            String productId = parts[0];
                            int quantity = Integer.parseInt(parts[1]);
                            productSales.put(productId, productSales.getOrDefault(productId, 0) + quantity);
                        }
                    }
                }
            }

            List<Map.Entry<String, Integer>> sortedSales = new ArrayList<>(productSales.entrySet());
            sortedSales.sort((a, b) -> b.getValue().compareTo(a.getValue()));

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(PRODUCTS_REPORT_FILE))) {
                writer.write("ProductID;TotalQuantity\n");
                for (Map.Entry<String, Integer> entry : sortedSales) {
                    writer.write(entry.getKey() + ";" + entry.getValue() + "\n");
                }
                System.out.println("✅ Reporte de productos vendidos generado exitosamente en: " + PRODUCTS_REPORT_FILE);
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("❌ Error al generar reporte de productos vendidos: " + e.getMessage());
        }
    }

    /**
     * Clase auxiliar para almacenar temporalmente datos de ventas.
     */

    private static class SalesEntry {

        String name;
        int total;

        /**
         * Crea una entrada de venta
         *
         * @param name Nombre del vendedor
         * @param total Total de ventas
         */

        public SalesEntry(String name, int total) {
            this.name = name;
            this.total = total;
        }
    }

    /**
     * Lista archivos en un directorio que contienen un ID específico en su
     * nombre.
     *
     * @param folder Directorio donde buscar los archivos
     * @param id Cadena que debe contener el nombre del archivo
     * @return Lista de nombres de archivos que cumplen con los criterios
     */

    private static List<String> listFilesByFolder(final File folder, String id) {
        List<String> files = new ArrayList<>();
        for (final File fileEntry : folder.listFiles()) {
            if (!fileEntry.isDirectory() && fileEntry.getName().contains(id)) {
                files.add(fileEntry.getName());
            }
        }
        return files;
    }
}
