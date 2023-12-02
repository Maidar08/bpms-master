/*
 * Copyright (C) ERIN SYSTEMS LLC, 2021. All rights reserved.
 *
 * The source code is protected by copyright laws and international copyright treaties, as well as
 * other intellectual property laws and treaties.
 */

CREATE TABLE ERIN_BPMS_COLLATERAL_PRODUCT
   (ID VARCHAR2(10 BYTE) NOT NULL,
	TYPE VARCHAR2(100 BYTE) NOT NULL,
	SUB_TYPE VARCHAR2(100 BYTE) NOT NULL,
	DESCRIPTION VARCHAR2(200 BYTE) NOT NULL,
	MORE_INFORMATION VARCHAR2(200 BYTE)NOT NULL,
	CONSTRAINT COLLATERAL_PRODUCT_PK PRIMARY KEY (ID)
   );


-- Collateral products -Үл хөдлөх хөрөнгө
INSERT INTO ERIN_BPMS_COLLATERAL_PRODUCT(ID, TYPE, SUB_TYPE, MORE_INFORMATION, DESCRIPTION) VALUES ('AB01', '1.Үл хөдлөх хөрөнгө', '1.Барилга', '1. Үйлдвэрийн барилга', 'Үйлдвэрийн барилга');
INSERT INTO ERIN_BPMS_COLLATERAL_PRODUCT(ID, TYPE, SUB_TYPE, MORE_INFORMATION, DESCRIPTION) VALUES ('AB02', '1.Үл хөдлөх хөрөнгө', '1.Барилга', '2. Оффис, үйлчилгээний барилга', 'Үйлчилгээ оффисийн барилга');
INSERT INTO ERIN_BPMS_COLLATERAL_PRODUCT(ID, TYPE, SUB_TYPE, MORE_INFORMATION, DESCRIPTION) VALUES ('AB03', '1.Үл хөдлөх хөрөнгө', '1.Барилга', '3. Шатахуун түгээх станц', 'Шатахуун түгээх станц');
INSERT INTO ERIN_BPMS_COLLATERAL_PRODUCT(ID, TYPE, SUB_TYPE, MORE_INFORMATION, DESCRIPTION) VALUES ('AB04', '1.Үл хөдлөх хөрөнгө', '1.Барилга', '4. Нийтийн орон сууц', 'Нийтийн орон сууц');
INSERT INTO ERIN_BPMS_COLLATERAL_PRODUCT(ID, TYPE, SUB_TYPE, MORE_INFORMATION, DESCRIPTION) VALUES ('AB05', '1.Үл хөдлөх хөрөнгө', '1.Барилга', '5. Амины сууц', 'Амины орон сууц');
INSERT INTO ERIN_BPMS_COLLATERAL_PRODUCT(ID, TYPE, SUB_TYPE, MORE_INFORMATION, DESCRIPTION) VALUES ('AB06', '1.Үл хөдлөх хөрөнгө', '1.Барилга', '6. Дуусаагүй барилга ', 'Амины болон нийтийн орон сууцны дуусаагүй барилга');
INSERT INTO ERIN_BPMS_COLLATERAL_PRODUCT(ID, TYPE, SUB_TYPE, MORE_INFORMATION, DESCRIPTION) VALUES ('AB07', '1.Үл хөдлөх хөрөнгө', '1.Барилга', '6. Дуусаагүй барилга ', 'Үйлчилгээний дуусаагүй барилга');
INSERT INTO ERIN_BPMS_COLLATERAL_PRODUCT(ID, TYPE, SUB_TYPE, MORE_INFORMATION, DESCRIPTION) VALUES ('AB08', '1.Үл хөдлөх хөрөнгө', '1.Барилга', '6. Дуусаагүй барилга ', 'Үйлдвэрийн дуусаагүй барилга');
INSERT INTO ERIN_BPMS_COLLATERAL_PRODUCT(ID, TYPE, SUB_TYPE, MORE_INFORMATION, DESCRIPTION) VALUES ('AB09', '1.Үл хөдлөх хөрөнгө', '1.Барилга', '7. Гараж', 'Гараж');
INSERT INTO ERIN_BPMS_COLLATERAL_PRODUCT(ID, TYPE, SUB_TYPE, MORE_INFORMATION, DESCRIPTION) VALUES ('AB10', '1.Үл хөдлөх хөрөнгө', '1.Барилга', '8. Зоорь, агуулах', 'Зоорь');
INSERT INTO ERIN_BPMS_COLLATERAL_PRODUCT(ID, TYPE, SUB_TYPE, MORE_INFORMATION, DESCRIPTION) VALUES ('AB11', '1.Үл хөдлөх хөрөнгө', '1.Барилга', '8. Зоорь, агуулах', 'Агуулах');
INSERT INTO ERIN_BPMS_COLLATERAL_PRODUCT(ID, TYPE, SUB_TYPE, MORE_INFORMATION, DESCRIPTION) VALUES ('BO02', '1.Үл хөдлөх хөрөнгө', '1.Барилга', '8. Зоорь, агуулах', 'Павилион');
INSERT INTO ERIN_BPMS_COLLATERAL_PRODUCT(ID, TYPE, SUB_TYPE, MORE_INFORMATION, DESCRIPTION) VALUES ('BK01', '1.Үл хөдлөх хөрөнгө', '1.Барилга', '8. Зоорь, агуулах', 'Чингэлэг   5-20тонн');
INSERT INTO ERIN_BPMS_COLLATERAL_PRODUCT(ID, TYPE, SUB_TYPE, MORE_INFORMATION, DESCRIPTION) VALUES ('BK02', '1.Үл хөдлөх хөрөнгө', '1.Барилга', '8. Зоорь, агуулах', 'Чингэлэг     30-40 тн');
INSERT INTO ERIN_BPMS_COLLATERAL_PRODUCT(ID, TYPE, SUB_TYPE, MORE_INFORMATION, DESCRIPTION) VALUES ('AS01', '1.Үл хөдлөх хөрөнгө', '2.Байгууламж', '9. Цахилгаан эрчим хүчний байгууламж', 'Цахилгаан эрчим хүчний байгууламж');
INSERT INTO ERIN_BPMS_COLLATERAL_PRODUCT(ID, TYPE, SUB_TYPE, MORE_INFORMATION, DESCRIPTION) VALUES ('AS02', '1.Үл хөдлөх хөрөнгө', '2.Байгууламж', '10. Хөдөө аж ахуйн байгууламж', 'Хөдөө аж ахуйн байгууламж');
INSERT INTO ERIN_BPMS_COLLATERAL_PRODUCT(ID, TYPE, SUB_TYPE, MORE_INFORMATION, DESCRIPTION) VALUES ('AB12', '1.Үл хөдлөх хөрөнгө', '3.Хашаа байшин (газрын хамт)', '11. Хувийн сууц', 'Хувийн сууц');
INSERT INTO ERIN_BPMS_COLLATERAL_PRODUCT(ID, TYPE, SUB_TYPE, MORE_INFORMATION, DESCRIPTION) VALUES ('AS03', '1.Үл хөдлөх хөрөнгө', '3.Хашаа байшин (газрын хамт)', '11. Хувийн сууц', 'Хашаа байшин');
INSERT INTO ERIN_BPMS_COLLATERAL_PRODUCT(ID, TYPE, SUB_TYPE, MORE_INFORMATION, DESCRIPTION) VALUES ('AL01', '1.Үл хөдлөх хөрөнгө', '4.Газар', '12. Газар өмчлөх эрх', 'Өмчлөлийн газар');
INSERT INTO ERIN_BPMS_COLLATERAL_PRODUCT(ID, TYPE, SUB_TYPE, MORE_INFORMATION, DESCRIPTION) VALUES ('AL02', '1.Үл хөдлөх хөрөнгө', '4.Газар', '13. Газар эзэмших эрх (Уул уурхай, газар тариалан, барилга барих зориулалттайгаас бусад)', 'Иргэний эзэмшлийн газар');
INSERT INTO ERIN_BPMS_COLLATERAL_PRODUCT(ID, TYPE, SUB_TYPE, MORE_INFORMATION, DESCRIPTION) VALUES ('AL03', '1.Үл хөдлөх хөрөнгө', '4.Газар', '13. Газар эзэмших эрх (Уул уурхай, газар тариалан, барилга барих зориулалттайгаас бусад)', 'Хуулийн этгээдийн эзэмшлийн газар');
INSERT INTO ERIN_BPMS_COLLATERAL_PRODUCT(ID, TYPE, SUB_TYPE, MORE_INFORMATION, DESCRIPTION) VALUES ('AL04', '1.Үл хөдлөх хөрөнгө', '4.Газар', '13. Газар эзэмших эрх', 'Уул уурхайн зориулалтаар ашиглах эрхтэй газар');
INSERT INTO ERIN_BPMS_COLLATERAL_PRODUCT(ID, TYPE, SUB_TYPE, MORE_INFORMATION, DESCRIPTION) VALUES ('AL05', '1.Үл хөдлөх хөрөнгө', '4.Газар', '13. Газар эзэмших эрх', 'Тариалангийн зориулалтаар эзэмших эрхтэй газар');

