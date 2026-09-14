package com.vc.androidcore.media

import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia.ImageOnly
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia.VideoOnly
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia.ImageAndVideo
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia.VisualMediaType
import androidx.fragment.app.Fragment

/**
 * Modern, lifecycle-safe single photo/media picker utilizing Android 13+ Photo Picker
 * (`PickVisualMedia`), fully backwards-compatible across all Android versions without
 * requiring storage permissions.
 *
 * Example in Activity:
 * ```kotlin
 * val photoPicker = PhotoPickerHelper(this) { uri ->
 *     uri?.let { ivProfile.setImageURI(it) }
 * }
 *
 * btnSelectPhoto.setOnClickListener {
 *     photoPicker.pickImage()
 * }
 * ```
 */
class PhotoPickerHelper {

    private val launcher: ActivityResultLauncher<PickVisualMediaRequest>

    constructor(activity: ComponentActivity, onResult: (Uri?) -> Unit) {
        launcher = activity.registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            onResult(uri)
        }
    }

    constructor(fragment: Fragment, onResult: (Uri?) -> Unit) {
        launcher = fragment.registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            onResult(uri)
        }
    }

    /**
     * Launches the Photo Picker allowing user to select only images.
     */
    fun pickImage() {
        launcher.launch(PickVisualMediaRequest(ImageOnly))
    }

    /**
     * Launches the Photo Picker allowing user to select only videos.
     */
    fun pickVideo() {
        launcher.launch(PickVisualMediaRequest(VideoOnly))
    }

    /**
     * Launches the Photo Picker allowing user to select both images and videos.
     */
    fun pickMedia() {
        launcher.launch(PickVisualMediaRequest(ImageAndVideo))
    }

    /**
     * Launches the Photo Picker with custom visual media type filter.
     */
    fun pickCustom(mediaType: VisualMediaType) {
        launcher.launch(PickVisualMediaRequest(mediaType))
    }
}

/**
 * Modern, lifecycle-safe multiple photo/media picker utilizing Android 13+
 * (`PickMultipleVisualMedia`), backwards-compatible across all Android versions.
 *
 * Example in Activity:
 * ```kotlin
 * val multiPhotoPicker = MultiPhotoPickerHelper(this, maxItems = 5) { uris ->
 *     adapter.submitList(uris)
 * }
 *
 * btnSelectGallery.setOnClickListener {
 *     multiPhotoPicker.pickImages()
 * }
 * ```
 */
class MultiPhotoPickerHelper {

    private val launcher: ActivityResultLauncher<PickVisualMediaRequest>

    constructor(activity: ComponentActivity, maxItems: Int = 10, onResult: (List<Uri>) -> Unit) {
        launcher = activity.registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(maxItems)) { uris ->
            onResult(uris)
        }
    }

    constructor(fragment: Fragment, maxItems: Int = 10, onResult: (List<Uri>) -> Unit) {
        launcher = fragment.registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(maxItems)) { uris ->
            onResult(uris)
        }
    }

    /**
     * Launches the Photo Picker allowing user to select multiple images.
     */
    fun pickImages() {
        launcher.launch(PickVisualMediaRequest(ImageOnly))
    }

    /**
     * Launches the Photo Picker allowing user to select multiple videos.
     */
    fun pickVideos() {
        launcher.launch(PickVisualMediaRequest(VideoOnly))
    }

    /**
     * Launches the Photo Picker allowing user to select multiple images and videos.
     */
    fun pickMedia() {
        launcher.launch(PickVisualMediaRequest(ImageAndVideo))
    }
}
