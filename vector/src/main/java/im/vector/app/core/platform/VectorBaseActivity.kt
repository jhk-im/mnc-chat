package im.vector.app.core.platform

import android.content.Context
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.view.WindowManager
import androidx.annotation.CallSuper
import androidx.annotation.MainThread
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.util.Util
import com.google.android.material.appbar.MaterialToolbar
import com.jakewharton.rxbinding3.view.clicks
import im.vector.app.core.di.*
import im.vector.app.core.extensions.restart
import im.vector.app.features.configuration.VectorConfiguration
import im.vector.app.features.navigation.Navigator
import im.vector.app.features.settings.FontScale
import im.vector.app.features.settings.VectorPreferences
import im.vector.app.features.themes.ActivityOtherThemes
import im.vector.app.features.themes.ThemeUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import org.matrix.android.sdk.api.MatrixConfiguration
import timber.log.Timber
import java.util.concurrent.TimeUnit
import kotlin.system.measureTimeMillis
import androidx.work.Configuration as WorkConfiguration

abstract class VectorBaseActivity<VB : ViewBinding> : AppCompatActivity(), HasScreenInjector {

    // ==========================================================================================
    // View
    // ==========================================================================================
    protected lateinit var views: VB

    // ==========================================================================================
    // View model
    // ==========================================================================================
    private lateinit var viewModelFactory: ViewModelProvider.Factory

    protected val viewModelProvider
        get() = ViewModelProvider(this, viewModelFactory)

    protected fun <T : VectorViewEvents> VectorViewModel<*, *, T>.observeViewEvents(observer: (T) -> Unit) {
        viewEvents
            .observe()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                hideWaitingView()
                observer(it)
            }
            .disposeOnDestroy()
    }

    // ==========================================================================================
    // Views
    // ==========================================================================================
    protected fun View.debouncedClicks(onClicked: () -> Unit) {
        clicks()
            .throttleFirst(300, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { onClicked() }
            .disposeOnDestroy()
    }

    // ==========================================================================================
    // Data
    // ==========================================================================================
    private lateinit var screenComponent: ScreenComponent
    private lateinit var configurationViewModel: ConfigurationViewModel
    private lateinit var fragmentFactory: FragmentFactory
    private lateinit var activeSessionHolder: ActiveSessionHolder
    private lateinit var vectorPreferences: VectorPreferences

    lateinit var navigator: Navigator
        private set

    private var savedInstanceState: Bundle? = null

    private val uiDisposables = CompositeDisposable()
    private val restorables = ArrayList<Restorable>()

    protected fun Disposable.disposeOnDestroy() {
        uiDisposables.add(this)
    }

    @MainThread
    protected fun <T : Restorable> T.register(): T {
        Util.assertMainThread()
        restorables.add(this)
        return this
    }

    override fun attachBaseContext(base: Context) {
        val vectorConfiguration = VectorConfiguration(this)
        super.attachBaseContext(vectorConfiguration.getLocalisedContext(base))
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        restorables.forEach { it.onSaveInstanceState(outState) }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        restorables.forEach { it.onRestoreInstanceState(savedInstanceState) }
        super.onRestoreInstanceState(savedInstanceState)
    }

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.i("onCreate Activity ${javaClass.simpleName}")
        val vectorComponent = getVectorComponent()
        screenComponent = DaggerScreenComponent.factory().create(vectorComponent, this)
        val timeForInjection = measureTimeMillis {
            injectWith(screenComponent)
        }

        Timber.v("Injecting dependencies into ${javaClass.simpleName} took $timeForInjection ms")
        ThemeUtils.setActivityTheme(this, getOtherThemes())
        fragmentFactory = screenComponent.fragmentFactory()
        supportFragmentManager.fragmentFactory = fragmentFactory

        super.onCreate(savedInstanceState)

        viewModelFactory = screenComponent.viewModelFactory()
        navigator = screenComponent.navigator()
        activeSessionHolder = screenComponent.activeSessionHolder()

        vectorPreferences = vectorComponent.vectorPreferences()
        configurationViewModel = viewModelProvider.get(ConfigurationViewModel::class.java)
        configurationViewModel.activityRestarter.observe(this) {
            if (!it.hasBeenHandled) {
                // Recreate the Activity because configuration has changed
                restart()
            }
        }

        // Set flag FLAG_SECURE
        if (vectorPreferences.useFlagSecure()) {
            window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        }

        doBeforeSetContentView()

        applyFontSize()

        views = getBinding()
        setContentView(views.root)

        this.savedInstanceState = savedInstanceState

        initUiAndData()

        val titleRes = getTitleRes()
        if (titleRes != -1) {
            supportActionBar?.let {
                it.setTitle(titleRes)
            } ?: run {
                setTitle(titleRes)
            }
        }
    }

    override fun injector(): ScreenComponent {
        return screenComponent
    }

    protected open fun injectWith(injector: ScreenComponent) = Unit

    /**
     * This method has to be called for the font size setting be supported correctly.
     */
    private fun applyFontSize() {
        resources.configuration.fontScale = FontScale.getFontScaleValue(this).scale

        @Suppress("DEPRECATION")
        resources.updateConfiguration(resources.configuration, resources.displayMetrics)
    }

    // ==========================================================================================
    // Handle loading view (also called waiting view or spinner view)
    // ==========================================================================================
    var waitingView: View? = null
        set(value) {
            field = value

            // Ensure this view is clickable to catch UI events
            value?.isClickable = true
        }

    /**
     * Hide the waiting view
     */
    open fun hideWaitingView() {
        waitingView?.isVisible = false
    }

    // ==========================================================================================
    // PRIVATE METHODS
    // ==========================================================================================
    internal fun getVectorComponent(): VectorComponent {
        return (application as HasVectorInjector).injector()
    }

    // ==========================================================================================
    // OPEN METHODS
    // ==========================================================================================
    abstract fun getBinding(): VB

    open fun doBeforeSetContentView() = Unit

    open fun initUiAndData() = Unit

    @StringRes
    open fun getTitleRes() = -1

    // ==========================================================================================
    // PUBLIC METHODS
    // ==========================================================================================
    open fun getCoordinatorLayout(): CoordinatorLayout? = null

    /**
     * Return a object containing other themes for this activity
     */
    open fun getOtherThemes(): ActivityOtherThemes = ActivityOtherThemes.Default

    // ==========================================================================================
    // PROTECTED METHODS
    // ==========================================================================================
    /**
     * Get the saved instance state.
     * Ensure {@link isFirstCreation()} returns false before calling this
     *
     * @return
     */
    protected fun getSavedInstanceState(): Bundle {
        return savedInstanceState!!
    }

    /**
     * Is first creation
     *
     * @return true if Activity is created for the first time (and not restored by the system)
     */
    protected fun isFirstCreation() = savedInstanceState == null

    /**
     * Configure the Toolbar, with default back button.
     */
    protected fun configureToolbar(toolbar: MaterialToolbar, displayBack: Boolean = true) {
        setSupportActionBar(toolbar)
        supportActionBar?.let {
            it.setDisplayShowHomeEnabled(displayBack)
            it.setDisplayHomeAsUpEnabled(displayBack)
            it.title = null
        }
    }
}
