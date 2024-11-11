package store.model;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
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
        Map<String, Product> productMap = new LinkedHashMap<>();

        for (String item : items) {
            String[] parts = item.split("-");
            String name = parts[0];
            int quantity = parseValidQuantity(parts[1]);

            // 이미 동일한 이름의 상품이 있는 경우, 수량을 추가
            if (productMap.containsKey(name)) {
                Product existingProduct = productMap.get(name);
                existingProduct.decreaseQuantity(-quantity); // 현재 수량에서 quantity 추가
            } else {
                // 새로운 상품인 경우, 맵에 추가
                Map<String, Object> productData = new LinkedHashMap<>();
                productData.put("name", name);
                productData.put("quantity", quantity);
                productMap.put(name, new Product(productData));
            }
        }

        return new ArrayList<>(productMap.values());
    }


    private int parseValidQuantity(String quantity)  throws ErrorException {
        int parsedQuantity = Integer.parseInt(quantity);
        validateQuantity(parsedQuantity);
        return parsedQuantity;
    }

    private void validateQuantity(int quantity) throws ErrorException{
        if (quantity <= 0) {
            throw new ErrorException(InputErrorType.NEED_AVAILABLE_INPUT);
        }
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
