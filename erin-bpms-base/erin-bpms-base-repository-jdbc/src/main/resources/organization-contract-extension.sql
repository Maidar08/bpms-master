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