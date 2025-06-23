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
            MainMenuHandler mainMenuHandler,
            NameRegistrationHandler nameRegistrationHandler,
            AddCatNameHandler addCatNameHandler,
            AddCatPhotoHandler addCatPhotoHandler,
            AddCatConfirmationHandler addCatConfirmationHandler
    ) {
        return Arrays.asList(
                nameRegistrationHandler,
                mainMenuHandler,
                addCatNameHandler,
                addCatPhotoHandler,
                addCatConfirmationHandler
        );
    }
}
