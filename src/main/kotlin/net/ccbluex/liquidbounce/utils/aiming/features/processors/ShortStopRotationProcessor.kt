/*
 * This file is part of LiquidBounce (https://github.com/CCBlueX/LiquidBounce)
 *
 * Copyright (c) 2015 - 2026 CCBlueX
 *
 * LiquidBounce is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * LiquidBounce is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with LiquidBounce. If not, see <https://www.gnu.org/licenses/>.
 */
package net.ccbluex.liquidbounce.utils.aiming.features.processors

import net.ccbluex.liquidbounce.config.types.group.ToggleableValueGroup
import net.ccbluex.liquidbounce.config.utils.percentageChance
import net.ccbluex.liquidbounce.event.EventListener
import net.ccbluex.liquidbounce.utils.aiming.RotationTarget
import net.ccbluex.liquidbounce.utils.aiming.data.Rotation
import net.ccbluex.liquidbounce.utils.kotlin.random

/**
 * Short stop temporarily halts aiming at the target based on a specified rate.
 */
class ShortStopRotationProcessor(owner: EventListener? = null)
    : ToggleableValueGroup(owner, "ShortStop", false), RotationProcessor {

    private val rate = percentageChance("Rate", 3, 1..25)
    
    // Slider for speed multiplier from 0.000 (0%) to 1.000 (100%)
    private val speedMult by float("SpeedMult", 0f, 0f..1f)

    override fun process(
        rotationTarget: RotationTarget,
        currentRotation: Rotation,
        targetRotation: Rotation
    ): Rotation {
        if (!this.running) {
            return targetRotation
        }

        // If the rate is met, we apply the speed multiplier
        if (rate.asBoolean) {
            return if (speedMult == 0f) {
                // 0 is the old logic: tiny random movement (effectively a short stop)
                currentRotation.towardsLinear(targetRotation, (0.0f..0.1f).random(), (0.0f..0.1f).random())
            } else {
                // Apply the speed multiplier to the current rotation delta
                val delta = currentRotation.rotationDeltaTo(targetRotation)
                currentRotation.towardsLinear(targetRotation, delta.x * speedMult, delta.y * speedMult)
            }
        }

        return targetRotation
    }

    }
