package org.abubusoft.mee.services.impl;

import org.abubusoft.mee.services.TcpController;
import org.abubusoft.mee.model.Connection;
import org.abubusoft.mee.services.ConnectionsServer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//@Component
public class ConnectionBeanPostProcessor implements BeanPostProcessor {
    private Map<String, Class> map = new HashMap<>();

    @Autowired
    private ConnectionsServer server;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();
        if (beanClass.isAnnotationPresent(TcpController.class)) {
            map.put(beanName, beanClass);
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (map.containsKey(beanName)) {
            List<Method> receiveMethods = new ArrayList<>();
            List<Method> connectMethods = new ArrayList<>();
            List<Method> disconnectMethods = new ArrayList<>();
            Method[] methods = bean.getClass().getMethods();
            for (Method method : methods) {
                if (method.getName().startsWith("receive") && method.getParameterCount() == 2
                        && method.getParameterTypes()[0] == Connection.class) {
                    receiveMethods.add(method);
                } else if (method.getName().startsWith("connect") && method.getParameterCount() == 1
                        && method.getParameterTypes()[0] == Connection.class) {
                    connectMethods.add(method);
                } else if (method.getName().startsWith("disconnect") && method.getParameterCount() == 1
                        && method.getParameterTypes()[0] == Connection.class) {
                    disconnectMethods.add(method);
                }
            }

            server.addListener(new Connection.Listener() {
                @Override
                public void messageReceived(Connection connection, Object message) {
                    for (Method receiveMethod : receiveMethods) {
                        Class<?> aClass = receiveMethod.getParameterTypes()[1];
                        if (message.getClass().isAssignableFrom(aClass)) {
                            try {
                                receiveMethod.invoke(bean, connection, message);
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            } catch (InvocationTargetException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

                @Override
                public void connected(Connection connection) {
                    for (Method connectMethod : connectMethods) {
                        try {
                            connectMethod.invoke(bean, connection);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void disconnected(Connection connection) {
                    for (Method disconnectMethod : disconnectMethods) {
                        try {
                            disconnectMethod.invoke(bean, connection);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
        return bean;
    }
}