package com.tarbar.kisan.Helper;

public class DataPart {
    private String fileName;
    private byte[] content;
    private String type;

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

