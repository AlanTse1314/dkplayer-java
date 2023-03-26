package com.example.video.view;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.example.video.enume.PlayDirectionEnume;

public class FloatingOnTouchListener implements View.OnTouchListener {
    private int x;
    private int y;
    private int maxWidth;
    private int minWidth;
    private Context context;
    private boolean isMove;
    private float mFirstDistance;
    private WindowManager.LayoutParams mLastLayoutParams;
    private  float mDefaultAspectRatio;
    private WindowManager mWindowsManager;
    private PlayDirectionEnume directionEnume;
    private int defaultWidth, defaultHeight;
    public FloatingOnTouchListener(Context context,final WindowManager.LayoutParams layoutParams, float defaultAspectRatio, int maxWidth, int minWidth, WindowManager wm) {
        super();
        this.mLastLayoutParams = layoutParams;
        this.mDefaultAspectRatio = defaultAspectRatio;
        this.maxWidth = maxWidth;
        this.minWidth = Math.min(minWidth, maxWidth);
        this.context = context;
        mWindowsManager = wm;
        
    }

    public void setmDefaultAspectRatio(float mDefaultAspectRatio) {
        this.mDefaultAspectRatio = mDefaultAspectRatio;
    }

    public void setMove(boolean move) {
        isMove = move;
    }

    public void releaseCoordinate(PlayDirectionEnume directionEnume,int width,int height) {
        this.directionEnume = directionEnume;
        this.defaultHeight=height;
        this.defaultWidth=width;
    }

    private static final int NONE = 0;
    private static final int ZOOM = 2;
    private int mode = NONE;

