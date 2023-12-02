ALTER SESSION SET CURRENT_SCHEMA = ERIN;
----PROCESS TYPE TABLE----

INSERT INTO PROCESS_TYPE(process_type_id, definition_key, version, name, process_definition_type, process_type_category) values ('bnplLoan', 'bpms_bnpl_loan_request', '4.9', 'BNPL', 'PROCESS', 'BNPL');
Insert into PROCESS_TYPE (PROCESS_TYPE_ID,DEFINITION_KEY,VERSION,NAME,PROCESS_DEFINITION_TYPE,PROCESS_TYPE_CATEGORY) values ('onlineSalary','bpms_direct_online_salary_loan_case','2.4.0','Онлайн цалингийн зээл','CASE','DIRECT_ONLINE');
INSERT INTO PROCESS_TYPE (PROCESS_TYPE_ID, DEFINITION_KEY, VERSION, NAME, PROCESS_DEFINITION_TYPE, PROCESS_TYPE_CATEGORY) VALUES ('instantLoan', 'bpms_instant_loan_request', '4.11.0', N'Шуурхай зээл', 'PROCESS', 'INSTANT_LOAN');
Insert into PROCESS_TYPE (PROCESS_TYPE_ID,DEFINITION_KEY,VERSION,NAME,PROCESS_DEFINITION_TYPE,PROCESS_TYPE_CATEGORY) values ('onlineLeasing','bpms_online_leasing','4.12.0','Онлайн лизинг','PROCESS','ONLINE_LEASING');

-------ERIN BPMS DEFAULT PARAMETER-----

------ LoanTerms------
INSERT INTO ERIN_BPMS_DEFAULT_PARAMETER(PROCESS_TYPE, ENTITY, PARAMETER_NAME, PARAMETER_VALUE, PARAMETER_DATA_TYPE) VALUES ('OnlineSalary', 'LoanTerms', 'MaxAmount', 30000000, 'String');
INSERT INTO ERIN_BPMS_DEFAULT_PARAMETER(PROCESS_TYPE, ENTITY, PARAMETER_NAME, PARAMETER_VALUE, PARAMETER_DATA_TYPE) VALUES ('OnlineSalary', 'LoanTerms', 'Interest', '15.6%', 'String');
INSERT INTO ERIN_BPMS_DEFAULT_PARAMETER(PROCESS_TYPE, ENTITY, PARAMETER_NAME, PARAMETER_VALUE, PARAMETER_DATA_TYPE) VALUES ('OnlineSalary', 'LoanTerms', 'Charge', '1%', 'String');
INSERT INTO ERIN_BPMS_DEFAULT_PARAMETER(PROCESS_TYPE, ENTITY, PARAMETER_NAME, PARAMETER_VALUE, PARAMETER_DATA_TYPE) VALUES ('OnlineSalary', 'LoanTerms', 'Term', 30, 'String');

