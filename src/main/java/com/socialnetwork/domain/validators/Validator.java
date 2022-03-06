package com.socialnetwork.domain.validators;

/**
 * Interfata pentru validator
 */
public interface Validator<T>
{
    /**
     * functie abstracta de validare
     * @param entity entitatea ce trebuie validata
     * @throws ValidationException clasa custom pentru exceptii
     */
    void validate(T entity) throws ValidationException;
}