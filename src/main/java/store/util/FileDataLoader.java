package store.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class FileDataLoader {

    public <T> List<T> loadDataFromFile(String fileName, Class<T> type) throws FileNotFoundException {
        URL resource = getClass().getClassLoader().getResource(fileName);
        Scanner fileData = readFile(resource);

        String[] fields = extractFields(fileData);
        return extractValues(fileData, fields, type);
    }

    private Scanner readFile(URL resource) throws FileNotFoundException {
        checkFilePathAvailable(resource);
        File file = new File(resource.getFile());
        checkFileEmpty(file);
        return new Scanner(file);
    }

    private void checkFileEmpty(File file) {
        if (file.length() == 0) {
            throw new IllegalArgumentException("File is empty: " + file.getPath());
        }
    }

    private void checkFilePathAvailable(URL resource) throws FileNotFoundException {
        if (resource ==null) {
            throw new FileNotFoundException("File path does not exist in resources");
        }
    }

    private String[] extractFields(Scanner fileData) {
        String[] fields = {};

        if (fileData.hasNextLine()) {
            String header = fileData.nextLine();
            fields = header.split(",", -1);
        }
        return fields;
    }

    private <T> List<T> extractValues(Scanner fileData, String[] productFields, Class<T> type) {
        List<T> items = new ArrayList<>();

        while (fileData.hasNextLine()) {
            String line = fileData.nextLine();
            String[] values = line.split(",", -1);

            Map<String, Object> itemData = matchValuesWithFields(productFields, values);
            T item = createInstance(type, itemData);
            items.add(item);
        }
        fileData.close();

        return items;

    }

    private Map<String, Object> matchValuesWithFields(String[] productFields, String[] values) {
        Map<String, Object> product = new LinkedHashMap<>();
        for(int i = 0; i< productFields.length; i++){
            String key = productFields[i];
            String value = values[i];
            product.put(key, value);
        }
        return product;
    }

    private <T> T createInstance(Class<T> type, Map<String, Object> data){
        try{
            return type.getDeclaredConstructor(Map.class).newInstance(data);
        }catch (Exception e){
            throw new RuntimeException("Failed to create instance of" + type.getName(), e);
        }
    }
}
