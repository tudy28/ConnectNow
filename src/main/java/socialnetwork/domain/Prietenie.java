package socialnetwork.domain;


import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;


public class Prietenie extends Entity<Tuple<Long,Long>> {

    private LocalDateTime date;
    private String status;

    /**
     * Metoda care construieste un obiect de tip Prietenie si initializeaza variabila date cu data in care a fost
     * realizato prietenia
     * @param ID1 ID-ul primului utilizator
     * @param ID2 ID-ul al celui de al doilea utilizator
     */
    public Prietenie(Long ID1,Long ID2) {
        Tuple<Long,Long> tuple=new Tuple<Long,Long>(ID1,ID2);
        super.setId(tuple);
        date=LocalDateTime.now();
        this.status="Pending";
    }

    /**
     *
     * @return data cand prietenia a fost creata
     */
    public LocalDateTime getDate() {
        return date;
    }

    /**
     * Metoda care modifica data unei prietenii cu o data noua
     * @param date o noua data
     */
    public void setDate(LocalDateTime date){
        this.date=date;
    }

    /**
     * @return statusul unei relatii de prietenie
     */
    public String getStatus(){
        return status;
    }

    /**
     * Metoda care modifica statusul unei relatii de prietenii
     * @param status un nou status de prietenie
     */
    public void setStatus(String status){
        this.status=status;
    }
}
