package socialnetwork.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Utilizator extends Entity<Long>{
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private List<Utilizator> friends;

    /**
     * Metoda care construieste un obiect de tip utilizator
     * @param firstName un string
     * @param lastName un string
     */
    public Utilizator(String firstName, String lastName,String email,String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email=email;
        this.password=password;
        friends=new ArrayList<Utilizator>();
    }

    /**
     * Metoda care returneaza primul nume al unui utilizator
     * @return primul nume(string)
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Metoda care modifica primul nume al unui utilizator
     * @param firstName primul nume nou
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Metoda care returneaza ultimul nume al unui utilizator
     * @return ultimul nume(string)
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Metoda care modifica ultimul nume al unui utilizator
     * @param lastName ultimul nume nou
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Metoda care returneaza lista de prieteni a unui utilizator
     * @return o lista de utilizatori
     */

    /**
     * Metoda care returneaza emailul unui utilizator
     * @return emailul(string)
     */
    public String getEmail(){
        return email;
    }

    /**
     * Metoda care returneaza emailul unui utilizator
     * @return emailul(string)
     */
    public String getPassword(){
        return password;
    }

    public List<Utilizator> getFriends() {
        return friends;
    }

    /**
     * Metoda care adauga un utilizator in lista de prieteni a unui utilizator
     * @param friend un utilizator
     */
    public void addFriend(Utilizator friend){
        if(!friends.contains(friend))
            friends.add(friend);
    }

    /**
     * Metoda care sterge un utilizator din lista de prieteni a unui utilzator
     * @param friend un utilizator
     */
    public void removeFriend(Utilizator friend){
        friends.remove(friend);
    }

    /**
     * Metoda care afiseaza lista de prieteni a unui utilizator
     * @return un string
     */
    private String displayFriends(){
        if(friends.size()==0)
            return "N/A";
        String friendsList= "";
        for(Utilizator friend:friends)
            friendsList+=friend.getFirstName()+" "+friend.getLastName()+", ";
        friendsList= friendsList.substring(0,friendsList.length()-2);
        return friendsList;
    }

    /**
     * Metoda care tipareste datele utilizatorului
     * @return string
     */
    @Override
    public String toString() {
        return firstName +" "+ lastName;
    }

    /**
     * Metoda  care verifica daca un utilizator este egal cu un alt utilizator
     * @param o un alt utilizator
     * @return true,daca utilizatorii sunt egali
     *         false, altfel
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Utilizator)) return false;
        Utilizator that = (Utilizator) o;
        return getFirstName().equals(that.getFirstName()) &&
                getLastName().equals(that.getLastName()) &&
                getFriends().equals(that.getFriends());
    }

    /**
     * Metoda care calculeaza Hashcode-ul unui utilizator
     * @return un hashcode
     */
    @Override
    public int hashCode() {
        return Objects.hash(getId(),getFirstName(), getLastName(), getFriends());
    }
}