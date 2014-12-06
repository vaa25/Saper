package saperobj.v2;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Vlasov Alexander
 * Date: 01.08.2014
 * Time: 1:48
 * To change this template use File | Settings | File Templates.
 *
 * @author Alexander Vlasov
 */
public class Island {
    private List<Group>list=new ArrayList<>();
    private List<Cell>toOpen=new ArrayList<>();
    private List<Cell>toMark=new ArrayList<>();
    private List<Group>indefinite=new ArrayList<>();
    private int amountCells;
    private Deque<StringBuilder> stack=new LinkedList<>();
    private List<Cell> indefiniteCells;
    private double[] possibilities;
    private Integer[] countOfMines;
    private List<Cell>minPossibilities; // ячейки с минимальной вероятностью нахождения мины
    private Map<Integer,Integer> m_x_0; //< количество мин, количество вариантов с таким количеством мин >
    private Map<Integer,Integer[]>m_x_i;
    private double minPossibility;

    public Island(List<Group> group) {
        add(group);
    }
    public Island(Group group) {
        add(group);
    }
    public void add(Group group) {
        list.add(group);
    }
    public void add(Collection<Group> groups){
        list.addAll(groups);
    }
    public void add(Island island){list.addAll(island.list);}
    public boolean isCross(Group group){
        for (Group group1 : list) {
            if (group.isCross(group1))return true;
        }
        return false;
    }
    public void optimize(){
        boolean repeat;
        do{
            repeat=false;
            for (int i = 0; i < list.size()-1; i++) {
                Group first = list.get(i);
                for (int j = i+1; j < list.size(); j++) {
                    Group second = list.get(j);
                    if (first!=second&&first.isCross(second)){
                        if (first.equals(second)){
                            list.remove(j--);
                            continue;
                        }
                        if (first.contains(second)){
                            first.subtraction(second);
                            repeat=true;
                            continue;
                        }
                        else if (second.contains(first)){
                            second.subtraction(first);
                            repeat=true;
                            continue;
                        }
                        else {
                            Group overlap = first.getOverlap(second);
                            if (overlap != null) {
                               first.subtraction(second);
                                repeat=true;
                                add(overlap);
                            }else{
                                overlap = second.getOverlap(first);
                                if (overlap != null) {
                                    second.subtraction(first);
                                    repeat=true;
                                    add(overlap);
                                }
                            }
                        }
                    }
                }
            }
        }while (repeat);
        determine();
        amountCells = size();
        Island inner=new Island(indefinite);

    }
    private void setIndefiniteCells(){
        indefiniteCells=new ArrayList<>();
        for (Group group : indefinite) {
            for (Cell cell : group.getList()) {
                if (!indefiniteCells.contains(cell))indefiniteCells.add(cell);
            }
        }
        amountCells=indefiniteCells.size()+toOpen.size()+toMark.size();
    }
    private int size(){
        return amountCells;
    }
    private void determine(){
        for (Group group : list) {
            if (group.getValue()==0){
                for (Cell cell : group.getList()) {
                    toOpen.add(cell);
                }
            }else if (group.size()==group.getValue()){
                for (Cell cell : group.getList()) {
                    toMark.add(cell);
                }
            }else indefinite.add(group);
        }
        setIndefiniteCells();
    }
    public void resolve(){
        for (Group group : indefinite) {
            group.setComb();
        }
        ListIterator<Group>iterator=indefinite.listIterator();
        makeTree(iterator);
        computePossibilities();
        setMinPossibilities();
    }
    private void makeTree(ListIterator<Group>iterator){
        if (iterator.hasNext()) {
            Group group=iterator.next();
            int combAmount=group.getCombSize();
            for (int i = 0; i < combAmount; i++) {
                if (group.checkCombination(i)){
                    group.storeCells();
                    group.setCellsComb(i);
                    makeTree(iterator);
                    group.restoreCells();
                    if (iterator.hasPrevious())iterator.previous();
                }
            }
        } else
        storeComb();
    }
    private void storeComb(){
        StringBuilder combSB=new StringBuilder(list.size());
        for (Cell cell : indefiniteCells) {
            if (cell.isMine())combSB.append(Group.MINE);
            if (cell.isValued())combSB.append(Group.VALUED);
            if (cell.isUnknown())combSB.append(Group.UNKNOWN);
        }
        stack.push(combSB);
    }
    private void computePossibilities(){
        int amountOfComb=stack.size();
        countOfMines=new Integer[indefiniteCells.size()];
        possibilities=new double[indefiniteCells.size()];
//        m_x_i=new HashMap<>();
        m_x_0=new HashMap<>();
        for (int i = 0; i < countOfMines.length; i++) {
            countOfMines[i]=0;
        }
        for (int i = 0; i < amountOfComb; i++) {
            StringBuilder combSB=stack.pop();
            int mines=0;
            for (int j = 0; j < indefiniteCells.size(); j++) {
                if (combSB.charAt(j)==Group.MINE){
                    countOfMines[j]++;
                    mines++;
                }
            }
//            m_x_i.put(mines,Arrays.copyOf(countOfMines,countOfMines.length));
            setMapWholeAmountOfMines(mines);

        }
        for (int i = 0; i < indefiniteCells.size(); i++) {
            possibilities[i]=100.0*countOfMines[i]/amountOfComb;
//            amountOfMinesPerOnePossibility+=countOfMines[i];
        }
    }
    private void setMinPossibilities(){
        double min=100;
        List<Integer>indices=new ArrayList<>();
        for (int i = 0; i < possibilities.length; i++) {
            double possibility=possibilities[i];
            indefiniteCells.get(i).setPossibility(possibility);
            if (possibility==min)indices.add(i);
            if (possibility<min){
                min=possibility;
                indices.clear();
                indices.add(i);
            }
        }
        minPossibilities=new ArrayList<>();
        for (Integer index : indices) {
            minPossibilities.add(indefiniteCells.get(index));
        }
        minPossibility=min;
    }
    private void setMapWholeAmountOfMines(Integer key){
        Integer value=0;
        if (m_x_0.containsKey(key))
            value= m_x_0.get(key);
        m_x_0.put(key, ++value);
    }

    //    Getters

    public List<Cell> getIndefiniteCells() {
        return indefiniteCells;
    }
    public double getMinPossibility() {
        return minPossibility;
    }
    public Map<Integer, Integer[]> getM_x_i() {
        return m_x_i;
    }
    public Map<Integer, Integer> getM_x_0() {
        return m_x_0;
    }
    public List<Cell> getMinPossibilities() {
        return minPossibilities;
    }
    public int getAmountCells() {
        return amountCells;
    }
    public List<Cell> getToOpen() {
        return toOpen;
    }
    public List<Cell> getToMark() {
        return toMark;
    }
    public List<Group> getIndefinite() {
        return indefinite;
    }
    @Override
    public String toString() {
        return "size=" +list.size();
    }
}
