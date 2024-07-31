package com.mio.activity

import android.Manifest
import android.app.NotificationManager
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.View.OnClickListener
import android.view.animation.BounceInterpolator
import android.view.animation.CycleInterpolator
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.kdt.mcgui.McAccountSpinner
import com.kdt.mcgui.ProgressLayout
import com.mio.fragments.DownloadFragment
import com.mio.fragments.HomeFragment
import com.mio.fragments.SelectAuthFragment
import com.mio.fragments.SettingFragment
import com.mio.utils.AnimUtil
import net.kdt.pojavlaunch.BaseActivity
import net.kdt.pojavlaunch.JMinecraftVersionList
import net.kdt.pojavlaunch.R
import net.kdt.pojavlaunch.Tools
import net.kdt.pojavlaunch.databinding.ActivityNewMainBinding
import net.kdt.pojavlaunch.extra.ExtraConstants
import net.kdt.pojavlaunch.extra.ExtraCore
import net.kdt.pojavlaunch.extra.ExtraListener
import net.kdt.pojavlaunch.fragments.MicrosoftLoginFragment
import net.kdt.pojavlaunch.lifecycle.ContextAwareDoneListener
import net.kdt.pojavlaunch.lifecycle.ContextExecutor
import net.kdt.pojavlaunch.modloaders.modpacks.ModloaderInstallTracker
import net.kdt.pojavlaunch.modloaders.modpacks.imagecache.IconCacheJanitor
import net.kdt.pojavlaunch.prefs.LauncherPreferences
import net.kdt.pojavlaunch.prefs.screens.LauncherPreferenceFragment
import net.kdt.pojavlaunch.progresskeeper.ProgressKeeper
import net.kdt.pojavlaunch.services.ProgressServiceKeeper
import net.kdt.pojavlaunch.tasks.AsyncMinecraftDownloader
import net.kdt.pojavlaunch.tasks.AsyncVersionList
import net.kdt.pojavlaunch.tasks.MinecraftDownloader
import net.kdt.pojavlaunch.utils.NotificationUtils
import net.kdt.pojavlaunch.value.launcherprofiles.LauncherProfiles
import java.lang.ref.WeakReference
import kotlin.system.exitProcess

