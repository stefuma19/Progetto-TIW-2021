package it.polimi.tiw.progetto.utils;

public class IdException extends Exception{

    @Override
    public String getMessage() {
        return "L'ID a cui si sta tentando di accedere non esiste";
    }
}