-----Scoring-----
INSERT INTO ERIN_BPMS_DEFAULT_PARAMETER(PROCESS_TYPE, ENTITY, PARAMETER_NAME, PARAMETER_VALUE,PARAMETER_DATA_TYPE) VALUES ('OnlineSalary', 'Scoring', 'sector', 'Аж үйлдвэр; Барилга; Уул уурхай; Уул уурхайн туслан гүйцэтгэгч; Үйлчилгээ; Хөдөө аж ахуй; Худалдаа; Бусад', 'LIST');
INSERT INTO ERIN_BPMS_DEFAULT_PARAMETER(PROCESS_TYPE, ENTITY, PARAMETER_NAME, PARAMETER_VALUE,PARAMETER_DATA_TYPE) VALUES ('OnlineSalary', 'Scoring', 'address', 'Улаанбаатар; Орон нутаг', 'LIST');
INSERT INTO ERIN_BPMS_DEFAULT_PARAMETER(PROCESS_TYPE, ENTITY, PARAMETER_NAME, PARAMETER_VALUE, PARAMETER_DATA_TYPE) VALUES ('OnlineSalary', 'Scoring', 'joblessMembers', '0; 1; 2; 2-с их', 'LIST');
INSERT INTO ERIN_BPMS_DEFAULT_PARAMETER(PROCESS_TYPE, ENTITY, PARAMETER_NAME, PARAMETER_VALUE, PARAMETER_DATA_TYPE) VALUES ('OnlineSalary', 'Scoring', 'workspan', '0; 1; 2; 3; 4; 5; 6; 7; 8; 9; 10; 10-с их', 'LIST');
INSERT INTO ERIN_BPMS_DEFAULT_PARAMETER(PROCESS_TYPE, ENTITY, PARAMETER_NAME, PARAMETER_VALUE, PARAMETER_DATA_TYPE) VALUES('OnlineSalary', 'Default', 'defaultBranch', '108', 'String');
INSERT INTO ERIN_BPMS_DEFAULT_PARAMETER(PROCESS_TYPE, ENTITY, PARAMETER_NAME, PARAMETER_VALUE, PARAMETER_DATA_TYPE) VALUES('OnlineSalary', 'Default', 'defaultProduct', 'EB71','String');

INSERT INTO ERIN_BPMS_DEFAULT_PARAMETER(PROCESS_TYPE, ENTITY, PARAMETER_NAME, PARAMETER_VALUE, PARAMETER_DATA_TYPE) VALUES ('BnplLoan', 'LoanTerms', 'MaxAmount', 30000000, 'String');
INSERT INTO ERIN_BPMS_DEFAULT_PARAMETER(PROCESS_TYPE, ENTITY, PARAMETER_NAME, PARAMETER_VALUE, PARAMETER_DATA_TYPE) VALUES ('BnplLoan', 'LoanTerms', 'Term', '45 хоног', 'String');
INSERT INTO ERIN_BPMS_DEFAULT_PARAMETER(PROCESS_TYPE, ENTITY, PARAMETER_NAME, PARAMETER_VALUE, PARAMETER_DATA_TYPE) VALUES('BnplLoan', 'Default', 'defaultProduct', 'EH94','String');
INSERT INTO ERIN_BPMS_DEFAULT_PARAMETER(PROCESS_TYPE, ENTITY, PARAMETER_NAME, PARAMETER_VALUE, PARAMETER_DATA_TYPE) VALUES('BnplLoan', 'Default', 'defaultBranch', '197', 'String');

INSERT INTO ERIN_BPMS_DEFAULT_PARAMETER(PROCESS_TYPE, ENTITY, PARAMETER_NAME, PARAMETER_VALUE, PARAMETER_DATA_TYPE)VALUES ('instantLoan', 'LoanTerms', 'MaxAmount', 500000, 'String');
INSERT INTO ERIN_BPMS_DEFAULT_PARAMETER(PROCESS_TYPE, ENTITY, PARAMETER_NAME, PARAMETER_VALUE, PARAMETER_DATA_TYPE)VALUES ('instantLoan', 'LoanTerms', 'grantMinimumAmount', 50000, 'String');
INSERT INTO ERIN_BPMS_DEFAULT_PARAMETER(PROCESS_TYPE, ENTITY, PARAMETER_NAME, PARAMETER_VALUE, PARAMETER_DATA_TYPE)VALUES ('instantLoan', 'LoanTerms', 'Interest', 2.5, 'String');
INSERT INTO ERIN_BPMS_DEFAULT_PARAMETER(PROCESS_TYPE, ENTITY, PARAMETER_NAME, PARAMETER_VALUE, PARAMETER_DATA_TYPE)VALUES ('instantLoan', 'LoanTerms', 'minInterest', 2.0, 'String');
INSERT INTO ERIN_BPMS_DEFAULT_PARAMETER(PROCESS_TYPE, ENTITY, PARAMETER_NAME, PARAMETER_VALUE, PARAMETER_DATA_TYPE)VALUES ('instantLoan', 'LoanTerms', 'Charge', '1%', 'String');
INSERT INTO ERIN_BPMS_DEFAULT_PARAMETER(PROCESS_TYPE, ENTITY, PARAMETER_NAME, PARAMETER_VALUE, PARAMETER_DATA_TYPE) VALUES ('instantLoan', 'LoanTerms', 'Term', '7; 14; 30', 'LIST');

