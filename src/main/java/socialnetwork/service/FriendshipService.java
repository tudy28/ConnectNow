package socialnetwork.service;

import socialnetwork.container.Graph;
import socialnetwork.domain.Prietenie;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.Utilizator;
import socialnetwork.domain.exceptions.FriendshipValidationException;
import socialnetwork.domain.exceptions.ServiceException;
import socialnetwork.repository.Repository;
import socialnetwork.utils.events.ChangeEventType;
import socialnetwork.utils.events.FriendshipChangeEvent;

import socialnetwork.utils.observer.Observable;
import socialnetwork.utils.observer.Observer;

import java.util.*;

public class FriendshipService implements Observable<FriendshipChangeEvent> {
    private Repository<Tuple<Long,Long>, Prietenie> repoFriendship;
    private Repository<Long,Utilizator> repoUser;


    /**
     * Metoda care construieste un obiect de tipul FriendshipService
     * @param repoFriendship un obiect de tipul Repository<Tuple<Long,Long>,Prietenie>>
     * @param repoUser un obiect de tipul Repository<Long,Utilizator></Long,Utilizator>
     */
    public FriendshipService(Repository<Tuple<Long,Long>,Prietenie> repoFriendship,Repository<Long,Utilizator> repoUser){
        this.repoFriendship=repoFriendship;
        this.repoUser=repoUser;
        loadFriends();
    }

    /**
     * Metoda care adauga o prietenie in lista de prietenii
     * @param ID1 ID-ul primul prieten
     * @param ID2 ID-ul celui de al doilea prieten
     * @throws ServiceException
     *          daca prietenia deja exista
     *          daca prietenia nu este valida
     *          daca prietenia este un obiect null
     */
    public void addFriend(long ID1, long ID2){
        String errors="";
        try {
            Prietenie p = new Prietenie(ID1, ID2);
            Prietenie p_reverse=new Prietenie(ID2,ID1);
            if(!repoFriendship.findOne(p_reverse.getId()).isPresent()) {
                if (repoFriendship.findOne(p.getId()).isPresent())
                    errors += "Friendship already exists...\n";
                else {
                    repoFriendship.save(p);
                    Optional<Utilizator> user1=repoUser.findOne(ID1);
                    Optional<Utilizator> user2=repoUser.findOne(ID2);
                    if(user1.isPresent() && user2.isPresent()) {
                        user1.get().addFriend(user2.get());
                        user2.get().addFriend(user1.get());
                    }
                    notifyObservers(new FriendshipChangeEvent(ChangeEventType.ADD, p));
                }
            }
            else{
                errors+="Friendship already exists...\n";
            }
        }
        catch (IllegalArgumentException | FriendshipValidationException e){
            errors+=e.getMessage();
        }
        if(errors.length()>0)
            throw new ServiceException(errors);
    }

    /**
     * Metoda care sterge o prietenie din lista de prietenii
     * @param ID1 ID-ul primul prieten
     * @param ID2 ID-ul al celui de al doilea prieten
     * @throws ServiceException
     *          daca prietenia nu exista
     *          daca ID-ul prieteniei este null
     */
    public void removeFriend(long ID1, long ID2){
        String errors="";
        try {
            Optional<Utilizator> user1;
            Optional<Utilizator> user2;
            Prietenie removedFriendship = new Prietenie(ID1, ID2);
            Prietenie removedFriendship_reverse = new Prietenie(ID2, ID1);
            if (repoFriendship.delete(removedFriendship.getId()).isPresent()) {
                user1 = repoUser.findOne(ID1);
                user2 = repoUser.findOne(ID2);
                if (user1.isPresent() && user2.isPresent()) {
                    user1.get().removeFriend(user2.get());
                    user2.get().removeFriend(user1.get());
                }
                notifyObservers(new FriendshipChangeEvent(ChangeEventType.DELETE, removedFriendship));
            }
            else if (repoFriendship.delete(removedFriendship_reverse.getId()).isPresent()) {
                    user1 = repoUser.findOne(ID1);
                    user2 = repoUser.findOne(ID2);
                    if (user1.isPresent() && user2.isPresent()) {
                        user1.get().addFriend(user2.get());
                        user2.get().addFriend(user1.get());
                    }
                notifyObservers(new FriendshipChangeEvent(ChangeEventType.DELETE, removedFriendship_reverse));
            }
            else {
                    errors += "Friendship doesn't exist...\n";
            }

        }
        catch(IllegalArgumentException e){
            errors+=e.getMessage();
        }
        if(errors.length()>0)
            throw new ServiceException(errors);
    }

