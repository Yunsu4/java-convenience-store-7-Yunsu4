package store.controller;

import camp.nextstep.edu.missionutils.DateTimes;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import store.model.Product;
import store.model.ProductStock;
import store.model.Promotion;
import store.model.Promotions;
import store.model.PurchaseDetail;
import store.model.PurchasedProducts;
import store.model.Receipt;
import store.view.InputView;
import store.view.error.ErrorException;
import store.view.error.InputErrorType;

public class PromotionController {

    private final InputView inputView;
    private final ProductStock productStock;
    private final Promotions promotions;
    private final ProductController productController;


    public void displayAllTheProducts() {
        productStock.display();
    }

    public PromotionController(InputView inputView, ProductController productController) {
        this.inputView = inputView;
        this.productStock = new ProductStock();
        this.promotions = new Promotions();
        this.productController = productController;
    }

    public Receipt process() throws ErrorException {
        List<String> items = getValidInput(inputView::readItem, productController::extractValidProducts);
        LocalDateTime localDateTime = DateTimes.now();

        PurchasedProducts purchasedProducts = new PurchasedProducts(items);
        productController.checkProductInConvenience(purchasedProducts, productStock);
        productController.checkProductQuantityAvailable(purchasedProducts, productStock);


        Receipt receipt = new Receipt(purchasedProducts, productStock, new PurchaseDetail());


        for (Product purchasedProduct : purchasedProducts.getProducts()) {
            String productName = purchasedProduct.getValueOfTheField("name");
            Product promotable = productStock.getSameFieldProductWithPromotion(productName, "name", true);
            Product nonPromotable = productStock.getSameFieldProductWithPromotion(productName, "name", false);


            int purchaseQuantity = purchasedProduct.parseQuantity();

            //프로모션이 없는 경우
            if (promotable == null) {
                nonPromotable.decreaseQuantity(purchaseQuantity);
                receipt.updateReceipt(purchasedProduct, 0, 0);
                continue;
            }

            //아래로 다 프로모션이 있는 경우

            int promotionalProductQuantity = promotable.parseQuantity();

            // 프로모션 재고가 0인 경우
            if (promotionalProductQuantity == 0) {
                nonPromotable.decreaseQuantity(purchaseQuantity);
                receipt.updateReceipt(purchasedProduct, 0, 0);
                continue;
            }

            // 아래 다 프로모션 재고가 0이 아닌 경우

            String promotion = promotable.getValueOfTheField("promotion");
            Promotion matchedPromotion = promotions.getMatchedPromotion(promotion, "name");

            //행사 기간이 아닌 경우
            if (!matchedPromotion.isInPromotionPeriod(localDateTime)) {
                nonPromotable.decreaseQuantity(purchaseQuantity);
                receipt.updateReceipt(purchasedProduct, 0, 0);
                continue;
            }

            //아래 다 행사 기간인 경우

            int promotionBonusQuantity = Integer.parseInt(matchedPromotion.getValueOfTheField("get"));
            int promotionMinQuantity = Integer.parseInt(matchedPromotion.getValueOfTheField("buy"));
            int promotionAcquiredQuantity = promotionMinQuantity + promotionBonusQuantity;

            //프로모션 최소 개수 > 구매 개수
            if (promotionMinQuantity > purchaseQuantity) {
                nonPromotable.decreaseQuantity(purchaseQuantity);
                receipt.updateReceipt(purchasedProduct, 0, 0);
                continue;
            }

            // 프로모션 최소 구매 개수<= 구매 개수< 프로모션 해당 개수
            if (promotionAcquiredQuantity > purchaseQuantity) {

                if(promotionalProductQuantity<promotionMinQuantity){
                    nonPromotable.decreaseQuantity(purchaseQuantity);
                    receipt.updateReceipt(purchasedProduct, 0,0);
                    continue;
                }
                if(promotionalProductQuantity==promotionMinQuantity){
                    boolean purchaseFullPrice = getValidInput(
                            () -> inputView.readPurchaseFullPrice(productName, promotionalProductQuantity),
                            this::isValidPositive
                    );

                    if(!purchaseFullPrice){
                        purchasedProduct.decreaseQuantity(promotionalProductQuantity);
                        receipt.updateReceipt(purchasedProduct,0,0);
                        continue;
                    }
                    promotable.decreaseQuantity(promotionalProductQuantity);
                    int currentQuantity = purchaseQuantity-promotionalProductQuantity;
                    nonPromotable.decreaseQuantity(currentQuantity);
                    receipt.updateReceipt(purchasedProduct, 0,0);
                    continue;
                }
                boolean addItem = getValidInput(
                        () -> inputView.readAddItem(productName, promotionBonusQuantity),
                        this::isValidPositive
                );

                // 증정 상품 추가 구매 안 할 경우
                if (!addItem) {
                    promotable.decreaseQuantity(purchaseQuantity);
                    receipt.updateReceipt(purchasedProduct, 0, 0);
                    continue;
                }
                //증정 상품 추가 구매할 경우
                purchasedProduct.addQuantity();
            }

            // 아래 다 구매 개수>= 프로모션 해당 개수

            // 프로모션 재고< 프로모션 해당 개수
            if (promotionalProductQuantity < promotionAcquiredQuantity) {
                int finalPurchaseQuantity = purchasedProduct.parseQuantity();
                int nonPromotableQuantity = finalPurchaseQuantity - promotionalProductQuantity;

                boolean purchaseFullPrice = getValidInput(
                        () -> inputView.readPurchaseFullPrice(productName, finalPurchaseQuantity),
                        this::isValidPositive
                );

                //프로모션 없는 거를 정가로 구매 안 하기
                if (!purchaseFullPrice) {
                    purchasedProduct.decreaseQuantity(finalPurchaseQuantity);
                    receipt.updateReceipt(purchasedProduct, 0, 0);
                    continue;
                }

                //프로모션 없는 거를 정가로 구매하기
                promotable.decreaseQuantity(promotionalProductQuantity);
                nonPromotable.decreaseQuantity(nonPromotableQuantity);
                receipt.updateReceipt(purchasedProduct, 0, 0);
                continue;
            }

            // 아래로 다 프로모션 재고>= 프로모션 해당 개수,   구매 개수>= 프로모션 해당 개수

            int promotableQuantity =
                    (promotionalProductQuantity / promotionAcquiredQuantity) * promotionAcquiredQuantity;

            purchaseQuantity = purchasedProduct.parseQuantity();
            int nonPromotableQuantity = purchaseQuantity - promotableQuantity;






            
            // 프로모션 받을 수 있는 개수>= 구매 개수
            if (promotableQuantity >= purchaseQuantity) {

                if(purchaseQuantity%promotionAcquiredQuantity == 0){
                    promotable.decreaseQuantity(purchaseQuantity);
                    receipt.updateReceipt(purchasedProduct, purchaseQuantity, purchaseQuantity / promotionAcquiredQuantity);
                    continue;
                }
                int currentNonPromotableQuantity = purchaseQuantity%promotionAcquiredQuantity;

                if (promotionMinQuantity == 1 && currentNonPromotableQuantity == 1) {
                    boolean addItem = getValidInput(
                            () -> inputView.readAddItem(productName, 1),
                            this::isValidPositive
                    );

                    if (addItem) {
                        purchasedProduct.addQuantity();
                        purchaseQuantity++;
                        promotable.decreaseQuantity(purchaseQuantity);
                        receipt.updateReceipt(purchasedProduct, purchaseQuantity, purchaseQuantity / 2);
                    } else {
                        promotable.decreaseQuantity(purchaseQuantity - 1);
                        nonPromotable.decreaseQuantity(1);
                        receipt.updateReceipt(purchasedProduct, purchaseQuantity - 1, (purchaseQuantity - 1) / 2);
                    }
                    continue;
                }


                // 프로모션 조건을 충족하지 않는 수량이 있는 경우
                if (currentNonPromotableQuantity > 0) {
                    int additionalItemsForPromotion = promotionAcquiredQuantity - currentNonPromotableQuantity;

                    int finalAdditionalItemsForPromotion = additionalItemsForPromotion;
                    boolean addItems = getValidInput(
                            () -> inputView.readAddItem(productName, finalAdditionalItemsForPromotion),
                            this::isValidPositive
                    );

                    if (addItems) {
                        // 추가 구매로 프로모션 조건 충족
                        purchasedProduct.decreaseQuantity(-finalAdditionalItemsForPromotion);
                        purchaseQuantity += additionalItemsForPromotion;
                        promotable.decreaseQuantity(purchaseQuantity);
                        receipt.updateReceipt(purchasedProduct, purchaseQuantity, purchaseQuantity / promotionAcquiredQuantity);
                    } else {
                        // 추가 구매 거부, 일부만 프로모션 적용
                        int promotionAppliedQuantity = purchaseQuantity - currentNonPromotableQuantity;
                        promotable.decreaseQuantity(promotionAppliedQuantity);
                        nonPromotable.decreaseQuantity(currentNonPromotableQuantity);
                        receipt.updateReceipt(purchasedProduct, promotionAppliedQuantity, promotionAppliedQuantity / promotionAcquiredQuantity);
                    }
                } else {
                    // 모든 수량이 프로모션 조건을 충족
                    promotable.decreaseQuantity(purchaseQuantity);
                    receipt.updateReceipt(purchasedProduct, purchaseQuantity, purchaseQuantity / promotionAcquiredQuantity);
                }
                continue;
            }








            //아래로 다 프로모션 받을 수 있는 개수< 구매 개수
            //=========================================

            boolean purchaseFullPrice = getValidInput(
                    () -> inputView.readPurchaseFullPrice(productName, nonPromotableQuantity),
                    this::isValidPositive
            );
            if (!purchaseFullPrice) {
                purchasedProduct.decreaseQuantity(nonPromotableQuantity);
                int finalPurchaseQuantity = purchasedProduct.parseQuantity();
                promotable.decreaseQuantity(finalPurchaseQuantity);
                receipt.updateReceipt(purchasedProduct, promotableQuantity,
                        promotableQuantity / promotionAcquiredQuantity);
                continue;
            }
            promotable.decreaseQuantity(promotableQuantity);
            purchaseFullPrice(promotable, nonPromotableQuantity, nonPromotable);

            receipt.updateReceipt(purchasedProduct, promotableQuantity, promotableQuantity / promotionAcquiredQuantity);

        }

        return receipt;
    }

    private void purchaseFullPrice(Product promotableProductInStock, int nonPromotableQuantity,
                                   Product nonPromotableProductInStock) {
        int remainingQuantityInStock = Integer.parseInt(promotableProductInStock.getValueOfTheField("quantity"));
        int decreaseInNonPromotableQuantity = nonPromotableQuantity - remainingQuantityInStock;

        promotableProductInStock.decreaseQuantity(remainingQuantityInStock);
        nonPromotableProductInStock.decreaseQuantity(decreaseInNonPromotableQuantity);
    }


    private boolean isValidPositive(String input) {
        if (input.equals("Y")) {
            return true;
        }
        if (input.equals("N")) {
            return false;
        }
        throw new ErrorException(InputErrorType.NEED_AVAILABLE_INPUT);
    }


    public static <T> T getValidInput(Supplier<String> inputSupplier, Function<String, T> converter) {
        while (true) {
            String input = inputSupplier.get();
            try {
                return converter.apply(input);
            } catch (ErrorException e) {
                System.out.println(e.getMessage());
            }
        }
    }

}
