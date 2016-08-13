
--   注意 : 如果升级了任意表结构，一定要到到DbDefinitions中升级版本号,并且在 assets/db/migrations中添加对应的sql文件.

-- 用户表
CREATE TABLE users (
			 id TEXT unique ,
			 name TEXT not null,
			 gender INTEGER
            ) ;

-- 书籍表
CREATE TABLE books (id TEXT NOT NULL unique,
            	   name TEXT NOT NULL
 ) ;


-- 借阅表
CREATE TABLE borrow (user_id TEXT NOT NULL,
            	   book_id TEXT NOT NULL, UNIQUE(user_id, book_id)
 ) ;
