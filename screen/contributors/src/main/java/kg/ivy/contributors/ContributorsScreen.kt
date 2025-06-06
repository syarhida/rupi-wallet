package kg.ivy.contributors

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import kg.ivy.navigation.IvyPreview
import kg.ivy.navigation.Navigation
import kg.ivy.navigation.navigation
import kg.ivy.navigation.screenScopedViewModel
import kg.ivy.ui.R
import kotlinx.collections.immutable.persistentListOf

@Composable
fun ContributorsScreenImpl() {
    val viewModel: ContributorsViewModel = screenScopedViewModel()
    val uiState = viewModel.uiState()

    ContributorsUi(
        uiState = uiState,
        onEvent = {
            viewModel.onEvent(it)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ContributorsUi(
    uiState: ContributorsState,
    onEvent: (ContributorsEvent) -> Unit
) {
    val nav = navigation()
    val browser = LocalUriHandler.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    TopAppBarTitle(
                        title = when (uiState.contributorsResponse) {
                            is ContributorsResponse.Error, ContributorsResponse.Loading ->
                                stringResource(R.string.project_contributors)

                            is ContributorsResponse.Success ->
                                stringResource(
                                    R.string.contributors_number,
                                    uiState.contributorsResponse.contributors.size
                                )
                        }
                    )
                },
                navigationIcon = {
                    BackButton(nav = nav)
                }
            )
        },
        content = {
            ScreenContent(
                paddingValues = it,
                contributorsState = uiState,
                onEvent = { contributorsEvent ->
                    onEvent(contributorsEvent)
                }
            )
        },
        floatingActionButton = {
            GitHubButton {
                browser.openUri("https://github.com/syarhida/rupi-wallet")
            }
        }
    )
}

@Composable
private fun TopAppBarTitle(title: String) {
    Text(
        text = title,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
private fun BackButton(nav: Navigation) {
    IconButton(onClick = {
        nav.back()
    }) {
        Icon(
            imageVector = Icons.Filled.ArrowBack,
            contentDescription = "Back"
        )
    }
}

@Composable
private fun ScreenContent(
    paddingValues: PaddingValues,
    contributorsState: ContributorsState,
    onEvent: (ContributorsEvent) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .padding(paddingValues),
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            bottom = 8.dp
        ),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        content(contributorsState = contributorsState, onEvent = onEvent)
    }
}

private fun LazyListScope.content(
    contributorsState: ContributorsState,
    onEvent: (ContributorsEvent) -> Unit
) {
    when (contributorsState.contributorsResponse) {
        is ContributorsResponse.Error -> item(key = "Error") {
            ContributorsErrorState(
                message = contributorsState.contributorsResponse.errorMessage
            ) {
                onEvent(ContributorsEvent.TryAgainButtonClicked)
            }
        }

        ContributorsResponse.Loading -> item(key = "Loading") {
            LoadingState()
        }

        is ContributorsResponse.Success -> {
            item(key = "Project info") {
                ProjectInfoContent(contributorsState = contributorsState)
            }

            items(contributorsState.contributorsResponse.contributors) {
                ContributorCard(contributor = it)
            }
        }
    }
}

@Composable
private fun ProjectInfoContent(contributorsState: ContributorsState) {
    when (contributorsState.projectResponse) {
        ProjectResponse.Error,
        ProjectResponse.Loading -> {
            // show nothing
        }

        is ProjectResponse.Success -> ProjectInfoRow(
            projectRepositoryInfo = contributorsState.projectResponse
        )
    }
}

