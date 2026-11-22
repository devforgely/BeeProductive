package com.devforgely.beeproductive.domain.model

data class ForageScript(
    val kind: ForageRewardKind,
    val title: String,
    val body: String,
    val grantsCosmetic: Boolean = false,
)

object HiveCopy {
    fun greeting(
        beeName: String,
        personality: BeePersonality,
        mood: Mood?,
        streak: Int,
    ): String {
        val name = beeName.ifBlank { "your bee" }
        if (mood == Mood.LOW) {
            return when (personality) {
                BeePersonality.CALM -> "Soft morning. $name is hovering close. Small is enough."
                BeePersonality.DILIGENT -> "$name is waiting quietly. One careful action will do."
                BeePersonality.PLAYFUL -> "$name is tucked in, still smiling. A tiny flight counts."
                BeePersonality.CURIOUS -> "$name wonders what the smallest sweet thing could be today."
                BeePersonality.COMPASSIONATE -> "$name stays close. One kind action is a full flight."
                BeePersonality.CONFIDENT -> "$name is ready when you are. Start with one sure step."
            }
        }
        if (streak <= 0) {
            return when (personality) {
                BeePersonality.CURIOUS -> "The meadow is quiet and ready. $name wants to see the first flower."
                BeePersonality.PLAYFUL -> "$name is doing a tiny hop. The hive starts whenever you do."
                BeePersonality.CALM -> "No rush in the grass. $name is content to begin again."
                BeePersonality.DILIGENT -> "A fresh comb is open. $name is ready for one honest action."
                BeePersonality.COMPASSIONATE -> "$name saved you a soft landing. Begin whenever it feels kind."
                BeePersonality.CONFIDENT -> "$name faces the meadow. The first flower is already enough."
            }
        }
        return when (personality) {
            BeePersonality.DILIGENT -> "Day is open, $name. $streak careful days are already in the wax."
            BeePersonality.PLAYFUL -> "$name spins once for a $streak-day glow. Want to fill another cell?"
            BeePersonality.CALM -> "$name hums over $streak steady days. The comb can take its time."
            BeePersonality.CURIOUS -> "$name has been out for $streak days and still sniffs the wind."
            BeePersonality.COMPASSIONATE -> "$name is proud of $streak gentle days. Stay as kind as you began."
            BeePersonality.CONFIDENT -> "$streak days in the comb. $name knows you can add one more."
        }
    }

    fun clip(personality: BeePersonality, mood: Mood?, celebrating: Boolean, foraging: Boolean): BeeClip {
        if (foraging) return BeeClip.FORAGE
        if (celebrating || mood == Mood.BRIGHT && personality == BeePersonality.PLAYFUL) {
            return BeeClip.CELEBRATE
        }
        return when {
            personality == BeePersonality.PLAYFUL -> BeeClip.FLUTTER
            personality == BeePersonality.CALM -> BeeClip.IDLE
            mood == Mood.BRIGHT -> BeeClip.FLUTTER
            mood == Mood.LOW -> BeeClip.IDLE
            personality == BeePersonality.CURIOUS -> BeeClip.FLUTTER
            else -> BeeClip.IDLE
        }
    }

    fun speed(personality: BeePersonality, mood: Mood?): Float = when {
        mood == Mood.LOW || personality == BeePersonality.CALM -> 0.75f
        personality == BeePersonality.PLAYFUL || mood == Mood.BRIGHT -> 1.25f
        else -> 1f
    }

    val basicForages: List<ForageScript> = listOf(
        ForageScript(
            ForageRewardKind.INSIGHT,
            "A short flight",
            "The sweetest honey starts as a single visit. You do not have to finish the meadow today.",
        ),
        ForageScript(
            ForageRewardKind.INSIGHT,
            "Warm wax",
            "Consistency is just returning while the light is still ordinary.",
        ),
        ForageScript(
            ForageRewardKind.STORY,
            "The clover path",
            "Your bee followed a clover path, got briefly lost, and came home anyway. That counted.",
        ),
        ForageScript(
            ForageRewardKind.COSMETIC,
            "A lantern seed",
            "Tucked in the pollen was a tiny lantern for the hive.",
            grantsCosmetic = true,
        ),
    )

    val plusForages: List<ForageScript> = listOf(
        ForageScript(
            ForageRewardKind.STORY,
            "The far blossom",
            "Past the usual flowers, the wind opened a rarer blossom. Longer flights still begin with one wingbeat.",
        ),
        ForageScript(
            ForageRewardKind.INSIGHT,
            "Cap of gold",
            "Bees seal a full cell so the honey keeps. You are allowed to protect what is already working.",
        ),
    )

    fun forageScript(daySeed: Int): ForageScript {
        val pool = basicForages + plusForages
        return pool[daySeed.mod(pool.size)]
    }

    fun phaseReflection(dayMark: Int): String = when (dayMark) {
        30 -> "The first comb is in place. What helped you return?"
        else -> "The honey is moving. What do you want more of in the next thirty days?"
    }
}
