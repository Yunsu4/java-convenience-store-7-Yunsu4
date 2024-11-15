package store.view.error;

public enum InputErrorType {

    ERROR_MESSAGE("[ERROR] "),
    NEED_AVAILABLE_FORMAT("올바르지 않은 형식으로 입력했습니다. 다시 입력해 주세요."),
    NEED_EXISTING_PRODUCT("존재하지 않는 상품입니다. 다시 입력해 주세요."),
    NEED_PRODUCT_COUNT_WITHIN_STOCK("재고 수량을 초과하여 구매할 수 없습니다. 다시 입력해 주세요."),
    NEED_AVAILABLE_INPUT("잘못된 입력입니다. 다시 입력해 주세요.");

    final String message;

    InputErrorType(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
