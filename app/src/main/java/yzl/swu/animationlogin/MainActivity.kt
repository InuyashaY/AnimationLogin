package yzl.swu.animationlogin

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.util.Log
import android.view.FocusFinder
import android.view.MotionEvent
import android.view.View
import android.view.WindowId
import android.view.animation.RotateAnimation
import android.view.animation.TranslateAnimation
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ViewPropertyAnimatorCompatSet
import androidx.core.view.ViewPropertyAnimatorCompat
import androidx.core.widget.addTextChangedListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), TextWatcher,View.OnFocusChangeListener {
    //懒加载定义动画对象
    //左手掌上移
    private val leftHandUpAnim by lazy {
        ObjectAnimator.ofFloat(mLeftHand,"translationY",0f).apply {
            duration = 500
        }
    }
    //左手掌下移
    private val leftHandDownAnim by lazy {
        ObjectAnimator.ofFloat(mLeftHand,"translationY",mLeftHand.height*0.5f).apply {
            duration = 500
        }
    }
    //右手掌上移
    private val rightHandUpAnim by lazy {
        ObjectAnimator.ofFloat(mRightHand,"translationY",0f).apply {
            duration = 500
        }
    }
    //右手掌下移
    private val rightHandDownAnim by lazy {
        ObjectAnimator.ofFloat(mRightHand,"translationY",mRightHand.height*0.5f).apply {
            duration = 500
        }
    }

    //左手臂向上旋转
    private val leftArmUpAnim:AnimatorSet by lazy {
        val rotateAnim =ObjectAnimator.ofFloat(mLeftArm,"rotation",140f)
        val tx = PropertyValuesHolder.ofFloat("translationX",dp2px(47f))
        val ty = PropertyValuesHolder.ofFloat("translationY",dp2px(-40f))
        val translate = ObjectAnimator.ofPropertyValuesHolder(mLeftArm,tx,ty)
        AnimatorSet().apply {
            playTogether(translate,rotateAnim)
            duration = 500
        }
    }
    //左手臂向下旋转
    private val leftArmDownAnim by lazy {
        val rotateAnim =ObjectAnimator.ofFloat(mLeftArm,"rotation",0f)
        val tx = PropertyValuesHolder.ofFloat("translationX",0f)
        val ty = PropertyValuesHolder.ofFloat("translationY",0f)
        val translate = ObjectAnimator.ofPropertyValuesHolder(mLeftArm,tx,ty)
        AnimatorSet().apply {
            playTogether(translate,rotateAnim)
            duration = 500
        }
    }
    //右手臂向上旋转
    private val rightArmUpAnim by lazy {
        val rotateAnim =ObjectAnimator.ofFloat(mRightArm,"rotation",-140f)
        val tx = PropertyValuesHolder.ofFloat("translationX",dp2px(-47f))
        val ty = PropertyValuesHolder.ofFloat("translationY",dp2px(-40f))
        val translate = ObjectAnimator.ofPropertyValuesHolder(mRightArm,tx,ty)
        AnimatorSet().apply {
            playTogether(translate,rotateAnim)
            duration = 500
        }
    }
    //右手臂向下旋转
    private val rightArmDownAnim by lazy {
        val rotateAnim =ObjectAnimator.ofFloat(mRightArm,"rotation",0f)
        val tx = PropertyValuesHolder.ofFloat("translationX",0f)
        val ty = PropertyValuesHolder.ofFloat("translationY",0f)
        val translate = ObjectAnimator.ofPropertyValuesHolder(mRightArm,tx,ty)
        AnimatorSet().apply {
            playTogether(translate,rotateAnim)
            duration = 500
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //监听输入框内容改变
        mName.addTextChangedListener(this)
        mPassword.addTextChangedListener(this)
        //监听输入框焦点改变事件
        mPassword.onFocusChangeListener = this

    }

    override fun onStart() {
        super.onStart()
        //开始虚化
        blurLayout.startBlur()

    }



    override fun onResume() {

        super.onResume()
        //停止虚化
        blurLayout.pauseBlur()
    }




    override fun afterTextChanged(s: Editable?) {
        //账号密码都有才能登录
        mLogin.isEnabled = mName.text.isNotBlank() && mPassword.text.isNotEmpty()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_DOWN){
            //隐藏键盘
            hideKeyBoard()
        }

        return super.onTouchEvent(event)
    }

    //隐藏键盘
    private fun  hideKeyBoard(){
        //取消焦点
        if (mName.hasFocus()){
            mName.clearFocus()
        }
        if (mPassword.hasFocus()){
            mPassword.clearFocus()
        }

        (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).apply {
            //隐藏
            hideSoftInputFromWindow(mPassword.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        if (hasFocus){
            //捂眼睛
            coverEye()
//            coverEyeByPropertyAnim()
//            coverEyeByTweenAnim()

        }else{
            //打开眼睛
            openEye()
//            openEyeByPropertyAnim()
//            openEyeByTeenAnim()

        }
    }

    //遮眼  ->  手掌下移  手臂旋转往上
    private fun coverEye(){
        AnimatorSet().apply {
            play(leftArmUpAnim)
                .with(rightArmUpAnim)
                .after(leftHandDownAnim)
                .after( rightHandDownAnim)
            start()
        }
    }

    //开眼  ->   手臂旋转下来   手掌上移
    private fun openEye(){
        AnimatorSet().apply {
            play(leftArmDownAnim)
                .with(rightArmDownAnim)
                .before(leftHandUpAnim)
                .before(rightHandUpAnim)
            start()
        }
    }


    // dp 转 px
    private fun dp2px(dp:Float):Float{
        val display = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(display)
        return dp*display.density
    }


    //使用ViewPropertyAnimator动画实现效果
    private fun coverEyeByPropertyAnim(){
        //左臂
        mLeftArm.animate()
            .rotation(140f)
            .translationX(dp2px(47f))
            .translationY(dp2px(-40f))
            .setDuration(500)
            .setStartDelay(500)
            .start()
        //右臂
        mRightArm.animate()
            .rotation(-140f)
            .translationX(dp2px(-47f))
            .translationY(dp2px(-40f))
            .setDuration(500)
            .setStartDelay(500)
            .start()
        //左掌
        mLeftHand.animate()
            .translationY(mLeftHand.height*0.5f)
            .setDuration(500)
            .start()
        //右掌
        mRightHand.animate()
            .translationY(mRightHand.height*0.5f)
            .setDuration(500)
            .start()
    }


    private fun openEyeByPropertyAnim(){
        //左臂
        mLeftArm.animate()
            .rotation(0f)
            .translationX(0f)
            .translationY(0f)
            .setDuration(500)
            .start()
        //右臂
        mRightArm.animate()
            .rotation(0f)
            .translationX(0f)
            .translationY(0f)
            .setDuration(500)
            .withStartAction {
                //左掌
                mLeftHand.animate()
                    .translationY(0f)
                    .setDuration(500)

                    .start()
                //右掌
                mRightHand.animate()
                    .translationY(0f)
                    .setDuration(500)

                    .start()
            }
    }




    //使用补间动画实现
    private fun coverEyeByTweenAnim(){
        RotateAnimation(0f,140f,RotateAnimation.RELATIVE_TO_SELF,0.92f,RotateAnimation.RELATIVE_TO_SELF,0.25f).apply {
            fillAfter=true
            duration=500
            mLeftArm.startAnimation(this)
        }

        TranslateAnimation(0f,0f,0f,mLeftHand.height*0.5f).apply {
            fillAfter=true
            duration=500
            mLeftHand.startAnimation(this)
            mRightHand.startAnimation(this)
        }

        RotateAnimation(0f,-140f,RotateAnimation.RELATIVE_TO_SELF,0.08f,RotateAnimation.RELATIVE_TO_SELF,0.25f).apply {
            fillAfter=true
            duration=500
            mRightArm.startAnimation(this)
        }

    }

    private  fun openEyeByTeenAnim(){
        RotateAnimation(140f,0f,RotateAnimation.RELATIVE_TO_SELF,0.92f,RotateAnimation.RELATIVE_TO_SELF,0.25f).apply {
            fillAfter=true
            duration=500
            mLeftArm.startAnimation(this)
        }

        TranslateAnimation(0f,0f,mLeftHand.height*0.5f,0f).apply {
            fillAfter=true
            duration=500
            mLeftHand.startAnimation(this)
            mRightHand.startAnimation(this)
        }

        RotateAnimation(-140f,0f,RotateAnimation.RELATIVE_TO_SELF,0.08f,RotateAnimation.RELATIVE_TO_SELF,0.25f).apply {
            fillAfter=true
            duration=500
            mRightArm.startAnimation(this)
        }
    }

}