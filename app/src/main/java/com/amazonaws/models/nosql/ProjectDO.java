package com.amazonaws.models.nosql;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

import java.io.Serializable;

@DynamoDBTable(tableName = "honeycomb-mobilehub-1154077879-Project")

public class ProjectDO implements Serializable
{
    private String _companyID;
    private String _projectID;
    private String _description;
    private String _name;

    @DynamoDBHashKey(attributeName = "companyID")
    @DynamoDBAttribute(attributeName = "companyID")
    public String getCompanyID() {
        return _companyID;
    }

    public void setCompanyID(final String _companyID) {
        this._companyID = _companyID;
    }
    @DynamoDBRangeKey(attributeName = "projectID")
    @DynamoDBAttribute(attributeName = "projectID")
    public String getProjectID() {
        return _projectID;
    }

    public void setProjectID(final String _projectID) {
        this._projectID = _projectID;
    }
    @DynamoDBAttribute(attributeName = "Description")
    public String getDescription() {
        return _description;
    }

    public void setDescription(final String _description) {
        this._description = _description;
    }
    @DynamoDBAttribute(attributeName = "Name")
    public String getName() {
        return _name;
    }

    public void setName(final String _name) {
        this._name = _name;
    }

}
