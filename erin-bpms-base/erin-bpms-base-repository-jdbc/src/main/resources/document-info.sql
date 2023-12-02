/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

CREATE TABLE DOCUMENT_INFO
   (	ID VARCHAR2(100 BYTE) NOT NULL,
	DOCUMENT_PARENT_ID VARCHAR2(100 BYTE),
	NAME VARCHAR2(100 BYTE) NOT NULL,
    TYPE VARCHAR2(50 BYTE),
    CONSTRAINT DOCUMENT_INFO_PK PRIMARY KEY (ID)
   );