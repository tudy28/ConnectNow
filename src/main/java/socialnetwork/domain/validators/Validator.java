package socialnetwork.domain.validators;

public interface Validator<T> {
    /**
     * Metoda care valideaza un obiect generic de tip T
     * @param entity un obiect generic de tip T
     * @throws ValidationException
     *          daca T nu este valid
     */
    void validate(T entity) throws ValidationException;
}