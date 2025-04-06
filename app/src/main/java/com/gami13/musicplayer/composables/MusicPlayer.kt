@file:Suppress("t")

import android.util.TypedValue
import androidx.compose.animation.core.tween
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.util.TypedValueCompat
import androidx.core.view.WindowInsetsCompat
import com.gami13.musicplayer.MainActivity
import com.gami13.musicplayer.composables.Container

enum class PlayerState {
  MINI,
  FULL
}

const val author = "Artist Name"
const val songTitle = "Song Title"

val miniHeight = 72.dp
private val miniCoverSize = 60.dp
private val fullCoverSize = 320.dp
private val miniPadding = 12.dp
private val miniTitlePadding = 12.dp
private val fullTopPadding = 72.dp
private val fullSpacing = 16.dp
private val miniTitleSize = 16.sp
private val fullTitleSize = 28.sp
private const val normalWeight = 400
private const val semiBoldWeight = 600
private val miniXOffset = miniPadding + miniCoverSize + miniTitlePadding
private const val authorWeight = 400
private val miniSpaceBetween = 8.dp
private val miniAuthorSize = 14.sp
private val fullAuthorSize = 18.sp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MusicPlayer(modifier: Modifier = Modifier, naturalOffset: Dp) {
  val windowInsets = LocalView.current.rootWindowInsets
  val statusBarHeight = remember {
    WindowInsetsCompat.toWindowInsetsCompat(windowInsets)
      .getInsets(WindowInsetsCompat.Type.statusBars()).top.toFloat().toDp()
  }

  val navigationBarHeight = remember {
    WindowInsetsCompat.toWindowInsetsCompat(windowInsets)
      .getInsets(WindowInsetsCompat.Type.navigationBars()).bottom.toFloat().toDp()
  }
  val density = LocalDensity.current
  val fullHeight = LocalConfiguration.current.screenHeightDp.dp + statusBarHeight +
          navigationBarHeight
  val fullHeightPx = with(density) {
    fullHeight.toPx()
  }
  val miniHeightPx = with(density) { miniHeight.toPx() }
  val heightRangePx = fullHeightPx - miniHeightPx


  val anchors = DraggableAnchors {
    PlayerState.FULL at 0f
    PlayerState.MINI at heightRangePx
  }
  val decayAnimationSpec = rememberSplineBasedDecay<Float>()

  val state = remember {
    AnchoredDraggableState(
      initialValue = PlayerState.MINI,
      anchors = anchors,
      positionalThreshold = { distance: Float -> distance * 0.5f },
      velocityThreshold = { with(density) { 100.dp.toPx() } },
      snapAnimationSpec = tween<Float>(durationMillis = 300),
      decayAnimationSpec = decayAnimationSpec
    )
  }


  val currentHeight = with(density) {
    (fullHeightPx - state.offset).coerceIn(miniHeightPx, fullHeightPx).toDp()
  }
  val progress = (1 - (state.offset / heightRangePx)).coerceIn(0f, 1f)

  Box(
    modifier = modifier
      .fillMaxSize()
      .offset(y = naturalOffset * (1 - progress))
  ) {
    Container(
      modifier = Modifier
        .height(currentHeight)
        .fillMaxWidth()
        .align(Alignment.BottomStart)
        .anchoredDraggable(state, Orientation.Vertical),
      shape = RoundedCornerShape(
        topStart = 8
          .dp, topEnd = 8.dp, bottomEnd = 0.dp, bottomStart = 0.dp
      ),

      ) {
      PlayerContent(progress)
    }
  }
}

