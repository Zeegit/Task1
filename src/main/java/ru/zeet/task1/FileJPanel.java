package ru.zeet.task1;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import java.awt.*;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileJPanel extends JPanel {
    private static Logger log = Logger.getLogger(FileJPanel.class.getName());

    JTextArea textArea;

    ArrayList<SearchResult> searchResult;
    int searchPos;
    private boolean searchDone;


    public FileJPanel() {
        textArea = new JTextArea();
        this.setLayout(new BorderLayout());
        JScrollPane scrollPane = new JScrollPane(textArea,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        this.add(scrollPane);
    }


    public void selectAll() {
        DefaultHighlighter.DefaultHighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(Color.CYAN);
        for (SearchResult sr : searchResult) {
            try {
                textArea.getHighlighter().addHighlight(sr.getStart(), sr.getStart() + sr.getGroup().length(), painter);
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        }
        textArea.grabFocus();
    }

    void nextSearch() {
        textArea.getHighlighter().removeAllHighlights();
        if (searchDone && searchResult.size() > 0) {
            searchPos++;
            if (searchPos >= searchResult.size()) searchPos = 0;
            setPosition(searchResult.get(searchPos).getStart(), searchResult.get(searchPos).getStart() + searchResult.get(searchPos).getGroup().length());
        }
    }

    public void previousSearch() {
        textArea.getHighlighter().removeAllHighlights();
        if (searchDone && searchResult.size() > 0) {
            searchPos--;
            if (searchPos < 0) searchPos = searchResult.size() - 1;
            setPosition(searchResult.get(searchPos).getStart(), searchResult.get(searchPos).getStart() + searchResult.get(searchPos).getGroup().length());
        }
    }

    void setPosition(int start, int end) {
        textArea.setCaretPosition(start);
        textArea.select(start, end);
        textArea.grabFocus();
    }

    private synchronized void setSearchResult(ArrayList<SearchResult> searchResult) {
        this.searchResult = searchResult;
        searchPos = -1;
        searchDone = true;
    }

    public synchronized void setText(String text, String pattern) {
        textArea.setText(text);
        goHome();
        Seacher task = new Seacher(text, pattern, true);
        task.execute();
    }

    public int getSearchResultCount() {
        return searchResult.size();
    }

    public void goHome() {
        textArea.setCaretPosition(0);
    }

    public class Seacher extends SwingWorker<String, String> {
        private String text;
        private String regexp;
        private boolean useRegexp;

        Pattern pattern;
        Matcher matcher;

        ArrayList<SearchResult> searchResults;

        public Seacher(String text, String regexp, boolean useRegexp) {
            this.text = text;
            this.regexp = regexp;
            this.useRegexp = useRegexp;
            searchResults = new ArrayList<>();
        }


        @Override
        protected String doInBackground() {
            if (useRegexp) {
                pattern = Pattern.compile(regexp, Pattern.CASE_INSENSITIVE);
                matcher = pattern.matcher(text);

                while (matcher.find()) {
                    searchResults.add(new SearchResult(matcher.start(), matcher.end(), matcher.group()));
                }
            } else {
                int start = -1;
                while (true) {
                    start = text.indexOf(regexp, start + 1);
                    if (start == -1) {
                        break;
                    }
                    searchResults.add(new SearchResult(start, start + regexp.length(), regexp));
                }
            }
            return null;
        }

        @Override
        protected void done() {
            setSearchResult(searchResults);
        }
    }
}
