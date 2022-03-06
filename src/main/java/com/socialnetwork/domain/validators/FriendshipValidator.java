package com.socialnetwork.domain.validators;

import com.socialnetwork.domain.Friendship;

public class FriendshipValidator implements Validator<Friendship> {
    /**
     * Friendship validator
     *
     * @param entity to be checked
     * @throws ValidationException
     */
    @Override
    public void validate(Friendship entity) throws ValidationException {
        String message = "";
        if(entity.getId1()<=0)
            message += "First id must be greater than 0";
        if(entity.getId2()<=0)
            message += "Second id must be greater than 0";
        if(entity.getId2() == entity.getId1())
            message += "A user cannot befriend himself";
        if(message!="")
            throw new ValidationException(message);
    }
}
