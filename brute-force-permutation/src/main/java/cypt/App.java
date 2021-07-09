package cypt;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.*;

import org.apache.commons.cli.*;

public class App {

    // preformance value
    private static int max;
    private static int previousMax;
    private static String key;
    private static int ans_index;

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

        // abcdefghijklmnopqrstuvwxyz
        List<String> alphabet = Arrays.asList("abcdefghijklmnopqrstuvwxyz".split(""));

        if (cmd.hasOption("a") && cmd.hasOption("t") && cmd.hasOption("l")) {
            algorithm = cmd.getOptionValue("a");
            target = cmd.getOptionValue("t");
            length = Integer.parseInt(cmd.getOptionValue("l"));
            // build the permutation list
            List<Object> alpha = permutation(alphabet, length);

            max = alpha.size();
            System.out.println("The total length of dictionary is : " + max);

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
            }, 0, 10000);
            // start to encrypt

            // set worker job chunk
            int chunk = alpha.size() / Runtime.getRuntime().availableProcessors();
            for (int i = 0; i < alpha.size(); i += chunk) {
                int finalI = i;
                // multiple thread to encryth the key
                new Thread(() -> {
                    for (int n = finalI; n < Math.min(alpha.size(), finalI + chunk); n++) {
                        max--;
                        key = alpha.get(n).toString();
                        String key_hashed = SHA256(key);
                        if (key_hashed.equals(target)) {
                            // difine the anwser
                            ans_index = n;

                            String ans = alpha.get(ans_index).toString();
                            System.out.printf("Found the anwser at '%s' with hash '%s'%n", ans, SHA256(ans));
                            System.out.println("Finished decyption");

                            // stop the thread and timer
                            timer.cancel();
                            System.exit(0);
                        }
                    }
                }).start();
            }

        } else {
            // print the help
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("Brute Force", options);
        }
    }

    // permutation (include repeat)
    public static List<Object> permutation(List<String> list, int length) {
        Stream<String> stream = list.stream();
        for (int i = 1; i < length; i++) {
            stream = stream.flatMap(str -> list.stream().map(str::concat));
        }
        return stream.collect(Collectors.toList());
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
