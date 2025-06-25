package com.example.catphototg.config;

import com.example.catphototg.handlers.*;
import com.example.catphototg.handlers.interfaces.UpdateHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
public class HandlerConfig {
    @Bean
    public List<UpdateHandler> handlers(
            StartCommandHandler startCommandHandler,
            MainMenuHandler mainMenuHandler,
            NameRegistrationHandler nameRegistrationHandler,
            AddCatNameHandler addCatNameHandler,
            AddCatPhotoHandler addCatPhotoHandler,
            AddCatConfirmationHandler addCatConfirmationHandler,
            StateRestoreHandler stateRestoreHandler
    ) {
        return Arrays.asList(
                startCommandHandler,
                nameRegistrationHandler,
                mainMenuHandler,
                addCatNameHandler,
                addCatPhotoHandler,
                addCatConfirmationHandler,
                stateRestoreHandler
        );
    }
}
