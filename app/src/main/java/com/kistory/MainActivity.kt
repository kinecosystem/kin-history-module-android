package com.kistory

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import org.kin.ecosystem.history.KinHistory

class MainActivity: AppCompatActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)

		val historyFragment = KinHistory.getHistoryFragment()

		historyFragment?.let {
			supportFragmentManager.beginTransaction()
					.replace(R.id.frag, historyFragment)
					.commit()
		}
	}
}