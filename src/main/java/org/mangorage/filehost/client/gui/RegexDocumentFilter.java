package org.mangorage.filehost.client.gui;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class RegexDocumentFilter extends DocumentFilter {
    public static final String IPV4_PARTIAL_PATTERN =
            "^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){0,3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])?$";
    public static final String NUMBERS_PATTERN = "\\d+";
    public static final String USERNAME_PATTERN = "^[a-zA-Z0-9+#@!]+$";


    private final String regex;
    private final int limit;

    public RegexDocumentFilter(String regex) {
        this(regex, -1);
    }
    public RegexDocumentFilter(String regex, int limit) {
        this.regex = regex;
        this.limit = limit;
    }

    @Override
    public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
        String newStr = fb.getDocument().getText(0, fb.getDocument().getLength()) + string;
        if (newStr.matches(regex) || newStr.isEmpty()) {
            if (limit != -1 && newStr.length() > limit)
                return;

            super.insertString(fb, offset, string, attr);
        }
    }

    @Override
    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
        String newStr = fb.getDocument().getText(0, fb.getDocument().getLength()) + text;
        if (newStr.matches(regex) || newStr.isEmpty()) {
            if (limit != -1 && newStr.length() > limit)
                return;

            super.replace(fb, offset, length, text, attrs);
        }
    }
}
