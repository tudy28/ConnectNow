package socialnetwork.domain.validators;

import socialnetwork.domain.Prietenie;
import socialnetwork.domain.Utilizator;
import socialnetwork.domain.exceptions.FriendshipValidationException;

public class FriendshipValidator implements Validator<Prietenie> {


    Iterable<Utilizator> users;

    /**
     * Constructor al FriendshipValidator care primeste ca si parametru o lista a utilizatorilor
     * @param users o lista de itlizatori
     */

    /**
     * Metoda care valideaza un obiect de tip Prietenie
     * @param entity obiect de tip Prietenie
     * @throws FriendshipValidationException
     *          daca obiectul de tip Prietenie
     *          are ID1=ID2
     *          unul dintre ID-uri sau ambele ID-uri nu exista
     */
    @Override
    public void validate(Prietenie entity) throws FriendshipValidationException {
        String errors="";
        if(entity.getId().getRight().longValue()==entity.getId().getLeft().longValue()){
            errors+="IDs must be different...\n";
        }
        boolean ID1exists=false;
        boolean ID2exists=false;

        if(errors.length()>0)
            throw new FriendshipValidationException(errors);
    }
}
