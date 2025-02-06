package com.royalit.garghi.Activitys

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.royalit.garghi.AdaptersAndModels.AskListAdapter
import com.royalit.garghi.AdaptersAndModels.AskListModel
import com.royalit.garghi.AdaptersAndModels.AskQuestionsModel
import com.royalit.garghi.AdaptersAndModels.AskQuestionsRequest
import com.royalit.garghi.Config.Preferences
import com.royalit.garghi.Config.ViewController
import com.royalit.garghi.R
import com.royalit.garghi.Retrofit.RetrofitClient
import com.royalit.garghi.databinding.ActivityAskQuestionsBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AskQuestionsActivity : AppCompatActivity(), View.OnClickListener {

    val binding: ActivityAskQuestionsBinding by lazy {
        ActivityAskQuestionsBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        ViewController.changeStatusBarColor(this, ContextCompat.getColor(this, R.color.blue), false)

        inits()

    }

    private fun inits() {
        binding.root.findViewById<TextView>(R.id.txtTitle).text = "Send Your Suggestions"
        binding.root.findViewById<ImageView>(R.id.imgBack).setOnClickListener { finish() }


        if(!ViewController.noInterNetConnectivity(applicationContext)){
            ViewController.showToast(applicationContext, "Please check your connection ")
        }else{
            AskListListApi()
        }

        binding.cardAdd.setOnClickListener(this)
    }
    private fun AskListListApi() {
        val userId = Preferences.loadStringValue(this@AskQuestionsActivity, Preferences.userId, "")
        Log.e("userId_",userId.toString())
        if(!ViewController.noInterNetConnectivity(this@AskQuestionsActivity)){
            ViewController.showToast(this@AskQuestionsActivity, "Please check your connection ")
            return
        }else {
            ViewController.showLoading(this@AskQuestionsActivity)
            val apiInterface = RetrofitClient.apiInterface
            apiInterface.askListListApi(userId.toString()).enqueue(object : retrofit2.Callback<List<AskListModel>> {
                override fun onResponse(
                    call: retrofit2.Call<List<AskListModel>>,
                    response: retrofit2.Response<List<AskListModel>>
                ) {
                    ViewController.hideLoading()
                    if (response.isSuccessful) {
                        val rsp = response.body()
                        if (rsp != null) {
                            val joblist = response.body()
                            if (joblist != null) {
                                NotificationDataSet(joblist)
                            }
                        }
                    } else {
                       binding.txtNoData.visibility = View.VISIBLE
                    }
                }
                override fun onFailure(call: retrofit2.Call<List<AskListModel>>, t: Throwable) {
                    Log.e("cat_error", t.message.toString())
                    ViewController.hideLoading()
                    binding.txtNoData.visibility = View.VISIBLE
                    ViewController.showToast(this@AskQuestionsActivity, "Try again: ${t.message}")
                }
            })
        }
    }
    private fun NotificationDataSet(joblist: List<AskListModel>) {
        val name = Preferences.loadStringValue(this@AskQuestionsActivity, Preferences.name, "")

        binding.recyclerview.layoutManager = LinearLayoutManager(this@AskQuestionsActivity)
        binding.recyclerview.adapter = AskListAdapter(joblist,name.toString()) { item ->
            //Toast.makeText(activity, "Clicked: ${item.text}", Toast.LENGTH_SHORT).show()
        }
    }



    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.cardAdd -> {
                AddshowDialog()
            }

        }
    }

    private fun AddshowDialog() {
        val dialog = Dialog(this@AskQuestionsActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.add_question_custom_dialog)

        val window = dialog.window
        window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT, // Width
            ViewGroup.LayoutParams.WRAP_CONTENT  // Height
        )

        val imgClose = dialog.findViewById<ImageView>(R.id.imgClose)
        val questionEdit = dialog.findViewById<EditText>(R.id.questionEdit)
        val decEdit = dialog.findViewById<EditText>(R.id.decEdit)
        val cardLogin = dialog.findViewById<CardView>(R.id.cardLogin)

        cardLogin.setOnClickListener {
            val question = questionEdit.text.toString()
            val dec = decEdit.text.toString()
            if (question.equals("")) {
                ViewController.showToast(applicationContext, "Enter Question")
            }else{
                dialog.dismiss()
                askQuestionApi(question,dec)
            }
        }

        imgClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun askQuestionApi(question: String, dec: String) {
        ViewController.showLoading(this@AskQuestionsActivity)

        val userId = Preferences.loadStringValue(this@AskQuestionsActivity, Preferences.userId, "")

        val apiInterface = RetrofitClient.apiInterface
        val askQuestion = AskQuestionsRequest(question,dec,userId.toString())

        apiInterface.askQuestionApi(askQuestion).enqueue(object :
            Callback<AskQuestionsModel> {
            override fun onResponse(call: Call<AskQuestionsModel>, response: Response<AskQuestionsModel>) {
                ViewController.hideLoading()
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse != null && loginResponse.status.equals("success")) {
                        ViewController.showToast(applicationContext, "Success")
                        startActivity(Intent(this@AskQuestionsActivity, DashBoardActivity::class.java))
                    } else {
                        ViewController.showToast(applicationContext, "Failed")
                    }
                } else {
                    ViewController.showToast(applicationContext, "Error: ${response.code()}")
                }
            }
            override fun onFailure(call: Call<AskQuestionsModel>, t: Throwable) {
                ViewController.hideLoading()
                ViewController.showToast(applicationContext, "Try again: ${t.message}")
            }
        })

    }

}