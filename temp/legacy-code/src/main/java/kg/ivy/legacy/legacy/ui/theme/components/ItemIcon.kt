package kg.ivy.wallet.ui.theme.components

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kg.ivy.design.l0_system.UI
import kg.ivy.design.utils.thenWhen
import kg.ivy.legacy.IvyWalletComponentPreview
import kg.ivy.legacy.utils.toLowerCaseLocal

@Deprecated("Old design system. Use `:ivy-design` and Material3")
@Composable
fun ItemIconL(
    modifier: Modifier = Modifier,
    iconName: String?,
    tint: Color = UI.colors.pureInverse,
    iconContentScale: ContentScale? = null,
    Default: (@Composable () -> Unit)? = null
) {
    ItemIcon(
        modifier = modifier
            .size(64.dp),
        size = "l",
        iconName = iconName,
        tint = tint,
        iconContentScale = iconContentScale,
        Default = Default
    )
}

@Deprecated("Old design system. Use `:ivy-design` and Material3")
@Composable
fun ItemIconMDefaultIcon(
    modifier: Modifier = Modifier,
    iconName: String?,
    tint: Color = UI.colors.pureInverse,
    @DrawableRes defaultIcon: Int
) {
    ItemIconM(
        modifier = modifier,
        iconName = iconName,
        tint = tint,
        Default = {
            Image(
                modifier = modifier,
                painter = painterResource(id = defaultIcon),
                colorFilter = ColorFilter.tint(tint),
                contentDescription = "item icon"
            )
        }
    )
}

@Deprecated("Old design system. Use `:ivy-design` and Material3")
@Composable
fun ItemIconM(
    modifier: Modifier = Modifier,
    iconName: String?,
    tint: Color = UI.colors.pureInverse,
    iconContentScale: ContentScale? = null,
    Default: (@Composable () -> Unit)? = null
) {
    ItemIcon(
        modifier = modifier
            .size(48.dp),
        size = "m",
        iconName = iconName,
        tint = tint,
        iconContentScale = iconContentScale,
        Default = Default
    )
}

@Deprecated("Old design system. Use `:ivy-design` and Material3")
@Composable
fun ItemIconSDefaultIcon(
    modifier: Modifier = Modifier,
    iconName: String?,
    tint: Color = UI.colors.pureInverse,
    @DrawableRes defaultIcon: Int
) {
    ItemIconS(
        modifier = modifier,
        iconName = iconName,
        tint = tint,
        Default = {
            Image(
                modifier = modifier,
                painter = painterResource(id = defaultIcon),
                colorFilter = ColorFilter.tint(tint),
                contentDescription = "item icon"
            )
        }
    )
}

@Deprecated("Old design system. Use `:ivy-design` and Material3")
@Composable
fun ItemIconS(
    modifier: Modifier = Modifier,
    iconName: String?,
    tint: Color = UI.colors.pureInverse,
    iconContentScale: ContentScale? = null,
    Default: (@Composable () -> Unit)? = null
) {
    ItemIcon(
        modifier = modifier
            .size(32.dp),
        size = "s",
        iconName = iconName,
        tint = tint,
        iconContentScale = iconContentScale,
        Default = Default
    )
}

