package org.leovegas.wallet;

import java.util.Random;

public class TestUtils {
    public static int generatePlayerId() {
        return new Random().nextInt(0, Integer.MAX_VALUE);
    }

    public static int generateTransactionId() {
        return new Random().nextInt(0, Integer.MAX_VALUE);
    }
}
