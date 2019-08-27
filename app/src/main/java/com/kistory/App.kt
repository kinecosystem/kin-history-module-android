package com.kistory

import android.app.Application
import org.kin.ecosystem.history.KinHistory

class App: Application() {

	override fun onCreate() {
		super.onCreate()
		KinHistory.init(this, "GAQWMXRUJGR4VGEIHNP2Q7YBHS6E6WSH7W3ONQSMEVJT4UZGGOXMIQX2")
	}
}