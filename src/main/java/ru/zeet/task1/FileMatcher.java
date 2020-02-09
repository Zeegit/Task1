package ru.zeet.task1;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileMatcher {
    File file;
    String regexp;

    public FileMatcher(File file, String regexp) {
        this.file = file;
        this.regexp = regexp;
    }

    public String readFileAsString(String fileName) throws IOException {
        return new String(Files.readAllBytes(Paths.get(fileName)));
    }


    public boolean matches() {
        boolean find = false;
        String text = null;
        try {
            text = readFileAsString(file.getCanonicalPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (text != null) {
            Pattern pattern = Pattern.compile(regexp, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(text);
            find = matcher.find();
        }
        return find;
    }
}
