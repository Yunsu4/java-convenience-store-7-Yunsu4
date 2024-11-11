package store.model;

import static java.util.stream.Collectors.toList;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import store.view.error.ErrorException;
import store.view.error.InputErrorType;

public class PurchasedProducts {

    private static final String validRegex = "^[^\\-]+-[\\d]+$";

    private List<Product> purchasedProducts;

    public PurchasedProducts(List<String> items)  throws ErrorException {
        validateInputItems(items);
        this.purchasedProducts = extractPurchasedProduct(items);
    }

    private void validateInputItems(List<String> items) {
        Pattern validFormat = Pattern.compile(validRegex);
        for (String item : items) {
            if (!validFormat.matcher(item).matches()) {
                throw new ErrorException(InputErrorType.NEED_AVAILABLE_FORMAT);
            }
        }
    }

    public List<Product> extractPurchasedProduct(List<String> items) throws ErrorException {

        return items.stream()
                .map(product -> product.split("-"))
                .map(parts -> {
                    Map<String, Object> product = new LinkedHashMap<>();
                    product.put("name", parts[0]);
                    product.put("quantity", parseValidQuantity(parts[1]));
                    return new Product(product);
                })
                .toList();
    }


    private int parseValidQuantity(String quantity)  throws ErrorException {
        int parsedQuantity = Integer.parseInt(quantity);
        validateQuantity(parsedQuantity);
        return parsedQuantity;
    }

    private void validateQuantity(int quantity) throws ErrorException{
        if (quantity <= 0) {
            throw new ErrorException(InputErrorType.NEED_PRODUCT_COUNT_WITHIN_STOCK);
        }
    }

    public void display() {
        purchasedProducts.forEach(Product::display);
    }

    public List<Product> getProducts() {
        return purchasedProducts;
    }

    public List<String> getProductsNames() {
        return purchasedProducts.stream()
                .map(product -> product.getValueOfTheField("name"))
                .collect(Collectors.toList());
    }

    public Product getSameFieldProduct(String value, String field) {
        for (Product product : purchasedProducts) {
            String extractedValue = product.getValueOfTheField(field);
            if (value.equals(extractedValue)) {
                return product;
            }

        }
        return null;
    }

}
