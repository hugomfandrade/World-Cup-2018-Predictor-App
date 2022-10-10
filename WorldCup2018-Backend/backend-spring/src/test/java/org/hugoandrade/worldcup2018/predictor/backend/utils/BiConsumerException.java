package org.hugoandrade.worldcup2018.predictor.backend.utils;


import java.util.function.BiConsumer;

@FunctionalInterface
public
interface BiConsumerException<T, U> extends BiConsumer<T, U> {

    /**
     * Performs this operation on the given arguments.
     *
     * @param t the first input argument
     * @param u the second input argument
     */
    void run(T t, U u) throws Exception;

    /**
     * Performs this operation on the given arguments.
     *
     * @param t the first input argument
     * @param u the second input argument
     */
    default void accept(T t, U u) {
        try {
            this.run(t, u);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}