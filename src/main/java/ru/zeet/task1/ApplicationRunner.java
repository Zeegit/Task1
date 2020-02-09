package ru.zeet.task1;


import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ApplicationRunner {
    private static Logger log = Logger.getLogger(FileJPanel.class.getName());

    public static void main(String[] args) throws IOException {
        log.setLevel(Level.INFO);
        new AppSeacher();
        //new FileBrowser();
    }
}

