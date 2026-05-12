package com.fluidcloud.module

import java.io.File

object Settings {
    private const val PRIVATE_PATH = "/data/data/com.fluidcloud.module/files/config.json"
    private const val SHARED_PATH = "/data/local/tmp/fluidcloud_config.json"

    var hookEnabled = true
    var leftValue = 540f
    var rightValue = 540f
    var capsuleHeight = 30
    var coverRoundRect = true
    var bgCornerRadius = 20
    var cardDividerHeight = 8
    var cardHeight = 8
    var cardCornerRadius = 20
    var artworkBgColor = "#4D000000"
    var capsuleBgColor = "#ff000000"
    var capsuleStrokeColor = "#40959595"
    var mediaCardStrokeColor = "#4D000000"
    var hotspotCapsuleDuration = 60000
    var musicCapsuleDuration = 300000
    private var loaded = false

    fun ensureLoaded() {
        if (loaded) return
        try {
            val file = File(SHARED_PATH)
            if (file.exists()) {
                val json = org.json.JSONObject(file.readText())
                hookEnabled = json.optBoolean("hookEnabled", true)
                leftValue = json.optDouble("leftValue", 440.0).toFloat()
                rightValue = json.optDouble("rightValue", 640.0).toFloat()
                capsuleHeight = json.optInt("capsuleHeight", 30)
                coverRoundRect = json.optBoolean("coverRoundRect", true)
                bgCornerRadius = json.optInt("bgCornerRadius", 20)
                cardDividerHeight = json.optInt("cardDividerHeight", 8)
                cardHeight = json.optInt("cardHeight", 8)
                cardCornerRadius = json.optInt("cardCornerRadius", 20)
                artworkBgColor = json.optString("artworkBgColor", "#4D000000")
                capsuleBgColor = json.optString("capsuleBgColor", "#ff000000")
                capsuleStrokeColor = json.optString("capsuleStrokeColor", "#40959595")
                mediaCardStrokeColor = json.optString("mediaCardStrokeColor", "#4D000000")
                hotspotCapsuleDuration = json.optInt("hotspotCapsuleDuration", 60000)
                musicCapsuleDuration = json.optInt("musicCapsuleDuration", 300000)
            }
        } catch (_: Exception) {}
        loaded = true
    }

    fun forceReload() {
        loaded = false
        ensureLoaded()
    }

    fun save() {
        try {
            val file = File(PRIVATE_PATH)
            file.parentFile?.mkdirs()
            val json = org.json.JSONObject()
            json.put("hookEnabled", hookEnabled)
            json.put("leftValue", leftValue.toDouble())
            json.put("rightValue", rightValue.toDouble())
            json.put("capsuleHeight", capsuleHeight)
            json.put("coverRoundRect", coverRoundRect)
            json.put("bgCornerRadius", bgCornerRadius)
            json.put("cardDividerHeight", cardDividerHeight)
            json.put("cardHeight", cardHeight)
            json.put("cardCornerRadius", cardCornerRadius)
            json.put("artworkBgColor", artworkBgColor)
            json.put("capsuleBgColor", capsuleBgColor)
            json.put("capsuleStrokeColor", capsuleStrokeColor)
            json.put("mediaCardStrokeColor", mediaCardStrokeColor)
            json.put("hotspotCapsuleDuration", hotspotCapsuleDuration)
            json.put("musicCapsuleDuration", musicCapsuleDuration)
            java.io.FileOutputStream(file).use { os ->
                os.write(json.toString(2).encodeToByteArray())
                os.flush()
                os.fd.sync()
            }
            Runtime.getRuntime().exec(arrayOf("su", "-c",
                "cp $PRIVATE_PATH $SHARED_PATH && chmod 644 $SHARED_PATH")).waitFor()
        } catch (_: Exception) {}
    }
}
