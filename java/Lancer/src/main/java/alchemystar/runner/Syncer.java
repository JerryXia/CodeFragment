/*
 * Copyright (C) 2016 alchemystar, Inc. All Rights Reserved.
 */
package alchemystar.runner;

import java.util.Properties;

import com.mysql.jdbc.StringUtils;

import alchemystar.compare.CompareUnits;
import alchemystar.meta.MetaData;
import alchemystar.util.SqlUtil;

/**
 * @Author lizhuyang
 */
public class Syncer {

    public static void sync(Properties prop) {
        String sourceHost = prop.getProperty("sourceHost");
        if (StringUtils.isEmptyOrWhitespaceOnly(sourceHost)) {
            System.out.println("sourceHost is miss");
            System.exit(-1);
        }
        String sourceUser = prop.getProperty("sourceUser");
        if (StringUtils.isEmptyOrWhitespaceOnly(sourceUser)) {
            System.out.println("sourceUser is miss");
            System.exit(-1);
        }
        String sourcePass = prop.getProperty("sourcePass");
        if (StringUtils.isEmptyOrWhitespaceOnly(sourcePass)) {
            System.out.println("sourcePass is miss");
            System.exit(-1);
        }
        String sourceSchema = prop.getProperty("sourceSchema");
        if (StringUtils.isEmptyOrWhitespaceOnly(sourceSchema)) {
            System.out.println("sourceSchema is miss");
            System.exit(-1);
        }
        String sourceCharset = prop.getProperty("sourceCharset");
        if (StringUtils.isEmptyOrWhitespaceOnly(sourceCharset)) {
            System.out.println("sourceCharset is miss");
            System.exit(-1);
        }
        String targetHost = prop.getProperty("targetHost");
        if (StringUtils.isEmptyOrWhitespaceOnly(targetHost)) {
            System.out.println("targetHost is miss");
            System.exit(-1);
        }
        String targetUser = prop.getProperty("targetUser");
        if (StringUtils.isEmptyOrWhitespaceOnly(targetUser)) {
            System.out.println("targetUser is miss");
            System.exit(-1);
        }
        String targetPass = prop.getProperty("targetPass");
        if (StringUtils.isEmptyOrWhitespaceOnly(targetPass)) {
            System.out.println("targetPass is miss");
            System.exit(-1);
        }
        String targetSchema = prop.getProperty("targetSchema");
        if (StringUtils.isEmptyOrWhitespaceOnly(targetSchema)) {
            System.out.println("targetSchema is miss");
            System.exit(-1);
        }
        String targetCharset = prop.getProperty("targetCharset");
        if (StringUtils.isEmptyOrWhitespaceOnly(targetCharset)) {
            System.out.println("targetCharset is miss");
            System.exit(-1);
        }

        String autoExecute = prop.getProperty("autoExecute");
        if (StringUtils.isEmptyOrWhitespaceOnly(autoExecute)) {
            autoExecute = "FALSE";
        }

        if (!sourceCharset.equals(targetCharset)) {
            System.out.println("source target charset not equal");
            System.out.println(-1);
        }

        MetaData source = new MetaData();
        source.setJdbcUrl("jdbc:mysql://" + sourceHost + "/" + sourceSchema+"?characterEncoding="+sourceCharset);
        source.setUser(sourceUser);
        source.setPassword(sourcePass);
        source.setSchema(sourceSchema);
        source.init();

        MetaData target = new MetaData();
        target.setJdbcUrl("jdbc:mysql://" + targetHost + "/" + targetSchema+"?characterEncoding="+targetCharset);
        target.setUser(targetUser);
        target.setPassword(targetPass);
        target.setSchema(targetSchema);
        target.init();

        CompareUnits units = new CompareUnits(source, target);
        units.compare();

        for (int i = 0; i < units.getChangeSql().size(); i++) {
            System.out.println(units.getChangeSql().get(i));
            if (autoExecute.equals("YES")) {
                SqlUtil.ddl(target.getConn(), units.getChangeSql().get(i));
            }
        }
    }
}

