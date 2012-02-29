package com.ramonli.lottery.core.support;

import java.util.Properties;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

public class XPropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer {
    public static final String JDBC_PASSWORD = "jdbc.password";
    public static final String JDBC_USER = "jdbc.user";
    
    public String resolvePlaceholder(String placeholder, Properties props) {
        String value = props.getProperty(placeholder);
        // decrypt the database user/password
//        if (JDBC_PASSWORD.equalsIgnoreCase(placeholder) || JDBC_USER.equalsIgnoreCase(placeholder)){
//            value = SecUtil.decrypt(value);
////            logger.debug("The value of placeholder:" + placeholder + " is " + value);
//        }
        return value;
    }

}
