package com.example.matkach

import android.os.Bundle
import android.widget.Button

class AboutActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        applySystemBarPadding(findViewById(R.id.main))
        //setupFontButton(findViewById(R.id.btnFontSize), findViewById(R.id.contentRoot))
        findViewById<Button>(R.id.btnBack).setOnClickListener { finish() }
    }
}
