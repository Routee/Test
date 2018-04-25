package com.routee.game.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.routee.game.R;
import com.routee.game.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.view.View.MeasureSpec.AT_MOST;

/**
 * @author: Routee
 * @date 2018/4/24
 * @mail wangc4@qianbaocard.com
 * ------------1.本类由Routee开发,阅读、修改时请勿随意修改代码排版格式后提交到git。
 * ------------2.阅读本类时，发现不合理请及时指正.
 * ------------3.如需在本类内部进行修改,请先联系Routee,若未经同意修改此类后造成损失本人概不负责。
 */

public class NinePicGameView extends RelativeLayout {

    private int             mSeparatorWidth;        //分割线宽度
    private int             mMinSize;               //view最小尺寸
    private int             mColumns;               //view的行数和列数
    private ArrayList<Rect> mRects;                 //每个小块的区域
    private List<Units>     mBms;                   //所有小块的Bitmap对象
    private float           mDownX;                 //手指按下X轴坐标
    private float           mDownY;                 //手指按下Y轴坐标
    private int             mCurrentItem;           //手指按下小块条目
    private String          mImagePath;             //图片文件地址
    private float           mX;                     //当前手指滑动到X位置
    private float           mY;                     //当前手指滑动到Y位置
    private int             mBlankItem;             //白块条目
    private boolean         mMoveAble;              //手指点按小块是否可滑动
    private int             mMoveOrientation;       //记录手指滑动方向
    private boolean         isMoveFinish;           //记录滑块移动结束状态
    private FinishListener  mListener;              //完成拼图完成监听

    public NinePicGameView(Context context) {
        this(context, null);
    }

