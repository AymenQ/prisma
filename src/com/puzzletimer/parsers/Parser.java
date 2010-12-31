package com.puzzletimer.parsers;

public class Parser {
    private char[] input;
    private int pos;

    public Parser(String input) {
        this.input = input.toCharArray();
        this.pos = 0;
    }

    public void skipSpaces() {
        while (this.pos < this.input.length && Character.isWhitespace(this.input[this.pos])) {
            this.pos++;
        }
    }

    public String anyChar(String chars) {
        if (this.pos < this.input.length && chars.contains(Character.toString(this.input[this.pos]))) {
            this.pos++;
            return Character.toString(this.input[this.pos - 1]);
        }

        return null;
    }

    public String string(String s) {
        char[] sChars = s.toCharArray();

        int p = this.pos;
        for (int i = 0; i < sChars.length; i++) {
            if (p < this.input.length && sChars[i] == this.input[p]) {
                p++;
            } else {
                return null;
            }
        }

        this.pos = p;
        return s;
    }

    public String number() {
        String result = "";

        int p = this.pos;
        if (this.input[p] == '-') {
            result += this.input[p];
            p++;
        }

        int nDigits = 0;
        while (p < this.input.length && Character.isDigit(this.input[p])) {
            nDigits++;
            result += this.input[p];
            p++;
        }

        if (nDigits <= 0) {
            return null;
        }

        this.pos = p;
        return result;
    }
}
