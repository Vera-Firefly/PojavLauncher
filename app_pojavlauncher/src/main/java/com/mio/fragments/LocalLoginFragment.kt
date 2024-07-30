package com.mio.fragments

import android.os.Bundle
import android.view.View
import com.mio.utils.FragmentUtil
import net.kdt.pojavlaunch.R
import net.kdt.pojavlaunch.Tools
import net.kdt.pojavlaunch.databinding.FragmentLocalLoginBinding
import net.kdt.pojavlaunch.extra.ExtraConstants
import net.kdt.pojavlaunch.extra.ExtraCore
import java.io.File
import java.util.regex.Pattern

class LocalLoginFragment : BaseFragment(R.layout.fragment_local_login) {
    companion object {
        const val TAG = "LocalLoginFragment"
    }

    private lateinit var binding: FragmentLocalLoginBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLocalLoginBinding.bind(view)
        binding.loginButton.setOnClickListener() {
            if (!checkEditText()) return@setOnClickListener;
            ExtraCore.setValue(
                ExtraConstants.MOJANG_LOGIN_TODO, arrayOf(
                    binding.loginEditEmail.getText().toString(), ""
                )
            )
            FragmentUtil.closeAll(parentFragmentManager)
        }
        binding.close.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun checkEditText(): Boolean {
        val text: String = binding.loginEditEmail.getText().toString()
        val pattern = Pattern.compile("[^a-zA-Z0-9_]")
        val matcher = pattern.matcher(text)

        return !(text.isEmpty() || text.length < 3 || text.length > 16 || matcher.find()
                || File(Tools.DIR_ACCOUNT_NEW + "/" + text + ".json").exists()
                )
    }
}