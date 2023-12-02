ALTER SESSION SET CURRENT_SCHEMA = ERIN;
UPDATE ERIN_BPMS_PRODUCT SET HAS_INSURANCE = 0  WHERE product_id = 'EB50' AND application_category = 'CONSUMER';
UPDATE ERIN_BPMS_PRODUCT SET HAS_INSURANCE = 0  WHERE product_id = 'EF50' AND application_category = 'CONSUMER';
UPDATE ERIN_BPMS_PRODUCT SET HAS_INSURANCE = 0  WHERE product_id = 'EA50' AND application_category = 'CONSUMER';
UPDATE ERIN_BPMS_PRODUCT SET HAS_INSURANCE = 0  WHERE product_id = 'EH53' AND application_category = 'CONSUMER';
UPDATE ERIN_BPMS_PRODUCT SET HAS_INSURANCE = 0  WHERE product_id = 'EG50' AND application_category = 'CONSUMER';
UPDATE ERIN_BPMS_PRODUCT SET HAS_INSURANCE = 0  WHERE product_id = 'EA52' AND application_category = 'CONSUMER';
UPDATE ERIN_BPMS_PRODUCT SET HAS_INSURANCE = 1  WHERE product_id = 'EH90' AND application_category = 'CONSUMER';
UPDATE ERIN_BPMS_PRODUCT SET HAS_INSURANCE = 1  WHERE product_id = 'EH83' AND application_category = 'CONSUMER';
UPDATE ERIN_BPMS_PRODUCT SET HAS_INSURANCE = 1  WHERE product_id = 'EH88' AND application_category = 'CONSUMER';
UPDATE ERIN_BPMS_PRODUCT SET HAS_INSURANCE = 1  WHERE product_id = 'DJ83' AND application_category = 'CONSUMER';
UPDATE ERIN_BPMS_PRODUCT SET HAS_INSURANCE = 0  WHERE product_id = 'EH55' AND application_category = 'CONSUMER';
UPDATE ERIN_BPMS_PRODUCT SET HAS_INSURANCE = 0  WHERE product_id = 'EH56' AND application_category = 'CONSUMER';
UPDATE ERIN_BPMS_PRODUCT SET HAS_INSURANCE = 0  WHERE product_id = 'EB51' AND application_category = 'CONSUMER';
UPDATE ERIN_BPMS_PRODUCT SET HAS_INSURANCE = 0  WHERE product_id = 'EA51' AND application_category = 'CONSUMER';
UPDATE ERIN_BPMS_PRODUCT SET HAS_INSURANCE = 0  WHERE product_id = 'EH54' AND application_category = 'CONSUMER';
UPDATE ERIN_BPMS_PRODUCT SET HAS_INSURANCE = 1  WHERE product_id = 'EH89' AND application_category = 'CONSUMER';
UPDATE ERIN_BPMS_PRODUCT SET HAS_INSURANCE = 1  WHERE product_id = 'DJ87' AND application_category = 'CONSUMER';
UPDATE ERIN_BPMS_PRODUCT SET HAS_INSURANCE = 0  WHERE product_id = 'EA53' AND application_category = 'CONSUMER';
UPDATE ERIN_BPMS_PRODUCT SET HAS_INSURANCE = 0  WHERE product_id = 'EH57' AND application_category = 'CONSUMER';

--Consumer loan product ltv update--

UPDATE ERIN_BPMS_PRODUCT SET LOAN_TO_VALUE_RATIO = '1' where PRODUCT_ID = 'EH53' AND APPLICATION_CATEGORY='CONSUMER';
UPDATE ERIN_BPMS_PRODUCT SET LOAN_TO_VALUE_RATIO = '1' where PRODUCT_ID = 'EH90' AND APPLICATION_CATEGORY='CONSUMER';
UPDATE ERIN_BPMS_PRODUCT SET LOAN_TO_VALUE_RATIO = '1' where PRODUCT_ID = 'EH83' AND APPLICATION_CATEGORY='CONSUMER';
UPDATE ERIN_BPMS_PRODUCT SET LOAN_TO_VALUE_RATIO = '1' where PRODUCT_ID = 'EH88' AND APPLICATION_CATEGORY='CONSUMER';
UPDATE ERIN_BPMS_PRODUCT SET LOAN_TO_VALUE_RATIO = '1' where PRODUCT_ID = 'DJ83' AND APPLICATION_CATEGORY='CONSUMER';
UPDATE ERIN_BPMS_PRODUCT SET LOAN_TO_VALUE_RATIO = '1' where PRODUCT_ID = 'EH55' AND APPLICATION_CATEGORY='CONSUMER';
UPDATE ERIN_BPMS_PRODUCT SET LOAN_TO_VALUE_RATIO = '1' where PRODUCT_ID = 'EH56' AND APPLICATION_CATEGORY='CONSUMER';
UPDATE ERIN_BPMS_PRODUCT SET LOAN_TO_VALUE_RATIO = '1' where PRODUCT_ID = 'EH54' AND APPLICATION_CATEGORY='CONSUMER';
UPDATE ERIN_BPMS_PRODUCT SET LOAN_TO_VALUE_RATIO = '1' where PRODUCT_ID = 'EH89' AND APPLICATION_CATEGORY='CONSUMER';
UPDATE ERIN_BPMS_PRODUCT SET LOAN_TO_VALUE_RATIO = '1' where PRODUCT_ID = 'DJ87' AND APPLICATION_CATEGORY='CONSUMER';
UPDATE ERIN_BPMS_PRODUCT SET LOAN_TO_VALUE_RATIO = '1' where PRODUCT_ID = 'EA53' AND APPLICATION_CATEGORY='CONSUMER';
UPDATE ERIN_BPMS_PRODUCT SET LOAN_TO_VALUE_RATIO = '1' where PRODUCT_ID = 'EH57' AND APPLICATION_CATEGORY='CONSUMER';

