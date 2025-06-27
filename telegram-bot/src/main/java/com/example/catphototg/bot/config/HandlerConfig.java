package com.example.catphototg.bot.config;

import com.example.catphototg.bot.handlers.*;
import com.example.catphototg.bot.handlers.interfaces.UpdateHandler;
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
            CatDetailsHandler catDetailsHandler,
            CatCardActionHandler catCardActionHandler,
            MyCatsHandler myCatsHandler,
            StateRestoreHandler stateRestoreHandler,
            ViewCatsHandler viewCatsHandler,
            ReactionHandler reactionHandler
    ) {
        return Arrays.asList(
                startCommandHandler,
                nameRegistrationHandler,
                mainMenuHandler,
                addCatNameHandler,
                addCatPhotoHandler,
                catDetailsHandler,
                catCardActionHandler,
                addCatConfirmationHandler,
                myCatsHandler,
                viewCatsHandler,
                reactionHandler,
                stateRestoreHandler
        );
    }
}
