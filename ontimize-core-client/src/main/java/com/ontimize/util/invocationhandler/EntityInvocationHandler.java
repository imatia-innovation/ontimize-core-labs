package com.ontimize.util.invocationhandler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.jee.common.db.Entity;

public class EntityInvocationHandler implements InvocationHandler {

    private static final Logger logger = LoggerFactory.getLogger(EntityInvocationHandler.class);

    protected Entity entity;

    protected String entityName;

    public EntityInvocationHandler(String entityName, Entity entity) {
        this.entity = entity;
        this.entityName = entityName;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        long t = System.currentTimeMillis();
        Object toRet = method.invoke(this.entity, args);
        if (EntityInvocationHandler.logger.isTraceEnabled()) {
            EntityInvocationHandler.logger.trace("{} ms in {} entity: invoke method -> {} with arguments {}",
                    System.currentTimeMillis() - t, this.entityName, method, args);
        }
        return toRet;
    }

}
