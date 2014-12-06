package saperobj.v2;

import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: Vlasov Alexander
 * Date: 06.09.13
 * Time: 11:51
 * To change this template use File | Settings | File Templates.
 */
public interface Saper {
    /**
     * Устанавливает статус всех ячеек "неизвестный"
     */
    public void clear();

    /**
     * Устанавливает статус ячейки "неизвестный"
     * @param x координата Х
     * @param y координата У
     */
    public void clear(int x, int y);

    /**
     * Устанавливает статус или значение заданной ячейки в зависимости от value
     * @param x координата Х
     * @param y координата У
     * @param value 0-8 - значение открытой ячейки
     *              9 - ячейка с миной
     *              10 - ячейка неизвестна
     */
    public void set(int x, int y, int value);

    /**
     * Возвращает массив новых помеченных ячеек.
     * @return
     */
    public Point[] getMarked();

    /**
     * Возвращает массив рекомендуемых ходов.
     * @return координаты рекомендуемых ходов, или null, если все ячейки уже открыты
     */
    public Point[] getOpen();

    /**
     * Помечает заданную ячейку как заминированную
     * @param x координата Х
     * @param y координата У
     */
    public void mark(int x, int y);

    /**
     * Возвращает значение вероятности нахождения мины в заданной ячейке
     * @param point координаты ячейки типа java.awt.Point
     * @return вероятность нахождения мины в процентах
     */
    public int getPossibility(Point point);

    /**
     * Рудимент от структурной версии
     */
    public void setParameters();
    @Override
    public String toString();


}
