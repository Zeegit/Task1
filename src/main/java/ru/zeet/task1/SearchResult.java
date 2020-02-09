package ru.zeet.task1;

class SearchResult {
    private int start;
    private int end;
    private String group;

    public SearchResult(int start, int end, String group) {
        this.start = start;
        this.end = end;
        this.group = group;
    }


    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }
}