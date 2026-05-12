package com.fluidcloud.module

import android.graphics.Color
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.util.Log
import android.util.TypedValue
import android.view.View

import io.github.libxposed.api.XposedInterface
import io.github.libxposed.api.XposedModule
import io.github.libxposed.api.XposedModuleInterface

class MainModule : XposedModule() {

    override fun onPackageLoaded(param: XposedModuleInterface.PackageLoadedParam) {
        Settings.ensureLoaded()
        when (param.packageName) {
            "com.android.systemui" -> {
                hookFluidCloud(param.defaultClassLoader)
                hookCapsuleHeight()
                hookCardHeight()
                hookCardDividerHeight()
                hookCardCornerRadius()
                hookCoverRoundRect()
                hookBgCornerRadius()
                hookFgCornerRadius()
                hookArtworkBgColor(param.defaultClassLoader)
                hookCapsuleStrokeColor()
                hookMediaCardStrokeColor()
                hookServiceConfigParsing()

            }
            "com.oplus.systemui.plugins" -> {
                hookFluidCloud(param.defaultClassLoader)
            }
        }
    }

    private fun hookFluidCloud(classLoader: ClassLoader) {
        try {
            val clazz = classLoader.loadClass(
                "com.oplus.systemui.statusbar.seeding.SeedlingPluginManager\$holeRectListener\$1"
            )
            val method = clazz.declaredMethods.first {
                it.name == "onRectChanged"
                    && it.parameterTypes.size == 1
                    && it.parameterTypes[0] == RectF::class.java
            }
            hook(method).intercept(object : XposedInterface.Hooker {
                override fun intercept(chain: XposedInterface.Chain): Any? {
                    if (!Settings.hookEnabled) return chain.proceed()
                    val r = chain.getArgs()[0] as? RectF ?: return chain.proceed()
                    r.left = Settings.leftValue
                    r.right = Settings.rightValue
                    return chain.proceed()
                }
            })
        } catch (e: Throwable) {
            Log.w("FluidCloud", "hookFluidCloud failed", e)
        }
    }

    private fun hookCapsuleHeight() {
        try {
            val method = android.content.res.Resources::class.java
                .getMethod("getDimensionPixelSize", Integer.TYPE)

            hook(method).intercept(object : XposedInterface.Hooker {
                override fun intercept(chain: XposedInterface.Chain): Any? {
                    if (!Settings.hookEnabled) return chain.proceed()
                    val res = chain.getThisObject() as? android.content.res.Resources
                        ?: return chain.proceed()
                    val id = chain.getArgs()[0] as Int
                    val name = try { res.getResourceEntryName(id) } catch (_: Throwable) { null }
                    if (name != "capsule_layout_max_height") return chain.proceed()
                    val px = android.util.TypedValue.applyDimension(
                        android.util.TypedValue.COMPLEX_UNIT_DIP,
                        Settings.capsuleHeight.toFloat(),
                        res.displayMetrics
                    )
                    return (px + 0.5f).toInt()
                }
            })
        } catch (_: Throwable) {}
    }

    private fun hookCardHeight() {
        try {
            val method = android.content.res.Resources::class.java
                .getMethod("getDimensionPixelSize", Integer.TYPE)

            hook(method).intercept(object : XposedInterface.Hooker {
                override fun intercept(chain: XposedInterface.Chain): Any? {
                    if (!Settings.hookEnabled) return chain.proceed()
                    val res = chain.getThisObject() as? android.content.res.Resources
                        ?: return chain.proceed()
                    val id = chain.getArgs()[0] as Int
                    val name = try { res.getResourceEntryName(id) } catch (_: Throwable) { null }
                    if (name != "offset_between_status_bar_and_card") return chain.proceed()
                    val px = android.util.TypedValue.applyDimension(
                        android.util.TypedValue.COMPLEX_UNIT_DIP,
                        Settings.cardHeight.toFloat(),
                        res.displayMetrics
                    )
                    return (px + 0.5f).toInt()
                }
            })
        } catch (_: Throwable) {}
    }

