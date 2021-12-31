package com.jkcq.train.ui

import android.view.View
import brandapp.isport.com.basicres.BaseActivity
import brandapp.isport.com.basicres.base.BaseConstant
import com.jkcq.train.R
import com.jkcq.train.callback.TrainFinishPresenter
import com.jkcq.train.callback.TrainFinishView
import com.jkcq.train.callback.TrainFishPre
import com.jkcq.train.http.bean.TrainCourseBean
import kotlinx.android.synthetic.main.activity_exit_train.*

class ExitTrainActivity : BaseActivity(), TrainFinishView {
    var mCurrentCourseInfo: TrainCourseBean? = null
    val mPresenter: TrainFishPre by lazy { TrainFishPre(this) }
    lateinit var mCourseId: String

    override fun initData() {
        mCourseId = intent.getStringExtra(BaseConstant.EXTRA_COURSE_ID).toString()
        mPresenter.getTrainCourseInfo(mCourseId, "1")
    }

    override fun getLayoutId(): Int = R.layout.activity_exit_train

    override fun initHeader() {
    }

    override fun initEvent() {
        iv_back.setOnClickListener { finish() }
        tv_too_easy.setOnClickListener { feedBack("0") }
        tv_too_hard.setOnClickListener { feedBack("1") }
        tv_unlike.setOnClickListener { feedBack("2") }
        tv_no_time.setOnClickListener { feedBack("3") }
    }

    override fun initView(view: View?) {
    }

    fun feedBack(type: String) {
        mCurrentCourseInfo?.let {
            mPresenter.quitCourse(mCourseId, type, it)
            finish()
        }
    }

    override fun onFeedbackSuccess() {
//        val toast = Toast.makeText(this, "提交成功", Toast.LENGTH_SHORT)
//        toast.setGravity(Gravity.CENTER, 0, 0)
//        toast.show()

    }

    override fun onGetCourseInfoSuccess(courseInfo: TrainCourseBean) {
        mCurrentCourseInfo = courseInfo
    }
}
