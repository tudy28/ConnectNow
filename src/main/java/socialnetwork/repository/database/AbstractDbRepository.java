package socialnetwork.repository.database;

import socialnetwork.domain.Entity;
import socialnetwork.domain.Utilizator;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.Repository;
import socialnetwork.repository.memory.InMemoryRepository;
import socialnetwork.repository.paging.Page;
import socialnetwork.repository.paging.Pageable;
import socialnetwork.repository.paging.Paginator;
import socialnetwork.repository.paging.PagingRepository;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;

public abstract class AbstractDbRepository<ID, E extends Entity<ID>> implements Repository<ID,E> {
    private String dbName;
    private String url;
    private String username;
    private String password;
    private Validator<E> validator;

    public AbstractDbRepository(String dbName, String url, String username, String password, Validator<E> validator) {
        this.dbName = dbName;
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }



    public abstract E extractEntity(ResultSet resultSet) throws SQLException;

    public abstract String createEntityAsQueryInsert(E entity);
    public abstract String createEntityAsQueryDelete(ID id);
    public abstract String createEntityAsQueryFindOne(ID id);

    @Override
    public Iterable<E> findAll(){
        return gettAllFromDatabase();
    }


    @Override
    public Optional<E> findOne(ID id){
        if (id == null) {
            throw new IllegalArgumentException("ID must not be null...");
        }
        E entity= getOneFromDatabase(id);
        return Optional.ofNullable(entity);

    }

    @Override
    public Optional<E> save(E entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity must not be null...");
        }
        validator.validate(entity);
        insertToDatabase(entity);
        return Optional.empty();
    }

    @Override
    public Optional<E> delete(ID id) {
        if (id == null) {
            throw new IllegalArgumentException("ID must not be null...");
        }
        Optional<E> e=findOne(id);
        if(e.isPresent()) {
            deleteFromDatabase(id);
            return e;
        }
        return Optional.empty();
    }

    @Override
    public Optional<E> update(E entity) {
        if(findOne(entity.getId()).isPresent()) {
            deleteFromDatabase(entity.getId());
            insertToDatabase(entity);
            return Optional.empty();
        }
        return Optional.of(entity);
    }



    protected void insertToDatabase(E entity) {

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(createEntityAsQueryInsert(entity)))
        {
            statement.executeUpdate();
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    protected void deleteFromDatabase(ID id) {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statementDrop = connection.prepareStatement(createEntityAsQueryDelete(id)))
        {
            statementDrop.executeUpdate();
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    protected E getOneFromDatabase(ID id) {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(createEntityAsQueryFindOne(id));
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                return extractEntity(resultSet);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    protected Iterable<E> gettAllFromDatabase() {
        ArrayList<E> list = new ArrayList<E>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + dbName);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                E entity = extractEntity(resultSet);
                list.add(entity);
            }
            return list;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return list;
    }

    @Override
    public int size() {
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) AS size FROM "+dbName);
            ResultSet resultSet = statement.executeQuery();)
        {
            int count=0;
            while(resultSet.next())
                count=resultSet.getInt("size");
            return count;
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return 0;
    }

    public long generateNewID(){
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("SELECT MAX(id) FROM "+dbName);
            ResultSet resultSet = statement.executeQuery();)
        {
            long lastID=-1;
            while(resultSet.next())
                lastID=resultSet.getInt("max");
            return lastID+1;
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return -1;
    }

}