INSERT INTO ERIN_BPMS_DEFAULT_PARAMETER(PROCESS_TYPE, ENTITY, PARAMETER_NAME, PARAMETER_VALUE, PARAMETER_DATA_TYPE)VALUES ('instantLoan', 'Default', 'defaultProduct', 'EB81', 'String');
INSERT INTO ERIN_BPMS_DEFAULT_PARAMETER(PROCESS_TYPE, ENTITY, PARAMETER_NAME, PARAMETER_VALUE, PARAMETER_DATA_TYPE)VALUES ('instantLoan', 'Default', 'defaultBranch', '197', 'String');
INSERT INTO ERIN_BPMS_DEFAULT_PARAMETER(PROCESS_TYPE, ENTITY, PARAMETER_NAME, PARAMETER_VALUE, PARAMETER_DATA_TYPE) VALUES ('instantLoan', 'LoanTerms', 'CalculationTerm', 1, 'String');


Insert into ERIN_BPMS_DEFAULT_PARAMETER(PROCESS_TYPE,ENTITY,PARAMETER_NAME,PARAMETER_DATA_TYPE,PARAMETER_VALUE) values ('OnlineLeasing','Default','defaultBranch','String', '197');
Insert into ERIN_BPMS_DEFAULT_PARAMETER (PROCESS_TYPE,ENTITY,PARAMETER_NAME,PARAMETER_DATA_TYPE,PARAMETER_VALUE) values ('OnlineLeasing1','Default','defaultProduct','String','EB91');
Insert into ERIN_BPMS_DEFAULT_PARAMETER (PROCESS_TYPE,ENTITY,PARAMETER_NAME,PARAMETER_DATA_TYPE,PARAMETER_VALUE) values ('OnlineLeasing2','Default','defaultProduct','String','EB92');
Insert into ERIN_BPMS_DEFAULT_PARAMETER(PROCESS_TYPE,ENTITY,PARAMETER_NAME,PARAMETER_DATA_TYPE,PARAMETER_VALUE) values ('OnlineLeasing1','LoanTerms','MaxAmount','String', '10000000');
Insert into ERIN_BPMS_DEFAULT_PARAMETER(PROCESS_TYPE,ENTITY,PARAMETER_NAME,PARAMETER_DATA_TYPE,PARAMETER_VALUE) values ('OnlineLeasing1','LoanTerms','grantMinimumAmount','String', '100000');
Insert into ERIN_BPMS_DEFAULT_PARAMETER(PROCESS_TYPE,ENTITY,PARAMETER_NAME,PARAMETER_DATA_TYPE,PARAMETER_VALUE) values ('OnlineLeasing1','LoanTerms','Interest','String', '1');
Insert into ERIN_BPMS_DEFAULT_PARAMETER(PROCESS_TYPE,ENTITY,PARAMETER_NAME,PARAMETER_DATA_TYPE,PARAMETER_VALUE) values ('OnlineLeasing1','LoanTerms','Term','String', '10');


