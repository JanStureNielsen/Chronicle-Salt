package net.openhft.chronicle.salt;

import java.util.stream.IntStream;

import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.bytes.BytesStore;
import net.openhft.chronicle.core.Jvm;

public class SHA256PerfMain {
    static final int LENGTH = Integer.getInteger("length", 55);

    public static void main(String[] args) {

        ThreadLocal<Bytes<?>> hashBytes = ThreadLocal.withInitial(() -> Bytes.allocateDirect(SHA2.HASH_SHA256_BYTES));
        BytesStore<?, ?> bytes = Ed25519.generateRandomBytes(LENGTH);
        BytesStore<?, ?> bytes2 = Ed25519.generateRandomBytes(LENGTH);

        for (int t = 0; t < 10; t++) {
            int runs = 10_000_000;
            long start = System.nanoTime();
            IntStream.range(0, runs).parallel().forEach(i -> {
                Bytes<?> hash256 = hashBytes.get();
                hash256.writePosition(0);
                SHA2.sha256(hash256, bytes);
                hash256.writePosition(0);
                SHA2.sha256(hash256, bytes2);
            });
            long time = System.nanoTime() - start;
            System.out.printf("Throughput: %,d hashes per second%n", (long) ((2 * runs * 1e9) / time));
            Jvm.pause(100);
        }
    }
}