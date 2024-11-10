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


    public List<String> extractValidProducts(String input){
        String trimmedInput = input.trim();
        List<String> items = new ArrayList<>();

        String processedInput = extractProductRecursively(trimmedInput, items);
        validateProductsAvailable(processedInput);

        return items;
    }

    private void validateProductsAvailable(String processedInput) {
        if(!processedInput.replace(",","").trim().isEmpty()){
            throw new ErrorException(InputErrorType.NEED_AVAILABLE_FORMAT);
        }
    }

    private String extractProductRecursively(String input, List<String> products) {
        try {
            String product = extractProduct(input);
            products.add(product);
            String updatedInputString = removeExtractedProduct(input, product);

            return extractProductRecursively(updatedInputString, products);
        } catch (IllegalArgumentException e) {
            return input;
        }
    }

    private String extractProduct(String input) {
        Matcher matcher = findProduct(input);
        return matcher.group(1);
    }

    private String removeExtractedProduct(String input, String product) {
        return input.replaceAll("\\["+product+"\\]","");
    }

    private Matcher findProduct(String input) {
        Matcher matcher = createMatcher(input);

        if (matcher.find()) {
            return matcher;
        }
        throw new ErrorException(InputErrorType.NEED_AVAILABLE_FORMAT);
    }

    private Matcher createMatcher(String input) {
        Pattern pattern = Pattern.compile("[\\[](.*?)[\\]]");
        return pattern.matcher(input);
    }

    public void checkProductInConvenience(Product promotable, Product nonPromotable){
        if(isOutOfStock(promotable) && isOutOfStock(nonPromotable)) {
            throw new ErrorException(InputErrorType.NEED_EXISTING_PRODUCT);
        }
    }

    private boolean isOutOfStock(Product productInStock){
        return productInStock == null;
    }

    public void checkProductQuantityAvailable(ProductStock productStock, Product purchasedProduct){
        String productName = purchasedProduct.getValueOfTheField("name");
        int quantityOfStock = productStock.getTotalQuantityByName(productName);

        if(quantityOfStock<purchasedProduct.parseQuantity()){
            throw new ErrorException(InputErrorType.NEED_PRODUCT_COUNT_WITHIN_STOCK);
        }
    }



}