class NewLauncherActivity : BaseActivity(), OnClickListener {
    private lateinit var binding: ActivityNewMainBinding
    lateinit var modInstaller: ActivityResultLauncher<String>
    private var runnable: WeakReference<Runnable>? = null
    private lateinit var notificationManager: NotificationManager
    private lateinit var progressServiceKeeper: ProgressServiceKeeper
    private lateinit var installTracker: ModloaderInstallTracker
    private lateinit var selectAuthListener: ExtraListener<Boolean>
    private lateinit var launchGameListener: ExtraListener<Boolean>
    private lateinit var createProfileListener: ExtraListener<Boolean>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initUI()
        startAnimation()
        initPojav()
    }

    private fun initUI() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_new_main)
        supportFragmentManager.beginTransaction().apply {
            setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)
            addToBackStack("ROOT")
            replace(
                R.id.container_fragment,
                HomeFragment::class.java,
                null,
                HomeFragment.TAG
            )
            commit()
        }
        setSupportActionBar(binding.toolbar)
        binding.navMain.setOnItemSelectedListener {
            val fragment = supportFragmentManager.findFragmentById(binding.containerFragment.id)
            when (it.itemId) {
                R.id.home -> if (fragment !is HomeFragment) swapFragment(
                    HomeFragment::class.java,
                    HomeFragment.TAG
                )

                R.id.download -> if (fragment !is DownloadFragment) swapFragment(
                    DownloadFragment::class.java,
                    DownloadFragment.TAG
                )

                R.id.setting -> if (fragment !is SettingFragment) swapFragment(
                    SettingFragment::class.java,
                    SettingFragment.TAG
                )
            }
            return@setOnItemSelectedListener true;
        }
        binding.exit.setOnClickListener(this)
        binding.start.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.exit -> {
                finish()
                exitProcess(0)
            }

            binding.start -> ExtraCore.setValue(ExtraConstants.LAUNCH_GAME, true)
        }
    }

    private fun startAnimation() {
        AnimUtil.playScaleX(binding.start, 2000, BounceInterpolator(), 1f, 1.8f, 1.5f).start()
        AnimUtil.playScaleY(binding.start, 2000, BounceInterpolator(), 1f, 1.8f, 1.5f).start()
        AnimUtil.playRotation(binding.start, 2000, CycleInterpolator(2f), 0f, 10f).start()
        AnimUtil.playTranslationY(binding.start, 2500, BounceInterpolator(), -500f, 0f).start()
    }

    private fun swapFragment(clazz: Class<out Fragment>, tag: String) {
        supportFragmentManager.beginTransaction().apply {
            setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)
            replace(
                R.id.container_fragment,
                clazz,
                null,
                tag
            )
            commit()
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            val fragment =
                supportFragmentManager.findFragmentByTag(MicrosoftLoginFragment.TAG) as MicrosoftLoginFragment?
            if (fragment != null && fragment.isVisible) {
                if (fragment.canGoBack()) {
                    fragment.goBack()
                    return false
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onAttachedToWindow() {
        LauncherPreferences.computeNotchSize(this)
    }

    override fun onResume() {
        super.onResume()
        ContextExecutor.setActivity(this)
        installTracker.attach()
    }

    override fun onPause() {
        super.onPause()
        ContextExecutor.clearActivity()
        installTracker.detach()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.progressLayout.cleanUpObservers()
        ProgressKeeper.removeTaskCountListener(binding.progressLayout)
        ProgressKeeper.removeTaskCountListener(progressServiceKeeper)
        ExtraCore.removeExtraListenerFromValue(
            ExtraConstants.SELECT_AUTH_METHOD,
            selectAuthListener
        )
        ExtraCore.removeExtraListenerFromValue(ExtraConstants.LAUNCH_GAME, launchGameListener)
        ExtraCore.removeExtraListenerFromValue(
            ExtraConstants.CREATE_NEW_PROFILE,
            createProfileListener
        )
    }

    private fun initPojav() {
        selectAuthListener = ExtraListener<Boolean> { _: String?, _: Boolean? ->
            val fragment =
                supportFragmentManager.findFragmentById(binding.containerFragment.id) as? HomeFragment
                    ?: return@ExtraListener false
            fragment.swapChildFragment(
                SelectAuthFragment::class.java,
                SelectAuthFragment.TAG
            )
            false
        }
        launchGameListener = ExtraListener<Boolean> { _: String?, _: Boolean? ->
            if (binding.progressLayout.hasProcesses()) {
                Toast.makeText(this, R.string.tasks_ongoing, Toast.LENGTH_LONG).show()
                return@ExtraListener false
            }
            val selectedProfile = LauncherPreferences.DEFAULT_PREF.getString(
                LauncherPreferences.PREF_KEY_CURRENT_PROFILE,
                ""
            )
            if (LauncherProfiles.mainProfileJson == null || !LauncherProfiles.mainProfileJson.profiles.containsKey(
                    selectedProfile
                )
            ) {
                Toast.makeText(this, R.string.error_no_version, Toast.LENGTH_LONG).show()
                return@ExtraListener false
            }
            val prof = LauncherProfiles.mainProfileJson.profiles[selectedProfile]
            if (prof?.lastVersionId == null || "Unknown" == prof.lastVersionId) {
                Toast.makeText(this, R.string.error_no_version, Toast.LENGTH_LONG).show()
                return@ExtraListener false
            }

            if (McAccountSpinner.getSelectedAccount() == null) {
                Toast.makeText(this, R.string.no_saved_accounts, Toast.LENGTH_LONG).show()
                ExtraCore.setValue(ExtraConstants.SELECT_AUTH_METHOD, true)
                return@ExtraListener false
            }
            val normalizedVersionId =
                AsyncMinecraftDownloader.normalizeVersionId(prof.lastVersionId)
            val mcVersion =
                AsyncMinecraftDownloader.getListedVersion(normalizedVersionId)
            MinecraftDownloader().start(
                this,
                mcVersion,
                normalizedVersionId,
                ContextAwareDoneListener(this, normalizedVersionId)
            )
            false
        }
        createProfileListener = ExtraListener<Boolean> { _: String?, _: Boolean? ->
            binding.navMain.selectedItemId = R.id.download
            false
        }
        IconCacheJanitor.runJanitor()
        modInstaller = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isAllowed: Boolean? ->
            if (!isAllowed!!) handleNoNotificationPermission()
            else {
                val runnable = runnable?.get()
                runnable?.run()
            }
        }
        checkNotificationPermission()
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        ProgressKeeper.addTaskCountListener { taskCount: Int ->
            if (taskCount > 0) {
                Tools.runOnUiThread { notificationManager.cancel(NotificationUtils.NOTIFICATION_ID_GAME_START) }
            }
        }
        progressServiceKeeper = ProgressServiceKeeper(this)
        ProgressKeeper.addTaskCountListener(progressServiceKeeper)
        ProgressKeeper.addTaskCountListener(binding.progressLayout)
        ExtraCore.addExtraListener(ExtraConstants.SELECT_AUTH_METHOD, selectAuthListener)
        ExtraCore.addExtraListener(ExtraConstants.LAUNCH_GAME, launchGameListener)
        ExtraCore.addExtraListener(ExtraConstants.CREATE_NEW_PROFILE, createProfileListener)
        AsyncVersionList().getVersionList({
            ExtraCore.setValue(
                ExtraConstants.RELEASE_TABLE,
                it
            )
        }, false)
        installTracker = ModloaderInstallTracker(this)
        binding.progressLayout.observe(ProgressLayout.DOWNLOAD_MINECRAFT)
        binding.progressLayout.observe(ProgressLayout.UNPACK_RUNTIME)
        binding.progressLayout.observe(ProgressLayout.INSTALL_MODPACK)
        binding.progressLayout.observe(ProgressLayout.AUTHENTICATE_MICROSOFT)
        binding.progressLayout.observe(ProgressLayout.DOWNLOAD_VERSION_LIST)
    }

    private fun checkNotificationPermission() {
        if (LauncherPreferences.PREF_SKIP_NOTIFICATION_PERMISSION_CHECK ||
            checkForNotificationPermission()
        ) {
            return
        }

        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            )
        ) {
            showNotificationPermissionReasoning()
            return
        }
        askForNotificationPermission(null)
    }

    private fun showNotificationPermissionReasoning() {
        AlertDialog.Builder(this).apply {
            setTitle(R.string.notification_permission_dialog_title)
            setMessage(R.string.notification_permission_dialog_text)
            setPositiveButton(android.R.string.ok) { _: DialogInterface?, _: Int ->
                askForNotificationPermission(
                    null
                )
            }
            setNegativeButton(android.R.string.cancel) { _: DialogInterface?, _: Int -> handleNoNotificationPermission() }
            show()
        }
    }

    private fun handleNoNotificationPermission() {
        LauncherPreferences.PREF_SKIP_NOTIFICATION_PERMISSION_CHECK = true
        LauncherPreferences.DEFAULT_PREF.edit()
            .putBoolean(LauncherPreferences.PREF_KEY_SKIP_NOTIFICATION_CHECK, true)
            .apply()
        Toast.makeText(this, R.string.notification_permission_toast, Toast.LENGTH_LONG).show()
    }

    fun checkForNotificationPermission(): Boolean {
        return Build.VERSION.SDK_INT < 33 || ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS
        ) != PackageManager.PERMISSION_DENIED
    }

    fun askForNotificationPermission(onSuccessRunnable: Runnable?) {
        if (Build.VERSION.SDK_INT < 33) return
        if (onSuccessRunnable != null) {
            runnable = WeakReference(onSuccessRunnable)
        }
        modInstaller.launch(Manifest.permission.POST_NOTIFICATIONS)
    }

}