--Micro loan product ltv update---

UPDATE ERIN_BPMS_PRODUCT SET LOAN_TO_VALUE_RATIO = '1' where PRODUCT_ID = 'EH53' AND APPLICATION_CATEGORY='SMALL_MICRO';
UPDATE ERIN_BPMS_PRODUCT SET LOAN_TO_VALUE_RATIO = '1' where PRODUCT_ID = 'EG50' AND APPLICATION_CATEGORY='SMALL_MICRO';
UPDATE ERIN_BPMS_PRODUCT SET LOAN_TO_VALUE_RATIO = '1' where PRODUCT_ID = 'EA52' AND APPLICATION_CATEGORY='SMALL_MICRO';
UPDATE ERIN_BPMS_PRODUCT SET LOAN_TO_VALUE_RATIO = '1' where PRODUCT_ID = 'EH90' AND APPLICATION_CATEGORY='SMALL_MICRO';
UPDATE ERIN_BPMS_PRODUCT SET LOAN_TO_VALUE_RATIO = '1' where PRODUCT_ID = 'EH83' AND APPLICATION_CATEGORY='SMALL_MICRO';
UPDATE ERIN_BPMS_PRODUCT SET LOAN_TO_VALUE_RATIO = '1' where PRODUCT_ID = 'EH88' AND APPLICATION_CATEGORY='SMALL_MICRO';
UPDATE ERIN_BPMS_PRODUCT SET LOAN_TO_VALUE_RATIO = '1' where PRODUCT_ID = 'DJ83' AND APPLICATION_CATEGORY='SMALL_MICRO';
UPDATE ERIN_BPMS_PRODUCT SET LOAN_TO_VALUE_RATIO = '1' where PRODUCT_ID = 'EH55' AND APPLICATION_CATEGORY='SMALL_MICRO';
UPDATE ERIN_BPMS_PRODUCT SET LOAN_TO_VALUE_RATIO = '1' where PRODUCT_ID = 'EH56' AND APPLICATION_CATEGORY='SMALL_MICRO';
UPDATE ERIN_BPMS_PRODUCT SET LOAN_TO_VALUE_RATIO = '1' where PRODUCT_ID = 'DJ85' AND APPLICATION_CATEGORY='SMALL_MICRO';

UPDATE ERIN_BPMS_PRODUCT SET LOAN_TO_VALUE_RATIO = '.75' WHERE PRODUCT_ID= 'ED52' AND APPLICATION_CATEGORY = 'MORTGAGE';
UPDATE ERIN_BPMS_PRODUCT SET LOAN_TO_VALUE_RATIO = '.85' WHERE PRODUCT_ID= 'EF21' AND APPLICATION_CATEGORY = 'MORTGAGE';

 -------UPDATE LTV-------
UPDATE ERIN_BPMS_PRODUCT SET LOAN_TO_VALUE_RATIO = '.80' where PRODUCT_ID = 'DB62' AND APPLICATION_CATEGORY='SMALL_MICRO';
UPDATE ERIN_BPMS_PRODUCT SET LOAN_TO_VALUE_RATIO = '.80' where PRODUCT_ID = 'DB50' AND APPLICATION_CATEGORY='SMALL_MICRO';
UPDATE ERIN_BPMS_PRODUCT SET LOAN_TO_VALUE_RATIO = '.80' where PRODUCT_ID = 'DB51' AND APPLICATION_CATEGORY='SMALL_MICRO';
UPDATE ERIN_BPMS_PRODUCT SET LOAN_TO_VALUE_RATIO = '.80' where PRODUCT_ID = 'DB60' AND APPLICATION_CATEGORY='SMALL_MICRO';
UPDATE ERIN_BPMS_PRODUCT SET LOAN_TO_VALUE_RATIO = '.80' where PRODUCT_ID = 'DB63' AND APPLICATION_CATEGORY='SMALL_MICRO';
UPDATE ERIN_BPMS_PRODUCT SET LOAN_TO_VALUE_RATIO = '.75' where PRODUCT_ID = 'DD55' AND APPLICATION_CATEGORY='SMALL_MICRO';
UPDATE ERIN_BPMS_PRODUCT SET LOAN_TO_VALUE_RATIO = '.75' where PRODUCT_ID = 'DD65' AND APPLICATION_CATEGORY='SMALL_MICRO';
UPDATE ERIN_BPMS_PRODUCT SET LOAN_TO_VALUE_RATIO = '.75' where PRODUCT_ID = 'DD64' AND APPLICATION_CATEGORY='SMALL_MICRO';


