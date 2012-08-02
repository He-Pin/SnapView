package us.sosia.snapview;

import android.content.Context;
import android.graphics.RectF;
import android.provider.ContactsContract.CommonDataKinds.Event;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Scroller;

public class SnapView extends FrameLayout {
	//TODO lock the event dispatch direction
	private TopView mTopView;
	private FrameLayout mLeftView;
	private FrameLayout mRightView;
	protected GestureDetector mGestureDetector;
	
	private RectF mLeftBoundRect;
	private RectF mRightBoundRect;
	
	private Distnation pDistnation = Distnation.TOP;
	private boolean pIsLeftViewEnable = false;
	private boolean pIsRightViewEnable = false;
	private boolean pDispatchToTop = true;  
	private boolean pLockDispatch = true;
 	
	private enum Position{
		LEFT,CENTER,RIGHT
	}
	
	private enum Direction{
		LEFT,RIGHT,IDLE
	}
	
	private enum State{
		SCROLL,FLING,IDLE
	}
	
	private enum Mode{
		STEP,SIDE,BLINK
	}
	
	private enum Distnation{
		TOP,LEFT,RIGHT,OUTTER
	}
	
	
	public void setLeftView(View leftView) {
		if (mLeftView.getChildCount() != 0) {
			mLeftView.removeAllViews();
		}
		mLeftView.addView(leftView);
		pIsLeftViewEnable = true;
	}

	public void setRightView(View rightView) {
		if (mRightView.getChildCount() != 0) {
			mRightView.removeAllViews();
		}
		mRightView.addView(rightView);
		pIsRightViewEnable = true;
	}
	
	public void setTopView(View topView) {
		if (mTopView.getChildCount() != 0) {
			mTopView.removeAllViews();
		}
		mTopView.addView(topView);
	}
	
	
	public RectF getmLeftBoundRect() {
		return mLeftBoundRect;
	}

	public RectF getmRightBoundRect() {
		return mRightBoundRect;
	}

