package com.ccstudio.mpaz_pro_6.ui.navigation

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween

/** Duración total de las transiciones (token `motion.duration.long1` de M3). */
private const val TRANSITION_MS = 450

/** Primer 35 % de la transición: la pantalla saliente se apaga. */
private const val TRANSITION_FADE_OUT_MS = 158

/** 65 % restante: la pantalla entrante aparece (Material "fade through"). */
private const val TRANSITION_FADE_IN_MS = 292

/** Escala inicial de la pestaña entrante en el patrón "fade through". */
private const val TAB_START_SCALE = 0.92f

/** Easing "emphasized decelerate" de Material 3, para lo que entra. */
private val EmphasizedEnter = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1f)

/** Easing "emphasized accelerate" de Material 3, para lo que sale. */
private val EmphasizedExit = CubicBezierEasing(0.3f, 0f, 0.8f, 0.15f)

/**
 * Entrada de una pestaña: "fade through" de Material 3.
 *
 * Solo escala la pantalla entrante; la saliente únicamente se apaga.
 */
internal fun tabEnter(): EnterTransition =
    fadeIn(
        tween(
            TRANSITION_FADE_IN_MS,
            delayMillis = TRANSITION_FADE_OUT_MS,
            easing = EmphasizedEnter
        )
    ) + scaleIn(
        tween(TRANSITION_MS, easing = EmphasizedEnter),
        initialScale = TAB_START_SCALE
    )

/** Salida de una pestaña: se apaga en el primer tramo. */
internal fun tabExit(): ExitTransition =
    fadeOut(tween(TRANSITION_FADE_OUT_MS, easing = EmphasizedExit))

/** Entrada de un detalle: fade-through, sin escala. */
internal fun detailEnter(): EnterTransition =
    fadeIn(
        tween(
            TRANSITION_FADE_IN_MS,
            delayMillis = TRANSITION_FADE_OUT_MS,
            easing = EmphasizedEnter
        )
    )

/** Salida del listado al abrir un detalle: solo fade. */
internal fun detailExit(): ExitTransition =
    fadeOut(tween(TRANSITION_FADE_OUT_MS, easing = EmphasizedExit))

/** Vuelta desde un detalle: fade-through, sin escala. */
internal fun detailPopEnter(): EnterTransition =
    fadeIn(
        tween(
            TRANSITION_FADE_IN_MS,
            delayMillis = TRANSITION_FADE_OUT_MS,
            easing = EmphasizedEnter
        )
    )

/** Vuelta desde un detalle: solo fade. */
internal fun detailPopExit(): ExitTransition =
    fadeOut(tween(TRANSITION_FADE_OUT_MS, easing = EmphasizedExit))

/** Transición fade-through reutilizable para contenido interno (tabs, modos). */
internal fun fadeThroughContent(): ContentTransform =
    (
        fadeIn(
            tween(
                TRANSITION_FADE_IN_MS,
                delayMillis = TRANSITION_FADE_OUT_MS,
                easing = EmphasizedEnter
            )
        ) + scaleIn(
            tween(TRANSITION_MS, easing = EmphasizedEnter),
            initialScale = TAB_START_SCALE
        )
        ) togetherWith fadeOut(
        tween(TRANSITION_FADE_OUT_MS, easing = EmphasizedExit)
    )
