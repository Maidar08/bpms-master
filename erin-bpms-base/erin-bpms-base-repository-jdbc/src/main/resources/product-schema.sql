/*
 * Copyright (C) ERIN SYSTEMS LLC, 2020. All rights reserved.
 *
 * The source code is protected by copyright laws and international copyright treaties, as well as
 * other intellectual property laws and treaties.
 */

CREATE TABLE ERIN_BPMS_PRODUCT
   (
    PRODUCT_ID VARCHAR2(10 BYTE) NOT NULL,
	APPLICATION_CATEGORY VARCHAR2(100 BYTE) NOT NULL,
	CATEGORY_DESCRIPTION VARCHAR2(100 BYTE),
	PRODUCT_DESCRIPTION VARCHAR2(200 BYTE) NOT NULL,
	TYPE VARCHAR2(100 BYTE) NOT NULL,
	LOAN_TO_VALUE_RATIO VARCHAR2(10 BYTE) NOT NULL,
	HAS_COLLATERAL NUMBER(1) NOT NULL,
	HAS_INSURANCE NUMBER(1) NOT NULL,
    CONSTRAINT PRODUCT_COMPOSITE_KEY PRIMARY KEY (PRODUCT_ID, APPLICATION_CATEGORY)
   );


--------------------------------------------------------------
-- CONSUMER - Үндсэн төлбөр тэнцүү
--------------------------------------------------------------
INSERT INTO ERIN_BPMS_PRODUCT(PRODUCT_ID, APPLICATION_CATEGORY, CATEGORY_DESCRIPTION, PRODUCT_DESCRIPTION, TYPE, LOAN_TO_VALUE_RATIO, HAS_COLLATERAL, HAS_INSURANCE) VALUES('EB50', 'CONSUMER', 'Үндсэн төлбөр тэнцүү', 'EB50-365-Цалингийн зээл - Иргэн', 'Цалин', '1', 0 , 0);
INSERT INTO ERIN_BPMS_PRODUCT(PRODUCT_ID, APPLICATION_CATEGORY, CATEGORY_DESCRIPTION, PRODUCT_DESCRIPTION, TYPE, LOAN_TO_VALUE_RATIO, HAS_COLLATERAL, HAS_INSURANCE) VALUES('EF50', 'CONSUMER', 'Үндсэн төлбөр тэнцүү', 'EF50-365-Ажиллагсадын хэрэглээний зээл', 'Цалин', '1', 0, 0);
INSERT INTO ERIN_BPMS_PRODUCT(PRODUCT_ID, APPLICATION_CATEGORY, CATEGORY_DESCRIPTION, PRODUCT_DESCRIPTION, TYPE, LOAN_TO_VALUE_RATIO, HAS_COLLATERAL, HAS_INSURANCE) VALUES('EA50', 'CONSUMER', 'Үндсэн төлбөр тэнцүү', 'EA50-365-Өрхийн зээл - Иргэн', 'Өрх', '.70', 1 , 0);
INSERT INTO ERIN_BPMS_PRODUCT(PRODUCT_ID, APPLICATION_CATEGORY, CATEGORY_DESCRIPTION, PRODUCT_DESCRIPTION, TYPE, LOAN_TO_VALUE_RATIO, HAS_COLLATERAL, HAS_INSURANCE) VALUES('EH53', 'CONSUMER', 'Үндсэн төлбөр тэнцүү', 'EH53-365-Хэрэглээний худалдан авалтын зээл - Иргэн', 'Худалдан авалт', '.80', 1 , 0);
INSERT INTO ERIN_BPMS_PRODUCT(PRODUCT_ID, APPLICATION_CATEGORY, CATEGORY_DESCRIPTION, PRODUCT_DESCRIPTION, TYPE, LOAN_TO_VALUE_RATIO, HAS_COLLATERAL, HAS_INSURANCE) VALUES('EG50', 'CONSUMER', 'Үндсэн төлбөр тэнцүү', 'EG50-365- Сургалтын төлбөрийн зээл', 'Цалин', '1', 1 , 0;
INSERT INTO ERIN_BPMS_PRODUCT(PRODUCT_ID, APPLICATION_CATEGORY, CATEGORY_DESCRIPTION, PRODUCT_DESCRIPTION, TYPE, LOAN_TO_VALUE_RATIO, HAS_COLLATERAL, HAS_INSURANCE) VALUES('EA52', 'CONSUMER', 'Үндсэн төлбөр тэнцүү', 'EA52-Эко хэрэглээний зээл -дулаалга', 'Өрх', '.70', 1 , 0);
INSERT INTO ERIN_BPMS_PRODUCT(PRODUCT_ID, APPLICATION_CATEGORY, CATEGORY_DESCRIPTION, PRODUCT_DESCRIPTION, TYPE, LOAN_TO_VALUE_RATIO, HAS_COLLATERAL, HAS_INSURANCE) VALUES('EH90', 'CONSUMER', 'Үндсэн төлбөр тэнцүү', 'EH90-Автомашины зээл-Иргэн', 'Худалдан авалт', '.80', 1 , 1);
INSERT INTO ERIN_BPMS_PRODUCT(PRODUCT_ID, APPLICATION_CATEGORY, CATEGORY_DESCRIPTION, PRODUCT_DESCRIPTION, TYPE, LOAN_TO_VALUE_RATIO, HAS_COLLATERAL, HAS_INSURANCE) VALUES('EH83', 'CONSUMER', 'Үндсэн төлбөр тэнцүү', 'EH83-Хаслизинг санхүү түрээс-Иргэн', 'Худалдан авалт', '.80', 1 , 1);
INSERT INTO ERIN_BPMS_PRODUCT(PRODUCT_ID, APPLICATION_CATEGORY, CATEGORY_DESCRIPTION, PRODUCT_DESCRIPTION, TYPE, LOAN_TO_VALUE_RATIO, HAS_COLLATERAL, HAS_INSURANCE) VALUES('EH88', 'CONSUMER', 'Үндсэн төлбөр тэнцүү', 'EH88-Дугаартай автомашины зээл-Иргэн', 'Худалдан авалт', '.80', 1 , 1);
INSERT INTO ERIN_BPMS_PRODUCT(PRODUCT_ID, APPLICATION_CATEGORY, CATEGORY_DESCRIPTION, PRODUCT_DESCRIPTION, TYPE, LOAN_TO_VALUE_RATIO, HAS_COLLATERAL, HAS_INSURANCE) VALUES('DJ83', 'CONSUMER', 'Үндсэн төлбөр тэнцүү', 'DJ83-Хаслизинг санхүү түрээс-ААН', 'Худалдан авалт', '.80', 1 , 1);
INSERT INTO ERIN_BPMS_PRODUCT(PRODUCT_ID, APPLICATION_CATEGORY, CATEGORY_DESCRIPTION, PRODUCT_DESCRIPTION, TYPE, LOAN_TO_VALUE_RATIO, HAS_COLLATERAL, HAS_INSURANCE) VALUES('EH55', 'CONSUMER', 'Үндсэн төлбөр тэнцүү', 'EH55-365-Эко хэрэглээний зээл-Иргэн', 'Өрх', '.70', 1, 0);
INSERT INTO ERIN_BPMS_PRODUCT(PRODUCT_ID, APPLICATION_CATEGORY, CATEGORY_DESCRIPTION, PRODUCT_DESCRIPTION, TYPE, LOAN_TO_VALUE_RATIO, HAS_COLLATERAL, HAS_INSURANCE) VALUES('EH56', 'CONSUMER', 'Үндсэн төлбөр тэнцүү', 'EH56-Эко хэрэглээний зээл /халаагуур/', 'Өрх', '.70', 1, 0);


--------------------------------------------------------------
-- CONSUMER - Нийт төлбөр тэнцүү
--------------------------------------------------------------
INSERT INTO ERIN_BPMS_PRODUCT(PRODUCT_ID, APPLICATION_CATEGORY, CATEGORY_DESCRIPTION, PRODUCT_DESCRIPTION, TYPE, LOAN_TO_VALUE_RATIO, HAS_COLLATERAL, HAS_INSURANCE) VALUES('EB51', 'CONSUMER', 'Нийт төлбөр тэнцүү', 'EB51-365-Цалингийн зээл -Иргэн –EMI', 'Цалин', '1', 0 , 0);
INSERT INTO ERIN_BPMS_PRODUCT(PRODUCT_ID, APPLICATION_CATEGORY, CATEGORY_DESCRIPTION, PRODUCT_DESCRIPTION, TYPE, LOAN_TO_VALUE_RATIO, HAS_COLLATERAL, HAS_INSURANCE) VALUES('EA51', 'CONSUMER', 'Нийт төлбөр тэнцүү', 'EA51-365-Өрхийн зээл - Иргэн-EMI', 'Өрх', '0.70', 1 , 0);
INSERT INTO ERIN_BPMS_PRODUCT(PRODUCT_ID, APPLICATION_CATEGORY, CATEGORY_DESCRIPTION, PRODUCT_DESCRIPTION, TYPE, LOAN_TO_VALUE_RATIO, HAS_COLLATERAL, HAS_INSURANCE) VALUES('EH54', 'CONSUMER', 'Нийт төлбөр тэнцүү', 'EH54-365-Хэрэглээний ХАЗ-EMI', 'Худалдан авалт', '.80', 1 , 0);
INSERT INTO ERIN_BPMS_PRODUCT(PRODUCT_ID, APPLICATION_CATEGORY, CATEGORY_DESCRIPTION, PRODUCT_DESCRIPTION, TYPE, LOAN_TO_VALUE_RATIO, HAS_COLLATERAL, HAS_INSURANCE) VALUES('EH89', 'CONSUMER', 'Нийт төлбөр тэнцүү', 'EH89-EMI-Автомашины зээл-Иргэн', 'Худалдан авалт', '.80', 1 , 1);
INSERT INTO ERIN_BPMS_PRODUCT(PRODUCT_ID, APPLICATION_CATEGORY, CATEGORY_DESCRIPTION, PRODUCT_DESCRIPTION, TYPE, LOAN_TO_VALUE_RATIO, HAS_COLLATERAL, HAS_INSURANCE) VALUES('DJ87', 'CONSUMER', 'Нийт төлбөр тэнцүү', 'DJ87-EMI-Автомашины зээл-ААН', 'Худалдан авалт', '.80', 1 , 1);
INSERT INTO ERIN_BPMS_PRODUCT(PRODUCT_ID, APPLICATION_CATEGORY, CATEGORY_DESCRIPTION, PRODUCT_DESCRIPTION, TYPE, LOAN_TO_VALUE_RATIO, HAS_COLLATERAL, HAS_INSURANCE) VALUES('EA53', 'CONSUMER', 'Нийт төлбөр тэнцүү', 'EA53-Эко хэрэглээний зээл-EMI /дулаалга/', 'Өрх', '.70', 1 ,0);
INSERT INTO ERIN_BPMS_PRODUCT(PRODUCT_ID, APPLICATION_CATEGORY, CATEGORY_DESCRIPTION, PRODUCT_DESCRIPTION, TYPE, LOAN_TO_VALUE_RATIO, HAS_COLLATERAL, HAS_INSURANCE) VALUES('EH57', 'CONSUMER', 'Нийт төлбөр тэнцүү', 'EH57-Эко хэрэглээний зээл-EMI/халаагуур/', 'Өрх', '.70', 1, 0);
