package com.royalit.garghi.Fragments

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.royalit.garghi.Activitys.AboutUsActivity
import com.royalit.garghi.Activitys.AskQuestionsActivity
import com.royalit.garghi.Activitys.ContactUsActivity
import com.royalit.garghi.Activitys.EditProfileActivity
import com.royalit.garghi.Activitys.FaqActivity
import com.royalit.garghi.Activitys.HelpAndSupportActivity
import com.royalit.garghi.Activitys.JobAlerts.JobAlertsActivity
import com.royalit.garghi.Activitys.Categorys.MyPostsActivity
import com.royalit.garghi.Activitys.MyTeamsActivity
import com.royalit.garghi.Activitys.PrivacyPolicyActivity
import com.royalit.garghi.Activitys.Sales.MyProductsActivity
import com.royalit.garghi.Activitys.TermsAndConditionsActivity
import com.royalit.garghi.Activitys.UsefulLinksActivity
import com.royalit.garghi.AdaptersAndModels.ProfileResponse
import com.royalit.garghi.Config.Preferences
import com.royalit.garghi.Config.ViewController
import com.royalit.garghi.Logins.LoginActivity
import com.royalit.garghi.R
import com.royalit.garghi.Retrofit.RetrofitClient
import com.royalit.garghi.databinding.FragmentProfileBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileFragment : Fragment(), View.OnClickListener  {

    private lateinit var binding: FragmentProfileBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()

    }

    private fun init() {

        getProfileApi()

        binding.imgEdit.setOnClickListener(this)
        binding.linearMyAccount.setOnClickListener(this)
        binding.linearMyTeam.setOnClickListener(this)
        binding.linearAboutUs.setOnClickListener(this)
        binding.linearContactUs.setOnClickListener(this)
        binding.linearTermsandConditions.setOnClickListener(this)
        binding.linearPrivacyPolicy.setOnClickListener(this)
        binding.linearFAQ.setOnClickListener(this)
        binding.linearHelpAndSupport.setOnClickListener(this)
        binding.linearLogout.setOnClickListener(this)
    }

    private fun getProfileApi() {
        val userId = Preferences.loadStringValue(requireActivity(), Preferences.userId, "")
        Log.e("userId_",userId.toString())

        ViewController.showLoading(requireActivity())
        val apiInterface = RetrofitClient.apiInterface
        apiInterface.getProfileApi(userId).enqueue(object : Callback<ProfileResponse> {
            override fun onResponse(call: Call<ProfileResponse>, response: Response<ProfileResponse>) {
                ViewController.hideLoading()
                if (response.isSuccessful) {
                    val rsp = response.body()
                    if (rsp != null) {
                        if (!rsp.data?.image.equals("")){
                            Glide.with(binding.profileImage)
                                .load(rsp.data?.image)
                                .error(R.drawable.dummy_profile)
                                .into(binding.profileImage)
                        }
                    }
                } else {
                    ViewController.showToast(requireActivity(), "Error: ${response.code()}")
                }
            }
            override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                ViewController.hideLoading()
                ViewController.showToast(requireActivity(), "Try again: ${t.message}")
            }
        })
    }


    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.imgEdit -> {
                startActivity(Intent(activity, EditProfileActivity::class.java))
            }

            R.id.linearMyAccount -> {
                startActivity(Intent(activity, EditProfileActivity::class.java))
            }

            R.id.linearMyTeam -> {
                startActivity(Intent(activity, MyTeamsActivity::class.java))
            }
            R.id.linearAboutUs -> {
                startActivity(Intent(activity, AboutUsActivity::class.java))
            }

            R.id.linearContactUs -> {
                startActivity(Intent(activity, ContactUsActivity::class.java))
            }

            R.id.linearTermsandConditions -> {
                startActivity(Intent(activity, TermsAndConditionsActivity::class.java))
            }

            R.id.linearPrivacyPolicy -> {
                startActivity(Intent(activity, PrivacyPolicyActivity::class.java))
            }

            R.id.linearFAQ -> {
                startActivity(Intent(activity, FaqActivity::class.java))
            }

            R.id.linearHelpAndSupport -> {
                startActivity(Intent(activity, HelpAndSupportActivity::class.java))
            }

            R.id.linearLogout -> {
                logOut()
            }

        }
    }


    private fun logOut() {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setMessage("Are you sure you want to logout?")
        builder.setTitle("Alert!")
        builder.setCancelable(false)
        builder.setPositiveButton(
            "Yes"
        ) { dialog: DialogInterface?, which: Int ->
            Preferences.deleteSharedPreferences(requireActivity())
            startActivity(Intent(requireActivity(), LoginActivity::class.java))
            requireActivity().finishAffinity()
        }
        builder.setNegativeButton(
            "No"
        ) { dialog: DialogInterface, which: Int ->
            dialog.cancel()
        }
        val alertDialog = builder.create()
        alertDialog.show()
    }


}