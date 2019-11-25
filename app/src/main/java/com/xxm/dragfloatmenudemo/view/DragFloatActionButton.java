package com.xxm.dragfloatmenudemo.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import androidx.annotation.LayoutRes;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.xxm.dragfloatmenudemo.R;
import com.xxm.dragfloatmenudemo.utils.DensityUtils;


public class DragFloatActionButton extends FloatingActionButton {
    private Context mContext;
    private static final String TAG = "DragFloatActionButton";
    //父View的高
    private int parentHeight;
    //父View的宽
    private int parentWidth;
    //菜单弹窗的宽
    private int mPopupWidth;
    //菜单弹窗的高
    private int mPopupHeight;
    //菜单弹窗与按钮的间距
    private int marginY;
    //菜单点击监听实例
    private OnMenuItemSelectListener onMenuItemSelectListener;
    //菜单弹窗
    private PopupWindow mPopWindow;
    //菜单布局资源id
    private int menuResource;


    public DragFloatActionButton(Context context) {
        super(context);
        this.mContext = context;
        initPopupView();
    }

    public DragFloatActionButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initPopupView();
    }

    public DragFloatActionButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initPopupView();
    }

    private void initPopupView() {
        marginY = DensityUtils.dp2px(mContext, 10);
//        contentView = LayoutInflater.from(mContext).inflate(R.layout.onwer_report_menu, null);
//        //制定测量规则 参数表示size + mode
//        int width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
//        int height = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
//        //调用measure方法之后就可以获取宽高
//        contentView.measure(width, height);
//        mPopupWidth = contentView.getMeasuredWidth();
//        mPopupHeight = contentView.getMeasuredHeight();
//        Log.d(TAG, "marginY =" + marginY);
//        Log.d(TAG, "contentView.getWidth() =" + contentView.getMeasuredWidth());
//        Log.d(TAG, "contentView.getHeight() =" + contentView.getMeasuredHeight());

        //offsetX = parentWidth - (getWidth() - mPopupWidth) / 2 - mPopupWidth;
    }

    //上一次的X坐标
    private int lastX;
    //上一次的Y坐标
    private int lastY;
    //拖拽标识
    private boolean isDrag;
    //是否是点击事件
    private boolean isClick;
    //弹窗的X轴方向偏移量
    private int offsetX;
    //弹窗的Y轴方向偏移量
    private int offsetY;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d(TAG, "onTouchEvent:" + MotionEvent.ACTION_MASK);
        int rawX = (int) event.getRawX();
        int rawY = (int) event.getRawY();
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                setPressed(true);
                isDrag = false;
                isClick = true;
                getParent().requestDisallowInterceptTouchEvent(true);
                lastX = rawX;
                lastY = rawY;
                ViewGroup parent;
                if (getParent() != null) {
                    parent = (ViewGroup) getParent();
                    parentHeight = parent.getHeight();
                    parentWidth = parent.getWidth();
                }
                Log.d(TAG, "按下事件  parentHeight=" + parentHeight + "     parentWidth:" + parentWidth);
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d(TAG, "移动事件  parentHeight=" + parentHeight + "     parentWidth:" + parentWidth);

                if (parentHeight <= 0 || parentWidth == 0) {
                    isDrag = false;
                    isClick = true;
                    break;
                } else {
                    isDrag = true;
                    isClick = false;
                }
                int dx = rawX - lastX;
                int dy = rawY - lastY;
                //这里修复一些无法触发点击事件
                int distance = (int) Math.sqrt(dx * dx + dy * dy);
                Log.d(TAG, "distance=" + distance);
                if (distance == 0) {
                    isDrag = false;
                    isClick = true;
                    break;
                }
                float x = getX() + dx;
                float y = getY() + dy;
                //检测是否到达边缘 左上右下
                x = x < 0 ? 0 : x > parentWidth - getWidth() ? parentWidth - getWidth() : x;
                y = getY() < 0 ? 0 : getY() + getHeight() > parentHeight ? parentHeight - getHeight() : y;
                setX(x);
                setY(y);
                lastX = rawX;
                lastY = rawY;
                Log.i(TAG, "isDrag=" + isDrag + "getX=" + getX() + ";getY=" + getY() + ";parentWidth=" + parentWidth);
                break;
            case MotionEvent.ACTION_UP:
                Log.i(TAG, "ACTION_UP ;getY=" + getY());
                if (!isNotDrag()) {
                    //恢复按压效果
                    setPressed(false);
                    //Log.i("getX="+getX()+"；screenWidthHalf="+screenWidthHalf);
                    if (rawX >= parentWidth / 2) {
                        offsetX = parentWidth - (getWidth() - mPopupWidth) / 2 - mPopupWidth;
                        //靠右吸附
                        animate().setInterpolator(new DecelerateInterpolator())
                                .setDuration(500)
                                .xBy(parentWidth - getWidth() - getX())
                                .start();
                    } else {
                        offsetX = getWidth() / 2 - mPopupWidth / 2;
                        //靠左吸附
                        ObjectAnimator oa = ObjectAnimator.ofFloat(this, "x", getX(), 0);
                        oa.setInterpolator(new DecelerateInterpolator());
                        oa.setDuration(500);
                        oa.start();
                    }


                }


                if (isClick)
                    performClick();
                break;
        }
        //如果是拖拽则消s耗事件，否则正常传递即可。
        return true;
    }

    private boolean isNotDrag() {
        Log.d(TAG, "getX()=" + getX() + "    parentWidth=" + parentWidth + "      getWidth()=" + getWidth() + "    parentWidth - getWidth()=" + (parentWidth - getWidth()));
        return !isDrag && (getX() == 0 || (getX() == parentWidth - getWidth()));
    }


    @Override
    public boolean performClick() {
        showMenu();
        return super.performClick();
    }

    /**
     * 设置菜单弹窗的布局资源id
     *
     * @param menuResource 布局资源id
     */
    public void setMenuResource(@LayoutRes int menuResource) {
        this.menuResource = menuResource;
    }


    /**
     * 显示菜单弹窗
     */
    private void showMenu() {
        if (mPopWindow == null) {
            View contentView = LayoutInflater.from(mContext).inflate(menuResource, null);
            mPopWindow = new PopupWindow(contentView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);

            //制定测量规则 参数表示size + mode
            int width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            int height = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            //调用measure方法之后就可以获取宽高
            contentView.measure(width, height);
            mPopupWidth = contentView.getMeasuredWidth();
            mPopupHeight = contentView.getMeasuredHeight();
            Log.d(TAG, "marginY =" + marginY);
            Log.d(TAG, "contentView.getWidth() =" + contentView.getMeasuredWidth());
            Log.d(TAG, "contentView.getHeight() =" + contentView.getMeasuredHeight());

            contentView.findViewById(R.id.menu_item1).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onMenuItemSelectListener != null) {
                        onMenuItemSelectListener.OnMenuItemSelected(v, mPopWindow);
                    }
                }
            });
            contentView.findViewById(R.id.menu_item2).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onMenuItemSelectListener != null) {
                        onMenuItemSelectListener.OnMenuItemSelected(v, mPopWindow);
                    }
                }
            });

            contentView.findViewById(R.id.menu_item3).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onMenuItemSelectListener != null) {
                        onMenuItemSelectListener.OnMenuItemSelected(v, mPopWindow);
                    }
                }
            });

            //初始的X偏移量
            offsetX = parentWidth - (getWidth() - mPopupWidth) / 2 - mPopupWidth;

        }

        int[] location = new int[2];
        this.getLocationOnScreen(location);
        int locationX = location[0];
        int locationY = location[1];

        Log.d(TAG, "locationX=" + locationX);
        Log.d(TAG, "locationY=" + locationY);

        if (getY() < (mPopupHeight + marginY)) {
            Log.w(TAG, "菜单在按钮的下面");

            offsetY = locationY + getHeight() + marginY;
            mPopWindow.setAnimationStyle(R.style.pop_float_menu_animation_down);
        } else {
            Log.w(TAG, "菜单在按钮的上面 ");
            offsetY = locationY - mPopupHeight - marginY;
            mPopWindow.setAnimationStyle(R.style.pop_float_menu_animation_up);
        }

        Log.d(TAG, "offsetX=" + offsetX);
        Log.d(TAG, "offsetY=" + offsetY);

        if (!mPopWindow.isShowing()) {
            mPopWindow.showAtLocation(getRootView(), Gravity.TOP | Gravity.START, offsetX, offsetY);
        }

    }

    /**
     * 隐藏菜单
     */
    public void dismissMenu() {
        if (mPopWindow != null && mPopWindow.isShowing()) {
            mPopWindow.dismiss();
            mPopWindow = null;
        }
    }

    public void setOnMenuItemSelectListener(OnMenuItemSelectListener onMenuItemSelectListener) {
        this.onMenuItemSelectListener = onMenuItemSelectListener;
    }


    public interface OnMenuItemSelectListener {
        /**
         * 已选中的item点击监听
         *
         * @param v
         */
        void OnMenuItemSelected(View v, PopupWindow popWindow);
    }
}