ALTER SESSION SET CURRENT_SCHEMA = ERIN;
-- AIM-ROLE values
SET DEFINE OFF;
Insert into AIM_ROLE (ROLE_ID,TENANT_ID,NAME) values ('branch_director','xac','Салбарын захирал');
Insert into AIM_ROLE (ROLE_ID,TENANT_ID,NAME) values ('hub_director','xac','HUB захирал');
Insert into AIM_ROLE (ROLE_ID,TENANT_ID,NAME) values ('npl_specialist','xac','Тусгай зээлийн мэргэжилтэн');
Insert into AIM_ROLE (ROLE_ID,TENANT_ID,NAME) values ('hr_specialist','xac','Зээлийн шинжээч /АЗМ/');
Insert into AIM_ROLE (ROLE_ID,TENANT_ID,NAME) values ('ranalyst','xac','Зээлийн шинжээч');
Insert into AIM_ROLE (ROLE_ID,TENANT_ID,NAME) values ('s_property_appraiser','xac','ХҮ ахлах мэргэжилтэн');
Insert into AIM_ROLE (ROLE_ID,TENANT_ID,NAME) values ('property_appraiser','xac','ХҮ мэргэжилтэн');
Insert into AIM_ROLE (ROLE_ID,TENANT_ID,NAME) values ('s_adclerk','xac','ЗХБ ахлах мэргэжилтэн');
Insert into AIM_ROLE (ROLE_ID,TENANT_ID,NAME) values ('adclerk','xac','ЗХБ мэргэжилтэн');
Insert into AIM_ROLE (ROLE_ID,TENANT_ID,NAME) values ('admin_1','xac','Төв албаны админ');
Insert into AIM_ROLE (ROLE_ID,TENANT_ID,NAME) values ('admin_2','xac','Салбарын админ');
Insert into AIM_ROLE (ROLE_ID,TENANT_ID,NAME) values ('admin','xac','Системийн админ');
Insert into AIM_ROLE (ROLE_ID,TENANT_ID,NAME) values ('branch_specialist','xac','Зээлийн мэргэжилтэн');
INSERT INTO AIM_ROLE (ROLE_ID, TENANT_ID,NAME) values ('cr_specialist', 'xac','Төв албаны ХБ мэргэжилтэн');




-- AIM-MEMBERSHIP values 
SET DEFINE OFF;
Insert into AIM_MEMBERSHIP (MEMBERSHIP_ID,USER_ID,GROUP_ID,ROLE_ID,TENANT_ID) values ('064a570f-60a5-4a20-98de-a4c56090ae7f','erdene-ochir','BPMS','admin','xac');
Insert into AIM_MEMBERSHIP (MEMBERSHIP_ID,USER_ID,GROUP_ID,ROLE_ID,TENANT_ID) values ('0aca787b-c5d0-481a-bdc5-25e1f48c629c','bayartsetseg','BPMS','admin','xac');
Insert into AIM_MEMBERSHIP (MEMBERSHIP_ID,USER_ID,GROUP_ID,ROLE_ID,TENANT_ID) values ('0eb72a4f-4994-4779-8dec-9e8e27dfecb5','bold-erdene','BPMS','admin','xac');
Insert into AIM_MEMBERSHIP (MEMBERSHIP_ID,USER_ID,GROUP_ID,ROLE_ID,TENANT_ID) values ('136db74c-f447-46de-a00a-c3987ce11530','zambaga','BPMS','branch_director','xac');
Insert into AIM_MEMBERSHIP (MEMBERSHIP_ID,USER_ID,GROUP_ID,ROLE_ID,TENANT_ID) values ('14c889e0-265b-4aba-aab5-403d3fec4adr','ranalyst101','101','ranalyst','xac');
Insert into AIM_MEMBERSHIP (MEMBERSHIP_ID,USER_ID,GROUP_ID,ROLE_ID,TENANT_ID) values ('14c889e0-295b-4aba-aab5-403d3fec4adu','hrSpecialist101','101','hr_specialist','xac');
Insert into AIM_MEMBERSHIP (MEMBERSHIP_ID,USER_ID,GROUP_ID,ROLE_ID,TENANT_ID) values ('14c889e0-295b-4aba-acb5-403d3fec4adu','hrSpecialist101a','101','hr_specialist','xac');
Insert into AIM_MEMBERSHIP (MEMBERSHIP_ID,USER_ID,GROUP_ID,ROLE_ID,TENANT_ID) values ('15c889e0-265b-4aba-aab5-403d3fec4adr','admin_2','108','admin_2','xac');
Insert into AIM_MEMBERSHIP (MEMBERSHIP_ID,USER_ID,GROUP_ID,ROLE_ID,TENANT_ID) values ('28019d53-82d3-4f86-809f-5c5dc4f9917d','ebank','BPMS','branch_specialist','xac');
Insert into AIM_MEMBERSHIP (MEMBERSHIP_ID,USER_ID,GROUP_ID,ROLE_ID,TENANT_ID) values ('34c889e0-265b-4aba-aab5-403d3fec4adr','branchSpecialist108a','108','branch_specialist','xac');
Insert into AIM_MEMBERSHIP (MEMBERSHIP_ID,USER_ID,GROUP_ID,ROLE_ID,TENANT_ID) values ('34c889e0-295b-4aba-aab5-403d3fec4ada','branchSpecialist101','101','branch_specialist','xac');
Insert into AIM_MEMBERSHIP (MEMBERSHIP_ID,USER_ID,GROUP_ID,ROLE_ID,TENANT_ID) values ('34c889e0-295b-4aba-aab5-403d3fec4adb','nplSpecialist108','108','npl_specialist','xac');
Insert into AIM_MEMBERSHIP (MEMBERSHIP_ID,USER_ID,GROUP_ID,ROLE_ID,TENANT_ID) values ('34c889e0-295b-4aba-aab5-403d3fec4adc','hrSpecialist108','108','hr_specialist','xac');
Insert into AIM_MEMBERSHIP (MEMBERSHIP_ID,USER_ID,GROUP_ID,ROLE_ID,TENANT_ID) values ('34c889e0-295b-4aba-aab5-403d3fec4add','otgoo','BPMS','branch_director','xac');
Insert into AIM_MEMBERSHIP (MEMBERSHIP_ID,USER_ID,GROUP_ID,ROLE_ID,TENANT_ID) values ('34c889e0-295b-4aba-aab5-403d3fec4adl','bold-erdene.j','108','branch_specialist','xac');
Insert into AIM_MEMBERSHIP (MEMBERSHIP_ID,USER_ID,GROUP_ID,ROLE_ID,TENANT_ID) values ('34c889e0-295b-4aba-aab5-403d3fec4ado','hubDirector101','101','hub_director','xac');
Insert into AIM_MEMBERSHIP (MEMBERSHIP_ID,USER_ID,GROUP_ID,ROLE_ID,TENANT_ID) values ('34c889e0-295b-4aba-aab5-403d3fec4adp','branchDirector191','191','branch_director','xac');
Insert into AIM_MEMBERSHIP (MEMBERSHIP_ID,USER_ID,GROUP_ID,ROLE_ID,TENANT_ID) values ('34c889e0-295b-4aba-aab5-403d3fec4adr','admin','001','admin','xac');
Insert into AIM_MEMBERSHIP (MEMBERSHIP_ID,USER_ID,GROUP_ID,ROLE_ID,TENANT_ID) values ('34c889e0-295b-4aba-aab5-403d3fec4adu','gantogoo','101','branch_specialist','xac');
Insert into AIM_MEMBERSHIP (MEMBERSHIP_ID,USER_ID,GROUP_ID,ROLE_ID,TENANT_ID) values ('34c889e0-295b-4aba-aab5-403d3fec4ady','uguumur','101','branch_specialist','xac');
Insert into AIM_MEMBERSHIP (MEMBERSHIP_ID,USER_ID,GROUP_ID,ROLE_ID,TENANT_ID) values ('34c889e0-295b-4aba-aab5-403d3fec4adz','branchSpecialist191','191','branch_specialist','xac');
Insert into AIM_MEMBERSHIP (MEMBERSHIP_ID,USER_ID,GROUP_ID,ROLE_ID,TENANT_ID) values ('37c889e0-295b-4aba-aab5-403d3fec4adu','admin_1','101','admin_1','xac');
Insert into AIM_MEMBERSHIP (MEMBERSHIP_ID,USER_ID,GROUP_ID,ROLE_ID,TENANT_ID) values ('4336ec04-474b-432b-a3d1-38fa412b719a','branchSpecialist108','108','branch_specialist','xac');
Insert into AIM_MEMBERSHIP (MEMBERSHIP_ID,USER_ID,GROUP_ID,ROLE_ID,TENANT_ID) values ('4336ec04-474b-432b-a3d1-38fa412b719b','branchDirector108','108','branch_director','xac');
Insert into AIM_MEMBERSHIP (MEMBERSHIP_ID,USER_ID,GROUP_ID,ROLE_ID,TENANT_ID) values ('4336ec04-474b-432b-a3d1-38fa412b719c','erin','BPMS','admin','xac');
Insert into AIM_MEMBERSHIP (MEMBERSHIP_ID,USER_ID,GROUP_ID,ROLE_ID,TENANT_ID) values ('5766d0cd-5fe7-4cfa-a192-2eb7fa927ce9','tamir','BPMS','admin','xac');
Insert into AIM_MEMBERSHIP (MEMBERSHIP_ID,USER_ID,GROUP_ID,ROLE_ID,TENANT_ID) values ('71574c89-7dec-4925-83fb-9dd5b98b6a6b','oyungerel','BPMS','admin','xac');
Insert into AIM_MEMBERSHIP (MEMBERSHIP_ID,USER_ID,GROUP_ID,ROLE_ID,TENANT_ID) values ('88574c89-7dec-4925-83fb-9dd5b98b6a6b', 'enkhamar.t','197','branch_specialist', 'xac');