Insert into ERIN_BPMS_DEFAULT_PARAMETER(PROCESS_TYPE,ENTITY,PARAMETER_NAME,PARAMETER_DATA_TYPE,PARAMETER_VALUE) values ('OnlineLeasing2','LoanTerms','MaxAmount','String', '20000000');
Insert into ERIN_BPMS_DEFAULT_PARAMETER(PROCESS_TYPE,ENTITY,PARAMETER_NAME,PARAMETER_DATA_TYPE,PARAMETER_VALUE) values ('OnlineLeasing2','LoanTerms','grantMinimumAmount','String', '200000');
Insert into ERIN_BPMS_DEFAULT_PARAMETER(PROCESS_TYPE,ENTITY,PARAMETER_NAME,PARAMETER_DATA_TYPE,PARAMETER_VALUE) values ('OnlineLeasing2','LoanTerms','Interest','String', '2');
Insert into ERIN_BPMS_DEFAULT_PARAMETER(PROCESS_TYPE,ENTITY,PARAMETER_NAME,PARAMETER_DATA_TYPE,PARAMETER_VALUE) values ('OnlineLeasing2','LoanTerms','Term','String', '20');


---MESSAGES TABLE---
Insert into MESSAGES (ID,KEY,LOCALE,TEXT) values ('dc1e505b-bfc1-4008-8189-8ccd408eb7df','sms.xac.SALARY_CALCULATION_COMPLETE','mn','Таны зээлийн тооцооллыг хийж дууссан тул та Цалингийн зээл цэсээр хандаж шалгана уу. Лавлах: 18001888');
Insert into MESSAGES (ID,KEY,LOCALE,TEXT) values ('8a96588e-8ecb-44f5-b3d1-190328921dab','sms.xac.SALARY_CALCULATION_COMPLETE','en','Estimation of salary loan amount has been completed. Please, refer to Salary loan menu.');
Insert into MESSAGES (ID,KEY,LOCALE,TEXT) values ('8a96588e-8ecb-44f5-b3d1-190328921yu6','email.xac.ONLINE_SALARY_LOAN_CONFIRMED','en','XacBank: Your request for a loan has been successful.');
Insert into MESSAGES (ID,KEY,LOCALE,TEXT) values ('dc1e505b-bfc1-4008-8189-8ccd408eb123','email.xac.ONLINE_SALARY_LOAN_CONFIRMED','mn','ХасБанк: Таны зээлийн хүсэлт амжилттай шийдвэрлэгдлээ.');
INSERT INTO MESSAGES (ID, KEY, LOCALE, TEXT) VALUES ('123423', 'sms.xac.DISBURSMENT_DETAIL_SMS', 'mn', 'Таны [{accountNumber}] дансанд [{loanAmount}] MNT зээлийн олголт хийгдлээ. Огноо: [{disbursedDate}] Лавлах: 18001888');
INSERT INTO MESSAGES (ID, KEY, LOCALE, TEXT) VALUES ('12345634', 'sms.xac.DISBURSMENT_DETAIL_SMS', 'en', ' Loan amount of [{loanAmount}] MNT has been issued to your [{accountNumber}] account. Date: [{disbursedDate}].');