	public SnapView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initSnapView();
 	}

	public SnapView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initSnapView();
 	}

	public SnapView(Context context) {
		super(context);
		initSnapView();
 	}
		
	private void  initSnapView(){
		mGestureDetector = new GestureDetector(getContext(),  new GestureListener());
	}
	
	private boolean isPointInRect(float x,float y,Position currentPosition){
		switch (currentPosition) {
		case CENTER:
			return false;
 		case LEFT:
 			return mLeftBoundRect.contains(x, y);
		case RIGHT:
			return mRightBoundRect.contains(x,y);
		default:
			break;
		}
		return false;
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		//bound,scroll DIrection
		mGestureDetector.onTouchEvent(ev);
		
		if (pLockDispatch) {
			switch (pDistnation) {
				case TOP:
					return mTopView.dispatchTouchEvent(ev);
	 			case LEFT:
	 				return mLeftView.dispatchTouchEvent(ev);
	 			case RIGHT:
	 				return mRightView.dispatchTouchEvent(ev);
	 			default:
					break;
			}
		}
 		return false;
	}





	private void resetState() {
		pLockDispatch = false;
		pDispatchToTop = true;
	}

	private class GestureListener implements OnGestureListener{

		@Override
		public boolean onDown(MotionEvent e) {
			resetState();
 			return true;
		}

		@Override
		public void onShowPress(MotionEvent e) {
 			
		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
		//	resetState();
			Position currentPosition = mTopView.getPosition();
			switch (currentPosition) {
				case LEFT:
					if (isPointInRect(e.getX(), e.getY(), Position.LEFT)) {
						mTopView.snapToCenter();
						return true;
					}else {
						//stup
						//to right
						return mRightView.dispatchTouchEvent(e);
					}
 				case RIGHT:
					if (isPointInRect(e.getX(), e.getY(), Position.RIGHT)) {
						mTopView.snapToCenter();
						return true;
					}else {
						//stup
						//to left
						return mLeftView.dispatchTouchEvent(e);                      
					}
 	
				default:
					break;
			}
			
			return false;
		}


		
		
		
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			//sroll <--------->
			if (!pLockDispatch) {
				//lock the dispatch direction in the first scroll event
				pLockDispatch = true;
				
				if (Math.abs(distanceX) > Math.abs(distanceY)) {
					mTopView.scroll((int)distanceX, 25);
					pDispatchToTop = true;
					pDistnation = Distnation.OUTTER;
				}else {
					//scroll  ^	
 					Position tempPosition = mTopView.getPosition();
 					switch (tempPosition) {
					case CENTER:
	 					pDispatchToTop = true;
						pDistnation = Distnation.TOP;
						break;
					case LEFT:
						if (isPointInRect(e2.getX(), e2.getY(), tempPosition)) {
							pDistnation = Distnation.RIGHT;
		 					pDispatchToTop = false;
						}
						break;
					case RIGHT:
						if (isPointInRect(e2.getX(), e2.getY(), tempPosition)) {
							pDistnation = Distnation.LEFT;
		 					pDispatchToTop = false;
						}
						break;
					default:
						break;
					}
 					
 					
				}
			}
			if (pDispatchToTop && pDistnation == Distnation.OUTTER) {
				mTopView.scroll((int)distanceX, 25);
			}

 			return true;
		}

		@Override
		public void onLongPress(MotionEvent e) {
 			
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			resetState();
			if (velocityX > 0) {
				snapToNext(Direction.LEFT);
			}else {
				snapToNext(Direction.RIGHT);
			}
 			return true;
		}
		
	}

	private void snapToNext(Direction snapDirection){
		Position currentPosition = mTopView.getPosition();
		switch (snapDirection) {
		case LEFT:
		{
			switch (currentPosition) {
			case RIGHT:
				mTopView.snapToCenter();
				break;
			case CENTER:
				mTopView.snapToLeft();
				break;
			default:
				break;
			}
		}
			break;
		case RIGHT:
		{
			switch (currentPosition) {
			case LEFT:
				mTopView.snapToCenter();
				break;
			case CENTER:
				mTopView.snapToRight();
				break;
			default:
				break;
			}
		}
			break;
		case IDLE:
			break;
		default:
			break;
		}
	}





	public class TopView extends FrameLayout{
		private int pLeftBound = 10;
		private int pRightBound = 10;
		private int pScrollToLeft = 0;
		private int pScrollToRight = 0;
		private int pCenterPosition = 0;
		private int pScrollDuration = 600;
		
		private Position pPosition = Position.CENTER;
		private State pState = State.IDLE;
		private Direction pDirection = Direction.IDLE;
		
		
 		private Scroller mScroller ;
		public TopView(Context context, AttributeSet attrs, int defStyle) {
			super(context, attrs, defStyle);
			initTopView();
 		}

		public TopView(Context context, AttributeSet attrs) {
			super(context, attrs);
 		}

		public TopView(Context context) {
			super(context);
			initTopView();
 		}

		private void initTopView(){
			mScroller = new Scroller(getContext());
			initTopView();
		}
		
		
		
		
		
		
/*		@Override
		public boolean dispatchTouchEvent(MotionEvent ev) {
			
			return true;	
		}*/

		@Override
		protected void onSizeChanged(int w, int h, int oldw, int oldh) {
 			super.onSizeChanged(w, h, oldw, oldh);
 			pCenterPosition = w/2;
 			pScrollToLeft = - (w - w / pLeftBound);
 			pScrollToRight = w - w / pRightBound;
 			mRightBoundRect = new RectF(pScrollToRight, 0, w, h);
 			mLeftBoundRect = new RectF(0, 0, w/pLeftBound, h);
		}
		


		@Override
		public void computeScroll() {
			if (mScroller.computeScrollOffset()) {
				scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
				postInvalidate();
			}else {
				reset();
				pState = State.IDLE;
			}
 		}

		public void reset(){
			if (isNeedReset()) {
				resetTo(resetToWhere());
			}
		}
		
		public boolean isNeedReset(){
			int cx = mScroller.getCurrX(); 
			return cx != 0 && cx != pLeftBound && cx != pRightBound;
		}
		
 		public Position resetToWhere(){
			if (Math.abs(mScroller.getCurrX()) > pCenterPosition) {
				if (mScroller.getCurrX()>0) {
					return Position.LEFT;
				}else {
					return Position.RIGHT;
				}
			}else {
				return Position.CENTER;
			}
		}
		
 		public void resetTo(Position resetToPosition){
 			switch (resetToPosition) {
			case LEFT:
				resetToLeft();
				break;
			case CENTER:
				resetToCenter();
				break;
			case RIGHT:
				resetToRight();
				break;
			default:
				break;
			}
 		}
		
		
		public void resetToLeft(){
			if (!mScroller.isFinished()) {
				mScroller.abortAnimation();
			}
			mScroller.startScroll(mScroller.getCurrX(), 0, 0,0,pScrollDuration/2);
			mScroller.setFinalX(pScrollToRight);
			invalidate();
			
			pPosition = Position.LEFT;
			pState = State.FLING;
		}
		
		public void resetToRight(){
			if (!mScroller.isFinished()) {
				mScroller.abortAnimation();
			}
			mScroller.startScroll(mScroller.getCurrX(), 0, 0,0,pScrollDuration/2);
			mScroller.setFinalX(pScrollToLeft);
			invalidate();
			
			pPosition = Position.RIGHT;
			pState = State.FLING;
		}
		
		public void resetToCenter(){
			if (!mScroller.isFinished()) {
				mScroller.abortAnimation();
			}
			mScroller.startScroll(mScroller.getCurrX(), 0, 0,0,pScrollDuration/2);
			mScroller.setFinalX(0);
			invalidate();
			
			pState = State.FLING;
			pPosition = Position.CENTER;
		}
		
		
		public void snapToLeft(){
			if (!mScroller.isFinished()) {
				mScroller.abortAnimation();
			}
			mScroller.startScroll(mScroller.getCurrX(), 0, 0,0,pScrollDuration);
			mScroller.setFinalX(pScrollToRight);
			invalidate();
			
			pPosition = Position.LEFT;
			pState = State.FLING;
 		}
		public void snapToCenter(){
			if (!mScroller.isFinished()) {
				mScroller.abortAnimation();
			}
			mScroller.startScroll(mScroller.getCurrX(), 0, 0,0,pScrollDuration);
			mScroller.setFinalX(0);
			invalidate();
			
			pState = State.FLING;
			pPosition = Position.CENTER;
 		}
		public void snapToRight(){
			if (!mScroller.isFinished()) {
				mScroller.abortAnimation();
			}
			mScroller.startScroll(mScroller.getCurrX(), 0, 0,0,pScrollDuration);
			mScroller.setFinalX(pScrollToLeft);
			invalidate();
			
			pPosition = Position.RIGHT;
			pState = State.FLING;
		}

		public void scroll(int distansX,int duration){
			if (!mScroller.isFinished()) {
				mScroller.abortAnimation();
			}
			mScroller.startScroll(mScroller.getCurrX(), 0, distansX, 0,duration);
			invalidate();
			
			pState = State.SCROLL;
			pDirection = distansX > 0 ? Direction.LEFT:Direction.RIGHT;
		}
		
		
		public int getScrollDuration() {
			return pScrollDuration;
		}

		public void setScrollDuration(int pScrollDuration) {
			this.pScrollDuration = pScrollDuration;
		}

		public Position getPosition() {
			return pPosition;
		}

		public int getLeftBound() {
			return pLeftBound;
		}

		public void setLeftBound(int pLeftBound) {
			this.pLeftBound = pLeftBound;
		}

		public int getRightBound() {
			return pRightBound;
		}

		public void setRightBound(int pRightBound) {
			this.pRightBound = pRightBound;
		}

		public int getScrollToLeft() {
			return pScrollToLeft;
		}

		public void setScrollToLeft(int pScrollToLeft) {
			this.pScrollToLeft = pScrollToLeft;
		}

		public int getScrollToRight() {
			return pScrollToRight;
		}

		public void setScrollToRight(int pScrollToRight) {
			this.pScrollToRight = pScrollToRight;
		}

		public int getCenterPosition() {
			return pCenterPosition;
		}

		public void setCenterPosition(int pCenterPosition) {
			this.pCenterPosition = pCenterPosition;
		}

		public State getState() {
			return pState;
		}

		public Direction getDirection() {
			return pDirection;
		}

	 
		
		
		
		
		
	}

}
