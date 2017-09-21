package com.meitu.mapproject.map;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.meitu.mapproject.MainActivity;

/**
 * @author zhangzq on 2017/9/18.
 */

public class CustomView extends android.support.v7.widget.AppCompatImageView implements IMap {
    float x_down = 0;//点击的初始位置
    float y_down = 0;

    PointF mStartMid = new PointF();//移动之前两点的中点
    PointF mFinalMid = new PointF();//移动之后两点的中点
    float mOldDist = 1f;//两点距离
    float mOldRotation = 0;
    Matrix matrix = new Matrix();
    Matrix matrix1 = new Matrix();
    Matrix mSaveMatrix = new Matrix();
    private int mWidth, mHeight;//设置默认宽高
    private int mLeft, mTop, mRight, mBottom;//设置可移动的边界范围
    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    int mode = NONE;
    boolean matrixCheck = false;
    int mRangeWidth, mRangeHeight; //控件可以动的最大长宽
    private Bitmap mGintama;//控件的图片
    private Bitmap mBitmap;//控件的背景
    private Bitmap mFinalBitmap;
    private int num;
    private boolean flag = false;
    private float mSensity=1;


    public CustomView(MainActivity context, int width, int height) {
        super(context);
        //设置画布的长宽
        mRangeWidth = width;
        mRangeHeight = height;
        matrix = new Matrix();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(mRangeWidth, mRangeHeight);
    }


    protected void onDraw(Canvas canvas) {
        if (mGintama == null) {
            mGintama = ((BitmapDrawable) this.getDrawable()).getBitmap();
            //如果有背景则对图片进行合成
            mFinalBitmap = mergeBitmap(mBitmap, mGintama);
        }
        canvas.save();
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG| Paint.FILTER_BITMAP_FLAG));
        canvas.drawBitmap(mFinalBitmap, matrix, null);
        canvas.restore();
    }

    //对图片进行合成
    private Bitmap mergeBitmap(Bitmap sBitmap, Bitmap oBitmap) {
        sBitmap = zoomImg(sBitmap, mWidth, mHeight);
        oBitmap = zoomImg(oBitmap, mWidth, mHeight);
        if (sBitmap != null) {
            Canvas canvas1 = new Canvas(sBitmap);
            canvas1.drawBitmap(oBitmap, 0, 0, null);
            return sBitmap;
        } else {
            return oBitmap;
        }
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        return super.dispatchTouchEvent(event);
    }

    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mode = DRAG;
                x_down = event.getX();
                y_down = event.getY();
                mSaveMatrix.set(matrix);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                mode = ZOOM;
                mOldDist = spacing(event);
                mOldRotation = rotation(event);
                mSaveMatrix.set(matrix);