@Composable
fun PlayerContent(progress: Float) {
  val authorTextSize = miniAuthorSize.plus(((fullAuthorSize.minus(miniAuthorSize)) * progress))

  val screenWidth = LocalConfiguration.current.screenWidthDp.dp
  val textMeasurer = rememberTextMeasurer()

  val miniTitleResult = textMeasurer.measure(
    text = songTitle,
    style = MaterialTheme.typography.bodyLarge.copy(
      fontSize = miniTitleSize,
      fontWeight = FontWeight(normalWeight),
    )
  )
  val miniAuthorResult = textMeasurer.measure(
    text = author,
    style = MaterialTheme.typography.bodyMedium.copy(
      fontSize = miniAuthorSize,
      fontWeight = FontWeight(authorWeight),
    )
  )
  val authorFontWeight = FontWeight(authorWeight)
  val authorNameResult = textMeasurer.measure(
    text = author,
    style = MaterialTheme.typography.bodyMedium.copy(
      fontSize = authorTextSize,
      fontWeight = authorFontWeight,
    )
  )
  val authorTextWidth = with(LocalDensity.current) { authorNameResult.size.width.toDp() }
  val titleTextSize = miniTitleSize.plus((((fullTitleSize.minus((miniTitleSize))) * progress)))
  val interpolatedTitleWeight = normalWeight + ((semiBoldWeight - normalWeight) * progress).toInt()
  val titleFontWeight = FontWeight(interpolatedTitleWeight)


  val titleLayoutResult = textMeasurer.measure(
    text = songTitle,
    style = MaterialTheme.typography.bodyLarge.copy(
      fontSize = titleTextSize,
      fontWeight = titleFontWeight,
    )
  )
  val miniSizeDifference =
    {
      (miniTitleResult.size.height.toFloat().toDp() - miniAuthorResult.size.height.toFloat()
        .toDp()) / 2f
    }
  Box(modifier = Modifier.fillMaxSize()) {
    // Mini layout (just for UI when minimized, no measurements)
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .height(100.dp)
        .padding(horizontal = 16.dp)
        .alpha(1f - progress * 3),
      verticalAlignment = Alignment.CenterVertically
    ) {

      Spacer(modifier = Modifier.size(miniCoverSize)) // Space for the cover
      Spacer(modifier = Modifier.weight(1f)) // Space for title

      Row {
        IconButton(onClick = { /* Play/Pause */ }) {
          Icon(Icons.Default.PlayArrow, contentDescription = "Play")
        }
        IconButton(onClick = { /* Next */ }) {
          Icon(Icons.Default.SkipNext, contentDescription = "Next")
        }
      }
    }

    // Full layout (Column) - only for UI elements when expanded
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp, vertical = 72.dp)
        .alpha(progress),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Spacer(modifier = Modifier.height(32.dp))

      // Artwork placeholder (just for spacing)
      Spacer(modifier = Modifier.size(fullCoverSize))

      Spacer(modifier = Modifier.height(fullSpacing + fullTitleSize.value.dp))
      Spacer(modifier = Modifier.height(fullAuthorSize.value.dp))

      Spacer(Modifier.weight(1f))

      // Controls in full view
      Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier
          .fillMaxWidth()
          .alpha(
            0f + progress
          )

      ) {
        IconButton(onClick = { /* Previous */ }) {
          Icon(Icons.Default.SkipPrevious, contentDescription = "Previous")
        }
        IconButton(onClick = { /* Play/Pause */ }) {
          Icon(
            Icons.Default.PlayArrow,
            contentDescription = "Play",
            modifier = Modifier.size(40.dp)
          )
        }
        IconButton(onClick = { /* Next */ }) {
          Icon(Icons.Default.SkipNext, contentDescription = "Next")
        }
      }
    }

    // Directly position animated elements with calculations instead of measurements

    // Animated cover art - calculate positions directly
    AnimatedCoverArt(
      progress = progress,
      miniCoverSize = miniCoverSize,
      fullCoverSize = fullCoverSize,
      screenWidth = screenWidth
    )

    // Animated title - calculate positions directly
    AnimatedTitle(
      progress = progress,
      screenWidth = screenWidth,
      miniTitleHeight = miniTitleResult.size.height.toFloat().toDp(),
      miniSizeDifference = miniSizeDifference(),
      titleLayoutWidth = titleLayoutResult.size.width.toFloat().toDp(),
      titleTextSize = titleTextSize,
      titleFontWeight = titleFontWeight
    )
//     Animated author name - calculate positions directly
    AnimatedAuthorName(
      progress = progress,
      screenWidth = screenWidth,
      authorTextSize = authorTextSize,
      authorFontWeight = authorFontWeight,
      authorTextWidth = authorTextWidth,
      miniAuthorHeight = miniAuthorResult.size.height.toFloat().toDp(),
      miniSizeDifference = miniSizeDifference()
    )
  }
}

