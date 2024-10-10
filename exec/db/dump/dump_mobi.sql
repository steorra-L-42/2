-- MySQL dump 10.13  Distrib 8.4.1, for Linux (x86_64)
--
-- Host: localhost    Database: mobipay
-- ------------------------------------------------------
-- Server version	8.4.1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `account`
--

DROP TABLE IF EXISTS `account`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `account` (
  `bank_code` varchar(3) NOT NULL,
  `created` datetime(6) NOT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `mobi_user_id` bigint DEFAULT NULL,
  `account_no` varchar(16) NOT NULL,
  `account_type_unique_no` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKruxg86y66hmn0a129n4j75akk` (`account_no`),
  KEY `FK5e73v9c0o1yt7n9ld39cry7qm` (`account_type_unique_no`),
  KEY `FKeg8q2ksis6llqhdynxt2aoxu4` (`mobi_user_id`),
  CONSTRAINT `FK5e73v9c0o1yt7n9ld39cry7qm` FOREIGN KEY (`account_type_unique_no`) REFERENCES `account_product` (`account_type_unique_no`),
  CONSTRAINT `FKeg8q2ksis6llqhdynxt2aoxu4` FOREIGN KEY (`mobi_user_id`) REFERENCES `mobi_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `account`
--

LOCK TABLES `account` WRITE;
/*!40000 ALTER TABLE `account` DISABLE KEYS */;
INSERT INTO `account` VALUES ('004','2024-10-10 10:13:26.917770',1,1,'0048854524309776','004-1-c880da59551a4e'),('004','2024-10-10 10:13:44.220716',2,2,'0047728359602522','004-1-c880da59551a4e'),('004','2024-10-10 10:15:12.287219',3,3,'0045902577806732','004-1-c880da59551a4e'),('004','2024-10-10 10:16:28.599198',4,4,'0047965537499771','004-1-c880da59551a4e'),('004','2024-10-10 10:19:23.884989',5,5,'0048580535868879','004-1-c880da59551a4e'),('004','2024-10-10 10:33:04.595233',6,6,'0044914360663268','004-1-c880da59551a4e');
/*!40000 ALTER TABLE `account` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `account_product`
--

DROP TABLE IF EXISTS `account_product`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `account_product` (
  `bank_code` varchar(3) DEFAULT NULL,
  `account_name` varchar(20) NOT NULL,
  `account_type_unique_no` varchar(20) NOT NULL,
  PRIMARY KEY (`account_type_unique_no`),
  KEY `FK5h5eqxwanmj2qhlymnrx0rg4a` (`bank_code`),
  CONSTRAINT `FK5h5eqxwanmj2qhlymnrx0rg4a` FOREIGN KEY (`bank_code`) REFERENCES `bank` (`bank_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `account_product`
--

LOCK TABLES `account_product` WRITE;
/*!40000 ALTER TABLE `account_product` DISABLE KEYS */;
INSERT INTO `account_product` VALUES ('001','한국은행 수시입출금 상품','001-1-5574949722ff43'),('004','국민은행 수시입출금 상품','004-1-c880da59551a4e'),('090','카카오뱅크 수시입출금 상품','090-1-68131944bcc749');
/*!40000 ALTER TABLE `account_product` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `approval_waiting`
--

DROP TABLE IF EXISTS `approval_waiting`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `approval_waiting` (
  `approved` bit(1) NOT NULL,
  `car_id` bigint DEFAULT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `merchant_id` bigint DEFAULT NULL,
  `payment_balance` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKbaf9l0fwb7nkb74mndswkke9b` (`car_id`),
  KEY `FKd1pendntxgpsmrcpwaxg31nly` (`merchant_id`),
  CONSTRAINT `FKbaf9l0fwb7nkb74mndswkke9b` FOREIGN KEY (`car_id`) REFERENCES `car` (`id`),
  CONSTRAINT `FKd1pendntxgpsmrcpwaxg31nly` FOREIGN KEY (`merchant_id`) REFERENCES `merchant` (`merchant_id`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `approval_waiting`
--

LOCK TABLES `approval_waiting` WRITE;
/*!40000 ALTER TABLE `approval_waiting` DISABLE KEYS */;
INSERT INTO `approval_waiting` VALUES (_binary '',4,1,1911,27000),(_binary '',4,2,1911,11200),(_binary '',6,3,1911,3600),(_binary '',4,4,1911,15300),(_binary '',6,5,1911,24000),(_binary '\0',6,6,1911,25200),(_binary '',2,7,1907,80000),(_binary '',4,8,1907,90000),(_binary '',6,9,1907,100000),(_binary '',6,10,1907,30000),(_binary '\0',8,11,1906,500),(_binary '\0',8,12,1906,1000),(_binary '',8,13,1906,1000),(_binary '\0',8,14,1906,500),(_binary '\0',8,15,1906,500),(_binary '',8,16,1911,2100),(_binary '',8,17,1911,2200),(_binary '',8,18,1906,500),(_binary '',8,19,1906,500),(_binary '\0',4,20,1907,100000),(_binary '',4,21,1907,100000);
/*!40000 ALTER TABLE `approval_waiting` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `bank`
--

DROP TABLE IF EXISTS `bank`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bank` (
  `bank_code` varchar(3) NOT NULL,
  `bank_name` varchar(20) NOT NULL,
  PRIMARY KEY (`bank_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bank`
--

LOCK TABLES `bank` WRITE;
/*!40000 ALTER TABLE `bank` DISABLE KEYS */;
INSERT INTO `bank` VALUES ('001','한국은행'),('002','산업은행'),('003','기업은행'),('004','국민은행'),('011','농협은행'),('020','우리은행'),('023','SC제일은행'),('027','시티은행'),('032','대구은행'),('034','광주은행'),('035','제주은행'),('037','전북은행'),('039','경남은행'),('045','새마을금고'),('081','KEB하나은행'),('088','신한은행'),('090','카카오뱅크'),('999','싸피은행');
/*!40000 ALTER TABLE `bank` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `car`
--

DROP TABLE IF EXISTS `car`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `car` (
  `auto_pay_status` bit(1) NOT NULL,
  `created` datetime(6) NOT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `number` varchar(8) NOT NULL,
  `owner_id` bigint DEFAULT NULL,
  `car_model` varchar(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKhni4urt3gng4yws350f7ebc1` (`number`),
  KEY `FK5huiha46ie4xolm9j3q3o5j6e` (`owner_id`),
  CONSTRAINT `FK5huiha46ie4xolm9j3q3o5j6e` FOREIGN KEY (`owner_id`) REFERENCES `mobi_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `car`
--

LOCK TABLES `car` WRITE;
/*!40000 ALTER TABLE `car` DISABLE KEYS */;
INSERT INTO `car` VALUES (_binary '\0','2024-10-10 10:14:46.405380',1,'393누1798',1,'SM6'),(_binary '','2024-10-10 10:15:00.394672',2,'231모6530',2,'GV80'),(_binary '','2024-10-10 10:15:02.462776',3,'138너2840',1,'모델Y'),(_binary '','2024-10-10 10:16:56.646803',4,'889나0651',4,'모하비'),(_binary '\0','2024-10-10 10:16:59.382588',5,'230루6662',3,'G70'),(_binary '\0','2024-10-10 10:17:16.135323',6,'347다9221',4,'아반떼'),(_binary '\0','2024-10-10 10:25:13.179656',7,'39어4103',5,'G70'),(_binary '','2024-10-10 10:28:42.331232',8,'998너7919',1,'Q8'),(_binary '\0','2024-10-10 10:30:49.765894',9,'116노6058',5,'팰리세이드'),(_binary '\0','2024-10-10 10:31:56.563997',10,'142소8362',5,'BMS7'),(_binary '\0','2024-10-10 10:32:24.837370',11,'156어2368',5,'모닝'),(_binary '\0','2024-10-10 10:33:02.861558',12,'169무8017',5,'GV70');
/*!40000 ALTER TABLE `car` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `car_group`
--

DROP TABLE IF EXISTS `car_group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `car_group` (
  `car_id` bigint NOT NULL,
  `mobi_user_id` bigint NOT NULL,
  PRIMARY KEY (`car_id`,`mobi_user_id`),
  KEY `FKd3fpok4bshvjg583ewdg11yyc` (`mobi_user_id`),
  CONSTRAINT `FKd3fpok4bshvjg583ewdg11yyc` FOREIGN KEY (`mobi_user_id`) REFERENCES `mobi_user` (`id`),
  CONSTRAINT `FKf1erf3llf0dmy9e5584u83ee6` FOREIGN KEY (`car_id`) REFERENCES `car` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `car_group`
--

LOCK TABLES `car_group` WRITE;
/*!40000 ALTER TABLE `car_group` DISABLE KEYS */;
INSERT INTO `car_group` VALUES (1,1),(3,1),(8,1),(1,2),(2,2),(3,2),(8,2),(1,3),(3,3),(5,3),(8,3),(4,4),(6,4),(7,5),(8,5),(9,5),(10,5),(11,5),(12,5);
/*!40000 ALTER TABLE `car_group` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `card_issuer`
--

DROP TABLE IF EXISTS `card_issuer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `card_issuer` (
  `card_issuer_code` varchar(4) NOT NULL,
  `card_issuer_name` varchar(20) NOT NULL,
  PRIMARY KEY (`card_issuer_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `card_issuer`
--

LOCK TABLES `card_issuer` WRITE;
/*!40000 ALTER TABLE `card_issuer` DISABLE KEYS */;
INSERT INTO `card_issuer` VALUES ('1001','KB국민카드'),('1002','삼성카드'),('1003','롯데카드'),('1004','우리카드'),('1005','신한카드'),('1006','현대카드'),('1007','BC 바로카드'),('1008','NH농협카드'),('1009','하나카드'),('1010','IBK기업은행');
/*!40000 ALTER TABLE `card_issuer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `card_product`
--

DROP TABLE IF EXISTS `card_product`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `card_product` (
  `card_issuer_code` varchar(4) DEFAULT NULL,
  `baseline_performance` bigint NOT NULL,
  `max_benefit_limit` bigint NOT NULL,
  `card_name` varchar(20) NOT NULL,
  `card_unique_no` varchar(20) NOT NULL,
  `card_description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`card_unique_no`),
  KEY `FKdddpawvamqr9bc2gxak9loups` (`card_issuer_code`),
  CONSTRAINT `FKdddpawvamqr9bc2gxak9loups` FOREIGN KEY (`card_issuer_code`) REFERENCES `card_issuer` (`card_issuer_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `card_product`
--

LOCK TABLES `card_product` WRITE;
/*!40000 ALTER TABLE `card_product` DISABLE KEYS */;
INSERT INTO `card_product` VALUES ('1001',700000,130000,'슬기로운 국민카드','1001-664f125022bf433','생활20%할인, 교통10% 할인, 대형마트5% 할인'),('1002',700000,130000,'삼성카드 taptap O','1002-218c5933582e430','통신10%할인, 교통10% 할인, 대형마트5% 할인'),('1005',700000,130000,'신한카드 SOL트래블','1005-6d3da5e1ab334fc','주유7%할인, 교통10% 할인, 대형마트5% 할인'),('1005',200000,1000000,'하나카드 MULTI Oil','1005-dcf45d7885a1442','주유 10%할인, 생활 5% 할인');
/*!40000 ALTER TABLE `card_product` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fcm_token`
--

DROP TABLE IF EXISTS `fcm_token`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `fcm_token` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `value` text NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fcm_token`
--

LOCK TABLES `fcm_token` WRITE;
/*!40000 ALTER TABLE `fcm_token` DISABLE KEYS */;
INSERT INTO `fcm_token` VALUES (1,'cf5hdo0URwiUlJAbeaDcMi:APA91bHxacQhQffdizO7vhAarsQFbfSwq7DfCyDJTBd6BxaRxPtRK08IIGxE8cOS3ijWuw0i4rGfqqwzvoqQAnowU-io4jWdSxZoM0QIa3yMFUQLZ6gK_gNsZ7wm5PajtDEtIRcRK5zi'),(2,'c4lcFArLRomRrInHkTeT-p:APA91bEc-sYWHdmOgqJRkcM4awvqIa5ZbVkx19wNS6zQ0ViY6pOLfed_iX7FwQnA1nUHkNOiyDNs_uUphsCxhqWBQKna_UIOl0o4aI_2LLt6PmGJu4dpLF6_FYXC8_mXwZtFgtSfSiRv'),(3,'cvEmjMo4QQSL_p5vPowh3p:APA91bGeX5mAAlxxfXQtwstmXu8mig7Qrfxy4cj27t-bGaNKP3QBwEnbDfUPsMbVHloJUkfLAQsxew6l2SzXWY30faCVenbK4lnbEfy-MqGGri5XZUQzGmtVOekWDJ9K3K6rdu3JjWYF'),(4,'e5nleWuqTv6dxMEWmzxkY8:APA91bH8zPwk4JLUyvyDiClTTVG3FuVOe0QD1amHy_EGcVBTwQ-D7LidtVw5V40LullmT5u7hrAirlw7_ZaurqsTzDjO7-kEXDvlpEkl5i763xsM2hXjWyati8XJp7se-uLTVnBsQKJb'),(5,'eh51UfZ9Q5mrpgXuJsL0Hj:APA91bGehRykpxlgjphg0XGMgNnjKe-M-rttcNhGkCV-9iEdCU4gmNZGL57IxYWzri2iRoCp9v30wnjK9-HiVgKIBdsl835vtRHz_-dfmh1FYN83kzChn8njUuYPwz5GifxtvWxmvwdN'),(6,'eh51UfZ9Q5mrpgXuJsL0Hj:APA91bGehRykpxlgjphg0XGMgNnjKe-M-rttcNhGkCV-9iEdCU4gmNZGL57IxYWzri2iRoCp9v30wnjK9-HiVgKIBdsl835vtRHz_-dfmh1FYN83kzChn8njUuYPwz5GifxtvWxmvwdN'),(7,'eh51UfZ9Q5mrpgXuJsL0Hj:APA91bGehRykpxlgjphg0XGMgNnjKe-M-rttcNhGkCV-9iEdCU4gmNZGL57IxYWzri2iRoCp9v30wnjK9-HiVgKIBdsl835vtRHz_-dfmh1FYN83kzChn8njUuYPwz5GifxtvWxmvwdN'),(8,'eh51UfZ9Q5mrpgXuJsL0Hj:APA91bGehRykpxlgjphg0XGMgNnjKe-M-rttcNhGkCV-9iEdCU4gmNZGL57IxYWzri2iRoCp9v30wnjK9-HiVgKIBdsl835vtRHz_-dfmh1FYN83kzChn8njUuYPwz5GifxtvWxmvwdN'),(9,'eh51UfZ9Q5mrpgXuJsL0Hj:APA91bGehRykpxlgjphg0XGMgNnjKe-M-rttcNhGkCV-9iEdCU4gmNZGL57IxYWzri2iRoCp9v30wnjK9-HiVgKIBdsl835vtRHz_-dfmh1FYN83kzChn8njUuYPwz5GifxtvWxmvwdN'),(10,'e-OYobuHTHGW9p9R_zzYlb:APA91bGheIEot4TRiPyad-7ui8dXbsrWZyADn5PSV7Zv_Kj2mGGQ6yNdA6qgg2BuzwXxrGXehypoa4orZQPgbQ83Ww3s-pes3ia58V9IcuxTGwHiioXkGs8r6gTIeVGOrlOslm6sO5HS'),(11,'e0Qi0JIGREuSz-IaK5AsnW:APA91bHC-jhBCX9UbxKu92456XGAFlmWE9_gxzXJzlkN41o-EJ7CCXx6UZ0ZUSZ_oKSsH8TVf0U6CqRGsg-hue-nmrPbCOgPfpg5gNrXYeykxNKL3B-A_QS8cOBrfdiaIjOx9hzFVh8a'),(12,'e2PmhGBLRY-Jg9_O0i97FK:APA91bG3BSd-WY6gKRqmELcR_C3ti3pZSgSMZwfE8LZgoaGJP9FukAb7kXdf3X-UasqAlcpWKuteHvA3HwreuUmG9lmHMk2lCtVWc-bIxv5KTWmXQSry-QtRA_ry0OxFc9q4YKpHrvEp'),(13,'e2PmhGBLRY-Jg9_O0i97FK:APA91bG3BSd-WY6gKRqmELcR_C3ti3pZSgSMZwfE8LZgoaGJP9FukAb7kXdf3X-UasqAlcpWKuteHvA3HwreuUmG9lmHMk2lCtVWc-bIxv5KTWmXQSry-QtRA_ry0OxFc9q4YKpHrvEp');
/*!40000 ALTER TABLE `fcm_token` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `invitation`
--

DROP TABLE IF EXISTS `invitation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `invitation` (
  `car_id` bigint DEFAULT NULL,
  `created` datetime(6) NOT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `mobi_user_id` bigint DEFAULT NULL,
  `modified` datetime(6) NOT NULL,
  `approved` enum('APPROVED','REJECTED','WAITING') NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKfpleu942ex4gpm0vpwj00jjo7` (`car_id`),
  KEY `FKh2u2l7vw09epn3bj77w5rj174` (`mobi_user_id`),
  CONSTRAINT `FKfpleu942ex4gpm0vpwj00jjo7` FOREIGN KEY (`car_id`) REFERENCES `car` (`id`),
  CONSTRAINT `FKh2u2l7vw09epn3bj77w5rj174` FOREIGN KEY (`mobi_user_id`) REFERENCES `mobi_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `invitation`
--

LOCK TABLES `invitation` WRITE;
/*!40000 ALTER TABLE `invitation` DISABLE KEYS */;
INSERT INTO `invitation` VALUES (1,'2024-10-10 10:15:57.971977',1,2,'2024-10-10 10:16:01.968734','APPROVED'),(1,'2024-10-10 10:15:58.022326',2,3,'2024-10-10 10:16:00.230991','APPROVED'),(3,'2024-10-10 10:16:26.860820',3,3,'2024-10-10 10:16:30.414895','APPROVED'),(3,'2024-10-10 10:16:26.878893',4,2,'2024-10-10 10:16:37.985265','APPROVED'),(8,'2024-10-10 10:29:16.389512',5,3,'2024-10-10 10:29:38.909017','APPROVED'),(8,'2024-10-10 10:29:16.461758',6,5,'2024-10-10 10:29:26.181993','APPROVED'),(8,'2024-10-10 10:29:16.461660',7,2,'2024-10-10 10:29:21.838300','APPROVED');
/*!40000 ALTER TABLE `invitation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `kakao_token`
--

DROP TABLE IF EXISTS `kakao_token`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `kakao_token` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `access_value` text NOT NULL,
  `refresh_value` text NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `kakao_token`
--

LOCK TABLES `kakao_token` WRITE;
/*!40000 ALTER TABLE `kakao_token` DISABLE KEYS */;
INSERT INTO `kakao_token` VALUES (1,'r0bhX039bUUEG3Yj73sEAKp2YPEZ3tAAAAAAAQo8IlEAAAGSc_xQLKj01SImjvGc','WD8Y5zsIyinUeRC-RN3mCRgnFlakRc_QAAAAAgo8IlEAAAGSc_xQKaj01SImjvGc'),(2,'nX5LJWxX0SBwXa82HaMU7rOPSVkw3cHaAAAAAQorDKcAAAGSc_yqeRKZRqbpl2cW','xZ5niollGlAsarTw0gK-i6vGKGdOj4T0AAAAAgorDKcAAAGSc_yqdhKZRqbpl2cW'),(3,'JNJQ4NUjRA3f1jAMnCIJBAhNOvKo17OVAAAAAQoqJVMAAAGSdBeDH82yTeNnt1bO','z4qdhZdqJlDSvbw7lfBTOO7jbHc7BQjmAAAAAgoqJVMAAAGSdBeDHc2yTeNnt1bO'),(4,'rcT4OZBo3ijSlIaLN48X3jjILC2ugLfRAAAAAQo9cpgAAAGSc_9Ajqj01SImjvGc','R-xYaZRoiZ9aykdLk2CRy6Vz_ikq3fpyAAAAAgo9cpgAAAGSc_9Ai6j01SImjvGc'),(5,'IfX5IamvrAUN60exkfJgssELAi3GjfzAAAAAAQo9c-wAAAGSdAsYr6j01SImjvGc','YmYOlnJK_uWJS-BKy7-Qq1yn28KWozHjAAAAAgo9c-wAAAGSdAsYrKj01SImjvGc'),(6,'npSr2XBVqDQJh8K5PTCL2Vyhm3zGRqZQAAAAAQoqJQ0AAAGSdCW_2KL4plhSrbcM','is3MW2hLnZShF_7CbQzY3nlYF3gT9-R7AAAAAgoqJQ0AAAGSdCW_06L4plhSrbcM');
/*!40000 ALTER TABLE `kakao_token` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `merchant`
--

DROP TABLE IF EXISTS `merchant`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `merchant` (
  `lat` double NOT NULL,
  `lng` double NOT NULL,
  `merchant_id` bigint NOT NULL,
  `category_id` varchar(20) NOT NULL,
  `mobi_api_key` varchar(20) NOT NULL,
  `merchant_name` varchar(100) NOT NULL,
  PRIMARY KEY (`merchant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `merchant`
--

LOCK TABLES `merchant` WRITE;
/*!40000 ALTER TABLE `merchant` DISABLE KEYS */;
INSERT INTO `merchant` VALUES (36.10869,128.419555,1906,'CG-9ca85f66311a23d','9dX2hN4jLwT7vK8pYmQ5','진평주차장'),(36.110484,128.41955,1907,'CG-3fa85f6425e811e','zF7R3jN1pV8aG6tLxB0','인동주유소'),(36.11029,128.422617,1908,'CG-9ca85f66311a23d','G3kP9tX5hJ8nL4mQ7aV','구미셀프세차장'),(36.107011,128.421024,1909,'CG-9ca85f66311a23d','R2pX8vN7gL4qJ5yK1wT','노상주차장'),(36.108661,128.41928,1910,'CG-9ca85f66311a23d','D4wZ6nT2vK3xR9yP8jM','투스데이'),(36.095567,128.43126,1911,'CG-9ca85f66311a23d','K1qT4xM9jW2bF5vYcN7','스타벅스 구미인의DT점');
/*!40000 ALTER TABLE `merchant` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `merchant_transaction`
--

DROP TABLE IF EXISTS `merchant_transaction`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `merchant_transaction` (
  `cancelled` bit(1) NOT NULL,
  `transaction_time` varchar(6) NOT NULL,
  `merchant_id` bigint DEFAULT NULL,
  `mobi_user_id` bigint DEFAULT NULL,
  `owned_card_id` bigint DEFAULT NULL,
  `payment_balance` bigint NOT NULL,
  `transaction_date` varchar(8) NOT NULL,
  `transaction_unique_no` bigint NOT NULL,
  `info` text NOT NULL,
  PRIMARY KEY (`transaction_unique_no`),
  KEY `FKgjliu7y4igwv2qka4b40oay65` (`merchant_id`),
  KEY `FKqvq5c7moh9ns7rr2o0euai0na` (`mobi_user_id`,`owned_card_id`),
  CONSTRAINT `FKgjliu7y4igwv2qka4b40oay65` FOREIGN KEY (`merchant_id`) REFERENCES `merchant` (`merchant_id`),
  CONSTRAINT `FKqvq5c7moh9ns7rr2o0euai0na` FOREIGN KEY (`mobi_user_id`, `owned_card_id`) REFERENCES `registered_card` (`mobi_user_id`, `owned_card_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `merchant_transaction`
--

LOCK TABLES `merchant_transaction` WRITE;
/*!40000 ALTER TABLE `merchant_transaction` DISABLE KEYS */;
INSERT INTO `merchant_transaction` VALUES (_binary '\0','102919',1911,4,12,27000,'20241010',964,'허니 자몽 블랙티 x 3, 초코 도넛 x 1, 시나몬 롤 x 2'),(_binary '\0','103011',1911,4,12,11200,'20241010',965,'아이스 말차 라떼 x 2'),(_binary '','103045',1911,4,12,3600,'20241010',967,'크로와상 x 2'),(_binary '\0','103105',1911,4,12,15300,'20241010',968,'아이스 카페라떼 x 3'),(_binary '\0','103130',1911,4,14,24000,'20241010',970,'초코 도넛 x 3, 허니 자몽 블랙티 x 2, 아이스 초코라떼 x 1'),(_binary '\0','103248',1907,2,6,80000,'20241010',971,'경유 x 2'),(_binary '','103414',1907,4,12,90000,'20241010',974,'경유 x 3'),(_binary '','103508',1907,4,12,100000,'20241010',976,'휘발유 x 2'),(_binary '','103653',1907,4,12,30000,'20241010',978,'휘발유 x 2'),(_binary '\0','103935',1906,1,1,1000,'20241010',980,'0시간 11분 : 1000원'),(_binary '\0','104354',1911,1,1,2100,'20241010',981,'초코 도넛 x 1'),(_binary '\0','104418',1911,1,1,2200,'20241010',982,'딸기 도넛 x 1'),(_binary '','104830',1906,1,1,500,'20241010',984,'0시간 8분 - 500원'),(_binary '','104922',1906,1,1,500,'20241010',986,'0시간 0분 : 500원'),(_binary '\0','105219',1907,4,12,100000,'20241010',993,'경유 x 2');
/*!40000 ALTER TABLE `merchant_transaction` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mobi_user`
--

DROP TABLE IF EXISTS `mobi_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `mobi_user` (
  `my_data_consent` bit(1) NOT NULL,
  `created` datetime(6) NOT NULL,
  `fcm_token_id` bigint DEFAULT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `kakao_token_id` bigint DEFAULT NULL,
  `refresh_token_id` bigint DEFAULT NULL,
  `ssafy_user_id` bigint DEFAULT NULL,
  `phone_number` varchar(20) NOT NULL,
  `name` varchar(25) NOT NULL,
  `email` varchar(40) NOT NULL,
  `picture` varchar(255) NOT NULL,
  `role` enum('ADMIN','USER') NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKmx0djmwwv6n00fxk21igpeby7` (`phone_number`),
  UNIQUE KEY `UKh3420gkpo8ft4rqoj9bi75bo8` (`email`),
  UNIQUE KEY `UK5fyfkng2fxo5bmyrb5fsqwqgy` (`fcm_token_id`),
  UNIQUE KEY `UKby0umcvqrhiy23nt0wgrmr5ev` (`kakao_token_id`),
  UNIQUE KEY `UKsgtw1sdrou119k0b0wyfehs9r` (`refresh_token_id`),
  UNIQUE KEY `UKrip00bq7pqlh460lq0h47huon` (`ssafy_user_id`),
  CONSTRAINT `FK2nuxqpvt50bqax8fa4tq0sxq6` FOREIGN KEY (`kakao_token_id`) REFERENCES `kakao_token` (`id`),
  CONSTRAINT `FK7hauxc4qxah04fakfw2wuy04i` FOREIGN KEY (`fcm_token_id`) REFERENCES `fcm_token` (`id`),
  CONSTRAINT `FKj3ktbxtk80i7v5n30bfodn8wy` FOREIGN KEY (`ssafy_user_id`) REFERENCES `ssafy_user` (`id`),
  CONSTRAINT `FKq41aepxhf735jq6clbj8c2yjw` FOREIGN KEY (`refresh_token_id`) REFERENCES `refresh_token` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mobi_user`
--

LOCK TABLES `mobi_user` WRITE;
/*!40000 ALTER TABLE `mobi_user` DISABLE KEYS */;
INSERT INTO `mobi_user` VALUES (_binary '','2024-10-10 10:13:26.777507',1,1,1,1,1,'01093858026','김범중','qkawnddl@naver.com','http://k.kakaocdn.net/dn/57xBd/btsHNoZXtFk/k0KV6XtXaZkPgb0NqnhiU0/img_640x640.jpg','USER'),(_binary '','2024-10-10 10:13:44.136743',2,2,2,2,2,'01079791555','정상수','tyfgh123@naver.com','http://k.kakaocdn.net/dn/IQJ0c/btsJRo4MQTo/9H8x6vlGGR35FCg1TIZVp1/img_640x640.jpg','USER'),(_binary '\0','2024-10-10 10:15:12.215789',11,3,3,11,3,'01025803467','이철민','lstork48@gmail.com','http://img1.kakaocdn.net/thumb/R640x640.q70/?fname=http://t1.kakaocdn.net/account_images/default_profile.jpeg','USER'),(_binary '','2024-10-10 10:16:28.523380',4,4,4,4,4,'01056486882','한누리','fkaus4598@naver.com','http://img1.kakaocdn.net/thumb/R640x640.q70/?fname=http://t1.kakaocdn.net/account_images/default_profile.jpeg','USER'),(_binary '','2024-10-10 10:19:23.816659',9,5,5,9,5,'01063458163','이재빈','jeabin1129@kakao.com','http://k.kakaocdn.net/dn/cmgtPp/btrPFyZ04n5/Jghm51oywrD7aaRlORMuA1/img_640x640.jpg','USER'),(_binary '\0','2024-10-10 10:33:04.530553',13,6,6,13,6,'01047568953','김세진','sejin1921@kakao.com','http://img1.kakaocdn.net/thumb/R640x640.q70/?fname=http://t1.kakaocdn.net/account_images/default_profile.jpeg','USER');
/*!40000 ALTER TABLE `mobi_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `owned_card`
--

DROP TABLE IF EXISTS `owned_card`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `owned_card` (
  `cvc` varchar(3) NOT NULL,
  `account_id` bigint DEFAULT NULL,
  `card_expiry_date` varchar(8) NOT NULL,
  `created` datetime(6) NOT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `mobi_user_id` bigint DEFAULT NULL,
  `withdrawal_date` varchar(10) NOT NULL,
  `card_no` varchar(16) NOT NULL,
  `card_unique_no` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKdmwg76umkmavkn348b9d7xw3j` (`card_no`),
  KEY `FKkdm83hbcsaqnodk3np8rcdeas` (`account_id`),
  KEY `FKlxxrqm7afghrt5w7q6y7gikxi` (`card_unique_no`),
  KEY `FK3n5t8d03sdpcwgck2l2nd5nv3` (`mobi_user_id`),
  CONSTRAINT `FK3n5t8d03sdpcwgck2l2nd5nv3` FOREIGN KEY (`mobi_user_id`) REFERENCES `mobi_user` (`id`),
  CONSTRAINT `FKkdm83hbcsaqnodk3np8rcdeas` FOREIGN KEY (`account_id`) REFERENCES `account` (`id`),
  CONSTRAINT `FKlxxrqm7afghrt5w7q6y7gikxi` FOREIGN KEY (`card_unique_no`) REFERENCES `card_product` (`card_unique_no`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `owned_card`
--

LOCK TABLES `owned_card` WRITE;
/*!40000 ALTER TABLE `owned_card` DISABLE KEYS */;
INSERT INTO `owned_card` VALUES ('320',1,'20291004','2024-10-10 10:13:27.099895',1,1,'1','1001692280721138','1001-664f125022bf433'),('114',1,'20291004','2024-10-10 10:13:27.115193',2,1,'1','1002434875888745','1002-218c5933582e430'),('951',1,'20291004','2024-10-10 10:13:27.130754',3,1,'1','1005717648103058','1005-dcf45d7885a1442'),('483',1,'20291004','2024-10-10 10:13:27.143154',4,1,'1','1005809762991741','1005-6d3da5e1ab334fc'),('359',2,'20291004','2024-10-10 10:13:44.385362',5,2,'1','1001713201696307','1001-664f125022bf433'),('809',2,'20291004','2024-10-10 10:13:44.394247',6,2,'1','1002312964685403','1002-218c5933582e430'),('900',2,'20291004','2024-10-10 10:13:44.402588',7,2,'1','1005202331422545','1005-6d3da5e1ab334fc'),('810',2,'20291004','2024-10-10 10:13:44.410888',8,2,'1','1005473129097990','1005-dcf45d7885a1442'),('707',3,'20290930','2024-10-10 10:15:12.448226',9,3,'1','1001719482607010','1001-664f125022bf433'),('583',3,'20290930','2024-10-10 10:15:12.455470',10,3,'1','1002766632239229','1002-218c5933582e430'),('889',3,'20290930','2024-10-10 10:15:12.462569',11,3,'1','1005509554634859','1005-6d3da5e1ab334fc'),('763',4,'20291003','2024-10-10 10:16:28.748296',12,4,'1','1001174313477967','1001-664f125022bf433'),('711',4,'20291003','2024-10-10 10:16:28.754472',13,4,'1','1002614672941425','1002-218c5933582e430'),('376',4,'20291003','2024-10-10 10:16:28.760605',14,4,'1','1005282666829674','1005-6d3da5e1ab334fc'),('843',5,'20290930','2024-10-10 10:19:24.050352',15,5,'1','1001633119385377','1001-664f125022bf433'),('374',5,'20290930','2024-10-10 10:19:24.055844',16,5,'1','1002280980234040','1002-218c5933582e430'),('969',5,'20290930','2024-10-10 10:19:24.061202',17,5,'1','1005445865319253','1005-6d3da5e1ab334fc'),('259',6,'20290930','2024-10-10 10:33:04.780944',18,6,'1','1001949459677718','1001-664f125022bf433'),('680',6,'20290930','2024-10-10 10:33:04.785364',19,6,'1','1002153918848182','1002-218c5933582e430'),('206',6,'20290930','2024-10-10 10:33:04.789642',20,6,'1','1005658523500915','1005-6d3da5e1ab334fc');
/*!40000 ALTER TABLE `owned_card` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `refresh_token`
--

DROP TABLE IF EXISTS `refresh_token`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `refresh_token` (
  `revoked` bit(1) NOT NULL,
  `expiredAt` datetime(6) NOT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `issuedAt` datetime(6) NOT NULL,
  `value` text NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `refresh_token`
--

LOCK TABLES `refresh_token` WRITE;
/*!40000 ALTER TABLE `refresh_token` DISABLE KEYS */;
INSERT INTO `refresh_token` VALUES (_binary '\0','2024-10-17 10:13:27.000000',1,'2024-10-10 10:13:27.000000','eyJhbGciOiJIUzI1NiJ9.eyJjYXRlZ29yeSI6InJlZnJlc2giLCJtb2JpVXNlcklkIjoxLCJlbWFpbCI6InFrYXduZGRsQG5hdmVyLmNvbSIsIm5hbWUiOiLquYDrspTspJEiLCJwaG9uZU51bWJlciI6IjAxMDkzODU4MDI2IiwicGljdHVyZSI6Imh0dHA6Ly9rLmtha2FvY2RuLm5ldC9kbi81N3hCZC9idHNITm9aWHRGay9rMEtWNlh0WGFaa1BnYjBOcW5oaVUwL2ltZ182NDB4NjQwLmpwZyIsImlhdCI6MTcyODUyMjgwNywiZXhwIjoxNzI5MTI3NjA3fQ.J6-r_Fjo3Vf73rBVBSevUrrp9cKgV6yYxWY-8prh7zY'),(_binary '\0','2024-10-17 10:13:44.000000',2,'2024-10-10 10:13:44.000000','eyJhbGciOiJIUzI1NiJ9.eyJjYXRlZ29yeSI6InJlZnJlc2giLCJtb2JpVXNlcklkIjoyLCJlbWFpbCI6InR5ZmdoMTIzQG5hdmVyLmNvbSIsIm5hbWUiOiLsoJXsg4HsiJgiLCJwaG9uZU51bWJlciI6IjAxMDc5NzkxNTU1IiwicGljdHVyZSI6Imh0dHA6Ly9rLmtha2FvY2RuLm5ldC9kbi9JUUowYy9idHNKUm80TVFUby85SDh4NnZsR0dSMzVGQ2cxVElaVnAxL2ltZ182NDB4NjQwLmpwZyIsImlhdCI6MTcyODUyMjgyNCwiZXhwIjoxNzI5MTI3NjI0fQ.t3dftvwbhZSmJ472_iZcKhB8Fgco2jXQxvz4-7tokX0'),(_binary '','2024-10-17 10:15:12.000000',3,'2024-10-10 10:15:12.000000','eyJhbGciOiJIUzI1NiJ9.eyJjYXRlZ29yeSI6InJlZnJlc2giLCJtb2JpVXNlcklkIjozLCJlbWFpbCI6ImxzdG9yazQ4QGdtYWlsLmNvbSIsIm5hbWUiOiLsnbTssqDrr7wiLCJwaG9uZU51bWJlciI6IjAxMDI1ODAzNDY3IiwicGljdHVyZSI6Imh0dHA6Ly9pbWcxLmtha2FvY2RuLm5ldC90aHVtYi9SNjQweDY0MC5xNzAvP2ZuYW1lPWh0dHA6Ly90MS5rYWthb2Nkbi5uZXQvYWNjb3VudF9pbWFnZXMvZGVmYXVsdF9wcm9maWxlLmpwZWciLCJpYXQiOjE3Mjg1MjI5MTIsImV4cCI6MTcyOTEyNzcxMn0.41Qvq5kq2Db6QhZWqvpxhYZetvvcV9uxHZu-GcqNXaA'),(_binary '\0','2024-10-17 10:16:28.000000',4,'2024-10-10 10:16:28.000000','eyJhbGciOiJIUzI1NiJ9.eyJjYXRlZ29yeSI6InJlZnJlc2giLCJtb2JpVXNlcklkIjo0LCJlbWFpbCI6ImZrYXVzNDU5OEBuYXZlci5jb20iLCJuYW1lIjoi7ZWc64iE66asIiwicGhvbmVOdW1iZXIiOiIwMTA1NjQ4Njg4MiIsInBpY3R1cmUiOiJodHRwOi8vaW1nMS5rYWthb2Nkbi5uZXQvdGh1bWIvUjY0MHg2NDAucTcwLz9mbmFtZT1odHRwOi8vdDEua2FrYW9jZG4ubmV0L2FjY291bnRfaW1hZ2VzL2RlZmF1bHRfcHJvZmlsZS5qcGVnIiwiaWF0IjoxNzI4NTIyOTg4LCJleHAiOjE3MjkxMjc3ODh9.UKjA94A69dc7xmsYgPuh-Et5VnjCaCWDUti_XFiLFL0'),(_binary '','2024-10-17 10:19:24.000000',5,'2024-10-10 10:19:24.000000','eyJhbGciOiJIUzI1NiJ9.eyJjYXRlZ29yeSI6InJlZnJlc2giLCJtb2JpVXNlcklkIjo1LCJlbWFpbCI6ImplYWJpbjExMjlAa2FrYW8uY29tIiwibmFtZSI6IuydtOyerOu5iCIsInBob25lTnVtYmVyIjoiMDEwNjM0NTgxNjMiLCJwaWN0dXJlIjoiaHR0cDovL2sua2FrYW9jZG4ubmV0L2RuL2NtZ3RQcC9idHJQRnlaMDRuNS9KZ2htNTFveXdyRDdhYVJsT1JNdUExL2ltZ182NDB4NjQwLmpwZyIsImlhdCI6MTcyODUyMzE2NCwiZXhwIjoxNzI5MTI3OTY0fQ.iE6-rgcjiNkVqQKt1zXLG-ptv2K8BEaBa07Czum_w-4'),(_binary '','2024-10-17 10:28:36.000000',6,'2024-10-10 10:28:36.000000','eyJhbGciOiJIUzI1NiJ9.eyJjYXRlZ29yeSI6InJlZnJlc2giLCJtb2JpVXNlcklkIjo1LCJlbWFpbCI6ImplYWJpbjExMjlAa2FrYW8uY29tIiwibmFtZSI6IuydtOyerOu5iCIsInBob25lTnVtYmVyIjoiMDEwNjM0NTgxNjMiLCJwaWN0dXJlIjoiaHR0cDovL2sua2FrYW9jZG4ubmV0L2RuL2NtZ3RQcC9idHJQRnlaMDRuNS9KZ2htNTFveXdyRDdhYVJsT1JNdUExL2ltZ182NDB4NjQwLmpwZyIsImlhdCI6MTcyODUyMzcxNiwiZXhwIjoxNzI5MTI4NTE2fQ.MP3D9d9Kld5Y5FTEkg-1sfGfTUzpbhvtbbMcTnlodOg'),(_binary '','2024-10-17 10:28:41.000000',7,'2024-10-10 10:28:41.000000','eyJhbGciOiJIUzI1NiJ9.eyJjYXRlZ29yeSI6InJlZnJlc2giLCJtb2JpVXNlcklkIjo1LCJlbWFpbCI6ImplYWJpbjExMjlAa2FrYW8uY29tIiwibmFtZSI6IuydtOyerOu5iCIsInBob25lTnVtYmVyIjoiMDEwNjM0NTgxNjMiLCJwaWN0dXJlIjoiaHR0cDovL2sua2FrYW9jZG4ubmV0L2RuL2NtZ3RQcC9idHJQRnlaMDRuNS9KZ2htNTFveXdyRDdhYVJsT1JNdUExL2ltZ182NDB4NjQwLmpwZyIsImlhdCI6MTcyODUyMzcyMSwiZXhwIjoxNzI5MTI4NTIxfQ.sTgKhbdQVKnyTWphzrMvRurvdEWgMdoKGs6IVupoSwA'),(_binary '','2024-10-17 10:28:46.000000',8,'2024-10-10 10:28:46.000000','eyJhbGciOiJIUzI1NiJ9.eyJjYXRlZ29yeSI6InJlZnJlc2giLCJtb2JpVXNlcklkIjo1LCJlbWFpbCI6ImplYWJpbjExMjlAa2FrYW8uY29tIiwibmFtZSI6IuydtOyerOu5iCIsInBob25lTnVtYmVyIjoiMDEwNjM0NTgxNjMiLCJwaWN0dXJlIjoiaHR0cDovL2sua2FrYW9jZG4ubmV0L2RuL2NtZ3RQcC9idHJQRnlaMDRuNS9KZ2htNTFveXdyRDdhYVJsT1JNdUExL2ltZ182NDB4NjQwLmpwZyIsImlhdCI6MTcyODUyMzcyNiwiZXhwIjoxNzI5MTI4NTI2fQ.JmTWbnPCqajZgqTbfaRqawZCmzDUPNhPqChHqWV12IE'),(_binary '\0','2024-10-17 10:28:57.000000',9,'2024-10-10 10:28:57.000000','eyJhbGciOiJIUzI1NiJ9.eyJjYXRlZ29yeSI6InJlZnJlc2giLCJtb2JpVXNlcklkIjo1LCJlbWFpbCI6ImplYWJpbjExMjlAa2FrYW8uY29tIiwibmFtZSI6IuydtOyerOu5iCIsInBob25lTnVtYmVyIjoiMDEwNjM0NTgxNjMiLCJwaWN0dXJlIjoiaHR0cDovL2sua2FrYW9jZG4ubmV0L2RuL2NtZ3RQcC9idHJQRnlaMDRuNS9KZ2htNTFveXdyRDdhYVJsT1JNdUExL2ltZ182NDB4NjQwLmpwZyIsImlhdCI6MTcyODUyMzczNywiZXhwIjoxNzI5MTI4NTM3fQ.3EwA7cM0e2qjbMSYoI1KSfSssa7koP1zZX3t8SytEUU'),(_binary '','2024-10-17 10:33:04.000000',10,'2024-10-10 10:33:04.000000','eyJhbGciOiJIUzI1NiJ9.eyJjYXRlZ29yeSI6InJlZnJlc2giLCJtb2JpVXNlcklkIjo2LCJlbWFpbCI6InNlamluMTkyMUBrYWthby5jb20iLCJuYW1lIjoi6rmA7IS47KeEIiwicGhvbmVOdW1iZXIiOiIwMTA0NzU2ODk1MyIsInBpY3R1cmUiOiJodHRwOi8vaW1nMS5rYWthb2Nkbi5uZXQvdGh1bWIvUjY0MHg2NDAucTcwLz9mbmFtZT1odHRwOi8vdDEua2FrYW9jZG4ubmV0L2FjY291bnRfaW1hZ2VzL2RlZmF1bHRfcHJvZmlsZS5qcGVnIiwiaWF0IjoxNzI4NTIzOTg0LCJleHAiOjE3MjkxMjg3ODR9.H0SBZUHyhFaHNsInqwmdUss73Q2FuJYapD5R4awdeVw'),(_binary '\0','2024-10-17 10:42:31.000000',11,'2024-10-10 10:42:31.000000','eyJhbGciOiJIUzI1NiJ9.eyJjYXRlZ29yeSI6InJlZnJlc2giLCJtb2JpVXNlcklkIjozLCJlbWFpbCI6ImxzdG9yazQ4QGdtYWlsLmNvbSIsIm5hbWUiOiLsnbTssqDrr7wiLCJwaG9uZU51bWJlciI6IjAxMDI1ODAzNDY3IiwicGljdHVyZSI6Imh0dHA6Ly9pbWcxLmtha2FvY2RuLm5ldC90aHVtYi9SNjQweDY0MC5xNzAvP2ZuYW1lPWh0dHA6Ly90MS5rYWthb2Nkbi5uZXQvYWNjb3VudF9pbWFnZXMvZGVmYXVsdF9wcm9maWxlLmpwZWciLCJpYXQiOjE3Mjg1MjQ1NTEsImV4cCI6MTcyOTEyOTM1MX0.ir4mU1ucEYWOm-CPABuBFXlAuJfva1Uv5IPSxBE12Uk'),(_binary '','2024-10-17 10:42:51.000000',12,'2024-10-10 10:42:51.000000','eyJhbGciOiJIUzI1NiJ9.eyJjYXRlZ29yeSI6InJlZnJlc2giLCJtb2JpVXNlcklkIjo2LCJlbWFpbCI6InNlamluMTkyMUBrYWthby5jb20iLCJuYW1lIjoi6rmA7IS47KeEIiwicGhvbmVOdW1iZXIiOiIwMTA0NzU2ODk1MyIsInBpY3R1cmUiOiJodHRwOi8vaW1nMS5rYWthb2Nkbi5uZXQvdGh1bWIvUjY0MHg2NDAucTcwLz9mbmFtZT1odHRwOi8vdDEua2FrYW9jZG4ubmV0L2FjY291bnRfaW1hZ2VzL2RlZmF1bHRfcHJvZmlsZS5qcGVnIiwiaWF0IjoxNzI4NTI0NTcxLCJleHAiOjE3MjkxMjkzNzF9.ca6oGbP1cDjUFmgkAoyBOcwN7xSUKvMhFbTVe8SeJs0'),(_binary '\0','2024-10-17 10:58:04.000000',13,'2024-10-10 10:58:04.000000','eyJhbGciOiJIUzI1NiJ9.eyJjYXRlZ29yeSI6InJlZnJlc2giLCJtb2JpVXNlcklkIjo2LCJlbWFpbCI6InNlamluMTkyMUBrYWthby5jb20iLCJuYW1lIjoi6rmA7IS47KeEIiwicGhvbmVOdW1iZXIiOiIwMTA0NzU2ODk1MyIsInBpY3R1cmUiOiJodHRwOi8vaW1nMS5rYWthb2Nkbi5uZXQvdGh1bWIvUjY0MHg2NDAucTcwLz9mbmFtZT1odHRwOi8vdDEua2FrYW9jZG4ubmV0L2FjY291bnRfaW1hZ2VzL2RlZmF1bHRfcHJvZmlsZS5qcGVnIiwiaWF0IjoxNzI4NTI1NDg0LCJleHAiOjE3MjkxMzAyODR9.o_cr9r0BaKTNw2fJBPH5A3EHPtO4sCj6O_A5CqQXKlQ');
/*!40000 ALTER TABLE `refresh_token` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `registered_card`
--

DROP TABLE IF EXISTS `registered_card`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `registered_card` (
  `auto_pay_status` bit(1) NOT NULL,
  `one_time_limit` int NOT NULL,
  `mobi_user_id` bigint NOT NULL,
  `owned_card_id` bigint NOT NULL,
  PRIMARY KEY (`mobi_user_id`,`owned_card_id`),
  KEY `FKgnbg5qurewy3bv0foagcwbb4i` (`owned_card_id`),
  CONSTRAINT `FKgnbg5qurewy3bv0foagcwbb4i` FOREIGN KEY (`owned_card_id`) REFERENCES `owned_card` (`id`),
  CONSTRAINT `FKmc8996h9x522kbwqkm267l5du` FOREIGN KEY (`mobi_user_id`) REFERENCES `mobi_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `registered_card`
--

LOCK TABLES `registered_card` WRITE;
/*!40000 ALTER TABLE `registered_card` DISABLE KEYS */;
INSERT INTO `registered_card` VALUES (_binary '',1000000,1,1),(_binary '\0',1000000,1,2),(_binary '\0',1000000,1,3),(_binary '',1000000,2,6),(_binary '\0',1000000,2,7),(_binary '\0',1000000,2,8),(_binary '',1000000,4,12),(_binary '\0',1000000,4,13),(_binary '\0',1000000,4,14);
/*!40000 ALTER TABLE `registered_card` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ssafy_user`
--

DROP TABLE IF EXISTS `ssafy_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ssafy_user` (
  `created` datetime(6) NOT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `modified` datetime(6) NOT NULL,
  `username` varchar(30) NOT NULL,
  `user_id` varchar(40) NOT NULL,
  `user_key` varchar(60) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKg1rvvn05h7kwk35qgkothsk1w` (`user_id`),
  UNIQUE KEY `UKbk0qrw597b824yegs1in1wda9` (`user_key`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ssafy_user`
--

LOCK TABLES `ssafy_user` WRITE;
/*!40000 ALTER TABLE `ssafy_user` DISABLE KEYS */;
INSERT INTO `ssafy_user` VALUES ('2024-10-04 00:39:27.738274',1,'2024-10-04 00:39:27.738273','qkawnddl','qkawnddl@naver.com','e2ee5a70-7209-4acc-9670-303f266aa799'),('2024-10-04 05:21:53.484189',2,'2024-10-04 05:21:53.484188','tyfgh123','tyfgh123@naver.com','60793df0-ead3-4000-9b86-5a0ea6dd7fd6'),('2024-09-30 01:48:47.575898',3,'2024-09-30 01:48:47.575897','lstork48','lstork48@gmail.com','01b9cd1d-cd8a-4f90-861b-4d66593190f6'),('2024-10-03 07:07:51.449475',4,'2024-10-03 07:07:51.449474','fkaus4598','fkaus4598@naver.com','45bc682a-3a33-4b71-9e69-7e45a678fdb7'),('2024-09-30 06:22:16.894480',5,'2024-09-30 06:22:16.894478','jeabin1129','jeabin1129@kakao.com','9941a5b7-cf5a-42bc-9b47-ce10c98558b0'),('2024-09-30 00:55:58.404277',6,'2024-09-30 00:55:58.404275','sejin1921','sejin1921@kakao.com','4b480dfa-82ad-46e3-b4f9-01a33afbb4f7');
/*!40000 ALTER TABLE `ssafy_user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-10-10  2:01:39
