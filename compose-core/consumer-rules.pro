# Jetpack Compose Consumer ProGuard Rules
-keepattributes *Annotation*,InnerClasses,EnclosingMethod

# Keep Compose Composable functions
-keepclassmembers class * {
    @androidx.compose.runtime.Composable *;
}
