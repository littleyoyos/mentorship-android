package org.systers.mentorship.viewmodels

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import org.systers.mentorship.MentorshipApplication
import org.systers.mentorship.R
import org.systers.mentorship.remote.datamanager.RelationDataManager
import org.systers.mentorship.remote.responses.CustomResponse
import org.systers.mentorship.remote.responses.MentorshipRelationResponse
import org.systers.mentorship.utils.CommonUtils
import retrofit2.HttpException
import java.io.IOException
import java.util.concurrent.TimeoutException

/**
 * This class represents the [ViewModel] component used for the Sign Up Activity
 */
class RelationViewModel : ViewModel() {

    var TAG = RelationViewModel::class.java.simpleName

    private val relationDataManager: RelationDataManager = RelationDataManager()

    val successfulGet: MutableLiveData<Boolean> = MutableLiveData()
    val successfulCancel: MutableLiveData<Boolean> = MutableLiveData()
    lateinit var mentorshipRelation: MentorshipRelationResponse
    lateinit var message: String

    /**
     * Fetches current relation details
     */
    fun getCurrentRelationDetails() {
        relationDataManager.getCurrentMentorshipRelation()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableObserver<MentorshipRelationResponse>() {
                    override fun onNext(mentorshipRelationResponse: MentorshipRelationResponse) {
                        mentorshipRelation = mentorshipRelationResponse
                        successfulGet.value = true
                    }

                    override fun onError(throwable: Throwable) {
                        when (throwable) {
                            is IOException -> {
                                message = MentorshipApplication.getContext()
                                        .getString(R.string.error_please_check_internet)
                            }
                            is TimeoutException -> {
                                message = MentorshipApplication.getContext()
                                        .getString(R.string.error_request_timed_out)
                            }
                            is HttpException -> {
                                message = CommonUtils.getErrorResponse(throwable).message.toString()
                            }
                            else -> {
                                message = MentorshipApplication.getContext()
                                        .getString(R.string.error_something_went_wrong)
                                Log.e(TAG, throwable.localizedMessage)
                            }
                        }
                        successfulGet.value = false
                    }

                    override fun onComplete() {
                    }
                })
    }

    /**
     * Cancels a mentorship relation
     */
    fun cancelMentorshipRelation(relationId: Int) {
        relationDataManager.cancelMentorshipRelation(relationId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableObserver<CustomResponse>() {
                    override fun onNext(customResponse: CustomResponse) {
                        message = customResponse.message ?: MentorshipApplication.getContext()
                                .getString(R.string.error_something_went_wrong)
                        successfulCancel.value = true
                    }

                    override fun onError(throwable: Throwable) {
                        when (throwable) {
                            is IOException -> {
                                message = MentorshipApplication.getContext()
                                        .getString(R.string.error_please_check_internet)
                            }
                            is TimeoutException -> {
                                message = MentorshipApplication.getContext()
                                        .getString(R.string.error_request_timed_out)
                            }
                            is HttpException -> {
                                message = CommonUtils.getErrorResponse(throwable).message.toString()
                            }
                            else -> {
                                message = MentorshipApplication.getContext()
                                        .getString(R.string.error_something_went_wrong)
                                Log.e(TAG, throwable.localizedMessage)
                            }
                        }
                        successfulCancel.value = false
                    }

                    override fun onComplete() {
                    }
                })
    }
}