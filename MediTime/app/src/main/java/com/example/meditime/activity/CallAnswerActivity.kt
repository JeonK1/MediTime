package com.example.meditime.activity

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.speech.RecognitionListener
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.meditime.database.DBCreater
import com.example.meditime.database.DBHelper
import com.example.meditime.R
import java.util.*

/*********************************
 * 전화 받은 화면
 * 전화 알람이 왔을 때 전화 받기를 누르면 오는 화면이다
 *********************************/

class CallAnswerActivity : AppCompatActivity() {

    val TAG = "CallAnswerActivity"

    // intent data
    var alarm_id = -1
    var title = ""
    var content = ""


    // for use tts, stt in main thread
    val handler = Handler()

    // stt
    lateinit var speechRecognizer: SpeechRecognizer
    lateinit var recognitionListener: RecognitionListener

    // tts
    lateinit var tts: TextToSpeech

    // 데이터 베이스 사용
    lateinit var dbHelper: DBHelper
    lateinit var dbCreater: DBCreater

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call_answer)
        globalInit()
        requestPermission()
        clickListenerInit()

        // TTS, STT 적용하기
        ttsInit()
        sttInit()
        ttsStart("약을 드셨나요?", 500L)
    }

    fun sttStart(delayMills:Long){
        handler.postDelayed(object : Runnable{
            override fun run() {
                speechRecognizer.startListening(intent)
            }
        }, delayMills)
    }

    fun ttsStart(speak_text:String, delayMills:Long){
        // speak_text 를 TTS로 내보내기
        handler.postDelayed(object : Runnable{
            override fun run() {
                tts.speak(speak_text, TextToSpeech.QUEUE_FLUSH, null, this.hashCode().toString())
            }
        }, delayMills)
    }

    private fun ttsInit() {
        // Text To Speech Init
        tts = TextToSpeech(this, object: TextToSpeech.OnInitListener {
            override fun onInit(status: Int) {
                if(status != TextToSpeech.ERROR){
                    tts.language = Locale.KOREAN
                    tts.setOnUtteranceProgressListener(object:UtteranceProgressListener(){
                        override fun onStart(utteranceId: String?) {
                            Log.e(TAG, "tts onStart()")
                        }

                        override fun onDone(utteranceId: String?) {
                            Log.e(TAG, "tts onDone()")
                            sttStart(500L) // tts 종료 후 사용자로부터 음성 인식 준비
                        }

                        override fun onError(utteranceId: String?) {
                            Log.e(TAG, "tts onError()")
                        }
                    })
                }
            }
        })
    }

    private fun sttInit() {
        // Speech To Text Init
        recognitionListener = object: RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                Toast.makeText(applicationContext, "음성인식을 시작합니다", Toast.LENGTH_SHORT).show()
            }

            override fun onRmsChanged(rmsdB: Float) {

            }

            override fun onBufferReceived(buffer: ByteArray?) {

            }

            override fun onPartialResults(partialResults: Bundle?) {

            }

            override fun onEvent(eventType: Int, params: Bundle?) {

            }

            override fun onBeginningOfSpeech() {

            }

            override fun onEndOfSpeech() {

            }

            override fun onError(error: Int) {
                var message:String
                when(error){
                    SpeechRecognizer.ERROR_AUDIO ->
                        message = "오디오 에러"
                    SpeechRecognizer.ERROR_CLIENT ->
                        message = "클라이언트 에러"
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS ->
                        message = "퍼미션 없음 에러"
                    SpeechRecognizer.ERROR_NETWORK ->
                        message = "네트워크 에러"
                    SpeechRecognizer.ERROR_NETWORK_TIMEOUT ->
                        message = "네트워크 타임아웃"
                    SpeechRecognizer.ERROR_NO_MATCH ->
                        message = "찾을 수 없음"
                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY ->
                        message = "Recognizer가 바쁨"
                    SpeechRecognizer.ERROR_SERVER ->
                        message = "서버가 이상함"
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT ->
                        message = "말하는 시간 초과"
                    else ->
                        message = "알 수 없는 오류"
                }
                Log.e(TAG, "STT 에러발생 (${message})")
                ttsStart("잘 모르겠어요. 다시 말씀해주세요.", 500L)
            }

            override fun onResults(results: Bundle?) {
                var result_text = ""
                var matches: ArrayList<String> = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION) as ArrayList<String>
                for (i in 0 until matches.size){
                    result_text = matches[i]
                }
                Log.e(TAG, "결과: ${result_text}")

                //regex 적용
                val regex_no = """(아직|아니|안먹|안 먹|no)""".toRegex()
                val regex_yes = """(응|어|먹었|예스|yes)""".toRegex()
                if(regex_no.containsMatchIn(result_text)){
                    // no
                    Toast.makeText(applicationContext, "안먹었군요", Toast.LENGTH_SHORT).show()
                    finish_activity(4000L) // 4초뒤 activity 종료
                } else if(regex_yes.containsMatchIn(result_text)) {
                    // yes
                    val record_no = dbCreater.get_record_no()
                    dbCreater.set_record_check(record_no)
                    Toast.makeText(applicationContext, "먹었군요", Toast.LENGTH_SHORT).show()
                    finish_activity(4000L) // 4초뒤 activity 종료
                } else {
                    // else
                    ttsStart("잘 모르겠어요. 다시 말씀해주세요.", 500L)
                }
            }
        }
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        speechRecognizer.setRecognitionListener(recognitionListener)
    }

    private fun requestPermission() {
        // RECORD_AUDIO permission 동적허가
        if(Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.RECORD_AUDIO), 0)
        }
    }

    private fun globalInit() {
        val id = intent.getIntExtra("id", -1)
        val title = intent.getStringExtra("title")
        val content = intent.getStringExtra("content")
        if(title!=null && content!=null && id!=-1) {
            Log.e(TAG, id.toString())
            Log.e(TAG, title)
            Log.e(TAG, content)
        } else {
            Log.e(TAG, "no extra")
        }
        // Database
        dbHelper = DBHelper(this, "MediDB.db", null, 1)
        dbCreater = DBCreater(dbHelper, dbHelper.writableDatabase)
    }

    private fun clickListenerInit() {
        val exit_btn = findViewById<ImageButton>(R.id.ib_call_answer_exit)
        exit_btn.setOnClickListener {
            finish()
        }
    }

    private fun finish_activity(delayMills:Long){
        handler.postDelayed(object : Runnable{
            override fun run() {
                finish()
            }
        }, delayMills)
    }

}