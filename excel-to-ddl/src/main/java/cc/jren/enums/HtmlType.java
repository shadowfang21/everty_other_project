package cc.jren.enums;

import java.util.Arrays;
import java.util.function.Predicate;

import cc.jren.model.HtmlColumn;

public enum HtmlType {
    
    CHECK(typeStartsWith("CHECK")),
    RADIO(typeStartsWith("RADIO")),
    DROP(typeStartsWith("下拉選單")),
    NUMBER(typeStartsWith("數字")),
    DATE(typeStartsWith("日期")),
    TEXT(c -> true);
    
    
    private Predicate<HtmlColumn> checker;

    private HtmlType(Predicate<HtmlColumn> checker) {
        this.checker = checker;
    }
    
    private static Predicate<HtmlColumn> typeStartsWith(String text) {
        return c -> c.getType().startsWith(text);
    }

    public Predicate<HtmlColumn> getChecker() {
        return checker;
    }
    
    public static HtmlType find(HtmlColumn col) {
        return Arrays.stream(HtmlType.values())
            .filter(c -> c.getChecker().test(col))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("html type not fund : " + col));
    }
}