Insert into MESSAGES (ID,KEY,LOCALE,TEXT) values ('dc1e505b-bfc1-4008-8190-8ccd408eb7df','tran.desc.addTransaction.refund','mn','Урьдчилгаа буцаав');
Insert into MESSAGES (ID,KEY,LOCALE,TEXT) values ('8a96588e-8ecb-44f5-b3d2-190328921dab','tran.desc.addTransaction.refund','en','The advance was returned.');
Insert into MESSAGES (ID,KEY,LOCALE,TEXT) values ('c1e505b-bfc1-4008-8189-8ccd40812','bnpl.xac.transaction.error','mn','Уучлаарай, Төлбөр төлөх гүйлгээ амжилтгүй боллоо, та дахин оролдоно уу.');
Insert into MESSAGES (ID,KEY,LOCALE,TEXT) values ('dc1e505b-bfc1-4008-8189-8ccd4044','bnpl.xac.transaction.error','en','Уучлаарай, Төлбөр төлөх гүйлгээ амжилтгүй боллоо, та дахин оролдоно уу.');
Insert into MESSAGES (ID,KEY,LOCALE,TEXT) values ('s1e505b-bfc1-4008-8189-8ccd40812','bnpl.xac.transaction.description','mn','Урьдчилгаа суутгав'); 
Insert into MESSAGES (ID,KEY,LOCALE,TEXT) values ('kl1e505b-bfc1-4008-8189-8ccd4044','bnpl.xac.transaction.description','en','Урьдчилгаа суутгав');
Insert into MESSAGES (ID,KEY,LOCALE,TEXT) values ('bnpl588e-8ecb-44f5-b3d1-190328921yu6','email.subject.xac.BNPL_LOAN_DISBURSED_EMAIL','en','XacBank: Loan contract');
Insert into MESSAGES (ID,KEY,LOCALE,TEXT) values ('bnpl505b-bfc1-4008-8189-8ccd408eb123','email.subject.xac.BNPL_LOAN_DISBURSED_EMAIL','mn','ХасБанк: Зээлийн гэрээ');
Insert into MESSAGES (ID,KEY,LOCALE,TEXT) values ('bnpl505b-bfc1-4008-8189-8ccd408eb7df','sms.xac.BNPL_LOAN_DISBURSED_SMS','mn','Таны худалдан авалт амжилттай хийгдлээ. Таны бүртгэлтэй имэйл хаягаар зээлийн гэрээ илгээгдсэн. ХасБанк ашид хамтдаа');
Insert into MESSAGES (ID,KEY,LOCALE,TEXT) values ('bnpl588e-8ecb-44f5-b3d1-190328921dab','sms.xac.BNPL_LOAN_DISBURSED_SMS','en','Tanii hudaldan avalt amjilttai hiigdlee. Tanii burtgeltei email haygaar zeeliin geree ilgeegdsen. Xacbank ashid hamtdaa');

INSERT INTO MESSAGES (ID, KEY, LOCALE, TEXT) VALUES ('1675741148931', 'sms.xac.INSTANT_LOAN_CALCULATION_COMPLETE', 'mn', 'Таны зээлийн тооцооллыг хийж дууссан тул та Instant LOAN цэсээр хандаж шалгана уу. Лавлах: 18001888');
INSERT INTO MESSAGES (ID, KEY, LOCALE, TEXT) VALUES ('1675741148932', 'sms.xac.INSTANT_LOAN_CALCULATION_COMPLETE', 'en', 'Estimation of salary loan amount has been completed. Please, refer to Instant loan menu.');

Insert into MESSAGES (ID,KEY,LOCALE,TEXT) values ('instant588e-8ecb-44f5-b3d1-190328921yu6','email.subject.xac.INSTANT_LOAN_DISBURSED_EMAIL','en','XacBank: Loan contract');
Insert into MESSAGES (ID,KEY,LOCALE,TEXT) values ('instant505b-bfc1-4008-8189-8ccd408eb123','email.subject.xac.INSTANT_LOAN_DISBURSED_EMAIL','mn','ХасБанк: Зээлийн гэрээ');
Insert into MESSAGES (ID,KEY,LOCALE,TEXT) values ('instant505b-bfc1-4008-8189-8ccd408eb7df','sms.xac.INSTANT_LOAN_DISBURSED_SMS','mn','Таны [{accountNumber}] дансанд [{loanAmount}] MNT зээлийн олголт хийгдлээ. Огноо: [{disbursedDate}] Лавлах: 18001888');
Insert into MESSAGES (ID,KEY,LOCALE,TEXT) values ('instant588e-8ecb-44f5-b3d1-190328921dab','sms.xac.INSTANT_LOAN_DISBURSED_SMS','en','Loan amount of [{loanAmount}] MNT has been issued to your [{accountNumber}] account. Date: [{disbursedDate}].');
INSERT INTO MESSAGES (ID, KEY, LOCALE, TEXT) VALUES ('40350', 'instantLoan.xac.transaction.description', 'mn', 'Сунгах хүсэлт');
INSERT INTO MESSAGES (ID, KEY, LOCALE, TEXT) VALUES ('40351', 'instantLoan.xac.transaction.description', 'en', 'Сунгах хүсэлт');
INSERT INTO MESSAGES (ID, KEY, LOCALE, TEXT) VALUES ('40352', 'instantLoan.xac.pay.intrest.description', 'mn', 'Сунгах хүсэлт');
INSERT INTO MESSAGES (ID, KEY, LOCALE, TEXT) VALUES ('40353', 'instantLoan.xac.pay.intrest.description', 'en', 'Сунгах хүсэлт');

