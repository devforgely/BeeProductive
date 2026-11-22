package com.devforgely.beeproductive.ui.common

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import com.devforgely.beeproductive.R
import com.devforgely.beeproductive.domain.model.LifeArea

data class AreaVisual(
    @param:DrawableRes val icon: Int,
    val color: Color,
)

fun areaVisual(area: LifeArea): AreaVisual = when (area) {
    LifeArea.HEALTH_ENERGY -> AreaVisual(R.drawable.ic_health_energy_lightning, Color(0xFFE36B6B))
    LifeArea.MIND_FOCUS -> AreaVisual(R.drawable.ic_mind_focus_brain, Color(0xFF8B7CDB))
    LifeArea.WORK_GOALS -> AreaVisual(R.drawable.ic_work_goals_briefcase, Color(0xFF5B8DEF))
    LifeArea.CREATIVITY -> AreaVisual(R.drawable.ic_creativity_palette, Color(0xFF6FBF73))
    LifeArea.REST -> AreaVisual(R.drawable.ic_rest_moon_star, Color(0xFF6C7BD6))
}
