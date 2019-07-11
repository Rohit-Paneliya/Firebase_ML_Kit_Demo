package com.josh.firebasemlkitdemo.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions
import com.josh.firebasemlkitdemo.R
import kotlinx.android.synthetic.main.activity_language_translation.*

class LanguageTranslationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_language_translation)

        //TODO handle download of languages
        //TODO Dropdown for multiple languages
        //TODO click on translate while downloading the text

        val options = FirebaseTranslatorOptions.Builder()
            .setSourceLanguage(FirebaseTranslateLanguage.EN)
            .setTargetLanguage(FirebaseTranslateLanguage.GU)
            .build()
        val englishGermanTranslator = FirebaseNaturalLanguage.getInstance().getTranslator(options)

        englishGermanTranslator.downloadModelIfNeeded()
            .addOnSuccessListener {
                Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
                // Model downloaded successfully. Okay to start translating.
                // (Set a flag, unhide the translation UI, etc.)
            }
            .addOnFailureListener {
                // Model couldn’t be downloaded or other internal error.
                // ...
            }

        buttonTranslate.setOnClickListener {
            translateText(englishGermanTranslator, editTextSourceText.text.toString())
        }

    }

    private fun translateText(
        englishGermanTranslator: FirebaseTranslator,
        sourceText: String
    ) {
        englishGermanTranslator.translate(sourceText)
            .addOnSuccessListener { translatedText ->
                textViewTranslatedText.text = translatedText
                // Translation successful.
            }
            .addOnFailureListener { exception ->
                // Error.
                // ...
            }
    }
}