-----UPDATE CATEGORY DESCRIPTION-----
UPDATE ERIN_BPMS_PRODUCT SET CATEGORY_DESCRIPTION = 'Үндсэн төлбөр тэнцүү' WHERE product_id = 'DA52' AND application_category = 'SMALL_MICRO';
UPDATE ERIN_BPMS_PRODUCT SET CATEGORY_DESCRIPTION = 'Үндсэн төлбөр тэнцүү' WHERE product_id = 'DA62' AND application_category = 'SMALL_MICRO';
UPDATE ERIN_BPMS_PRODUCT SET CATEGORY_DESCRIPTION = 'Үндсэн төлбөр тэнцүү' WHERE product_id = 'DB50' AND application_category = 'SMALL_MICRO';
UPDATE ERIN_BPMS_PRODUCT SET CATEGORY_DESCRIPTION = 'Үндсэн төлбөр тэнцүү' WHERE product_id = 'DB51' AND application_category = 'SMALL_MICRO';
UPDATE ERIN_BPMS_PRODUCT SET CATEGORY_DESCRIPTION = 'Үндсэн төлбөр тэнцүү' WHERE product_id = 'DB60' AND application_category = 'SMALL_MICRO';
UPDATE ERIN_BPMS_PRODUCT SET CATEGORY_DESCRIPTION = 'Үндсэн төлбөр тэнцүү' WHERE product_id = 'DA53' AND application_category = 'SMALL_MICRO';
UPDATE ERIN_BPMS_PRODUCT SET CATEGORY_DESCRIPTION = 'Үндсэн төлбөр тэнцүү' WHERE product_id = 'DA63' AND application_category = 'SMALL_MICRO';
UPDATE ERIN_BPMS_PRODUCT SET CATEGORY_DESCRIPTION = 'Үндсэн төлбөр тэнцүү' WHERE product_id = 'DA50' AND application_category = 'SMALL_MICRO';
UPDATE ERIN_BPMS_PRODUCT SET CATEGORY_DESCRIPTION = 'Үндсэн төлбөр тэнцүү' WHERE product_id = 'DD54' AND application_category = 'SMALL_MICRO';
UPDATE ERIN_BPMS_PRODUCT SET CATEGORY_DESCRIPTION = 'Үндсэн төлбөр тэнцүү' WHERE product_id = 'DA65' AND application_category = 'SMALL_MICRO';
UPDATE ERIN_BPMS_PRODUCT SET CATEGORY_DESCRIPTION = 'Үндсэн төлбөр тэнцүү' WHERE product_id = 'DD65' AND application_category = 'SMALL_MICRO';
UPDATE ERIN_BPMS_PRODUCT SET CATEGORY_DESCRIPTION = 'Үндсэн төлбөр тэнцүү' WHERE product_id = 'DJ85' AND application_category = 'SMALL_MICRO';
UPDATE ERIN_BPMS_PRODUCT SET CATEGORY_DESCRIPTION = 'Үндсэн төлбөр тэнцүү' WHERE product_id = 'DB63' AND application_category = 'SMALL_MICRO';
UPDATE ERIN_BPMS_PRODUCT SET CATEGORY_DESCRIPTION = 'Үндсэн төлбөр тэнцүү' WHERE product_id = 'DD55' AND application_category = 'SMALL_MICRO';
UPDATE ERIN_BPMS_PRODUCT SET CATEGORY_DESCRIPTION = 'Үндсэн төлбөр тэнцүү' WHERE product_id = 'DA66' AND application_category = 'SMALL_MICRO';
UPDATE ERIN_BPMS_PRODUCT SET CATEGORY_DESCRIPTION = 'Үндсэн төлбөр тэнцүү' WHERE product_id = 'DD64' AND application_category = 'SMALL_MICRO';
UPDATE ERIN_BPMS_PRODUCT SET CATEGORY_DESCRIPTION = 'Үндсэн төлбөр тэнцүү' WHERE product_id = 'DB62' AND application_category = 'SMALL_MICRO';
UPDATE ERIN_BPMS_PRODUCT SET CATEGORY_DESCRIPTION = 'Үндсэн төлбөр тэнцүү' WHERE product_id = 'EH53' AND application_category = 'SMALL_MICRO';
UPDATE ERIN_BPMS_PRODUCT SET CATEGORY_DESCRIPTION = 'Үндсэн төлбөр тэнцүү' WHERE product_id = 'EG50' AND application_category = 'SMALL_MICRO';
UPDATE ERIN_BPMS_PRODUCT SET CATEGORY_DESCRIPTION = 'Үндсэн төлбөр тэнцүү' WHERE product_id = 'EA52' AND application_category = 'SMALL_MICRO';
UPDATE ERIN_BPMS_PRODUCT SET CATEGORY_DESCRIPTION = 'Үндсэн төлбөр тэнцүү' WHERE product_id = 'EH90' AND application_category = 'SMALL_MICRO';
UPDATE ERIN_BPMS_PRODUCT SET CATEGORY_DESCRIPTION = 'Үндсэн төлбөр тэнцүү' WHERE product_id = 'EH83' AND application_category = 'SMALL_MICRO';
UPDATE ERIN_BPMS_PRODUCT SET CATEGORY_DESCRIPTION = 'Үндсэн төлбөр тэнцүү' WHERE product_id = 'EH88' AND application_category = 'SMALL_MICRO';
UPDATE ERIN_BPMS_PRODUCT SET CATEGORY_DESCRIPTION = 'Үндсэн төлбөр тэнцүү' WHERE product_id = 'DJ83' AND application_category = 'SMALL_MICRO';
UPDATE ERIN_BPMS_PRODUCT SET CATEGORY_DESCRIPTION = 'Үндсэн төлбөр тэнцүү' WHERE product_id = 'EH55' AND application_category = 'SMALL_MICRO';
UPDATE ERIN_BPMS_PRODUCT SET CATEGORY_DESCRIPTION = 'Үндсэн төлбөр тэнцүү' WHERE product_id = 'EH56' AND application_category = 'SMALL_MICRO';
UPDATE ERIN_BPMS_PRODUCT SET CATEGORY_DESCRIPTION = 'Үндсэн төлбөр тэнцүү' WHERE product_id = 'DB52' AND application_category = 'SMALL_MICRO';
UPDATE ERIN_BPMS_PRODUCT SET CATEGORY_DESCRIPTION = 'Үндсэн төлбөр тэнцүү' WHERE product_id = 'DD53' AND application_category = 'SMALL_MICRO';
UPDATE ERIN_BPMS_PRODUCT SET CATEGORY_DESCRIPTION = 'Үндсэн төлбөр тэнцүү' WHERE product_id = 'DD69' AND application_category = 'SMALL_MICRO';
UPDATE ERIN_BPMS_PRODUCT SET CATEGORY_DESCRIPTION = 'Үндсэн төлбөр тэнцүү' WHERE product_id = 'DD70' AND application_category = 'SMALL_MICRO';
UPDATE ERIN_BPMS_PRODUCT SET CATEGORY_DESCRIPTION = 'Үндсэн төлбөр тэнцүү' WHERE product_id = 'DD71' AND application_category = 'SMALL_MICRO';
UPDATE ERIN_BPMS_PRODUCT SET CATEGORY_DESCRIPTION = 'Үндсэн төлбөр тэнцүү' WHERE product_id = 'DD72' AND application_category = 'SMALL_MICRO';


