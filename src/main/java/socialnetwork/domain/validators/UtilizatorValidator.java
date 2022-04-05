package socialnetwork.domain.validators;

import socialnetwork.domain.Utilizator;
import socialnetwork.domain.exceptions.UserValidationException;

public class UtilizatorValidator implements Validator<Utilizator> {

    /**
     * Metoda care valideaza un obiect de tip Utilizator
     * @param entity obiect de tip Utilizator
     * @throws UserValidationException
     *          daca numele sau prenumele este vid
     *          daca numele sau prenume contine altceva pe langa litere
     */
    @Override
    public void validate(Utilizator entity) throws UserValidationException {
        String errors = "";
        if (entity.getFirstName().equals("") || entity.getLastName().equals(""))
            errors += "The first/last name cannot be empty!\n";
        if(entity.getPassword().equals(""))
            errors +="The password cannot be empty!\n";
        String legat = entity.getFirstName() + entity.getLastName();
        for (char c : legat.toCharArray())
            if (!Character.isLetter(c)) {
                errors += "The first/last name must contain only letters!\n";
                break;
            }
        if(!entity.getEmail().matches("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}")){
            errors+= "The email is not valid!\n";
        }
        if(errors.length()>0)
            throw new UserValidationException(errors);
    }
}
