package kg.ivy.onboarding.steps

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kg.ivy.data.model.Category
import kg.ivy.data.model.CategoryId
import kg.ivy.data.model.primitive.ColorInt
import kg.ivy.data.model.primitive.IconAsset
import kg.ivy.data.model.primitive.NotBlankTrimmedString
import kg.ivy.design.l0_system.UI
import kg.ivy.design.l0_system.style
import kg.ivy.legacy.IvyWalletPreview
import kg.ivy.legacy.utils.toLowerCaseLocal
import kg.ivy.navigation.navigation
import kg.ivy.onboarding.components.OnboardingProgressSlider
import kg.ivy.onboarding.components.OnboardingToolbar
import kg.ivy.onboarding.components.Suggestions
import kg.ivy.ui.R
import kg.ivy.wallet.domain.deprecated.logic.model.CreateCategoryData
import kg.ivy.wallet.ui.theme.GradientIvy
import kg.ivy.wallet.ui.theme.Green
import kg.ivy.wallet.ui.theme.Ivy
import kg.ivy.wallet.ui.theme.IvyDark
import kg.ivy.wallet.ui.theme.Orange
import kg.ivy.wallet.ui.theme.OrangeLight
import kg.ivy.wallet.ui.theme.Red
import kg.ivy.wallet.ui.theme.RedLight
import kg.ivy.wallet.ui.theme.White
import kg.ivy.wallet.ui.theme.components.GradientCutBottom
import kg.ivy.wallet.ui.theme.components.ItemIconSDefaultIcon
import kg.ivy.wallet.ui.theme.components.OnboardingButton
import kg.ivy.wallet.ui.theme.findContrastTextColor
import kg.ivy.wallet.ui.theme.modal.edit.CategoryModal
import kg.ivy.wallet.ui.theme.modal.edit.CategoryModalData
import kg.ivy.wallet.ui.theme.toComposeColor
import java.util.UUID

@ExperimentalFoundationApi
@Composable
fun BoxWithConstraintsScope.OnboardingCategories(
    suggestions: List<CreateCategoryData>,
    categories: List<Category>,

    onCreateCategory: (CreateCategoryData) -> Unit = { },
    onEditCategory: (Category) -> Unit = { _ -> },

    onSkip: () -> Unit = {},
    onDoneClick: () -> Unit = {}
) {
    var categoryModalData: CategoryModalData? by remember { mutableStateOf(null) }

    val nav = navigation()
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        stickyHeader {
            OnboardingToolbar(
                hasSkip = categories.isEmpty(),
                onBack = { nav.onBackPressed() },
                onSkip = onSkip
            )
        }

        item {
            Column {
                Spacer(Modifier.height(8.dp))

                Text(
                    modifier = Modifier.padding(horizontal = 32.dp),
                    text = stringResource(R.string.add_categories),
                    style = UI.typo.h2.style(
                        fontWeight = FontWeight.Black
                    )
                )

//                PremiumInfo(
//                    itemLabelPlural = "categories",
//                    itemsCount = categories.size,
//                    freeItemsCount = Constants.FREE_CATEGORIES
//                )

                if (categories.isEmpty()) {
                    Spacer(Modifier.height(16.dp))

                    Image(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        painter = painterResource(id = R.drawable.onboarding_illustration_categories),
                        contentDescription = "categories illustration"
                    )

                    OnboardingProgressSlider(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        selectedStep = 3,
                        stepsCount = 4,
                        selectedColor = IvyDark
                    )

                    Spacer(Modifier.height(48.dp))
                } else {
                    Spacer(Modifier.height(24.dp))
                }

                Categories(
                    categories = categories,
                    onClick = {
                        categoryModalData = CategoryModalData(
                            category = it
                        )
                    }
                )

                if (categories.isNotEmpty()) {
                    Spacer(Modifier.height(20.dp))
                }

                Text(
                    modifier = Modifier.padding(horizontal = 32.dp),
                    text = stringResource(R.string.suggestions),
                    style = UI.typo.b1.style(
                        fontWeight = FontWeight.ExtraBold
                    )
                )

                Spacer(Modifier.height(16.dp))

                Suggestions(
                    suggestions = suggestions.filter { suggestion ->
                        categories.map { it.name.value.toLowerCaseLocal() }
                            .contains(suggestion.name.toLowerCaseLocal()).not()
                    },
                    onAddSuggestion = {
                        onCreateCategory(it as CreateCategoryData)
                    },
                    onAddNew = {
                        categoryModalData = CategoryModalData(
                            category = null
                        )
                    }
                )

                Spacer(Modifier.height(96.dp))
            }
        }
    }

    GradientCutBottom(
        height = 96.dp
    )

    if (categories.isNotEmpty()) {
        OnboardingButton(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 20.dp),

            text = stringResource(R.string.finish),
            textColor = White,
            backgroundGradient = GradientIvy,
            hasNext = false,
            enabled = true
        ) {
            onDoneClick()
        }
    }

    CategoryModal(
        modal = categoryModalData,
        onCreateCategory = onCreateCategory,
        onEditCategory = onEditCategory,
        dismiss = {
            categoryModalData = null
        }
    )
}