UPDATE ERIN_BPMS_PRODUCT SET CATEGORY_DESCRIPTION = 'Нийт төлбөр тэнцүү' WHERE product_id = 'DD77' AND application_category = 'SMALL_MICRO';
UPDATE ERIN_BPMS_PRODUCT SET CATEGORY_DESCRIPTION = 'Нийт төлбөр тэнцүү' WHERE product_id = 'DD78' AND application_category = 'SMALL_MICRO';
UPDATE ERIN_BPMS_PRODUCT SET CATEGORY_DESCRIPTION = 'Нийт төлбөр тэнцүү' WHERE product_id = 'DD79' AND application_category = 'SMALL_MICRO';
UPDATE ERIN_BPMS_PRODUCT SET CATEGORY_DESCRIPTION = 'Нийт төлбөр тэнцүү' WHERE product_id = 'DD80' AND application_category = 'SMALL_MICRO';
UPDATE ERIN_BPMS_PRODUCT SET CATEGORY_DESCRIPTION = 'Нийт төлбөр тэнцүү' WHERE product_id = 'EH89' AND application_category = 'SMALL_MICRO';
UPDATE ERIN_BPMS_PRODUCT SET CATEGORY_DESCRIPTION = 'Нийт төлбөр тэнцүү' WHERE product_id = 'DJ87' AND application_category = 'SMALL_MICRO';
UPDATE ERIN_BPMS_PRODUCT SET CATEGORY_DESCRIPTION = 'Нийт төлбөр тэнцүү' WHERE product_id = 'EA53' AND application_category = 'SMALL_MICRO';
UPDATE ERIN_BPMS_PRODUCT SET CATEGORY_DESCRIPTION = 'Нийт төлбөр тэнцүү' WHERE product_id = 'EH57' AND application_category = 'SMALL_MICRO';

UPDATE ERIN_BPMS_PRODUCT SET BORROWER_TYPE = 'Иргэн' WHERE product_id = 'DD55' AND application_category = 'SMALL_MICRO';

update erin_bpms_product set borrower_type = 'Иргэн' where product_id = 'DA52' and application_category = 'SMALL_MICRO';
update erin_bpms_product set borrower_type = 'ААН' where product_id = 'DA62' and application_category = 'SMALL_MICRO';
update erin_bpms_product set borrower_type = 'Иргэн' where product_id = 'DB50' and application_category = 'SMALL_MICRO';
update erin_bpms_product set borrower_type = 'Иргэн' where product_id = 'DB51' and application_category = 'SMALL_MICRO';
update erin_bpms_product set borrower_type = 'ААН' where product_id = 'DB60' and application_category = 'SMALL_MICRO';

