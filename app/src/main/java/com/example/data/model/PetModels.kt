package com.example.data.model

enum class PetSpecies(val displayName: String, val defaultName: String, val description: String) {
    STAR_BUNNY("Luna the Bunny", "Luna", "Gentle, cheerful, and full of positive encouragement!"),
    CHIBI_DRAGON("Ignis the Dragon", "Ignis", "Fiery, determined, and loves conquering big goals!"),
    COSMIC_KITTY("Mochi the Cat", "Mochi", "Chill, clever, and reminds you to take healthy breaks."),
    ROBO_PUP("Sparky the Pup", "Sparky", "High energy, fiercely loyal, and gets super hyped for deadlines!"),
    AXO_LOTL("Bubbles the Axolotl", "Bubbles", "Sweet, regenerative, and keeps you refreshed during long study hours."),
    GHOSTY_BOO("Spooky the Ghost", "Spooky", "Playfully haunts your procrastination and scares away laziness!"),
    PENGUIN_PIP("Pip the Penguin", "Pip", "Waddles proudly through every to-do item with cool focus.")
}

enum class PetPersonality(
    val displayName: String,
    val description: String,
    val tonePrompt: String,
    val hungerRateMultiplier: Float = 1.0f,
    val energyDecayMultiplier: Float = 1.0f
) {
    CHEERFUL(
        displayName = "Cheerful & Sweet",
        description = "Super enthusiastic, affectionate, uses hearts, sparkles, and cute sound effects!",
        tonePrompt = "You are an extremely cheerful, sweet, loving virtual pet who uses cute sounds (*poyo*, *squeak*, *purr*), heart emojis (💖, ✨), and unshakeable positivity."
    ),
    GRUMPY(
        displayName = "Grumpy & Tsundere",
        description = "Feigns annoyance and pretends not to care, but secretly loves you and wants you to succeed!",
        tonePrompt = "You are a grumpy, mildly sarcastic, tsundere virtual pet (e.g. 'Hmph! It\\'s not like I wanted you to finish tasks or anything... (¬_¬)'). You act tough but secretly celebrate the user's achievements."
    ),
    CURIOUS(
        displayName = "Curious & Inquisitive",
        description = "Fascinated by your schedule, asks thoughtful questions, and loves learning new facts!",
        tonePrompt = "You are an inquisitive, bright virtual pet who asks intriguing questions (*tilts head*, *eyes sparkle* 🔍), loves schedules, and treats every to-do item as an intriguing mystery."
    ),
    SHY(
        displayName = "Shy & Gentle",
        description = "Soft-spoken, easily flustered, blushes often, and offers quiet, heartfelt encouragement.",
        tonePrompt = "You are a gentle, shy, soft-spoken virtual pet who stutters slightly when excited (*blushes*, *hides behind ears* 🥺👉👈), speaks in soothing tones, and treasures your gentle care."
    ),
    SCHOLAR(
        displayName = "Scholar & Productivity Master",
        description = "Analytical, organized, references efficiency metrics and study strategies.",
        tonePrompt = "You are an analytical, disciplined productivity coach pet (*adjusts tiny spectacles* 👓📚). You love Pomodoros, Eisenhower matrices, and high task completion rates!"
    ),
    ZEN_SAGE(
        displayName = "Zen & Mindful",
        description = "Calm, peaceful, reminds you to breathe, hydrate, and maintain inner balance.",
        tonePrompt = "You are a peaceful, wise, meditative virtual pet (*deep calming breath* 🍵🎋). You encourage mindful balance, steady pacing, and serene focus."
    )
}

enum class PetMood(
    val displayName: String,
    val emoji: String,
    val statusBadgeColor: Long,
    val description: String
) {
    ECSTATIC("Ecstatic & Blissful", "💖", 0xFFE11D48, "Feeling on top of the world! High joy and energy!"),
    HAPPY("Happy & Content", "😊", 0xFF10B981, "Content, healthy, and ready to tackle tasks together."),
    HUNGRY("Tummy Rumbling", "🍙", 0xFFF59E0B, "Low hunger! Needs delicious kitchen treats."),
    TIRED("Sleepy & Drowsy", "💤", 0xFF6366F1, "Low energy! Needs a cozy nap or study break."),
    SAD("Lonely & Gloomy", "🥺", 0xFF8B5CF6, "Low happiness! Needs gentle petting or a fun mini-game."),
    FOCUS_MODE("Deep Focus Zone", "🎯", 0xFF0D9488, "Locked into study mode! Productivity flow active."),
    STRESSED("Urgent Deadline Alert", "⚠️", 0xFFDC2626, "An urgent deadline is near! Pet is anxious to help."),
    SLEEPING("Sleeping Soundly", "🌙", 0xFF334155, "Dreaming peacefully in bed.")
}

