package socialnetwork.repository.database;


import socialnetwork.domain.Message;
import socialnetwork.domain.Prietenie;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.Utilizator;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.paging.Page;
import socialnetwork.repository.paging.PageImplementation;
import socialnetwork.repository.paging.Pageable;
import socialnetwork.repository.paging.Paginator;
import socialnetwork.utils.Constants;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

public class FriendshipDbRepository extends AbstractDbRepository<Tuple<Long,Long>, Prietenie> {

    private String url;
    private String username;
    private String password;

    public FriendshipDbRepository(String dbName, String url, String username, String password, Validator<Prietenie> validator) {
        super(dbName, url, username, password, validator);
        this.url = url;
        this.username = username;
        this.password = password;
    }


    @Override
    public Prietenie extractEntity(ResultSet resultSet) throws SQLException {
        Long requesterID = resultSet.getLong("requester_id");
        Long addresseeID = resultSet.getLong("addressee_id");
        String createdDateString = resultSet.getString("created_date");
        String status = resultSet.getString("status");
        LocalDateTime createdDate = LocalDateTime.parse(createdDateString, Constants.DATE_TIME_FORMATTER);
        Prietenie friendship = new Prietenie(requesterID, addresseeID);
        friendship.setDate(createdDate);
        friendship.setStatus(status);
        return friendship;
    }

    @Override
    public String createEntityAsQueryInsert(Prietenie friendship) {
        Long requesterID = friendship.getId().getLeft();
        Long addresseeID = friendship.getId().getRight();
        String createdDate = "'" + friendship.getDate().format(Constants.DATE_TIME_FORMATTER) + "'";
        String status = "'" + friendship.getStatus() + "'";
        return "INSERT INTO friendships (requester_id, addressee_id,created_date,status) VALUES (" + requesterID + "," + addresseeID + "," + createdDate + "," + status + ");";
    }

    @Override
    public String createEntityAsQueryDelete(Tuple<Long, Long> friendshipID) {
        return "DELETE FROM friendships WHERE requester_id=" + friendshipID.getLeft() + " AND addressee_id=" + friendshipID.getRight();
    }

    @Override
    public String createEntityAsQueryFindOne(Tuple<Long, Long> friendshipID) {
        return "SELECT * FROM friendships WHERE requester_id=" + friendshipID.getLeft() + " AND addressee_id=" + friendshipID.getRight();
    }

    public Iterable<Prietenie> getFriendsUser(long userID,long lastID,int pageSize) {
        ArrayList<Prietenie> list = new ArrayList<Prietenie>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("" +
                     "select requester_id+addressee_id-"+userID+" as friend_id,created_date,status " +
                     "from friendships\n" +
                     "where (addressee_id=" + userID + " or requester_id=" + userID + ") and status='Accepted'"+
                     "and requester_id+addressee_id-"+userID+">"+lastID+" order by requester_id+addressee_id-"+userID+" limit "+pageSize);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Long requesterID = userID;
                Long addresseeID = resultSet.getLong("friend_id");
                String createdDateString = resultSet.getString("created_date");
                String status = resultSet.getString("status");
                LocalDateTime createdDate = LocalDateTime.parse(createdDateString, Constants.DATE_TIME_FORMATTER);
                Prietenie friendship = new Prietenie(requesterID, addresseeID);
                friendship.setDate(createdDate);
                friendship.setStatus(status);
                list.add(friendship);
            }
            return list;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return list;
    }


    public Iterable<Prietenie> getFirstNFriendsUser(long userID,long n) {
        ArrayList<Prietenie> list = new ArrayList<Prietenie>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT * from friendships\n" +
                     "where (addressee_id=" + userID + " or requester_id=" + userID + ") and status='Accepted' order by requester_id+addressee_id-"+userID+" LIMIT "+n);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Prietenie prietenie = extractEntity(resultSet);
                list.add(prietenie);
            }
            return list;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return list;
    }

    public Page<Prietenie> findAllFriendsPaged(Pageable pageable,long userID,long lastID){
        return new PageImplementation<Prietenie>(pageable, StreamSupport.stream(this.getFriendsUser(userID,lastID,pageable.getPageSize()).spliterator(),false));
    }


}