    private fun hookCardDividerHeight() {
        try {
            val method = android.content.res.Resources::class.java
                .getMethod("getDimensionPixelSize", Integer.TYPE)

            hook(method).intercept(object : XposedInterface.Hooker {
                override fun intercept(chain: XposedInterface.Chain): Any? {
                    if (!Settings.hookEnabled) return chain.proceed()
                    val res = chain.getThisObject() as? android.content.res.Resources
                        ?: return chain.proceed()
                    val id = chain.getArgs()[0] as Int
                    val name = try { res.getResourceEntryName(id) } catch (_: Throwable) { null }
                    if (name != "capsule_card_divider_height") return chain.proceed()
                    val px = android.util.TypedValue.applyDimension(
                        android.util.TypedValue.COMPLEX_UNIT_DIP,
                        Settings.cardDividerHeight.toFloat(),
                        res.displayMetrics
                    )
                    return (px + 0.5f).toInt()
                }
            })
        } catch (_: Throwable) {}
    }

    private fun hookCardCornerRadius() {
        try {
            val method = android.content.res.Resources::class.java
                .getMethod("getDimensionPixelSize", Integer.TYPE)

            hook(method).intercept(object : XposedInterface.Hooker {
                override fun intercept(chain: XposedInterface.Chain): Any? {
                    if (!Settings.hookEnabled) return chain.proceed()
                    val res = chain.getThisObject() as? android.content.res.Resources
                        ?: return chain.proceed()
                    val id = chain.getArgs()[0] as Int
                    val name = try { res.getResourceEntryName(id) } catch (_: Throwable) { null }
                    if (name != "round_corner_radius_fluid_cloud") return chain.proceed()
                    val px = android.util.TypedValue.applyDimension(
                        android.util.TypedValue.COMPLEX_UNIT_DIP,
                        Settings.cardCornerRadius.toFloat(),
                        res.displayMetrics
                    )
                    return (px + 0.5f).toInt()
                }
            })
        } catch (_: Throwable) {}
    }

    private fun hookCoverRoundRect() {
        try {
            val method = android.content.res.Resources::class.java
                .getMethod("getString", Integer.TYPE)

            hook(method).intercept(object : XposedInterface.Hooker {
                override fun intercept(chain: XposedInterface.Chain): Any? {
                    if (!Settings.hookEnabled || !Settings.coverRoundRect) return chain.proceed()
                    val res = chain.getThisObject() as? android.content.res.Resources
                        ?: return chain.proceed()
                    val id = chain.getArgs()[0] as Int
                    val name = try { res.getResourceEntryName(id) } catch (_: Throwable) { null }
                    if (name != "shapeArtworkCircle") return chain.proceed()
                    return "round-rectangle"
                }
            })
        } catch (_: Throwable) {}
    }

    private fun hookBgCornerRadius() {
        try {
            val method = View::class.java.getMethod("setBackground", Drawable::class.java)
            hook(method).intercept(object : XposedInterface.Hooker {
                override fun intercept(chain: XposedInterface.Chain): Any? {
                    val result = chain.proceed()
                    if (!Settings.hookEnabled) return result
                    val view = chain.getThisObject() as? View ?: return result
                    if (view::class.java.name !=
                        "com.oplus.systemui.plugins.seedling.capsule.ui.view.CapsuleView"
                    ) return result
                    val bg = view.background as? GradientDrawable ?: return result
                    bg.setCornerRadius(cornerRadiusPx(view))
                    bg.setColor(Color.parseColor(Settings.capsuleBgColor))
                    return result
                }
            })
        } catch (_: Throwable) {}
    }

    private fun hookFgCornerRadius() {
        try {
            val method = android.widget.FrameLayout::class.java
                .getMethod("setForeground", Drawable::class.java)
            hook(method).intercept(object : XposedInterface.Hooker {
                override fun intercept(chain: XposedInterface.Chain): Any? {
                    val result = chain.proceed()
                    if (!Settings.hookEnabled) return result
                    val view = chain.getThisObject() as? View ?: return result
                    if (view::class.java.name !=
                        "com.oplus.systemui.plugins.seedling.capsule.ui.view.CapsuleView"
                    ) return result
                    val fg = view.foreground as? GradientDrawable ?: return result
                    fg.setCornerRadius(cornerRadiusPx(view))
                    return result
                }
            })
        } catch (_: Throwable) {}
    }

    private fun hookArtworkBgColor(classLoader: ClassLoader) {
        try {
            val clazz = classLoader.loadClass(
                "com.oplus.systemui.seedlingservice.mediaControl.SeedlingMediaData"
            )
            val method = clazz.getDeclaredMethod("getArtworkBgColor")
            hook(method).intercept(object : XposedInterface.Hooker {
                override fun intercept(chain: XposedInterface.Chain): Any? {
                    if (!Settings.hookEnabled) return chain.proceed()
                    return Settings.artworkBgColor
                }
            })
        } catch (_: Throwable) {}
    }

