package com.mathassistant;

import java.io.FileOutputStream;
import java.io.IOException;

public class FibTestRunner {
    public static void main(String[] args) {
        try {
            FibonacciCurve gen = new FibonacciCurve();
            byte[] png = gen.generateCurve(12, 800);
            try (FileOutputStream fos = new FileOutputStream("target/fib_test.png")) {
                fos.write(png);
            }
            System.out.println("Wrote target/fib_test.png (size=" + png.length + ")");
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(2);
        }
    }
}
