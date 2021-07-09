package cypt;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Timer;
import java.util.TimerTask;

class Test {

    // preformance value
    private static long max;
    private static long previousMax;
    private static String key;

    private static long length;
    private static String algorithm;
    private static String target;
    private static String alphabet;

    private static long start = 0;
    private static long end = 0;
    private static int base = 26;

    public static void main(String[] args) {

        length = 6;
        algorithm = "sha256";
        target = "27A77F49FD908494422C415F88D61663859E0E119500796293B1D1E2F3355C80".toLowerCase();
        alphabet = "abcdefghijklmnopqrstuvwxyz0123456789";
        base = alphabet.length();

        for (long i = 0; i < length; i++) {
            double s = 1 * Math.pow(base, i);
            double e = base * Math.pow(base, i);
            start += s;
            end += e;
        }

        max = end - start;
        System.out.println("The total length of dictionary is : " + max);
        long startTimestamp = System.currentTimeMillis();
        // monitor the encyption
        Timer timer = new Timer();
        previousMax = max;
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                // calculate the preformance
                double speed = (previousMax - max) / 10;
                previousMax = max;
                System.out.printf("Time now is %s still %d to encrypt in %f/s%n", LocalDateTime.now(), max, speed);
            }
        }, 10000, 10000);
        // start to encrypt
        long chunk = max / Runtime.getRuntime().availableProcessors();
        for (long i = start; i < end; i += chunk) {
            long finalI = i;
            // multiple thread to encryth the key
            new Thread(() -> {
                for (long n = finalI; n < Math.min(end, finalI + chunk); n++) {
                    max--;
                    key = convertToBase(n);
                    String key_hashed = SHA256(key);
                    if (key_hashed.equals(target)) {
                        // difine the anwser
                        long ans = n;
                        String ans_string = convertToBase(ans);
                        System.out.printf("Found the anwser at '%s' with hash '%s'%n", ans_string, SHA256(ans_string));
                        System.out.println("Finished decyption");
                        System.out.println("Using " + ((System.currentTimeMillis() - startTimestamp) / 1000) + "s");
                        // stop the thread and timer
                        timer.cancel();
                        System.exit(0);
                    }
                }
            }).start();
        }
    }

    // encryption
    public static String SHA256(String message) {

        MessageDigest md;
        byte[] bytes;
        StringBuilder sb = new StringBuilder();
        try {
            md = MessageDigest.getInstance(algorithm);
            md.update(message.getBytes());
            bytes = md.digest();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return sb.toString();
    }

    public static String convertToBase(long dec) {
        StringBuffer buffer = new StringBuffer();
        while (dec != 0) {
            // start with 1 and end with base number
            long remainder = dec % base == 0 ? base : dec % base;
            // dictonary with non-sequecy alphatbet
            char res = (char) alphabet.charAt((int) remainder - 1);
            buffer.insert(0, res);
            // confirm that the dec equal to 0 when end
            dec = (dec - remainder) / base;
        }
        return buffer.toString();
    }
}