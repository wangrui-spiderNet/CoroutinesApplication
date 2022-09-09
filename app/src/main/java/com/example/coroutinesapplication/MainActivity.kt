package com.example.coroutinesapplication

import android.graphics.*
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import java.net.URL
import java.net.URLConnection

class MainActivity : AppCompatActivity() {

    //自定义Scope,创建一个Scope
    val scope = MainScope()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val iv_bitmap_with_watermark = findViewById<ImageView>(R.id.iv_bitmap_with_watermark)
        val btnCallback = findViewById<Button>(R.id.btn_callback);
        val btnImage = findViewById<Button>(R.id.btn_image)
        val btnLunch = findViewById<Button>(R.id.btn_lunch)
        val btnLunchFast = findViewById<Button>(R.id.btn_lunch_fast)

        btnCallback.setOnClickListener {

        }

        btnImage.setOnClickListener {
            /**
             * 协程测试
             * 顺序：从网络下载图片 -> 对下载图片加水印 -> 放置到imageview上
             *
             */
            val imageJob = CoroutineScope(Dispatchers.Main).launch {
                val bitmap = getImageFromNetwork()
                val bitmapWithWatermark = createWatermark(bitmap, "CHIU")
                iv_bitmap_with_watermark.setImageBitmap(bitmapWithWatermark)
            }
        }


        btnLunch.setOnClickListener {

            CoroutineScope(Dispatchers.Main).launch {
                Toast.makeText(this@MainActivity, shopping(), Toast.LENGTH_SHORT).show()
                Toast.makeText(this@MainActivity, wash(), Toast.LENGTH_SHORT).show()
                Toast.makeText(this@MainActivity, openFire(), Toast.LENGTH_SHORT).show()
                Toast.makeText(this@MainActivity, cooking(), Toast.LENGTH_SHORT).show()
                Toast.makeText(this@MainActivity, eat(), Toast.LENGTH_SHORT).show()
                Toast.makeText(this@MainActivity, sleep(), Toast.LENGTH_SHORT).show()

            }

        }

        /**
         * 并发执行
         **/
        btnLunchFast.setOnClickListener {

            scope.launch {

                var shop = async { shopping() }
                var wash = async { wash() }
                var fire = async { openFire() }
                var cooking = async { cooking() }
                var eating = async { eat() }
                var sleeping = async { sleep() }
                var wake = async { wakeUp() }

                Toast.makeText(this@MainActivity, shop.await(), Toast.LENGTH_SHORT).show()
                Toast.makeText(this@MainActivity, wash.await(), Toast.LENGTH_SHORT).show()
                Toast.makeText(this@MainActivity, fire.await(), Toast.LENGTH_SHORT).show()
                Toast.makeText(this@MainActivity, cooking.await(), Toast.LENGTH_SHORT).show()
                Toast.makeText(this@MainActivity, eating.await(), Toast.LENGTH_SHORT).show()
                Toast.makeText(this@MainActivity, sleeping.await(), Toast.LENGTH_SHORT).show()
                Toast.makeText(this@MainActivity, wake.await(), Toast.LENGTH_SHORT).show()

            }

        }

    }

    /**
     * 挂起函数
     */
    private suspend fun shopping(): String {
        delay(5000)
        return "买到菜啦"
    }

    private suspend fun wash(): String {
        delay(3000)
        return "洗好菜啦"
    }

    private suspend fun openFire(): String {
        delay(1000)
        return "开火啦"
    }

    private suspend fun cooking(): String {
        delay(10000)
        return "饭做好啦"
    }

    private suspend fun eat(): String {
        delay(10000)
        return "吃饱啦"
    }

    /***
     * 挂起函数
     */
    private suspend fun sleep(): String {
        return withContext(Dispatchers.IO) {
            "睡了午觉"
        }
    }

    /**
     * 不能挂起
     * */
    private fun wakeUp(): String {

        return "睡醒了！"
    }

    /**
     * 从网络下载图片
     * 协程测试 耗时操作1
     */
    private suspend fun getImageFromNetwork() = withContext(Dispatchers.IO) {
        val url =
            URL("https://static.wikia.nocookie.net/dogelore/images/9/97/Doge.jpg/revision/latest?cb=20190205113053")
        val connection = url.openConnection() as URLConnection
        val inputStream = connection.getInputStream()
        BitmapFactory.decodeStream(inputStream)
    }

    /**
     * 对下载的图片加水印
     * 协程测试 耗时操作2
     */
    private suspend fun createWatermark(bitmap: Bitmap, mark: String) =
        withContext(Dispatchers.IO) {
            val w = bitmap.width
            val h = bitmap.height
            val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bmp)
            val paint = Paint()
            paint.color = Color.parseColor("#C5FF0000")
            paint.textSize = 150F
            paint.isAntiAlias = true
            canvas.drawBitmap(bitmap, 0f, 0f, paint)
            canvas.drawText(mark, 0f, (h / 2).toFloat(), paint)
            canvas.save()
            canvas.restore()
            return@withContext bmp
        }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}