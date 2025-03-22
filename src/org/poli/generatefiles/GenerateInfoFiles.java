package org.poli.generatefiles;

import java.io.*;
import java.util.*;

public class GenerateInfoFiles {

    private static final String DATA_DIR = "data/";
    private static final String VENDORS_FILE = DATA_DIR + "salesmen.csv";
    private static final String[] FIRST_NAMES = {"Carlos", "Maria", "Luis", "Andrea", "Pedro"};
    private static final String[] LAST_NAMES = {"Gomez", "Rodriguez", "Lopez", "Fernandez", "Martinez"};
    private static final Random RANDOM = new Random();

    public static void main(String[] args) {
        try {
            createDirectory(DATA_DIR);
            createSalesManInfoFile(5);
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


    public static List<Long> createSalesManInfoFile(int salesmanCount) throws IOException {
        Set<Long> ids = new HashSet<>();
        List<Long> idList = new ArrayList<>();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(VENDORS_FILE))) {
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

    // Generates a random ID (C.C.) between 8 and 10 digits
    public static long generateRandomID() {
        int digits = 8 + RANDOM.nextInt(3);
        long min = (long) Math.pow(10, digits - 1);
        long max = (long) Math.pow(10, digits) - 1;
        return min + (long)(RANDOM.nextDouble() * (max - min));
    }
}
