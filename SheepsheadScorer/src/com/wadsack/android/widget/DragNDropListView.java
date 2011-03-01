package com.wadsack.android.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.*;
import android.widget.ImageView;
import android.widget.ListView;


/**
 * Author: Jeremy Wadsack
 */
public class DragNDropListView extends ListView
{


	boolean mDragMode;

	int mStartPosition;
	int mEndPosition;
	int mDragPointOffset;		//Used to adjust drag view location

	ImageView mDragView;
	GestureDetector mGestureDetector;
    ImageView dropPointView;

	DropListener mDropListener;
	RemoveListener mRemoveListener;
	DragListener mDragListener;

	public DragNDropListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setDropListener(DropListener l) {
		mDropListener = l;
	}

	public void setRemoveListener(RemoveListener l) {
		mRemoveListener = l;
	}

	public void setDragListener(DragListener l) {
		mDragListener = l;
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		final int action = ev.getAction();
		final int x = (int) ev.getX();
		final int y = (int) ev.getY();

		if (action == MotionEvent.ACTION_DOWN && x > 3 * this.getWidth()/4) {
			mDragMode = true;
		}

		if (!mDragMode)
			return super.onTouchEvent(ev);

		switch (action) {
			case MotionEvent.ACTION_DOWN:
				mStartPosition = pointToPosition(x,y);
				if (mStartPosition != INVALID_POSITION) {
					int mItemPosition = mStartPosition - getFirstVisiblePosition();
                    mDragPointOffset = y - getChildAt(mItemPosition).getTop();
                    mDragPointOffset -= ((int)ev.getRawY()) - y;
					startDrag(mItemPosition,y);
					drag(x,y);
				}
				break;
			case MotionEvent.ACTION_MOVE:
				drag(x,y);
				break;
			case MotionEvent.ACTION_CANCEL:
			case MotionEvent.ACTION_UP:
			default:
				mDragMode = false;
				mEndPosition = pointToPosition(x, y);
                if (mEndPosition != INVALID_POSITION) {
                    mEndPosition = pointToPosition(x, y - getChildAt(mEndPosition).getHeight()/2);
                }
				stopDrag(mStartPosition - getFirstVisiblePosition());
				if (mDropListener != null && mStartPosition != INVALID_POSITION && mEndPosition != INVALID_POSITION)
	        		 mDropListener.onDrop(mStartPosition, mEndPosition);
				break;
		}
		return true;
	}

	// move the drag view
	private void drag(int x, int y) {
		WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams)mDragView.getLayoutParams();
		layoutParams.x = 0; //x;  -- because it's a list; never want it to move left or right
		layoutParams.y = y - mDragPointOffset;

        WindowManager mWindowManager = (WindowManager)getContext().getSystemService(Context.WINDOW_SERVICE);
        mWindowManager.updateViewLayout(mDragView, layoutParams);

        // This attempts to draw an arrow at the insertion point
        // it doesn't work because it draws the arrow instead of the dragWindow
//        // highlight the list separator view in the space where it will be dropped
//        int endPosition = pointToPosition(x, y);
//        WindowManager.LayoutParams dropParams = (WindowManager.LayoutParams)dropPointView.getLayoutParams();
//        dropParams.x = 0;
//        dropParams.y = 0;
//        View v = getChildAt(endPosition);
//        if (v != null) {
//            dropParams.y = v.getTop();
//        }
//        mWindowManager.updateViewLayout(dropPointView, dropParams);


        // Something like this attempts to re-order the list each time we drag
        // however, the listview needs the original 'to' to change but it can't change it's adapter
//            int position = listView.pointToPosition(x, y);
//            if (position != ListView.INVALID_POSITION) {
//                if (from == ListView.INVALID_POSITION) {
//                    from = position;
//                } else {
//                    position = listView.pointToPosition(x, y - listView.getChildAt(position).getHeight()/2);
//                    if (position != ListView.INVALID_POSITION) {
//                        to = position;
//                        runOnUiThread(new Runnable() {
//                            public void run() {
//                                players.add(to, players.remove(from));
//                                ((BaseAdapter) getListAdapter()).notifyDataSetChanged();
//                                from = to;
//                            }
//                        });
//                    }
//                }
//            }


        if (mDragListener != null) {
            mDragListener.onDrag(x, y, this);
        }
    }

	// enable the drag view for dragging
	private void startDrag(int itemIndex, int y) {
		stopDrag(itemIndex);

		View item = getChildAt(itemIndex);
		if (item == null) return;
		item.setDrawingCacheEnabled(true);
		if (mDragListener != null)
			mDragListener.onStartDrag(item);

        // Create a copy of the drawing cache so that it does not get recycled
        // by the framework when the list tries to clean up memory
        Bitmap bitmap = Bitmap.createBitmap(item.getDrawingCache());

        WindowManager.LayoutParams mWindowParams = new WindowManager.LayoutParams();
        mWindowParams.gravity = Gravity.TOP;
        mWindowParams.x = 0;
        mWindowParams.y = y - mDragPointOffset;

        mWindowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        mWindowParams.format = PixelFormat.TRANSLUCENT;
        mWindowParams.windowAnimations = 0;

        Context context = getContext();
        ImageView v = new ImageView(context);
        v.setImageBitmap(bitmap);

        WindowManager mWindowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        mWindowManager.addView(v, mWindowParams);

        mDragView = v;

//        ImageView dv = new ImageView(context);
//        v.setImageResource(android.R.drawable.ic_media_play);
//        WindowManager.LayoutParams dvParam = new WindowManager.LayoutParams();
//        dvParam.copyFrom(mWindowParams);
//        mWindowManager.addView(dv, dvParam);
//
//        dropPointView = dv;
	}

	// destroy drag view
	private void stopDrag(int itemIndex) {
		if (mDragView != null) {
			if (mDragListener != null)
				mDragListener.onStopDrag(getChildAt(itemIndex));
            mDragView.setVisibility(GONE);
            WindowManager wm = (WindowManager)getContext().getSystemService(Context.WINDOW_SERVICE);
            wm.removeView(mDragView);
            mDragView.setImageDrawable(null);
            mDragView = null;

//            dropPointView.setVisibility(View.GONE);
//            wm.removeView(dropPointView);
//            dropPointView.setImageDrawable(null);
//            dropPointView = null;
        }
	}

//	private GestureDetector createFlingDetector() {
//		return new GestureDetector(getContext(), new SimpleOnGestureListener() {
//            @Override
//            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
//                    float velocityY) {
//                if (mDragView != null) {
//                	int deltaX = (int)Math.abs(e1.getX()-e2.getX());
//                	int deltaY = (int)Math.abs(e1.getY() - e2.getY());
//
//                	if (deltaX > mDragView.getWidth()/2 && deltaY < mDragView.getHeight()) {
//                		mRemoveListener.onRemove(mStartPosition);
//                	}
//
//                	stopDrag(mStartPosition - getFirstVisiblePosition());
//
//                    return true;
//                }
//                return false;
//            }
//        });
//	}
}