INSERT INTO MESSAGES (ID, KEY, LOCALE, TEXT) VALUES ('onlineLeasingSms01', 'sms.xac.ONLINE_LEASING_CALCULATION_COMPLETE', 'mn', 'Таны зээлийн тооцооллыг хийж дууссан тул та Онлайн лизинг цэсээр хандаж шалгана уу. Лавлах: 18001888');
INSERT INTO MESSAGES (ID, KEY, LOCALE, TEXT) VALUES ('onlineLeasingSms02', 'sms.xac.ONLINE_LEASING_CALCULATION_COMPLETE', 'en', 'Estimation of online leasing amount has been completed. Please, refer to Online leasing menu.');

---ERIN BPMS PRODUCT---

INSERT INTO ERIN_BPMS_PRODUCT (PRODUCT_ID, APPLICATION_CATEGORY, CATEGORY_DESCRIPTION, PRODUCT_DESCRIPTION, TYPE, LOAN_TO_VALUE_RATIO, HAS_COLLATERAL, HAS_INSURANCE, BORROWER_TYPE) 
VALUES ('EB50', 'ONLINE_SALARY', 'Үндсэн төлбөр тэнцүү', 'EB50-365-Цалингийн зээл - Иргэн', 'Цалин', '1', '0', '0', 'Иргэн');

INSERT INTO ERIN_BPMS_PRODUCT (PRODUCT_ID, APPLICATION_CATEGORY, CATEGORY_DESCRIPTION, PRODUCT_DESCRIPTION, TYPE, LOAN_TO_VALUE_RATIO, HAS_COLLATERAL, HAS_INSURANCE, BORROWER_TYPE) 
VALUES ('EB51', 'ONLINE_SALARY', 'Нийт төлбөр тэнцүү', 'EB51-365-Цалингийн зээл -Иргэн –EMI', 'Цалин', '1', '0', '0', 'Иргэн');

INSERT INTO ERIN_BPMS_PRODUCT (PRODUCT_ID, APPLICATION_CATEGORY, CATEGORY_DESCRIPTION, PRODUCT_DESCRIPTION, TYPE, LOAN_TO_VALUE_RATIO, HAS_COLLATERAL, HAS_INSURANCE, BORROWER_TYPE) 
VALUES ('EF50', 'ONLINE_SALARY', 'Үндсэн төлбөр тэнцүү', 'EF50-365-Ажиллагсадын хэрэглээний зээл', 'Цалин', '1', '0', '0', 'Иргэн');

INSERT INTO ERIN_BPMS_PRODUCT (PRODUCT_ID, APPLICATION_CATEGORY, CATEGORY_DESCRIPTION, PRODUCT_DESCRIPTION, TYPE, LOAN_TO_VALUE_RATIO, HAS_COLLATERAL, HAS_INSURANCE, BORROWER_TYPE) 
VALUES ('EB71', 'ONLINE_SALARY', 'Үндсэн төлбөр тэнцүү', 'EB71-Онлайн цалингийн зээл', 'Цалин', '1', '0', '0', 'Иргэн');



---Update scripts----

