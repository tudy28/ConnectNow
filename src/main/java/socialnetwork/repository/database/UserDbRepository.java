package socialnetwork.repository.database;

import socialnetwork.domain.Prietenie;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.Utilizator;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.paging.Page;
import socialnetwork.repository.paging.PageImplementation;
import socialnetwork.repository.paging.Pageable;
import socialnetwork.repository.paging.Paginator;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

public class UserDbRepository extends AbstractDbRepository<Long, Utilizator> {

    private String url;
    private String username;
    private String password;
    public UserDbRepository(String dbName,String url, String username, String password, Validator<Utilizator> validator) {
        super(dbName,url,username,password,validator);
        this.url=url;
        this.username=username;
        this.password=password;
    }

    public Optional<Utilizator> checkBypass(String email,String password){
        if(email.length()==0 || password.length()==0)
            return Optional.empty();
        String query="SELECT * FROM users WHERE '"+email+"'=email AND password=crypt('"+password+"',password)";
        try (Connection connection = DriverManager.getConnection(url, username, this.password);
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                return Optional.of(extractEntity(resultSet));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return Optional.empty();
    }


    public boolean checkExistingEmail(String email){
        String query="SELECT * FROM users WHERE '"+email+"'=email";
        try (Connection connection = DriverManager.getConnection(url, username, this.password);
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                return true;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    @Override
    public Utilizator extractEntity(ResultSet resultSet) throws SQLException {
            Long id = resultSet.getLong("id");
            String firstName = resultSet.getString("firstname");
            String lastName = resultSet.getString("lastname");
            String email=resultSet.getString("email");
            String password=resultSet.getString("password");
            Utilizator user = new Utilizator(firstName, lastName,email,password);
            user.setId(id);
            return user;
        }


    @Override
    public String createEntityAsQueryDelete(Long userID) {
        return "DELETE FROM users WHERE id="+userID;
    }

    //salt used:blowfish
    @Override
    public String createEntityAsQueryInsert(Utilizator user) {
        Long id=user.getId();
        String firstName="'"+user.getFirstName()+"'";
        String lastName="'"+user.getLastName()+"'";
        String email="'"+user.getEmail()+"'";
        String password="'"+user.getPassword()+"'";
        return "INSERT INTO users (id, firstname, lastname,email,password) VALUES ("+id+","+firstName+","+lastName+","+email+",crypt("+password+",gen_salt('bf',8)));";
    }

    public String createEntityAsQueryFindOne(Long userID){
        return "SELECT * FROM users WHERE id="+userID;
    }


    public List<Utilizator> getNonFriendsUserFilteredName(Long userID,String name,Long lastID,int pageSize){
        ArrayList<Utilizator> list = new ArrayList<Utilizator>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT u2.id,u2.firstname,u2.lastname,u2.email,u2.password\n"+
                     "FROM users u1,users u2\n"+
                             "WHERE NOT EXISTS(SELECT 1 FROM friendships f\n"+
                             "WHERE (\n" +
                             "(f.requester_id = u1.id AND f.addressee_id = u2.id) or\n"+
                             "(f.requester_id = u2.id AND f.addressee_id = u1.id)))\n"+
                             "AND u1.id != u2.id AND u1.id = "+userID +"\n" +
                             "AND LOWER(CONCAT(u2.firstname, ' ', u2.lastname)) like '"+name+"%'"+
                             "AND u2.id>"+lastID+" limit "+pageSize
        );
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Utilizator user = extractEntity(resultSet);
                list.add(user);
            }
            return list;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return list;
    }

    public Iterable<Utilizator> getReceivedRequestsUser(long userID,long lastID,int pageSize) {
        ArrayList<Utilizator> list = new ArrayList<Utilizator>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("select * from friendships where\n" +
                     "addressee_id="+userID+" and status='Pending'\n"+
                     "and requester_id>"+lastID+" order by requester_id limit "+pageSize);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Long id = resultSet.getLong("requester_id");
                Utilizator u= this.findOne(id).get();
                list.add(u);
            }
            return list;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return list;
    }

    public Iterable<Utilizator> getSentRequestsUser(long userID,long lastID, int pageSize) {
        ArrayList<Utilizator> list = new ArrayList<Utilizator>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("select * from friendships where\n" +
                     "requester_id="+userID+" and status='Pending'\n"+
                     "and addressee_id>"+lastID+" order by addressee_id limit "+pageSize);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Long id = resultSet.getLong("addressee_id");
                Utilizator u= this.findOne(id).get();
                list.add(u);
            }
            return list;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return list;
    }

    public Page<Utilizator> findAllNonFriendsPaged(Pageable pageable, long userID,String name,long lastID){
        return new PageImplementation<Utilizator>(pageable, StreamSupport.stream(this.getNonFriendsUserFilteredName(userID,name,lastID,pageable.getPageSize()).spliterator(),false));
    }

    public Page<Utilizator> findAllReceivedRequestsPaged(Pageable pageable, long userID,long lastID){
        return new PageImplementation<Utilizator>(pageable, StreamSupport.stream(this.getReceivedRequestsUser(userID,lastID,pageable.getPageSize()).spliterator(),false));
    }

    public Page<Utilizator> findAllSentRequestsPaged(Pageable pageable, long userID,long lastID){
        return new PageImplementation<Utilizator>(pageable, StreamSupport.stream(this.getSentRequestsUser(userID,lastID, pageable.getPageSize()).spliterator(),false));
    }
}

