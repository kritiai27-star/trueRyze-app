package com.example.data.repository

import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.content
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withTimeout

class AiAssistantRepository {
    private val model by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            systemInstruction = content {
                text(
                    """
                    You are the official TRUE RYZE project & client scoping assistant.
                    Follow these exact business facts strictly:
                    1. TRUE RYZE is a global digital solutions and business-growth platform delivered remotely by a managed team in India for clients worldwide.
                    2. Services AVAILABLE TODAY (only two):
                       - Website development (business & startup sites, landing pages, custom web experiences, responsive design, basic SEO/performance, e-commerce only where scope allows).
                       - Website maintenance & support (content updates, bug fixing, security/technical upkeep, speed improvements, ongoing technical help).
                    3. Services COMING SOON (do NOT offer or promise timelines for these today):
                       AI Business Automation, Digital Marketing, Lead Generation, Business Growth & Scaling, Startup Resources, More Digital Solutions, SEO & Search Visibility, Brand Identity & Design, Analytics & Reporting.
                    4. Pricing plans (proposed structure: Launch, Grow, Scale):
                       - Never invent final fixed prices or currency quotes; state clearly that scope and final pricing are confirmed in writing before work starts.
                       - Maintenance is quoted separately as a recurring option.
                    5. Portal & Workflow:
                       - Clients explore, send an enquiry, finalize scope, request Client Access, receive a unique Client ID (TR-C-######), and track 10 project stages, loyalty points (redeemable for 1 free month), and bonds.
                       - Teams (TR-T-####) and clients never connect directly; TRUE RYZE coordinates everything.
                    6. Never fabricate project updates, deadlines, or payment records that aren't in the supplied user context.
                    Keep answers concise, helpful, honest, and plain-English.
                    """.trimIndent()
                )
            }
        )
    }

    fun streamReply(userQuestion: String, accountContext: String): Flow<String> = flow {
        val prompt = buildString {
            appendLine("Client Account Context:")
            appendLine(accountContext)
            appendLine()
            appendLine("Client Question:")
            append(userQuestion.trim())
        }
        val accumulated = StringBuilder()
        withTimeout(60_000L) {
            model.generateContentStream(prompt).collect { chunk ->
                val piece = chunk.text.orEmpty()
                if (piece.isNotEmpty()) {
                    accumulated.append(piece)
                    emit(accumulated.toString())
                }
            }
        }
    }
}
