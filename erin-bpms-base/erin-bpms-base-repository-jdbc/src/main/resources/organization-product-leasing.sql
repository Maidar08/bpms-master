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