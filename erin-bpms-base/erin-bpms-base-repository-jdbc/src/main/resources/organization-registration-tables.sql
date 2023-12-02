--------------------------------------------------------
--  ERIN ORGANIZATION LEASING TABLE
--------------------------------------------------------
CREATE TABLE ERIN_ORG_LEASING
   (
    ORG_REQUEST_ID VARCHAR2(50 BYTE) NOT NULL,
	PROCESS_TYPE_ID VARCHAR2(50 BYTE) NOT NULL,
	BRANCH_ID VARCHAR2(4 BYTE) NOT NULL,

	CONTRACT_NUM VARCHAR2(50 BYTE),
    REG_NUMBER VARCHAR2(50 BYTE),
    COUNTRY_REG_NUMBER VARCHAR2(255 BYTE),

    FULL_NAME VARCHAR2(255 BYTE),
    FIRST_NAME VARCHAR2(255 BYTE),
	LAST_NAME VARCHAR2(255 BYTE),

	COLLABORATOR_TYPE VARCHAR2(50 BYTE),
    MODIFIED_DATE DATE,

    MODIFIED_USER_ID VARCHAR2(255 BYTE),
    STATE VARCHAR2(255 BYTE),

    CITY VARCHAR2(255 BYTE),
    DISTRICT VARCHAR2(255 BYTE),
    QUARTER VARCHAR2(255 BYTE),

    ADDRESS VARCHAR2(255 BYTE),
    PHONE VARCHAR2(100 BYTE),
    FAX VARCHAR2(255 BYTE),
    EMAIL VARCHAR2(100 BYTE),

    CONSTRAINT ERIN_ORG_LEASING_PK1 PRIMARY KEY (ORG_REQUEST_ID)
   );

--------------------------------------------------------
--  ERIN ORGANIZATION SALARY TABLE
--------------------------------------------------------
CREATE TABLE ERIN_ORG_SALARY
   (
    ORG_REQUEST_ID VARCHAR2(50 BYTE) NOT NULL,
	PROCESS_TYPE_ID VARCHAR2(50 BYTE) NOT NULL,
	BRANCH_ID VARCHAR2(4 BYTE) NOT NULL,

	CONTRACT_NUM VARCHAR2(50 BYTE),
    REG_NUMBER VARCHAR2(50 BYTE),
	COUNTRY_REG_NUMBER VARCHAR2(50 BYTE),

    CONTRACT_ORG_NAME VARCHAR2(255 BYTE),
	REGISTERED_ORG_NAME VARCHAR2(255 BYTE),

	OP_TYPE_ID VARCHAR2(10 BYTE),
    OP_TYPE_DESCRIPTION VARCHAR2(255 BYTE),

    ORG_TYPE NUMBER(1),
    MODIFIED_DATE DATE,

    MODIFIED_USER_ID VARCHAR2(255 BYTE),
    STATE VARCHAR2(255 BYTE),

    ACCOUNT_NUMBER VARCHAR2(10 BYTE),
    EMP_COUNT NUMBER(20),

    IS_SALARY_LOAN NUMBER(1),
    HAS_CONTRACT NUMBER(1),

    SPECIAL_CONDITION NCLOB,

    CONSTRAINT ERIN_ORG_SALARY_PK1 PRIMARY KEY (ORG_REQUEST_ID)
   );


--------------------------------------------------------
--  ERIN CONTRACT INFO TABLE OF SALARY ORGANIZATION
--------------------------------------------------------
CREATE TABLE ERIN_CONTRACT_INFO_SALARY
  (
     ORG_REQUEST_ID               VARCHAR2(50 BYTE) NOT NULL,
     CONTRACT_NUM             VARCHAR2(50 BYTE) NOT NULL,

     BRANCH_ID                VARCHAR2(4 BYTE),
     TERM_YEAR                CHAR(2 CHAR) DEFAULT '1',

     LEAKAGE                  NUMBER(18, 2),
     ORG_RANK                 VARCHAR2(255 BYTE),

     INT_RATE_COND            CHAR(2 CHAR) DEFAULT 'ST',
     KEY_EMP_INT_RATE_MONTHLY NUMBER(18, 2),

     MAX_INT_RATE             NUMBER(18, 2),
     MIN_INT_RATE             NUMBER(18, 2),

     SALARY_TRANS_FEE         NUMBER DEFAULT 1,
     SALARY_COUNT_MONTHLY     CHAR(1 CHAR) DEFAULT '2',

     SALARY_FIRST_DAY         CHAR(2 CHAR) DEFAULT '11',
     SALARY_SECOND_DAY        CHAR(2 CHAR) DEFAULT '25',

     CREATED_DATE             TIMESTAMP(6),
     END_DATE                 TIMESTAMP(6),

     CREATED_USER_ID          VARCHAR2(50 BYTE) NOT NULL,
     CREATED_USER_NAME        VARCHAR2(100 BYTE),

     LAST_UPDATED_USER_ID     VARCHAR2(50 BYTE) NOT NULL,
     LAST_UPDATED_USER_NAME   VARCHAR2(100 BYTE),

     APPROVED_USER_ID         VARCHAR2(50 BYTE),
     APPROVED_USER_NAME       VARCHAR2(100 BYTE),

     REL_EMP_NAME             VARCHAR2(255 BYTE),
     REL_EMP_PHONE            VARCHAR2(255 BYTE),

     CREATED_AT               TIMESTAMP(6) NOT NULL,
     UPDATED_AT               TIMESTAMP(6) NOT NULL,

     MODIFIED_NUM             VARCHAR2(50 BYTE) NOT NULL,
     IS_LATEST                CHAR(1 CHAR) DEFAULT 'N',
     SPECIAL_CONDITION        NCLOB,

     CONSTRAINT ERIN_CONTRACT_INFO_SALARY_PK1 PRIMARY KEY (ORG_REQUEST_ID, CONTRACT_NUM)
  );

