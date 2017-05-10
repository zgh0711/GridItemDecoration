package com.zgh.griditemdecoration;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.ColorInt;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;

/**
 * 给 RecyclerView item 设置边距，
 * 构造函数需要传入 context，要设置的边距宽度（单位：dp），边距颜色
 * 使用示例：
 *
        mRecyclerView.setLayoutManager(new GridLayoutManager(_mActivity, 2));
        int color = getResources().getColor(R.color.gray);
        mRecyclerView.addItemDecoration(new GridItemDecoration(_mActivity, 8, color) {
            @Override
            public boolean[] getItemSidesIsHaveOffsets(int itemPosition) {
                //顺序:left, top, right, bottom
                boolean[] booleans = {false, false, false, false};
                if (itemPosition == 0) {
                    //因为给 RecyclerView 添加了 header，所以原本的 position 发生了变化
                    //position 为 0 的地方实际上是 header，真正的列表 position 从 1 开始
                } else {
                    switch (itemPosition % 2) {
                        case 0:
                            //每一行第二个只显示左边距和下边距
                            booleans[0] = true;
                            booleans[3] = true;
                            break;
                        case 1:
                            //每一行第一个显示右边距和下边距
                            booleans[2] = true;
                            booleans[3] = true;
                            break;
                    }
                }
                return booleans;
            }
        });
 */

public abstract class GridItemDecoration extends RecyclerView.ItemDecoration {

    private Paint mPaint;
    private int   lineWidth;//px 分割线宽

    public GridItemDecoration(Context context, float lineWidthDp, @ColorInt int colorRGB) {
        this.lineWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, lineWidthDp, context.getResources().getDisplayMetrics());
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(colorRGB);
        mPaint.setStyle(Paint.Style.FILL);
    }

    public GridItemDecoration(Context context, int lineWidthDp, @ColorInt int colorRGB) {
        this(context, (float) lineWidthDp, colorRGB);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        //left, top, right, bottom
        int childCount1 = parent.getChildCount();
        //        int childCount2 = parent.getLayoutManager().getChildCount();
        //        int childCount3 = parent.getAdapter().getItemCount();
        //        Log.e("count", "getChildCount()=" + childCount1 + "-----getLayoutManager().getChildCount()=" + childCount2 + "----getAdapter().getItemCount()=" + childCount3);
        for (int i = 0; i < childCount1; i++) {
            View child = parent.getChildAt(i);

            int itemPosition = ((RecyclerView.LayoutParams) child.getLayoutParams()).getViewLayoutPosition();

            boolean[] sideOffsetBooleans = getItemSidesIsHaveOffsets(itemPosition);
            if (sideOffsetBooleans[0]) {
                drawChildLeftVertical(child, c, parent);
            }
            if (sideOffsetBooleans[1]) {
                drawChildTopHorizontal(child, c, parent);
            }
            if (sideOffsetBooleans[2]) {
                drawChildRightVertical(child, c, parent);
            }
            if (sideOffsetBooleans[3]) {
                drawChildBottomHorizontal(child, c, parent);
            }
        }
    }

    private void drawChildBottomHorizontal(View child, Canvas c, RecyclerView parent) {
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                .getLayoutParams();
        int left = child.getLeft() - params.leftMargin - lineWidth;
        int right = child.getRight() + params.rightMargin + lineWidth;
        int top = child.getBottom() + params.bottomMargin;
        int bottom = top + lineWidth;

        c.drawRect(left, top, right, bottom, mPaint);
    }

    private void drawChildTopHorizontal(View child, Canvas c, RecyclerView parent) {
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                .getLayoutParams();
        int left = child.getLeft() - params.leftMargin - lineWidth;
        int right = child.getRight() + params.rightMargin + lineWidth;
        int bottom = child.getTop() - params.topMargin;
        int top = bottom - lineWidth;

        c.drawRect(left, top, right, bottom, mPaint);
    }

    private void drawChildLeftVertical(View child, Canvas c, RecyclerView parent) {
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                .getLayoutParams();
        int top = child.getTop() - params.topMargin - lineWidth;
        int bottom = child.getBottom() + params.bottomMargin + lineWidth;
        int right = child.getLeft() - params.leftMargin;
        int left = right - lineWidth;

        c.drawRect(left, top, right, bottom, mPaint);
    }

    private void drawChildRightVertical(View child, Canvas c, RecyclerView parent) {
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                .getLayoutParams();
        int top = child.getTop() - params.topMargin - lineWidth;
        int bottom = child.getBottom() + params.bottomMargin + lineWidth;
        int left = child.getRight() + params.rightMargin;
        int right = left + lineWidth;

        c.drawRect(left, top, right, bottom, mPaint);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        //outRect 看源码可知这里只是把Rect类型的outRect作为一个封装了left,right,top,bottom的数据结构,
        //作为传递left,right,top,bottom的偏移值来用的
        int itemPosition = ((RecyclerView.LayoutParams) view.getLayoutParams()).getViewLayoutPosition();
        boolean[] sideOffsetBooleans = getItemSidesIsHaveOffsets(itemPosition);

        //如果是设置左边或者右边的边距，就只设置成指定宽度的一半，
        // 因为这个项目中的 Grid 是一行二列，如果不除以二的话，那么中间的间距就会很宽，
        //可根据实际项目需要修改成合适的值
        int left = sideOffsetBooleans[0] ? lineWidth/2 : 0;
        int top = sideOffsetBooleans[1] ? lineWidth : 0;
        int right = sideOffsetBooleans[2] ? lineWidth/2 : 0;
        int bottom = sideOffsetBooleans[3] ? lineWidth : 0;

        outRect.set(left, top, right, bottom);
    }

    /**
     * 顺序:left, top, right, bottom
     *
     * @return boolean[4]
     */
    public abstract boolean[] getItemSidesIsHaveOffsets(int itemPosition);


}



