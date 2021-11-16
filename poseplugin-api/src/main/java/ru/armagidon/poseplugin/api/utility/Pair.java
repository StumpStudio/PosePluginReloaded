package ru.armagidon.poseplugin.api.utility;

import java.util.function.Function;

public record Pair<F, S>(F first, S second) {

    public static <F, S> Pair<F, S> of(F first, S second) {
        return new Pair<>(first, second);
    }

    public <Fp, Sp> Pair<Fp, Sp> map(Function<F, Fp> firstMapper, Function<S, Sp> secondMapper) {
        return new Pair<>(firstMapper.apply(first), secondMapper.apply(second));
    }
}