update erin_bpms_product set borrower_type = 'Иргэн' where product_id = 'DA53' and application_category = 'SMALL_MICRO';
update erin_bpms_product set borrower_type = 'ААН' where product_id = 'DA63' and application_category = 'SMALL_MICRO';
update erin_bpms_product set borrower_type = 'Иргэн' where product_id = 'DA50' and application_category = 'SMALL_MICRO';
update erin_bpms_product set borrower_type = 'Иргэн' where product_id = 'DD54' and application_category = 'SMALL_MICRO';
update erin_bpms_product set borrower_type = 'ААН' where product_id = 'DA65' and application_category = 'SMALL_MICRO';

update erin_bpms_product set borrower_type = 'ААН' where product_id = 'DD65' and application_category = 'SMALL_MICRO';
update erin_bpms_product set borrower_type = 'ААН' where product_id = 'DJ85' and application_category = 'SMALL_MICRO';
update erin_bpms_product set borrower_type = 'ААН' where product_id = 'DB63' and application_category = 'SMALL_MICRO';
update erin_bpms_product set borrower_type = 'ААН' where product_id = 'DD55' and application_category = 'SMALL_MICRO';
update erin_bpms_product set borrower_type = 'ААН' where product_id = 'DA66' and application_category = 'SMALL_MICRO';

update erin_bpms_product set borrower_type = 'ААН' where product_id = 'DD64' and application_category = 'SMALL_MICRO';
update erin_bpms_product set borrower_type = 'Иргэн' where product_id = 'DB62' and application_category = 'SMALL_MICRO';
update erin_bpms_product set borrower_type = 'Иргэн' where product_id = 'EH53' and application_category = 'SMALL_MICRO';
update erin_bpms_product set borrower_type = 'Иргэн' where product_id = 'EG50' and application_category = 'SMALL_MICRO';
update erin_bpms_product set borrower_type = 'Иргэн' where product_id = 'EA52' and application_category = 'SMALL_MICRO';

update erin_bpms_product set borrower_type = 'Иргэн' where product_id = 'EH90' and application_category = 'SMALL_MICRO';
update erin_bpms_product set borrower_type = 'Иргэн' where product_id = 'EH83' and application_category = 'SMALL_MICRO';
update erin_bpms_product set borrower_type = 'Иргэн' where product_id = 'EH88' and application_category = 'SMALL_MICRO';
update erin_bpms_product set borrower_type = 'ААН' where product_id = 'DJ83' and application_category = 'SMALL_MICRO';
update erin_bpms_product set borrower_type = 'Иргэн' where product_id = 'EH55' and application_category = 'SMALL_MICRO';

update erin_bpms_product set borrower_type = 'Иргэн' where product_id = 'EH56' and application_category = 'SMALL_MICRO';
update erin_bpms_product set borrower_type = 'Иргэн' where product_id = 'DB52' and application_category = 'SMALL_MICRO';
update erin_bpms_product set borrower_type = 'Иргэн' where product_id = 'DD53' and application_category = 'SMALL_MICRO';
update erin_bpms_product set borrower_type = 'ААН' where product_id = 'DD69' and application_category = 'SMALL_MICRO';
update erin_bpms_product set borrower_type = 'ААН' where product_id = 'DD70' and application_category = 'SMALL_MICRO';

update erin_bpms_product set borrower_type = 'Иргэн' where product_id = 'DD71' and application_category = 'SMALL_MICRO';
update erin_bpms_product set borrower_type = 'Иргэн' where product_id = 'DD72' and application_category = 'SMALL_MICRO';
update erin_bpms_product set borrower_type = 'ААН' where product_id = 'DD77' and application_category = 'SMALL_MICRO';
update erin_bpms_product set borrower_type = 'ААН' where product_id = 'DD78' and application_category = 'SMALL_MICRO';
update erin_bpms_product set borrower_type = 'Иргэн' where product_id = 'DD79' and application_category = 'SMALL_MICRO';

update erin_bpms_product set borrower_type = 'Иргэн' where product_id = 'DD80' and application_category = 'SMALL_MICRO';
update erin_bpms_product set borrower_type = 'Иргэн' where product_id = 'EH89' and application_category = 'SMALL_MICRO';
update erin_bpms_product set borrower_type = 'ААН' where product_id = 'DJ87' and application_category = 'SMALL_MICRO';
update erin_bpms_product set borrower_type = 'Иргэн' where product_id = 'EA53' and application_category = 'SMALL_MICRO';
update erin_bpms_product set borrower_type = 'Иргэн' where product_id = 'EH57' and application_category = 'SMALL_MICRO';

UPDATE ERIN_BPMS_PRODUCT SET LOAN_TO_VALUE_RATIO = '.75' WHERE PRODUCT_ID= 'ED52' AND APPLICATION_CATEGORY = 'MORTGAGE';
UPDATE ERIN_BPMS_PRODUCT SET LOAN_TO_VALUE_RATIO = '.85' WHERE PRODUCT_ID= 'EF21' AND APPLICATION_CATEGORY = 'MORTGAGE';

