package com.amazonaws.models.nosql;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

import java.io.Serializable;

@DynamoDBTable(tableName = "honeycomb-mobilehub-1154077879-Company")

public class CompanyDO implements Serializable
{
    private String _companyID;
    private String _name;

    @DynamoDBHashKey(attributeName = "companyID")
    @DynamoDBAttribute(attributeName = "companyID")
    public String getCompanyID() {
        return _companyID;
    }

    public void setCompanyID(final String _companyID) {
        this._companyID = _companyID;
    }
    @DynamoDBAttribute(attributeName = "name")
    public String getName() {
        return _name;
    }

    public void setName(final String _name) {
        this._name = _name;
    }

}
