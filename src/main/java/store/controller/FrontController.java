package store.controller;

import static store.controller.PromotionController.getValidInput;

import java.text.NumberFormat;
import java.util.List;
import store.model.Receipt;
import store.view.InputView;
import store.view.OutputView;
import store.view.error.ErrorException;
import store.view.error.InputErrorType;

public class FrontController {

    private static final String newline = System.getProperty("line.separator");

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
        boolean skipStartMessage = false;

        while (true) {
            try {
                if (!skipStartMessage) {
                    outputView.startMessage();
                    display();
                }

                Receipt receipt = promotionController.process();
                membershipController.execute(receipt);
                printTotalReceipt(receipt);
                return;
            } catch (ErrorException e) {
                System.out.println(e.getMessage());
                if (e.getMessage().contains(InputErrorType.NEED_PRODUCT_COUNT_WITHIN_STOCK.getMessage()) ||
                        e.getMessage().contains(InputErrorType.NEED_EXISTING_PRODUCT.getMessage())) {
                    skipStartMessage = true;
                }
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

    private void printTotalReceipt(Receipt receipt) {
        outputView.printReceiptStart();
        printProductDetails(receipt);
        outputView.startPrintBonusProduct();
        printBonusProductDetails(receipt);
        outputView.printDividingLine();
        receipt.printFinalReceipt();
    }


    private void printProductDetails(Receipt receipt) {
        List<String> productDetails = receipt.getProductDetails();
        int groupSize = 3;
        StringBuilder output = new StringBuilder();

        NumberFormat numberFormat = NumberFormat.getInstance();

        for (int i = 0; i < productDetails.size(); i++) {
            String detail = productDetails.get(i);

            if ((i + 1) % groupSize == 0) {
                try {
                    int number = Integer.parseInt(detail);
                    output.append(numberFormat.format(number));
                } catch (NumberFormatException e) {
                    output.append(detail);
                }
                output.append(newline);
            }

            if ((i + 1) % groupSize != 0 && i != productDetails.size() - 1) {
                output.append(detail).append("    ");
            }
        }

        System.out.print(output);
    }


    private void printBonusProductDetails(Receipt receipt) {
        List<String> bonusProductDetails = receipt.getBonusProductDetails();
        int groupSize = 2;
        StringBuilder output = new StringBuilder();

        NumberFormat numberFormat = NumberFormat.getInstance();

        for (int i = 0; i < bonusProductDetails.size(); i++) {
            String detail = bonusProductDetails.get(i);

            if ((i + 1) % groupSize == 0) {
                try {
                    int number = Integer.parseInt(detail);
                    output.append(numberFormat.format(number));
                } catch (NumberFormatException e) {
                    output.append(detail);
                }
                output.append(newline);
            }

            if ((i + 1) % groupSize != 0 && i != bonusProductDetails.size() - 1) {
                output.append(detail).append("            ");
            }
        }

        System.out.print(output);
    }
}
