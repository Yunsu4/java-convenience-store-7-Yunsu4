package store.controller;


import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import store.model.Product;
import store.model.ProductStock;
import store.model.PurchasedProducts;
import store.view.error.ErrorException;
import store.view.error.InputErrorType;

public class ProductController {

    private static final Pattern BRACKET_PATTERN = Pattern.compile("\\[(.*?)]");
    private static final Pattern PRODUCT_PATTERN = Pattern.compile("^[가-힣a-zA-Z]+-\\d+$");

    public List<String> extractValidProducts(String input) throws ErrorException {
        List<String> products = new ArrayList<>();
        Matcher matcher = BRACKET_PATTERN.matcher(input);

        int matchCount = 0;

        while (matcher.find()) {
            String product = matcher.group(1);
            if (isValidProductFormat(product)) {
                products.add(product);
                matchCount++;
            } else {
                throw new ErrorException(InputErrorType.NEED_AVAILABLE_FORMAT);
            }
        }

        if (products.isEmpty()) {
            throw new ErrorException(InputErrorType.NEED_AVAILABLE_FORMAT);
        }

        int commaCount = countCommas(input);
        if (commaCount != matchCount - 1) {
            throw new ErrorException(InputErrorType.NEED_AVAILABLE_FORMAT);
        }

        String inputWithoutProducts = input.replaceAll("\\[.*?]", "").replaceAll(",", "").trim();
        if (!inputWithoutProducts.isEmpty()) {
            throw new ErrorException(InputErrorType.NEED_AVAILABLE_FORMAT);
        }

        return products;
    }

    private int countCommas(String input) {
        int count = 0;
        for (char c : input.toCharArray()) {
            if (c == ',') {
                count++;
            }
        }
        return count;
    }

    private boolean isValidProductFormat(String product) {
        return PRODUCT_PATTERN.matcher(product).matches();
    }


    public void checkProductInConvenience(PurchasedProducts purchasedProducts, ProductStock productStock) {
        List<String> productNames = purchasedProducts.getProductsNames();

        List<String> stockNames = productStock.getProductsNames();

        for (String productName : productNames) {
            if (!stockNames.contains(productName)) {
                throw new ErrorException(InputErrorType.NEED_EXISTING_PRODUCT);
            }
        }

    }

    public void checkProductQuantityAvailable(PurchasedProducts purchasedProducts, ProductStock productStock) {
        for (Product purchasedProduct : purchasedProducts.getProducts()) {
            String productName = purchasedProduct.getValueOfTheField("name");

            int totalStockQuantity =productStock.getTotalQuantityByName(productName);

            if (totalStockQuantity < purchasedProduct.parseQuantity()) {
                throw new ErrorException(InputErrorType.NEED_PRODUCT_COUNT_WITHIN_STOCK);
            }
        }
    }

}
