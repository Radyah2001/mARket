package com.example.market.presentation.view

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.sceneview.Scene
import io.github.sceneview.animation.Transition.animateRotation
import io.github.sceneview.math.Position
import io.github.sceneview.math.Rotation
import io.github.sceneview.node.ModelNode
import io.github.sceneview.rememberCameraManipulator
import io.github.sceneview.rememberCameraNode
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberEnvironmentLoader
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.rememberNode
import io.github.sceneview.rememberOnGestureListener
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit.MILLISECONDS
import com.example.market.R
import com.example.market.presentation.viewModel.ListingSharedViewModel
import com.example.market.ui.theme.Beige
import com.example.market.ui.theme.Black

private const val kModelFile = "models/old_couch.glb"
private const val kEnvironmentFile = "environments/sky_2k.hdr"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModelViewerScreen(listingSharedViewModel: ListingSharedViewModel) {
    ModelViewer(listingSharedViewModel.selectedListing.value?.modelUrl ?: kModelFile)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModelViewer(modelPath: String) {

    var isAnimating by remember { mutableStateOf(true) } // State to control animation
    Box(modifier = Modifier.fillMaxSize()) {
        val engine = rememberEngine()
        val modelLoader = rememberModelLoader(engine)
        val environmentLoader = rememberEnvironmentLoader(engine)

        val centerNode = rememberNode(engine)
        // We'll store our loaded ModelNode (or null if not loaded yet)
        var modelNode by remember { mutableStateOf<ModelNode?>(null) }

        val cameraNode = rememberCameraNode(engine) {
            position = Position(y = -0.5f, z = 2.0f)
            lookAt(centerNode)
            centerNode.addChildNode(this)
        }

        val cameraTransition = rememberInfiniteTransition(label = "CameraTransition")
        val cameraRotation by cameraTransition.animateRotation(
            initialValue = Rotation(y = 0.0f),
            targetValue = Rotation(y = 360.0f),
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 7.seconds.toInt(MILLISECONDS))
            )
        )
        // **Load** the model as soon as `modelPath` changes or we first enter
        LaunchedEffect(modelPath) {
            val model = modelLoader.loadModel(modelPath)  // `suspend` call
            if (model != null) {
                // Create a node for the loaded model
                modelNode = ModelNode(
                    modelInstance = model.instance,  // get the ModelInstance from Model
                    scaleToUnits = 0.25f
                )
            }
        }

        Scene(
            modifier = Modifier.fillMaxSize(),
            engine = engine,
            modelLoader = modelLoader,
            cameraNode = cameraNode,
            cameraManipulator = rememberCameraManipulator(
                orbitHomePosition = cameraNode.worldPosition,
                targetPosition = centerNode.worldPosition
            ),
            childNodes = listOfNotNull(
                centerNode,
                modelNode
            ),
            environment = environmentLoader.createHDREnvironment(
                assetFileLocation = kEnvironmentFile
            )!!,
            onFrame = {
                if (isAnimating) { // Apply rotation only if animating
                    centerNode.rotation = cameraRotation
                    cameraNode.lookAt(centerNode)
                }
            },
            onGestureListener = rememberOnGestureListener(
                onDoubleTap = { _, node ->
                    node?.apply {
                        scale *= 2.0f
                    }
                }
            )
        )

        Button(
            onClick = { isAnimating = !isAnimating }, // Toggle animation state
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Beige
            )
        ) {
            Text(if (isAnimating) "Stop Animation" else "Start Animation", color = Black)
        }
        Image(
            modifier = Modifier
                .width(192.dp)
                .align(Alignment.BottomEnd)
                .navigationBarsPadding()
                .padding(16.dp)
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(
                        alpha = 0.5f
                    ),
                    shape = MaterialTheme.shapes.medium
                )
                .padding(8.dp),
            painter = painterResource(id = R.drawable.group_1),
            contentDescription = "Logo"
        )
        TopAppBar(
            title = {
                Text(
                    text = stringResource(id = R.string.app_name)
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f),
                titleContentColor = MaterialTheme.colorScheme.onPrimary

            )
        )
    }
}