    private fun hookCapsuleStrokeColor() {
        try {
            val method = android.content.res.Resources::class.java
                .getMethod("getColor", Integer.TYPE, android.content.res.Resources.Theme::class.java)
            hook(method).intercept(object : XposedInterface.Hooker {
                override fun intercept(chain: XposedInterface.Chain): Any? {
                    if (!Settings.hookEnabled) return chain.proceed()
                    val res = chain.getThisObject() as? android.content.res.Resources ?: return chain.proceed()
                    val id = chain.getArgs()[0] as Int
                    val name = try { res.getResourceEntryName(id) } catch (_: Throwable) { null }
                    if (name != "capsule_stroke_default_color") return chain.proceed()
                    return Color.parseColor(Settings.capsuleStrokeColor)
                }
            })
        } catch (_: Throwable) {}
    }

    private fun hookMediaCardStrokeColor() {
        try {
            val method = android.content.res.Resources::class.java
                .getMethod("getColor", Integer.TYPE, android.content.res.Resources.Theme::class.java)
            hook(method).intercept(object : XposedInterface.Hooker {
                override fun intercept(chain: XposedInterface.Chain): Any? {
                    if (!Settings.hookEnabled) return chain.proceed()
                    val res = chain.getThisObject() as? android.content.res.Resources ?: return chain.proceed()
                    val id = chain.getArgs()[0] as Int
                    val name = try { res.getResourceEntryName(id) } catch (_: Throwable) { null }
                    if (name != "default_stroke_color") return chain.proceed()
                    return Color.parseColor(Settings.mediaCardStrokeColor)
                }
            })
        } catch (_: Throwable) {}
    }

    /**
     * Hook SharedPreferencesImpl.getStringSet() to intercept "rus_content_key" reads.
     *
     * The plugin stores capsule config as a Set<String> in SP, where each entry is:
     *   [serviceId, attr1, attr2, ..., custom_capsule_duration, ...]
     *
     * We replace index 10 (custom_capsule_duration) for hotspot/music services
     * with user-configured values from Settings.
     *
     * This is the single interception point covering ALL paths:
     *   - XML re-parses → e() → f() → SP write → SP read → our hook
     *   - Cached data  ──────────────────────→ SP read → our hook
     */
    private fun hookServiceConfigParsing() {
        try {
            Log.d("FluidCloud", "hookServiceConfigParsing: hooking SharedPreferencesImpl.getStringSet...")
            val spClass = Class.forName("android.app.SharedPreferencesImpl")
            val getSsMethod = spClass.getDeclaredMethod(
                "getStringSet", String::class.java, Set::class.java
            )

            hook(getSsMethod).intercept(object : XposedInterface.Hooker {
                override fun intercept(chain: XposedInterface.Chain): Any? {
                    val key = chain.getArgs()[0] as? String
                    if (key != "rus_content_key" || !Settings.hookEnabled) {
                        return chain.proceed()
                    }
                    @Suppress("UNCHECKED_CAST")
                    val original = chain.proceed() as? Set<String> ?: return chain.proceed()
                    if (original.isEmpty()) return original

                    val modified = HashSet<String>(original.size)
                    for (entry in original) {
                        if (!entry.startsWith("[") || !entry.endsWith("]")) {
                            modified.add(entry)
                            continue
                        }
                        val parts = entry
                            .removeSurrounding("[", "]")
                            .split(",")
                            .map { it.trim() }
                            .toMutableList()
                        if (parts.size <= 10) {
                            modified.add(entry)
                            continue
                        }
                        val serviceId = parts[0]
                        val customDuration = when (serviceId) {
                            "268451843" -> Settings.hotspotCapsuleDuration.toString()
                            "268451910", "268451911", "268452006" -> Settings.musicCapsuleDuration.toString()
                            else -> null
                        }
                        if (customDuration != null) {
                            parts[10] = customDuration
                            modified.add("[" + parts.joinToString(", ") + "]")
                            Log.d("FluidCloud",
                                "SP: override $serviceId duration -> $customDuration")
                        } else {
                            modified.add(entry)
                        }
                    }
                    return modified
                }
            })
            Log.d("FluidCloud", "hookServiceConfigParsing: SP getStringSet hook installed")
        } catch (e: Throwable) {
            Log.e("FluidCloud", "hookServiceConfigParsing: SP getStringSet failed", e)
        }
    }

    private fun cornerRadiusPx(view: View): Float = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        Settings.bgCornerRadius.toFloat(),
        view.resources.displayMetrics
    )
}
