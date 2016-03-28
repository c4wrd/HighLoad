package com.c4wrd.loadtester.util;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class Output {

    private static LogLevel currentLogLevel = LogLevel.DEFAULT;
    private static OutputStream outStream;
    private static OutputStreamWriter writer;

    public static void write(int level, boolean flush, String formattedString, Object... args) {
        try
        {
            if ( level <= currentLogLevel.toInteger() ) {
                writer.write(String.format(formattedString, args));
                if ( flush )
                    writer.flush();
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void write(int level, String formattedString, Object... args) {
        write(level, false, formattedString, args);
    }

    public static void print(int level, String formattedString, Object... args) {
        write(level, true, formattedString, args);
    }

    public static void println(int level, String formattedString, Object... args) {
        print(level, formattedString + "\n", args);
    }

    public static void print(LogLevel level, String formattedString, Object... args) {
        print(level.toInteger(), formattedString, args);
    }

    public static void println(LogLevel level, String formattedString, Object... args) {
        print(level.toInteger(), formattedString + "\n", args);
    }

    public static void flush() {
        try {
            writer.flush();
        } catch (IOException ignored) {
            // todo?
        }
    }

    public static void setCurrentLogLevel(LogLevel level) {
        currentLogLevel = level;
    }

    public static void setOutputStream(OutputStream stream) {
        outStream = stream;
        writer = new OutputStreamWriter(outStream);
    }

    public static void println() {
        write(0, true, "\n");
    }

    public static LogLevel getCurrentLogLevel() {
        return currentLogLevel;
    }
}
