package com.yueke.keywordtags;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * �ؼ��ʱ�ǩ�Զ���View
 * 
 * */
public class KeyWordTagsView extends FrameLayout implements OnGlobalLayoutListener {
    public KeyWordTagsView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initKeywordTags( );
    }

    public KeyWordTagsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initKeywordTags( );
    }

    public KeyWordTagsView(Context context) {
        super(context);
        initKeywordTags( );
    }
    
    public interface OnItemClickListener{
        public void onItemClick( View v, String keyWords );
    }
    
    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }
    
    public void onGlobalLayout() {
        int viewWidth = getWidth( );
        int viewHeight = getHeight( );
        
        if ( mViewWidth != viewWidth || mViewHeight != viewHeight ) {
            mViewWidth = viewWidth;
            mViewHeight = viewHeight;
            
            showKeyWordTags( );
        }
    }

    public ArrayList<String> getKeywords() {
        return mKeyWordsList;
    }

    public void clearKeyWords() {
        mKeyWordsList.clear();
    }

    public void removeAllTags( ) {
        removeAllViews( );
    }

    public long getDuration() {
        return mAnimationDuration;
    }

    public void setDuration(long duration) {
        mAnimationDuration = duration;
    }

    public boolean addKeyWord(String keyword) {
        boolean result = false;
        
        if (mKeyWordsList.size() < KEY_WORD_MAX_LEN ) {
            result = mKeyWordsList.add( keyword );
        }
        
        return result;
    }

    public boolean showKeyWordTags( int animType ) {
        if (System.currentTimeMillis() - mLastStartAnimationTime > mAnimationDuration) {
            mEnableShow = true;
            
            if (animType == TYPE_ANIMATION_IN) {
                mTagsAnimationInType = OUTSIDE_TO_LOCATION;
                mTagsAnimationOutType = LOCATION_TO_CENTER;
            } else if (animType == TYPE_ANIMATION_OUT) {
                mTagsAnimationInType = CENTER_TO_LOCATION;
                mTagsAnimationOutType = LOCATION_TO_OUTSIDE;
            }
            
            hideKeyWordTags( );
            boolean result = showKeyWordTags( );
            
            return result;
        }
        return false;
    }
    
    private void initKeywordTags( ) {
        mRandom = new Random( );
        mKeyWordsList = new ArrayList<String>( KEY_WORD_MAX_LEN );
        
        mInterpolator = AnimationUtils.loadInterpolator(getContext(),android.R.anim.decelerate_interpolator);
        mAlphaAnimationShow = new AlphaAnimation(0.0f, 1.0f);
        mAlphaAnimationHide = new AlphaAnimation(1.0f, 0.0f);
        mScaleAnimationLargreToNormal = new ScaleAnimation(2, 1, 2, 1);
        mScalAnimationNormalToLarge = new ScaleAnimation(1, 2, 1, 2);
        mScalAnimationZeroToNormal = new ScaleAnimation(0, 1, 0, 1);
        mScaleAnimationNormalToZero = new ScaleAnimation(1, 0, 1, 0);
        
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    private void hideKeyWordTags( ) {
        int size = getChildCount( );
        
        for (int i = size - 1; i >= 0; i--) {
            final TextView txt = (TextView) getChildAt(i);
            if (txt.getVisibility() == View.GONE) {
                removeView(txt);
                continue;
            }
            FrameLayout.LayoutParams layParams = (LayoutParams) txt.getLayoutParams();
            int[] xy = new int[] { layParams.leftMargin, layParams.topMargin,txt.getWidth() };
            AnimationSet animSet = getAnimationSet(xy, (mViewWidth >> 1),(mViewHeight >> 1), mTagsAnimationOutType);
            txt.startAnimation(animSet);
            animSet.setAnimationListener(new AnimationListener() {
                public void onAnimationStart(Animation animation) {
                    
                }

                public void onAnimationRepeat(Animation animation) {
                    
                }

                public void onAnimationEnd(Animation animation) {
                    txt.setOnClickListener(null);
                    txt.setClickable(false);
                    txt.setVisibility(View.GONE);
                }
            });
        }
    }

    private boolean showKeyWordTags( ){
        if (mViewWidth > 0 && mViewHeight > 0 && mKeyWordsList != null && mKeyWordsList.size() > 0 && mEnableShow){
            mEnableShow = false;
            mLastStartAnimationTime = System.currentTimeMillis();
            // �ҵ����ĵ�
            int xCenter = mViewWidth >> 1;
            int yCenter = mViewHeight >> 1;
            int keyWordsSize = mKeyWordsList.size();
            int xItem = mViewWidth / keyWordsSize;
            int yItem = mViewHeight / keyWordsSize;
            
            LinkedList<Integer> listX = new LinkedList<Integer>(), listY = new LinkedList<Integer>();
            for (int i = 0; i < keyWordsSize; i++) {
                // ׼�������ѡ�����ֱ��Ӧx/y��λ��
                listX.add(i * xItem);
                listY.add(i * yItem + (yItem >> 2));
            }
            
            LinkedList<TextView> listTxtTop = new LinkedList<TextView>();
            LinkedList<TextView> listTxtBottom = new LinkedList<TextView>();
            for (int i = 0; i < keyWordsSize; i++) {
                final String keyword = mKeyWordsList.get(i);
                // �����ɫ
                int ranColor = 0xff000000 | mRandom.nextInt(0x0077ffff);
                // ���λ�ã���ֵ
                int xy[] = mRandomXY(mRandom, listX, listY, xItem);
                // ��������С
                int txtSize = TEXT_SIZE_MIN + mRandom.nextInt(TEXT_SIZE_MAX - TEXT_SIZE_MIN + 1);
                // ʵ����TextView
                final TextView txt = new TextView(getContext());
                txt.setOnClickListener( new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnItemClickListener.onItemClick( txt, keyword );
                    }
                });
                txt.setText(keyword);
                txt.setTextColor(ranColor);
                txt.setTextSize(TypedValue.COMPLEX_UNIT_SP, txtSize);
                txt.setGravity(Gravity.CENTER);

                // ��ȡ�ı�����
                Paint paint = txt.getPaint();
                int strWidth = 0;
                try {
                    strWidth = (int) Math.ceil(paint.measureText(keyword));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                xy[INDEX_TAG_WIDTH] = strWidth;
                // ��һ������:����x����
                if (xy[INDEX_X] + strWidth > mViewWidth - (xItem >> 1)) {
                    int baseX = mViewWidth - strWidth;
                    // �����ı��ұ�Եһ���ĸ���
                    xy[INDEX_X] = baseX - xItem + mRandom.nextInt(xItem >> 1);
                } else if (xy[INDEX_X] == 0) {
                    // �����ı����Եһ���ĸ���
                    xy[INDEX_X] = Math.max(mRandom.nextInt(xItem), xItem / 3);
                }
                xy[INDEX_TAG_TO_CENTER_DISTANCE] = Math.abs(xy[INDEX_Y] - yCenter);
                txt.setTag(xy);
                if (xy[INDEX_Y] > yCenter) {
                    listTxtBottom.add(txt);
                } else {
                    listTxtTop.add(txt);
                }
            }
            
            addKeyWordTagToView(listTxtTop, xCenter, yCenter, yItem);
            addKeyWordTagToView(listTxtBottom, xCenter, yCenter, yItem);
            
            return true;
        }
        
        return false;
    }

    private void addKeyWordTagToView(LinkedList<TextView> listTxt, int xCenter,int yCenter, int yItem) {
        int size = listTxt.size();
        sortXYList(listTxt, size);
        for (int i = 0; i < size; i++) {
            TextView txt = listTxt.get(i);
            int[] iXY = (int[]) txt.getTag();
            // �ڶ�������:����y����
            int yDistance = iXY[INDEX_Y] - yCenter;
            // ����������ĵ�ģ���ֵ�������yItem<br/>
            // ���ڿ���һ·�½������ĵ�ģ����ֵҲ����Ӧ�����Ĵ�С<br/>
            int yMove = Math.abs(yDistance);
            inner: for (int k = i - 1; k >= 0; k--) {
                int[] kXY = (int[]) listTxt.get(k).getTag();
                int startX = kXY[INDEX_X];
                int endX = startX + kXY[INDEX_TAG_WIDTH];
                // y�������ĵ�Ϊ�ָ��ߣ���ͬһ��
                if (yDistance * (kXY[INDEX_Y] - yCenter) > 0) {
                    if (isXMixed(startX, endX, iXY[INDEX_X], iXY[INDEX_X] + iXY[INDEX_TAG_WIDTH])) {
                        int tmpMove = Math.abs(iXY[INDEX_Y] - kXY[INDEX_Y]);
                        if (tmpMove > yItem) {
                            yMove = tmpMove;
                        } else if (yMove > 0) {
                            // ȡ��Ĭ��ֵ��
                            yMove = 0;
                        }
                        
                        break inner;
                    }
                }
            }
            if (yMove > yItem) {
                int maxMove = yMove - yItem;
                int mRandomMove = mRandom.nextInt(maxMove);
                int realMove = Math.max(mRandomMove, maxMove >> 1) * yDistance / Math.abs(yDistance);
                iXY[INDEX_Y] = iXY[INDEX_Y] - realMove;
                iXY[INDEX_TAG_TO_CENTER_DISTANCE] = Math.abs(iXY[INDEX_Y] - yCenter);
                // �Ѿ�������ǰi����Ҫ�ٴ�����
                sortXYList(listTxt, i + 1);
            }
            FrameLayout.LayoutParams layParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,FrameLayout.LayoutParams.WRAP_CONTENT);
            layParams.gravity = Gravity.LEFT | Gravity.TOP;
            layParams.leftMargin = iXY[INDEX_X];
            layParams.topMargin = iXY[INDEX_Y];
            addView(txt, layParams);
            // ����
            AnimationSet animSet = getAnimationSet(iXY, xCenter, yCenter,mTagsAnimationInType);
            txt.startAnimation(animSet);
        }
    }

    public AnimationSet getAnimationSet(int[] xy, int xCenter, int yCenter,int type) {
        AnimationSet animSet = new AnimationSet(true);
        animSet.setInterpolator( mInterpolator );
        if (type == OUTSIDE_TO_LOCATION) {
            animSet.addAnimation(mAlphaAnimationShow);
            animSet.addAnimation(mScaleAnimationLargreToNormal);
            TranslateAnimation translate = new TranslateAnimation((xy[INDEX_X] + (xy[INDEX_TAG_WIDTH] >> 1) - xCenter) << 1, 0, (xy[INDEX_Y] - yCenter) << 1, 0);
            animSet.addAnimation(translate);
        } else if (type == LOCATION_TO_OUTSIDE) {
            animSet.addAnimation(mAlphaAnimationHide);
            animSet.addAnimation(mScalAnimationNormalToLarge);
            TranslateAnimation translate = new TranslateAnimation(0, (xy[INDEX_X] + (xy[INDEX_TAG_WIDTH] >> 1) - xCenter) << 1, 0, (xy[INDEX_Y] - yCenter) << 1);
            animSet.addAnimation(translate);
        } else if (type == LOCATION_TO_CENTER) {
            animSet.addAnimation(mAlphaAnimationHide);
            animSet.addAnimation(mScaleAnimationNormalToZero);
            TranslateAnimation translate = new TranslateAnimation(0, (-xy[INDEX_X] + xCenter), 0, (-xy[INDEX_Y] + yCenter));
            animSet.addAnimation(translate);
        } else if (type == CENTER_TO_LOCATION) {
            animSet.addAnimation(mAlphaAnimationShow);
            animSet.addAnimation(mScalAnimationZeroToNormal);
            TranslateAnimation translate = new TranslateAnimation( (-xy[INDEX_X] + xCenter), 0, (-xy[INDEX_Y] + yCenter), 0);
            animSet.addAnimation(translate);
        }
        animSet.setDuration(mAnimationDuration);
        
        return animSet;
    }

    /**
     * ���������ĵ�ľ����ɽ���Զ����ð������
     * 
     * @param endIdx ��ʼλ�á�
     * @param txtArr ����������顣
     * 
     */
    private void sortXYList(LinkedList<TextView> listTxt, int endIdx) {
        for (int i = 0; i < endIdx; i++) {
            for (int k = i + 1; k < endIdx; k++) {
                if (((int[]) listTxt.get(k).getTag())[INDEX_TAG_TO_CENTER_DISTANCE] < ((int[]) listTxt.get(i).getTag())[INDEX_TAG_TO_CENTER_DISTANCE]) {
                    TextView iTmp = listTxt.get(i);
                    TextView kTmp = listTxt.get(k);
                    listTxt.set(i, kTmp);
                    listTxt.set(k, iTmp);
                }
            }
        }
    }

    /** A�߶���B�߶��������ֱ����X��ӳ�����Ƿ��н����� */
    private boolean isXMixed(int startA, int endA, int startB, int endB) {
        boolean result = false;
        if (startB >= startA && startB <= endA) {
            result = true;
        } else if (endB >= startA && endB <= endA) {
            result = true;
        } else if (startA >= startB && startA <= endB) {
            result = true;
        } else if (endA >= startB && endA <= endB) {
            result = true;
        }
        
        return result;
    }

    private int[] mRandomXY(Random random, LinkedList<Integer> listX,LinkedList<Integer> listY, int xItem) {
        int[] arr = new int[4];
        arr[INDEX_X] = listX.remove(random.nextInt(listX.size()));
        arr[INDEX_Y] = listY.remove(random.nextInt(listY.size()));
        
        return arr;
    }

    private static final int KEY_WORD_MAX_LEN = 30;
    
    // ��������
    public static final int TYPE_ANIMATION_IN = 1;
    public static final int TYPE_ANIMATION_OUT = 2;
    
    // λ�ƶ������ͣ�����Χ�ƶ��������
    public static final int OUTSIDE_TO_LOCATION = 1;
    // λ�ƶ������ͣ���������ƶ�����Χ
    public static final int LOCATION_TO_OUTSIDE = 2;
    //λ�ƶ������ͣ������ĵ��ƶ��������
    public static final int CENTER_TO_LOCATION = 3;
    //λ�ƶ������ͣ���������ƶ������ĵ�
    public static final int LOCATION_TO_CENTER = 4;

    // ��ǩ�ĺ�����������
    public static final int INDEX_X = 0;
    public static final int INDEX_Y = 1;
    // ��ǩ�Ŀ������
    public static final int INDEX_TAG_WIDTH = 2;
    // ��ǩ�����ĵ�ľ�������
    public static final int INDEX_TAG_TO_CENTER_DISTANCE = 3;
    
    // ����Ч��ʱ������λms
    private static final long ANIM_DURATION = 5000;
    
    // ��ǩ���ִ�С�ķ�Χ[16,32]��������
    public static final int TEXT_SIZE_MAX = 32;
    public static final int TEXT_SIZE_MIN = 16;
    
    private OnItemClickListener mOnItemClickListener = null;
    private Interpolator mInterpolator = null;
    
    // ��������Alpha����
    private AlphaAnimation mAlphaAnimationShow = null;
    private AlphaAnimation mAlphaAnimationHide = null;
    
    // ��С�任Scale����
    private ScaleAnimation mScaleAnimationLargreToNormal = null;
    private ScaleAnimation mScalAnimationNormalToLarge = null;
    private ScaleAnimation mScalAnimationZeroToNormal = null;
    private ScaleAnimation mScaleAnimationNormalToZero = null;
    
    private int mViewWidth = 0;
    private int mViewHeight = 0;
    private boolean mEnableShow = false;
    private Random mRandom = null;
    
    private int mTagsAnimationInType = 0;
    private int mTagsAnimationOutType = 0;
    
    private long mLastStartAnimationTime = 0;
    private long mAnimationDuration = ANIM_DURATION;
    
    private ArrayList<String> mKeyWordsList = null;
}
