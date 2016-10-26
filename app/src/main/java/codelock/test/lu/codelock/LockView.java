package codelock.test.lu.codelock;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/10/18 0018.
 */
public class LockView extends View {
    public static final int num_lock = 4;
    private String secret = "1234";

    private final int STATE_IDEL = 0;
    private final int STATE_MOVING = 1;
    private final int STATE_MOVE_BINGO = -10;
    private final int STATE_MOVE_WRONG = -20;

    private int state = STATE_IDEL;
    private Context mContext;
    private Paint mPaint;
    private int mCellWidth;
    private int mCenterWidth;
    private ArrayList<LockBean> mKeyBeans = new ArrayList<LockBean>();
    private ArrayList<LockBean> mAllBeans = new ArrayList<LockBean>();
    private int mLastMeasureX;
    private int x, y;
    private boolean once = false;

    public LockView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public LockView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LockView(Context context) {
        this(context, null);
    }

    private void init(Context context) {
        mContext = context;
        mPaint = new Paint();
        mPaint.setColor(Color.BLUE);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
    }

    private void initAllBeans() {
        mAllBeans.clear();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                LockBean bean = new LockBean(j * mCellWidth + mCellWidth / 2, i * mCellWidth + mCellWidth / 2, mCenterWidth / 2, (i * 3 + j + 1));
                mAllBeans.add(bean);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int w = MeasureSpec.getSize(widthMeasureSpec);
        int h = MeasureSpec.getSize(heightMeasureSpec);
        int x = Math.min(w, h);
        if (x >= 0 && x != mLastMeasureX) {
            mCellWidth = x / 3;
            mCenterWidth = mCellWidth / 2;
            mLastMeasureX = x;
            initAllBeans();
        }
        setMeasuredDimension(x, x);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        x = (int) event.getX();
        y = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (once || state != STATE_IDEL) {
                    return false;
                }

                once = true;
                state = STATE_MOVING;
                checkXY(x, y);
                return true;

            case MotionEvent.ACTION_MOVE:
                if (!once) {
                    break;
                }
                if (state == STATE_MOVING) {
                    checkXY(x, y);
                } else {
                    this.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            state = STATE_IDEL;
                            clear();
                            invalidate();
                        }
                    }, 1000);
                    once = false;
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (state == STATE_MOVING) {
                    state = STATE_IDEL;
                    clear();
                    once = false;
                }

                break;
        }
        invalidate();
        return super.onTouchEvent(event);
    }

    private boolean checkXY(int x, int y) {
        for (int i = 0; i < mAllBeans.size(); i++) {
            LockBean lockBean = mAllBeans.get(i);
            if (lockBean.isInside(x, y)) {
                if (!mKeyBeans.contains(lockBean)) {
                    mKeyBeans.add(lockBean);
                    checkLock();
                }
                return true;
            }
        }
        return false;
    }

    private void checkLock() {
        if (mKeyBeans.size() == num_lock) {
            if (getResult().equals(secret)) {
                state = STATE_MOVE_BINGO;
            } else {
                state = STATE_MOVE_WRONG;
            }
        } else {
            state = STATE_MOVING;
        }
    }

    private void clear() {
        mKeyBeans.clear();
    }

    private String getResult() {
        if (null == mKeyBeans) {
            return "";
        }
        StringBuffer sb = new StringBuffer("");
        for (LockBean bean :
                mKeyBeans) {
            sb.append(bean.getValue() + "");
        }
        return sb.toString();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.BLUE);
        int color = Color.WHITE;
        if (state == STATE_MOVE_BINGO) {
            color = Color.GREEN;
        } else if (state == STATE_MOVE_WRONG) {
            color = Color.RED;
        }
        for (LockBean lockBean :
                mAllBeans) {
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(5);
            mPaint.setColor(color);
            canvas.drawCircle(lockBean.x, lockBean.y, (float) (mCellWidth / 2 * 0.8), mPaint);
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(Color.WHITE);
            canvas.drawCircle(lockBean.x, lockBean.y, (float) (mCenterWidth / 2), mPaint);
        }

        if (state == STATE_MOVING) {
            mPaint.setColor(Color.YELLOW);
        } else if (state == STATE_MOVE_BINGO) {
            mPaint.setColor(Color.GREEN);
        } else if (state == STATE_MOVE_WRONG) {
            mPaint.setColor(Color.RED);
        }

        if (mKeyBeans.size() > 0) {
            for (int i = 0; i < mKeyBeans.size(); i++) {
                LockBean lockBean = mKeyBeans.get(i);
                mPaint.setStyle(Paint.Style.FILL);
                canvas.drawCircle(lockBean.x, lockBean.y, (float) (mCenterWidth / 2 * 0.8), mPaint);
            }
            Path path = new Path();
            for (int i = 0; i < mKeyBeans.size(); i++) {
                LockBean bean = mKeyBeans.get(i);
                if (0 == i) {
                    path.moveTo(bean.x, bean.y);
                } else {
                    path.lineTo(bean.x, bean.y);
                }
            }
            if (state == STATE_MOVING) {
                path.lineTo(x, y);
            }
            mPaint.setStrokeWidth(25);
            mPaint.setStyle(Paint.Style.STROKE);
            canvas.drawPath(path, mPaint);
        }
    }
}