@Composable
fun AnimatedCoverArt(
  progress: Float,
  miniCoverSize: Dp,
  fullCoverSize: Dp,
  screenWidth: Dp
) {
  val size = miniCoverSize + (fullCoverSize - miniCoverSize) * progress
  val fullScreenCenter = (screenWidth - fullCoverSize) / 2
  val miniY = (miniHeight - miniCoverSize) / 2
  val x = miniPadding + (fullScreenCenter - miniPadding) * progress
  val y = miniY + (fullTopPadding - miniY) * progress
  Box(
    modifier = Modifier
      .size(size)
      .offset(x = x, y = y)
      .background(Color(0xFF3C3F41), shape = RoundedCornerShape(8.dp)),
    contentAlignment = Alignment.Center
  ) {
    Icon(
      imageVector = Icons.Default.MusicNote,
      contentDescription = "Album Cover",
      modifier = Modifier.size(size / 2),
      tint = Color.White
    )
  }
}

@Composable
fun AnimatedTitle(
  progress: Float,
  screenWidth: Dp,
  miniTitleHeight: Dp,
  miniSizeDifference: Dp,
  titleLayoutWidth: Dp,
  titleTextSize: TextUnit,
  titleFontWeight: FontWeight
) {
  val textWidth = with(LocalDensity.current) { titleLayoutWidth }
  val fullX = (screenWidth - textWidth) / 2
  val interpolatedTitleOffset = miniXOffset + (fullX - miniXOffset) * progress
  val miniY = (miniHeight / 2) - (miniTitleHeight) / 2f -
          miniSpaceBetween - miniSizeDifference / 2
  val fullY = fullTopPadding + fullCoverSize + fullSpacing
  val y = miniY + (fullY - miniY) * progress
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .offset(y = y)
//      .background(Color.Red),
  ) {
    Text(
      text = songTitle,
      color = MaterialTheme.colorScheme.primary,

      style = MaterialTheme.typography.bodyLarge.copy(
        fontSize = titleTextSize,
        fontWeight = titleFontWeight,
      ),
      modifier = Modifier
        .offset(
          x = interpolatedTitleOffset,
        )
    )
  }
}

@Composable
fun AnimatedAuthorName(
  progress: Float,
  screenWidth: Dp,
  authorTextSize: TextUnit,
  authorFontWeight: FontWeight,
  authorTextWidth: Dp,
  miniAuthorHeight: Dp,
  miniSizeDifference: Dp
) {

  val textWidth = with(LocalDensity.current) { authorTextWidth }

  val fullX = (screenWidth - textWidth) / 2
  val interpolatedAuthorOffset = miniXOffset + (fullX - miniXOffset) * progress

  val miniY = (miniHeight / 2) - (miniAuthorHeight / 2f) +
          miniSpaceBetween + miniSizeDifference / 2

  val fullY =
    fullTopPadding + fullCoverSize + fullSpacing + fullTitleSize.value.dp + fullSpacing / 2

  val y = miniY + (fullY - miniY) * progress

  val alpha = 0.7f + (0.3f * progress)

  Box(
    modifier = Modifier
      .fillMaxWidth()
      .offset(y = y)
//      .background(Color.Red),
  ) {
    Text(
      text = author,
      color = MaterialTheme.colorScheme.onSurface,
      style = MaterialTheme.typography.bodyMedium.copy(
        fontSize = authorTextSize,
        fontWeight = authorFontWeight,
      ),
      modifier = Modifier
        .offset(x = interpolatedAuthorOffset)
        .alpha(alpha)
    )
  }
}

private fun TextUnit.minus(unit: TextUnit): TextUnit {
  val newValue = this.value - unit.value
  return TextUnit(newValue, this.type)

}

private fun TextUnit.plus(value: TextUnit): TextUnit {
  val newValue = this.value + value.value
  return TextUnit(newValue, this.type)
}

private fun Float.toDp(): Dp {
  return TypedValueCompat.deriveDimension(
    TypedValue.COMPLEX_UNIT_DIP, this, MainActivity
      .appContext.resources.displayMetrics
  ).dp
}

private fun TextUnit.toPx(): Float {
  return TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_SP,
    this.value,
    MainActivity.appContext.resources.displayMetrics
  )
}

private fun TextUnit.toDp(): Dp {
  return TypedValueCompat.deriveDimension(
    TypedValue.COMPLEX_UNIT_DIP, this.toPx(), MainActivity
      .appContext.resources.displayMetrics
  ).dp

}