-- AIM-GROUP 

SET DEFINE OFF;
Insert into AIM_GROUP (ID,TENANT_ID,PARENT_ID,NAME,NTH_SIBLING) values ('CHO','xac',null,'BPMS user groups','0');
Insert into AIM_GROUP (ID,TENANT_ID,PARENT_ID,NAME,NTH_SIBLING) values ('116','xac','CHO','HUB Баянгол салбар','1');
Insert into AIM_GROUP (ID,TENANT_ID,PARENT_ID,NAME,NTH_SIBLING) values ('173','xac','CHO','HUB Оргил салбар','2');
Insert into AIM_GROUP (ID,TENANT_ID,PARENT_ID,NAME,NTH_SIBLING) values ('133','xac','CHO','HUB Сансар салбар','3');
Insert into AIM_GROUP (ID,TENANT_ID,PARENT_ID,NAME,NTH_SIBLING) values ('106','xac','CHO','HUB Сонгинохайрхан салбар','4');
Insert into AIM_GROUP (ID,TENANT_ID,PARENT_ID,NAME,NTH_SIBLING) values ('101','xac','CHO','HUB Төв салбар','5');
Insert into AIM_GROUP (ID,TENANT_ID,PARENT_ID,NAME,NTH_SIBLING) values ('147','xac','CHO','HUB Түшиг салбар','6');
Insert into AIM_GROUP (ID,TENANT_ID,PARENT_ID,NAME,NTH_SIBLING) values ('148','xac','116','Нарны гүүр тооцооны төв','1');
Insert into AIM_GROUP (ID,TENANT_ID,PARENT_ID,NAME,NTH_SIBLING) values ('156','xac','116','10 дугаар хороолол тооцооны төв','2');
Insert into AIM_GROUP (ID,TENANT_ID,PARENT_ID,NAME,NTH_SIBLING) values ('179','xac','116','Тумбааш тооцооны төв','3');
Insert into AIM_GROUP (ID,TENANT_ID,PARENT_ID,NAME,NTH_SIBLING) values ('109','xac','173','Хан-Уул тооцооны төв','1');
Insert into AIM_GROUP (ID,TENANT_ID,PARENT_ID,NAME,NTH_SIBLING) values ('159','xac','173','Нисэх тооцооны төв','2');
Insert into AIM_GROUP (ID,TENANT_ID,PARENT_ID,NAME,NTH_SIBLING) values ('183','xac','173','Зайсан тооцооны төв','3');
Insert into AIM_GROUP (ID,TENANT_ID,PARENT_ID,NAME,NTH_SIBLING) values ('186','xac','173','Мишээл тооцооны төв','4');
Insert into AIM_GROUP (ID,TENANT_ID,PARENT_ID,NAME,NTH_SIBLING) values ('189','xac','173','Ривер стрийт тооцооны төв','5');
Insert into AIM_GROUP (ID,TENANT_ID,PARENT_ID,NAME,NTH_SIBLING) values ('190','xac','173','Энхтайваны гүүр тооцооны төв','6');
Insert into AIM_GROUP (ID,TENANT_ID,PARENT_ID,NAME,NTH_SIBLING) values ('105','xac','133','Баянзүрх салбар','1');
Insert into AIM_GROUP (ID,TENANT_ID,PARENT_ID,NAME,NTH_SIBLING) values ('150','xac','133','Цайз тооцооны төв','2');
Insert into AIM_GROUP (ID,TENANT_ID,PARENT_ID,NAME,NTH_SIBLING) values ('151','xac','133','Нарантуул тооцооны төв','3');
Insert into AIM_GROUP (ID,TENANT_ID,PARENT_ID,NAME,NTH_SIBLING) values ('172','xac','133','Их орд тооцооны төв','4');
Insert into AIM_GROUP (ID,TENANT_ID,PARENT_ID,NAME,NTH_SIBLING) values ('184','xac','133','Баянмонгол тооцооны төв','5');
Insert into AIM_GROUP (ID,TENANT_ID,PARENT_ID,NAME,NTH_SIBLING) values ('149','xac','106','Баянхошуу тооцооны төв','1');
Insert into AIM_GROUP (ID,TENANT_ID,PARENT_ID,NAME,NTH_SIBLING) values ('152','xac','106','Долоон буудал тооцооны төв','2');
Insert into AIM_GROUP (ID,TENANT_ID,PARENT_ID,NAME,NTH_SIBLING) values ('153','xac','106','21 дүгээр хороолол тооцооны төв','3');
Insert into AIM_GROUP (ID,TENANT_ID,PARENT_ID,NAME,NTH_SIBLING) values ('158','xac','106','Хүчит шонхор тооцооны төв','4');
Insert into AIM_GROUP (ID,TENANT_ID,PARENT_ID,NAME,NTH_SIBLING) values ('170','xac','106','Өнөр Тооцооны Төв','5');
Insert into AIM_GROUP (ID,TENANT_ID,PARENT_ID,NAME,NTH_SIBLING) values ('191','xac','101','Шангри-Ла тооцооны төв','1');
Insert into AIM_GROUP (ID,TENANT_ID,PARENT_ID,NAME,NTH_SIBLING) values ('108','xac','101','Сүхбаатар салбар','2');
Insert into AIM_GROUP (ID,TENANT_ID,PARENT_ID,NAME,NTH_SIBLING) values ('141','xac','101','Төв талбай салбар','3');
Insert into AIM_GROUP (ID,TENANT_ID,PARENT_ID,NAME,NTH_SIBLING) values ('157','xac','101','Зуун айл тооцооны төв','4');
Insert into AIM_GROUP (ID,TENANT_ID,PARENT_ID,NAME,NTH_SIBLING) values ('187','xac','101','И-Март тооцооны төв','5');
Insert into AIM_GROUP (ID,TENANT_ID,PARENT_ID,NAME,NTH_SIBLING) values ('188','xac','101','Ай Си Си тооцооны төв','6');
Insert into AIM_GROUP (ID,TENANT_ID,PARENT_ID,NAME,NTH_SIBLING) values ('103','xac','147','Чингэлтэй тооцооны төв','1');
Insert into AIM_GROUP (ID,TENANT_ID,PARENT_ID,NAME,NTH_SIBLING) values ('137','xac','147','Ард салбар','2');
Insert into AIM_GROUP (ID,TENANT_ID,PARENT_ID,NAME,NTH_SIBLING) values ('192','xac','147','Бөмбөгөр тооцооны төв','3');
INSERT INTO AIM_GROUP (ID,TENANT_ID,PARENT_ID,NAME,NTH_SIBLING) values ('201','xac','CHO','Орон нутгийн салбар','7');
Insert into AIM_GROUP (ID,TENANT_ID,PARENT_ID,NAME,NTH_SIBLING) values ('110','xac','201','Сэлэнгэ салбар','39');
Insert into AIM_GROUP (ID,TENANT_ID,PARENT_ID,NAME,NTH_SIBLING) values ('111','xac','201','Өвөрхангай салбар','1');
Insert into AIM_GROUP (ID,TENANT_ID,PARENT_ID,NAME,NTH_SIBLING) values ('162','xac','201','Арвайхээр тооцооны төв','2');
Insert into AIM_GROUP (ID,TENANT_ID,PARENT_ID,NAME,NTH_SIBLING) values ('113','xac','201','Эрдэнэт салбар','3');
Insert into AIM_GROUP (ID,TENANT_ID,PARENT_ID,NAME,NTH_SIBLING) values ('163','xac','201','Баян-Өндөр тооцооны төв','4');
Insert into AIM_GROUP (ID,TENANT_ID,PARENT_ID,NAME,NTH_SIBLING) values ('114','xac','201','Архангай салбар','5');
Insert into AIM_GROUP (ID,TENANT_ID,PARENT_ID,NAME,NTH_SIBLING) values ('115','xac','201','Завхан салбар','6');
Insert into AIM_GROUP (ID,TENANT_ID,PARENT_ID,NAME,NTH_SIBLING) values ('169','xac','201','Ханжаргалант тооцооны төв','7');
Insert into AIM_GROUP (ID,TENANT_ID,PARENT_ID,NAME,NTH_SIBLING) values ('117','xac','201','Хөвсгөл салбар','8');
Insert into AIM_GROUP (ID,TENANT_ID,PARENT_ID,NAME,NTH_SIBLING) values ('167','xac','201','Дэлгэрхаан тооцооны төв','9');
Insert into AIM_GROUP (ID,TENANT_ID,PARENT_ID,NAME,NTH_SIBLING) values ('120','xac','201','Говь-Алтай салбар','10');
Insert into AIM_GROUP (ID,TENANT_ID,PARENT_ID,NAME,NTH_SIBLING) values ('121','xac','201','Баянхонгор салбар','11');
Insert into AIM_GROUP (ID,TENANT_ID,PARENT_ID,NAME,NTH_SIBLING) values ('164','xac','201','Хонгор тооцооны төв','12');
Insert into AIM_GROUP (ID,TENANT_ID,PARENT_ID,NAME,NTH_SIBLING) values ('124','xac','201','Ховд салбар','13');
Insert into AIM_GROUP (ID,TENANT_ID,PARENT_ID,NAME,NTH_SIBLING) values ('125','xac','201','Увс салбар','14');
Insert into AIM_GROUP (ID,TENANT_ID,PARENT_ID,NAME,NTH_SIBLING) values ('166','xac','201','Улаангом тооцооны төв','15');
Insert into AIM_GROUP (ID,TENANT_ID,PARENT_ID,NAME,NTH_SIBLING) values ('126','xac','201','Баян-Өлгий салбар','16');
Insert into AIM_GROUP (ID,TENANT_ID,PARENT_ID,NAME,NTH_SIBLING) values ('127','xac','201','Хархорин тооцооны төв','17');
Insert into AIM_GROUP (ID,TENANT_ID,PARENT_ID,NAME,NTH_SIBLING) values ('128','xac','201','Налайх салбар','18');
Insert into AIM_GROUP (ID,TENANT_ID,PARENT_ID,NAME,NTH_SIBLING) values ('131','xac','201','Булган салбар','19');
Insert into AIM_GROUP (ID,TENANT_ID,PARENT_ID,NAME,NTH_SIBLING) values ('140','xac','201','Тосонцэнгэл тооцооны төв','20');
Insert into AIM_GROUP (ID,TENANT_ID,PARENT_ID,NAME,NTH_SIBLING) values ('102','xac','201','Дорноговь салбар','21');
Insert into AIM_GROUP (ID,TENANT_ID,PARENT_ID,NAME,NTH_SIBLING) values ('104','xac','201','Хэнтий салбар','22');
Insert into AIM_GROUP (ID,TENANT_ID,PARENT_ID,NAME,NTH_SIBLING) values ('107','xac','201','Дархан салбар','23');
Insert into AIM_GROUP (ID,TENANT_ID,PARENT_ID,NAME,NTH_SIBLING) values ('160','xac','201','Шинэ-Орчин тооцооны төв','24');
Insert into AIM_GROUP (ID,TENANT_ID,PARENT_ID,NAME,NTH_SIBLING) values ('161','xac','201','Шинэ Дархан тооцооны төв','25');
Insert into AIM_GROUP (ID,TENANT_ID,PARENT_ID,NAME,NTH_SIBLING) values ('112','xac','201','Чойр салбар','26');
Insert into AIM_GROUP (ID,TENANT_ID,PARENT_ID,NAME,NTH_SIBLING) values ('118','xac','201','Өмнөговь салбар','27');
Insert into AIM_GROUP (ID,TENANT_ID,PARENT_ID,NAME,NTH_SIBLING) values ('119','xac','201','Дундговь салбар','28');
Insert into AIM_GROUP (ID,TENANT_ID,PARENT_ID,NAME,NTH_SIBLING) values ('122','xac','201','Дорнод салбар','29');
Insert into AIM_GROUP (ID,TENANT_ID,PARENT_ID,NAME,NTH_SIBLING) values ('165','xac','201','Хэрлэн тооцооны төв','30');
Insert into AIM_GROUP (ID,TENANT_ID,PARENT_ID,NAME,NTH_SIBLING) values ('129','xac','201','Багануур салбар','31');
Insert into AIM_GROUP (ID,TENANT_ID,PARENT_ID,NAME,NTH_SIBLING) values ('130','xac','201','Зуунмод салбар','32');
Insert into AIM_GROUP (ID,TENANT_ID,PARENT_ID,NAME,NTH_SIBLING) values ('132','xac','201','Зүүнхараа тооцооны төв','33');
Insert into AIM_GROUP (ID,TENANT_ID,PARENT_ID,NAME,NTH_SIBLING) values ('134','xac','201','Хөтөл тооцооны төв','34');
Insert into AIM_GROUP (ID,TENANT_ID,PARENT_ID,NAME,NTH_SIBLING) values ('139','xac','201','Замын-Үүд тооцооны төв','35');
Insert into AIM_GROUP (ID,TENANT_ID,PARENT_ID,NAME,NTH_SIBLING) values ('143','xac','201','Бор-Өндөр тооцооны төв','36');
Insert into AIM_GROUP (ID,TENANT_ID,PARENT_ID,NAME,NTH_SIBLING) values ('145','xac','201','Цогтцэций тооцооны төв','37');
Insert into AIM_GROUP (ID,TENANT_ID,PARENT_ID,NAME,NTH_SIBLING) values ('146','xac','201','Ханбогд тооцооны төв','38');
INSERT INTO AIM_GROUP (ID,TENANT_ID,PARENT_ID,NAME,NTH_SIBLING) VALUES ('195', 'xac', 'CHO', '195-Лизингийн газар', 1);
INSERT INTO AIM_GROUP (ID,TENANT_ID,PARENT_ID,NAME,NTH_SIBLING) VALUES ('197', 'xac', 'CHO', BNPL салбар', 1);


-- AIM-PARENT-GROUP
SET DEFINE OFF;
Insert into AIM_GROUP_PARENT_CHILD (PARENT_ID,CHILD_ID) values ('101','108');
Insert into AIM_GROUP_PARENT_CHILD (PARENT_ID,CHILD_ID) values ('101','141');
Insert into AIM_GROUP_PARENT_CHILD (PARENT_ID,CHILD_ID) values ('101','157');
Insert into AIM_GROUP_PARENT_CHILD (PARENT_ID,CHILD_ID) values ('101','187');
Insert into AIM_GROUP_PARENT_CHILD (PARENT_ID,CHILD_ID) values ('101','188');
Insert into AIM_GROUP_PARENT_CHILD (PARENT_ID,CHILD_ID) values ('101','191');
Insert into AIM_GROUP_PARENT_CHILD (PARENT_ID,CHILD_ID) values ('106','149');
Insert into AIM_GROUP_PARENT_CHILD (PARENT_ID,CHILD_ID) values ('106','152');
Insert into AIM_GROUP_PARENT_CHILD (PARENT_ID,CHILD_ID) values ('106','153');
Insert into AIM_GROUP_PARENT_CHILD (PARENT_ID,CHILD_ID) values ('106','158');
Insert into AIM_GROUP_PARENT_CHILD (PARENT_ID,CHILD_ID) values ('106','170');
Insert into AIM_GROUP_PARENT_CHILD (PARENT_ID,CHILD_ID) values ('201','102');
Insert into AIM_GROUP_PARENT_CHILD (PARENT_ID,CHILD_ID) values ('201','104');
Insert into AIM_GROUP_PARENT_CHILD (PARENT_ID,CHILD_ID) values ('201','107');
Insert into AIM_GROUP_PARENT_CHILD (PARENT_ID,CHILD_ID) values ('201','111');
Insert into AIM_GROUP_PARENT_CHILD (PARENT_ID,CHILD_ID) values ('201','112');
Insert into AIM_GROUP_PARENT_CHILD (PARENT_ID,CHILD_ID) values ('201','113');
Insert into AIM_GROUP_PARENT_CHILD (PARENT_ID,CHILD_ID) values ('201','114');
Insert into AIM_GROUP_PARENT_CHILD (PARENT_ID,CHILD_ID) values ('201','115');
Insert into AIM_GROUP_PARENT_CHILD (PARENT_ID,CHILD_ID) values ('201','117');
Insert into AIM_GROUP_PARENT_CHILD (PARENT_ID,CHILD_ID) values ('201','118');
Insert into AIM_GROUP_PARENT_CHILD (PARENT_ID,CHILD_ID) values ('201','119');
Insert into AIM_GROUP_PARENT_CHILD (PARENT_ID,CHILD_ID) values ('201','120');
Insert into AIM_GROUP_PARENT_CHILD (PARENT_ID,CHILD_ID) values ('201','121');
Insert into AIM_GROUP_PARENT_CHILD (PARENT_ID,CHILD_ID) values ('201','122');
Insert into AIM_GROUP_PARENT_CHILD (PARENT_ID,CHILD_ID) values ('201','124');
Insert into AIM_GROUP_PARENT_CHILD (PARENT_ID,CHILD_ID) values ('201','125');
Insert into AIM_GROUP_PARENT_CHILD (PARENT_ID,CHILD_ID) values ('201','126');
Insert into AIM_GROUP_PARENT_CHILD (PARENT_ID,CHILD_ID) values ('201','127');
Insert into AIM_GROUP_PARENT_CHILD (PARENT_ID,CHILD_ID) values ('201','128');
Insert into AIM_GROUP_PARENT_CHILD (PARENT_ID,CHILD_ID) values ('201','129');
Insert into AIM_GROUP_PARENT_CHILD (PARENT_ID,CHILD_ID) values ('201','130');
Insert into AIM_GROUP_PARENT_CHILD (PARENT_ID,CHILD_ID) values ('201','131');
Insert into AIM_GROUP_PARENT_CHILD (PARENT_ID,CHILD_ID) values ('201','132');
Insert into AIM_GROUP_PARENT_CHILD (PARENT_ID,CHILD_ID) values ('201','134');
Insert into AIM_GROUP_PARENT_CHILD (PARENT_ID,CHILD_ID) values ('201','139');
Insert into AIM_GROUP_PARENT_CHILD (PARENT_ID,CHILD_ID) values ('201','140');
Insert into AIM_GROUP_PARENT_CHILD (PARENT_ID,CHILD_ID) values ('201','143');
Insert into AIM_GROUP_PARENT_CHILD (PARENT_ID,CHILD_ID) values ('201','145');
Insert into AIM_GROUP_PARENT_CHILD (PARENT_ID,CHILD_ID) values ('201','146');
Insert into AIM_GROUP_PARENT_CHILD (PARENT_ID,CHILD_ID) values ('201','160');
Insert into AIM_GROUP_PARENT_CHILD (PARENT_ID,CHILD_ID) values ('201','161');
Insert into AIM_GROUP_PARENT_CHILD (PARENT_ID,CHILD_ID) values ('201','162');
Insert into AIM_GROUP_PARENT_CHILD (PARENT_ID,CHILD_ID) values ('201','163');
Insert into AIM_GROUP_PARENT_CHILD (PARENT_ID,CHILD_ID) values ('201','164');
Insert into AIM_GROUP_PARENT_CHILD (PARENT_ID,CHILD_ID) values ('201','165');
Insert into AIM_GROUP_PARENT_CHILD (PARENT_ID,CHILD_ID) values ('201','166');
Insert into AIM_GROUP_PARENT_CHILD (PARENT_ID,CHILD_ID) values ('201','167');
Insert into AIM_GROUP_PARENT_CHILD (PARENT_ID,CHILD_ID) values ('201','169');
Insert into AIM_GROUP_PARENT_CHILD (PARENT_ID,CHILD_ID) values ('201','110');
Insert into AIM_GROUP_PARENT_CHILD (PARENT_ID,CHILD_ID) values ('116','148');
Insert into AIM_GROUP_PARENT_CHILD (PARENT_ID,CHILD_ID) values ('116','156');
Insert into AIM_GROUP_PARENT_CHILD (PARENT_ID,CHILD_ID) values ('116','179');
Insert into AIM_GROUP_PARENT_CHILD (PARENT_ID,CHILD_ID) values ('133','105');
Insert into AIM_GROUP_PARENT_CHILD (PARENT_ID,CHILD_ID) values ('133','150');
Insert into AIM_GROUP_PARENT_CHILD (PARENT_ID,CHILD_ID) values ('133','151');
Insert into AIM_GROUP_PARENT_CHILD (PARENT_ID,CHILD_ID) values ('133','172');
Insert into AIM_GROUP_PARENT_CHILD (PARENT_ID,CHILD_ID) values ('133','184');
Insert into AIM_GROUP_PARENT_CHILD (PARENT_ID,CHILD_ID) values ('147','103');
Insert into AIM_GROUP_PARENT_CHILD (PARENT_ID,CHILD_ID) values ('147','137');
Insert into AIM_GROUP_PARENT_CHILD (PARENT_ID,CHILD_ID) values ('147','192');
Insert into AIM_GROUP_PARENT_CHILD (PARENT_ID,CHILD_ID) values ('173','109');
Insert into AIM_GROUP_PARENT_CHILD (PARENT_ID,CHILD_ID) values ('173','159');
Insert into AIM_GROUP_PARENT_CHILD (PARENT_ID,CHILD_ID) values ('173','183');
Insert into AIM_GROUP_PARENT_CHILD (PARENT_ID,CHILD_ID) values ('173','186');
Insert into AIM_GROUP_PARENT_CHILD (PARENT_ID,CHILD_ID) values ('173','189');
Insert into AIM_GROUP_PARENT_CHILD (PARENT_ID,CHILD_ID) values ('173','190');
Insert into AIM_GROUP_PARENT_CHILD (PARENT_ID,CHILD_ID) values ('CHO','101');
Insert into AIM_GROUP_PARENT_CHILD (PARENT_ID,CHILD_ID) values ('CHO','106');
Insert into AIM_GROUP_PARENT_CHILD (PARENT_ID,CHILD_ID) values ('CHO','116');
Insert into AIM_GROUP_PARENT_CHILD (PARENT_ID,CHILD_ID) values ('CHO','133');
Insert into AIM_GROUP_PARENT_CHILD (PARENT_ID,CHILD_ID) values ('CHO','147');
Insert into AIM_GROUP_PARENT_CHILD (PARENT_ID,CHILD_ID) values ('CHO','173');
INSERT INTO aim_group_parent_child(PARENT_ID, CHILD_ID) VALUES ('CHO', '201');
INSERT INTO AIM_GROUP_PARENT_CHILD(PARENT_ID, CHILD_ID) VALUES('CHO', '195');


-- AIM-PERMISSION 
SET DEFINE OFF;
Insert into AIM_ROLE_PERMISSION (ROLE_ID,PERMISSION_ID) values ('admin','bpms.bpm.GroupModuleConfiguration');
Insert into AIM_ROLE_PERMISSION (ROLE_ID,PERMISSION_ID) values ('admin_1','bpms.bpm.DeleteProcess');
Insert into AIM_ROLE_PERMISSION (ROLE_ID,PERMISSION_ID) values ('admin_1','bpms.bpm.GetAllProcessRequests');
Insert into AIM_ROLE_PERMISSION (ROLE_ID,PERMISSION_ID) values ('admin_1','bpms.bpm.GetUnassignedRequestsByChannel');
Insert into AIM_ROLE_PERMISSION (ROLE_ID,PERMISSION_ID) values ('admin_2','bpms.bpm.GetUnassignedRequestsByChannel');
Insert into AIM_ROLE_PERMISSION (ROLE_ID,PERMISSION_ID) values ('branch_director','bpms.bpm.GetGroupProcessRequests');
Insert into AIM_ROLE_PERMISSION (ROLE_ID,PERMISSION_ID) values ('branch_director','bpms.bpm.GetProcessRequest');
Insert into AIM_ROLE_PERMISSION (ROLE_ID,PERMISSION_ID) values ('branch_director','bpms.bpm.GetProcessRequestByProcessInstanceId');
Insert into AIM_ROLE_PERMISSION (ROLE_ID,PERMISSION_ID) values ('branch_director','bpms.bpm.GetProcessRequestsByAssignedUserId');
Insert into AIM_ROLE_PERMISSION (ROLE_ID,PERMISSION_ID) values ('branch_director','bpms.bpm.GetRequestsByCreatedDate');
Insert into AIM_ROLE_PERMISSION (ROLE_ID,PERMISSION_ID) values ('branch_director','bpms.bpm.ManualActivate');
Insert into AIM_ROLE_PERMISSION (ROLE_ID,PERMISSION_ID) values ('branch_director','bpms.bpm.UpdateAssignedUser');
Insert into AIM_ROLE_PERMISSION (ROLE_ID,PERMISSION_ID) values ('branch_director','bpms.bpm.UpdateParameters');
Insert into AIM_ROLE_PERMISSION (ROLE_ID,PERMISSION_ID) values ('branch_specialist','bpms.bpm.CreateProcessRequest');
Insert into AIM_ROLE_PERMISSION (ROLE_ID,PERMISSION_ID) values ('branch_specialist','bpms.bpm.GetProcessRequest');
Insert into AIM_ROLE_PERMISSION (ROLE_ID,PERMISSION_ID) values ('branch_specialist','bpms.bpm.GetProcessRequestByProcessInstanceId');
Insert into AIM_ROLE_PERMISSION (ROLE_ID,PERMISSION_ID) values ('branch_specialist','bpms.bpm.GetProcessRequestsByAssignedUserId');
Insert into AIM_ROLE_PERMISSION (ROLE_ID,PERMISSION_ID) values ('branch_specialist','bpms.bpm.GetProcessType');
Insert into AIM_ROLE_PERMISSION (ROLE_ID,PERMISSION_ID) values ('branch_specialist','bpms.bpm.GetProcessTypes');
Insert into AIM_ROLE_PERMISSION (ROLE_ID,PERMISSION_ID) values ('branch_specialist','bpms.bpm.GetRequestsByCreatedDate');
Insert into AIM_ROLE_PERMISSION (ROLE_ID,PERMISSION_ID) values ('branch_specialist','bpms.bpm.ManualActivate');
Insert into AIM_ROLE_PERMISSION (ROLE_ID,PERMISSION_ID) values ('branch_specialist','bpms.bpm.SearchByRegisterNumber');
Insert into AIM_ROLE_PERMISSION (ROLE_ID,PERMISSION_ID) values ('branch_specialist','bpms.bpm.StartProcess');
Insert into AIM_ROLE_PERMISSION (ROLE_ID,PERMISSION_ID) values ('branch_specialist','bpms.bpm.UpdateAssignedUser');
Insert into AIM_ROLE_PERMISSION (ROLE_ID,PERMISSION_ID) values ('branch_specialist','bpms.bpm.UpdateParameters');
Insert into AIM_ROLE_PERMISSION (ROLE_ID,PERMISSION_ID) values ('hr_specialist','bpms.bpm.CreateProcessRequest');
Insert into AIM_ROLE_PERMISSION (ROLE_ID,PERMISSION_ID) values ('hr_specialist','bpms.bpm.GetAllProcessRequests');
Insert into AIM_ROLE_PERMISSION (ROLE_ID,PERMISSION_ID) values ('hr_specialist','bpms.bpm.GetProcessRequest');
Insert into AIM_ROLE_PERMISSION (ROLE_ID,PERMISSION_ID) values ('hr_specialist','bpms.bpm.GetProcessRequestByProcessInstanceId');
Insert into AIM_ROLE_PERMISSION (ROLE_ID,PERMISSION_ID) values ('hr_specialist','bpms.bpm.GetProcessRequestsByAssignedUserId');
Insert into AIM_ROLE_PERMISSION (ROLE_ID,PERMISSION_ID) values ('hr_specialist','bpms.bpm.GetProcessType');
Insert into AIM_ROLE_PERMISSION (ROLE_ID,PERMISSION_ID) values ('hr_specialist','bpms.bpm.GetProcessTypes');
Insert into AIM_ROLE_PERMISSION (ROLE_ID,PERMISSION_ID) values ('hr_specialist','bpms.bpm.GetRequestsByCreatedDate');
Insert into AIM_ROLE_PERMISSION (ROLE_ID,PERMISSION_ID) values ('hr_specialist','bpms.bpm.ManualActivate');
Insert into AIM_ROLE_PERMISSION (ROLE_ID,PERMISSION_ID) values ('hr_specialist','bpms.bpm.StartProcess');
Insert into AIM_ROLE_PERMISSION (ROLE_ID,PERMISSION_ID) values ('hr_specialist','bpms.bpm.UpdateAssignedUser');
Insert into AIM_ROLE_PERMISSION (ROLE_ID,PERMISSION_ID) values ('hr_specialist','bpms.bpm.UpdateParameters');
Insert into AIM_ROLE_PERMISSION (ROLE_ID,PERMISSION_ID) values ('hub_director','bpms.bpm.GetProcessRequest');
Insert into AIM_ROLE_PERMISSION (ROLE_ID,PERMISSION_ID) values ('hub_director','bpms.bpm.GetProcessRequestByProcessInstanceId');
Insert into AIM_ROLE_PERMISSION (ROLE_ID,PERMISSION_ID) values ('hub_director','bpms.bpm.GetProcessRequestsByAssignedUserId');
Insert into AIM_ROLE_PERMISSION (ROLE_ID,PERMISSION_ID) values ('hub_director','bpms.bpm.GetRequestsByCreatedDate');
Insert into AIM_ROLE_PERMISSION (ROLE_ID,PERMISSION_ID) values ('hub_director','bpms.bpm.GetSubGroupProcessRequests');
Insert into AIM_ROLE_PERMISSION (ROLE_ID,PERMISSION_ID) values ('hub_director','bpms.bpm.ManualActivate');
Insert into AIM_ROLE_PERMISSION (ROLE_ID,PERMISSION_ID) values ('hub_director','bpms.bpm.UpdateAssignedUser');
Insert into AIM_ROLE_PERMISSION (ROLE_ID,PERMISSION_ID) values ('hub_director','bpms.bpm.UpdateParameters');
Insert into AIM_ROLE_PERMISSION (ROLE_ID,PERMISSION_ID) values ('npl_specialist','bpms.bpm.GetProcessRequest');
Insert into AIM_ROLE_PERMISSION (ROLE_ID,PERMISSION_ID) values ('npl_specialist','bpms.bpm.GetProcessRequestByProcessInstanceId');
Insert into AIM_ROLE_PERMISSION (ROLE_ID,PERMISSION_ID) values ('npl_specialist','bpms.bpm.GetProcessRequestsByAssignedUserId');
Insert into AIM_ROLE_PERMISSION (ROLE_ID,PERMISSION_ID) values ('npl_specialist','bpms.bpm.GetRequestsByCreatedDate');
Insert into AIM_ROLE_PERMISSION (ROLE_ID,PERMISSION_ID) values ('npl_specialist','bpms.bpm.GetSubGroupProcessRequests');
Insert into AIM_ROLE_PERMISSION (ROLE_ID,PERMISSION_ID) values ('npl_specialist','bpms.bpm.ManualActivate');
Insert into AIM_ROLE_PERMISSION (ROLE_ID,PERMISSION_ID) values ('npl_specialist','bpms.bpm.UpdateAssignedUser');
Insert into AIM_ROLE_PERMISSION (ROLE_ID,PERMISSION_ID) values ('npl_specialist','bpms.bpm.UpdateParameters');
Insert into AIM_ROLE_PERMISSION (ROLE_ID,PERMISSION_ID) values ('ranalyst','bpms.bpm.GetProcessRequest');
Insert into AIM_ROLE_PERMISSION (ROLE_ID,PERMISSION_ID) values ('ranalyst','bpms.bpm.GetProcessRequestByProcessInstanceId');
Insert into AIM_ROLE_PERMISSION (ROLE_ID,PERMISSION_ID) values ('ranalyst','bpms.bpm.GetProcessRequestsByAssignedUserId');
Insert into AIM_ROLE_PERMISSION (ROLE_ID,PERMISSION_ID) values ('ranalyst','bpms.bpm.GetRequestsByCreatedDate');
Insert into AIM_ROLE_PERMISSION (ROLE_ID,PERMISSION_ID) values ('ranalyst','bpms.bpm.ManualActivate');
Insert into AIM_ROLE_PERMISSION (ROLE_ID,PERMISSION_ID) values ('ranalyst','bpms.bpm.UpdateAssignedUser');
Insert into AIM_ROLE_PERMISSION (ROLE_ID,PERMISSION_ID) values ('ranalyst','bpms.bpm.UpdateParameters');
Insert into AIM_ROLE_PERMISSION (ROLE_ID,PERMISSION_ID) values ('user_admin','bpms.bpm.GroupModuleConfiguration');

INSERT INTO aim_role_permission(role_id, permission_id) VALUES ('branch_specialist','bpms.bpm.LoanContract');
INSERT INTO aim_role_permission(role_id, permission_id) VALUES ('hr_specialist','bpms.bpm.LoanContract');
INSERT INTO aim_role_permission(role_id, permission_id) VALUES ('admin_1','bpms.bpm.LoanContract');
INSERT INTO aim_role_permission(role_id, permission_id) VALUES ('teller','bpms.bpm.LoanContract');
INSERT INTO aim_role_permission(role_id, permission_id) VALUES ('branch_director','bpms.bpm.LoanContract');
INSERT INTO aim_role_permission(role_id, permission_id) VALUES ('hub_director','bpms.bpm.LoanContract');
INSERT INTO aim_role_permission(role_id, permission_id) VALUES ('hub_director', 'bpms.bpm.GetSubGroupLoanContractProcessRequest');
INSERT INTO AIM_ROLE_PERMISSION (ROLE_ID,PERMISSION_ID) values ('cr_specialist','bpms.bpm.StartProcess');
INSERT INTO aim_role_permission(role_id, permission_id) VALUES ('cr_specialist','bpms.bpm.LoanContract');
INSERT INTO aim_role_permission(role_id, permission_id) VALUES ('cr_specialist','bpms.bpm.GetLoanContractProcessRequest');

INSERT INTO AIM_ROLE_PERMISSION (ROLE_ID,PERMISSION_ID) values ('branch_specialist','bpms.bpm.BranchBanking');


Insert into AIM_ROLE_PERMISSION (ROLE_ID,PERMISSION_ID) values ('teller','bpms.bpm.CreateProcessRequest');
Insert into AIM_ROLE_PERMISSION (ROLE_ID,PERMISSION_ID) values ('teller','bpms.bpm.GetProcessRequest');
Insert into AIM_ROLE_PERMISSION (ROLE_ID,PERMISSION_ID) values ('teller','bpms.bpm.GetProcessRequestByProcessInstanceId');
Insert into AIM_ROLE_PERMISSION (ROLE_ID,PERMISSION_ID) values ('teller','bpms.bpm.GetProcessRequestsByAssignedUserId');
Insert into AIM_ROLE_PERMISSION (ROLE_ID,PERMISSION_ID) values ('teller','bpms.bpm.GetProcessType');
Insert into AIM_ROLE_PERMISSION (ROLE_ID,PERMISSION_ID) values ('teller','bpms.bpm.GetProcessTypes');
Insert into AIM_ROLE_PERMISSION (ROLE_ID,PERMISSION_ID) values ('teller','bpms.bpm.GetRequestsByCreatedDate');
Insert into AIM_ROLE_PERMISSION (ROLE_ID,PERMISSION_ID) values ('teller','bpms.bpm.ManualActivate');
Insert into AIM_ROLE_PERMISSION (ROLE_ID,PERMISSION_ID) values ('teller','bpms.bpm.SearchByRegisterNumber');
Insert into AIM_ROLE_PERMISSION (ROLE_ID,PERMISSION_ID) values ('teller','bpms.bpm.UpdateAssignedUser');
Insert into AIM_ROLE_PERMISSION (ROLE_ID,PERMISSION_ID) values ('teller','bpms.bpm.UpdateParameters');

INSERT INTO aim_role_permission(role_id, permission_id) VALUES ( 'branch_specialist', 'bpms.bpm.GetSalaryOrganizationRequests' ); 
INSERT INTO aim_role_permission(role_id, permission_id) VALUES ( 'branch_specialist', 'bpms.bpm.GetLeasingOrganizationRequests' ); 
INSERT INTO aim_role_permission(role_id, permission_id) VALUES ( 'branch_specialist', 'bpms.bpm.CreateOrganizationRequest' );
INSERT INTO aim_role_permission(role_id, permission_id) VALUES ( 'banker', 'bpms.bpm.GetSalaryOrganizationRequests' ); 
INSERT INTO aim_role_permission(role_id, permission_id) VALUES ( 'banker', 'bpms.bpm.GetLeasingOrganizationRequests' ); 
INSERT INTO aim_role_permission(role_id, permission_id) VALUES ( 'banker', 'bpms.bpm.CreateOrganizationRequest' );
INSERT INTO aim_role_permission(role_id, permission_id) VALUES ( 'branch_director', 'bpms.bpm.GetSalaryOrganizationRequests' ); 
INSERT INTO aim_role_permission(role_id, permission_id) VALUES ( 'branch_director', 'bpms.bpm.GetLeasingOrganizationRequests' ); 
INSERT INTO aim_role_permission(role_id, permission_id) VALUES ( 'hub_director', 'bpms.bpm.GetSalaryOrganizationRequests' ); 
INSERT INTO aim_role_permission(role_id, permission_id) VALUES ( 'hub_director', 'bpms.bpm.GetLeasingOrganizationRequests' ); 
INSERT INTO aim_role_permission(role_id, permission_id) VALUES ( 'admin_2', 'bpms.bpm.GetSalaryOrganizationRequests' ); 
INSERT INTO aim_role_permission(role_id, permission_id) VALUES ( 'admin_2', 'bpms.bpm.GetLeasingOrganizationRequests' ); 
INSERT INTO aim_role_permission(role_id, permission_id) VALUES ( 'admin_1', 'bpms.bpm.GetSalaryOrganizationRequests' ); 
INSERT INTO aim_role_permission(role_id, permission_id) VALUES ( 'admin_1', 'bpms.bpm.GetLeasingOrganizationRequests' ); 
INSERT INTO aim_role_permission(role_id, permission_id) VALUES ( 'teller', 'bpms.bpm.GetSalaryOrganizationRequests' ); 
INSERT INTO aim_role_permission(role_id, permission_id) VALUES ( 'teller', 'bpms.bpm.GetLeasingOrganizationRequests' ); 

INSERT INTO aim_role_permission(role_id, permission_id) VALUES ( 'branch_specialist', 'bpms.bpm.EditOrganizationRequests' ); 
INSERT INTO aim_role_permission(role_id, permission_id) VALUES ( 'branch_specialist', 'bpms.bpm.ContinueOrganizationRequests' ); 
INSERT INTO aim_role_permission(role_id, permission_id) VALUES ( 'branch_director', 'bpms.bpm.ConfirmOrganizationRequests' ); 
INSERT INTO aim_role_permission(role_id, permission_id) VALUES ( 'hub_director', 'bpms.bpm.ConfirmOrganizationRequests' );

Insert into AIM_ROLE (ROLE_ID,TENANT_ID,NAME) values ('rc_specialist','xac','ИЗХ мэргэжилтэн');

Insert into AIM_ROLE_PERMISSION (ROLE_ID,PERMISSION_ID) values ('rc_specialist','bpms.bpm.GetGroupProcessRequests');
Insert into AIM_ROLE_PERMISSION (ROLE_ID,PERMISSION_ID) values ('rc_specialist','bpms.bpm.GetProcessRequest');
Insert into AIM_ROLE_PERMISSION (ROLE_ID,PERMISSION_ID) values ('rc_specialist','bpms.bpm.GetProcessRequestByProcessInstanceId');
Insert into AIM_ROLE_PERMISSION (ROLE_ID,PERMISSION_ID) values ('rc_specialist','bpms.bpm.GetProcessRequestsByAssignedUserId');
Insert into AIM_ROLE_PERMISSION (ROLE_ID,PERMISSION_ID) values ('rc_specialist','bpms.bpm.GetRequestsByCreatedDate');
Insert into AIM_ROLE_PERMISSION (ROLE_ID,PERMISSION_ID) values ('rc_specialist','bpms.bpm.ManualActivate');
Insert into AIM_ROLE_PERMISSION (ROLE_ID,PERMISSION_ID) values ('rc_specialist','bpms.bpm.UpdateAssignedUser');
Insert into AIM_ROLE_PERMISSION (ROLE_ID,PERMISSION_ID) values ('rc_specialist','bpms.bpm.UpdateParameters');
INSERT INTO AIM_ROLE_PERMISSION (ROLE_ID, PERMISSION_ID) VALUES ('admin_1', 'bpms.bpm.GetAllLoanRequestsExceptCHO');


--update parent id and child id value
  UPDATE aim_group_parent_child SET PARENT_ID = '201' WHERE PARENT_ID = '110';
  UPDATE aim_group_parent_child SET PARENT_ID = '201' WHERE CHILD_ID = '110';

--update 110 branch
  UPDATE AIM_GROUP SET NAME = 'Сэлэнгэ салбар', PARENT_ID = '201', NTH_SIBLING = '39' WHERE ID = '110';
  UPDATE AIM_GROUP SET PARENT_ID = '201' WHERE PARENT_ID = '110';

-- update branch name with id --
  update aim_group
  set name = id || '-' || name where id != 'CHO' AND id != '201';

Insert into AIM_ROLE_PERMISSION (ROLE_ID,PERMISSION_ID) values ('admin_1','bpms.bpm.CheckInProgressRequest');
Insert into AIM_ROLE_PERMISSION (ROLE_ID,PERMISSION_ID) values ('hr_specialist','bpms.bpm.CheckInProgressRequest');

Insert into AIM_ROLE_PERMISSION (ROLE_ID,PERMISSION_ID) values ('admin_2','bpms.bpm.EditOrganizationRequests');
Insert into AIM_ROLE_PERMISSION (ROLE_ID,PERMISSION_ID) values ('admin_2','bpms.bpm.ContinueOrganizationRequests');
Insert into AIM_ROLE_PERMISSION (ROLE_ID,PERMISSION_ID) values ('admin_2','bpms.bpm.CreateOrganizationRequest');

insert into AIM_ROLE(ROLE_ID, TENANT_ID, NAME) VALUES('cc_officer','xac','ХҮТ зөвлөх');
Insert into AIM_ROLE_PERMISSION (ROLE_ID,PERMISSION_ID) values ('cc_officer','bpms.bpm.GetSalaryOrganizationRequests');
Insert into AIM_ROLE_PERMISSION (ROLE_ID,PERMISSION_ID) values ('cc_officer','bpms.bpm.GetLeasingOrganizationRequests');
Insert into AIM_ROLE_PERMISSION (ROLE_ID,PERMISSION_ID) values ('cc_officer','bpms.bpm.GetAllLoanRequestsExceptCHO');
Insert into AIM_ROLE_PERMISSION (ROLE_ID,PERMISSION_ID) values ('cc_officer','bpms.bpm.GetAllProcessRequests');
Insert into AIM_ROLE_PERMISSION (ROLE_ID,PERMISSION_ID) values ('cc_officer','bpms.bpm.DeleteProcess');
Insert into AIM_ROLE_PERMISSION (ROLE_ID,PERMISSION_ID) values ('cc_officer','bpms.bpm.BranchBanking');
Insert into AIM_ROLE_PERMISSION (ROLE_ID,PERMISSION_ID) values ('cc_officer','bpms.bpm.StartProcess');

