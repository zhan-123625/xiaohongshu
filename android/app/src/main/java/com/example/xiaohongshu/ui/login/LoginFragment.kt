package com.example.xiaohongshu.ui.login

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.xiaohongshu.R
import com.example.xiaohongshu.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint

/**
 * 登录页面 Fragment
 * 处理用户输入、协议勾选和登录操作
 */
@AndroidEntryPoint
class LoginFragment : Fragment() {

    private val TAG = "LoginFragment"
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: 初始化登录页面")

        // 帮助按钮点击事件
        binding.tvHelp.setOnClickListener {
            Log.d(TAG, "Click: Help")
            Toast.makeText(context, "帮助中心即将上线", Toast.LENGTH_SHORT).show()
        }

        // 微信登录点击事件
        binding.ivWechat.setOnClickListener {
            Log.d(TAG, "Click: WeChat Login")
            Toast.makeText(context, "微信登录即将上线", Toast.LENGTH_SHORT).show()
        }

        // 手机验证码登录点击事件
        binding.ivPhoneCode.setOnClickListener {
            Log.d(TAG, "Click: Phone Code Login")
            Toast.makeText(context, "验证码登录即将上线", Toast.LENGTH_SHORT).show()
        }

        // 登录按钮点击事件
        binding.btnLogin.setOnClickListener {
            // 检查是否勾选用户协议
            if (!binding.cbAgreement.isChecked) {
                Log.w(TAG, "Login attempt without agreement")
                Toast.makeText(context, "请阅读并同意用户协议", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val phone = binding.etPhone.text.toString()
            val password = binding.etPassword.text.toString()
            
            // 检查输入是否为空
            if (phone.isBlank() || password.isBlank()) {
                Log.w(TAG, "Login attempt with empty fields")
                Toast.makeText(context, "请输入手机号和密码", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Log.d(TAG, "Initiating login for phone: $phone")
            viewModel.login(phone, password)
        }

        // 观察登录结果
        viewModel.loginResult.observe(viewLifecycleOwner) { success ->
            if (success) {
                Log.i(TAG, "Login successful")
                Toast.makeText(context, "登录成功", Toast.LENGTH_SHORT).show()
                // 登录成功跳转至首页
                findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
            } else {
                Log.e(TAG, "Login failed")
                Toast.makeText(context, "登录失败，请检查账号密码", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
