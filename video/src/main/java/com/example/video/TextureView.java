package com.example.video;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import com.example.video.util.MeasureHelper;
import com.example.video.view.PlayFrameLayout;


public class TextureView extends android.view.TextureView {
    protected static final String TAG = "JZResizeTextureView";
    private final MeasureHelper measureHelper;


    private Context context;
    private int currentVideoWidth, currentVideoHeight;
    private Video play;
    public TextureView(Context context, Video play) {
        super(context);
        measureHelper = new MeasureHelper();
        this.context = context;
        this.play=play;
        setOnTouchListener(new ZoomOnTouchListeners());


    }

    public void setVideoSize(int currentVideoWidth, int currentVideoHeight) {

        if (this.currentVideoHeight != currentVideoHeight || this.currentVideoWidth != currentVideoWidth) {
            this.currentVideoWidth = currentVideoWidth;
            this.currentVideoHeight = currentVideoHeight;
            measureHelper.setVideoSize(currentVideoWidth, currentVideoHeight);
            requestLayout();

        }
    }

    public void setVideoAspectRatio(int videoSarNum, int videoSarDen) {
        if (videoSarNum > 0 && videoSarDen > 0) {
            measureHelper.setVideoSampleAspectRatio(videoSarNum, videoSarDen);
            requestLayout();
        }
    }

    public void setAspectRatio(int aspectRatio) {
        measureHelper.setAspectRatio(aspectRatio);
        requestLayout();
    }

    @Override
    public void setRotation(float rotation) {
        if (rotation != getRotation()) {
            this.measureHelper.setVideoRotation((int) rotation);
            super.setRotation(rotation);
        }
    }

    @Override
    public void requestLayout() {
        super.requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        this.measureHelper.doMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(this.measureHelper.getMeasuredWidth(), this.measureHelper.getMeasuredHeight());

    }

    private static final String SUPERSTATE_KEY = "superState";
    private static final String MIN_SCALE_KEY = "minScale";
    private static final String MAX_SCALE_KEY = "maxScale";
    public static float MIN_SCALE = 1f;
    private float minScale = MIN_SCALE;
    //最大放大比例
    private float maxScale = 5f;
    private float saveScale = MIN_SCALE;

    public void setMinScale(float scale) {
        if (scale < 1.0f || scale > maxScale) {
            throw new RuntimeException("minScale can't be lower than 1 or larger than maxScale(" + maxScale + ")");
        } else {
            minScale = scale;
        }
    }

    public void setMaxScale(float scale) {
        if (scale < 1.0f || scale < minScale) {
            throw new RuntimeException("maxScale can't be lower than 1 or minScale(" + minScale + ")");
        } else {
            minScale = scale;
        }
    }

    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    private int mode = NONE;

    private Matrix matrix = new Matrix();

    private ScaleGestureDetector mScaleDetector;
    private float[] m;

