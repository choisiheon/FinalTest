create schema springJPA;

use springJPA;

DROP TABLE IF EXISTS community_group;
DROP TABLE IF EXISTS community_board;
DROP TABLE IF EXISTS community_member;

CREATE TABLE community_member
(
    member_id VARCHAR(20) PRIMARY KEY COMMENT '회원 아이디',
    member_pw VARCHAR(100) NOT NULL COMMENT '비밀번호(암호화)',
    name      VARCHAR(20)  NOT NULL COMMENT '이름',
    email     VARCHAR(200) COMMENT '이메일'
);
SELECT *FROM community_member;

CREATE TABLE community_board
(
    board_num   INT AUTO_INCREMENT PRIMARY KEY COMMENT '글 번호',
    member_id   VARCHAR(20) COMMENT '작성자 아이디',
    category    VARCHAR(50)   NOT NULL COMMENT '카테고리(STUDY/HEALTHY/TRIP)',
    title       VARCHAR(200)  NOT NULL COMMENT '제목',
    contents    VARCHAR(2000) NOT NULL COMMENT '내용',
    headcnt     INT          DEFAULT 0 COMMENT '현재 인원 수',
    capacity    INT          DEFAULT 5 COMMENT '최대 정원 수',
    status      VARCHAR(100) DEFAULT 'OPEN' COMMENT '모집 상태(OPEN/CLOSED)',
    create_date TIMESTAMP    DEFAULT CURRENT_TIMESTAMP COMMENT '작성일',

    CONSTRAINT fk_board_member FOREIGN KEY (member_id)
        REFERENCES community_member (member_id) ON DELETE SET NULL
);

CREATE TABLE community_group
(
    group_id    INT AUTO_INCREMENT PRIMARY KEY COMMENT '참여 번호',
    board_num   INT COMMENT '참여 모집 글 번호',
    member_id   VARCHAR(20) COMMENT '참여 회원 아이디',
    role        VARCHAR(20) DEFAULT 'MEMBER' COMMENT '역할(LEADER/MEMBER)',
    status      VARCHAR(20) DEFAULT 'PENDING' COMMENT '상태(PENDING/JOINED/REJECTED)',
    joined_date TIMESTAMP   DEFAULT CURRENT_TIMESTAMP COMMENT '참여/신청 일시',

    CONSTRAINT fk_group_board FOREIGN KEY (board_num)
        REFERENCES community_board (board_num) ON DELETE CASCADE,

    CONSTRAINT fk_group_member FOREIGN KEY (member_id)
        REFERENCES community_member (member_id) ON DELETE CASCADE
);