UPDATE ERIN_BPMS_PRODUCT SET LOAN_TO_VALUE_RATIO = '.9' WHERE PRODUCT_ID= 'EH90' AND APPLICATION_CATEGORY = 'CONSUMER';
UPDATE ERIN_BPMS_PRODUCT SET LOAN_TO_VALUE_RATIO = '.7' WHERE PRODUCT_ID= 'EH88' AND APPLICATION_CATEGORY = 'CONSUMER';
UPDATE ERIN_BPMS_PRODUCT SET LOAN_TO_VALUE_RATIO = '.75' WHERE PRODUCT_ID= 'EA50' AND APPLICATION_CATEGORY = 'CONSUMER';
UPDATE ERIN_BPMS_PRODUCT SET LOAN_TO_VALUE_RATIO = '1' WHERE PRODUCT_ID= 'EA52' AND APPLICATION_CATEGORY = 'CONSUMER';
UPDATE ERIN_BPMS_PRODUCT SET LOAN_TO_VALUE_RATIO = '.9' WHERE PRODUCT_ID= 'EH89' AND APPLICATION_CATEGORY = 'CONSUMER';
UPDATE ERIN_BPMS_PRODUCT SET LOAN_TO_VALUE_RATIO = '.75' WHERE PRODUCT_ID= 'EA51' AND APPLICATION_CATEGORY = 'CONSUMER';

UPDATE ERIN_BPMS_PRODUCT SET LOAN_TO_VALUE_RATIO = '.7' WHERE PRODUCT_ID= 'EH88' AND APPLICATION_CATEGORY = 'SMALL_MICRO';
UPDATE ERIN_BPMS_PRODUCT SET LOAN_TO_VALUE_RATIO = '.9' WHERE PRODUCT_ID= 'EH89' AND APPLICATION_CATEGORY = 'SMALL_MICRO';
UPDATE ERIN_BPMS_PRODUCT SET LOAN_TO_VALUE_RATIO = '.9' WHERE PRODUCT_ID= 'EH90' AND APPLICATION_CATEGORY = 'SMALL_MICRO';
UPDATE ERIN_BPMS_PRODUCT SET LOAN_TO_VALUE_RATIO = '.7' WHERE PRODUCT_ID= 'DJ85' AND APPLICATION_CATEGORY = 'SMALL_MICRO';
UPDATE ERIN_BPMS_PRODUCT SET LOAN_TO_VALUE_RATIO = '.9' WHERE PRODUCT_ID= 'DJ87' AND APPLICATION_CATEGORY = 'SMALL_MICRO';
UPDATE ERIN_BPMS_PRODUCT SET LOAN_TO_VALUE_RATIO = '.9' WHERE PRODUCT_ID= 'DJ88' AND APPLICATION_CATEGORY = 'SMALL_MICRO';



UPDATE ERIN_BPMS_PRODUCT SET BORROWER_TYPE = 'Иргэн' WHERE APPLICATION_CATEGORY = 'CONSUMER';
UPDATE ERIN_BPMS_PRODUCT SET BORROWER_TYPE = 'Иргэн' WHERE APPLICATION_CATEGORY = 'MORTGAGE';