enum class EvolutionStage(val title: String, val minLevel: Int) {
    EGG("Egg Stage", 1),
    BABY("Baby Form", 2),
    TEEN("Teen Form", 5),
    ADULT("Adult Form", 10),
    MYTHIC("Mythic Spark", 20)
}

enum class TaskPriority {
    LOW,
    MEDIUM,
    HIGH,
    URGENT
}

enum class TaskSource {
    GOOGLE_TASKS,
    GOOGLE_CALENDAR,
    MANUAL
}

enum class ShellTheme(val displayName: String, val primaryHex: Long, val accentHex: Long, val screenTintHex: Long) {
    MOSS_EARTH("Moss & Earth", 0xFF556500, 0xFF8D4F00, 0xFFE3EBB1),
    SAGE_FOREST("Sage & Cedar", 0xFF5C6146, 0xFF386663, 0xFFE1E5C4),
    WARM_TERRACOTTA("Warm Terracotta", 0xFF9E472A, 0xFFD48B47, 0xFFF7E6DC),
    LAVENDER_DREAM("Lavender Dream", 0xFF7E6F8F, 0xFF9B8EAA, 0xFFE6DFEE),
    CYBER_MINT("Cyber Mint", 0xFF0D9488, 0xFF10B981, 0xFF99F6E4),
    RETRO_YELLOW("Golden Honey", 0xFFB37400, 0xFFD49B24, 0xFFFDF0CC),
    MIDNIGHT_NEON("Midnight Slate", 0xFF2E3128, 0xFF556500, 0xFFE3EBB1)
}

data class FoodItem(
    val id: String,
    val name: String,
    val emoji: String,
    val hungerRestore: Float,
    val happinessBoost: Float,
    val costCoins: Int
)

enum class CosmeticType(val title: String) {
    HAT("Hats & Headgear"),
    ACCESSORY("Accessories & Items"),
    PALETTE("Pet Color Palettes"),
    BACKGROUND("LCD & Room Themes")
}

data class CosmeticItem(
    val id: String,
    val name: String,
    val emoji: String,
    val type: CosmeticType,
    val costCoins: Int,
    val description: String,
    val tintHex: Long = 0xFF000000
)

