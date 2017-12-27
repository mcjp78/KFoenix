package kfoenix

import javafx.animation.ParallelTransition
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.image.ImageView
import javafx.stage.Popup
import javafx.stage.PopupWindow
import javafx.stage.Screen
import javafx.stage.Window
import javafx.util.Duration
import kotlin.properties.Delegates

class KFXNotification {

    private var title               = ""
    private var text                = ""
    private var typeMessage         = "MESSAGE"
    private var position            = Pos.BOTTOM_RIGHT
    private var hideAfterDuration   = Duration.seconds(5.0)
    private var hideCloseButton     = false
    private var onAction            = EventHandler<ActionEvent> { }
    private var graphic : Node      = ImageView("/img/dialog-message.png")
    private var owner: Window?      = null
    private var screen              = Screen.getPrimary()
    val styleClass                  = mutableListOf<String>()

    fun text(text: String): KFXNotification {
        this.text = text
        return this
    }

    fun title(title: String): KFXNotification {
        this.title = title
        return this
    }

    fun position(position: Pos): KFXNotification {
        this.position = position
        return this
    }

    fun owner(owner: Any) : KFXNotification {
        if(owner is Screen) {
            this.screen = owner
        } else {
            this.owner = getWindow(owner)
        }
        return this
    }

    fun hideAfter(duration: Duration): KFXNotification {
        this.hideAfterDuration = duration
        return this
    }

    fun onAction(onAction: EventHandler<ActionEvent>): KFXNotification {
        this.onAction = onAction
        return this
    }

    fun hideCloseButton(): KFXNotification {
        this.hideCloseButton = true
        return this
    }

    fun graphic(graphic: Node): KFXNotification {
        this.graphic = graphic
        return this
    }

    private fun show() {
        KFXNotification.NotificationPopupHandler.show(this)
    }

    fun showMessage() {
        graphic(ImageView(KFXNotification::class.java.getResource("/img/dialog-message.png").toExternalForm()))
        typeMessage = "MESSAGE"
        show()
    }

    fun showSuccess() {
        graphic(ImageView(KFXNotification::class.java.getResource("/img/dialog-success.png").toExternalForm()))
        typeMessage = "SUCCESS"
        show()
    }

    fun showError() {
        graphic(ImageView(KFXNotification::class.java.getResource("/img/dialog-error.png").toExternalForm()))
        typeMessage = "ERROR"
        show()
    }

    fun showWarning() {
        graphic(ImageView(KFXNotification::class.java.getResource("/img/dialog-warning.png").toExternalForm()))
        typeMessage = "WARNING"
        show()
    }



    companion object {
        fun create(): KFXNotification = KFXNotification()

        fun getWindow(owner: Any?): Window {
            var window: Window by Delegates.notNull()
            return when (owner) {
                null -> {
                    Window.impl_getWindows().forEach {
                        window = it
                        if(window.isFocused && window !is PopupWindow) {
                            return@forEach
                        }
                    }
                    window
                }
                is Window -> owner
                is Node   -> owner.scene.window
                else -> { throw IllegalArgumentException("??") }
            }
        }
    }

    private object NotificationPopupHandler {

        private val popupsMap = hashMapOf<Pos, List<Popup>>()
        private val padding = 15
        private val parallelTransition = ParallelTransition()
        private var isShowing = false

        var startX      : Double by Delegates.notNull()
        var startY      : Double by Delegates.notNull()
        var screenWidth : Double by Delegates.notNull()
        var screenHeight: Double by Delegates.notNull()

        fun show(notification: KFXNotification) {
            var window: Window by Delegates.notNull()
            if(notification.owner == null) {
                notification.screen.visualBounds.apply {
                    startX = minX
                    startY = minY
                    screenWidth = width
                    screenHeight = height
                }
                window = getWindow(null)
            } else {
                window = notification.owner!!.apply {
                    startX = x
                    startY = y
                    screenWidth = width
                    screenHeight = height
                }
            }
            show(window, notification)
        }

        fun show(owner: Window, notification: KFXNotification) {
            var ownerWindow: Window = owner
            while(ownerWindow is PopupWindow) {
                ownerWindow = ownerWindow.ownerWindow
            }

            ownerWindow.scene?.run {
                val stylesheetUrl = KFXNotification::class.java.getResource("/css/notificationpopup.css").toExternalForm()
                if(!stylesheets.contains(stylesheetUrl)) {
                    stylesheets.add(0, stylesheetUrl)
                }
            }

            val popup = Popup()
            popup.isAutoFix = false
            val p = notification.position


        }

    }
}