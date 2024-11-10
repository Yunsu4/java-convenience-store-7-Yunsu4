package store.model;

public class Receipt {

    private int totalBonusQuantity = 0;
    private int totalDiscountedPrice = 0;
    private int totalFullPrice = 0;
    private int totalOriginalPrice = 0;
    private int membershipDiscountPrice = 0;
    private int finalAmountDue = 0;


    private PurchasedProducts purchasedProducts;
    private ProductStock productStock;


    public Receipt(PurchasedProducts purchasedProducts, ProductStock productStock) {
        this.purchasedProducts = purchasedProducts;
        this.productStock = productStock;
    }


    public void updateReceipt(Product purchased, int promotableQuantity, int promotionBonusQuantity) {
        purchasedProducts.getProducts().stream()
                .filter(purchasedProduct -> purchasedProduct.equals(purchased))
                .findFirst()
                .ifPresent(purchasedProduct -> processReceiptUpdate(purchasedProduct, promotableQuantity,
                        promotionBonusQuantity));
    }

    private void processReceiptUpdate(Product purchasedProduct, int promotableQuantity, int promotionBonusQuantity) {
        int purchaseQuantity = purchasedProduct.parseQuantity();
        String productName = purchasedProduct.getValueOfTheField("name");
        Product productInStock = productStock.getSameFieldProduct(productName, "name");
        int currentPurchasePrice = Integer.parseInt(productInStock.getValueOfTheField("price"));

        totalDiscountedPrice += promotionBonusQuantity * currentPurchasePrice;
        totalFullPrice += (purchaseQuantity - promotableQuantity) * currentPurchasePrice;

        totalBonusQuantity += promotionBonusQuantity;
        totalOriginalPrice += currentPurchasePrice * purchaseQuantity;
        finalAmountDue = totalOriginalPrice - totalDiscountedPrice - membershipDiscountPrice;

    }


    public void printFinalReceipt() {
        System.out.println("총 구매 금액: " + String.format("%,d", totalOriginalPrice));
        System.out.println("증정 상품 개수: " + String.format("%,d", totalBonusQuantity));
        System.out.println("프로모션으로 할인 받은 금액: " + String.format("%,d", totalDiscountedPrice));
        System.out.println("정가로 구매한 금액: " + String.format("%,d", totalFullPrice));
        System.out.println("멤버십 할인 금액: " + String.format("%,d", membershipDiscountPrice));
        System.out.println("내실돈" + String.format("%,d", finalAmountDue));
    }

    public void executeMembership() {
        membershipDiscountPrice += (int) Math.ceil(totalFullPrice * 0.3);
        if (membershipDiscountPrice > 8000) {
            membershipDiscountPrice = 8000;
        }
        finalAmountDue = totalOriginalPrice - totalDiscountedPrice - membershipDiscountPrice;


    }


}