    private PointF last = new PointF();
    private PointF start = new PointF();
    private float right, bottom;


    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(SUPERSTATE_KEY, super.onSaveInstanceState());
        bundle.putFloat(MIN_SCALE_KEY, minScale);
        bundle.putFloat(MAX_SCALE_KEY, maxScale);
        return bundle;

    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            this.minScale = bundle.getInt(MIN_SCALE_KEY);
            this.minScale = bundle.getInt(MAX_SCALE_KEY);
            state = bundle.getParcelable(SUPERSTATE_KEY);
        }
        super.onRestoreInstanceState(state);
    }

    public float getSaveScale() {
        return saveScale;
    }

    public void deoxidization() {
        saveScale = MIN_SCALE;
        matrix.reset();
        setTransform(matrix);
        invalidate();

    }


    public class ZoomOnTouchListeners implements OnTouchListener {
        boolean allowZoom = false;
        long time = System.currentTimeMillis();
        long fingerTime = System.currentTimeMillis();

        public ZoomOnTouchListeners() {
            super();
            m = new float[9];
            mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        }

        @Override
        public boolean onTouch(View v, MotionEvent motionEvent) {
            if(!play.isSmallWindow()) {
                mScaleDetector.onTouchEvent(motionEvent);
                matrix.getValues(m);
                float x = m[Matrix.MTRANS_X];
                float y = m[Matrix.MTRANS_Y];
                PointF curr = new PointF(motionEvent.getX(), motionEvent.getY());
                switch (motionEvent.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        last.set(motionEvent.getX(), motionEvent.getY());
                        start.set(last);
                        mode = DRAG;
                        if (motionEvent.getPointerCount() == 1) {
                            fingerTime = System.currentTimeMillis();
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        mode = NONE;
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        last.set(motionEvent.getX(), motionEvent.getY());
                        start.set(last);
                        mode = ZOOM;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if ((mode == ZOOM || (mode == DRAG && saveScale > minScale))
                                && measureHelper.getmCurrentAspectRatio() == MeasureHelper.FILL_PARENT && (System.currentTimeMillis() - time) > 1000) {
                            float deltaX = curr.x - last.x;// x difference
                            float deltaY = curr.y - last.y;// y difference
                            if (y + deltaY > 0) {
                                deltaY = -y;
                            } else if (y + deltaY < -bottom) {
                                deltaY = -(y + bottom);
                            }
                            if (x + deltaX > 0) {
                                deltaX = -x;
                            } else if (x + deltaX < -right) {
                                deltaX = -(x + right);
                            }
                            matrix.postTranslate(deltaX, deltaY);
                            last.set(curr.x, curr.y);
                            setTransform(matrix);
                            invalidate();
                        }
                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                        mode = NONE;
                        break;
                }
            }else{
                deoxidization();
            }
            View view = (View) v.getParent();
            if (view instanceof PlayFrameLayout) {
                ((PlayFrameLayout) view).onTouch((TextureView) v, motionEvent);
            }
            return true;
        }


        private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
            @Override
            public boolean onScaleBegin(ScaleGestureDetector detector) {
                mode = ZOOM;
                return true;
            }

            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                float mScaleFactor = detector.getScaleFactor();
                float defaultScaleFactor = mScaleFactor;
                float origScale = saveScale;
                saveScale *= mScaleFactor;
                if (saveScale > maxScale) {
                    saveScale = maxScale;
                    mScaleFactor = maxScale / origScale;
                } else if (saveScale < minScale) {
                    saveScale = minScale;
                    mScaleFactor = minScale / origScale;
                }
                allowZoom = false;
                if (defaultScaleFactor >= 1.0f) {
                    //放大操作
                    switch (measureHelper.getmCurrentAspectRatio()) {
                        case MeasureHelper.FIT_PARENT_4_3:
                            if ((System.currentTimeMillis() - time) > 500) {
                                deoxidization();
                                setAspectRatio(MeasureHelper.FIT_PARENT);
                                time = System.currentTimeMillis();
                            }
                            break;
                        case MeasureHelper.FIT_PARENT:
                        case MeasureHelper.MATCH_PARENT:
                        case MeasureHelper.FIT_PARENT_16_9:
                        case MeasureHelper.FIT_PARENT_18_9:
                            if ((System.currentTimeMillis() - time) > 500) {
                                deoxidization();
                                //填充
                                setAspectRatio(MeasureHelper.FILL_PARENT);
                                time = System.currentTimeMillis();
                            }
                            break;
                        case MeasureHelper.FILL_PARENT:
                            //填充
                            allowZoom = true;
                            break;

                    }
                } else if (defaultScaleFactor < 1.0f && saveScale == 1.0f) {
                    //缩小操作
                    switch (measureHelper.getmCurrentAspectRatio()) {
                        case MeasureHelper.FILL_PARENT:
                        case MeasureHelper.MATCH_PARENT:
                            if ((System.currentTimeMillis() - time) > 500) {
                                deoxidization();
                                setAspectRatio(MeasureHelper.FIT_PARENT);
                                time = System.currentTimeMillis();
                            }
                            allowZoom = true;
                            break;
                        case MeasureHelper.FIT_PARENT:
                        case MeasureHelper.FIT_PARENT_16_9:
                        case MeasureHelper.FIT_PARENT_18_9:
                            deoxidization();
                            setAspectRatio(MeasureHelper.FIT_PARENT_4_3);
                            time = System.currentTimeMillis();
                            allowZoom = true;
                            break;
                    }

                } else {
                    allowZoom = true;
                }
                if (allowZoom && (System.currentTimeMillis() - time) > 300) {
                    right = getWidth() * saveScale - getWidth();
                    bottom = getHeight() * saveScale - getHeight();
                    if (0 <= getWidth() || 0 <= getHeight()) {
                        matrix.postScale(mScaleFactor, mScaleFactor, detector.getFocusX(), detector.getFocusY());
                        if (mScaleFactor < 1) {
                            matrix.getValues(m);
                            float x = m[Matrix.MTRANS_X];
                            float y = m[Matrix.MTRANS_Y];

                                if (0 < getWidth()) {
                                    if (y < -bottom) {
                                        matrix.postTranslate(0, -(y + bottom));
                                    } else if (y > 0) {
                                        matrix.postTranslate(0, -y);
                                    }
                                } else {
                                    if (x < -right) {
                                        matrix.postTranslate(-(x + right), 0);
                                    } else if (x > 0) {
                                        matrix.postTranslate(-x, 0);
                                    }

                                }
                            
                        }
                    } else {
                        matrix.postScale(mScaleFactor, mScaleFactor, detector.getFocusX(), detector.getFocusY());
                        matrix.getValues(m);
                        float x = m[Matrix.MTRANS_X];
                        float y = m[Matrix.MTRANS_Y];
                        if (mScaleFactor < 1) {
                            if (x < -right) {
                                matrix.postTranslate(-(x + right), 0);
                            } else if (x > 0) {
                                matrix.postTranslate(-x, 0);
                            }
                            if (y < -bottom) {
                                matrix.postTranslate(0, -(y + bottom));
                            } else if (y > 0) {
                                matrix.postTranslate(0, -y);
                            }
                        }
                    }
                } else {
                    deoxidization();
                }

                return true;
            }
        }
    }


}
