package org.abubusoft.mee.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

//@Component
public class TcpServerAutoStarterApplicationListener implements ApplicationListener<ContextRefreshedEvent> {

//    @Autowired
//    private TcpServerProperties properties;

    @Autowired
    public TcpServerAutoStarterApplicationListener setServer(ConnectionsServer server) {
        this.server = server;
        return this;
    }

    private ConnectionsServer server;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
//        boolean autoStart = properties.getAutoStart();
//        if (autoStart){
//            server.setPort(9000);
//            server.start();
//        }
    }
}