package ru.flounder

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.flounder.fragments.auth.LoginFragment
import ru.flounder.fragments.menu.CreateModuleFragment
import ru.flounder.fragments.menu.DownloadsFragment
import ru.flounder.fragments.menu.MyModulesFragment
import ru.flounder.fragments.menu.StudyModulesFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, LoginFragment())
                .commit()
        }
    }

    fun navigateToStudyModules() {
        supportFragmentManager.beginTransaction()
           // .replace(R.id.fragment_container, StudyModulesFragment())
            .replace(R.id.fragment_container,  DownloadsFragment())
            .addToBackStack(null)
            .commit()
    }
}