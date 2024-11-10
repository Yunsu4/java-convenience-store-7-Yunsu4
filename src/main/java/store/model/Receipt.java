package store.model;

import java.util.ArrayList;
import java.util.List;

public class Receipt {

    private int totalDiscountedPrice = 0;
    private int totalFullPrice = 0;
    private int totalOriginalPrice = 0;
    private int membershipDiscountPrice = 0;
    private int finalAmountDue = 0;

    private int totalPurchaseQuantity = 0;


    private PurchasedProducts purchasedProducts;
    private ProductStock productStock;

    private PurchaseDetail purchaseDetail;


    public Receipt(PurchasedProducts purchasedProducts, ProductStock productStock, PurchaseDetail purchaseDetail) {
        this.purchasedProducts = purchasedProducts;
        this.productStock = productStock;
        this.purchaseDetail = purchaseDetail;
    }


    public void updateReceipt(Product purchased, int promotableQuantity, int promotionBonusQuantity) {
        purchasedProducts.getProducts().stream()
                .filter(purchasedProduct -> purchasedProduct.equals(purchased))
                .findFirst()
                .ifPresent(purchasedProduct -> processReceiptUpdate(purchasedProduct, promotableQuantity,
                        promotionBonusQuantity));
    }

    private void processReceiptUpdate(Product purchasedProduct, int promotableQuantity, int promotionBonusQuantity) {
        String productName = purchasedProduct.getValueOfTheField("name");
        Product productInStock = productStock.getSameFieldProduct(productName, "name");
        int purchaseQuantity = purchasedProduct.parseQuantity();
        int currentPurchasePrice = Integer.parseInt(productInStock.getValueOfTheField("price"));

        totalDiscountedPrice += promotionBonusQuantity * currentPurchasePrice;
        totalFullPrice += (purchaseQuantity - promotableQuantity) * currentPurchasePrice;
        totalPurchaseQuantity += purchaseQuantity;

        int currentOriginalPrice = currentPurchasePrice * purchaseQuantity;
        totalOriginalPrice += currentOriginalPrice;
        finalAmountDue = totalOriginalPrice - totalDiscountedPrice - membershipDiscountPrice;

        extractPurchaseDetails(promotionBonusQuantity, productName, purchaseQuantity, currentOriginalPrice);
    }

    private void extractPurchaseDetails(int promotionBonusQuantity, String productName, int purchaseQuantity,
                                        int currentOriginalPrice) {
        List<String> productDetail = new ArrayList<>();
        productDetail.add(productName);
        productDetail.add(Integer.toString(purchaseQuantity));
        productDetail.add(Integer.toString(currentOriginalPrice));
        List<String> bonusProductDetail = new ArrayList<>();

        if (promotionBonusQuantity > 0) {
            bonusProductDetail.add(productName);
            bonusProductDetail.add(Integer.toString(promotionBonusQuantity));
            purchaseDetail.addPurchaseDetail(productDetail, bonusProductDetail);
        }
        if (promotionBonusQuantity <= 0) {
            purchaseDetail.addPurchaseDetail(productDetail);
        }
    }

    public List<String> getProductDetails() {
        return purchaseDetail.getProductDetails();
    }

    public List<String> getBonusProductDetails() {
        return purchaseDetail.getBonusProductDetails();
    }


    public void printFinalReceipt() {
        System.out.println(
                "총 구매액    " + String.format("%,d", totalPurchaseQuantity) +"    "+ String.format("%,d", totalOriginalPrice));
        System.out.println("행사 할인" + String.format("       -%,d", totalDiscountedPrice));
        System.out.println("멤버십 할인" + String.format("       -%,d", membershipDiscountPrice));
        System.out.println("내실돈" + String.format("        %,d", finalAmountDue));
    }


    public void executeMembership() {
        membershipDiscountPrice += (int) Math.ceil(totalFullPrice * 0.3);
        if (membershipDiscountPrice > 8000) {
            membershipDiscountPrice = 8000;
        }
        finalAmountDue = totalOriginalPrice - totalDiscountedPrice - membershipDiscountPrice;
    }
}
