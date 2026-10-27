package com.clean.application;

public class MissionInputException extends RuntimeException{
    public MissionInputException(int lineNumber, String message) {
        super("Line " + lineNumber + ": " + message);
    }

    public MissionInputException(String message) {
        super(message);
    }
}
