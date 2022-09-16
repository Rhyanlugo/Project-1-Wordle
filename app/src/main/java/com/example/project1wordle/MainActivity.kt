package com.example.project1wordle

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.set
import androidx.core.text.toSpannable
import nl.dionsegijn.konfetti.xml.KonfettiView

class MainActivity : AppCompatActivity()
{
	private var wordToGuess: String = FourLetterWordList.FourLetterWordList.getRandomFourLetterWord().uppercase()
	private lateinit var viewKonfetti: KonfettiView

	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)

		initializeGame()
	}

	private fun initializeGame()
	{
//		Toast.makeText(applicationContext, "Word: $wordToGuess", Toast.LENGTH_SHORT).show()

		// User Input Answers
		val guessNumOne = findViewById<TextView>(R.id.guessNumOneAnswer)
		val guessNumTwo = findViewById<TextView>(R.id.guessNumTwoAnswer)
		val guessNumThree = findViewById<TextView>(R.id.guessNumThreeAnswer)

		// Answer Check based on Word
		val guessNumOneCheck = findViewById<TextView>(R.id.guessNumOneCheckAnswer)
		val guessNumTwoCheck = findViewById<TextView>(R.id.guessNumTwoCheckAnswer)
		val guessNumThreeCheck = findViewById<TextView>(R.id.guessNumThreeCheckAnswer)

		// Input text field and button
		val editTextBox = findViewById<EditText>(R.id.inputTextBox)
		val streakText = findViewById<TextView>(R.id.streakText)
		val guessBtn = findViewById<Button>(R.id.guessBtn)
		var resetBtn = findViewById<Button>(R.id.resetBtn)

		// Word display
		var wordReveal = findViewById<TextView>(R.id.wordReveal)
		wordReveal.text = wordToGuess

		var numOfGuess = 3
		var streak = 0
		streakText.text = "Streak: $streak"
		var userWordInput: String
		var checkWordString = ""

		var sb : SpannableString

		viewKonfetti = findViewById(R.id.konfettiView)

		guessBtn.setOnClickListener {
			if (numOfGuess > 0)
			{
				userWordInput = getUserWordButtonOnClick(editTextBox)

				if (userWordInput != "" && userWordInput.length == 4)
				{
					checkWordString = checkGuess(userWordInput)

					sb = setSpannableText(checkWordString, userWordInput)

					if (checkWordString == "OOOO")
					{
						grayOutButtonAndEditTextFromWin(guessBtn, resetBtn, editTextBox, wordReveal)
						streak++
						streakText.text = "Streak: $streak"
					}

					when (numOfGuess)
					{
						3 -> setGuessTextViews(guessNumOne, guessNumOneCheck, userWordInput, sb)
						2 -> setGuessTextViews(guessNumTwo, guessNumTwoCheck, userWordInput, sb)
						1 -> setGuessTextViews(guessNumThree, guessNumThreeCheck, userWordInput, sb)
					}

					numOfGuess--
				}
			}

			if (numOfGuess == 0 && checkWordString != "OOOO")
			{
				grayOutButtonAndEditTextFromLoss(guessBtn, resetBtn, editTextBox, wordReveal)
			}
		}

		resetBtn.setOnClickListener {
			wordToGuess = FourLetterWordList.FourLetterWordList.getRandomFourLetterWord().uppercase()

//			Toast.makeText(applicationContext, "Word: $wordToGuess", Toast.LENGTH_SHORT).show()

			if (checkWordString != "OOOO")
			{
				streak = 0
				streakText.text = "Streak: $streak"
			}

			guessNumOne.text = ""
			guessNumTwo.text = ""
			guessNumThree.text = ""

			guessNumOneCheck.text = ""
			guessNumTwoCheck.text = ""
			guessNumThreeCheck.text = ""

			resetButtonsAndEditText(guessBtn, resetBtn, editTextBox, wordReveal)

			numOfGuess = 3
			userWordInput = ""
			checkWordString = ""
		}
	}

	private fun getUserWordButtonOnClick(editTextBox : EditText) : String
	{
		var userAnswer : String = editTextBox.text.toString()
		userAnswer.replace(" ", "")

		// If user has not inputted any word, display a message telling them to input a word.
		if (userAnswer.compareTo("") == 0 || userAnswer.length < 4)
		{
			Toast.makeText(applicationContext, "Please input a valid word!", Toast.LENGTH_SHORT).show()
		}
		else
		{
			userAnswer = editTextBox.text.toString().uppercase()
			editTextBox.setText("")
			closeKeyboard()
		}

		return userAnswer
	}

	private fun checkGuess(guess: String) : String {
		var result = ""
		for (i in 0..3) {
			if (guess[i] == wordToGuess[i])
			{
				result += "O"
			}
			else if (guess[i] in wordToGuess)
			{
				result += "+"
			}
			else
			{
				result += "X"
			}
		}
		return result
	}

	private fun setSpannableText(checkWordString: String, userWordInput : String) : SpannableString
	{
		var sb : SpannableString = userWordInput.toSpannable() as SpannableString

		sb.setSpan(ForegroundColorSpan(Color.RED), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

		for (i in checkWordString.indices)
		{
			if (checkWordString[i] == 'O')
			{
				sb.setSpan(ForegroundColorSpan(Color.RED), i, i+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
			}
			else if (checkWordString[i] == '+')
			{
				sb.setSpan(ForegroundColorSpan(Color.GREEN), i, i+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
			}
			else
			{
				sb.setSpan(ForegroundColorSpan(Color.WHITE), i, i+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
			}
		}

		return sb
	}

	private fun setGuessTextViews(guessNum : TextView, guessNumCheck : TextView, userWordInput : String, sb : SpannableString)
	{
		guessNum.text = userWordInput
		guessNumCheck.text = sb
	}

	private fun grayOutButtonAndEditTextFromWin(guessBtn : Button, resetBtn : Button, editTextBox: EditText, wordReveal : TextView)
	{
		Toast.makeText(applicationContext, "You guessed the word!", Toast.LENGTH_SHORT).show()

		rain()

		wordReveal.visibility = View.VISIBLE

		guessBtn.backgroundTintList = ColorStateList.valueOf(Color.GRAY)
		guessBtn.isClickable = false
		guessBtn.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG

		resetBtn.visibility = View.VISIBLE
		resetBtn.isClickable = true

		editTextBox.inputType = 0
		editTextBox.setText("")
		editTextBox.hint = "You guessed correctly!"

		closeKeyboard()
	}

	private fun grayOutButtonAndEditTextFromLoss(guessBtn : Button, resetBtn : Button, editTextBox: EditText, wordReveal : TextView)
	{
		Toast.makeText(applicationContext, "Reached maximum guesses!", Toast.LENGTH_SHORT).show()

		wordReveal.visibility = View.VISIBLE

		guessBtn.backgroundTintList = ColorStateList.valueOf(Color.GRAY)
		guessBtn.isClickable = false
		guessBtn.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG

		resetBtn.visibility = View.VISIBLE
		resetBtn.isClickable = true

		editTextBox.inputType = 0
		editTextBox.setText("")
		editTextBox.hint = "Maximum guesses reached"

		closeKeyboard()
	}

	private fun resetButtonsAndEditText(guessBtn : Button, resetBtn : Button, editTextBox: EditText, wordReveal : TextView)
	{
		wordReveal.visibility = View.INVISIBLE
		wordReveal.text = wordToGuess

		guessBtn.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#673AB7"))
		guessBtn.isClickable = true
		guessBtn.paintFlags = 0

		resetBtn.visibility = View.GONE
		resetBtn.isClickable = false

		editTextBox.inputType = 61
		editTextBox.setText("")
		editTextBox.hint = resources.getString(R.string.enter_4_letter_guess_here)

	}


	private fun festive() {
		/**
		 * See [Presets] for this configuration
		 */
		viewKonfetti.start(Presets.festive())
	}

	private fun explode() {
		/**
		 * See [Presets] for this configuration
		 */
		viewKonfetti.start(Presets.explode())
	}

	private fun parade() {
		/**
		 * See [Presets] for this configuration
		 */
		viewKonfetti.start(Presets.parade())
	}

	private fun rain() {
		/**
		 * See [Presets] for this configuration
		 */
		viewKonfetti.start(Presets.rain())
	}

	private fun closeKeyboard()
	{
		val inputManager: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

		inputManager.hideSoftInputFromWindow(
			if (null == currentFocus) null else currentFocus!!.windowToken,
			InputMethodManager.HIDE_NOT_ALWAYS
		)
	}

}