@Composable
private fun Categories(
    categories: List<Category>,
    onClick: (Category) -> Unit
) {
    for (category in categories) {
        CategoryCard(
            category = category
        ) {
            onClick(category)
        }

        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun CategoryCard(
    category: Category,
    onClick: () -> Unit
) {
    val categoryColor = category.color.value.toComposeColor()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(UI.shapes.r3)
            .background(UI.colors.medium, UI.shapes.r3)
            .clickable {
                onClick()
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(20.dp))

        ItemIconSDefaultIcon(
            modifier = Modifier
                .background(categoryColor, CircleShape),
            iconName = category.icon?.id,
            defaultIcon = R.drawable.ic_custom_category_s,
            tint = findContrastTextColor(categoryColor)
        )

        Spacer(Modifier.width(16.dp))

        Text(
            modifier = Modifier
                .padding(start = 16.dp, end = 24.dp)
                .padding(vertical = 24.dp),
            text = category.name.value,
            style = UI.typo.b2.style(
                fontWeight = FontWeight.Bold
            )
        )

        Spacer(Modifier.width(24.dp))
    }
}

@ExperimentalFoundationApi
@Preview
@Composable
private fun Preview_Empty() {
    IvyWalletPreview {
        OnboardingCategories(
            suggestions = listOf(
                CreateCategoryData(
                    name = "Food & Drinks",
                    color = Green,
                    icon = "fooddrink"
                ),

                CreateCategoryData(
                    name = "Bills & Fees",
                    color = Red,
                    icon = "bills"
                ),

                CreateCategoryData(
                    name = "Transport",
                    color = OrangeLight,
                    icon = "transport"
                ),

                CreateCategoryData(
                    name = "Groceries",
                    color = Color(0xFF75ff4d),
                    icon = "groceries"
                ),

                CreateCategoryData(
                    name = "Entertainment",
                    color = Orange,
                    icon = "game"
                ),

                CreateCategoryData(
                    name = "Shopping",
                    color = Ivy,
                    icon = "shopping"
                ),

                CreateCategoryData(
                    name = "Gifts",
                    color = RedLight,
                    icon = "gift"
                ),

                CreateCategoryData(
                    name = "Health",
                    color = Color(0xFF4dfff3),
                    icon = "health"
                ),

                CreateCategoryData(
                    name = "Investments",
                    color = Color(0xFF1e5166),
                    icon = "leaf"
                ),
            ),
            categories = listOf()
        )
    }
}

@ExperimentalFoundationApi
@Preview
@Composable
private fun Preview_Categories() {
    IvyWalletPreview {
        OnboardingCategories(
            suggestions = listOf(
                CreateCategoryData(
                    name = "Food & Drinks",
                    color = Green,
                    icon = "fooddrink"
                ),

                CreateCategoryData(
                    name = "Bills & Fees",
                    color = Red,
                    icon = "bills"
                ),

                CreateCategoryData(
                    name = "Transport",
                    color = OrangeLight,
                    icon = "transport"
                ),

                CreateCategoryData(
                    name = "Groceries",
                    color = Color(0xFF75ff4d),
                    icon = "groceries"
                ),

                CreateCategoryData(
                    name = "Entertainment",
                    color = Orange,
                    icon = "game"
                ),

                CreateCategoryData(
                    name = "Shopping",
                    color = Ivy,
                    icon = "shopping"
                ),

                CreateCategoryData(
                    name = "Gifts",
                    color = RedLight,
                    icon = "gift"
                ),

                CreateCategoryData(
                    name = "Health",
                    color = Color(0xFF4dfff3),
                    icon = "health"
                ),

                CreateCategoryData(
                    name = "Investments",
                    color = Color(0xFF1e5166),
                    icon = "leaf"
                ),
            ),
            categories = listOf(
                Category(
                    name = NotBlankTrimmedString.unsafe("Food & Drinks"),
                    color = ColorInt(Orange.toArgb()),
                    icon = IconAsset.unsafe("fooddrinks"),
                    id = CategoryId(UUID.randomUUID()),
                    orderNum = 0.0,
                )
            )
        )
    }
}

@ExperimentalFoundationApi
@Preview
@Composable
private fun Preview_Premium() {
    IvyWalletPreview {
        OnboardingCategories(
            suggestions = listOf(
                CreateCategoryData(
                    name = "Food & Drinks",
                    color = Green,
                    icon = "fooddrink"
                ),

                CreateCategoryData(
                    name = "Bills & Fees",
                    color = Red,
                    icon = "bills"
                ),

                CreateCategoryData(
                    name = "Transport",
                    color = OrangeLight,
                    icon = "transport"
                ),

                CreateCategoryData(
                    name = "Groceries",
                    color = Color(0xFF75ff4d),
                    icon = "groceries"
                ),

                CreateCategoryData(
                    name = "Entertainment",
                    color = Orange,
                    icon = "game"
                ),

                CreateCategoryData(
                    name = "Shopping",
                    color = Ivy,
                    icon = "shopping"
                ),

                CreateCategoryData(
                    name = "Gifts",
                    color = RedLight,
                    icon = "gift"
                ),

                CreateCategoryData(
                    name = "Health",
                    color = Color(0xFF4dfff3),
                    icon = "health"
                ),

                CreateCategoryData(
                    name = "Investments",
                    color = Color(0xFF1e5166),
                    icon = "leaf"
                ),
            ),
            categories = List(12) {
                Category(
                    name = NotBlankTrimmedString.unsafe("Food & Drinks"),
                    color = ColorInt(Orange.toArgb()),
                    icon = IconAsset.unsafe("fooddrinks"),
                    id = CategoryId(UUID.randomUUID()),
                    orderNum = 0.0,
                )
            }
        )
    }
}
