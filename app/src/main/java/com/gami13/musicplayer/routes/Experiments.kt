

@Preview(showBackground = true)
@Composable
fun ExperimentsRoute(modifier: Modifier = Modifier) {
  Column(
    modifier = modifier
      .fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {

    MusicPlayer(naturalOffset = 0.dp)
    Spacer(modifier = Modifier.height(32.dp))
  }
}

@PreviewLightDark
@Composable
fun ExperimentRoutePreview(
) {
  Previewer {
    ExperimentsRoute()
  }

}