update ERIN_BPMS_DEFAULT_PARAMETER set parameter_value = '20.4%'where entity = 'LoanTerms' and parameter_name = 'Interest';
update erin_bpms_default_parameter set parameter_value = '0; 1; 2; 2-с их' where parameter_name = 'joblessMembers' and process_type = 'OnlineSalary';
update erin_bpms_default_parameter set parameter_value = '0; 1; 2; 3; 4; 5; 6; 7; 8; 9; 10; 10-с их' where parameter_name = 'workspan' and process_type = 'OnlineSalary';
update erin_bpms_default_parameter set parameter_value = 'Аж үйлдвэр; Барилга; Уул уурхай; Уул уурхайн туслан гүйцэтгэгч; Үйлчилгээ; Хөдөө аж ахуй; Худалдаа; Бусад' where parameter_name = 'sector' and process_type = 'OnlineSalary';

ALTER TABLE ERIN_BPMS_PRODUCT ADD RATE NUMBER;
ALTER TABLE ERIN_BPMS_PRODUCT ADD FREQUENCY NUMBER;

Insert into ERIN_BPMS_PRODUCT (PRODUCT_ID,APPLICATION_CATEGORY,CATEGORY_DESCRIPTION,PRODUCT_DESCRIPTION,TYPE,LOAN_TO_VALUE_RATIO,HAS_COLLATERAL,HAS_INSURANCE,BORROWER_TYPE,RATE,FREQUENCY) values ('EB91','OnlineLeasing1','Үндсэн төлбөр тэнцүү','Онлайн лизинг','Цалин','1',0,0,'Иргэн',null,null);

INSERT INTO ERIN_BPMS_PRODUCT (PRODUCT_ID, APPLICATION_CATEGORY, CATEGORY_DESCRIPTION, PRODUCT_DESCRIPTION, TYPE, LOAN_TO_VALUE_RATIO, HAS_COLLATERAL, HAS_INSURANCE, BORROWER_TYPE, RATE, FREQUENCY) 
VALUES ('EH94', 'BNPL', 'Үндсэн төлбөр тэнцүү', 'EH94-Онлайн зээл', 'Цалин', '1', '0', '0', 'Иргэн','','');

INSERT INTO ERIN_BPMS_PRODUCT (PRODUCT_ID, APPLICATION_CATEGORY, CATEGORY_DESCRIPTION, PRODUCT_DESCRIPTION, TYPE, LOAN_TO_VALUE_RATIO, HAS_COLLATERAL, HAS_INSURANCE, BORROWER_TYPE) VALUES ('EB81', 'INSTANT_LOAN', 'Үндсэн төлбөр тэнцүү', 'EB81-Шуурхай зээл', 'Цалин', '1', '0', '0', 'Иргэн');

UPDATE ERIN_BPMS_PRODUCT SET RATE = 1.5, FREQUENCY = 1 WHERE PRODUCT_ID = 'EB71' AND APPLICATION_CATEGORY = 'ONLINE_SALARY';

UPDATE MESSAGES SET TEXT = 'Tanii [{accountNumber}] dansand [{loanAmount}] MNT zeeliin olgolt hiigdlee. Ognoo: [{disbursedDate}] Lavlah: 18001888' WHERE ID = '123423' AND KEY = 'sms.xac.DISBURSMENT_DETAIL_SMS';

UPDATE MESSAGES SET TEXT = 'Tanii zeeliin tootsoolliig hiij duussan tul ta 24 tsagiin dotor Tsalingiin zeel tseseer handaj shalgana u. Lavlah: 18001888' WHERE ID = 'dc1e505b-bfc1-4008-8189-8ccd408eb7df' AND KEY = 'sms.xac.SALARY_CALCULATION_COMPLETE';

update MESSAGES set TEXT = 'Шимтгэл суутгав.' where KEY = 'instantLoan.xac.transaction.description';
update MESSAGES set TEXT = 'Хүү төлөв.' where KEY = 'instantLoan.xac.pay.intrest.description';