-- Collateral products -Хөдлөх хөрөнгө


INSERT INTO ERIN_BPMS_COLLATERAL_PRODUCT(ID, TYPE, SUB_TYPE, MORE_INFORMATION, DESCRIPTION) VALUES ('BC01', '2.Хөдлөх хөрөнгө', '5.Тээврийн хэрэгсэл', '14. Суудлын автомашин', 'Япон суудлын автомашин');
INSERT INTO ERIN_BPMS_COLLATERAL_PRODUCT(ID, TYPE, SUB_TYPE, MORE_INFORMATION, DESCRIPTION) VALUES ('BC02', '2.Хөдлөх хөрөнгө', '5.Тээврийн хэрэгсэл', '14. Суудлын автомашин', 'Солонгос суудлын автомашин');
INSERT INTO ERIN_BPMS_COLLATERAL_PRODUCT(ID, TYPE, SUB_TYPE, MORE_INFORMATION, DESCRIPTION) VALUES ('BC03', '2.Хөдлөх хөрөнгө', '5.Тээврийн хэрэгсэл', '14. Суудлын автомашин', 'Америк суудлын автомашин');
INSERT INTO ERIN_BPMS_COLLATERAL_PRODUCT(ID, TYPE, SUB_TYPE, MORE_INFORMATION, DESCRIPTION) VALUES ('BC04', '2.Хөдлөх хөрөнгө', '5.Тээврийн хэрэгсэл', '14. Суудлын автомашин', 'Герман суудлын автомашин');
INSERT INTO ERIN_BPMS_COLLATERAL_PRODUCT(ID, TYPE, SUB_TYPE, MORE_INFORMATION, DESCRIPTION) VALUES ('BC05', '2.Хөдлөх хөрөнгө', '5.Тээврийн хэрэгсэл', '14. Суудлын автомашин', 'Орос  суудлын автомашин');
INSERT INTO ERIN_BPMS_COLLATERAL_PRODUCT(ID, TYPE, SUB_TYPE, MORE_INFORMATION, DESCRIPTION) VALUES ('BC06', '2.Хөдлөх хөрөнгө', '5.Тээврийн хэрэгсэл', '14. Суудлын автомашин', 'Бусад улсын суудлын автомашин');
INSERT INTO ERIN_BPMS_COLLATERAL_PRODUCT(ID, TYPE, SUB_TYPE, MORE_INFORMATION, DESCRIPTION) VALUES ('BT01', '2.Хөдлөх хөрөнгө', '5.Тээврийн хэрэгсэл', '15. Ачааны автомашин', 'Япон ачааны автомашин');
INSERT INTO ERIN_BPMS_COLLATERAL_PRODUCT(ID, TYPE, SUB_TYPE, MORE_INFORMATION, DESCRIPTION) VALUES ('BT02', '2.Хөдлөх хөрөнгө', '5.Тээврийн хэрэгсэл', '15. Ачааны автомашин', 'Солонгос ачааны автомашин');
INSERT INTO ERIN_BPMS_COLLATERAL_PRODUCT(ID, TYPE, SUB_TYPE, MORE_INFORMATION, DESCRIPTION) VALUES ('BT03', '2.Хөдлөх хөрөнгө', '5.Тээврийн хэрэгсэл', '15. Ачааны автомашин', 'Америк ачааны автомашин');
INSERT INTO ERIN_BPMS_COLLATERAL_PRODUCT(ID, TYPE, SUB_TYPE, MORE_INFORMATION, DESCRIPTION) VALUES ('BT04', '2.Хөдлөх хөрөнгө', '5.Тээврийн хэрэгсэл', '15. Ачааны автомашин', 'Герман ачааны автомашин');
INSERT INTO ERIN_BPMS_COLLATERAL_PRODUCT(ID, TYPE, SUB_TYPE, MORE_INFORMATION, DESCRIPTION) VALUES ('BT05', '2.Хөдлөх хөрөнгө', '5.Тээврийн хэрэгсэл', '15. Ачааны автомашин', 'Орос ачааны автомашин');
INSERT INTO ERIN_BPMS_COLLATERAL_PRODUCT(ID, TYPE, SUB_TYPE, MORE_INFORMATION, DESCRIPTION) VALUES ('BT06', '2.Хөдлөх хөрөнгө', '5.Тээврийн хэрэгсэл', '15. Ачааны автомашин', 'Хятад  ачааны автомашин');
INSERT INTO ERIN_BPMS_COLLATERAL_PRODUCT(ID, TYPE, SUB_TYPE, MORE_INFORMATION, DESCRIPTION) VALUES ('BT07', '2.Хөдлөх хөрөнгө', '5.Тээврийн хэрэгсэл', '15. Ачааны автомашин', 'Бусад улсын ачааны автомашин');
INSERT INTO ERIN_BPMS_COLLATERAL_PRODUCT(ID, TYPE, SUB_TYPE, MORE_INFORMATION, DESCRIPTION) VALUES ('BM01', '2.Хөдлөх хөрөнгө', '5.Тээврийн хэрэгсэл', '16. Мотоцикл, мопед', 'Мотоцикл, мопед');
INSERT INTO ERIN_BPMS_COLLATERAL_PRODUCT(ID, TYPE, SUB_TYPE, MORE_INFORMATION, DESCRIPTION) VALUES ('BM02', '2.Хөдлөх хөрөнгө', '5.Тээврийн хэрэгсэл', '17. Өргөгч машин, механизм', 'Өргөгч машин механизм');
INSERT INTO ERIN_BPMS_COLLATERAL_PRODUCT(ID, TYPE, SUB_TYPE, MORE_INFORMATION, DESCRIPTION) VALUES ('BM03', '2.Хөдлөх хөрөнгө', '5.Тээврийн хэрэгсэл', '18. Нийтийн тээврийн хэрэгсэл', 'Нийтийн тээврийн хэрэгсэл');
INSERT INTO ERIN_BPMS_COLLATERAL_PRODUCT(ID, TYPE, SUB_TYPE, MORE_INFORMATION, DESCRIPTION) VALUES ('BM04', '2.Хөдлөх хөрөнгө', '5.Тээврийн хэрэгсэл', '19. Хөдөө аж ахуйн тээврийн хэрэгсэл', 'Хөдөө аж ахуйн тээврийн хэрэгсэл');
INSERT INTO ERIN_BPMS_COLLATERAL_PRODUCT(ID, TYPE, SUB_TYPE, MORE_INFORMATION, DESCRIPTION) VALUES ('BI01', '2.Хөдлөх хөрөнгө', '6.Тоног төхөөрөмж', '20. Үйлдвэрлэлийн тоног төхөөрөмж', 'Үйлдвэрлэлийн тоног төхөөрөмж');
INSERT INTO ERIN_BPMS_COLLATERAL_PRODUCT(ID, TYPE, SUB_TYPE, MORE_INFORMATION, DESCRIPTION) VALUES ('BI02', '2.Хөдлөх хөрөнгө', '6.Тоног төхөөрөмж', '21. Үйлчилгээний тоног төхөөрөмж', 'Үйлчилгээний тоног төхөөрөмж');
INSERT INTO ERIN_BPMS_COLLATERAL_PRODUCT(ID, TYPE, SUB_TYPE, MORE_INFORMATION, DESCRIPTION) VALUES ('BI03', '2.Хөдлөх хөрөнгө', '6.Тоног төхөөрөмж', '21. Үйлчилгээний тоног төхөөрөмж', 'Эмнэлэгийн тоног төхөөрөмж');
INSERT INTO ERIN_BPMS_COLLATERAL_PRODUCT(ID, TYPE, SUB_TYPE, MORE_INFORMATION, DESCRIPTION) VALUES ('BI04', '2.Хөдлөх хөрөнгө', '6.Тоног төхөөрөмж', '22. Хөдөө аж ахуйн тоног төхөөрөмж', 'Хөдөө аж ахуйн тоног төхөөрөмж');
INSERT INTO ERIN_BPMS_COLLATERAL_PRODUCT(ID, TYPE, SUB_TYPE, MORE_INFORMATION, DESCRIPTION) VALUES ('BI05', '2.Хөдлөх хөрөнгө', '6.Тоног төхөөрөмж', '23. Бусад тоног төхөөрөмж', 'Албан тасалгааны тоног төхөөрөмж');
INSERT INTO ERIN_BPMS_COLLATERAL_PRODUCT(ID, TYPE, SUB_TYPE, MORE_INFORMATION, DESCRIPTION) VALUES ('BI06', '2.Хөдлөх хөрөнгө', '6.Тоног төхөөрөмж', '23. Бусад тоног төхөөрөмж', 'Судалгаа шинжилгээний тоног төхөөрөмж');
INSERT INTO ERIN_BPMS_COLLATERAL_PRODUCT(ID, TYPE, SUB_TYPE, MORE_INFORMATION, DESCRIPTION) VALUES ('BI07', '2.Хөдлөх хөрөнгө', '6.Тоног төхөөрөмж', '23. Бусад тоног төхөөрөмж', 'Сервер хамгаалалтын тоног төхөөрөмж');
INSERT INTO ERIN_BPMS_COLLATERAL_PRODUCT(ID, TYPE, SUB_TYPE, MORE_INFORMATION, DESCRIPTION) VALUES ('BM05', '2.Хөдлөх хөрөнгө', '7.Мөнгө', '24. Хугацаатай/Хугацаагүй хадгаламж', 'Тээврийн хэрэгслийн урьдчилгаа');
INSERT INTO ERIN_BPMS_COLLATERAL_PRODUCT(ID, TYPE, SUB_TYPE, MORE_INFORMATION, DESCRIPTION) VALUES ('CC02', '2.Хөдлөх хөрөнгө', '7.Мөнгө', '24. Хугацаатай/Хугацаагүй хадгаламж', 'Хадгаламжийн хүүгийн орлого');
INSERT INTO ERIN_BPMS_COLLATERAL_PRODUCT(ID, TYPE, SUB_TYPE, MORE_INFORMATION, DESCRIPTION) VALUES ('CC03', '2.Хөдлөх хөрөнгө', '7.Мөнгө', '23. Хугацаатай/Хугацаагүй хадгаламж', 'Харилцах дансны хүүгийн орлого');
INSERT INTO ERIN_BPMS_COLLATERAL_PRODUCT(ID, TYPE, SUB_TYPE, MORE_INFORMATION, DESCRIPTION) VALUES ('CL01', '2.Хөдлөх хөрөнгө', '7.Мөнгө', '23. Хугацаатай/Хугацаагүй хадгаламж', 'Хадгаламж');
INSERT INTO ERIN_BPMS_COLLATERAL_PRODUCT(ID, TYPE, SUB_TYPE, MORE_INFORMATION, DESCRIPTION) VALUES ('CL02', '2.Хөдлөх хөрөнгө', '7.Мөнгө', '23. Хугацаатай/Хугацаагүй хадгаламж', 'Харилцах дансан дахь мөнгө');
INSERT INTO ERIN_BPMS_COLLATERAL_PRODUCT(ID, TYPE, SUB_TYPE, MORE_INFORMATION, DESCRIPTION) VALUES ('CA01', '2.Хөдлөх хөрөнгө', '8.Мал', '25. Даатгагдсан мал', 'Даатгалтай бог мал');
INSERT INTO ERIN_BPMS_COLLATERAL_PRODUCT(ID, TYPE, SUB_TYPE, MORE_INFORMATION, DESCRIPTION) VALUES ('CA03', '2.Хөдлөх хөрөнгө', '8.Мал', '25. Даатгагдсан мал', 'Даатгалтай бод мал');
INSERT INTO ERIN_BPMS_COLLATERAL_PRODUCT(ID, TYPE, SUB_TYPE, MORE_INFORMATION, DESCRIPTION) VALUES ('CA02', '2.Хөдлөх хөрөнгө', '8.Мал', '26. Даатгагдаагүй мал', 'Даатгалгүй бог мал');
INSERT INTO ERIN_BPMS_COLLATERAL_PRODUCT(ID, TYPE, SUB_TYPE, MORE_INFORMATION, DESCRIPTION) VALUES ('CA04', '2.Хөдлөх хөрөнгө', '8.Мал', '26. Даатгагдаагүй мал', 'Даатгалгүй бод мал');
INSERT INTO ERIN_BPMS_COLLATERAL_PRODUCT(ID, TYPE, SUB_TYPE, MORE_INFORMATION, DESCRIPTION) VALUES ('CM01', '2.Хөдлөх хөрөнгө', '9.Бараа материал', '27. Бараа материал', 'Түүхий эд хагас боловсруулсан бүтээгдэхүүн');
INSERT INTO ERIN_BPMS_COLLATERAL_PRODUCT(ID, TYPE, SUB_TYPE, MORE_INFORMATION, DESCRIPTION) VALUES ('CP01', '2.Хөдлөх хөрөнгө', '9.Бараа материал', '27. Бараа материал', 'Хүнсний бүтээгдэхүүн');
INSERT INTO ERIN_BPMS_COLLATERAL_PRODUCT(ID, TYPE, SUB_TYPE, MORE_INFORMATION, DESCRIPTION) VALUES ('CP02', '2.Хөдлөх хөрөнгө', '9.Бараа материал', '27. Бараа материал', 'Бараа бэлэн бүтээгдэхүүн');
INSERT INTO ERIN_BPMS_COLLATERAL_PRODUCT(ID, TYPE, SUB_TYPE, MORE_INFORMATION, DESCRIPTION) VALUES ('CP03', '2.Хөдлөх хөрөнгө', '9.Бараа материал', '27. Бараа материал', 'Гоо сайхны бүтээгдэхүүн ');
INSERT INTO ERIN_BPMS_COLLATERAL_PRODUCT(ID, TYPE, SUB_TYPE, MORE_INFORMATION, DESCRIPTION) VALUES ('CP07', '2.Хөдлөх хөрөнгө', '9.Бараа материал', '27. Бараа материал', 'Барилгын материал');
INSERT INTO ERIN_BPMS_COLLATERAL_PRODUCT(ID, TYPE, SUB_TYPE, MORE_INFORMATION, DESCRIPTION) VALUES ('CP05', '2.Хөдлөх хөрөнгө', '9.Бараа материал', '27. Бараа материал', 'Борлуулж буй тоног төхөөрөмж');
INSERT INTO ERIN_BPMS_COLLATERAL_PRODUCT(ID, TYPE, SUB_TYPE, MORE_INFORMATION, DESCRIPTION) VALUES ('CP04', '2.Хөдлөх хөрөнгө', '9.Бараа материал', '27. Бараа материал', 'Эмийн болон химийн бүтээгдэхүүн');
INSERT INTO ERIN_BPMS_COLLATERAL_PRODUCT(ID, TYPE, SUB_TYPE, MORE_INFORMATION, DESCRIPTION) VALUES ('CR01', '2.Хөдлөх хөрөнгө', '10.Авлага', '28. Авлага', 'Дансаарх авлага');
INSERT INTO ERIN_BPMS_COLLATERAL_PRODUCT(ID, TYPE, SUB_TYPE, MORE_INFORMATION, DESCRIPTION) VALUES ('CC12', '2.Хөдлөх хөрөнгө', '10.Авлага', '28. Авлага', 'Батлан даалтын сан');
INSERT INTO ERIN_BPMS_COLLATERAL_PRODUCT(ID, TYPE, SUB_TYPE, MORE_INFORMATION, DESCRIPTION) VALUES ('CC11', '2.Хөдлөх хөрөнгө', '10.Авлага', '29. Вексель', 'Вексел');
INSERT INTO ERIN_BPMS_COLLATERAL_PRODUCT(ID, TYPE, SUB_TYPE, MORE_INFORMATION, DESCRIPTION) VALUES ('DLIC', '2.Хөдлөх хөрөнгө', '10.Авлага', '29. Вексель', 'Үйл ажиллагаа явуулах зөвшөөрлөөс бусад лиценз');

