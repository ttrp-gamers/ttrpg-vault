package com.ttrp.manager.exception;

public class UserCredentialsAlreadyInUseException extends RuntimeException {

    private final boolean usernameAlreadyExists;
    private final boolean emailAlreadyExists;

    public UserCredentialsAlreadyInUseException(String message, boolean usernameAlreadyExists, boolean emailAlreadyExists) {
        this.emailAlreadyExists = emailAlreadyExists;
        this.usernameAlreadyExists = usernameAlreadyExists;
        super(message);
    }

    public String getErrorMsg(){
        return  (usernameAlreadyExists ? "Username" : "") +
                (usernameAlreadyExists && emailAlreadyExists ? " and " : "") +
                (emailAlreadyExists ? "Email" : "") + " already in use";
    }


}
