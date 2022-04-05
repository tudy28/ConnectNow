package socialnetwork.repository.database;

import socialnetwork.domain.Event;
import socialnetwork.domain.Prietenie;
import socialnetwork.domain.Utilizator;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.paging.Page;
import socialnetwork.repository.paging.PageImplementation;
import socialnetwork.repository.paging.Pageable;
import socialnetwork.utils.Constants;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.stream.StreamSupport;

public class EventDbRepository extends AbstractDbRepository<Long, Event> {
    private UserDbRepository repoUsers;
    private String url;
    private String username;
    private String password;


    public EventDbRepository(String dbName, String url, String username, String password, Validator<Event> validator,UserDbRepository repoUsers) {
        super(dbName, url, username, password, validator);
        this.repoUsers=repoUsers;
        this.url=url;
        this.username=username;
        this.password=password;
    }

    @Override
    public Event extractEntity(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getLong("id");
        Long ownerID= resultSet.getLong("owner_id");
        Utilizator owner=repoUsers.findOne(ownerID).get();
        String startDateString = resultSet.getString("start_date");
        String endDateString = resultSet.getString("end_date");
        String eventTitle=resultSet.getString("title");
        String eventDescription=resultSet.getString("description");
        LocalDateTime startDate = LocalDateTime.parse(startDateString, Constants.DATE_TIME_FORMATTER);
        LocalDateTime endDate = LocalDateTime.parse(endDateString, Constants.DATE_TIME_FORMATTER);
        Event createdEvent=new Event(owner,startDate,endDate,eventTitle,eventDescription);
        createdEvent.setId(id);
        return createdEvent;
    }

    @Override
    public String createEntityAsQueryInsert(Event entity) {
        String startDate="'"+entity.getStartDate().format(Constants.DATE_TIME_FORMATTER_HM)+"'";
        String endDate="'"+entity.getEndDate().format(Constants.DATE_TIME_FORMATTER_HM)+"'";
        String title="'"+entity.getName()+"'";
        String description="'"+entity.getDescription()+"'";
        return "INSERT INTO events(owner_id, start_date, end_date, title, description)"+
        "VALUES ("+entity.getOwner().getId()+","+startDate+","+endDate+","+title+","+description+");";
    }

    @Override
    public String createEntityAsQueryDelete(Long aLong) {
        return "DELETE FROM events WHERE id="+aLong;
    }

    @Override
    public String createEntityAsQueryFindOne(Long aLong) {
        return "SELECT * FROM EVENTS WHERE id="+aLong;
    }

    public void followEvent(Long user_id,Long eventID) {

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("INSERT INTO events_users(event_id,user_id)" +
                     "VALUES(+"+eventID+","+user_id+")"))
        {
            statement.executeUpdate();
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void unfollowEvent(Long user_id,Long eventID) {

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("DELETE FROM events_users where user_id="+user_id+" and event_id="+eventID))
        {
            statement.executeUpdate();
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public Iterable<Utilizator> getAllParticipantsFromEvent(long eventID) {
        ArrayList<Utilizator> list = new ArrayList<Utilizator>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("select * from events_users where event_id="+eventID);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Long id = resultSet.getLong("user_id");
                Utilizator participant= repoUsers.findOne(id).get();
                list.add(participant);
            }
            return list;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return list;
    }

    public Iterable<Event> getUnfollowedEvents(long userID,long lastID,int pageSize){
        ArrayList<Event> list=new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(
                     "select * \n" +
                             "from events \n" +
                             "where start_date>current_date and\n" +
                             "id not in \n" +
                             "(\n" +
                             "    select event_id \n" +
                             "    from events_users eu \n" +
                             "    inner join events e on eu.event_id  = e.id \n" +
                             "    where eu.user_id = "+userID +
                             ") and id>"+lastID+" limit "+pageSize);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Event event=extractEntity(resultSet);
                list.add(event);
            }
            return list;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return list;
    }

    public Iterable<Event> getFollowedEvents(long userID,long lastID,int pageSize){
        ArrayList<Event> list=new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(
                     "select * \n" +
                             "from events \n" +
                             "where start_date>current_date and\n" +
                             "id in \n" +
                             "(\n" +
                             "    select event_id \n" +
                             "    from events_users eu \n" +
                             "    inner join events e on eu.event_id  = e.id \n" +
                             "    where eu.user_id = "+userID +
                             ") and id>"+lastID+" limit "+pageSize);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Event event=extractEntity(resultSet);
                list.add(event);
            }
            return list;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return list;
    }

    public Iterable<Event> getAllFollowedEvents(long userID){
        ArrayList<Event> list=new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(
                     "select * \n" +
                             "from events \n" +
                             "where start_date>current_date and\n" +
                             "id in \n" +
                             "(\n" +
                             "    select event_id \n" +
                             "    from events_users eu \n" +
                             "    inner join events e on eu.event_id  = e.id \n" +
                             "    where eu.user_id = "+userID +
                             ")");
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Event event=extractEntity(resultSet);
                list.add(event);
            }
            return list;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return list;
    }

    public Page<Event> findAllUnfollowedEventsPaged(Pageable pageable, long userID, long lastID){
        return new PageImplementation<Event>(pageable, StreamSupport.stream(this.getUnfollowedEvents(userID,lastID,pageable.getPageSize()).spliterator(),false));
    }

    public Page<Event> findAllFollowedEventsPaged(Pageable pageable, long userID, long lastID){
        return new PageImplementation<Event>(pageable, StreamSupport.stream(this.getFollowedEvents(userID,lastID,pageable.getPageSize()).spliterator(),false));
    }


}
