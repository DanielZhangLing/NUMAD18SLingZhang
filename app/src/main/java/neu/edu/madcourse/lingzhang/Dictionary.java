package neu.edu.madcourse.lingzhang;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

public class Dictionary {
    private HashSet<String> set;
    private List<String> nineLetterWords = new ArrayList<>();

    public Dictionary(InputStream inputStream) throws IOException {
        set = new HashSet<>();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferReader = new BufferedReader(inputStreamReader);
        String line;
        while ((line = bufferReader.readLine()) != null) {
            set.add(line);
            if (line.length() == 9)
                nineLetterWords.add(line);
        }
    }

    public boolean isWord(String word, int num) {
        return word.length() >= num && set.contains(word.toLowerCase());
    }

    public String[] generateWordList() {
        String[] result = new String[9];
        List<String> oldNineLetterWords = new ArrayList<>(nineLetterWords);
        int arraySize = nineLetterWords.size();
        if (nineLetterWords != null && arraySize != 0) {
            for (int i = 0; i < 9; i++) {
                int randomIndex = new Random().nextInt(arraySize--);
                String targetWord = nineLetterWords.get(randomIndex);
                result[i] = targetWord;
                nineLetterWords.remove(targetWord);
            }
            nineLetterWords = oldNineLetterWords;
        }
        return result;
    }
}