//                midPoint(mStartMid, event);
                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == ZOOM) {
                    matrix1.set(mSaveMatrix);
                    float rotation = rotation(event) - mOldRotation;
                    float newDist = spacing(event);
                    float finalDist = (newDist-mOldDist)*mSensity+mOldDist;
                    float scale = finalDist / mOldDist;
                    getCenter(mFinalMid,matrix1,mFinalBitmap);
                    matrix1.postScale(scale, scale, mFinalMid.x, mFinalMid.y);// 缩放
                    matrix1.postRotate(rotation*mSensity, mFinalMid.x, mFinalMid.y);//旋转
                    matrixCheck = matrixCheck();
                    if (matrixCheck == false) {
                        matrix.set(matrix1);
                        invalidate();
                    }
                } else if (mode == DRAG) {
                    matrix1.set(mSaveMatrix);
                    matrix1.postTranslate((event.getX() - x_down)*mSensity, (event.getY()
                            - y_down)*mSensity);//平移 
                    matrixCheck = matrixCheck();
                    if (matrixCheck == false) {
                        matrix.set(matrix1);
                        invalidate();
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                break;
        }
        return true;
    }

    public boolean isContains(float x,float y) {
        float[] f = new float[9];
        matrix.getValues(f);//图片4个顶点的坐标  
        float x1 = f[0] * 0 + f[1] * 0 + f[2];
        float y1 = f[3] * 0 + f[4] * 0 + f[5];
        float x2 = f[0] * mFinalBitmap.getWidth() + f[1] * 0 + f[2];
        float y2 = f[3] * mFinalBitmap.getWidth() + f[4] * 0 + f[5];
        float x3 = f[0] * 0 + f[1] * mFinalBitmap.getHeight() + f[2];
        float y3 = f[3] * 0 + f[4] * mFinalBitmap.getHeight() + f[5];
        float x4 = f[0] * mFinalBitmap.getWidth() + f[1] * mFinalBitmap.getHeight() + f[2];
        float y4 = f[3] * mFinalBitmap.getWidth() + f[4] * mFinalBitmap.getHeight() + f[5];
        if (fun(x1,y1,x2,y2,x,y)<0&&fun(x2,y2,x4,y4,x,y)<0
                &&fun(x4,y4,x3,y3,x,y)<0&&fun(x3,y3,x1,y1,x,y)<0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断当前控件是否越界
     *
     * @return
     */
    private boolean matrixCheck() {
        float[] f = new float[9];
        matrix1.getValues(f);//图片4个顶点的坐标  
        float x1 = f[0] * 0 + f[1] * 0 + f[2];
        float y1 = f[3] * 0 + f[4] * 0 + f[5];
        float x2 = f[0] * mFinalBitmap.getWidth() + f[1] * 0 + f[2];
        float y2 = f[3] * mFinalBitmap.getWidth() + f[4] * 0 + f[5];
        float x3 = f[0] * 0 + f[1] * mFinalBitmap.getHeight() + f[2];
        float y3 = f[3] * 0 + f[4] * mFinalBitmap.getHeight() + f[5];
        float x4 = f[0] * mFinalBitmap.getWidth() + f[1] * mFinalBitmap.getHeight() + f[2];
        float y4 = f[3] * mFinalBitmap.getWidth() + f[4] * mFinalBitmap.getHeight() + f[5];
        //图片现宽度  
        double width = Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));

        //出界判断  
        if ((x1 < mLeft && x2 < mLeft && x3 < mLeft && x4 < mLeft)
                || (x1 > mRight && x2 > mRight && x3 > mRight && x4 > mRight)
                || (y1 < mTop && y2 < mTop && y3 < mTop && y4 < mTop)
                || (y1 > mBottom && y2 > mBottom && y3 > mBottom && y4 > mBottom)) {
            return true;
        }
        return false;
    }

    /**
     * 触碰两点之间的距离
     */
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * 选转的角度
     *
     * @param event
     * @return
     */
    private float rotation(MotionEvent event) {
        double delta_x = (event.getX(0) - event.getX(1));
        double delta_y = (event.getY(0) - event.getY(1));
        double radians = Math.atan2(delta_y, delta_x);
        return (float) Math.toDegrees(radians);
    }

    /**
     * 求图像的中心位置
     * @param point
     * @param matrix
     * @param bitmap
     */
    public void getCenter(PointF point,Matrix matrix,Bitmap bitmap){
        RectF rectF = new RectF();
        rectF.set(0,0,bitmap.getWidth(),bitmap.getHeight());
        matrix.mapRect(rectF);
        //其实在此处就可以获得中心!
        float centerX = rectF.centerX();
        float centerY = rectF.centerY();
        point.set(centerX,centerY);
    }

    /**
     * 将bitmap设置为指定长宽
     *
     * @param bm
     * @param newWidth
     * @param newHeight
     * @return
     */
    public Bitmap zoomImg(Bitmap bm, int newWidth, int newHeight) {
        // 获得图片的宽高   
        int width = bm.getWidth();
        int height = bm.getHeight();
        // 计算缩放比例   
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数   
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片   www.2cto.com
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        return newbm;
    }

    /**
     * 设置空间的移动范围
     *
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    @Override
    public void setMoveRange(int left, int top, int right, int bottom) {
        mLeft = left;
        mTop = top;
        mRight = right;
        mBottom = bottom;
    }

    @Override
    public void setBackground(Bitmap bitmap) {
        this.mBitmap = bitmap;
    }

    @Override
    public void setBackground(int resId) {
        this.mBitmap = ((BitmapDrawable) getResources().getDrawable(resId)).getBitmap();
    }

    @Override
    public void setBackground(Drawable d) {
        this.mBitmap = ((BitmapDrawable) d).getBitmap();
    }

    @Override
    public void setSensitivity(float f) {
        mSensity = f;
    }

    /**
     * 设置控件的初始大小
     *
     * @param width
     * @param height
     */
    @Override
    public void setDefaultSize(int width, int height) {
        mWidth = width;
        mHeight = height;
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setLayoutParams(params);
        this.setScaleType(ScaleType.CENTER);
        matrix.postTranslate(mRangeWidth / 2 - mWidth / 2, mRangeHeight / 2 - mHeight / 2);
    }

    /**
     * 获取最终的贴图图片
     *
     * @return
     */
    @Override
    public Bitmap getFinalBitmap() {
        return mFinalBitmap;
    }

    /**
     * 获取最终变换得到的matrix
     *
     * @param width
     * @param height
     * @return
     */
    public Matrix getMatrix(int width, int height) {
        if (width == mRangeWidth) {
            matrix.postTranslate(0, -(mRangeHeight - height) / 2f);
        } else if (height == mRangeHeight) {
            matrix.postTranslate(-(mRangeWidth - width) / 2f, 0);
        }
        return matrix;
    }

    /**
     * 判断是否出界，假设有四个点A,B,C,D，AB:fun(A,B,T),BC:fun(B,C,T)
     * CD:fun(C,D,T),DA:fun(D,A,T),四个同时为负值时即在矩形内部，原理根据向量进行判断
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @param x
     * @param y
     * @return
     */
    private float fun(float x1,float y1,float x2,float y2,float x,float y) {
        return (y-y1)*(x-x2)-(y-y2)*(x-x1);
    }
}