    private int mStartX, mStartY;
    private int mlpX, mlpY;

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        if(view==null || !view.isAttachedToWindow()){
            return false;
        }
        int fingerCount = event.getPointerCount();
        if (1 == fingerCount) {
            int defaultX = (int) event.getX();
            int defaultY = (int) event.getY();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    isMove = false;
                    x = (int) event.getRawX();
                    y = (int) event.getRawY();
                    mStartX = defaultX;
                    mStartY = defaultY;
                    mlpX = mLastLayoutParams.x;
                    mlpY = mLastLayoutParams.y;
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (mode != ZOOM) {
                        int nowX = (int) event.getRawX();
                        int nowY = (int) event.getRawY();
                        int movedX = nowX - x;
                        int movedY = nowY - y;
                        x = nowX;
                        y = nowY;
                        mLastLayoutParams.x = mLastLayoutParams.x + movedX;
                        mLastLayoutParams.y = mLastLayoutParams.y + movedY;
                        mWindowsManager.updateViewLayout(view, mLastLayoutParams);
                        float deltaX = defaultX - mStartX;
                        float deltaY = defaultY - mStartY;
                        if (Math.abs(deltaX) >= 5 || Math.abs(deltaY) >= 5) {
                            //不允许点击
                            isMove = true;
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (mode == ZOOM) {
                        if (directionEnume!=PlayDirectionEnume.HORIZONTAL_SCREEN) {
                            if (mLastLayoutParams.height == defaultHeight) {
                                mLastLayoutParams.y = 0;
                                if (mLastLayoutParams.width > defaultWidth) {
                                    mLastLayoutParams.x = -(mLastLayoutParams.width - defaultWidth) / 2;
                                } else {
                                    mLastLayoutParams.x = mlpX;
                                }
                            } else {
                                if (mLastLayoutParams.width > maxWidth) {
                                    mLastLayoutParams.width = maxWidth;
                                } else if (mLastLayoutParams.width < minWidth) {
                                    mLastLayoutParams.width = minWidth;
                                }
                                mLastLayoutParams.height = (int) (mLastLayoutParams.width / mDefaultAspectRatio);
                            }
                            mWindowsManager.updateViewLayout(view, mLastLayoutParams);
                        }else if (mLastLayoutParams.height!=defaultHeight && mLastLayoutParams.width>maxWidth){
                            mLastLayoutParams.width = maxWidth;
                            mLastLayoutParams.height = (int) (maxWidth/mDefaultAspectRatio);
                        }
                        mode = NONE;
                    }
                    if (directionEnume!=PlayDirectionEnume.HORIZONTAL_SCREEN) {
                        validityXY();
                        computeX();
                        computeY();
                    }
                    mWindowsManager.updateViewLayout(view, mLastLayoutParams);
                default:
                    break;
            }
            view.performClick();
        } else if (2 == fingerCount) {
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_POINTER_DOWN:
                    mFirstDistance = getDistance(event);
                    break;
                case MotionEvent.ACTION_MOVE:
                    mode = ZOOM;
                    isMove = true;
                    float distance = getDistance(event);
                    float scale = distance / mFirstDistance;
                    int width = (int) (mLastLayoutParams.width * scale);
                    int height = (int) (width / mDefaultAspectRatio);
                    if (directionEnume!=PlayDirectionEnume.HORIZONTAL_SCREEN) {  //竖屏限制高
                        if (height <= defaultHeight) {
                            mLastLayoutParams.width = width;
                            mLastLayoutParams.height = height;
                        } else {
                            mLastLayoutParams.width = (int) (defaultHeight * mDefaultAspectRatio);
                            mLastLayoutParams.height = defaultHeight;
                        }
                        if (mLastLayoutParams.width > defaultWidth) {
                            int x=(mLastLayoutParams.width - defaultWidth) / 2;

                            mLastLayoutParams.x = -x;
                            mLastLayoutParams.y = (defaultHeight - mLastLayoutParams.height) / 2;
                        }
                    } else if (height >=defaultHeight) {//横屏限制高
                        mLastLayoutParams.width = (int) (defaultHeight*mDefaultAspectRatio);
                        mLastLayoutParams.height = defaultHeight;
                        mLastLayoutParams.x = -(mLastLayoutParams.width - defaultWidth) / 2;
                        mLastLayoutParams.y =0;
                    } else if (width < minWidth) {
                        mLastLayoutParams.width = minWidth;
                        mLastLayoutParams.height = (int) (minWidth / mDefaultAspectRatio);
                    } else {
                        mLastLayoutParams.width = width;
                        mLastLayoutParams.height = height;
                    }
                    mWindowsManager.updateViewLayout(view, mLastLayoutParams);
                    mFirstDistance = distance;
                    break;
                default:
                    break;
            }
        }
        return isMove;
    }

    private void computeX() {
        if (mLastLayoutParams.width <= defaultWidth) {
            if (mLastLayoutParams.x < 0) {
                //靠左边
                mLastLayoutParams.x = -(defaultWidth - mLastLayoutParams.width) / 2;
            } else if ((mLastLayoutParams.x + mLastLayoutParams.width) > defaultWidth) {
                //靠右边
                mLastLayoutParams.x = (defaultWidth - mLastLayoutParams.width) / 2;
            }
        }
    }

    private void computeY() {
        if (mLastLayoutParams.height < defaultHeight) {
            if (mLastLayoutParams.y < 0) {
                //靠顶边
                mLastLayoutParams.y = 0;
            } else if ((mLastLayoutParams.y + mLastLayoutParams.height) > defaultHeight) {
                //靠底边
                mLastLayoutParams.y = (defaultHeight - mLastLayoutParams.height);
            }
        } else {
            mLastLayoutParams.y = 0;
        }
    }

    private void validityXY() {
        if (mLastLayoutParams.height == defaultHeight) {
            int x = (mLastLayoutParams.width - defaultWidth) / 2;
            if (mLastLayoutParams.x < 0) {
                if (-(mLastLayoutParams.x) >= mLastLayoutParams.width / 2) {
                    //居右
                    mLastLayoutParams.x = -x;
                }
            } else {
                if (mLastLayoutParams.x >= mLastLayoutParams.width / 2) {
                    //居左
                    mLastLayoutParams.x = x;
                }
            }
        }
    }


    private float getDistance(MotionEvent event) {
        float x1 = event.getX();
        float y1 = event.getY();
        float x2 = event.getX(1);
        float y2 = event.getY(1);

        return (float) Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }
}