update ERIN_BPMS_DEFAULT_PARAMETER set parameter_value = '{"1": {"minimumWage": 632500, "socialInsurancePremiums": 0.115},"2": {"minimumWage": 632500, "socialInsurancePremiums": 0.115},"3": {"minimumWage": 632500, "socialInsurancePremiums": 0.115},"4": {"minimumWage": 632500, "socialInsurancePremiums": 0.115},"5": {"minimumWage": 632500, "socialInsurancePremiums": 0.115},"6": {"minimumWage": 632500, "socialInsurancePremiums": 0.115},"7": {"minimumWage": 632500, "socialInsurancePremiums": 0.115},"8": {"minimumWage": 632500, "socialInsurancePremiums": 0.115},"9": {"minimumWage": 632500, "socialInsurancePremiums": 0.115},"10": {"minimumWage": 632500, "socialInsurancePremiums": 0.115},"11": {"minimumWage": 632500, "socialInsurancePremiums": 0.115},"12": {"minimumWage": 632500, "socialInsurancePremiums": 0.115}}' where parameter_name = '2023' and entity = 'SalaryCalculation' and PROCESS_TYPE = 'ConsumptionLoan';
  update ERIN_BPMS_DEFAULT_PARAMETER set parameter_value = '{"1": {"minimumWage": 632500, "socialInsurancePremiums": 0.115},"2": {"minimumWage": 632500, "socialInsurancePremiums": 0.115},"3": {"minimumWage": 632500, "socialInsurancePremiums": 0.115},"4": {"minimumWage": 632500, "socialInsurancePremiums": 0.115},"5": {"minimumWage": 632500, "socialInsurancePremiums": 0.115},"6": {"minimumWage": 632500, "socialInsurancePremiums": 0.115},"7": {"minimumWage": 632500, "socialInsurancePremiums": 0.115},"8": {"minimumWage": 632500, "socialInsurancePremiums": 0.115},"9": {"minimumWage": 632500, "socialInsurancePremiums": 0.115},"10": {"minimumWage": 632500, "socialInsurancePremiums": 0.115},"11": {"minimumWage": 632500, "socialInsurancePremiums": 0.115},"12": {"minimumWage": 632500, "socialInsurancePremiums": 0.115}}' where parameter_name = '2024' and entity = 'SalaryCalculation' and PROCESS_TYPE = 'ConsumptionLoan';
  update ERIN_BPMS_DEFAULT_PARAMETER set parameter_value = '{"1": {"minimumWage": 632500, "socialInsurancePremiums": 0.115},"2": {"minimumWage": 632500, "socialInsurancePremiums": 0.115},"3": {"minimumWage": 632500, "socialInsurancePremiums": 0.115},"4": {"minimumWage": 632500, "socialInsurancePremiums": 0.115},"5": {"minimumWage": 632500, "socialInsurancePremiums": 0.115},"6": {"minimumWage": 632500, "socialInsurancePremiums": 0.115},"7": {"minimumWage": 632500, "socialInsurancePremiums": 0.115},"8": {"minimumWage": 632500, "socialInsurancePremiums": 0.115},"9": {"minimumWage": 632500, "socialInsurancePremiums": 0.115},"10": {"minimumWage": 632500, "socialInsurancePremiums": 0.115},"11": {"minimumWage": 632500, "socialInsurancePremiums": 0.115},"12": {"minimumWage": 632500, "socialInsurancePremiums": 0.115}}' where parameter_name = '2025' and entity = 'SalaryCalculation' and PROCESS_TYPE = 'ConsumptionLoan';
  update ERIN_BPMS_DEFAULT_PARAMETER set parameter_value = '{"1": {"minimumWage": 632500, "socialInsurancePremiums": 0.115},"2": {"minimumWage": 632500, "socialInsurancePremiums": 0.115},"3": {"minimumWage": 632500, "socialInsurancePremiums": 0.115},"4": {"minimumWage": 632500, "socialInsurancePremiums": 0.115},"5": {"minimumWage": 632500, "socialInsurancePremiums": 0.115},"6": {"minimumWage": 632500, "socialInsurancePremiums": 0.115},"7": {"minimumWage": 632500, "socialInsurancePremiums": 0.115},"8": {"minimumWage": 632500, "socialInsurancePremiums": 0.115},"9": {"minimumWage": 632500, "socialInsurancePremiums": 0.115},"10": {"minimumWage": 632500, "socialInsurancePremiums": 0.115},"11": {"minimumWage": 632500, "socialInsurancePremiums": 0.115},"12": {"minimumWage": 632500, "socialInsurancePremiums": 0.115}}' where parameter_name = '2026' and entity = 'SalaryCalculation' and PROCESS_TYPE = 'ConsumptionLoan';
  update ERIN_BPMS_DEFAULT_PARAMETER set parameter_value = '{"1": {"minimumWage": 632500, "socialInsurancePremiums": 0.115},"2": {"minimumWage": 632500, "socialInsurancePremiums": 0.115},"3": {"minimumWage": 632500, "socialInsurancePremiums": 0.115},"4": {"minimumWage": 632500, "socialInsurancePremiums": 0.115},"5": {"minimumWage": 632500, "socialInsurancePremiums": 0.115},"6": {"minimumWage": 632500, "socialInsurancePremiums": 0.115},"7": {"minimumWage": 632500, "socialInsurancePremiums": 0.115},"8": {"minimumWage": 632500, "socialInsurancePremiums": 0.115},"9": {"minimumWage": 632500, "socialInsurancePremiums": 0.115},"10": {"minimumWage": 632500, "socialInsurancePremiums": 0.115},"11": {"minimumWage": 632500, "socialInsurancePremiums": 0.115},"12": {"minimumWage": 632500, "socialInsurancePremiums": 0.115}}' where parameter_name = '2027' and entity = 'SalaryCalculation' and PROCESS_TYPE = 'ConsumptionLoan';
  update ERIN_BPMS_DEFAULT_PARAMETER set parameter_value = '{"1": {"minimumWage": 632500, "socialInsurancePremiums": 0.115},"2": {"minimumWage": 632500, "socialInsurancePremiums": 0.115},"3": {"minimumWage": 632500, "socialInsurancePremiums": 0.115},"4": {"minimumWage": 632500, "socialInsurancePremiums": 0.115},"5": {"minimumWage": 632500, "socialInsurancePremiums": 0.115},"6": {"minimumWage": 632500, "socialInsurancePremiums": 0.115},"7": {"minimumWage": 632500, "socialInsurancePremiums": 0.115},"8": {"minimumWage": 632500, "socialInsurancePremiums": 0.115},"9": {"minimumWage": 632500, "socialInsurancePremiums": 0.115},"10": {"minimumWage": 632500, "socialInsurancePremiums": 0.115},"11": {"minimumWage": 632500, "socialInsurancePremiums": 0.115},"12": {"minimumWage": 632500, "socialInsurancePremiums": 0.115}}' where parameter_name = '2028' and entity = 'SalaryCalculation' and PROCESS_TYPE = 'ConsumptionLoan';
  update ERIN_BPMS_DEFAULT_PARAMETER set parameter_value = '{"1": {"minimumWage": 632500, "socialInsurancePremiums": 0.115},"2": {"minimumWage": 632500, "socialInsurancePremiums": 0.115},"3": {"minimumWage": 632500, "socialInsurancePremiums": 0.115},"4": {"minimumWage": 632500, "socialInsurancePremiums": 0.115},"5": {"minimumWage": 632500, "socialInsurancePremiums": 0.115},"6": {"minimumWage": 632500, "socialInsurancePremiums": 0.115},"7": {"minimumWage": 632500, "socialInsurancePremiums": 0.115},"8": {"minimumWage": 632500, "socialInsurancePremiums": 0.115},"9": {"minimumWage": 632500, "socialInsurancePremiums": 0.115},"10": {"minimumWage": 632500, "socialInsurancePremiums": 0.115},"11": {"minimumWage": 632500, "socialInsurancePremiums": 0.115},"12": {"minimumWage": 632500, "socialInsurancePremiums": 0.115}}' where parameter_name = '2029' and entity = 'SalaryCalculation' and PROCESS_TYPE = 'ConsumptionLoan';
