--------------------------------------------------------
--  File created - Friday-April-20-2020
--------------------------------------------------------

--------------------------------------------------------
--  Use this if you need to delete a sequence or trigger
--------------------------------------------------------
DROP SEQUENCE PROCESS_REQUEST_SEQ;

--------------------------------------------------------
--  (1) Create a sequence - used by trigger when a new process is inserted - updated to start from 0 (4/20/2020)
--------------------------------------------------------
CREATE SEQUENCE PROCESS_REQUEST_SEQ
      INCREMENT BY 1
      START WITH 1
      NOMAXVALUE
      NOCYCLE
      CACHE 50;

--------------------------------------------------------
--  (2) Run the table creation queries below
--------------------------------------------------------

CREATE TABLE PROCESS_REQUEST
   (	PROCESS_REQUEST_ID VARCHAR2(50 BYTE) NOT NULL,
	PROCESS_TYPE_ID VARCHAR2(50 BYTE) NOT NULL,
	GROUP_ID VARCHAR2(50 BYTE) NOT NULL,
	REQUESTED_USER_ID VARCHAR2(50 BYTE) NOT NULL,
	CREATED_TIME DATE NOT NULL,
	ASSIGNED_USER_ID VARCHAR2(50 BYTE),
	PROCESS_INSTANCE_ID VARCHAR2(50 BYTE),
	ASSIGNED_TIME DATE,
	PROCESS_REQUEST_STATE VARCHAR2(20 BYTE) NOT NULL,
    CONSTRAINT PROCESS_REQUEST_PK1 PRIMARY KEY (PROCESS_REQUEST_ID)
   );

   CREATE TABLE PROCESS_TYPE
   (	PROCESS_TYPE_ID VARCHAR2(50 BYTE) NOT NULL,
	DEFINITION_KEY VARCHAR2(100 BYTE) NOT NULL,
	VERSION VARCHAR2(20 BYTE),
	NAME NVARCHAR2(50) NOT NULL,
	PROCESS_DEFINITION_TYPE VARCHAR2(20 BYTE) NOT NULL,
	PROCESS_TYPE_CATEGORY VARCHAR (100 BYTE) NOT NULL,
    CONSTRAINT PROCESS_TYPE_PK1 PRIMARY KEY (PROCESS_TYPE_ID)
    );

     CREATE TABLE PROCESS_REQUEST_PARAMETER
   (	PROCESS_REQUEST_ID VARCHAR2(100 BYTE) NOT NULL,
	PARAMETER_NAME VARCHAR2(100 BYTE) NOT NULL,
	PARAMETER_VALUE VARCHAR2(100 BYTE) NOT NULL,
	PARAMETER_TYPE VARCHAR2(50 BYTE) NOT NULL,
    CONSTRAINT PROCESS_REQ_PARAM_PK1 PRIMARY KEY (PROCESS_REQUEST_ID, PARAMETER_NAME)
   );

--------------------------------------------------------
--  This is used to display the format more precisely, has to be entered every session(temporary fix)
--------------------------------------------------------
    alter session set nls_date_format = 'dd/MON/yyyy hh24:mi:ss'

--------------------------------------------------------
-- This is used to add microLoan option to process_type (huselt usgeh option)
--------------------------------------------------------
    INSERT INTO PROCESS_TYPE (PROCESS_TYPE_ID, DEFINITION_KEY, VERSION, NAME, PROCESS_DEFINITION_TYPE) values ('microLoan', 'bpms_micro_loan_case', 2.2, 'Бизнесийн зээл', 'CASE');

--------------------------------------------------------
--  This query is used to increase column size in process request parameter table - 2020 October 13
--------------------------------------------------------
-- ALTER TABLE PROCESS_REQUEST_PARAMETER
-- MODIFY (
-- PROCESS_REQUEST_ID VARCHAR2(100 BYTE),
-- PARAMETER_NAME VARCHAR2(100 BYTE),
-- PARAMETER_VALUE VARCHAR2(100 BYTE),
-- PARAMETER_TYPE VARCHAR2(50 BYTE)
-- );
--
-- COMMIT;