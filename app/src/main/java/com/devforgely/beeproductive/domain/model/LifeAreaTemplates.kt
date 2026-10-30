package com.devforgely.beeproductive.domain.model

data class StateOption(
    val id: String,
    val label: String,
    val tone: String,
)

data class LifeAreaTemplate(
    val area: LifeArea,
    val northStar: String,
    val stateOptions: List<StateOption>,
    val starterHabits: List<String>,
    val elevatedHabits: List<String>,
    val roadmap: Map<CycleStage, List<String>>,
)

object LifeAreaTemplates {
    fun forArea(area: LifeArea): LifeAreaTemplate = all.getValue(area)

    private val all: Map<LifeArea, LifeAreaTemplate> = listOf(
        template(
            area = LifeArea.HEALTH_ENERGY,
            northStar = "Feel steady energy through small daily care",
            states = listOf(
                StateOption("scattered", "Running on fumes", "Start with the gentlest reps."),
                StateOption("uneven", "Good days, flat days", "Protect the rhythm you already have."),
                StateOption("ready", "Ready for a kinder push", "Add one honest challenge."),
            ),
            starters = listOf(
                "Drink a glass of water after waking",
                "Step outside for five minutes",
                "Lights low thirty minutes before bed",
            ),
            elevated = listOf(
                "A twenty-minute easy walk",
                "Cook one nourishing meal",
                "In bed at a steady hour",
            ),
            roadmap = mapOf(
                CycleStage.POLLINATING to listOf("Morning light", "A bedtime cue"),
                CycleStage.NECTAR_FLOW to listOf("Movement most days", "A steadier plate"),
                CycleStage.CAPPING to listOf("Energy feels familiar", "Rest is non-negotiable"),
            ),
        ),
        template(
            area = LifeArea.MIND_FOCUS,
            northStar = "Give my attention a quiet place to land",
            states = listOf(
                StateOption("noisy", "My mind feels noisy", "Shrink the first session."),
                StateOption("avoiding", "I start, then scatter", "Name one next step only."),
                StateOption("ready", "I can hold a short focus", "Lengthen one block."),
            ),
            starters = listOf(
                "Two minutes of still breathing",
                "One focused block before messages",
                "Write tomorrow's first step tonight",
            ),
            elevated = listOf(
                "A twenty-five minute focus block",
                "A phone-free first hour",
                "Close the day with a three-line review",
            ),
            roadmap = mapOf(
                CycleStage.POLLINATING to listOf("Show up to one block", "A tiny shutdown ritual"),
                CycleStage.NECTAR_FLOW to listOf("Protect a deep hour", "Batch the noise"),
                CycleStage.CAPPING to listOf("Focus feels ordinary", "Evenings stay clear"),
            ),
        ),
        template(
            area = LifeArea.WORK_GOALS,
            northStar = "Move one meaningful project forward each day",
            states = listOf(
                StateOption("stuck", "The goal feels far away", "Pick a pebble, not the mountain."),
                StateOption("busy", "Busy, but not on the real thing", "Touch the real work first."),
                StateOption("ready", "I know the next milestone", "Make the milestone visible."),
            ),
            starters = listOf(
                "Ten minutes on the real project",
                "Write the next tiny step",
                "Clear one open loop",
            ),
            elevated = listOf(
                "A daily shippable slice",
                "A weekly milestone review",
                "One brave ask or share",
            ),
            roadmap = mapOf(
                CycleStage.POLLINATING to listOf("Touch the work daily", "Define the first milestone"),
                CycleStage.NECTAR_FLOW to listOf("Finish a mid-size piece", "Share progress once a week"),
                CycleStage.CAPPING to listOf("The project has a rhythm", "The finish line is specific"),
            ),
        ),
        template(
            area = LifeArea.RELATIONSHIPS,
            northStar = "Be someone the people I love can feel",
            states = listOf(
                StateOption("distant", "I've gone quiet", "One small reach-out is enough."),
                StateOption("rushed", "I'm present, but rushed", "Slow down one conversation."),
                StateOption("ready", "I want to be more intentional", "Make care a ritual."),
            ),
            starters = listOf(
                "Send one honest note",
                "Put my phone away for one meal",
                "Thank someone specifically",
            ),
            elevated = listOf(
                "A weekly unrushed conversation",
                "Plan one shared moment",
                "Repair a small tension kindly",
            ),
            roadmap = mapOf(
                CycleStage.POLLINATING to listOf("Daily tiny contact", "Name who matters"),
                CycleStage.NECTAR_FLOW to listOf("Deeper check-ins", "Shared time on the calendar"),
                CycleStage.CAPPING to listOf("Care feels mutual", "Repair is practiced"),
            ),
        ),
        template(
            area = LifeArea.CREATIVITY,
            northStar = "Make something small and real, most days",
            states = listOf(
                StateOption("rusty", "I haven't made anything in a while", "Five minutes counts."),
                StateOption("critical", "I edit before I begin", "Make a messy first mark."),
                StateOption("ready", "I have a project humming", "Give it a daily appointment."),
            ),
            starters = listOf(
                "Make a five-minute mark",
                "Collect one spark in a note",
                "Stop while I still want more",
            ),
            elevated = listOf(
                "A daily making block",
                "Finish one small piece a week",
                "Share something unfinished",
            ),
            roadmap = mapOf(
                CycleStage.POLLINATING to listOf("Reopen the practice", "A home for sparks"),
                CycleStage.NECTAR_FLOW to listOf("A weekly finish", "A braver share"),
                CycleStage.CAPPING to listOf("Making is a habit", "A body of small work"),
            ),
        ),
        template(
            area = LifeArea.REST,
            northStar = "Let rest become part of how I live",
            states = listOf(
                StateOption("wired", "I don't know how to stop", "Build a softer landing."),
                StateOption("guilty", "Rest feels undeserved", "Treat rest as part of the work."),
                StateOption("ready", "I want a kinder pace", "Guard one true pause."),
            ),
            starters = listOf(
                "A ten-minute screen-free pause",
                "One slow cup of something warm",
                "Name one thing I can leave undone",
            ),
            elevated = listOf(
                "A protected evening hour",
                "A weekly half-day of quiet",
                "A morning without rushing",
            ),
            roadmap = mapOf(
                CycleStage.POLLINATING to listOf("A daily pause", "An earlier wind-down"),
                CycleStage.NECTAR_FLOW to listOf("Evenings have a shape", "One longer rest"),
                CycleStage.CAPPING to listOf("Rest is allowed", "The pace feels like mine"),
            ),
        ),
    ).associateBy { it.area }

    private fun template(
        area: LifeArea,
        northStar: String,
        states: List<StateOption>,
        starters: List<String>,
        elevated: List<String>,
        roadmap: Map<CycleStage, List<String>>,
    ) = LifeAreaTemplate(area, northStar, states, starters, elevated, roadmap)
}
