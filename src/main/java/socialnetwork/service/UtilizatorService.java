package socialnetwork.service;

import socialnetwork.domain.Prietenie;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.Utilizator;
import socialnetwork.domain.UtilizatorPrietenieDTO;
import socialnetwork.domain.exceptions.ServiceException;
import socialnetwork.domain.exceptions.UserValidationException;
import socialnetwork.repository.Repository;
import socialnetwork.repository.database.FriendshipDbRepository;
import socialnetwork.repository.database.UserDbRepository;
import socialnetwork.repository.paging.Page;
import socialnetwork.repository.paging.Pageable;
import socialnetwork.repository.paging.PageableImplementation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class UtilizatorService  {
    private UserDbRepository repoUser;
    private FriendshipDbRepository repoFriendship;

    /**
     * Metoda care construieste un obiect de tipul UtilizatorService
     * @param repoFriendship un obiect de tipul Repository<Tuple<Long,Long>,Prietenie>>
     * @param repoUser un obiect de tipul Repository<Long,Utilizator></Long,Utilizator>
     */
    public UtilizatorService(UserDbRepository repoUser,FriendshipDbRepository repoFriendship) {
        this.repoUser = repoUser;
        this.repoFriendship=repoFriendship;
    }


    /**
     * Metoda care adauga un utilizator in lista de utilizatori
     * @param user un obiect utilizator
     * @throws ServiceException
     *          daca utilizatorul exista deja
     *          daca utilizatorul nu este valid
     *          daca utilizatorul este null
     */
    public void addUtilizator(Utilizator user) {
        String errors="";
        try {
            if(repoUser.save(user).isPresent())
                errors+="User already exists...\n";
        }
        catch (IllegalArgumentException | UserValidationException e){
            errors+=e.getMessage();
        }
        if(errors.length()>0)
            throw new ServiceException(errors);

    }

    /**
     * Metoda care sterge un utilizator din lista de utilizatori
     * @param id ID-ul utilizatorului care trebuie sters
     * @throws ServiceException
     *          daca utilizatorul nu exista
     *          daca ID-ul utilizatorului este null
     */
    public void removeUtilizator(long id){
        String errors="";
        try {
            if(repoUser.findOne(id).isPresent()) {
                ArrayList<Prietenie> prietenii=new ArrayList<>();
                for (Prietenie p : repoFriendship.findAll()) {
                    if (p.getId().getRight() == id || p.getId().getLeft() == id)
                        prietenii.add(p);
                }
                for(Prietenie pDeSters:prietenii){
                    Optional<Utilizator> userLeft=repoUser.findOne(pDeSters.getId().getLeft());
                    Optional<Utilizator> userRight=repoUser.findOne(pDeSters.getId().getRight());
                    if(userLeft.isPresent() && userRight.isPresent()) {
                        userLeft.get().removeFriend(userRight.get());
                        userRight.get().removeFriend(userLeft.get());
                    }
                    repoFriendship.delete(pDeSters.getId());
                }
            }
            if(!repoUser.delete(id).isPresent())
                errors+="User doesn't exist...\n";
        }
        catch (IllegalArgumentException e){
            errors=e.getMessage();
        }
        if(errors.length()>0)
            throw new ServiceException(errors);
    }

    /**
     * Metoda care modifica un utilizator din lista de utilizatori
     * @param user un obiect de tip utilizator
     * @return null daca utilizatorul a fost modificat
     *         user daca utilizatorul nu a fost modificat
     */
    public void updateUtilizator(Utilizator user){
        repoUser.update(user);
    }


    /**
     * Metoda care returneaza o lista iterabila de utilizatori
     * @return o lista iterabila de utilizatori
     */
    public Iterable<Utilizator> getAll(){
        return repoUser.findAll();
    }

    public Optional<Utilizator> getUser(long id){
        return repoUser.findOne(id);
    }


    /**
     * Metoda care returneaza ID-ul ultimului utilizator din lista de utilizatori
     * @return un ID
     */
    public long getLastID(){
        long lastID=0;
        for(Utilizator u:repoUser.findAll())
            lastID=u.getId();
        return lastID;
    }


    /**
     * Metoda care returneaza lista de prieteni a unui utilizator
     * @param id ID-ul utilizatorului
     * @return lista de prieteni
     */
    public List<UtilizatorPrietenieDTO> getFriends(long id){
        return StreamSupport.stream(repoFriendship.findAll().spliterator(),false)
                .filter(x->(x.getId().getLeft()==id || x.getId().getRight()==id) && x.getStatus().equals("Accepted"))
                .map(x->{
                    UtilizatorPrietenieDTO dto;
                    Utilizator userLeft=repoUser.findOne(x.getId().getLeft()).get();
                    Utilizator userRight=repoUser.findOne(x.getId().getRight()).get();
                    if(userLeft.getId()!=id) {
                        dto = new UtilizatorPrietenieDTO(userLeft.getEmail(),userLeft.getFirstName(), userLeft.getLastName(), x.getDate());
                        dto.setId(userLeft.getId());
                    }
                    else {
                        dto = new UtilizatorPrietenieDTO(userRight.getEmail(),userRight.getFirstName(), userRight.getLastName(), x.getDate());
                        dto.setId(userRight.getId());
                    }
                    return dto;
                })


                .collect(Collectors.toList());
    }

    public List<UtilizatorPrietenieDTO> getFriendsMonth(long id, Month month){

        return  getFriends(id).stream()
                .filter(x->((x.getDate().getMonth()==month)))
                .collect(Collectors.toList());
    }

    public List<UtilizatorPrietenieDTO> getFriendsBetween(long id, LocalDate date1, LocalDate date2){

        return  getFriends(id).stream()
                .filter(x-> x.getDate().toLocalDate().compareTo(date1) >= 0 && x.getDate().toLocalDate().compareTo(date2) <= 0)
                .collect(Collectors.toList());
    }

    public List<Utilizator> getReceivedFriendRequests(long id){
        return StreamSupport.stream(repoFriendship.findAll().spliterator(),false)
                .filter(x->(x.getId().getRight()==id) && x.getStatus().equals("Pending"))
                .map(x-> repoUser.findOne(x.getId().getLeft()).get())
                .collect(Collectors.toList());
    }

    public List<Utilizator> getSentFriendRequests(long id){
        return StreamSupport.stream(repoFriendship.findAll().spliterator(),false)
                .filter(x->(x.getId().getLeft()==id) && x.getStatus().equals("Pending"))
                .map(x-> repoUser.findOne(x.getId().getRight()).get())
                .collect(Collectors.toList());
    }

    public Optional<Utilizator> getBypass(String email,String password){
        return repoUser.checkBypass(email,password);
    }

    public boolean checkExistingEmail(String email){
        return repoUser.checkExistingEmail(email);
    }


    public List<UtilizatorPrietenieDTO> getFirstNFriends(long id,long n){
        return StreamSupport.stream(repoFriendship.getFirstNFriendsUser(id,n).spliterator(),false)
                .map(x->{
                    UtilizatorPrietenieDTO dto;
                    Utilizator userLeft=repoUser.findOne(x.getId().getLeft()).get();
                    Utilizator userRight=repoUser.findOne(x.getId().getRight()).get();
                    if(userLeft.getId()!=id) {
                        dto = new UtilizatorPrietenieDTO(userLeft.getEmail(),userLeft.getFirstName(), userLeft.getLastName(), x.getDate());
                        dto.setId(userLeft.getId());
                    }
                    else {
                        dto = new UtilizatorPrietenieDTO(userLeft.getEmail(),userRight.getFirstName(), userRight.getLastName(), x.getDate());
                        dto.setId(userRight.getId());
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }


    private int pageFriends=-1;
    private int sizeFriends=1;

    public void setPageFriendsSize(int newSizeFriends){
        sizeFriends=newSizeFriends;
    }

    public int getPageFriendsSize() {
        return sizeFriends;
    }




    public List<UtilizatorPrietenieDTO> getNextFriends(long userID,long lastID){
        pageFriends++;
        return getFriendsOnPage(pageFriends,userID,lastID);

    }

    public List<UtilizatorPrietenieDTO> getFriendsOnPage(int page,long userID,long lastID){
        pageFriends=page;
        Pageable pageable=new PageableImplementation(pageFriends,sizeFriends);
        Page<Prietenie> friendPage=repoFriendship.findAllFriendsPaged(pageable,userID,lastID);

        return friendPage.getContent()
                .map(x->{
                    UtilizatorPrietenieDTO dto;
                    Utilizator userLeft=repoUser.findOne(x.getId().getLeft()).get();
                    Utilizator userRight=repoUser.findOne(x.getId().getRight()).get();
                    if(userLeft.getId()!=userID) {
                        dto = new UtilizatorPrietenieDTO(userLeft.getEmail(),userLeft.getFirstName(), userLeft.getLastName(), x.getDate());
                        dto.setId(userLeft.getId());
                    }
                    else {
                        dto = new UtilizatorPrietenieDTO(userRight.getEmail(),userRight.getFirstName(), userRight.getLastName(), x.getDate());
                        dto.setId(userRight.getId());
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }

    private int pageNonFriends=-1;
    private int sizeNonFriends=1;

    public void setPageNonFriendsSize(int newSizeNonFriends){
        sizeNonFriends=newSizeNonFriends;
    }

    public int getPageNonFriendsSize(){
        return sizeNonFriends;
    }



    public List<Utilizator> getNextNonFriendsFiltered(long userID,String string,long lastID){
        pageNonFriends++;
        return getNonFriendsFilteredOnPage(pageNonFriends,userID,string,lastID);

    }


    public List<Utilizator> getNonFriendsFilteredOnPage(int page,long userID,String name,long lastID){
        pageNonFriends=page;
        Pageable pageable=new PageableImplementation(pageNonFriends,sizeNonFriends);
        if(name.equals(""))
            return repoUser.findAllNonFriendsPaged(pageable,userID,"-",lastID).getContent()
                .collect(Collectors.toList());
        else
            return repoUser.findAllNonFriendsPaged(pageable,userID,name.toLowerCase(),lastID).getContent()
                .collect(Collectors.toList());
    }

    private int pageUsersRequestReceived=-1;
    private int sizeUsersRequestReceived=1;

    public void setPageUsersRequestReceived(int newSizeUsersRequestReceived){
        sizeUsersRequestReceived=newSizeUsersRequestReceived;
    }

    public int getPageUsersRequestReceivedSize(){
        return sizeUsersRequestReceived;
    }



    public List<Utilizator> getNextUsersRequestReceived(long userID,long lastID){
        pageUsersRequestReceived++;
        return getUsersRequestReceived(pageUsersRequestReceived,userID,lastID);

    }


    public List<Utilizator> getUsersRequestReceived(int page,long userID,long lastID){
        pageUsersRequestReceived=page;
        Pageable pageable=new PageableImplementation(pageUsersRequestReceived,sizeUsersRequestReceived);
        return repoUser.findAllReceivedRequestsPaged(pageable,userID,lastID).getContent()
                .collect(Collectors.toList());

    }

    private int pageUsersRequestSent=-1;
    private int sizeUsersRequestSent=1;

    public void setPageUsersRequestSent(int newSizeUsersRequestSent){
        sizeUsersRequestSent=newSizeUsersRequestSent;
    }

    public int getPageUsersRequestSentSize(){
        return sizeUsersRequestSent;
    }


    public List<Utilizator> getNextUsersRequestSent(long userID,long lastID){
        pageUsersRequestSent++;
        return getUsersRequestSent(pageUsersRequestSent,userID,lastID);

    }

    public List<Utilizator> getUsersRequestSent(int page,long userID,long lastID){
        pageUsersRequestSent=page;
        Pageable pageable=new PageableImplementation(pageUsersRequestSent,sizeUsersRequestSent);
        return repoUser.findAllSentRequestsPaged(pageable,userID,lastID).getContent()
                .collect(Collectors.toList());
    }


}
