package com.yueke.keywordtags;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.widget.EditText;

import com.yueke.keywordtags.KeyWordTagsView.OnItemClickListener;

public class KeyWordTagActivity extends Activity implements OnItemClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_key_word_tag);
        
        findAllView( );
        showContent( );
    }
    
    @Override
    public void onItemClick(View v, String keyWords) {
        hideView( v, keyWords );
        onSearch( );
    }
    
    public void onSearch( ){
        // search keyWords
    }
    
    private void findAllView( ){
        mSearchEditTxt = ( EditText )findViewById( R.id.searchEditTxtId );
        mKeyWordTagsView = ( KeyWordTagsView )findViewById( R.id.keyWordTagsViewId );
        
        mKeyWordTagsView.setOnItemClickListener( this );
    }
    
    private void showContent( ){
        new Thread( new Runnable( ) {
            @Override
            public void run() {
                mKeyWordTagsView.clearKeyWords( );
                mKeyWordTagsView.removeAllTags( );
                for( String keyWord : mKeyWords ){
                    mKeyWordTagsView.addKeyWord( keyWord );
                }
                
                KeyWordTagActivity.this.runOnUiThread( new Runnable() {
                    @Override
                    public void run() {
                        mKeyWordTagsView.showKeyWordTags( KeyWordTagsView.TYPE_ANIMATION_IN );
                    }
                });
            }
        }).start( );
    }
    
    private void hideView( final View view, final String keyWords ){
        Animation hideAnimation = getViewHideAnimation( );
        hideAnimation.setAnimationListener( new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                
            }
            
            @Override
            public void onAnimationRepeat(Animation animation) {
                
            }
            
            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility( View.GONE );
                mSearchEditTxt.setText( keyWords );
            }
        });
        
        view.startAnimation( hideAnimation );
    }
    
    private AnimationSet getViewHideAnimation( ){
        AnimationSet animationSet = new AnimationSet(true);
        AlphaAnimation alphaHideAnimation = new AlphaAnimation(1.0f, 0.0f);
        
        alphaHideAnimation.setDuration( 300 );
        animationSet.addAnimation( alphaHideAnimation );
        
        return animationSet;
    }

    private String[] mKeyWords = new String[]{
            "Àî°×","¶Å¸¦","°×¾ÓÒ×","ÐÁÆú¼²","ÀîÇåÕÕ"
            ,"ËÕéø","Öî¸ðÁÁ","Íõ²ª","ºØÖªÕÂ","ÍõÎ¬"
            ,"ÁõÓíÎý","ÀîÉÌÒþ","Âæ±öÍõ","ÃÏºÆÈ»","Íõº²"
            ,"ÀîºØ","º«Óú","Áø×ÚÔª","Ñî¾¼","Â¬ÕÕÁÚ"
            ,"³Â×Ó°º","³Â×Ó°º","´Þò«","ÕÅ¼®","á¯²Î"};
    
    private KeyWordTagsView mKeyWordTagsView = null;
    private EditText mSearchEditTxt = null;
}
