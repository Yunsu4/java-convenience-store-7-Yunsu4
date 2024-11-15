package store;


import store.controller.FrontController;
import store.controller.ProductController;
import store.controller.PromotionController;
import store.view.InputView;
import store.view.OutputView;


public class Application {


    public static void main(String[] args) {
        InputView inputView = new InputView();
        OutputView outputView = new OutputView();
        ProductController productController = new ProductController();
        PromotionController promotionController = new PromotionController(inputView, productController);

        FrontController frontController = new FrontController(inputView, outputView, productController,
                promotionController);
        frontController.runFrontController();

    }

}
