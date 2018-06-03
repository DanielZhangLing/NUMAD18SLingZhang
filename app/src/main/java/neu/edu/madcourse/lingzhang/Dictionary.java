package neu.edu.madcourse.lingzhang;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;

public class Dictionary {
    private HashSet<String> set;

    Dictionary(InputStream inputStream) throws IOException {
        set = new HashSet<>();
        InputStreamReader inputStreamReader= new InputStreamReader(inputStream);
        BufferedReader bufferReader = new BufferedReader(inputStreamReader);
        String line;
        while((line = bufferReader.readLine()) !=null)
            set.add(line);
    }

    boolean isWord(String word){
        return word.length() >= 3 && set.contains(word);
    }
}
