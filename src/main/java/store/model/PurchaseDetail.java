package store.model;

import java.util.ArrayList;
import java.util.List;

public class PurchaseDetail {


    private List<String> productDetails;
    private List<String> bonusProductDetails;

    public PurchaseDetail() {
        this.productDetails = new ArrayList<>();
        this.bonusProductDetails = new ArrayList<>();
    }

    public void addPurchaseDetail(List<String> addedProductDetail, List<String> addedBonusQuantity) {
        productDetails.addAll(addedProductDetail);
        bonusProductDetails.addAll(addedBonusQuantity);
    }
    public void addPurchaseDetail(List<String> addedProductDetail) {
        productDetails.addAll(addedProductDetail);
    }

    public List<String> getProductDetails(){
        return productDetails;
    }

    public List<String> getBonusProductDetails(){
        return bonusProductDetails;
    }
}