update erin_bpms_default_parameter SET parameter_value='36000000' where parameter_name= 'minAmount';

UPDATE  ERIN_BPMS_DEFAULT_PARAMETER SET  PARAMETER_VALUE='{"6000000":"160000","12000000":"140000","18000000":"120000","24000000":"100000","30000000":"80000","36000000":"60000"}' where entity= 'HhoatDiscount' and parameter_name ='2017';
UPDATE  ERIN_BPMS_DEFAULT_PARAMETER SET  PARAMETER_VALUE='{"6000000":"160000","12000000":"140000","18000000":"120000","24000000":"100000","30000000":"80000","36000000":"60000"}' where entity= 'HhoatDiscount' and parameter_name ='2018';
UPDATE  ERIN_BPMS_DEFAULT_PARAMETER SET  PARAMETER_VALUE='{"6000000":"240000","12000000":"216000","18000000":"192000","24000000":"168000","30000000":"144000","36000000":"120000"}' where entity= 'HhoatDiscount' and parameter_name ='2019';
UPDATE  ERIN_BPMS_DEFAULT_PARAMETER SET  PARAMETER_VALUE='{"6000000":"240000","12000000":"216000","18000000":"192000","24000000":"168000","30000000":"144000","36000000":"120000"}' where entity= 'HhoatDiscount' and parameter_name ='2020';
UPDATE  ERIN_BPMS_DEFAULT_PARAMETER SET  PARAMETER_VALUE='{"6000000":"240000","12000000":"216000","18000000":"192000","24000000":"168000","30000000":"144000","36000000":"120000"}' where entity= 'HhoatDiscount' and parameter_name ='2021';
UPDATE  ERIN_BPMS_DEFAULT_PARAMETER SET  PARAMETER_VALUE='{"6000000":"240000","12000000":"216000","18000000":"192000","24000000":"168000","30000000":"144000","36000000":"120000"}' where entity= 'HhoatDiscount' and parameter_name ='2022';
UPDATE  ERIN_BPMS_DEFAULT_PARAMETER SET  PARAMETER_VALUE='{"6000000":"240000","12000000":"216000","18000000":"192000","24000000":"168000","30000000":"144000","36000000":"120000"}' where entity= 'HhoatDiscount' and parameter_name ='2023';
UPDATE  ERIN_BPMS_DEFAULT_PARAMETER SET  PARAMETER_VALUE='{"6000000":"240000","12000000":"216000","18000000":"192000","24000000":"168000","30000000":"144000","36000000":"120000"}' where entity= 'HhoatDiscount' and parameter_name ='2024';
UPDATE  ERIN_BPMS_DEFAULT_PARAMETER SET  PARAMETER_VALUE='{"6000000":"240000","12000000":"216000","18000000":"192000","24000000":"168000","30000000":"144000","36000000":"120000"}' where entity= 'HhoatDiscount' and parameter_name ='2025';
UPDATE  ERIN_BPMS_DEFAULT_PARAMETER SET  PARAMETER_VALUE='{"6000000":"240000","12000000":"216000","18000000":"192000","24000000":"168000","30000000":"144000","36000000":"120000"}' where entity= 'HhoatDiscount' and parameter_name ='2026';
UPDATE  ERIN_BPMS_DEFAULT_PARAMETER SET  PARAMETER_VALUE='{"6000000":"240000","12000000":"216000","18000000":"192000","24000000":"168000","30000000":"144000","36000000":"120000"}' where entity= 'HhoatDiscount' and parameter_name ='2027';
UPDATE  ERIN_BPMS_DEFAULT_PARAMETER SET  PARAMETER_VALUE='{"6000000":"240000","12000000":"216000","18000000":"192000","24000000":"168000","30000000":"144000","36000000":"120000"}' where entity= 'HhoatDiscount' and parameter_name ='2028';
UPDATE  ERIN_BPMS_DEFAULT_PARAMETER SET  PARAMETER_VALUE='{"6000000":"240000","12000000":"216000","18000000":"192000","24000000":"168000","30000000":"144000","36000000":"120000"}' where entity= 'HhoatDiscount' and parameter_name ='2029';


