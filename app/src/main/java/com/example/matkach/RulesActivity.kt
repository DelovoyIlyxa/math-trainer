package com.example.matkach

import android.os.Bundle
import android.widget.Button

class RulesActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rules)
        applySystemBarPadding(findViewById(R.id.main))
        //setupFontButton(findViewById(R.id.btnFontSize), findViewById(R.id.contentRoot))
        findViewById<Button>(R.id.btnBackRules).setOnClickListener { finish() }
    }
}
