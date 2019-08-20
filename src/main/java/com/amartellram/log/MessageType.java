package com.amartellram.log;

public enum MessageType {
    DEFAULT("0"),
    MESSAGE("1"),
    ERROR("2"),
    WARNING("3");

    private String type;

    MessageType(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }
}
