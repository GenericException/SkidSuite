package net.contra.jmd.util;

public class LogHandler {
    private String _className = "NoClass";

    public LogHandler(String className) {
        _className = className;
    }

    public void message(String msg) {
        System.out.println(msg);
    }

    public void log(String msg) {
        System.out.println("[" + _className + "]" + msg);
    }

    public void debug(String msg) {
            System.out.println("[" + _className + "]" + "[DEBUG]" + msg);
    }

    public void error(String msg) {
        System.out.println("[" + _className + "]" + "[ERROR]" + msg);
    }
}
