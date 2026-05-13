package com.example.matkach

import android.content.Intent
import android.os.Bundle
import android.widget.*

class MainMenuActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)
        applySystemBarPadding(findViewById(R.id.main))

        val contentRoot     = findViewById<android.view.View>(R.id.contentRoot)
        //val btnFontSize     = findViewById<Button>(R.id.btnFontSize)
        val btnBegin        = findViewById<Button>(R.id.btnBegin)
        val btnAbout        = findViewById<Button>(R.id.btnAbout)
        val btnRules        = findViewById<Button>(R.id.btnRules)
        val levelGroup      = findViewById<RadioGroup>(R.id.levelGroup)
        val cbAddition      = findViewById<CheckBox>(R.id.cbAddition)
        val cbSubtraction   = findViewById<CheckBox>(R.id.cbSubtraction)
        val cbMultiplication = findViewById<CheckBox>(R.id.cbMultiplication)
        val cbDivision      = findViewById<CheckBox>(R.id.cbDivision)
        val cbWord          = findViewById<CheckBox>(R.id.cbWord)

       // setupFontButton(btnFontSize, contentRoot)

        btnRules.setOnClickListener { startActivity(Intent(this, RulesActivity::class.java)) }
        btnAbout.setOnClickListener { startActivity(Intent(this, AboutActivity::class.java)) }

        btnBegin.setOnClickListener {
            val difficulty = when (levelGroup.checkedRadioButtonId) {
                R.id.easyLevel -> "easy"
                R.id.hardLevel -> "hard"
                else -> "easy"
            }

            val selectedTypes = buildList {
                if (cbAddition.isChecked)       add("addition")
                if (cbSubtraction.isChecked)    add("subtraction")
                if (cbMultiplication.isChecked) add("multiplication")
                if (cbDivision.isChecked)       add("division")
                if (cbWord.isChecked)           add("word_problem")
            }

            if (selectedTypes.isEmpty()) {
                Toast.makeText(this, getString(R.string.error_no_types), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            startActivity(
                Intent(this, MainActivity::class.java).apply {
                    putExtra("difficulty", difficulty)
                    putStringArrayListExtra("types", ArrayList(selectedTypes))
                }
            )
        }
    }
}
