package store.controller;

import store.model.Receipt;
import store.view.InputView;

public class MembershipController {

    private InputView inputView;

    public MembershipController(InputView inputView) {
        this.inputView = inputView;
    }

    public void execute(Receipt receipt) {
        String membershipDiscount = inputView.readMembershipDiscount();

        if (membershipDiscount.equals("Y")) {
            receipt.executeMembership();
        }
        receipt.printFinalReceipt();
    }
}
