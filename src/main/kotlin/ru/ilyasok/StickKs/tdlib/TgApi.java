package ru.ilyasok.StickKs.tdlib;

import org.springframework.stereotype.Component;

import java.util.logging.Logger;
@Component
public class TgApi {

    private static final Logger logger = Logger.getLogger(TgApi.class.getName());

    private static final String TDLibName = "tdjni";

    static {
        try {
            System.loadLibrary(TDLibName);
        } catch (UnsatisfiedLinkError e) {
            logger.info(e.getMessage());
        }
    }

}
