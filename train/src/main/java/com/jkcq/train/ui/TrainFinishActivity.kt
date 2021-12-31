package com.jkcq.train.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import brandapp.isport.com.basicres.BaseTitleActivity
import brandapp.isport.com.basicres.base.BaseConstant
import brandapp.isport.com.basicres.commonutil.BitmapUtil
import brandapp.isport.com.basicres.commonutil.FileUtil
import brandapp.isport.com.basicres.commonutil.UIUtils
import brandapp.isport.com.basicres.commonview.TitleBarView
import brandapp.isport.com.basicres.share.CommonPopupWindowFactory
import brandapp.isport.com.basicres.share.ShareBean
import com.jkcq.train.R
import com.jkcq.train.callback.TrainFinishView
import com.jkcq.train.callback.TrainFishPre
import com.jkcq.train.http.bean.TrainCourseBean
import kotlinx.android.synthetic.main.activity_train_finish.*
import java.io.File

class TrainFinishActivity : BaseTitleActivity(), TrainFinishView {

    lateinit var mPresenter: TrainFishPre
    lateinit var mCourseId: String
    var mCurrentCourseInfo: TrainCourseBean? = null
    var isCheck = false
    override fun getLayoutId(): Int {
        return R.layout.activity_train_finish
    }

    override fun initHeader() {
        titleBarView.setLeftIcon(R.drawable.icon_back)
        titleBarView.setRightIcon(R.drawable.icon_white_share)
        titleBarView.setTitle("")
        titleBarView.setOnTitleBarClickListener(object : TitleBarView.OnTitleBarClickListener() {
            override fun onRightClicked(view: View) {
                var shareBean = ShareBean()

                shareBean.bitmap = BitmapUtil.getAnyViewShot(ll_content)
               // shareBean.file = getFullScreenBitmap(ll_content, this@TrainFinishActivity)
                //显示分享的layout
                CommonPopupWindowFactory.getInstance().showShareWindow(this@TrainFinishActivity, shareBean)
            }

            override fun onLeftClicked(view: View) {
                finish()
            }

        })
    }

    /**
     * 获取长截图
     *
     * @return
     */
    fun getFullScreenBitmap(scrollVew: ViewGroup, context: Context?): File? {
        var h = 0
        val bitmap: Bitmap
        for (i in 0 until scrollVew.childCount) {
            h += scrollVew.getChildAt(i).height
        }
        // 创建对应大小的bitmap
        /* bitmap = Bitmap.createBitmap(scrollVew.getWidth(), h - DisplayUtils.dip2px(context, 80),
                Bitmap.Config.ARGB_8888);*/bitmap = Bitmap.createBitmap(scrollVew.width, h,
                Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        scrollVew.draw(canvas)
        // 测试输出
        return FileUtil.writeImage(bitmap, FileUtil.getImageFile(FileUtil.getPhotoFileName()), 100)
    }


    override fun initEvent() {
    }

    override fun initView(view: View?) {
        rg_train_feel.setOnCheckedChangeListener { group, checkedId ->
            if (isCheck) return@setOnCheckedChangeListener
            mCurrentCourseInfo?.let {
                isCheck = true
                when (checkedId) {
                    R.id.rbtn_good -> {
                        rbtn_good.setTextColor(resources.getColor(R.color.common_text_color))
                        rbtn_not_good.setTextColor(resources.getColor(R.color.common_gray_color))
                        rbtn_normal.setTextColor(resources.getColor(R.color.common_gray_color))
                        rbtn_good.setCompoundDrawablesWithIntrinsicBounds(null, getDrawable(R.mipmap.icon_good_selected), null, null)
                        rbtn_not_good.setCompoundDrawablesWithIntrinsicBounds(null, getDrawable(R.mipmap.icon_not_good_unselected), null, null)
                        rbtn_normal.setCompoundDrawablesWithIntrinsicBounds(null, getDrawable(R.mipmap.icon_normal_unselected), null, null)
                        mPresenter.postCompleteCourse(mCourseId, "2", it)
                    }
                    R.id.rbtn_not_good -> {
                        rbtn_good.setTextColor(resources.getColor(R.color.common_gray_color))
                        rbtn_not_good.setTextColor(resources.getColor(R.color.common_text_color))
                        rbtn_normal.setTextColor(resources.getColor(R.color.common_gray_color))
                        rbtn_good.setCompoundDrawablesWithIntrinsicBounds(null, getDrawable(R.mipmap.icon_good_unselected), null, null)
                        rbtn_not_good.setCompoundDrawablesWithIntrinsicBounds(null, getDrawable(R.mipmap.icon_not_good_selected), null, null)
                        rbtn_normal.setCompoundDrawablesWithIntrinsicBounds(null, getDrawable(R.mipmap.icon_normal_unselected), null, null)
                        mPresenter.postCompleteCourse(mCourseId, "0", it)
                    }
                    R.id.rbtn_normal -> {
                        rbtn_good.setTextColor(resources.getColor(R.color.common_gray_color))
                        rbtn_not_good.setTextColor(resources.getColor(R.color.common_gray_color))
                        rbtn_normal.setTextColor(resources.getColor(R.color.common_text_color))
                        rbtn_good.setCompoundDrawablesWithIntrinsicBounds(null, getDrawable(R.mipmap.icon_good_unselected), null, null)
                        rbtn_not_good.setCompoundDrawablesWithIntrinsicBounds(null, getDrawable(R.mipmap.icon_not_good_unselected), null, null)
                        rbtn_normal.setCompoundDrawablesWithIntrinsicBounds(null, getDrawable(R.mipmap.icon_normal_selected), null, null)
                        mPresenter.postCompleteCourse(mCourseId, "1", it)
                    }
                }
            }
        }
    }

    override fun initData() {
        mPresenter = TrainFishPre(this)
        mCourseId = intent.getStringExtra(BaseConstant.EXTRA_COURSE_ID).toString()
        Log.e("testt", "mCourseId=" + mCourseId)
        mPresenter.getTrainCourseInfo(mCourseId, "2")
//        tv_time.text = "2020-08-08 12:12:12"
        //      https://test.api.mini-banana.com/isport/producer-course/onlineCourses/quitFeedback
//        tv_train_times.text = getString(R.string.finish_train_times, "2")
//        tv_course_name.text = "dfafsf"
//        tv_train_duration.text = getString(R.string.text_train_duration, "12")
//        tv_consume_cal.text = getString(R.string.train_consume_cal, "366")

    }

    override fun onFeedbackSuccess() {
        val toast = Toast.makeText(this, UIUtils.getString(R.string.commint_success), Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.CENTER, 0, 0)
        toast.show()
//        DialogFactory.getInstance().showOnlyContentDialog(this,"提交成功")
    }

    override fun onGetCourseInfoSuccess(courseInfo: TrainCourseBean) {
        mCurrentCourseInfo = courseInfo
        tv_time.text = courseInfo.lastUpdateTime
        tv_train_times.text = getString(R.string.finish_train_times, "" + courseInfo.myCompletedNums)
        tv_course_name.text = courseInfo.name
        tv_train_duration.text = getString(R.string.text_train_duration, "" + courseInfo.duration)
        tv_consume_cal.text = getString(R.string.train_consume_cal, "" + courseInfo.calories)
    }

}
