package com.example.mytraveldiary;

import java.io.Serializable;

public class Page implements Serializable {
    private String filePath;
    private String fileType;
    private String sentence;
    private String date;

    public Page() {
    }

    public Page(String filePath, String fileType, String sentence, String date) {
        this.filePath = filePath;
        this.fileType = fileType;
        this.sentence = sentence;
        this.date = date;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getSentence() {

        return sentence;
    }

    public String getDate() {

        return date;
    }

    public String getFileType() {

        return fileType;
    }
}
