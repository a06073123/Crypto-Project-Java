package cypt;

public class test4 {
    public static String alphabet;
    public static String password;
    public static int base;

    public static void main(String[] args) {
        alphabet = "abcdefghijklmnopqrstuvwxyz";
        base = alphabet.length();
        // System.out.printf("dec value=%d password=%s\n", getDecimal(password),
        // password);
        System.out.printf("dec value=%d password=%s\n", 141071531, convertToBase(141071531));
        System.out.printf("dec value=%d password=%s\n", 153943021, convertToBase(153943021));
    }

    public static long getDecimal(String password) {
        long number = 0;
        int power = password.length() - 1;
        for (int i = 0; i < password.length(); i++) {
            double s = (alphabet.indexOf(password.charAt(i)) + 1) * Math.pow(base, power--);
            number += s;
        }
        return number;
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
