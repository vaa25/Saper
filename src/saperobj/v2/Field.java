package saperobj.v2;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Vlasov Alexander
 * Date: 03.08.2014
 * Time: 10:41
 * To change this template use File | Settings | File Templates.
 *
 * @author Alexander Vlasov
 */
public class Field implements Saper {
    private PlayField playField;
    private final Cell[][] field;
    private final List<Cell> cells;
    public final int amount;
    public final int width, height;
    private List<Group> groups = new ArrayList<>();
    private List<Island> islands;
    private List<Cell>toOpen=new ArrayList<>();
    private List<Cell>toMark=new ArrayList<>();
    private List<Group>indefinite=new ArrayList<>();
    private int minesLeft;
    private boolean exploded;
    private boolean win;
    private int minesSetted;
    private int valued;
    private Map<Integer,Integer>whole;
    private Map<Integer,Integer>real;
    public static void main(String[] args) {
        Map<Integer,Integer>whole=new TreeMap<>();
        Map<Integer,Integer>real=new TreeMap<>();
        int plays=0;
        int wins=0;
        int loses=0;
        double money=100;
        double pet=1;
        while (true){
            PlayField playField1=new PlayField(30,16,99);
//        playField1.setMine(0,0);
//        playField1.setMine(2,0);
//        playField1.setMine(3,2);
//        playField1.setMine(1,1);
            playField1.setMines();
            Field field1=new Field(playField1);
            if (field1.play()){
                wins++;
                if (field1.getOpenPercentage()==100)money+=pet*2.5;
                else money+=pet*1.1;


            }
            else {
                loses++;
                int s= (int)Math.round(field1.getOpenPercentage()/10-0.5);
                money+=pet*0.05*s;
            }
            addMapToMap(whole,field1.whole);
            addMapToMap(real,field1.real);

            if (++plays%100==0){

                System.out.println("Played games: "+(plays)+"   wins "+(100*wins/plays)+" %    Money = "+money);
                printStatistic(whole,real);
                System.out.println();
            }

        }
    }
    private static void printStatistic(Map<Integer,Integer>whole, Map<Integer,Integer>real){
        System.out.print("Clicked ");
        for (int i = 0; i < 101; i++) {
            if (whole.containsKey(i)){
                System.out.print(getNumber(whole.get(i)));
            }
        }
        System.out.println();
        System.out.print("Theory  ");
        for (int i = 0; i < 101; i++) {
            if (whole.containsKey(i)){
                System.out.print(getNumber(i));
            }
        }
        System.out.println();
        System.out.print("Reality ");
        for (int i = 0; i < 101; i++) {
            if (whole.containsKey(i)){
                int value=0;
                if (real.containsKey(i)){
                    value=(int)(100.0*real.get(i)/whole.get(i));
                }
                System.out.print(getNumber(value));
            }
        }
    }
    private static String getNumber(int number){
        if (number>=1000)return " "+number;
        if (number>=100)return "  "+number;
        if (number>=10)return "   "+number;
        if (number>=0)return "    "+number;
        return null;
    }
    private static<K> void addMapToMap(Map<K,Integer> to, Map<K,Integer> added){
        for (Map.Entry<K, Integer> entry : added.entrySet()) {
            K key=entry.getKey();
            int value=added.get(key);
            if (to.containsKey(key)){
                value+=to.get(key);
            }
            to.put(key,value);
        }
    }
    public Field(PlayField playField) {
        this(playField.width,playField.height,playField.amount);
        this.playField = playField;
    }
    public Field(int width, int height, int amount1) {
        this.amount = amount1;
        this.width = width;
        this.height = height;
        field = new Cell[width][height];
        cells = new LinkedList<>();
        islands =new ArrayList<>();
        createCells();
        setCellsNeighbours();

    }
    public void setPlayField(PlayField playField) {
        this.playField = playField;
    }
    private void createCells(){
        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++) {
                field[x][y] = new Cell(x, y);
                cells.add(field[x][y]);
            }

    }
    private void setCellsNeighbours(){
        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++) {
                if (x > 0) field[x][y].addNeighbour(field[x - 1][y]);
                if (y > 0) field[x][y].addNeighbour(field[x][y - 1]);
                if (x > 0 && y > 0) field[x][y].addNeighbour(field[x - 1][y - 1]);
                if (x < width - 1) field[x][y].addNeighbour(field[x + 1][y]);
                if (y < height - 1) field[x][y].addNeighbour(field[x][y + 1]);
                if (x < width - 1 && y < height - 1) field[x][y].addNeighbour(field[x + 1][y + 1]);
                if (x > 0 && y < height - 1) field[x][y].addNeighbour(field[x - 1][y + 1]);
                if (x < width - 1 && y > 0) field[x][y].addNeighbour(field[x + 1][y - 1]);
            }

    }
    private void scanPlayField(){
        minesSetted=0;
        valued=0;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int value=playField.get(x,y);
                if (value==PlayField.BOMB||value== PlayField.EXPLODE)exploded=true;
                else if (value== PlayField.UNOPENED)field[x][y].setUnknown();
                else if (value== PlayField.FLAG){
                    field[x][y].setMine();
                    minesSetted++;
                }
                else {
                    field[x][y].setValue(value);
                    valued++;
                }
            }
        }
    }
    private boolean isWon(){
        for (Cell cell : cells) {
            if (cell.isUnknown())return false;
        }
        return true;
    }
    private void setGroups(){
        groups.clear();
        for (Cell cell : cells) {
            if (cell.isValued()&&cell.hasUnknownAround()){
                groups.add(new Group(cell.getUnknownCells(),cell.getValue()-cell.countMinesAround()));
            }
        }

    }
    private void optimizeIslands(){
        for (Island island : islands) {
            island.optimize();
        }
    }
    private void divideGroupsToIslands(List<Group>groups){
        islands.clear();
        for (Group group : groups) {
            boolean added=false;
            Island addedTo=null;
            for (int i = 0; i < islands.size(); i++) {
                Island currentIsland = islands.get(i);
                if (currentIsland.isCross(group)){
                    if (!added){
                        currentIsland.add(group);
                        added=true;
                        addedTo= currentIsland;
                    }
                    else {
                        addedTo.add(currentIsland);
                        islands.remove(i);
                    }
                }
            }
            if (!added){
                islands.add(new Island(group));
            }
        }

    }
    private boolean play() {
        whole=new HashMap<>();
        real=new HashMap<>();
//        playField.print();
        int turn = 0;
        int x = -1;
        int y = -1;
//        Random random = new Random();
//        y = random.nextInt(height);
//        x = random.nextInt(width);
//        System.out.println("open (" + x + "," + y + ")");
//        playField.click(x, y);
        while (!exploded && !win) {
            islands.clear();
            indefinite.clear();
            turn++;
//            System.out.println("turn " + (turn));
            setGroups();
            divideGroupsToIslands(groups);
            optimizeIslands();
            determineMarkOpenIndefinite();
            if (toOpen.size() != 0 || toMark.size() != 0) {
                divideGroupsToIslands(indefinite);
                optimizeIslands();
            }
            double clickedPossibility=-1;
            if (clickPlayField()) ;
            else {
                if (480-minesSetted-valued!=getUnknownCells().size())
                    System.exit(-2);
                if (isWon()) System.out.println("won");
                int deepCellsAmount = getDeepCellsAmount();  // число глубинных клеток
                minesLeft=amount-minesSetted;
                double average=0;  // среднее число мин в островах
                double minPossibility=100;
                List<Cell>minPossibilities=new ArrayList<>();
                for (Island island : islands) {
                    island.resolve();
                    double possibility=island.getMinPossibility();
                    if (possibility==minPossibility){
                        minPossibilities.addAll(island.getMinPossibilities());
                    }
                    if (possibility<minPossibility){
                        minPossibility=possibility;
                        minPossibilities=island.getMinPossibilities();
                    }
                    Map<Integer, Integer>mx0= island.getM_x_0();
                    int whole=0;
                    for (Integer integer : mx0.values()) {
                        whole+=integer;
                    }
                    for (Map.Entry<Integer, Integer> entry : mx0.entrySet()) {
                        average+=(1.0*entry.getKey()*entry.getValue()/whole);
                    }

                }
                if (480-minesSetted-valued!=getUnknownCells().size())
                    System.exit(-3);
                double deepPossibility=100;
                if (deepCellsAmount>0)deepPossibility=100.0*(minesLeft-average)/deepCellsAmount;
                List<Cell>deepCells=getDeepCells();
                setPossibility(deepCells,deepPossibility);
                Cell toClick=null;
                if (minPossibility<deepPossibility){
                    if (minPossibilities.size()>0)                   toClick=getOneOf(minPossibilities);
                    else System.out.println();
                }
                if (minPossibility>deepPossibility)toClick=getOneOf(deepCells);
                if (minPossibility==deepPossibility)toClick=getOneOf(minPossibilities, getDeepCells());
                if (toClick == null) {
                    System.out.println();
                }
                x=toClick.getX();
                y=toClick.getY();

                clickedPossibility=toClick.getPossibility();
//                System.out.println("open (" + x + "," + y + "),   possibility= "+clickedPossibility);
                playField.click(x, y);
                mapIncValue(whole,(int)clickedPossibility);
            }
            scanPlayField();
//            print();
            win = isWon();
            if (exploded)mapIncValue(real,(int)clickedPossibility);
        }
        boolean res=false;
        if (exploded) {
//            System.out.println("Explode!!! (" + x + "," + y + ")");

            res= false;
            if (turn==1)res=true;
        }
        if (win) {
//            System.out.println("Win!!! (" + x + "," + y + ")");
            res= true;
        }
        return res;
    }
    private<K> void  mapIncValue(Map<K, Integer> map,K key ){
        int value=0;
        if (map.containsKey(key))value=map.get(key);
        map.put(key,++value);
    }
    private void setPossibility(List<Cell>list, double possibility){
        for (Cell cell : list) {
            cell.setPossibility(possibility);
        }
    }
    private Cell getOneOf(List<Cell>list){
        Random random=new Random();
        return list.get(random.nextInt(list.size()));
    }
    private Cell getOneOf(List<Cell>list1, List<Cell>list2){
        List<Cell>list=new ArrayList<>(list1);
        list.addAll(list2);
        return getOneOf(list);
    }
    private int countUnknownCells(){
        int res=0;
        for (Cell cell : cells) {
            if (cell.isUnknown())res++;
        }
        return res;
    }
    public int getOpenPercentage(){
        return 100*countValuedCells()/(width*height-amount);
    }
    private int countValuedCells(){
        int res=0;
        for (Cell cell : cells) {
            if (cell.isValued())res++;
        }
        return res;
    }
    private List<Cell> getUnknownCells(){
        List<Cell> res=new ArrayList<>();
        for (Cell cell : cells) {
            if (cell.isUnknown())res.add(cell);
        }
        return res;
    }
    private int getDeepCellsAmount(){
        int u=countUnknownCells();
        for (Island island : islands) {
            u=u-island.getAmountCells()-toMark.size()-toOpen.size();
        }
        return u;
    }
    private List<Cell> getDeepCells(){
        List<Cell>unknown=getUnknownCells();
        for (Island island : islands) {
            unknown.removeAll(island.getIndefiniteCells());
        }
        return unknown;
    }
    private void determineMarkOpenIndefinite(){
        indefinite.clear();
        for (Island island : islands) {
            toOpen.addAll(island.getToOpen());
            toMark.addAll(island.getToMark());
            indefinite.addAll(island.getIndefinite());
        }
    }
    private boolean clickPlayField(){
        boolean clicked=false;
        for (Cell cell : toOpen) {
            playField.click(cell.getCoords());
            clicked=true;
        }
        for (Cell cell : toMark) {
            playField.click2(cell.getCoords());
            clicked=true;
        }
        toMark.clear();
        toOpen.clear();
        return clicked;
    }
    public void print(){
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (field[x][y].isMine())System.out.print("* ");
                else
                if (field[x][y].isUnknown())System.out.print("  ");
                else System.out.print(field[x][y].getValue()+" ");
            }
            System.out.println();

        }
        System.out.println();
    }

    /**
     * Устанавливает статус всех ячеек "неизвестный"
     */
    @Override
    public void clear() {
        for (Cell cell : cells) {
            cell.setUnknown();
        }
    }

    /**
     * Устанавливает статус ячейки "неизвестный"
     *
     * @param x координата Х
     * @param y координата У
     */
    @Override
    public void clear(int x, int y) {
        field[x][y].setUnknown();
    }

    /**
     * Устанавливает статус или значение заданной ячейки в зависимости от value
     *
     * @param x     координата Х
     * @param y     координата У
     * @param value 0-8 - значение открытой ячейки
     *              9 - ячейка с миной
     */
    @Override
    public void set(int x, int y, int value) {
        if (value==9)field[x][y].setMine();
        if (value<9&&value>=0)field[x][y].setValue(value);
    }

    /**
     * Возвращает массив новых помеченных ячеек.
     *
     * @return
     */
    @Override
    public Point[] getMarked() {
        Point[] points=new Point[toMark.size()];
        for (int i = 0; i < toMark.size(); i++) {
            points[i]=new Point(toMark.get(i).getX(),toMark.get(i).getY());
        }
        return points;
    }

    /**
     * Возвращает массив рекомендуемых ходов.
     *
     * @return координаты рекомендуемых ходов, или null, если все ячейки уже открыты
     */
    @Override
    public Point[] getOpen() {
        Point[] points=new Point[toOpen.size()];
        for (int i = 0; i < toOpen.size(); i++) {
            points[i]=new Point(toOpen.get(i).getX(),toOpen.get(i).getY());
        }
        return points;
    }

    /**
     * Помечает заданную ячейку как заминированную
     *
     * @param x координата Х
     * @param y координата У
     */
    @Override
    public void mark(int x, int y) {
        field[x][y].setMine();
    }

    /**
     * Возвращает значение вероятности нахождения мины в заданной ячейке
     *
     * @param point координаты ячейки типа java.awt.Point
     *
     * @return вероятность нахождения мины в процентах
     */
    @Override
    public int getPossibility(Point point) {
        return (int)field[point.x][point.y].getPossibility();
    }

    /**
     * Рудимент от структурной версии
     */
    @Override
    public void setParameters() {

    }
}
