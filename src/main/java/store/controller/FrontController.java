package store.controller;

import static store.controller.PromotionController.getValidInput;

import store.model.Receipt;
import store.view.InputView;
import store.view.OutputView;
import store.view.error.ErrorException;
import store.view.error.InputErrorType;

public class FrontController {
    private final InputView inputView;
    private final OutputView outputView;

    private final ProductController productController;
    private final PromotionController promotionController;

    public FrontController(InputView inputView, OutputView outputView,
                           ProductController productController, PromotionController promotionController) {
        this.inputView = inputView;
        this.outputView = outputView;
        this.productController = productController;
        this.promotionController = promotionController;

    }

    public void runFrontController() {
        run();
        retry();
    }

    private void display() {
        promotionController.displayAllTheProducts();
    }

    private void retry() {
        while (true) {
            try {
                boolean continuePurchase = getValidInput(inputView::readContinuePurchase, this::isValidPositive);
                if (!continuePurchase) {
                    throw new IllegalArgumentException();
                }
                run();
            } catch (IllegalArgumentException e) {
                return;
            }
        }
    }

    private void run() {
        MembershipController membershipController = new MembershipController(inputView);

        while (true) {
            try {
                display();
                Receipt receipt = promotionController.process();
                membershipController.execute(receipt);
                receipt.printFinalReceipt();
                return;
            } catch (ErrorException e) {
                System.out.println(e.getMessage());
                new FrontController(inputView, outputView, productController, promotionController);
            }
        }
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
}
