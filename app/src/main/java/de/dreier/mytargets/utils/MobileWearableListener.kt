/*
 * Copyright (C) 2018 Florian Dreier
 *
 * This file is part of MyTargets.
 *
 * MyTargets is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
 * MyTargets is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package de.dreier.mytargets.utils

import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService
import de.dreier.mytargets.R
import de.dreier.mytargets.app.ApplicationInstance
import de.dreier.mytargets.features.settings.SettingsManager
import de.dreier.mytargets.shared.models.Environment
import de.dreier.mytargets.shared.models.TimerSettings
import de.dreier.mytargets.shared.models.augmented.AugmentedEnd
import de.dreier.mytargets.shared.models.augmented.AugmentedRound
import de.dreier.mytargets.shared.models.augmented.AugmentedStandardRound
import de.dreier.mytargets.shared.models.augmented.AugmentedTraining
import de.dreier.mytargets.shared.models.dao.RoundDAO
import de.dreier.mytargets.shared.models.dao.TrainingDAO
import de.dreier.mytargets.shared.models.db.Round
import de.dreier.mytargets.shared.models.db.Training
import de.dreier.mytargets.shared.utils.unmarshall
import de.dreier.mytargets.shared.wearable.WearableClientBase
import org.threeten.bp.LocalDate
import java.util.*

/**
 * Listens for incoming connections of wearable devices.
 * On request the class takes care of creating a new training or
 * adding an end to an existing training.
 */
class MobileWearableListener : WearableListenerService() {

    override fun onMessageReceived(messageEvent: MessageEvent) {
        super.onMessageReceived(messageEvent)
        when (messageEvent.path) {
            WearableClientBase.TRAINING_CREATE -> createTraining(messageEvent)
            WearableClientBase.END_UPDATE -> endUpdated(messageEvent)
            WearableClientBase.REQUEST_TRAINING_TEMPLATE -> trainingTemplate()
            WearableClientBase.TIMER_SETTINGS -> timerSettings(messageEvent)
            else -> {
            }
        }
    }

    private fun timerSettings(messageEvent: MessageEvent) {
        val settings = messageEvent.data.unmarshall(TimerSettings.CREATOR)
        SettingsManager.timerSettings = settings
        ApplicationInstance.wearableClient.sendTimerSettingsFromRemote()
    }

    private fun trainingTemplate() {
        val lastTraining = TrainingDAO.loadTrainings().minWith(Collections.reverseOrder())
        if (lastTraining != null && lastTraining.date.isEqual(LocalDate.now())) {
            ApplicationInstance.wearableClient.updateTraining(AugmentedTraining(lastTraining))
        } else {
            val training = Training()
            training.title = getString(R.string.training)
            training.date = LocalDate.now()
            training.environment = Environment.getDefault(SettingsManager.indoor)
            training.bowId = SettingsManager.bow
            training.arrowId = SettingsManager.arrow
            training.arrowNumbering = false
            val aTraining = AugmentedTraining(training)

            val freeTraining = lastTraining?.standardRoundId == null
            if (freeTraining) {
                val round = Round()
                round.target = SettingsManager.target
                round.shotsPerEnd = SettingsManager.shotsPerEnd
                round.maxEndCount = null
                round.distance = SettingsManager.distance
                aTraining.rounds = mutableListOf(AugmentedRound(round))
            } else {
                aTraining.initRoundsFromTemplate(AugmentedStandardRound(lastTraining!!.standardRound!!))
            }
            ApplicationInstance.wearableClient.sendTrainingTemplate(aTraining)
        }
    }

    private fun createTraining(messageEvent: MessageEvent) {
        val augmentedTraining = messageEvent.data.unmarshall(AugmentedTraining.CREATOR)
        TrainingDAO.saveTraining(augmentedTraining.training, augmentedTraining.rounds.map { it.round })
        ApplicationInstance.wearableClient.updateTraining(augmentedTraining)
        ApplicationInstance.wearableClient.sendCreateTrainingFromRemoteBroadcast()
    }

    private fun endUpdated(messageEvent: MessageEvent) {
        val (end, shots) = messageEvent.data.unmarshall(AugmentedEnd.CREATOR)
        val round = AugmentedRound(RoundDAO.loadRound(end.roundId!!))
        val newEnd = getLastEmptyOrCreateNewEnd(round)
        newEnd.end.exact = false
        newEnd.shots = shots
        newEnd.save()

        ApplicationInstance.wearableClient.sendUpdateTrainingFromRemoteBroadcast(round.round, newEnd.end)
        ApplicationInstance.wearableClient
                .sendUpdateTrainingFromLocalBroadcast(AugmentedTraining(TrainingDAO.loadTraining(round.round.trainingId!!)))
    }

    private fun getLastEmptyOrCreateNewEnd(round: AugmentedRound): AugmentedEnd {
        if (round.ends.isEmpty()) {
            return round.addEnd()
        }
        val lastEnd = round.ends[round.ends.size - 1]
        return if (lastEnd.isEmpty) {
            lastEnd
        } else {
            round.addEnd()
        }
    }
}
