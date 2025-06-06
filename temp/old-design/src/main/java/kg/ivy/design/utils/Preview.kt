package kg.ivy.design.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import kg.ivy.base.resource.AndroidResourceProvider
import kg.ivy.base.legacy.Theme
import kg.ivy.base.time.impl.DeviceTimeProvider
import kg.ivy.base.time.impl.StandardTimeConverter
import kg.ivy.design.IvyContext
import kg.ivy.design.api.IvyDesign
import kg.ivy.design.api.IvyUI
import kg.ivy.design.api.systems.IvyWalletDesign
import kg.ivy.design.l0_system.UI
import kg.ivy.ui.time.impl.AndroidDevicePreferences
import kg.ivy.ui.time.impl.IvyTimeFormatter

@Deprecated("Old design system. Use `:ivy-design` and Material3")
@Composable
fun IvyComponentPreview(
    modifier: Modifier = Modifier,
    design: IvyDesign = defaultDesign(),
    theme: Theme = Theme.LIGHT,
    content: @Composable BoxScope.() -> Unit
) {
    IvyPreview(
        design = design,
        theme = theme
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(UI.colors.pure),
            contentAlignment = Alignment.Center
        ) {
            content()
        }
    }
}

@Deprecated("Old design system. Use `:ivy-design` and Material3")
@Composable
fun IvyPreview(
    design: IvyDesign,
    theme: Theme = Theme.LIGHT,
    content: @Composable BoxWithConstraintsScope.() -> Unit
) {
    design.context().switchTheme(theme = theme)
    val timeProvider = DeviceTimeProvider()
    val timeConverter = StandardTimeConverter(timeProvider)
    IvyUI(
        design = design,
        content = content,
        timeConverter = timeConverter,
        timeProvider = timeProvider,
        timeFormatter = IvyTimeFormatter(
            resourceProvider = AndroidResourceProvider(LocalContext.current),
            timeProvider = timeProvider,
            converter = timeConverter,
            devicePreferences = AndroidDevicePreferences(LocalContext.current)
        )
    )
}

@Deprecated("Old design system. Use `:ivy-design` and Material3")
fun defaultDesign(): IvyDesign = object : IvyWalletDesign() {
    override fun context(): IvyContext = object : IvyContext() {
    }
}
