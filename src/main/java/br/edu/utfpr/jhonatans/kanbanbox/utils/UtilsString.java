package br.edu.utfpr.jhonatans.kanbanbox.utils;

public class UtilsString {
    public static boolean stringVazia(String texto){

        return texto == null || texto.trim().length() == 0;
    }
}