    public NinePicGameView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NinePicGameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(attrs);
    }

    private void initView(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.NinePicGameView);
        mColumns = a.getInteger(R.styleable.NinePicGameView_columns, 3);
        mSeparatorWidth = Utils.dp2px(getContext(), a.getInteger(R.styleable.NinePicGameView_separator_line_width, 1));
        a.recycle();
    }

    public void setImage(String path) {
        mImagePath = path;
        removeAllViews();
        initRects();
        mBms = calcBitmap();
        for (int i = 0; i < mBms.size(); i++) {
            ImageView imageView = new ImageView(getContext());
            imageView.setImageBitmap(mBms.get(i).bm);
            RelativeLayout.LayoutParams lp = new LayoutParams(getWidth() / mColumns, getHeight() / mColumns);
            imageView.setLayoutParams(lp);
            addView(imageView);
        }
        invalidate();
    }

    private void initRects() {
        mRects = new ArrayList<>();
        int unitWidth = getWidth() / mColumns;
        int unitHeight = getHeight() / mColumns;

        for (int i = 0; i < mColumns * mColumns; i++) {
            mRects.add(new Rect(i % mColumns * unitWidth
                                       , i / mColumns * unitHeight
                                       , i % mColumns * unitWidth + unitWidth
                                       , i / mColumns * unitHeight + unitHeight));
        }
    }

    private List<Units> calcBitmap() {
        if (TextUtils.isEmpty(mImagePath)) {
            return null;
        }
        Bitmap bitmap = BitmapFactory.decodeFile(mImagePath);
        List<Units> bms = new ArrayList<>();
        int bw = bitmap.getWidth();
        int bh = bitmap.getHeight();
        float scale = 1;
        if ((getWidth() * 1.0f) / (bw * 1.0f) > (getHeight() * 1.0f) / (bh * 1.0f)) {
            scale = (getWidth() * 1.0f) / (bw * 1.0f);
        } else {
            scale = (getHeight() * 1.0f) / (bh * 1.0f);
        }
        Matrix matrix = new Matrix();
        matrix.setScale(scale, scale);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                bitmap.getHeight(), matrix, true);
        bw = bitmap.getWidth();
        bh = bitmap.getHeight();
        if (bw * 1.f / bh > getWidth() * 1.f / getHeight()) {
            bitmap = Bitmap.createBitmap(bitmap, (bw - getWidth()) / 2, 0, getWidth(), getHeight());
        } else {
            bitmap = Bitmap.createBitmap(bitmap, 0, (bh - getHeight()) / 2, getWidth(), getHeight());
        }
        //        if (BuildConfig.DEBUG) {
        //            android.util.Log.e("xxxxxx", "bitmapWidth = " + bitmap.getWidth()
        //                                                 + "\r\nbitmapHeight = " + bitmap.getHeight()
        //                                                 + "\r\nviewWidth = " + getWidth()
        //                                                 + "\r\nviewHeight = " + getHeight());
        //        }
        for (int i = 0; i < mColumns * mColumns; i++) {
            if (i != mColumns * mColumns - 1) {
                bms.add(new Units(Bitmap.createBitmap(bitmap, i % mColumns * getWidth() / mColumns, i / mColumns * getHeight() / mColumns, getWidth() / mColumns, getHeight() / mColumns), i));
            } else {
                Collections.shuffle(bms);
                bms.add(new Units(Bitmap.createBitmap(getWidth() / mColumns, getHeight() / mColumns, Bitmap.Config.ALPHA_8), i));
            }
        }
        return bms;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        if (widthSpecMode == AT_MOST && heightSpecMode == AT_MOST) {
            setMeasuredDimension(mMinSize, mMinSize);
        } else if (widthMeasureSpec == AT_MOST) {
            setMeasuredDimension(mMinSize, heightSpecSize);
        } else if (heightMeasureSpec == AT_MOST) {
            setMeasuredDimension(widthSpecSize, mMinSize);
        }
        measureChild();
    }

    private void measureChild() {
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).measure(getWidth() / mColumns - mSeparatorWidth, getHeight() / mColumns - mSeparatorWidth);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getPointerCount() > 1 || TextUtils.isEmpty(mImagePath)) {
            return false;
        }
        mX = event.getX();
        mY = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = event.getX();
                mDownY = event.getY();
                calcCurrentItem();
                break;
            case MotionEvent.ACTION_MOVE:
                calcOrientation();
                calcMoveAble();
                calcMoveFinish();
                if (!mMoveAble) {
                    return false;
                }
                requestLayout();
                break;
            case MotionEvent.ACTION_UP:
                if (!mMoveAble) {
                    return false;
                }
                if (isMoveFinish) {
                    Collections.swap(mBms, mCurrentItem, mBlankItem);
                    resetChild();
                } else {
                    calcNeedMoveBlock();
                }
                calcResult();
                requestLayout();
                break;
            default:
                break;
        }
        return true;
    }

    private void calcResult() {
        // TODO: 2018/4/25 检查运行游戏结果
    }

    private void resetChild() {
        mCurrentItem = 0;
        mBlankItem = 0;
        mX = 0;
        mY = 0;
        mDownX = 0;
        mDownY = 0;
        mMoveAble = false;
        removeAllViews();
        for (int i = 0; i < mBms.size(); i++) {
            ImageView imageView = new ImageView(getContext());
            imageView.setImageBitmap(mBms.get(i).bm);
            RelativeLayout.LayoutParams lp = new LayoutParams(getWidth() / mColumns, getHeight() / mColumns);
            imageView.setLayoutParams(lp);
            addView(imageView);
        }
        invalidate();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        for (int i = 0; i < getChildCount(); i++) {
            if (mCurrentItem == i) {
                if (isMoveFinish) {
                    getChildAt(i).layout(mRects.get(mBlankItem).left + mSeparatorWidth / 2
                            , mRects.get(mBlankItem).top + mSeparatorWidth / 2
                            , mRects.get(mBlankItem).right - mSeparatorWidth / 2
                            , mRects.get(mBlankItem).bottom - mSeparatorWidth / 2);
                } else {
                    switch (mMoveOrientation) {
                        case 1:
                            getChildAt(i).layout((int) (mX - mDownX + mRects.get(mCurrentItem).left) + mSeparatorWidth / 2
                                    , mRects.get(mCurrentItem).top + mSeparatorWidth / 2
                                    , (int) (mX - mDownX + mRects.get(mCurrentItem).right) - mSeparatorWidth / 2
                                    , mRects.get(mCurrentItem).bottom - mSeparatorWidth / 2);
                            break;
                        case 3:
                            getChildAt(i).layout((int) (mX - mDownX + mRects.get(mCurrentItem).left) + mSeparatorWidth / 2
                                    , mRects.get(mCurrentItem).top + mSeparatorWidth / 2
                                    , (int) (mX - mDownX + mRects.get(mCurrentItem).right) - mSeparatorWidth / 2
                                    , mRects.get(mCurrentItem).bottom - mSeparatorWidth / 2);
                            break;
                        case 2:
                            getChildAt(i).layout(mRects.get(mCurrentItem).left + mSeparatorWidth / 2
                                    , (int) (mY - mDownY + mRects.get(mCurrentItem).top) + mSeparatorWidth / 2
                                    , mRects.get(mCurrentItem).right - mSeparatorWidth / 2
                                    , (int) (mY - mDownY + mRects.get(mCurrentItem).bottom) - mSeparatorWidth / 2);
                            break;
                        case 4:
                            getChildAt(i).layout(mRects.get(mCurrentItem).left + mSeparatorWidth / 2
                                    , (int) (mY - mDownY + mRects.get(mCurrentItem).top) + mSeparatorWidth / 2
                                    , mRects.get(mCurrentItem).right - mSeparatorWidth / 2
                                    , (int) (mY - mDownY + mRects.get(mCurrentItem).bottom) - mSeparatorWidth / 2);
                            break;
                        default:
                            getChildAt(i).layout(mRects.get(i).left + mSeparatorWidth / 2
                                    , mRects.get(i).top + mSeparatorWidth / 2
                                    , mRects.get(i).right - mSeparatorWidth / 2
                                    , mRects.get(i).bottom - mSeparatorWidth / 2);
                            break;
                    }
                }
            } else {
                getChildAt(i).layout(mRects.get(i).left + mSeparatorWidth / 2
                        , mRects.get(i).top + mSeparatorWidth / 2
                        , mRects.get(i).right - mSeparatorWidth / 2
                        , mRects.get(i).bottom - mSeparatorWidth / 2);
            }
        }
    }

    /**
     * 计算滑动方向
     */
    private void calcOrientation() {
        if (Math.abs(mX - mDownX) > Math.abs(mY - mDownY) && mX - mDownX < 0) {
            mMoveOrientation = 1;
        } else if (Math.abs(mX - mDownX) > Math.abs(mY - mDownY) && mX - mDownX > 0) {
            mMoveOrientation = 3;
        } else if (Math.abs(mX - mDownX) < Math.abs(mY - mDownY) && mY - mDownY > 0) {
            mMoveOrientation = 4;
        } else {
            mMoveOrientation = 2;
        }
        //        if (BuildConfig.DEBUG) {
        //            android.util.Log.e("xxxxxx", "mMoveOrientation = " + mMoveOrientation);
        //        }
    }

    /**
     * 计算是否可以滑动
     */
    private void calcMoveAble() {
        for (int i = 0; i < mBms.size(); i++) {
            if (mBms.get(i).tag == mColumns * mColumns - 1) {
                mBlankItem = i;
                break;
            }
        }
        if ((mBlankItem + 1) % mColumns == 0) {
            switch (mMoveOrientation) {
                case 2:
                    mMoveAble = mCurrentItem - mColumns == mBlankItem;
                    break;
                case 3:
                    mMoveAble = mCurrentItem + 1 == mBlankItem;
                    break;
                case 4:
                    mMoveAble = mCurrentItem + mColumns == mBlankItem;
                    break;
                case 1:
                default:
                    mMoveAble = false;
                    break;
            }
        } else if ((mBlankItem + 1) % mColumns == 1) {
            switch (mMoveOrientation) {
                case 1:
                    mMoveAble = mCurrentItem - 1 == mBlankItem;
                    break;
                case 2:
                    mMoveAble = mCurrentItem - mColumns == mBlankItem;
                    break;
                case 4:
                    mMoveAble = mCurrentItem + mColumns == mBlankItem;
                    break;
                case 3:
                default:
                    mMoveAble = false;
                    break;
            }
        } else {
            switch (mMoveOrientation) {
                case 1:
                    mMoveAble = mCurrentItem - 1 == mBlankItem;
                    break;
                case 2:
                    mMoveAble = mCurrentItem - mColumns == mBlankItem;
                    break;
                case 3:
                    mMoveAble = mCurrentItem + 1 == mBlankItem;
                    break;
                case 4:
                    mMoveAble = mCurrentItem + mColumns == mBlankItem;
                    break;
                default:
                    mMoveAble = false;
                    break;
            }
        }
    }

    /**
     * 判断滑动是否结束
     */
    private void calcMoveFinish() {
        switch (mMoveOrientation) {
            case 1:
            case 3:
                isMoveFinish = Math.abs(mX - mDownX) > mRects.get(mBlankItem).right - mRects.get(mBlankItem).left;
                break;
            case 2:
            case 4:
                isMoveFinish = Math.abs(mY - mDownY) > mRects.get(mBlankItem).bottom - mRects.get(mBlankItem).top;
                break;
            default:
                break;
        }
    }

    /**
     * 计算是否需要移动图块
     */
    private void calcNeedMoveBlock() {
        switch (mMoveOrientation) {
            case 1:
            case 3:
                isMoveFinish = Math.abs(mX - mDownX) * 3 > Math.abs(mRects.get(mCurrentItem).right - mRects.get(mCurrentItem).left);
                break;
            case 2:
            case 4:
                isMoveFinish = Math.abs(mY - mDownY) * 3 > Math.abs(mRects.get(mCurrentItem).bottom - mRects.get(mCurrentItem).top);
                break;
            default:
                break;
        }
        if (isMoveFinish) {
            Collections.swap(mBms, mCurrentItem, mBlankItem);
            resetChild();
        } else {
            resetChild();
        }
    }

    interface FinishListener {
        void finish();
    }

    public void addFinishListener(FinishListener listener) {
        mListener = listener;
    }

    /**
     * 计算当前点选的方块
     */
    private void calcCurrentItem() {
        if (TextUtils.isEmpty(mImagePath)) {
            return;
        }
        for (int i = 0; i < mRects.size(); i++) {
            if (mRects.get(i).contains((int) mDownX, (int) mDownY)) {
                mCurrentItem = i;
                return;
            }
        }
    }

    class Units {
        Bitmap bm;
        int    tag;

        Units(Bitmap bm, int tag) {
            this.bm = bm;
            this.tag = tag;
        }
    }
}
