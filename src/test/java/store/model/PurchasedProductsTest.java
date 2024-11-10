package store.model;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.Test;

class PurchasedProductsTest {

    @Test
    void 형식이_맞지_않으면_예외가_발생한다() {
        assertThatThrownBy(() -> new PurchasedProducts(List.of("사이다,3", "감자칩-1")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 구매_수량이_0개_이하이면_예외가_발생한다() {
        assertThatThrownBy(() -> new PurchasedProducts(List.of("사이다-3", "감자칩-0")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 구매_수량이_숫자가_아니면_예외가_발생한다() {
        assertThatThrownBy(() -> new PurchasedProducts(List.of("사이다-세개", "감자칩-0")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 상품명이_입력되지_않으면_예외가_발생한다() {
        assertThatThrownBy(() -> new PurchasedProducts(List.of("-3")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 수량이_입력되지_않으면_예외가_발생한다() {
        assertThatThrownBy(() -> new PurchasedProducts(List.of("사이다-")))
                .isInstanceOf(IllegalArgumentException.class);
    }


/*
    @Test
    void 생성자_테스트() {
        PurchasedProducts purchasedProducts = new PurchasedProducts(List.of("[사이다-3]","[감자칩-1]"));
        List<Integer> expectedLotto = List.of(1, 2, 3, 4, 5, 6);

        assertEquals(lotto.getLottoNumbers(), expectedLotto);
    }


 */
}