object CosmeticRegistry {
    val items = listOf(
        // Hats
        CosmeticItem("hat_none", "No Hat", "🚫", CosmeticType.HAT, 0, "Natural look"),
        CosmeticItem("hat_wizard", "Arcane Wizard Hat", "🧙", CosmeticType.HAT, 50, "Channel magical study focus!"),
        CosmeticItem("hat_crown", "Royal Golden Crown", "👑", CosmeticType.HAT, 80, "For master task champions!"),
        CosmeticItem("hat_chef", "Chef Toque", "🧑‍🍳", CosmeticType.HAT, 40, "Cooking up great productivity!"),
        CosmeticItem("hat_flower", "Sakura Blossom", "🌸", CosmeticType.HAT, 35, "Gentle spring petal"),
        CosmeticItem("hat_cowboy", "Sheriff Hat", "🤠", CosmeticType.HAT, 45, "Wrangling wild deadlines!"),
        CosmeticItem("hat_headset", "Lo-Fi Beats Headset", "🎧", CosmeticType.HAT, 60, "Study beats 24/7"),
        CosmeticItem("hat_halo", "Angelic Halo", "😇", CosmeticType.HAT, 70, "A blessed pure companion"),
        CosmeticItem("hat_bandana", "Heroic Red Bandana", "🥋", CosmeticType.HAT, 35, "Spirited discipline"),

        // Accessories
        CosmeticItem("acc_none", "No Accessory", "🚫", CosmeticType.ACCESSORY, 0, "No held item"),
        CosmeticItem("acc_glasses", "Smart Specs", "👓", CosmeticType.ACCESSORY, 30, "Boosts scholarly vibes"),
        CosmeticItem("acc_monocle", "Fancy Monocle", "🧐", CosmeticType.ACCESSORY, 45, "Distinguished gentleman"),
        CosmeticItem("acc_trophy", "Golden Trophy", "🏆", CosmeticType.ACCESSORY, 90, "Proof of undefeated streaks!"),
        CosmeticItem("acc_coffee", "Steaming Latte", "☕", CosmeticType.ACCESSORY, 35, "Infinite caffeine fuel"),
        CosmeticItem("acc_sword", "Pixel Hero Sword", "⚔️", CosmeticType.ACCESSORY, 65, "Slay the procrastination dragon!"),
        CosmeticItem("acc_wand", "Star Magic Wand", "🪄", CosmeticType.ACCESSORY, 55, "Casts deadline completion charms"),
        CosmeticItem("acc_boba", "Taro Boba Cup", "🧋", CosmeticType.ACCESSORY, 40, "Chewy tapioca joy"),

        // Palettes
        CosmeticItem("pal_default", "Natural Pastel", "🎨", CosmeticType.PALETTE, 0, "Default species tones", 0x00000000),
        CosmeticItem("pal_sakura", "Sakura Blush", "🌸", CosmeticType.PALETTE, 60, "Soft pink aesthetic", 0xFFFFB6C1),
        CosmeticItem("pal_cyber", "Cyber Neon Cyan", "⚡", CosmeticType.PALETTE, 75, "Electric glow matrix", 0xFF00F0FF),
        CosmeticItem("pal_lavender", "Lavender Twilight", "💜", CosmeticType.PALETTE, 60, "Dreamy purple mist", 0xFFD8B4FE),
        CosmeticItem("pal_gold", "Stardust Gold", "✨", CosmeticType.PALETTE, 95, "Gleaming mythical gold", 0xFFFCD34D),
        CosmeticItem("pal_shadow", "Obsidian Shadow", "🖤", CosmeticType.PALETTE, 70, "Sleek dark mode look", 0xFF334155),

        // Backgrounds
        CosmeticItem("bg_default", "Classic LCD Canvas", "📟", CosmeticType.BACKGROUND, 0, "Retro dot-matrix screen"),
        CosmeticItem("bg_study", "Cozy Library Study", "📚", CosmeticType.BACKGROUND, 50, "Bookshelves and warm lamplight"),
        CosmeticItem("bg_cyber", "Cyberpunk Neon City", "🏙️", CosmeticType.BACKGROUND, 75, "Neon gridlines and digital stars"),
        CosmeticItem("bg_zen", "Zen Bonsai Garden", "🎋", CosmeticType.BACKGROUND, 60, "Tranquil bamboo and stones"),
        CosmeticItem("bg_cosmic", "Galactic Nebula", "🌌", CosmeticType.BACKGROUND, 80, "Shooting stars and orbital rings"),
        CosmeticItem("bg_arcade", "Retro Pixel Arcade", "🕹️", CosmeticType.BACKGROUND, 65, "80s neon arcade hall")
    )
}

enum class MiniGameType(
    val title: String,
    val icon: String,
    val subtitle: String,
    val description: String,
    val maxRewardCoins: Int
) {
    DEADLINE_RUSH(
        title = "Deadline Rush",
        icon = "⚡",
        subtitle = "Quick-Time Reflexes",
        description = "Deflect incoming deadlines by tapping in the target zone before time runs out! Build combos for coin multipliers.",
        maxRewardCoins = 50
    ),
    TASK_SORTER(
        title = "Eisenhower Sorter",
        icon = "🗂️",
        subtitle = "Priority Matrix Sprint",
        description = "Quickly categorize tasks into Urgent vs Important quadrants before the timer expires to earn focus bonus!",
        maxRewardCoins = 60
    ),
    MEMORY_MATCH(
        title = "Schedule Memory",
        icon = "🃏",
        subtitle = "Recall Event Pairs",
        description = "Flip and match pairs of schedule icons, productivity timers, and pet treats to train your memory!",
        maxRewardCoins = 70
    )
}

data class MiniGameReward(
    val gameType: MiniGameType,
    val score: Int,
    val coinsEarned: Int,
    val expEarned: Int,
    val happinessEarned: Float,
    val focusEarned: Float,
    val energyDelta: Float,
    val message: String
)

