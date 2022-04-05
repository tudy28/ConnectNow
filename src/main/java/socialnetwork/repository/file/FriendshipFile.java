package socialnetwork.repository.file;

import socialnetwork.domain.Prietenie;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.validators.Validator;

import java.util.List;

public class FriendshipFile extends AbstractFileRepository<Tuple<Long,Long>, Prietenie> {

    /**
     * Metoda care construieste un obiect de tipul FriendshipFile
     * @param fileName un nume de fisier
     * @param validator un validator de tipul Prietenie
     */
    public FriendshipFile(String fileName, Validator<Prietenie> validator) {
        super(fileName, validator);
    }

    /**
     * Metoda care extrage un obiect de tip Prietenie din fisier
     * @param attributes o linie din fisier
     * @return un obiect de tip Prietenie
     */
    @Override
    public Prietenie extractEntity(List<String> attributes) {
        return new Prietenie(Long.parseLong(attributes.get(0)),Long.parseLong(attributes.get(1)));
    }

    /**
     * Metoda care returneaza un string cu datele din obiectul Prietenie
     * @param entity un obiect de tip Prietenie
     * @return un string
     */
    @Override
    protected String createEntityAsString(Prietenie entity) {
        return entity.getId().getLeft()+";"+entity.getId().getRight();
    }




}
