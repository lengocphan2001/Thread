package com.example.thread

import android.annotation.SuppressLint
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout

class MainActivity : AppCompatActivity(), View.OnTouchListener{
    private var reSult = 0
    private var check = false
    private var dY: Float = 0.0f
    private final val MESSAGE_RUN: Int = 18
    private lateinit var myLayout: ConstraintLayout
    private lateinit var txtResult: TextView
    private lateinit var btnUp: Button
    private lateinit var btnDown: Button
    private lateinit var mHandler: Handler
    private lateinit var myHandler: Handler
    private lateinit var threadRun: Thread
    private lateinit var runnable: Runnable
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
        initHandler()
        btnDown.setOnTouchListener(this)
        btnUp.setOnTouchListener(this)
        myLayout.setOnTouchListener(this)
        threadRun = Thread(Runnable {
            txtResult.text = reSult.toString()
        })
        threadRun.start()
    }
    private fun initHandler(){
        myHandler = Handler(Looper.getMainLooper())
        mHandler = @SuppressLint("HandlerLeak")
        object : Handler() {
            override fun handleMessage(msg: Message) {
                when(msg.what){
                    MESSAGE_RUN -> {
                        txtResult.text = msg.arg1.toString()
                    }
                }
            }
        }
    }
    private fun makeMess(key: Int, message: Int): Message{
        val msg = Message()
        msg.what = message
        msg.arg1 = key
        return msg
    }
    private fun initView() {
        myLayout = findViewById(R.id.myLayout)
        txtResult = findViewById(R.id.txtResult)
        btnDown = findViewById(R.id.btnDown)
        btnUp = findViewById(R.id.btnUp)
    }
    override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
        try{

        }
        catch(e: InterruptedException){
            Thread.currentThread().interrupt()
        }
        when(p1?.actionMasked){
            MotionEvent.ACTION_DOWN -> {
                check = false
                Thread(Runnable {
                    when (p0?.id) {
                        R.id.btnUp -> {
                            reSult++
                            val msg = makeMess(reSult, MESSAGE_RUN)
                            mHandler.sendMessage(msg)
                        }
                        R.id.btnDown -> {
                            reSult--
                            val msg = makeMess(reSult, MESSAGE_RUN)
                            mHandler.sendMessage(msg)
                        }
                        else -> {
                            dY = p1.y
                        }
                    }
                }).start()
            }
            MotionEvent.ACTION_MOVE -> {
                check = false
                Thread(Runnable {
                    if (p1.y > dY) {
                        reSult--
                        val msg = makeMess(reSult, MESSAGE_RUN)
                        mHandler.sendMessage(msg)
                    } else {
                        reSult++
                        val msg = makeMess(reSult, MESSAGE_RUN)
                        mHandler.sendMessage(msg)
                    }
                    dY = p1.y
                }).start()
            }
            MotionEvent.ACTION_UP -> {
                check = true
                Thread(Runnable {
                    Thread.sleep(1000)
                    runToZero()
                }).start()
            }
        }
        return true
    }
    private fun runToZero() {
        if (reSult > 0 && check) {
            do {
                reSult--
                try {
                    Thread.sleep(70)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
                myHandler.post(Runnable {
                    txtResult.text = reSult.toString()
                })
            } while ((reSult > 0))
        } else if (reSult < 0) {
            do {
                reSult++
                try {
                    Thread.sleep(70)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
                myHandler.post(Runnable {
                    txtResult.text = reSult.toString()
                })
            } while ((reSult < 0))
        }
    }
}