-- Collateral products -Unsecured
INSERT INTO ERIN_BPMS_COLLATERAL_PRODUCT(ID, TYPE, SUB_TYPE, MORE_INFORMATION, DESCRIPTION) VALUES ('CC01', 'Unsecured', 'Unsecured', '30. Цалингийн (Бизнесийн) орлого', 'Ногдол ашгийн орлого');
INSERT INTO ERIN_BPMS_COLLATERAL_PRODUCT(ID, TYPE, SUB_TYPE, MORE_INFORMATION, DESCRIPTION) VALUES ('CC04', 'Unsecured', 'Unsecured', '30. Цалингийн (Бизнесийн) орлого', 'Тогтмол цалингийн орлого');
INSERT INTO ERIN_BPMS_COLLATERAL_PRODUCT(ID, TYPE, SUB_TYPE, MORE_INFORMATION, DESCRIPTION) VALUES ('CC05', 'Unsecured', 'Unsecured', '30. Цалингийн (Бизнесийн) орлого', 'Ф-ийн гэрээтэй ХЗХ -ы гишүүдээс хувь нийлүүлсэн хөрөнгө');
INSERT INTO ERIN_BPMS_COLLATERAL_PRODUCT(ID, TYPE, SUB_TYPE, MORE_INFORMATION, DESCRIPTION) VALUES ('CC06', 'Unsecured', 'Unsecured', '30. Цалингийн (Бизнесийн) орлого', 'Ф-ийн гэрээгүй ХЗХ -ы гишүүдээс хувь нийлүүлсэн хөрөнгө');
INSERT INTO ERIN_BPMS_COLLATERAL_PRODUCT(ID, TYPE, SUB_TYPE, MORE_INFORMATION, DESCRIPTION) VALUES ('CC07', 'Unsecured', 'Unsecured', '30. Цалингийн (Бизнесийн) орлого', 'ББСБ-ын дүрмийн сан');
INSERT INTO ERIN_BPMS_COLLATERAL_PRODUCT(ID, TYPE, SUB_TYPE, MORE_INFORMATION, DESCRIPTION) VALUES ('CC08', 'Unsecured', 'Unsecured', '30. Цалингийн (Бизнесийн) орлого', 'Тогтмол цалингийн орлого /Овердрафт/');
INSERT INTO ERIN_BPMS_COLLATERAL_PRODUCT(ID, TYPE, SUB_TYPE, MORE_INFORMATION, DESCRIPTION) VALUES ('CC09', 'Unsecured', 'Unsecured', '30. Цалингийн (Бизнесийн) орлого', 'Хувьцаа');
INSERT INTO ERIN_BPMS_COLLATERAL_PRODUCT(ID, TYPE, SUB_TYPE, MORE_INFORMATION, DESCRIPTION) VALUES ('CC10', 'Unsecured', 'Unsecured', '30. Цалингийн (Бизнесийн) орлого', 'Гэрээний орлого');
INSERT INTO ERIN_BPMS_COLLATERAL_PRODUCT(ID, TYPE, SUB_TYPE, MORE_INFORMATION, DESCRIPTION) VALUES ('CC13', 'Unsecured', 'Unsecured', '30. Цалингийн (Бизнесийн) орлого', 'БИЗНЕСИЙН ОРЛОГО');
INSERT INTO ERIN_BPMS_COLLATERAL_PRODUCT(ID, TYPE, SUB_TYPE, MORE_INFORMATION, DESCRIPTION) VALUES ('BO01', 'Unsecured', 'Unsecured', '31.Бусад', 'Хөгжмийн зэмсэг');
INSERT INTO ERIN_BPMS_COLLATERAL_PRODUCT(ID, TYPE, SUB_TYPE, MORE_INFORMATION, DESCRIPTION) VALUES ('BC07', 'Unsecured', 'Unsecured', '31.Бусад', 'Унадаг дугуй');
INSERT INTO ERIN_BPMS_COLLATERAL_PRODUCT(ID, TYPE, SUB_TYPE, MORE_INFORMATION, DESCRIPTION) VALUES ('BE01', 'Unsecured', 'Unsecured', '31.Бусад', 'Хөргөгч');
INSERT INTO ERIN_BPMS_COLLATERAL_PRODUCT(ID, TYPE, SUB_TYPE, MORE_INFORMATION, DESCRIPTION) VALUES ('BE02', 'Unsecured', 'Unsecured', '31.Бусад', 'Угаалгын машин');
INSERT INTO ERIN_BPMS_COLLATERAL_PRODUCT(ID, TYPE, SUB_TYPE, MORE_INFORMATION, DESCRIPTION) VALUES ('BE03', 'Unsecured', 'Unsecured', '31.Бусад', 'Тоос сорогч');
INSERT INTO ERIN_BPMS_COLLATERAL_PRODUCT(ID, TYPE, SUB_TYPE, MORE_INFORMATION, DESCRIPTION) VALUES ('BE04', 'Unsecured', 'Unsecured', '31.Бусад', 'Зурагт');
INSERT INTO ERIN_BPMS_COLLATERAL_PRODUCT(ID, TYPE, SUB_TYPE, MORE_INFORMATION, DESCRIPTION) VALUES ('BE05', 'Unsecured', 'Unsecured', '31.Бусад', 'Хөлдөөгч');
INSERT INTO ERIN_BPMS_COLLATERAL_PRODUCT(ID, TYPE, SUB_TYPE, MORE_INFORMATION, DESCRIPTION) VALUES ('BE06', 'Unsecured', 'Unsecured', '31.Бусад', 'Плитка');
INSERT INTO ERIN_BPMS_COLLATERAL_PRODUCT(ID, TYPE, SUB_TYPE, MORE_INFORMATION, DESCRIPTION) VALUES ('BE07', 'Unsecured', 'Unsecured', '31.Бусад', 'Хөгжим');
INSERT INTO ERIN_BPMS_COLLATERAL_PRODUCT(ID, TYPE, SUB_TYPE, MORE_INFORMATION, DESCRIPTION) VALUES ('BE08', 'Unsecured', 'Unsecured', '31.Бусад', 'Гэрийн театр');
INSERT INTO ERIN_BPMS_COLLATERAL_PRODUCT(ID, TYPE, SUB_TYPE, MORE_INFORMATION, DESCRIPTION) VALUES ('BE09', 'Unsecured', 'Unsecured', '31.Бусад', 'Компьютер');
INSERT INTO ERIN_BPMS_COLLATERAL_PRODUCT(ID, TYPE, SUB_TYPE, MORE_INFORMATION, DESCRIPTION) VALUES ('BE10', 'Unsecured', 'Unsecured', '31.Бусад', 'Видео камер');
INSERT INTO ERIN_BPMS_COLLATERAL_PRODUCT(ID, TYPE, SUB_TYPE, MORE_INFORMATION, DESCRIPTION) VALUES ('BE11', 'Unsecured', 'Unsecured', '31.Бусад', 'Хувилагч машин');
INSERT INTO ERIN_BPMS_COLLATERAL_PRODUCT(ID, TYPE, SUB_TYPE, MORE_INFORMATION, DESCRIPTION) VALUES ('BE12', 'Unsecured', 'Unsecured', '31.Бусад', 'Принтер сканнер');
INSERT INTO ERIN_BPMS_COLLATERAL_PRODUCT(ID, TYPE, SUB_TYPE, MORE_INFORMATION, DESCRIPTION) VALUES ('BE13', 'Unsecured', 'Unsecured', '31.Бусад', 'Шарах шүүгээ');
INSERT INTO ERIN_BPMS_COLLATERAL_PRODUCT(ID, TYPE, SUB_TYPE, MORE_INFORMATION, DESCRIPTION) VALUES ('BE14', 'Unsecured', 'Unsecured', '31.Бусад', 'Зургийн аппарат');
INSERT INTO ERIN_BPMS_COLLATERAL_PRODUCT(ID, TYPE, SUB_TYPE, MORE_INFORMATION, DESCRIPTION) VALUES ('BE15', 'Unsecured', 'Unsecured', '31.Бусад', 'Гар утас');
INSERT INTO ERIN_BPMS_COLLATERAL_PRODUCT(ID, TYPE, SUB_TYPE, MORE_INFORMATION, DESCRIPTION) VALUES ('BE16', 'Unsecured', 'Unsecured', '31.Бусад', 'Халаагуур');
INSERT INTO ERIN_BPMS_COLLATERAL_PRODUCT(ID, TYPE, SUB_TYPE, MORE_INFORMATION, DESCRIPTION) VALUES ('BF01', 'Unsecured', 'Unsecured', '31.Бусад', 'Албан тасалгааны тавилга хэрэгсэл');
INSERT INTO ERIN_BPMS_COLLATERAL_PRODUCT(ID, TYPE, SUB_TYPE, MORE_INFORMATION, DESCRIPTION) VALUES ('BF02', 'Unsecured', 'Unsecured', '31.Бусад', 'Үйлдвэрлэлийн тавилга хэрэгсэл');
INSERT INTO ERIN_BPMS_COLLATERAL_PRODUCT(ID, TYPE, SUB_TYPE, MORE_INFORMATION, DESCRIPTION) VALUES ('BF03', 'Unsecured', 'Unsecured', '31.Бусад', 'Үйлчилгээний тавилга хэрэгсэл');
INSERT INTO ERIN_BPMS_COLLATERAL_PRODUCT(ID, TYPE, SUB_TYPE, MORE_INFORMATION, DESCRIPTION) VALUES ('BG01', 'Unsecured', 'Unsecured', '31.Бусад', '4 ханатай гэр');
INSERT INTO ERIN_BPMS_COLLATERAL_PRODUCT(ID, TYPE, SUB_TYPE, MORE_INFORMATION, DESCRIPTION) VALUES ('BG02', 'Unsecured', 'Unsecured', '31.Бусад', '5 ханатай гэр');
INSERT INTO ERIN_BPMS_COLLATERAL_PRODUCT(ID, TYPE, SUB_TYPE, MORE_INFORMATION, DESCRIPTION) VALUES ('BG03', 'Unsecured', 'Unsecured', '31.Бусад', '6 ханатай гэр');
INSERT INTO ERIN_BPMS_COLLATERAL_PRODUCT(ID, TYPE, SUB_TYPE, MORE_INFORMATION, DESCRIPTION) VALUES ('BG04', 'Unsecured', 'Unsecured', '31.Бусад', '8 ханатай гэр');
INSERT INTO ERIN_BPMS_COLLATERAL_PRODUCT(ID, TYPE, SUB_TYPE, MORE_INFORMATION, DESCRIPTION) VALUES ('BG05', 'Unsecured', 'Unsecured', '31.Бусад', '10 ханатай гэр');
INSERT INTO ERIN_BPMS_COLLATERAL_PRODUCT(ID, TYPE, SUB_TYPE, MORE_INFORMATION, DESCRIPTION) VALUES ('BG06', 'Unsecured', 'Unsecured', '31.Бусад', 'Сийлбэртэй гэр');
INSERT INTO ERIN_BPMS_COLLATERAL_PRODUCT(ID, TYPE, SUB_TYPE, MORE_INFORMATION, DESCRIPTION) VALUES ('BH01', 'Unsecured', 'Unsecured', '31.Бусад', 'Зочны өрөөний тавилга');
INSERT INTO ERIN_BPMS_COLLATERAL_PRODUCT(ID, TYPE, SUB_TYPE, MORE_INFORMATION, DESCRIPTION) VALUES ('BH02', 'Unsecured', 'Unsecured', '31.Бусад', 'Унтлагын өрөөний тавилга');
INSERT INTO ERIN_BPMS_COLLATERAL_PRODUCT(ID, TYPE, SUB_TYPE, MORE_INFORMATION, DESCRIPTION) VALUES ('BH03', 'Unsecured', 'Unsecured', '31.Бусад', 'Гал тогооны тавилга');
INSERT INTO ERIN_BPMS_COLLATERAL_PRODUCT(ID, TYPE, SUB_TYPE, MORE_INFORMATION, DESCRIPTION) VALUES ('BH04', 'Unsecured', 'Unsecured', '31.Бусад', 'Хивс');
INSERT INTO ERIN_BPMS_COLLATERAL_PRODUCT(ID, TYPE, SUB_TYPE, MORE_INFORMATION, DESCRIPTION) VALUES ('BH05', 'Unsecured', 'Unsecured', '31.Бусад', 'Оёдолын машин');
INSERT INTO ERIN_BPMS_COLLATERAL_PRODUCT(ID, TYPE, SUB_TYPE, MORE_INFORMATION, DESCRIPTION) VALUES ('CA05', 'Unsecured', 'Unsecured', '31.Бусад', 'Туслах аж ахуйн мал амьтад');
INSERT INTO ERIN_BPMS_COLLATERAL_PRODUCT(ID, TYPE, SUB_TYPE, MORE_INFORMATION, DESCRIPTION) VALUES ('CA06', 'Unsecured', 'Unsecured', '31.Бусад', 'Туслах аж ахуйн мал амьтад');
 

