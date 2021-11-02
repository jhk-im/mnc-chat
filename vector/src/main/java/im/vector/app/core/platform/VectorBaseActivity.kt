package im.vector.app.core.platform

import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import androidx.annotation.CallSuper
import androidx.annotation.MainThread
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.util.Util
import com.jakewharton.rxbinding3.view.clicks
import im.vector.app.core.di.*
import im.vector.app.core.extensions.restart
import im.vector.app.features.themes.ActivityOtherThemes
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
    private lateinit var fragmentFactory: FragmentFactory
    private lateinit var configurationViewModel: ConfigurationViewModel

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

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {

        Timber.i("onCreate Activity ${javaClass.simpleName}")
//        val vectorComponent = getVectorComponent()
//        screenComponent = DaggerScreenComponent.factory().create(vectorComponent, this)
//        val timeForInjection = measureTimeMillis {
//            injectWith(screenComponent)
//        }

//        Timber.v("Injecting dependencies into ${javaClass.simpleName} took $timeForInjection ms")
//         fragmentFactory = screenComponent.fragmentFactory()
//         supportFragmentManager.fragmentFactory = fragmentFactory

        super.onCreate(savedInstanceState)

//        viewModelFactory = screenComponent.viewModelFactory()
//        configurationViewModel = viewModelProvider.get(ConfigurationViewModel::class.java)
//        configurationViewModel.activityRestarter.observe(this) {
//            if (!it.hasBeenHandled) {
//                // Recreate the Activity because configuration has changed
//                restart()
//            }
//        }

        views = getBinding()
        setContentView(views.root)
    }

    override fun injector(): ScreenComponent {
        return screenComponent
    }

    protected open fun injectWith(injector: ScreenComponent) = Unit

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

    /**
     * Return a object containing other themes for this activity
     */
    open fun getOtherThemes(): ActivityOtherThemes = ActivityOtherThemes.Default
}
