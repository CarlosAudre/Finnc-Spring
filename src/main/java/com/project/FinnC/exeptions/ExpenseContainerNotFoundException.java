package com.project.FinnC.exeptions;

public class ExpenseContainerNotFoundException extends RuntimeException {

    public ExpenseContainerNotFoundException() {
        super("Despesa não encontrada");
    }

    public ExpenseContainerNotFoundException(String message) {
        super(message);
    }

}
