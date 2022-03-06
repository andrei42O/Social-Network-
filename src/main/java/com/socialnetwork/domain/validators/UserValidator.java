package com.socialnetwork.domain.validators;

import com.socialnetwork.domain.User;

public class UserValidator implements Validator<User> {

    /**
     * Functoa de vaidare a entitatii utilizator
     * @param entity entitatea ce trebuie validata
     * @throws ValidationException exceptia aruncata
     */
    @Override
    public void validate(User entity) throws ValidationException {
        String err = "";
        if(entity.getId() == null)
            err += "The id cannot be null!\n";
        if(entity.getFirstName().equals(""))
            err += "The first name cannot be empty!\n";
        if(entity.getLastName().equals(""))
            err += "The last name cannot be empty!\n";
        if(!entity.getFirstName().matches("^[A-Z][a-zA-Z]+(-[A-Z][a-zA-Z]+)?$"))
            err += "The first name must start with capital letter and only have letters!\n";
        if(!entity.getLastName().matches("^[A-Z][a-zA-Z]+$"))
            err += "The last name must start with capital letter and only have letters!\n";
        if(err.length() != 0)
            throw new ValidationException(err);

    }
}
