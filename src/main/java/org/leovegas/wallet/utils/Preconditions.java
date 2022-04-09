package org.leovegas.wallet.utils;

import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public final class Preconditions {
    public static void require(boolean value, Supplier<String> lazyMessage ) {
        if (!value) {
            throw new IllegalArgumentException(lazyMessage.get());
        }
    }

    public static <T> T requireNotNull(@Nullable T value, Supplier<String> lazyMessage ) {
        if (value == null) {
            throw new IllegalArgumentException(lazyMessage.get());
        } else {
            return value;
        }
    }
}
