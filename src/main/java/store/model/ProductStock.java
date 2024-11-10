package store.model;


import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;
import store.util.FileDataLoader;

public class ProductStock {

    private List<Product> products;
    private FileDataLoader fileDataLoader;

    public ProductStock(){
        this.fileDataLoader = new FileDataLoader();
        this.products = loadProductsFromFile();

    }

    public void display(){
        products.forEach(Product::display);
    }

    public List<Product> loadProductsFromFile(){
        List<Product> productStock = new LinkedList<>();
        try {
            productStock = fileDataLoader.loadDataFromFile("products.md", Product.class);
        } catch (FileNotFoundException | IllegalArgumentException e) {
            System.err.println("[Error] " + e.getMessage());
        }
        return productStock;
    }

    public int getTotalQuantityByName(String productName){
        Product promotable = getSameFieldProductWithPromotion(productName, "name", true);
        Product nonPromotable = getSameFieldProductWithPromotion(productName, "name", false);

        int totalQuantity = 0;
        if(promotable != null){
            totalQuantity+=promotable.parseQuantity();
        }
        if(nonPromotable != null){
            totalQuantity+=nonPromotable.parseQuantity();
        }

        return totalQuantity;
    }

    public Product getSameFieldProduct(String value, String field){
        for(Product product: products){
            String extractedValue = product.getValueOfTheField(field);
            if(value.equals(extractedValue)){
                return product;
            }
        }
        return null;
    }

    public Product getSameFieldProductWithPromotion(String value, String field, boolean isPromotable){
        for(Product product: products) {
            boolean isMatchValue = isMatchValue(product,value, field);
            boolean hasPromotion = hasPromotion(product);

            if (isMatchValue && (isPromotable == hasPromotion)) {
                return product;
            }
        }
        return null;
    }

    private boolean hasPromotion(Product product) {
        String promotion = product.getValueOfTheField("promotion");
        return !"null".equals(promotion);
    }

    private boolean isMatchValue(Product product, String value, String field) {
        String extractedValue = product.getValueOfTheField(field);
        return value.equals(extractedValue);
    }
}
