package ru.flounder.adapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import ru.flounder.fragments.menu.CreateModuleFragment
import ru.flounder.fragments.menu.DownloadsFragment
import ru.flounder.fragments.menu.MyModulesFragment
import ru.flounder.fragments.menu.StudyModulesFragment

class ViewPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    private val fragments = listOf(
        StudyModulesFragment(),
        CreateModuleFragment(),
        DownloadsFragment(),
        MyModulesFragment()
    )

    private val fragmentTitles = listOf("Home", "Create", "Downloads", "My")

    override fun getCount(): Int = fragments.size

    override fun getItem(position: Int): Fragment = fragments[position]

    override fun getPageTitle(position: Int): CharSequence? = fragmentTitles[position]
}