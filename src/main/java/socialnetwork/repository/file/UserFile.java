package socialnetwork.repository.file;

import socialnetwork.domain.Utilizator;
import socialnetwork.domain.validators.UtilizatorValidator;
import socialnetwork.domain.validators.Validator;

import java.util.List;

public class UserFile extends AbstractFileRepository<Long, Utilizator>{

    /**
     * Metoda care construieste un obiect de tipul UserFile
     * @param fileName un nume de fisier
     * @param validator un validator de tipul Utilizator
     */
    public UserFile(String fileName, Validator<Utilizator> validator) {
        super(fileName, validator);
    }

    /**
     * Metoda care extrage un obiect de tip Utilizator din fisier
     * @param attributes o linie din fisier
     * @return un obiect de tip Utilizator
     */
    @Override
    public Utilizator extractEntity(List<String> attributes) {
        //TODO: implement method
        Utilizator user = new Utilizator(attributes.get(1),attributes.get(2),attributes.get(3),attributes.get(4));
        user.setId(Long.parseLong(attributes.get(0)));

        return user;
    }

    /**
     * Metoda care returneaza un string cu datele din obiectul Utilizator
     * @param entity un obiect de tip Utilizator
     * @return un string
     */
    @Override
    protected String createEntityAsString(Utilizator entity) {
        return entity.getId()+";"+entity.getFirstName()+";"+entity.getLastName();
    }
}
