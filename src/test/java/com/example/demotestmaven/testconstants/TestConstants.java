package com.example.demotestmaven.testconstants;

public class 

TestConstants {
    // PATH CONSTANTS
    public static final String TEST_DATA_PATH = "testdata/";
    private static final String USER_EXCEL_IMPORT_PATH = TEST_DATA_PATH + "user_excel_import/";
    public static final String USER_EXCEL_IMPORT_NORMAL_FILE = USER_EXCEL_IMPORT_PATH + "normal_file.xlsx";
    public static final String USER_EXCEL_IMPORT_EMPTY_FILE = USER_EXCEL_IMPORT_PATH + "empty_file.xlsx";
    public static final String USER_EXCEL_IMPORT_DUPLICATE_USERNAME_FILE = USER_EXCEL_IMPORT_PATH + "duplicate_username.xlsx";
    public static final String USER_EXCEL_IMPORT_DUPLICATE_EMAIL_FILE = USER_EXCEL_IMPORT_PATH + "duplicate_email.xlsx";
    public static final String USER_EXCEL_IMPORT_MISSING_USERNAME_FILE = USER_EXCEL_IMPORT_PATH + "missing_username.xlsx";
    public static final String USER_EXCEL_IMPORT_MISSING_PASSWORD_FILE = USER_EXCEL_IMPORT_PATH + "missing_password.xlsx";
    public static final String USER_EXCEL_IMPORT_FALSE_STRUCTURE_FILE = USER_EXCEL_IMPORT_PATH + "false_structure_file.xlsx";
    public static final String USER_EXCEL_IMPORT_FALSE_TYPE_FILE = USER_EXCEL_IMPORT_PATH + "false_type_file.txt";
    public static final String USER_EXCEL_IMPORT_FORMAT_MISMATCH_FILE = USER_EXCEL_IMPORT_PATH + "format_missmatch.xlsx";
    public static final String USER_EXCEL_IMPORT_MISSING_EMAIL_FILE = USER_EXCEL_IMPORT_PATH + "missing_email.xlsx"; 
    public static final String USER_EXCEL_IMPORT_MISSING_ROLE_CODE_FILE = USER_EXCEL_IMPORT_PATH + "missing_rolecode.xlsx";
    
    
    // HEADER CONSTANTS
    public static final String USER_EXCEL_IMPORT_USERNAME_HEADER = "username";
    public static final String USER_EXCEL_IMPORT_PASSWORD_HEADER = "password";
    public static final String USER_EXCEL_IMPORT_EMAIL_HEADER = "email";
    public static final String USER_EXCEL_IMPORT_ROLE_CODE_HEADER = "rolecode";
    public static final String USER_EXCEL_IMPORT_ROLE_TYPE_HEADER = "roletype";
    public static final String USER_EXCEL_IMPORT_ROW_NUMBER_HEADER = "rownumber";

    // STATUS CONSTANTS
    public static final String statusSuccess = "success";
    public static final String statusError = "error";
}
