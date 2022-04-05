package socialnetwork.domain;

import java.io.Serializable;

public class Entity<ID> implements Serializable {

    private static final long serialVersionUID = 7331115341259248461L;
    private ID id;

    /**
     * Metoda care returneaza ID-ul unei entitati
     * @return un ID
     */
    public ID getId() {
        return id;
    }

    /**
     * Metoda care modifica ID-ul unei entitati
     * @param id noul ID
     */
    public void setId(ID id) {
        this.id = id;
    }
}