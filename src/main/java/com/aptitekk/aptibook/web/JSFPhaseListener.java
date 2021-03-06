/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.web;

import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import java.util.HashMap;
import java.util.Map;

public class JSFPhaseListener implements PhaseListener {

    private Map<PhaseId, Long> phaseTimeMap = new HashMap<>();

    @Override
    public PhaseId getPhaseId() {
        return PhaseId.ANY_PHASE;
    }

    @Override
    public void beforePhase(PhaseEvent event) {
        /*if (event.getPhaseId() == PhaseId.RESTORE_VIEW) {
            phaseTimeMap = new HashMap<>();
            LogManager.logInfo("");
            LogManager.logInfo("######### Starting Phases...");
        }
        phaseTimeMap.putIfAbsent(event.getPhaseId(), System.nanoTime());*/
    }

    @Override
    public void afterPhase(PhaseEvent event) {
        /*LogManager.logInfo("### Executed Phase " + event.getPhaseId());
        if (phaseTimeMap.get(event.getPhaseId()) != null) {
            long endTime = System.nanoTime();
            long diffMs = (long) ((endTime - phaseTimeMap.get(event.getPhaseId())) * 0.000001);

            LogManager.logInfo("### Execution Time = " + diffMs + "ms");
        }*/
    }

}
