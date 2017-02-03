package de.novatec.marioai.base.api;

import ch.idsia.agents.controllers.MarioHijackAIBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MarioAiBaseApi extends MarioHijackAIBase{

    private static Logger log = LoggerFactory.getLogger(MarioAiBaseApi.class);

    public void testCall() {
        log.info("TESTCALL");
    }
}
