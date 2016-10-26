package codelock.test.lu.codelock;

import android.graphics.Rect;

/**
 * Created by Administrator on 2016/10/18 0018.
 */
public class LockBean {

    public LockBean(int x, int y, int length, int value) {
        setX(x);
        setY(y);
        setValue(value);
        Rect rect = new Rect(x - length / 2, y - length / 2, x + length / 2, y + length / 2);
        setRect(rect);
    }

    int value;
    Rect rect;
    int x;
    int y;

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public Rect getRect() {
        return rect;
    }

    public void setRect(Rect rect) {
        this.rect = rect;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }


    public boolean isInside(int x, int y){
        return rect.contains(x, y);
    }

    @Override
    public boolean equals(Object o) {
        return value == ((LockBean)o).getValue();
    }
}
