package com.vc.composecore.media

import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

/**
 * Modern photo picker hook utilizing AndroidX ActivityResultContracts.PickVisualMedia.
 */
@Composable
fun rememberImagePicker(
    onImagePicked: (Uri?) -> Unit
): () -> Unit {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        onImagePicked(uri)
    }

    return remember {
        {
            launcher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }
    }
}

/**
 * Multi-photo picker hook.
 */
@Composable
fun rememberMultipleImagePicker(
    maxItems: Int = 10,
    onImagesPicked: (List<Uri>) -> Unit
): () -> Unit {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems)
    ) { uris: List<Uri> ->
        onImagesPicked(uris)
    }

    return remember {
        {
            launcher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }
    }
}

/**
 * Video picker hook.
 */
@Composable
fun rememberVideoPicker(
    onVideoPicked: (Uri?) -> Unit
): () -> Unit {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        onVideoPicked(uri)
    }

    return remember {
        {
            launcher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
            )
        }
    }
}

/**
 * Generic file picker hook (PDF, Docs, Any MIME).
 */
@Composable
fun rememberFilePicker(
    mimeType: String = "*/*",
    onFilePicked: (Uri?) -> Unit
): () -> Unit {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        onFilePicked(uri)
    }

    return remember(mimeType) {
        {
            launcher.launch(mimeType)
        }
    }
}

/**
 * Camera picture launcher taking a pre-created output Uri.
 */
@Composable
fun rememberCameraLauncher(
    onPictureTaken: (Boolean) -> Unit
): (Uri) -> Unit {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        onPictureTaken(success)
    }

    return remember {
        { outputUri: Uri ->
            launcher.launch(outputUri)
        }
    }
}