    public void updateFriend(long ID1,long ID2,String newStatus){
        String errors="";
        try{
            Prietenie p = new Prietenie(ID1, ID2);
            p.setStatus(newStatus);
            Optional<Prietenie> opt=repoFriendship.update(p);
            if(opt.isPresent())
                errors="Friendship doesn't exist...\n";
        }
        catch (IllegalArgumentException | FriendshipValidationException e){
            errors+=e.getMessage();
        }
        if(errors.length()>0)
            throw new ServiceException(errors);
    }

    /**
     * Metoda care adauga in lista fiecarui utilizator prietenii pe care ii are din lista de prietenii
     */
    public void loadFriends() {
        for (Prietenie prietenie : repoFriendship.findAll()) {
            long ID1 = prietenie.getId().getLeft();
            long ID2 = prietenie.getId().getRight();
            Optional<Utilizator> user1 = repoUser.findOne(ID1);
            Optional<Utilizator> user2 = repoUser.findOne(ID2);
            if (user1.isPresent() && user2.isPresent()) {
                user1.get().addFriend(user2.get());
                user2.get().addFriend(user1.get());
            }
        }
    }


    /**
     * Metoda care determina numarul de comunitati din reteaua de socializare
     * O comunitate trebuie sa fie formata din cel putin 2 oameni
     * @return numarul de comunitati
     */
    public long numberOfCommunities(){
        Graph<Long> g = new Graph<Long>();

        //Daca un utilizator fara prieteni reprezinta o comunititate, atunci trebuie decomentat for-ul
        //for(Utilizator u: repoUser.findAll())
        //    g.addVertex(u.getId());

        for(Prietenie p: repoFriendship.findAll())
            g.addEdge(p.getId().getLeft(),p.getId().getRight(),true);

        return g.getConnectedComponents();

    }

    /**
     * Metoda care returneaza ID-urile utilizatorilor care fac parte din cea mai sociabila comunitate
     * @return ID-urile utilizatorilor
     */
    public ArrayList<Long> mostSocial(){
        Graph<Long> g = new Graph<Long>();

        for(Utilizator u: repoUser.findAll())
            g.addVertex(u.getId());

        for(Prietenie p: repoFriendship.findAll())
            g.addEdge(p.getId().getLeft(),p.getId().getRight(),true);

        return g.longestPathGraph();
    }


    public void acceptRequest(long ID1,long ID2){
        try {
            Prietenie p=new Prietenie(ID1,ID2);
            if(repoFriendship.findOne(p.getId()).isPresent() && repoFriendship.findOne(p.getId()).get().getStatus().equals("Accepted"))
                throw new ServiceException("Friendship already accepted...\n");
            this.updateFriend(ID1, ID2, "Accepted");
            notifyObservers(new FriendshipChangeEvent(ChangeEventType.DELETE, p));
        }
        catch (ServiceException e){
            throw new ServiceException(e.getMessage());
        }
    }

    private List<Observer<FriendshipChangeEvent>> observers=new ArrayList<>();

    @Override
    public void addObserver(Observer<FriendshipChangeEvent> e) {
        observers.add(e);
    }

    @Override
    public void removeObserver(Observer<FriendshipChangeEvent> e) {
        observers.remove(e);
    }

    @Override
    public void notifyObservers(FriendshipChangeEvent t) {
        observers.forEach(x->x.update(t));
    }
}
