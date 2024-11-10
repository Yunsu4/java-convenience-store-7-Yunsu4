package store.view;

public class OutputView {

    private static final String newline = System.getProperty("line.separator");

    public void startMessage(){
        System.out.println("안녕하세요. W편의점입니다."+newline
                + "현재 보유하고 있는 상품입니다."+newline);
    }

    public void printReceiptStart(){
        System.out.println("==============W 편의점================"
                +newline+"상품명        수량    금액");
    }

    public void startPrintBonusProduct(){
        System.out.println("=============증    정===============");
    }

    public void printDividingLine(){
        System.out.println("====================================");
    }


}
