import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.*;

public class Main {

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    static HashMap<Integer, ArrayList<String>> hashMapObj = new HashMap<>();

    public static void main(String[] args) throws InterruptedException {

        Scanner scanner = new Scanner(System.in);  // Create a Scanner object
        System.out.println("Please state your choice... UPPER case or lower case (U or L):");
        String caseType = scanner.nextLine();  // Read user input

        System.out.println("Please state your choice...\n" +
                "Color of characters (R or Y):");
        String color = scanner.nextLine();  // Read user input

        System.out.println("Please state your choice...\n" +
                "How many characters to shift (number between 1-3): ");
        int shiftNumber = scanner.nextInt();  // Read user input

        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream("./textfile.txt"),
                            Charset.forName("UTF-8")));
            int c;
            int charCounter = 1;
            while ((c = reader.read()) != -1) {
                char character = (char) c;
                ArrayList<String> values = new ArrayList<>();
                String originalLetter = String.valueOf(character);
                values.add(originalLetter);
                hashMapObj.put(charCounter, values);
                charCounter++;
            }

        } catch (IOException e) {
            e.printStackTrace(); // File couldn't found
        }

        Thread threadChangeCase = new Thread((new ChangeCaseType(hashMapObj, caseType)));
        threadChangeCase.start();
        //threadChangeCase.join();

        Thread threadShiftLetters = new Thread((new ShiftLetters(hashMapObj, shiftNumber)));
        threadShiftLetters.start();
        //threadShiftLetters.join();

        Thread threadChangeColor = new Thread(new ChangeColor(color, hashMapObj));
        threadChangeColor.start();
        //threadChangeColor.join();

        printHashMap(0, "ORIGINAL HASHMAP");

        printHashMap(1, "AFTER CASE CHANGED");

        printHashMap(2, "AFTER SHIFTED");

        printHashMap(3, "AFTER COLORED");

    }

    public static void printHashMap(int elementIndex, String message) {

        synchronized (hashMapObj) {
            System.out.println("_________" + message + "____________");
            hashMapObj.forEach((index, arrayList) -> {
                System.out.print(arrayList.get(elementIndex));
            });
            System.out.println("");
        }
    }

    public static class ChangeColor implements Runnable {
        private String color;
        HashMap<Integer, ArrayList<String>> hashMap;

        public ChangeColor(String color, HashMap<Integer, ArrayList<String>> map) {
            this.color = color;
            this.hashMap = map;
        }

        @Override
        public void run() {
            synchronized (hashMap) {
                hashMap.forEach((index, arrayList) -> {
                    if (color.equals("r") || color.equals("R")) {
                        String letter = arrayList.get(0);
                        letter = new String(ANSI_RED + letter + ANSI_RESET);
                        arrayList.add(letter);
                    } else if (color.equals("y") || color.equals("Y")) {
                        String letter = arrayList.get(0);
                        letter = new String(ANSI_YELLOW + letter + ANSI_RESET);
                        arrayList.add(letter);
                    }
                });
            }

        }
    }

    public static class ShiftLetters implements Runnable {

        private int shiftNumber;
        HashMap<Integer, ArrayList<String>> hashMap;

        int getShiftNumber() {
            return shiftNumber;
        }

        HashMap getHashMap() {
            return hashMap;
        }

        public ShiftLetters(HashMap<Integer, ArrayList<String>> map, int shiftNumber) {
            this.hashMap = map;
            this.shiftNumber = shiftNumber;
        }

        @Override
        public void run() {
            synchronized (hashMap) {
                hashMap.forEach((index, arrayList) -> {
                    int asciiCode = (int) arrayList.get(0).charAt(0) + shiftNumber;
                    char shiftedLetter = (char) asciiCode;
                    arrayList.add(String.valueOf(shiftedLetter));
                    // System.out.println(arrayList.get(0)+ " shifted-> " + String.valueOf(shiftedLetter));
                });
            }
        }
    }

    public static class ChangeCaseType implements Runnable {

        private String caseType;
        HashMap<Integer, ArrayList<String>> hashMap;

        String caseType() {
            return caseType;
        }

        HashMap getHashMap() {
            return hashMap;
        }

        public ChangeCaseType(HashMap<Integer, ArrayList<String>> map, String caseType) {
            this.hashMap = map;
            this.caseType = caseType;
        }

        @Override
        public void run() {
            synchronized (hashMap) {
                hashMap.forEach((index, arrayList) -> {
                    if (caseType.equals("l") || caseType.equals("L")) {
                        arrayList.add(arrayList.get(0).toLowerCase());
                    } else if (caseType.equals("U") || caseType.equals("u")) {
                        arrayList.add(arrayList.get(0).toUpperCase());
                    }
                });
            }
        }
    }


}

