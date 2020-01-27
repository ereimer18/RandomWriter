import javax.sound.midi.SysexMessage;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * RandomWriter describes a procedural text generator.
 *
 * This program performs a character-based analysis of
 * the provided input text(s) to build a language model,
 * and then generates output text based on the probability
 * of a character appearing next, given a prefix sequence of k characters.
 *
 * @author ereimer18@georgefox.edu
 */
public class RandomWriter {

    private static final int MIN_ARGS = 3;


    /**
     * Adds a sequence of characters and a following character
     * to the HashMap.
     *
     * If the sequence of characters exists as a key,
     * the following character is added as a value.
     * Else, a new key is created with the following character
     * as a value.
     *
     * @return the updated HashMap.
     */
    private static HashMap add(HashMap<String, ArrayList<String>> repo, ArrayList<String> key, String character) {
        String sequence = "";
        for (String e : key)  {
            sequence = sequence + e;
        }

        if (repo.containsKey(sequence)) {
            repo.get(sequence).add(character);
        }
        else {
            ArrayList<String> chars = new ArrayList<>();
            chars.add(character);
            repo.put(sequence, chars);
        }
        return repo;
    }


    /**
     * Returns the prefix length.
     *
     * Parses the prefix length from the command-line argument.
     * If the prefix is less than 1, the program exits with a status of 1;
     * else the prefix length is returned.
     *
     * @param prefix a String argument taken from the command-line.
     * @return the prefix length.
     */
    private static int instantiatePrefixLength(String prefix) {
        int prefixLength = -1;
        try {
            prefixLength = Integer.parseInt(prefix);
        }
        catch (NumberFormatException e) {
            System.exit(1);
        }
        if (prefixLength < 1) {
            System.exit(1);
        }
        return prefixLength;
    }


    /**
     * Returns the output length.
     *
     * Parses the output length from the command-line argument.
     * If the output is less than 0, the program exits with a status of 1;
     * else the output length is returned.
     *
     * @param output a String argument taken from the command-line.
     * @return the output length.
     */
    private static int instantiateOutputLength(String output) {
        int outputLength = -1;
        try {
            outputLength = Integer.parseInt(output);
        }
        catch (NumberFormatException e) {
            System.exit(1);
        }
        if (outputLength < 0) {
            System.exit(1);
        }
        return outputLength;
    }


    /**
     * Read a text file and updates a HashMap.
     *
     * Reads through a text file and writes its
     * character sequences into a HashMap.
     * This HashMap is returned.
     *
     * @param repo the HashMap to write sequences into.
     * @param txtFile the text file to read.
     * @param prefixLength the number of characters in a sequence.
     * @return the updated HashMap.
     */
    private static HashMap<String, ArrayList<String>> read(HashMap<String, ArrayList<String>> repo,
                                                           String txtFile, int prefixLength) {
        String currentLine;
        String txt = "";
        String character = "";
        ArrayList<String> tmpList = new ArrayList<>(prefixLength + 1);

        for (int i = 0; i < prefixLength; i++) {
            tmpList.add(null);
        }

        try {
            char[] chars;
            BufferedReader txtReader = new BufferedReader(new FileReader(txtFile));
            while ((currentLine = txtReader.readLine()) != null) {
                txt += currentLine;
            }
            chars = txt.toCharArray();
            for (int i = 0; i < chars.length - prefixLength; i++) {
                for (int j = 0; j < prefixLength; j++) {
                    tmpList.set(j, chars[i + j]  +  "");
                }
                character = chars[i + prefixLength] + "";
                add(repo, tmpList, character);
            }
        }
        catch (IOException e) {
            System.exit(1);
        }

        return repo;
    }


    /**
     * Writes a randomly generated text.
     *
     * Uses the HashMap of character sequences to randomly generate text.
     * The number of characters to write is the output length specified
     * in the command-line arguments.
     *
     * @param prefixLength the length of each sequence.
     * @param outputLength the number of characters to generate.
     * @param repo the HashMap to pull sequences and values from.
     */
    private static void write(int prefixLength, int outputLength, HashMap<String, ArrayList<String>> repo) {

        String value;
        String sequence;
        Random rand = new Random();
        int randInt = rand.nextInt(repo.size());
        ArrayList<String> values;
        sequence = startWrite(repo, randInt);
        System.out.print(sequence);

        for (int i = 0; i <= outputLength - prefixLength; i++) {
            try {
                values = repo.get(sequence);
                randInt = rand.nextInt(values.size());
                value = values.get(randInt);
                System.out.print(value);
                sequence = nextSequence(sequence, value);
            }
            catch (NullPointerException e) {
                System.exit(2);
            }
        }
    }


    /**
     * Returns a randomly selected key of the specified HashMap.
     *
     * Randomly selects one of the String sequences in the HashMap
     * to use as the starting sequence.
     *
     * @param repo the specified HashMap containing valid keys.
     * @param randInt a randomly generated integer.
     * @return a randomly selected key of the specified HashMap.
     */
    private static String startWrite(HashMap<String, ArrayList<String>> repo, int randInt) {
        String[] keys = new String[repo.size()];
        keys = repo.keySet().toArray(keys);
        return keys[randInt];
    }


    /**
     * Given a value, returns the sequence with the 0th element dropped
     * and the value appended.
     *
     * Breaks the sequence into a character array.
     * The sequence is rebuilt with the 0th element dropped.
     * The value is then appended and the new sequence is returned.
     *
     * @param sequence the current sequence of characters.
     * @param value the character to append.
     * @return the sequence with the 0th element dropped and the value appended.
     */
    private static String nextSequence(String sequence, String value) {
        char[] chars = sequence.toCharArray();
        sequence = "";

        for (int i = 1; i < chars.length; i++) {
            sequence += chars[i];
        }
        return sequence.concat(value);
    }


    /**
     * The main method of the RandomWriter.
     *
     * @param args the command-line arguments for a random writer.
     */
    public static void main(String[] args) {
        HashMap<String, ArrayList<String>>  repository = new HashMap<>();
        int prefixLength;
        int outputLength;
        String txtInputOne;
        String txtInputTwo;

        if (args.length < MIN_ARGS) {
            System.exit(1);
        }

        prefixLength = instantiatePrefixLength(args[0]);
        outputLength = instantiateOutputLength(args[1]);

        if (prefixLength > outputLength) {
            System.exit(2);
        }

        for (int i = 2; i < args.length; i++) {
            read(repository, args[i], prefixLength);
        }

        write(prefixLength, outputLength, repository);
    }
}
