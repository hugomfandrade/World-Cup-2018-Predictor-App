package org.hugoandrade.worldcup2018.predictor.backend.utils;

import org.junit.jupiter.api.Assertions;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.IntStream;

import static org.hugoandrade.worldcup2018.predictor.backend.utils.QuickParserUtils.parseList;

public class ListResultMatchers<T> {

    public static  <T> ListResultMatchers<T> list(Class<T> clazz) {
        return new ListResultMatchers<T>(clazz);
    }

    private final Class<T> clazz;

    public ListResultMatchers(Class<T> clazz) {
        this.clazz = clazz;
    }

    public ResultMatcher equals(List<T> o, Function<List<T>, Object> function) {
        return result -> Assertions.assertEquals(
                function.apply(o),
                function.apply(parseList(result, clazz)));
    }

    public ResultMatcher equalsValue(Object o, Function<List<T>, Object> function) {
        return result -> Assertions.assertEquals(o, function.apply(parseList(result, clazz)));
    }

    public ResultMatcher notNull(Function<List<T>, Object> function) {
        return result -> Assertions.assertNotNull(function.apply(parseList(result, clazz)));
    }

    public ResultMatcher addDo(Consumer<List<T>> consumer) {
        return result -> consumer.accept(parseList(clazz, result));
    }

    public ResultMatcher assertSize(int expectedSize) {
        return result -> Assertions.assertEquals(expectedSize, parseList(clazz, result).size());
    }

    public ResultMatcher assertEquals(List<T> expectedList) {
        return result -> {
            List<T> o = parseList(clazz, result);
            Assertions.assertEquals(expectedList.size(), o.size());
            Assertions.assertArrayEquals(expectedList.toArray(), o.toArray());
        };
    }

    public ResultMatcher assertEquals(List<T> expectedList, Comparator<T> sorter, Comparator<T> comparator) {

        return result -> {
            List<T> actualList = parseList(clazz, result);

            actualList.sort(sorter);
            expectedList.sort(sorter);

            Assertions.assertTrue(expectedList.size() == actualList.size() &&
                    IntStream.range(0, expectedList.size())
                            .allMatch(i -> comparator.compare(expectedList.get(i), actualList.get(i)) == 0));
        };
    }

    public ResultMatcher assertEquals(List<T> expectedList, Comparator<T> sorter) {

        return result -> {
            List<T> actualList = parseList(clazz, result);

            actualList.sort(sorter);
            expectedList.sort(sorter);

            Assertions.assertEquals(expectedList, actualList);
        };
    }
}