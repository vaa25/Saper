package saperobj.v2;

import javafx.util.Pair;
import saperobj.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Vlasov Alexander
 * Date: 04.09.13
 * Time: 18:08
 * To change this template use File | Settings | File Templates.
 */

/** Ячейка на поле */
public class Cell {

    private int x, y, value;
    /**
     * ячейка открыта и имеет цифру
     */
    private boolean valued;
    /**
     * ячейка отмечена бомбой
     */
    private boolean mine;
    /**
     * ячейка не открыта
     */
    private boolean unknown;
    /**
     * общаяя вероятность
     */
    private double defaultPossibility;
    private double possibility;
    private List<Double>possibilities;
    private List<Cell> neighbours;
    private boolean defaultPos;
    private List<Group>groups;


    public Cell(int x, int y) {
        unknown = true;
        mine = false;
        valued = false;
        neighbours = new ArrayList<>();
        possibilities=new ArrayList<>();
        possibility = -1;
        this.x = x;
        this.y = y;
        groups=new ArrayList<>();
    }
    public void add(Group group){
        groups.add(group);
    }

    /**
     * Добавляет заданную ячейку к списку соседних
     *
     * @param cell заданная ячейка
     */
    public void addNeighbour(Cell cell) {
        neighbours.add(cell);
    }
    public List<Cell>getValued(){
        List<Cell> valued=new ArrayList();
        for (Cell cell : neighbours) {
            if (cell.isValued())valued.add(cell);
        }
        return valued;
    }
    public void setOnePossibility(){
        if (possibility!=0.0&possibility!=100.0)
        possibility= Prob.sum(possibilities)*100;
        defaultPos=false;

    }
    public boolean isDefaultPossibility() {
        return defaultPos;
    }

    public void setDefaultPossibility(double defaultPossibility) {
        this.defaultPossibility = defaultPossibility;
        defaultPos =true;
    }

    public double getPossibility() {
        if (defaultPos)return defaultPossibility;
        else return possibility;
    }

    public boolean isValued() {
        return valued;
    }

    public int getValue() {
        return value;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    /**
     * Устанавливает значение вероятности нахождения мины в ячейке
     *
     * @param possibility вероятность нахождения мины в процентах
     */
//    public void setPossibility(double possibility) {
//        setPossibility(possibility, true);
//
//    }
    public void addPossibility(double possibility){
        possibilities.add(possibility/100);
    }
    public void setPossibility(double possibility) {
        defaultPos=false;
        if (Math.abs(this.possibility - possibility) == 100) {
            try {
                throw new Exception("Possibility 0/100 collision at " + toString());
            } catch (Exception e) {
                e.printStackTrace();
//                System.exit(-1);
            }
        }
        this.possibility =  possibility;

    }

    /**
     * Открывает ячейку, устанавливая в нее заданное число окружающих мин
     *
     * @param value число окружающих ячейку мин
     *
     * @throws IllegalAccessException
     */
    public void setValue(int value) {
//        if (countMinesAround() > value) throw new IllegalArgumentException("(" + x + "," + y +
//                ") Try to set value less than mines around. value="+value+" minesAround="+countMinesAround());
        this.value = value;
        valued = true;
        unknown = false;
        mine = false;
    }

    public boolean isMine() {
        return mine;
    }

    public void setMine() {
        this.mine = true;
        unknown = false;
        valued = false;
    }

    public boolean isUnknown() {
        return unknown;
    }
    public void setValued() {
        unknown = false;
        mine = false;
        valued = true;
    }
    public void setUnknown() {
        this.unknown = true;
        mine = false;
        valued = false;
    }

    public Point getPoint() {
        return new Point(x, y);
    }

    public boolean check() {
        if (valued && value > 0 && value != countMinesAround()) return false;
        return true;
    }

    /**
     * Подсчитывает количество неизвестных ячеек вокруг этой ячейки
     *
     * @return количество неизвестных ячеек вокруг этой ячейки
     */
    public int countUnknownAround() {
        int result = 0;
        for (Cell neighbour : neighbours) if (neighbour.isUnknown()) result++;
        return result;
    }

    public ArrayList<Cell> getUnknownCells() {
        ArrayList<Cell> cells = new ArrayList<Cell>();
        for (Cell neighbour : neighbours) if (neighbour.isUnknown()) cells.add(neighbour);
        return cells;
    }



    /**
     * Подсчитывает количество известных мин, окружающих эту ячейку
     *
     * @return количество известных мин, окружающих эту ячейку
     */
    public int countMinesAround() {
        int result = 0;
        for (Cell neighbour : neighbours) if (neighbour.isMine()) result++;
        return result;
    }

    @Override
    public String toString() {
        String string = "(" + x + "," + y + ")=" + (mine ? "mine" : unknown ? ("unknown, " + getPossibility() + "%") : value);
        return string;
    }
    public String toStringShort(){
        String string;
        if (isMine())string="*";
        else if (isUnknown())string="`";
        else if (value==0)string=" ";
        else string=Integer.toString(value);
        return string;
    }



    public Pair<Integer, Integer> getCoords() {
        return new Pair<>(x,y);
    }

    public List<Group> getGroups() {
        return groups;
    }

    public boolean hasUnknownAround() {
        for (Cell neighbour : neighbours) if (neighbour.isUnknown()) return true;
        return false;
    }
}
