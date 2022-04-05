package socialnetwork.domain;

import java.util.Objects;


/**
 * Define a Tuple o generic type entities
 * @param <E1> - tuple first entity type
 * @param <E2> - tuple second entity type
 */
public class Tuple<E1, E2> {
    private E1 e1;
    private E2 e2;

    /**
     * Metoda care construieste un tuplu
     * @param e1 un obiect generic
     * @param e2 un obiect generic
     */
    public Tuple(E1 e1, E2 e2) {
        this.e1 = e1;
        this.e2 = e2;
    }

    /**
     * Metoda care returneaza obiectul generic din stanga
     * @return obiectul generic din stanga
     */
    public E1 getLeft() {
        return e1;
    }

    /**
     * Metoda care modifica obiectul generic din stanga
     * @param e1 un nou obiect generic
     */
    public void setLeft(E1 e1) {
        this.e1 = e1;
    }

    /**
     * Metoda care returneaza obiectul generic din dreapta
     * @return obiectul generic din dreapta
     */
    public E2 getRight() {
        return e2;
    }

    /**
     * Metoda care modifica obiectul generic din dreapta
     * @param e2 un nou obiect generic
     */
    public void setRight(E2 e2) {
        this.e2 = e2;
    }

    /**
     * @return Tuplul sub forma de string
     */
    @Override
    public String toString() {
        return "" + e1 + "," + e2;

    }

    /**
     * Metoda care verifica daca un tuplu este egal cu alt Tuple
     * @param obj un alt obiect Tuple
     * @return true,daca sunt egale
     *         false, altfel
     */
    @Override
    public boolean equals(Object obj) {
        return this.e1.equals(((Tuple) obj).e1) && this.e2.equals(((Tuple) obj).e2);
    }

    /**
     * Metoda care calculeaza Hashcode-ul unui tuplu
     * @return un hashcode
     */
    @Override
    public int hashCode() {
        return Objects.hash(e1, e2);
    }
}