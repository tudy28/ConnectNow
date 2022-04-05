package socialnetwork.container;
import socialnetwork.domain.Utilizator;

import java.util.*;


public class Graph<T> {

    private Map<T, List<T> > map = new HashMap<>();
    private Map<T,Boolean> connectedCompMap=new HashMap<>();;
    private long maxLengthPath;

    /**
     * Metoda care adauga un nod de tip T in graf
     * @param s nodul grafului
     */
    public void addVertex(T s)
    {
        map.put(s, new LinkedList<T>());
    }

    /**
     * Metoda care adauga o muchie care are ca si extremitati doua noduri de tip T
     * @param source nodul stang
     * @param destination nodul drept
     * @param bidirectional
     *          muchie orientata daca bidirectional==false
     *          muchie neorientata daca bidirectional==true
     */
    public void addEdge(T source,
                        T destination,
                        boolean bidirectional)
    {

        if (!map.containsKey(source))
            addVertex(source);

        if (!map.containsKey(destination))
            addVertex(destination);

        map.get(source).add(destination);
        if (bidirectional) {
            map.get(destination).add(source);
        }
    }

    /**
     * Metoda care returneaza numarul de noduri din graf
     * @return numarul de noduri(int)
     */
    public int getVertexCount()
    {
        return map.keySet().size();
    }

    /**
     * Metoda care returneaza numarul de muchii din graf
     * @param bidirection
     *      muchiile orientate daca bidirectional==false
     *      muchiile neorientate daca bidirectional==true
     * @return numarul de muchii(int) in functie de valoarea lui bidirectional
     */
    public int getEdgesCount(boolean bidirection)
    {
        int count = 0;
        for (T v : map.keySet()) {
            count += map.get(v).size();
        }
        if (bidirection) {
            count = count / 2;
        }
        return count;
    }


    /**
     * Metoda care parcurge in adancime graful
     * @param v nodul din care se porneste parcurgerea in adancime
     * @param visited vectorul de noduri in care sunt vizitate progresiv nodurile in timpul parcurgerii
     */
    public void DFS(T v, Map<T,Boolean>visited) {
        visited.put(v,true);
        connectedCompMap.put(v,true);
        for (T adjacentVertex : map.get(v)) {
                if(!visited.get(adjacentVertex))
                    DFS(adjacentVertex,visited);
        }
    }

    /**
     * Metoda care calculeaza numarul componentelor conexe din graf
     * @return numarul de componente conexe(int)
     */
    public int getConnectedComponents(){
        Map<T,Boolean> visited=new HashMap<>();
        for(T vertex: map.keySet()){
            visited.put(vertex,false);
        }
        int connctedComponents=0;
        for(T vertex: visited.keySet()){
            if(!visited.get(vertex)) {
                DFS(vertex, visited);
                connctedComponents++;
            }
        }
        return connctedComponents;
    }

    /**
     * Metoda care determina lantul elementar de lungime maxima dintr-un anumit varf retinandu-i lungimea maxima in maxLengthPath
     * @param vertex nodul din care se porneste cautarea lantului elementar
     * @param visited vectorul de noduri in care sunt vizitate progresiv nodurile in timpul parcurgerii
     * @param nr variabila care contorizeaza lungimea lantului elementar actual
     */
    public void longestPathVertex(T vertex,Map<T,Boolean> visited,long nr){
        if (nr > maxLengthPath) {
            maxLengthPath = nr;
        }
        for(T i: map.keySet())
            if (map.get(vertex).contains(i) && !visited.get(i)) {
                visited.put(i,true);
                visited.put(vertex,true);
                nr++;
                longestPathVertex(i, visited, nr);
                nr--;
                visited.put(i,false);
                visited.put(vertex,false);
            }
    }

    /**
     * Metoda care determina lantul elementar de lungime maxima dintr-o componenta conexa retinandu-i lungimea maxima in maxLengthPath
     * @return lungimea maxima a unui lant elemenar dintr-o componenta conexa
     */
    public long longestPathConnectedComp(){
        Map<T,Boolean> visited=new HashMap<>();
        long maxim=-1;
        for(T vertex: connectedCompMap.keySet()) {
            for (T visitedVertex : connectedCompMap.keySet()) {
                visited.put(visitedVertex, false);
            }
            maxLengthPath = -1;
            longestPathVertex(vertex,visited,0);
            if(maxim<maxLengthPath)
                maxim=maxLengthPath;
        }
        return maxim;

    }

    /**
     * Metoda care determina lantul elementar de lungime maxima din intregul graf si returneaza componenta conexa
     * @return o lista care contine varfurile componentei conexe care are lantul elementar de lungime maxima dintre toate componentele conexe,
     * pe ultima pozitie a listei aflandu-se lungimea lantului
     */
    public ArrayList<Long> longestPathGraph(){
        Map<T,Boolean> visited=new HashMap<>();
        for(T vertex: map.keySet()){
            visited.put(vertex,false);
        }
        long maxGraphPath=-1;
        long longestPathConnectedComp;
        ArrayList<Long> maxConnectedComp=new ArrayList<>();
        for(T vertex: visited.keySet()){
            if(!visited.get(vertex)) {
                connectedCompMap.clear();
                DFS(vertex, visited);
                if(connectedCompMap.keySet().size()>1) {//At least a community (min 2 users)
                    longestPathConnectedComp = longestPathConnectedComp();
                    if(maxGraphPath<longestPathConnectedComp) {
                        maxConnectedComp.clear();
                        maxGraphPath = longestPathConnectedComp;
                        for(T key:connectedCompMap.keySet())
                            maxConnectedComp.add((long)key);
                        maxConnectedComp.add(maxGraphPath);
                    }
                }
            }
        }
        return maxConnectedComp;
    }








}