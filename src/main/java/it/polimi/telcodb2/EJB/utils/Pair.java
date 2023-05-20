package it.polimi.telcodb2.EJB.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to handle easier coupled values
 * @param <X> type of the first element of the pair
 * @param <Y> type of the second element of the pair
 */
public class Pair<X, Y> {

    private X x;
    private Y y;

    public Pair(X x, Y y) {
        this.x = x;
        this.y = y;
    }

    public X getX() {
        return x;
    }

    public void setX(X x) {
        this.x = x;
    }

    public Y getY() {
        return y;
    }

    public void setY(Y y) {
        this.y = y;
    }

    public static <X, Y> List<Pair<X, Y>> createPairList(List<X> xList, List<Y> yList) {
        if (xList.isEmpty() || yList.isEmpty() || xList.size() != yList.size()) {
            return null;
        }
        List<Pair<X, Y>> pairList = new ArrayList<Pair<X, Y>>();
        for (int i=0; i<xList.size(); i++) {
            Pair<X, Y> pair = new Pair<X, Y>(xList.get(i), yList.get(i));
            pairList.add(pair);
        }
        return pairList;
    }
}