--------------------------------------------------------
--  ERIN CONTRACT INFO TABLE OF LEASING ORGANIZATION
--------------------------------------------------------
CREATE TABLE ERIN_CONTRACT_INFO_LEASING
  (
     ORG_REQUEST_ID           VARCHAR2(50 BYTE) NOT NULL,
     CONTRACT_NUM             VARCHAR2(50 BYTE) NOT NULL,

     BRANCH_ID                VARCHAR2(4 BYTE),
     TERM_YEAR                CHAR(2 CHAR) DEFAULT '1',

     CUSTOMER_CIF             VARCHAR(50 BYTE),
     ACCOUNT_NUMBER           VARCHAR(10 BYTE),

     PAYMENT_TYPE             VARCHAR(50 BYTE),
     PAYMENT_AMOUNT           VARCHAR(255 BYTE),
     PAYMENT_PERCENT          NUMBER(18, 2),

     SUPP_PAY_TERM_DAY        CHAR(2 CHAR),
     SUPP_PAY_PERCENT         NUMBER(18),

     IS_ZERO_RATE             CHAR(2 CHAR) DEFAULT 'N',
     FEE                      NUMBER(18),

     CREATED_DATE             TIMESTAMP(6),
     END_DATE                 TIMESTAMP(6),

     CREATED_USER_ID          VARCHAR2(50 BYTE) NOT NULL,
     CREATED_USER_NAME        VARCHAR2(100 BYTE),

     LAST_UPDATED_USER_ID     VARCHAR2(50 BYTE) NOT NULL,
     LAST_UPDATED_USER_NAME   VARCHAR2(100 BYTE),

     APPROVED_USER_ID         VARCHAR2(50 BYTE),
     APPROVED_USER_NAME       VARCHAR2(100 BYTE),

     REL_EMP_NAME             VARCHAR2(255 BYTE),
     REL_EMP_PHONE            VARCHAR2(255 BYTE),

     CREATED_AT               TIMESTAMP(6) NOT NULL,
     UPDATED_AT               TIMESTAMP(6) NOT NULL,

     MODIFIED_NUM             VARCHAR2(50 BYTE) NOT NULL,
     IS_LATEST                CHAR(1 CHAR) DEFAULT 'N',
     CONSTRAINT ERIN_CONTRACT_INFO_LEASING_PK1 PRIMARY KEY (ORG_REQUEST_ID, CONTRACT_NUM)
  );


--------------------------------------------------------
--  ERIN CONTRACT EXTENSION TABLE
--------------------------------------------------------
CREATE TABLE ERIN_CONTRACT_EXTENSION
  (
     EXTENSION_ID             VARCHAR2(80 BYTE) NOT NULL,
     CONTRACT_NUM             VARCHAR(50 BYTE) NOT NULL,
     IS_EXTENDED              CHAR(2 CHAR) DEFAULT 'N',

     EXTENDED_DATE            TIMESTAMP(6),
     END_DATE                 TIMESTAMP(6),

     FEE                      NUMBER,
     STATUS                   VARCHAR2(255 BYTE),

     TERM_YEAR                CHAR(2 CHAR) DEFAULT '1',
     TYPE                     VARCHAR2(255 BYTE),

     CONSTRAINT ERIN_CONTRACT_EXTENSION_PK1 PRIMARY KEY (EXTENSION_ID)
  );



--------------------------------------------------------
--  ERIN PRODUCT LEASING TABLE
--------------------------------------------------------
CREATE TABLE ERIN_PRODUCT_LEASING  (
     ID                       VARCHAR(80 BYTE) NOT NULL,
     CONTRACT_NUM             VARCHAR(50 BYTE),
     CATEGORY                 VARCHAR(255 BYTE),

     SUB_CATEGORY             VARCHAR(255 BYTE),
     DESCRIPTION              CLOB,
     CONSTRAINT ERIN_PRODUCT_LEASING_PK1 PRIMARY KEY (ID)
  );

