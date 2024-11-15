package store.model;

import java.util.Map;
import store.view.error.ErrorException;

public class Product {

    private Map<String, Object> product;

    public Product(Map<String, Object> product) {
        this.product = product;
    }

    public void display() {
        for (String key : product.keySet()) {
            String value = String.valueOf(product.get(key));

            if (key.equals("name")) {
                System.out.print("- " + value + " ");
            }
            if (key.equals("price")) {
                int parsedValue = Integer.parseInt(value);
                String reformattedValue = String.format("%,d", parsedValue);
                System.out.print(reformattedValue + "원 ");
            }
            if (key.equals("quantity")) {
                if (value.equals("0")) {
                    System.out.print("재고 없음 ");
                }
                if (!value.equals("0")) {
                    System.out.print(value + "개 ");
                }
            }
            if (key.equals("promotion")) {
                if (value.equals("null")) {
                    System.out.println();
                }
                if (!value.equals("null")) {
                    System.out.println(value);
                }
            }
        }
    }

    public int parseQuantity() {
        Object value = product.get("quantity");
        if (String.valueOf(value).equals("null")) {
            return 0;
        }

        return Integer.parseInt(String.valueOf(value));
    }

    public String getValueOfTheField(String field) {
        Object value = product.get(field);
        return String.valueOf(value);
    }

    public void addQuantity() {
        Object quantityValue = product.get("quantity");

        int currentQuantity = getCurrentQuantity(quantityValue);
        product.put("quantity", currentQuantity + 1);
    }


    public void decreaseQuantity(int promotableQuantity) {
        Object quantityValue = product.get("quantity");
        int currentQuantity = getCurrentQuantity(quantityValue);
        product.put("quantity", currentQuantity - promotableQuantity);
    }


    private static int getCurrentQuantity(Object quantityValue) {
        int currentQuantity = 0;
        if (!(quantityValue instanceof String) && !(quantityValue instanceof Integer)) {
            throw new ErrorException("파일이 잘못 되었습니다.");
        }
        if (quantityValue instanceof String) {
            currentQuantity = Integer.parseInt((String) quantityValue);
        }
        if (quantityValue instanceof Integer) {
            currentQuantity = (Integer) quantityValue;
        }
        return currentQuantity;
    }
}
