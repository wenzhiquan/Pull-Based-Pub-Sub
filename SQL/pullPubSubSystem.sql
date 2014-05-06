/*
SQLyog Ultimate v11.24 (32 bit)
MySQL - 5.6.15 : Database - pullpubsubsystem
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`pullpubsubsystem` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `pullpubsubsystem`;

/*Table structure for table `messages` */

DROP TABLE IF EXISTS `messages`;

CREATE TABLE `messages` (
  `msgID` int(10) NOT NULL AUTO_INCREMENT,
  `publisher` varchar(20) NOT NULL,
  `topicName` varchar(10) NOT NULL,
  `msgContent` varchar(255) NOT NULL,
  `pubTime` varchar(14) NOT NULL,
  PRIMARY KEY (`msgID`),
  KEY `publisher` (`publisher`),
  KEY `topicName` (`topicName`),
  CONSTRAINT `messages_ibfk_1` FOREIGN KEY (`publisher`) REFERENCES `users` (`username`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `messages_ibfk_2` FOREIGN KEY (`topicName`) REFERENCES `topics` (`topicName`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8;

/*Data for the table `messages` */

insert  into `messages`(`msgID`,`publisher`,`topicName`,`msgContent`,`pubTime`) values (1,'wen','News','新闻测试1-1\n新闻测试1-2\n新闻测试1-3\n新闻测试1-4\n新闻测试1-5\n','20140223070828'),(2,'wen','News','新闻测试2-1\n新闻测试2-2\n新闻测试2-3\n新闻测试2-4\n新闻测试2-5\n','20140223070912'),(3,'wen','News','新闻测试3-1\n新闻测试3-2\n新闻测试3-3\n新闻测试3-4\n新闻测试3-5\n','20140223070922'),(4,'wen','Books','书籍测试1-1\n书籍测试1-2\n书籍测试1-3\n书籍测试1-4\n书籍测试1-5\n','20140223070956'),(5,'wen','Books','书籍测试2-1\n书籍测试2-2\n书籍测试2-3\n书籍测试2-4\n书籍测试2-5\n','20140223071008'),(6,'admin','Sports','运动测试1-1\n运动测试1-2\n运动测试1-3\n运动测试1-4\n','20140223071040'),(7,'admin','Sports','运动测试2-1\n运动测试2-2\n运动测试2-3\n运动测试2-4\n','20140223071048'),(8,'admin','Sports','运动测试3-1\n运动测试3-2\n运动测试3-3\n运动测试3-4\n','20140223071057'),(9,'admin','Sports','运动测试4-1\n运动测试4-2\n运动测试4-3\n运动测试4-4\n','20140223071141'),(10,'wen','News','新闻测试4-1\n新闻测试4-2\n新闻测试4-3\n新闻测试4-4\n','20140223073734'),(11,'wen','Books','111\n222\n333\n444\n555\n','20140323114117'),(12,'admin','Books','这是什么呢？\n','20140428103751'),(13,'admin','Books','这是什么呢？\n','20140428103803'),(14,'wen','Movies','电影测试1\n电影测试2\n电影测试3\n电影测试4\n电影测试5\n','20140430083758'),(16,'wen','Movies','电影测试\n电影测试1\n电影测试2\n电影测试3\n电影测试4\n电影测试5\n','20140430084152');

/*Table structure for table `subscribes` */

DROP TABLE IF EXISTS `subscribes`;

CREATE TABLE `subscribes` (
  `topicName` varchar(20) NOT NULL,
  `userName` varchar(20) NOT NULL,
  PRIMARY KEY (`topicName`,`userName`),
  KEY `userName` (`userName`),
  CONSTRAINT `subscribes_ibfk_1` FOREIGN KEY (`userName`) REFERENCES `users` (`username`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `subscribes` */

insert  into `subscribes`(`topicName`,`userName`) values ('Books','111'),('Movies','111'),('Movies','222'),('News','222'),('Books','444'),('Movies','444'),('News','444'),('Sports','444'),('Books','admin'),('News','admin'),('Books','wen'),('Movies','wen'),('News','wen'),('Sports','wen');

/*Table structure for table `topics` */

DROP TABLE IF EXISTS `topics`;

CREATE TABLE `topics` (
  `topicName` varchar(10) NOT NULL,
  PRIMARY KEY (`topicName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `topics` */

insert  into `topics`(`topicName`) values ('Books'),('Movies'),('Music'),('News'),('Sports');

/*Table structure for table `users` */

DROP TABLE IF EXISTS `users`;

CREATE TABLE `users` (
  `username` varchar(20) NOT NULL,
  `password` varchar(20) NOT NULL,
  PRIMARY KEY (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `users` */

insert  into `users`(`username`,`password`) values ('111','111'),('222','222'),('333','333'),('444','444'),('555','555'),('admin','admin'),('wen','111'),('zhi','111');

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
