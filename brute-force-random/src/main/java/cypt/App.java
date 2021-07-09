package cypt;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import org.apache.commons.cli.*;

public class App {

    // preformance value
    private static String key;

    private static int length;
    private static String algorithm;
    private static String target;

    public static void main(String[] args) throws Exception {

        // define the option of decode and encode
        Options options = new Options();

        // -accept sha1 sha256 md1
        options.addOption("a", "algorithm", true, "what decode algorithm using");
        options.addOption("l", "length", true, "length of the codeword");
        options.addOption("t", "target", true, "target you want to decrypt");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);
        Random random = new Random();

        if (cmd.hasOption("a") && cmd.hasOption("t") && cmd.hasOption("l")) {
            algorithm = cmd.getOptionValue("a");
            target = cmd.getOptionValue("t");
            length = Integer.parseInt(cmd.getOptionValue("l"));

            // start to encrypt
            long start = System.currentTimeMillis();
            int cpuThread = Runtime.getRuntime().availableProcessors();
            boolean foundAns = false;

            while (!foundAns) {
                // set worker job chunk
                for (int i = 0; i < cpuThread; i++) {
                    // multiple thread to encryth the key
                    new Thread(() -> {
                        key = random.ints((int) 'a', (int) 'z' + 1).limit(length)
                                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                                .toString();
                        String key_hashed = SHA256(key);
                        // System.out.println(key_hashed);
                        if (key_hashed.equals(target)) {
                            String ans = key;
                            System.out.printf("Found the anwser at '%s' with hash '%s'%n", ans, SHA256(ans));
                            System.out.printf("Finished decyption. Using %dms", System.currentTimeMillis() - start);

                            // stop the thread and timer
                            System.exit(0);
                        }
                    }).start();
                }
            }

        } else {
            // print the help
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("Brute Force", options);
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
}
