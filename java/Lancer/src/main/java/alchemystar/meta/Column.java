/*
 * Copyright (C) 2016 alchemystar, Inc. All Rights Reserved.
 */
package alchemystar.meta;

import com.mysql.jdbc.StringUtils;

/**
 * @Author lizhuyang
 */
public class Column {

    //$sql = 'select COLUMN_NAME,COLUMN_TYPE,IS_NULLABLE,COLUMN_DEFAULT,COLUMN_COMMENT,EXTRA from information_schema.columns ';
    //$sql.= 'where TABLE_SCHEMA=\''.$db.'\' and TABLE_NAME=\''.$table.'\' order by ORDINAL_POSITION asc';
    private String name;
    private String type;
    private String isNull;
    private String defaultValue;
    private String comment;
    private String extra;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIsNull() {
        return isNull;
    }

    public void setIsNull(String isNull) {
        this.isNull = isNull;
    }

    public String getDefaultValue() {
        if(StringUtils.isEmptyOrWhitespaceOnly(defaultValue)){
            return "";
        }
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Column column = (Column) o;

        if (name != null ? !name.equals(column.name) : column.name != null) {
            return false;
        }
        if (type != null ? !type.equals(column.type) : column.type != null) {
            return false;
        }
        if (isNull != null ? !isNull.equals(column.isNull) : column.isNull != null) {
            return false;
        }
        if (defaultValue != null ? !defaultValue.equals(column.defaultValue) : column.defaultValue != null) {
            return false;
        }
        // todo comment remove
        /*if (comment != null ? !comment.equals(column.comment) : column.comment != null) {
            return false;
        }*/
        return extra != null ? extra.equals(column.extra) : column.extra == null;

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (isNull != null ? isNull.hashCode() : 0);
        result = 31 * result + (defaultValue != null ? defaultValue.hashCode() : 0);
        result = 31 * result + (comment != null ? comment.hashCode() : 0);
        result = 31 * result + (extra != null ? extra.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Column{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", isNull='" + isNull + '\'' +
                ", defaultValue='" + defaultValue + '\'' +
                ", comment='" + comment + '\'' +
                ", extra='" + extra + '\'' +
                '}';
    }
}
