package org.leovegas.wallet.configurations;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {
        "org.leovegas.wallet.controllers"
})
public class RootConfiguration {

}
