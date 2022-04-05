package socialnetwork.repository.file;

import socialnetwork.domain.Entity;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.memory.InMemoryRepository;

import java.io.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


///Aceasta clasa implementeaza sablonul de proiectare Template Method; puteti inlucui solutia propusa cu un Factori (vezi mai jos)
public abstract class AbstractFileRepository<ID, E extends Entity<ID>> extends InMemoryRepository<ID,E> {
    String fileName;

    /**
     * Metoda care construieste un obiect de tip AbstractFileRepository
     * @param fileName un nume de fisier din care se vor citi/scrie date
     * @param validator un validator de tip E
     */
    public AbstractFileRepository(String fileName, Validator<E> validator) {
        super(validator);
        this.fileName=fileName;
        loadData();

    }

    /**
     * Metoda care incarca date din fisier in memorie
     */
    private void loadData() {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String linie;
            while((linie=br.readLine())!=null){
                List<String> attr=Arrays.asList(linie.split(";"));
                try {
                    E e = extractEntity(attr);
                    super.save(e);
                }
                catch (Exception e){
                    System.out.println("The record '"+linie+"' is corrupted...");
                }
            }
        } catch (IOException e) {
            System.out.println("File '"+fileName+"' doesn't exist...");
        }

        //sau cu lambda - curs 4, sem 4 si 5
//        Path path = Paths.get(fileName);
//        try {
//            List<String> lines = Files.readAllLines(path);
//            lines.forEach(linie -> {
//                E entity=extractEntity(Arrays.asList(linie.split(";")));
//                super.save(entity);
//            });
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }

    /**
     *  extract entity  - template method design pattern
     *  creates an entity of type E having a specified list of @code attributes
     * @param attributes
     * @return an entity of type E
     */
    public abstract E extractEntity(List<String> attributes);
    ///Observatie-Sugestie: in locul metodei template extractEntity, puteti avea un factory pr crearea instantelor entity

    protected abstract String createEntityAsString(E entity);

    /**
     * Metoda care salveaza un obiect in fisier si in memorie
     * @param entity un obiect generic de tip E
     * @return entity daca elementul exista deja
     *         null daca entity a fost adaugat
     */
    @Override
    public Optional<E> save(E entity){
        Optional<E> e=super.save(entity);
        if (e.isPresent())
        {
            return e;
        }
        writeToFile(entity);
        return Optional.empty();

    }


    /**
     * Metoda care sterge un obiect din fisier si din memorie
     * @param id ID-ul obiectului
     * @return elementul, daca acesta a fost sters cu succes
     *         null,daca elementul nu a existat
     */
    @Override
    public Optional<E> delete(ID id) {
        Optional<E> e=super.delete(id);
        if(e.isPresent()) {
            writeToFile();
            return e;
        }
        return Optional.empty();
    }

    /**
     * Metoda care modifica un obiect generic de tip E
     * @param entity un nou obiect de tip E
     * @return null, daca obiectul s-a modificat
     *         entity, daca obiectul nu exista
     */
    @Override
    public Optional<E> update(E entity) {
        Optional<E> e=super.update(entity);
        if(!e.isPresent()) {
            writeToFile();
            return Optional.empty();
        }
        return e;
    }

    /**
     * Metoda care scrie in fisier un singur obiect generic de tip E
     * @param entity un obiect generic de tip E
     */
    protected void writeToFile(E entity){
        try (BufferedWriter bW = new BufferedWriter(new FileWriter(fileName,true))) {
            bW.write(createEntityAsString(entity));
            bW.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Metoda care scrie in fisier toata lista din memorie de obiecte generice
     */
    protected void writeToFile(){
        try (BufferedWriter bW = new BufferedWriter(new FileWriter(fileName,false))) {
            for(E e:findAll()) {
                bW.write(createEntityAsString(e));
                bW.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}

