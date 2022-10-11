package org.hugoandrade.worldcup2018.predictor.backend.utils;

import org.junit.jupiter.api.Assertions;
import org.springframework.test.web.servlet.ResultMatcher;

import static org.hugoandrade.worldcup2018.predictor.backend.utils.QuickParserUtils.parse;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class ObjResultMatchers<T> {

    public static <T> ObjResultMatchers<T> obj(Class<T> clazz) {
        return new ObjResultMatchers<T>(clazz);
    }

    private final Class<T> clazz;

    public ObjResultMatchers(Class<T> clazz) {
        this.clazz = clazz;
    }

    public ResultMatcher equals(T o, Function<T, Object> function) {
        return result -> Assertions.assertEquals(
                function.apply(o),
                function.apply(parse(result, clazz)));
    }

    public ResultMatcher assertEquals(T o) {
        return result -> Assertions.assertEquals(o, parse(result, clazz));
    }

    public ResultMatcher equalsValue(Object o, Function<T, Object> function) {
        return result -> Assertions.assertEquals(o, function.apply(parse(result, clazz)));
    }

    public ResultMatcher notNull(Function<T, Object> function) {
        return result -> Assertions.assertNotNull(function.apply(parse(result, clazz)));
    }

    public ResultMatcher addDo(Consumer<T> consumer) {
        return result -> consumer.accept(parse(result, clazz));
    }
}