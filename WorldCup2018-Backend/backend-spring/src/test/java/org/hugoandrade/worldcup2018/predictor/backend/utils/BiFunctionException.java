package org.hugoandrade.worldcup2018.predictor.backend.utils;


import java.util.function.BiFunction;

@FunctionalInterface
public interface BiFunctionException<T, U, R> extends BiFunction<T, U, R> {

    /**
     * Performs this operation on the given arguments.
     *
     * @param t the first input argument
     * @param u the second input argument
     */
    R run(T t, U u) throws Exception;

    /**
     * Performs this operation on the given arguments.
     *
     * @param t the first input argument
     * @param u the second input argument
     */
    default R apply(T t, U u) {
        try {
            return this.run(t, u);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}