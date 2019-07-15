package br.edu.ufabc.compv1;

import java.io.File;
import java.nio.file.Files;
import java.io.IOException;

public class AnalisadorLexico {

    private String[] reservedWords = {"programa", "declare", "escreva", "leia", "inicio", "fim", "inteiros"};
    private char[] content;
    private int pos;

    public AnalisadorLexico(String filename) {
        try {
            byte[] bContent = Files.readAllBytes(new File(filename).toPath());
            this.content = new String(bContent).toCharArray();
            this.pos = 0;
        } catch (IOException ex) {
            System.err.println("Error reading the file.");
        }

    }

    private boolean isReservedWord(String text) {
        for (String s : reservedWords) {
            if (text.equals(s)) {
                return true;
            }
        }
        return false;
    }

    boolean eof() {
        return pos == content.length;
    }

    private char nextChar() {
        return content[pos++];
    }

    private boolean isLetter(char c) {
        return c >= 'a' && c <= 'z';
    }

    private boolean isBlank(char c) {
        return c == ' ' || c == '\n' || c == '\t' || c == '\r';
    }

    private boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/' || c == '^' || c == ':' || c == '=';
    }

    private boolean isPunctuation(char c) {
        return c == ',' || c == ':' || c == '(' || c == ')' || c == '.';
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private void retroceder(int i) {
        pos -= i;
    }

    public Token nextToken() {
        int s = 0;
        String text = "";
        Token token;
        while (true) {
            if (eof()) {
                return null;
            }
            switch (s) {
                case 0:
                    char c = nextChar();
                    if (isDigit(c)) {
                        s = 4;
                        text += c;
                    } else if (isLetter(c)) {
                        s = 1;
                        text += c;
                    } else if (isOperator(c)) {
                        text += c;
                        s = 2;
                    } else if (isPunctuation(c)) {
                        s = 3;
                        text += c;
                    } else if (isBlank(c)) {
                        s = 0;
                    } else {
                        return new Token(Token.ERROR, text);
                    }
                    break;
                case 1:
                    c = nextChar();
                    if (isLetter(c) || isDigit(c)) {
                        s = 1;
                        text += c;
                    } else if (isBlank(c)) {
                        if (isReservedWord(text)) {
                            return new Token(Token.RESERVERD_WORD, text);
                        } else {
                            return new Token(Token.ID, text);
                        }
                    } else if (isOperator(c) || isPunctuation(c)) {
                        retroceder(1);
                        return new Token(Token.ID, text);
                    } else {
                        return null;
                    }
                    break;
                case 2:
                    c = nextChar();
                    if (isOperator(c) && c == '=') {
                        text += c;
                        return new Token(Token.OPERATOR, text);
                    }
                    return new Token(Token.OPERATOR, text);

                case 3:
                    return new Token(Token.PUNCTUATION, text);

                case 4:
                    c = nextChar();
                    if (isDigit(c)) {
                        s = 4;
                        text += c;
                    } else if (isPunctuation(c)) {
                        char next = nextChar();

                        if (isDigit(next)) {

                        } else if (isBlank(next)) {
                            retroceder(2);
                            return new Token(Token.INT_NUMBER, text);
                        }
                    }
                    break;
            }
        }
    }
}
