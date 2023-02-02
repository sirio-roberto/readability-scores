package readability;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    static int numOfWords = 0;
    static int numOfSentences = 0;
    static int numOfChars = 0;
    static int numOfSyllables = 0;
    static int numOfPolysyllables = 0;

    public static void main(String[] args) {
        try (BufferedReader br = new BufferedReader(new FileReader(args[0]))) {
            System.out.println(getTextProperties(br.lines().collect(Collectors.toList())));

            System.out.print("Enter the score you want to calculate (ARI, FK, SMOG, CL, all): ");
            String chosenScoreType = new java.util.Scanner(System.in).nextLine();
            System.out.println();

            printScore(chosenScoreType);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }

    private static void printScore(String chosenScoreType) {
        double score = calculateARI();
        int roundedScore = (int) Math.ceil(score);
        int approximateAge = getApproximateAge(roundedScore);
        int totalAge = approximateAge;
        String ARI = String.format("Automated Readability Index: %.2f (about %d-year-olds).",
                score, approximateAge);

        score = calculateFleschKincaid();
        roundedScore = (int) Math.ceil(score);
        approximateAge = getApproximateAge(roundedScore);
        totalAge += approximateAge;
        String FK = String.format("Flesch–Kincaid readability tests: %.2f (about %d-year-olds).",
                score, approximateAge);

        score = calculateGobbledygook();
        roundedScore = (int) Math.ceil(score);
        approximateAge = getApproximateAge(roundedScore);
        totalAge += approximateAge;
        String SMOG = String.format("Simple Measure of Gobbledygook: %.2f (about %d-year-olds).",
                score, approximateAge);

        score = calculateColemanLiau();
        roundedScore = (int) Math.ceil(score);
        approximateAge = getApproximateAge(roundedScore);
        totalAge += approximateAge;
        String CL = String.format("Coleman–Liau index: %.2f (about %d-year-olds).",
                score, approximateAge);

        switch (chosenScoreType) {
            case "ARI" -> System.out.println(ARI);
            case "FK" -> System.out.println(FK);
            case "SMOG" -> System.out.println(SMOG);
            case "CL" -> System.out.println(CL);
            default -> {
                System.out.println(ARI);
                System.out.println(FK);
                System.out.println(SMOG);
                System.out.println(CL);
                System.out.println();
                double avgAge = ((double) totalAge) / 4.0;
                System.out.printf("This text should be understood in average by %.2f-year-olds.%n", avgAge);
            }
        }
    }

    private static String getTextProperties(List<String> userText) {
        StringBuilder sb = new StringBuilder("The text is:\n");
        for (String line: userText) {
            sb.append(line);
            String[] sentences = line.split("[.!?]");
            numOfSentences += sentences.length;
            String[] words = line.split("\\s");
            numOfWords += words.length;
            for (String word: words) {
                word = word.replaceAll("[.!?,]", "");
                word = word.replaceAll("[eE]\\b", "s");
                word = word.replaceAll("[aeiouyAEIOUY]{2,}", "a");
                if (!word.matches("\\w*[aeiouyAEIOUY]\\w*")) {
                    numOfSyllables++;
                } else {
                    // to check Polysyllables
                    int auxNumOfSyl = 0;
                    for(char c: word.toCharArray()) {
                        if (String.valueOf(c).matches("[aeiouyAEIOUI]")) {
                            auxNumOfSyl++;
                            numOfSyllables++;
                        }
                    }
                    if (auxNumOfSyl > 2) {
                        numOfPolysyllables++;
                    }
                }
            }
            String characters = line.replaceAll("\\s", "");
            numOfChars += characters.length();
        }
        sb.append("\n");
        sb.append("\nWords: ").append(numOfWords);
        sb.append("\nSentences: ").append(numOfSentences);
        sb.append("\nCharacters: ").append(numOfChars);
        sb.append("\nSyllables: ").append(numOfSyllables);
        sb.append("\nPolysyllables: ").append(numOfPolysyllables);
        return sb.toString();
    }

    private static int getApproximateAge(int roundedScore) {
        return roundedScore == 14 ? roundedScore + 8 : roundedScore + 5;
    }

    static private double calculateARI() {
        return 4.71 * ((double) numOfChars / numOfWords) + 0.5 * ((double) numOfWords / numOfSentences) - 21.43;
    }


    static private double calculateFleschKincaid() {
        return 0.39 * ((double) numOfWords / numOfSentences) + 11.8 * ((double) numOfSyllables / numOfWords) - 15.59;
    }

    static private double calculateGobbledygook() {
        return 1.043 * Math.sqrt(numOfPolysyllables * (30.0 / numOfSentences)) + 3.1291;
    }

    static private double calculateColemanLiau() {
        double L = (double) numOfChars / numOfWords * 100;
        double S = (double) numOfSentences / numOfWords * 100;
        return 0.0588 * L - 0.296 * S - 15.8;
    }

}
