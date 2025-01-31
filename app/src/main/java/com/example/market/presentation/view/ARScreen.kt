package com.example.market.presentation.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.market.R
import com.example.market.presentation.viewModel.ListingSharedViewModel
import com.google.android.filament.Engine
import com.google.ar.core.Anchor
import com.google.ar.core.Config
import com.google.ar.core.Frame
import com.google.ar.core.Plane
import com.google.ar.core.TrackingFailureReason
import io.github.sceneview.ar.ARScene
import io.github.sceneview.ar.arcore.createAnchorOrNull
import io.github.sceneview.ar.arcore.getUpdatedPlanes
import io.github.sceneview.ar.getDescription
import io.github.sceneview.ar.node.AnchorNode
import io.github.sceneview.ar.rememberARCameraNode
import io.github.sceneview.loaders.MaterialLoader
import io.github.sceneview.loaders.ModelLoader
import io.github.sceneview.node.CubeNode
import io.github.sceneview.node.ModelNode
import io.github.sceneview.rememberCollisionSystem
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberMaterialLoader
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.rememberNodes
import io.github.sceneview.rememberView
import kotlinx.coroutines.launch

private const val kModelFile = "models/old_couch.glb"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ARScreen(listingSharedViewModel: ListingSharedViewModel) {
    ARView(listingSharedViewModel.selectedListing.value?.modelUrl ?: kModelFile)
}

@Composable
fun ARView(modelPath: String) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        val coroutineScope = rememberCoroutineScope()
        val engine = rememberEngine()
        val modelLoader = rememberModelLoader(engine)
        val materialLoader = rememberMaterialLoader(engine)
        val cameraNode = rememberARCameraNode(engine)
        val childNodes = rememberNodes()
        val view = rememberView(engine)
        val collisionSystem = rememberCollisionSystem(view)

        // Flags for single auto-placement
        var autoPlaced by remember { mutableStateOf(false) }
        var loadingModel by remember { mutableStateOf(false) }

        // AR flags
        var planeRenderer by remember { mutableStateOf(true) }
        var trackingFailureReason by remember {
            mutableStateOf<TrackingFailureReason?>(null)
        }
        var frame by remember { mutableStateOf<Frame?>(null) }

        ARScene(
            modifier = Modifier.fillMaxSize(),
            childNodes = childNodes,
            engine = engine,
            view = view,
            modelLoader = modelLoader,
            collisionSystem = collisionSystem,
            sessionConfiguration = { session, config ->
                config.depthMode =
                    when (session.isDepthModeSupported(Config.DepthMode.AUTOMATIC)) {
                        true -> Config.DepthMode.AUTOMATIC
                        else -> Config.DepthMode.DISABLED
                    }
                config.instantPlacementMode = Config.InstantPlacementMode.LOCAL_Y_UP
                config.lightEstimationMode = Config.LightEstimationMode.ENVIRONMENTAL_HDR
            },
            cameraNode = cameraNode,
            planeRenderer = planeRenderer,
            onTrackingFailureChanged = { failure ->
                trackingFailureReason = failure
            },
            onSessionUpdated = { session, updatedFrame ->
                frame = updatedFrame
                // Only place one model if we haven't yet, and we're not already loading it
                if (!autoPlaced && !loadingModel) {
                    val plane = updatedFrame
                        .getUpdatedPlanes()
                        .firstOrNull { it.type == Plane.Type.HORIZONTAL_UPWARD_FACING }

                    plane?.createAnchorOrNull(plane.centerPose)?.let { anchor ->
                        loadingModel = true
                        coroutineScope.launch {
                            createAnchorNodeSuspended(
                                engine = engine,
                                modelLoader = modelLoader,
                                materialLoader = materialLoader,
                                anchor = anchor,
                                modelPath = modelPath
                            )?.let { anchorNode ->
                                childNodes += anchorNode
                                autoPlaced = true
                            }
                            loadingModel = false
                        }
                    }
                }
            },
        )

        Text(
            modifier = Modifier
                .systemBarsPadding()
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(top = 16.dp, start = 32.dp, end = 32.dp),
            textAlign = TextAlign.Center,
            fontSize = 28.sp,
            color = Color.White,
            text = trackingFailureReason?.getDescription(LocalContext.current)
                ?: if (!autoPlaced) {
                    // If no model placed yet
                    stringResource(R.string.point_your_phone_down)
                } else {
                    // Once the model is placed
                    "Model placed. Pinch or rotate to adjust."
                }
        )
    }
}

suspend fun createAnchorNodeSuspended(
    engine: Engine,
    modelLoader: ModelLoader,
    materialLoader: MaterialLoader,
    anchor: Anchor,
    modelPath: String
): AnchorNode? {
    val modelInstance = modelLoader.loadModelInstance(fileLocation = modelPath)
        ?: return null

    val anchorNode = AnchorNode(engine = engine, anchor = anchor)
    val modelNode = ModelNode(modelInstance).apply {
        isEditable = true
        editableScaleRange = 0.2f..0.75f
    }

    // Optional bounding box for editing
    val boundingBoxNode = CubeNode(
        engine,
        size = modelNode.extents,
        center = modelNode.center,
        materialInstance = materialLoader.createColorInstance(Color.White.copy(alpha = 0.5f))
    ).apply { isVisible = false }

    modelNode.addChildNode(boundingBoxNode)
    anchorNode.addChildNode(modelNode)

    // Show bounding box only while editing
    listOf(modelNode, anchorNode).forEach { node ->
        node.onEditingChanged = { editingTransforms ->
            boundingBoxNode.isVisible = editingTransforms.isNotEmpty()
        }
    }

    return anchorNode
}
