package org.leovegas.wallet.configurations;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {
        "org.leovegas.wallet.controllers",
        "org.leovegas.wallet.repositories",
        "org.leovegas.wallet.services",
})
public class RootConfiguration {

}