@Deprecated("Old design system. Use `:ivy-design` and Material3")
@Composable
private fun ItemIcon(
    modifier: Modifier = Modifier,
    iconName: String?,
    size: String,
    tint: Color = UI.colors.pureInverse,
    iconContentScale: ContentScale? = null,
    Default: (@Composable () -> Unit)? = null
) {
    val context = LocalContext.current
    val iconInfo = getCustomIconId(
        context = context,
        iconName = iconName,
        size = size
    )

    if (iconInfo != null) {
        Image(
            modifier = modifier
                .thenWhen {
                    if (!iconInfo.newFormat) {
                        // do nothing for the old format of icons
                        return@thenWhen this
                    }

                    when (iconInfo.style) {
                        IconStyle.L ->
                            // 64.dp - 48.dp = 16.dp / 4 = 4.dp
                            this.padding(all = 4.dp)
                        IconStyle.M ->
                            // 48.dp - 32.dp = 16.dp / 4 = 4.dp
                            this.padding(all = 4.dp)
                        IconStyle.S ->
                            // 32.dp - 24.dp = 8.dp / 4 = 2.dp
                            // 2.dp is too small padding
                            this.padding(all = 4.dp)
                        IconStyle.UNKNOWN -> this
                    }
                },
            painter = painterResource(id = iconInfo.iconId),
            colorFilter = ColorFilter.tint(tint),
            alignment = Alignment.Center,
            contentScale = iconContentScale ?: if (iconInfo.newFormat) {
                ContentScale.Fit
            } else {
                ContentScale.None
            },
            contentDescription = iconName ?: "item icon"
        )
    } else {
        Default?.invoke()
    }
}

@Deprecated("Old design system. Use `:ivy-design` and Material3")
@DrawableRes
@Composable
fun getCustomIconIdS(
    iconName: String?,
    @DrawableRes defaultIcon: Int
): Int {
    val context = LocalContext.current
    return getCustomIconId(
        context = context,
        iconName = iconName,
        size = "s"
    )?.iconId ?: defaultIcon
}

@Deprecated("Old design system. Use `:ivy-design` and Material3")
fun getCustomIconId(
    context: Context,
    iconName: String?,
    size: String,
): IconInfo? {
    val iconStyle = when (size) {
        "l" -> IconStyle.L
        "m" -> IconStyle.M
        "s" -> IconStyle.S
        else -> IconStyle.UNKNOWN
    }

    return iconName?.let {
        try {
            val iconNameNormalized = iconName
                .replace(" ", "")
                .trim()
                .toLowerCaseLocal()

            val itemId = context.resources.getIdentifier(
                "ic_custom_${iconNameNormalized}_$size",
                "drawable",
                context.packageName
            ).takeIf { it != 0 }

            itemId?.let { nonNullId ->
                IconInfo(
                    iconId = nonNullId,
                    style = iconStyle,
                    newFormat = false
                )
            } ?: fallbackToNewIconFormat(
                context = context,
                iconName = iconName,
                iconStyle = iconStyle
            )
        } catch (e: Exception) {
            fallbackToNewIconFormat(
                context = context,
                iconName = iconName,
                iconStyle = iconStyle
            )
        }
    }
}

@Deprecated("Old design system. Use `:ivy-design` and Material3")
data class IconInfo(
    @DrawableRes
    val iconId: Int,
    val style: IconStyle,
    val newFormat: Boolean
)

@Deprecated("Old design system. Use `:ivy-design` and Material3")
enum class IconStyle {
    L, M, S, UNKNOWN
}

@Deprecated("Old design system. Use `:ivy-design` and Material3")
fun fallbackToNewIconFormat(
    iconStyle: IconStyle,
    context: Context,
    iconName: String?,
): IconInfo? {
    return iconName?.let {
        try {
            val iconNameNormalized = iconName
                .replace(" ", "")
                .trim()
                .toLowerCaseLocal()

            val iconId = context.resources.getIdentifier(
                iconNameNormalized,
                "drawable",
                context.packageName
            ).takeIf { it != 0 }

            iconId?.let { nonNullId ->
                IconInfo(
                    iconId = nonNullId,
                    style = iconStyle,
                    newFormat = true
                )
            }
        } catch (e: Exception) {
            null
        }
    }
}

@Preview
@Composable
private fun Preview_L() {
    IvyWalletComponentPreview {
        ItemIconL(iconName = "dna")
    }
}

@Preview
@Composable
private fun Preview_M() {
    IvyWalletComponentPreview {
        ItemIconM(iconName = "document")
    }
}

@Preview
@Composable
private fun Preview_S() {
    IvyWalletComponentPreview {
        ItemIconS(iconName = "fooddrink")
    }
}
