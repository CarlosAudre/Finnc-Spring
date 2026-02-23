package com.project.FinnC.exeptions;

public class EmailAlreadyExistsException extends RuntimeException {

    public EmailAlreadyExistsException(){ //Se eu quiser lançar uma mensagem padrão
        super("Email já cadastrado");
    }

    public EmailAlreadyExistsException(String message){ //Se eu quiser lançar uma mensagem personalizada
        super(message);
    }
}