@Composable
private fun ProjectInfoRow(
    projectRepositoryInfo: ProjectResponse.Success,
    modifier: Modifier = Modifier
) {
    val browser = LocalUriHandler.current

    Row(modifier = modifier.fillMaxWidth()) {
        ProjectInfoButton(
            icon = {
                Icon(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(id = R.drawable.ic_vue_dev_hierarchy),
                    contentDescription = "Forks"
                )
            },
            info = "${projectRepositoryInfo.projectInfo.forks} forks",
            onClick = {
                browser.openUri("https://github.com/syarhida/rupi-wallet/fork")
            }
        )

        Spacer(modifier = Modifier.weight(1f))

        ProjectInfoButton(
            icon = {
                Icon(
                    modifier = Modifier.size(24.dp),
                    imageVector = Icons.Filled.Star,
                    contentDescription = "Stars"
                )
            },
            info = "${projectRepositoryInfo.projectInfo.stars} stars",
            onClick = {
                browser.openUri(projectRepositoryInfo.projectInfo.url)
            }
        )
    }
}

@Composable
private fun ProjectInfoButton(
    icon: @Composable () -> Unit,
    info: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    OutlinedButton(
        modifier = modifier,
        onClick = onClick
    ) {
        icon()

        Spacer(modifier = Modifier.width(4.dp))

        Text(info)
    }
}

@Composable
private fun ContributorsErrorState(
    message: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.error
        )
        ElevatedButton(
            onClick = onClick
        ) {
            Text(text = stringResource(R.string.try_again))
        }
    }
}

@Composable
private fun LoadingState(modifier: Modifier = Modifier) {
    Text(
        modifier = modifier,
        text = stringResource(R.string.loading)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ContributorCard(contributor: Contributor) {
    val browser = LocalUriHandler.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        onClick = {
            if (contributor.githubProfileUrl.isNotBlank()) {
                browser.openUri(contributor.githubProfileUrl)
            }
        }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                modifier = Modifier
                    .size(72.dp)
                    .border(
                        border = BorderStroke(
                            width = 0.5.dp,
                            color = MaterialTheme.colorScheme.outlineVariant
                        ),
                        shape = RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp)
                    ),
                model = contributor.photoUrl,
                contentDescription = null
            )

            Column(
                modifier = Modifier.padding(horizontal = 12.dp)
            ) {
                Text(
                    text = contributor.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.W600,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = when (contributor.contributionsCount.toInt()) {
                        1 -> stringResource(R.string.one_contribution)
                        else -> stringResource(
                            R.string.contributions_number,
                            contributor.contributionsCount
                        )
                    },
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun GitHubButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    FloatingActionButton(
        modifier = modifier,
        onClick = onClick,
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary
    ) {
        Icon(
            painter = painterResource(id = R.drawable.github_logo),
            contentDescription = "GitHub"
        )
    }
}

@Preview
@Composable
private fun PreviewSuccess(isDark: Boolean = false) {
    IvyPreview(isDark) {
        ContributorsUi(
            uiState = ContributorsState(
                projectResponse = ProjectResponse.Success(
                    projectInfo = ProjectRepositoryInfo(
                        forks = "259",
                        stars = "1524",
                        url = ""
                    )
                ),
                contributorsResponse = ContributorsResponse.Success(
                    contributors = persistentListOf(
                        Contributor(
                            name = "Iliyan",
                            photoUrl = "",
                            contributionsCount = "567",
                            githubProfileUrl = ""
                        )
                    )
                )
            ),
            onEvent = {}
        )
    }
}

@Preview
@Composable
private fun PreviewError() {
    IvyPreview {
        ContributorsUi(
            uiState = ContributorsState(
                projectResponse = ProjectResponse.Error,
                contributorsResponse = ContributorsResponse.Error(stringResource(R.string.error))
            ),
            onEvent = {}
        )
    }
}

@Preview
@Composable
private fun PreviewLoading() {
    IvyPreview {
        ContributorsUi(
            uiState = ContributorsState(
                projectResponse = ProjectResponse.Loading,
                contributorsResponse = ContributorsResponse.Loading
            ),
            onEvent = {}
        )
    }
}

/** For screenshot testing */
@Composable
fun ContributorScreenUiTest(isDark: Boolean) {
    PreviewSuccess(isDark)
}