package com.github.anastr.rxlab.controllers.schedulers

import com.github.anastr.rxlab.objects.drawing.FixedEmitsOperation
import com.github.anastr.rxlab.objects.drawing.ObserverObject
import com.github.anastr.rxlab.objects.emits.BallEmit
import com.github.anastr.rxlab.preview.OperationController
import com.github.anastr.rxlab.view.Action
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers

class IoSchedulerController: OperationController() {

    override fun onCreate() {
        setCode("Observable.just(\"A\")\n" +
                "        .observeOn(Schedulers.io())\n" +
                "        .subscribe();")

        addNote("used for any process needs to wait, like Api call, read and write on files.")

        val a = BallEmit("A")

        val justOperation = FixedEmitsOperation("just", listOf(a))
        surfaceView.addDrawingObject(justOperation)
        val observerObject = ObserverObject("Observer")
        surfaceView.addDrawingObject(observerObject)

        val actions = ArrayList<Action>()

        Observable.just(a)
            .observeOn(Schedulers.io())
            .subscribe({
                val thread = Thread.currentThread().name
                actions.add(Action(1000) {
                    it.checkThread(thread)
                    moveEmitOnRender(it, observerObject)
                })
            }, errorHandler, {
                actions.add(Action(0) { doOnRenderThread { observerObject.complete() } })
                surfaceView.actions(actions)
            })
            .disposeOnDestroy()
    }
}
