package store.controller;


import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import store.model.Product;
import store.model.ProductStock;
import store.view.error.ErrorException;
import store.view.error.InputErrorType;

public class ProductController {

    private static final Pattern PRODUCT_PATTERN = Pattern.compile("\\[(\\S+?-\\d+)]");

    public List<String> extractValidProducts(String input) throws ErrorException {
        List<String> products = new ArrayList<>();
        Matcher matcher = PRODUCT_PATTERN.matcher(input);

        while (matcher.find()) {
            String product = matcher.group(1);
            products.add(product);
        }

        String reconstructedInput = products.stream()
                .map(p -> "[" + p + "]")
                .reduce((p1, p2) -> p1 + "," + p2)
                .orElse("");

        if (!reconstructedInput.equals(input.trim())) {
            throw new ErrorException(InputErrorType.NEED_AVAILABLE_FORMAT);
        }

        return products;
    }


    public void checkProductInConvenience(Product promotable, Product nonPromotable) {
        if (isOutOfStock(promotable) && isOutOfStock(nonPromotable)) {
            throw new ErrorException(InputErrorType.NEED_EXISTING_PRODUCT);
        }
    }

    private boolean isOutOfStock(Product productInStock) {
        return productInStock == null;
    }

    public void checkProductQuantityAvailable(ProductStock productStock, Product purchasedProduct) {
        String productName = purchasedProduct.getValueOfTheField("name");
        int quantityOfStock = productStock.getTotalQuantityByName(productName);

        if (quantityOfStock < purchasedProduct.parseQuantity()) {
            throw new ErrorException(InputErrorType.NEED_PRODUCT_COUNT_WITHIN_STOCK);
        }
    }


}
