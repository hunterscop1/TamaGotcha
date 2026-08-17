package com.example.api.gemini

import com.example.data.local.PetStateEntity
import com.example.data.local.TaskItemEntity
import com.example.data.model.PetMood
import com.example.data.model.PetPersonality
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DialogueEngine {

    suspend fun generateDeadlineReminder(
        pet: PetStateEntity,
        task: TaskItemEntity,
        minutesUntilDue: Long?
    ): String {
        val timeDesc = when {
            minutesUntilDue == null -> "soon"
            minutesUntilDue < 0 -> "OVERDUE by ${-minutesUntilDue} minutes"
            minutesUntilDue < 60 -> "in $minutesUntilDue minutes"
            minutesUntilDue < 1440 -> "in ${minutesUntilDue / 60} hours"
            else -> "in ${minutesUntilDue / 1440} days"
        }

        val mood = pet.getComputedMood(hasUrgentTask = true)

        val prompt = """
            You are ${pet.name}, a virtual pet (${pet.species.displayName}) with personality: ${pet.personality.displayName}.
            Current Mood: ${mood.displayName} (${mood.emoji})
            Stats: Hunger ${pet.hunger.toInt()}%, Energy ${pet.energy.toInt()}%, Happiness ${pet.happiness.toInt()}%, Focus ${pet.focus.toInt()}%.
            
            Give the user a short (1-2 sentences), highly characteristic in-game dialogue reminding them about their upcoming task/deadline:
            Task: "${task.title}"
            Due: $timeDesc
            Priority: ${task.priority.name}
            Source: ${task.source.name}
            ${if (task.notes.isNotBlank()) "Notes: ${task.notes}" else ""}
            
            Match your personality tone (${pet.personality.description}). Stay strictly in character!
        """.trimIndent()

        val systemInstruction = "${pet.personality.tonePrompt} You live on the user's Tamagotchi screen."

        val result = GeminiClient.generateDialogue(systemInstruction, prompt)
        return result.getOrElse {
            fallbackDeadlineReminder(pet, task, timeDesc)
        }
    }

    suspend fun generatePetChatResponse(
        pet: PetStateEntity,
        userMessage: String,
        upcomingTasks: List<TaskItemEntity>
    ): String {
        val tasksContext = if (upcomingTasks.isEmpty()) {
            "No urgent tasks right now."
        } else {
            "Upcoming tasks: " + upcomingTasks.take(3).joinToString("; ") {
                "${it.title} (due: ${formatDueTime(it.dueTimestamp)})"
            }
        }

        val mood = pet.getComputedMood(hasUrgentTask = upcomingTasks.isNotEmpty())

        val prompt = """
            User says to you: "$userMessage"
            
            Your state:
            - Pet Name: ${pet.name}
            - Species: ${pet.species.displayName}
            - Personality: ${pet.personality.displayName}
            - Current Mood: ${mood.displayName} ${mood.emoji}
            - Hunger: ${pet.hunger.toInt()}%
            - Happiness: ${pet.happiness.toInt()}%
            - Energy: ${pet.energy.toInt()}%
            - Focus: ${pet.focus.toInt()}%
            - Level: ${pet.level}
            - $tasksContext
            
            Respond in 1-3 sentences as the Tamagotchi pet directly. Be characteristic of your personality (${pet.personality.displayName}).
        """.trimIndent()

        val systemInstruction = "${pet.personality.tonePrompt} You are a pocket virtual pet living on the user's device. Stay immersed and charming!"

        val result = GeminiClient.generateDialogue(systemInstruction, prompt)
        return result.getOrElse {
            fallbackChatResponse(pet, userMessage)
        }
    }

    suspend fun generateIdleThought(
        pet: PetStateEntity,
        topTask: TaskItemEntity?
    ): String {
        val taskClause = if (topTask != null) {
            "User's next priority: '${topTask.title}' due ${formatDueTime(topTask.dueTimestamp)}."
        } else {
            "User has a clear schedule!"
        }

        val mood = pet.getComputedMood(hasUrgentTask = topTask != null && (topTask.dueTimestamp ?: 0) < System.currentTimeMillis() + 7200000)

        val prompt = """
            Generate a single short, cute in-game speech bubble dialogue (max 20 words) for when the pet is hanging out on screen.
            $taskClause
            Mood: ${mood.displayName} ${mood.emoji}
            Hunger: ${pet.hunger.toInt()}%, Happiness: ${pet.happiness.toInt()}%, Energy: ${pet.energy.toInt()}%.
        """.trimIndent()

        val systemInstruction = "${pet.personality.tonePrompt} Keep it very brief and cute like an authentic Tamagotchi bubble."
        val result = GeminiClient.generateDialogue(systemInstruction, prompt)
        return result.getOrElse {
            fallbackIdleThought(pet, topTask)
        }
    }

    fun fallbackDeadlineReminder(pet: PetStateEntity, task: TaskItemEntity, timeDesc: String): String {
        return when (pet.personality) {
            PetPersonality.CHEERFUL -> "Poyo! Just a sweet reminder that '${task.title}' is due $timeDesc! You've got this, superstar! (✿◠‿◠)✨"
            PetPersonality.GRUMPY -> "Hmph! Put down the snacks! '${task.title}' is due $timeDesc! Don't make me nag you! (¬_¬)⚡"
            PetPersonality.CURIOUS -> "Ooh! Did you know '${task.title}' is scheduled for $timeDesc? How are we tackling it? 🔍✨"
            PetPersonality.SHY -> "U-um... please don't forget '${task.title}' is due $timeDesc... I-I'm cheering for you quietly! (⁄ ⁄•⁄ω⁄•⁄ ⁄)🌸"
            PetPersonality.SCHOLAR -> "Efficiency alert: '${task.title}' is due $timeDesc. Initiating focus protocol immediately. 👓📊"
            PetPersonality.ZEN_SAGE -> "*Breathes in harmony* '${task.title}' approaches $timeDesc. Stay calm, centered, and execute step by step. 🍵🌿"
        }
    }

    fun fallbackChatResponse(pet: PetStateEntity, message: String): String {
        val lower = message.lowercase()
        val mood = pet.getComputedMood()

        if (mood == PetMood.HUNGRY) {
            return when (pet.personality) {
                PetPersonality.CHEERFUL -> "*Tummy rumbles cutely* My tummy is squeaking for a snack! Can we visit the kitchen? 🍙🍓"
                PetPersonality.GRUMPY -> "I can't focus on chatting when I'm starving! Feed me already! (⇀‸↼‶)"
                PetPersonality.CURIOUS -> "Hmm, did you know pets run on delicious calories? Speaking of which... feed me? 🥞🧐"
                PetPersonality.SHY -> "U-um... my tummy just made an embarrassing sound... could I have a small treat? (,,•﹏•,,)"
                PetPersonality.SCHOLAR -> "Glucose and energy levels are critically low (<35%). Nutrition input required for optimal brain output. 🥪"
                PetPersonality.ZEN_SAGE -> "Even the strongest willow needs water and nourishment. A mindful snack would restore balance. 🍵"
            }
        }

        if (mood == PetMood.TIRED) {
            return when (pet.personality) {
                PetPersonality.CHEERFUL -> "*Yawns with little sparkles* So sleepy... a cozy power nap would give me super zoomies later! (∪｡∪)｡｡zZZ"
                PetPersonality.GRUMPY -> "I'm exhausted. Don't look at me like that, even hard workers need sleep! (¬_¬)💤"
                PetPersonality.CURIOUS -> "Fascinating how sleep consolidates memory... I think my brain needs a quick recharge nap! 😴"
                PetPersonality.SHY -> "*Droopy eyes* I-is it okay if I rest my eyes for a tiny bit...? Zzzz... (｡-ω-)｡o○"
                PetPersonality.SCHOLAR -> "Circadian rhythms suggest high fatigue. Initiating 20-minute rest cycle to restore cognitive stamina. 💤"
                PetPersonality.ZEN_SAGE -> "Rest is not idleness; it is restoring the spirit. Close your eyes and breathe gently. 🌙"
            }
        }

        return when {
            "hello" in lower || "hi" in lower || "hey" in lower -> {
                when (pet.personality) {
                    PetPersonality.CHEERFUL -> "Yay! Hello! *wiggles happily* It makes my heart shine when you visit me! (｡♥‿♥｡)✨"
                    PetPersonality.GRUMPY -> "Oh, it's you. Don't expect me to jump for joy... okay fine, hi. (¬‿¬)"
                    PetPersonality.CURIOUS -> "Greetings! What exciting discoveries or tasks are we uncovering today? 🔍"
                    PetPersonality.SHY -> "H-hello there... I'm really happy whenever you stop by to say hi... (✿◠‿◠)"
                    PetPersonality.SCHOLAR -> "Salutations! Productivity systems are operational. What is our agenda? 📋"
                    PetPersonality.ZEN_SAGE -> "Peace and welcome to this tranquil moment. I hope your heart is at ease. 🌿"
                }
            }
            "food" in lower || "feed" in lower || "hungry" in lower -> {
                "Ooooh did someone say snacks?! Let's check the Kitchen for yummy treats! 🍙🥞🍓"
            }
            "task" in lower || "deadline" in lower || "work" in lower || "study" in lower -> {
                when (pet.personality) {
                    PetPersonality.CHEERFUL -> "Every task you finish gives me EXP and shiny coins! Let's conquer today! ٩(ˊᗜˋ*)و"
                    PetPersonality.GRUMPY -> "Get to work already! The sooner you finish, the sooner we can relax! (・`ω´・)"
                    PetPersonality.CURIOUS -> "I love seeing tasks marked complete! It feels like solving great puzzles! ✨"
                    PetPersonality.SHY -> "I believe in you so much... one little task at a time! (っ˘ω˘ς)"
                    PetPersonality.SCHOLAR -> "Executing planned task milestones yields maximum dopamine and goal attainment. Let's execute! 📊"
                    PetPersonality.ZEN_SAGE -> "A journey of a thousand miles begins with a single focused step. Breathe and begin. 🎋"
                }
            }
            else -> {
                when (pet.personality) {
                    PetPersonality.CHEERFUL -> "Hehe! I love chatting with you! You make every day super bright! (★ω★)"
                    PetPersonality.GRUMPY -> "Hmph, interesting thought! But are your tasks finished yet? Hehe! (¬‿¬)"
                    PetPersonality.CURIOUS -> "That's intriguing! Let me ponder that while keeping an eye on our schedule! 💡"
                    PetPersonality.SHY -> "Thank you for sharing that with me... I always feel safe with you. (⁄ ⁄•⁄ω⁄•⁄ ⁄)"
                    PetPersonality.SCHOLAR -> "Noted and filed under important collaborative discussions. Ready for next action! 📝"
                    PetPersonality.ZEN_SAGE -> "Every conversation with you brings harmony and stillness to my digital heart. 🍵"
                }
            }
        }
    }

    fun fallbackIdleThought(pet: PetStateEntity, task: TaskItemEntity?): String {
        val mood = pet.getComputedMood()
        return when (mood) {
            PetMood.HUNGRY -> {
                when (pet.personality) {
                    PetPersonality.CHEERFUL -> "*Tummy rumbles* Spare a strawberry for your sweet pet? (｡•́︿•̀｡)🍓"
                    PetPersonality.GRUMPY -> "My stomach is complaining and it's your fault! Kitchen time! (⇀‸↼‶)"
                    PetPersonality.SHY -> "U-um... getting a tiny bit hungry... 🥺🍙"
                    else -> "Hunger is low! A quick snack would restore stamina. 🥪"
                }
            }
            PetMood.TIRED -> {
                when (pet.personality) {
                    PetPersonality.CHEERFUL -> "*Yawns softly* Power naps make champions! (∪｡∪)｡｡zZZ"
                    PetPersonality.GRUMPY -> "Can I nap yet? I'm not a machine, you know... 💤"
                    else -> "Energy is low... time for a rest! 🌙"
                }
            }
            PetMood.SAD -> {
                when (pet.personality) {
                    PetPersonality.CHEERFUL -> "*Pokes screen* Can we play a mini-game or tap for head pats? (◕‿◕✿)"
                    PetPersonality.SHY -> "Feeling a little lonely... hug? (っ˘̩╭╮˘̩)っ"
                    else -> "Happiness is low. Let's play a mini-game to cheer up! 🎮"
                }
            }
            PetMood.ECSTATIC -> {
                when (pet.personality) {
                    PetPersonality.CHEERFUL -> "YAY! Today is amazing! We are unstoppable! (★ω★)💖✨"
                    PetPersonality.GRUMPY -> "Fine, I admit it... today has been pretty great. Don't get used to it! (⁄ ⁄>⁄ ▽ ⁄<⁄ ⁄)"
                    else -> "Peak happiness reached! All systems humming with joy! 🌟"
                }
            }
            PetMood.FOCUS_MODE -> {
                when (pet.personality) {
                    PetPersonality.SCHOLAR -> "Hyper-focus zone active. Productivity coefficient: 100%! 🎯📈"
                    else -> "In the study flow! Zero distractions! ( •̀ᴗ•́ )و"
                }
            }
            PetMood.STRESSED -> {
                "⚠️ Upcoming deadline alert! Let's conquer it together right now! ⚔️"
            }
            else -> {
                if (task != null) "Next goal on our radar: '${task.title}'! ( •̀ᴗ•́ )و"
                else "Chilling in our cozy digital world! All goals in harmony! (◡‿◡✿)✨"
            }
        }
    }

    private fun formatDueTime(timestamp: Long?): String {
        if (timestamp == null) return "soon"
        val sdf = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}
