package com.example.xiaohongshu

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import com.example.xiaohongshu.utils.TokenManager

/**
 * 主 Activity
 * 负责承载整个应用的导航结构和底部导航栏
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    @Inject
    lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d(TAG, "onCreate: 初始化应用")

        // 获取 NavHostFragment 和 NavController
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        
        // 设置底部导航栏与 NavController 联动
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setupWithNavController(navController)

        // 监听导航目的地变化，控制底部导航栏的显示与隐藏
        navController.addOnDestinationChangedListener { _, destination, _ ->
            Log.d(TAG, "Navigated to: ${destination.label}")
            if (destination.id == R.id.loginFragment) {
                // 在登录页面隐藏底部导航栏
                bottomNavigationView.visibility = View.GONE
            } else {
                // 在其他页面显示底部导航栏
                bottomNavigationView.visibility = View.VISIBLE
            }
        }

        // 检查登录状态，实现自动登录逻辑
        val token = tokenManager.getToken()
        if (token != null) {
            Log.d(TAG, "Token found, redirecting to Home")
            // 如果已登录，修改导航图的起始目的地为首页
            val graph = navController.navInflater.inflate(R.navigation.nav_graph)
            graph.setStartDestination(R.id.homeFragment)
            navController.graph = graph
        } else {
            Log.d(TAG, "No token found, starting at Login")
        }
    }
}
